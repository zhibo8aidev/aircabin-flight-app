export const seedData = {
  users: [
    {
      id: "user_demo",
      deviceToken: "demo-device-token",
      nickname: "CloudSeat"
    }
  ],
  flightRecords: [
    {
      id: "flight_demo_ca123",
      userId: "user_demo",
      flightNo: "CA123",
      airline: "Air China",
      departureAirport: "PEK",
      arrivalAirport: "SHA",
      departureCity: "Beijing",
      arrivalCity: "Shanghai",
      departureTime: "2026-04-22T05:30:00.000Z",
      arrivalTime: "2026-04-22T07:40:00.000Z",
      departureDate: "2026-04-22",
      cabinClass: "economy",
      seatNo: "18A",
      source: "manual",
      status: "scheduled",
      distanceKm: 1088,
      isInternational: false,
      createdAt: "2026-04-22T04:50:00.000Z",
      updatedAt: "2026-04-22T04:50:00.000Z"
    }
  ],
  chatRooms: [],
  chatMessages: [],
  reportTickets: [],
  moderationEvents: []
};
