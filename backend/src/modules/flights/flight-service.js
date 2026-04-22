import { DataStore } from "../../storage/data-store.js";

function computeProgress(departureTime, arrivalTime, now = new Date()) {
  const departureMs = new Date(departureTime).getTime();
  const arrivalMs = new Date(arrivalTime).getTime();
  const nowMs = now.getTime();
  const totalMs = Math.max(arrivalMs - departureMs, 1);
  const elapsedMs = Math.max(0, Math.min(nowMs - departureMs, totalMs));
  const remainingMs = Math.max(arrivalMs - nowMs, 0);
  const progressRatio = Math.max(0, Math.min(elapsedMs / totalMs, 1));

  let phase = "scheduled";
  if (nowMs >= arrivalMs) {
    phase = "arrived";
  } else if (nowMs >= departureMs) {
    if (progressRatio < 0.12) {
      phase = "takeoff";
    } else if (progressRatio < 0.82) {
      phase = "cruise";
    } else {
      phase = "landing";
    }
  }

  return {
    totalMinutes: Math.round(totalMs / 60000),
    elapsedMinutes: Math.round(elapsedMs / 60000),
    remainingMinutes: Math.round(remainingMs / 60000),
    progressRatio: Number(progressRatio.toFixed(4)),
    phase
  };
}

function getCurrentPosition(geometry, progressRatio) {
  if (geometry.length === 0) {
    return null;
  }

  const rawIndex = progressRatio * (geometry.length - 1);
  const startIndex = Math.floor(rawIndex);
  const endIndex = Math.min(startIndex + 1, geometry.length - 1);
  const segmentRatio = rawIndex - startIndex;
  const start = geometry[startIndex];
  const end = geometry[endIndex];

  return {
    lat: Number((start.lat + (end.lat - start.lat) * segmentRatio).toFixed(4)),
    lng: Number((start.lng + (end.lng - start.lng) * segmentRatio).toFixed(4))
  };
}

export class FlightService {
  constructor(store, provider) {
    this.store = store;
    this.provider = provider;
  }

  async createFlightRecord(user, payload) {
    const now = new Date().toISOString();
    const record = {
      id: DataStore.createId("flight"),
      userId: user.id,
      flightNo: payload.flightNo,
      airline: payload.airline,
      departureAirport: payload.departureAirport,
      arrivalAirport: payload.arrivalAirport,
      departureCity: payload.departureCity,
      arrivalCity: payload.arrivalCity,
      departureTime: payload.departureTime,
      arrivalTime: payload.arrivalTime,
      departureDate: payload.departureDate,
      cabinClass: payload.cabinClass,
      seatNo: payload.seatNo ?? null,
      source: payload.source ?? "manual",
      status: payload.status ?? "scheduled",
      distanceKm: payload.distanceKm ?? 0,
      isInternational: Boolean(payload.isInternational),
      createdAt: now,
      updatedAt: now
    };

    await this.store.transaction((state) => {
      state.flightRecords.push(record);
    });

    return this.buildFlightSummary(record);
  }

  async getFlightRecord(userId, flightId) {
    return this.store.read((state) => state.flightRecords.find((item) => item.id === flightId && item.userId === userId) ?? null);
  }

  async getCurrentFlight(userId) {
    const records = await this.store.read((state) => state.flightRecords.filter((item) => item.userId === userId));
    if (records.length === 0) {
      return null;
    }

    const nowMs = Date.now();
    const sorted = [...records].sort((left, right) => {
      const leftDistance = Math.abs(new Date(left.departureTime).getTime() - nowMs);
      const rightDistance = Math.abs(new Date(right.departureTime).getTime() - nowMs);
      return leftDistance - rightDistance;
    });

    return this.buildFlightSummary(sorted[0]);
  }

  async buildFlightSummary(record) {
    const schedule = await this.provider.getFlightSchedule(record.flightNo, record.departureDate, record);
    const status = await this.provider.getFlightStatus(record.flightNo, record.departureDate, record);
    const route = await this.provider.getRouteGeometry(record.flightNo, record.departureDate, record);
    const progress = computeProgress(schedule.departureTime, schedule.arrivalTime);

    return {
      flightId: record.id,
      flightNo: record.flightNo,
      airline: record.airline,
      cabinClass: record.cabinClass,
      seatNo: record.seatNo,
      departure: {
        airport: record.departureAirport,
        city: record.departureCity,
        time: record.departureTime
      },
      arrival: {
        airport: record.arrivalAirport,
        city: record.arrivalCity,
        time: record.arrivalTime
      },
      mode: {
        code: "simulated_route",
        label: "模拟航线",
        caption: "计划参考",
        sourceChip: status.source === "plan_only" ? "Plan only" : "Synced"
      },
      phase: progress.phase,
      progress,
      route: {
        geometry: route.geometry,
        currentPosition: getCurrentPosition(route.geometry, progress.progressRatio),
        providerState: status.state,
        updatedAt: status.updatedAt
      },
      status: record.status,
      distanceKm: record.distanceKm,
      isInternational: record.isInternational
    };
  }
}
