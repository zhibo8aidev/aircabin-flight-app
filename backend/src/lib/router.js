import { methodNotAllowed, notFound } from "./http.js";

function normalizePathname(pathname) {
  if (pathname.length > 1 && pathname.endsWith("/")) {
    return pathname.slice(0, -1);
  }

  return pathname;
}

function matchSegments(routeSegments, pathSegments) {
  if (routeSegments.length !== pathSegments.length) {
    return null;
  }

  const params = {};
  for (let index = 0; index < routeSegments.length; index += 1) {
    const routeSegment = routeSegments[index];
    const pathSegment = pathSegments[index];

    if (routeSegment.startsWith(":")) {
      params[routeSegment.slice(1)] = decodeURIComponent(pathSegment);
      continue;
    }

    if (routeSegment !== pathSegment) {
      return null;
    }
  }

  return params;
}

export class Router {
  constructor() {
    this.routes = [];
  }

  register(method, path, handler) {
    this.routes.push({
      method: method.toUpperCase(),
      path,
      handler,
      segments: normalizePathname(path).split("/").filter(Boolean)
    });
  }

  async handle(req, res) {
    const url = new URL(req.url, "http://localhost");
    const pathname = normalizePathname(url.pathname);
    const pathSegments = pathname.split("/").filter(Boolean);

    let matchedPathButWrongMethod = false;

    for (const route of this.routes) {
      const params = matchSegments(route.segments, pathSegments);
      if (params === null) {
        continue;
      }

      if (route.method !== req.method.toUpperCase()) {
        matchedPathButWrongMethod = true;
        continue;
      }

      req.params = params;
      req.query = Object.fromEntries(url.searchParams.entries());
      await route.handler(req, res);
      return;
    }

    if (matchedPathButWrongMethod) {
      methodNotAllowed(res, [...new Set(this.routes.filter((route) => matchSegments(route.segments, pathSegments) !== null).map((route) => route.method))]);
      return;
    }

    notFound(res);
  }
}
