import path from "node:path";
import { fileURLToPath } from "node:url";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

export const config = {
  port: Number.parseInt(process.env.PORT ?? "8787", 10),
  host: process.env.HOST ?? "0.0.0.0",
  dataFile: process.env.AIRCABIN_DATA_FILE ?? path.join(__dirname, "..", "data", "store.json"),
  defaultYear: new Date().getUTCFullYear(),
  roomWindowHours: {
    beforeDeparture: 2,
    afterArrival: 6
  }
};
