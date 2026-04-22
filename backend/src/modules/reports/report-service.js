import { DataStore } from "../../storage/data-store.js";

export class ReportService {
  constructor(store, riskMiddleware, statsService) {
    this.store = store;
    this.riskMiddleware = riskMiddleware;
    this.statsService = statsService;
  }

  async createReport(user, payload) {
    const risk = this.riskMiddleware.check("report", user.id, payload.reason ?? "");
    if (!risk.ok) {
      return {
        error: {
          code: risk.reason === "rate_limited" ? "REPORT_RATE_LIMITED" : "REPORT_BLOCKED",
          message: "Report request was blocked by risk control.",
          details: risk.details
        }
      };
    }

    const ticket = {
      id: DataStore.createId("report"),
      reporterUserId: user.id,
      roomId: payload.roomId,
      messageId: payload.messageId ?? null,
      reportedUserId: payload.reportedUserId ?? null,
      category: payload.category,
      reason: payload.reason,
      status: "queued",
      createdAt: new Date().toISOString()
    };

    await this.store.transaction((state) => {
      state.reportTickets.push(ticket);
      state.moderationEvents.push({
        id: DataStore.createId("mod"),
        type: "report_queued",
        reportId: ticket.id,
        createdAt: ticket.createdAt
      });
    });

    return ticket;
  }

  async createYearlyReportMetadata(userId, year) {
    const summary = await this.statsService.getYearlySummary(userId, year);
    const topAirline = summary.breakdown.airlines[0]?.key ?? null;
    const topRoute = summary.breakdown.routes[0]?.key ?? null;

    return {
      year,
      title: `${year} 飞行年度报告`,
      shareCardStatus: "ready",
      generatedAt: new Date().toISOString(),
      highlights: {
        totalFlights: summary.totals.count,
        totalMinutes: summary.totals.totalMinutes,
        totalDistanceKm: summary.totals.totalDistanceKm,
        topAirline,
        topRoute
      }
    };
  }
}
