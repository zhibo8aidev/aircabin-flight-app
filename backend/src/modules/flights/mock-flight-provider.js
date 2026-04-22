import { FlightProvider } from "./flight-provider.js";

const airportCoordinates = {
  PEK: { lat: 40.0799, lng: 116.6031 },
  SHA: { lat: 31.1979, lng: 121.3363 },
  PVG: { lat: 31.1443, lng: 121.8083 },
  CAN: { lat: 23.3924, lng: 113.2988 },
  SZX: { lat: 22.6393, lng: 113.8107 },
  HKG: { lat: 22.308, lng: 113.9185 }
};

function lerp(start, end, ratio) {
  return start + (end - start) * ratio;
}

function buildGeometry(fromAirport, toAirport) {
  const from = airportCoordinates[fromAirport] ?? { lat: 0, lng: 0 };
  const to = airportCoordinates[toAirport] ?? { lat: 0, lng: 0 };
  const points = [];
  for (let step = 0; step <= 12; step += 1) {
    const ratio = step / 12;
    points.push({
      lat: Number(lerp(from.lat, to.lat, ratio).toFixed(4)),
      lng: Number(lerp(from.lng, to.lng, ratio).toFixed(4))
    });
  }
  return points;
}

export class MockFlightProvider extends FlightProvider {
  async getFlightSchedule(flightNo, flightDate, record) {
    return {
      flightNo,
      flightDate,
      departureTime: record.departureTime,
      arrivalTime: record.arrivalTime
    };
  }

  async getFlightStatus(flightNo, flightDate, record) {
    return {
      flightNo,
      flightDate,
      state: "simulated",
      source: "plan_only",
      updatedAt: new Date().toISOString(),
      recordStatus: record.status
    };
  }

  async getRouteGeometry(flightNo, flightDate, record) {
    return {
      flightNo,
      flightDate,
      geometry: buildGeometry(record.departureAirport, record.arrivalAirport)
    };
  }
}
