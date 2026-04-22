import { DataStore } from "../../storage/data-store.js";

export class ReportService {
  constructor(store, riskMiddleware) {
    this.store = store;
    this.riskMiddleware = riskMiddleware;
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
}
