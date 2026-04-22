import { randomUUID } from "node:crypto";

const JSON_HEADERS = {
  "content-type": "application/json; charset=utf-8"
};

export function withRequestId(handler) {
  return async (req, res) => {
    req.requestId = randomUUID();
    res.setHeader("x-request-id", req.requestId);
    await handler(req, res);
  };
}

export function json(res, statusCode, payload) {
  res.writeHead(statusCode, JSON_HEADERS);
  res.end(JSON.stringify(payload, null, 2));
}

export function success(res, data, meta = {}) {
  json(res, 200, {
    success: true,
    data,
    meta
  });
}

export function created(res, data, meta = {}) {
  json(res, 201, {
    success: true,
    data,
    meta
  });
}

export function error(res, statusCode, code, message, details = {}) {
  json(res, statusCode, {
    success: false,
    error: {
      code,
      message,
      details
    }
  });
}

export async function readJsonBody(req) {
  const chunks = [];
  for await (const chunk of req) {
    chunks.push(chunk);
  }

  if (chunks.length === 0) {
    return {};
  }

  const raw = Buffer.concat(chunks).toString("utf8");
  return JSON.parse(raw);
}

export function notFound(res) {
  error(res, 404, "NOT_FOUND", "Route not found.");
}

export function methodNotAllowed(res, allowedMethods) {
  res.setHeader("allow", allowedMethods.join(", "));
  error(res, 405, "METHOD_NOT_ALLOWED", "Method is not allowed for this route.", {
    allowedMethods
  });
}

export function badRequest(res, message, details = {}) {
  error(res, 400, "BAD_REQUEST", message, details);
}

export function unauthorized(res, message = "Missing or invalid device token.") {
  error(res, 401, "UNAUTHORIZED", message);
}

export function tooManyRequests(res, message, details = {}) {
  error(res, 429, "RATE_LIMITED", message, details);
}
