import http from "node:http";
import { buildApp } from "./app.js";
import { config } from "./config.js";
import { withRequestId } from "./lib/http.js";

const app = await buildApp();

const server = http.createServer(withRequestId((req, res) => app.handle(req, res)));

server.listen(config.port, config.host, () => {
  console.log(`AirCabin backend listening on http://${config.host}:${config.port}`);
});
