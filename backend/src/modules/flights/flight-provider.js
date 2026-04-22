export class FlightProvider {
  async getFlightSchedule() {
    throw new Error("FlightProvider#getFlightSchedule must be implemented.");
  }

  async getFlightStatus() {
    throw new Error("FlightProvider#getFlightStatus must be implemented.");
  }

  async getRouteGeometry() {
    throw new Error("FlightProvider#getRouteGeometry must be implemented.");
  }
}
