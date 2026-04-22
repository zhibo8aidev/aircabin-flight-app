import { Router } from "./lib/router.js";
import { badRequest, created, error, readJsonBody, success, tooManyRequests } from "./lib/http.js";
import { DataStore } from "./storage/data-store.js";
import { config } from "./config.js";
import { MockFlightProvider } from "./modules/flights/mock-flight-provider.js";
import { FlightService } from "./modules/flights/flight-service.js";
import { StatsService } from "./modules/stats/stats-service.js";
import { RiskMiddleware } from "./modules/moderation/risk-middleware.js";
import { ChatService } from "./modules/chat/chat-service.js";
import { ReportService } from "./modules/reports/report-service.js";
import { AuthService } from "./modules/auth/auth-service.js";

function validateFlightPayload(payload) {
  const required = [
    "flightNo",
    "airline",
    "departureAirport",
    "arrivalAirport",
    "departureCity",
    "arrivalCity",
    "departureTime",
    "arrivalTime",
    "departureDate",
    "cabinClass"
  ];

  const missing = required.filter((field) => !payload[field]);
  if (missing.length > 0) {
    return `Missing required flight fields: ${missing.join(", ")}`;
  }

  return null;
}

function handleRiskError(res, riskError) {
  if (riskError.code?.includes("RATE_LIMITED")) {
    tooManyRequests(res, riskError.message, riskError.details ?? {});
    return;
  }

  error(res, 403, riskError.code, riskError.message, riskError.details ?? {});
}

export async function buildApp() {
  const store = new DataStore(config.dataFile);
  await store.init();

  const provider = new MockFlightProvider();
  const riskMiddleware = new RiskMiddleware();
  const authService = new AuthService(store);
  const flightService = new FlightService(store, provider);
  const statsService = new StatsService(store);
  const chatService = new ChatService(store, riskMiddleware);
  const reportService = new ReportService(store, riskMiddleware);

  const router = new Router();

  router.register("GET", "/health", async (_req, res) => {
    success(res, {
      service: "aircabin-backend",
      status: "ok"
    });
  });

  router.register("POST", "/api/v1/flights", async (req, res) => {
    const user = await authService.requireUser(req, res);
    if (!user) {
      return;
    }

    const payload = await readJsonBody(req);
    const validationError = validateFlightPayload(payload);
    if (validationError) {
      badRequest(res, validationError);
      return;
    }

    const record = await flightService.createFlightRecord(user, payload);
    created(res, record);
  });

  router.register("GET", "/api/v1/flights/current", async (req, res) => {
    const user = await authService.requireUser(req, res);
    if (!user) {
      return;
    }

    const currentFlight = await flightService.getCurrentFlight(user.id);
    success(res, currentFlight);
  });

  router.register("GET", "/api/v1/flights/:flightId/summary", async (req, res) => {
    const user = await authService.requireUser(req, res);
    if (!user) {
      return;
    }

    const record = await flightService.getFlightRecord(user.id, req.params.flightId);
    if (!record) {
      error(res, 404, "FLIGHT_NOT_FOUND", "Flight record was not found.");
      return;
    }

    const summary = await flightService.buildFlightSummary(record);
    success(res, summary);
  });

  router.register("GET", "/api/v1/flights/:flightId/route", async (req, res) => {
    const user = await authService.requireUser(req, res);
    if (!user) {
      return;
    }

    const record = await flightService.getFlightRecord(user.id, req.params.flightId);
    if (!record) {
      error(res, 404, "FLIGHT_NOT_FOUND", "Flight record was not found.");
      return;
    }

    const summary = await flightService.buildFlightSummary(record);
    success(res, {
      flightId: summary.flightId,
      mode: summary.mode,
      phase: summary.phase,
      route: summary.route
    });
  });

  router.register("GET", "/api/v1/stats/yearly", async (req, res) => {
    const user = await authService.requireUser(req, res);
    if (!user) {
      return;
    }

    const year = Number.parseInt(req.query.year ?? String(config.defaultYear), 10);
    const summary = await statsService.getYearlySummary(user.id, year);
    success(res, summary);
  });

  router.register("POST", "/api/v1/chat/rooms/join", async (req, res) => {
    const user = await authService.requireUser(req, res);
    if (!user) {
      return;
    }

    const payload = await readJsonBody(req);
    if (!payload.flightRecordId) {
      badRequest(res, "Missing required field: flightRecordId");
      return;
    }

    const record = await flightService.getFlightRecord(user.id, payload.flightRecordId);
    if (!record) {
      error(res, 404, "FLIGHT_NOT_FOUND", "Flight record was not found.");
      return;
    }

    const joinResult = await chatService.joinRoom(user, payload, record);
    if (joinResult.error) {
      handleRiskError(res, joinResult.error);
      return;
    }

    created(res, joinResult);
  });

  router.register("GET", "/api/v1/chat/rooms/:roomId/messages", async (req, res) => {
    const user = await authService.requireUser(req, res);
    if (!user) {
      return;
    }

    const limit = Number.parseInt(req.query.limit ?? "50", 10);
    const messages = await chatService.getMessages(req.params.roomId, limit);
    success(res, {
      roomId: req.params.roomId,
      items: messages
    });
  });

  router.register("POST", "/api/v1/reports", async (req, res) => {
    const user = await authService.requireUser(req, res);
    if (!user) {
      return;
    }

    const payload = await readJsonBody(req);
    const missing = ["roomId", "category", "reason"].filter((field) => !payload[field]);
    if (missing.length > 0) {
      badRequest(res, `Missing required report fields: ${missing.join(", ")}`);
      return;
    }

    const report = await reportService.createReport(user, payload);
    if (report.error) {
      handleRiskError(res, report.error);
      return;
    }

    created(res, report);
  });

  return {
    async handle(req, res) {
      try {
        await router.handle(req, res);
      } catch (requestError) {
        error(res, 500, "INTERNAL_ERROR", "Unexpected server error.", {
          requestId: req.requestId,
          message: requestError.message
        });
      }
    }
  };
}
