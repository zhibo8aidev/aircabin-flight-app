function defaultBucket() {
  return {
    count: 0,
    totalMinutes: 0,
    totalDistanceKm: 0
  };
}

function incrementMap(map, key) {
  map.set(key, (map.get(key) ?? 0) + 1);
}

function toTopList(map) {
  return [...map.entries()]
    .sort((left, right) => right[1] - left[1])
    .map(([key, count]) => ({ key, count }));
}

export class StatsService {
  constructor(store) {
    this.store = store;
  }

  async getYearlySummary(userId, year) {
    const result = await this.store.read((state) => {
      const records = state.flightRecords.filter((item) => item.userId === userId && item.departureDate.startsWith(String(year)));

      const totals = defaultBucket();
      const monthly = new Map();
      const quarterly = new Map();
      const airlines = new Map();
      const routes = new Map();
      const cities = new Map();
      let domesticCount = 0;
      let internationalCount = 0;

      for (const record of records) {
        const departureMs = new Date(record.departureTime).getTime();
        const arrivalMs = new Date(record.arrivalTime).getTime();
        const minutes = Math.max(0, Math.round((arrivalMs - departureMs) / 60000));
        const month = record.departureDate.slice(0, 7);
        const monthIndex = Number.parseInt(record.departureDate.slice(5, 7), 10);
        const quarter = `${year}-Q${Math.floor((monthIndex - 1) / 3) + 1}`;

        totals.count += 1;
        totals.totalMinutes += minutes;
        totals.totalDistanceKm += record.distanceKm ?? 0;

        if (!monthly.has(month)) {
          monthly.set(month, defaultBucket());
        }
        if (!quarterly.has(quarter)) {
          quarterly.set(quarter, defaultBucket());
        }

        const monthBucket = monthly.get(month);
        monthBucket.count += 1;
        monthBucket.totalMinutes += minutes;
        monthBucket.totalDistanceKm += record.distanceKm ?? 0;

        const quarterBucket = quarterly.get(quarter);
        quarterBucket.count += 1;
        quarterBucket.totalMinutes += minutes;
        quarterBucket.totalDistanceKm += record.distanceKm ?? 0;

        incrementMap(airlines, record.airline);
        incrementMap(routes, `${record.departureAirport}-${record.arrivalAirport}`);
        incrementMap(cities, record.departureCity);
        incrementMap(cities, record.arrivalCity);

        if (record.isInternational) {
          internationalCount += 1;
        } else {
          domesticCount += 1;
        }
      }

      return {
        year,
        totals,
        breakdown: {
          domesticCount,
          internationalCount,
          monthly: [...monthly.entries()].map(([period, value]) => ({ period, ...value })),
          quarterly: [...quarterly.entries()].map(([period, value]) => ({ period, ...value })),
          airlines: toTopList(airlines),
          routes: toTopList(routes),
          cities: toTopList(cities)
        }
      };
    });

    return result;
  }
}
