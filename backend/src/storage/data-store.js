import fs from "node:fs/promises";
import path from "node:path";
import { randomUUID } from "node:crypto";
import { seedData } from "./seed.js";

function clone(value) {
  return JSON.parse(JSON.stringify(value));
}

export class DataStore {
  constructor(filePath) {
    this.filePath = filePath;
    this.state = null;
  }

  async init() {
    if (this.state !== null) {
      return;
    }

    try {
      const content = await fs.readFile(this.filePath, "utf8");
      this.state = JSON.parse(content);
    } catch (error) {
      if (error.code !== "ENOENT") {
        throw error;
      }

      this.state = clone(seedData);
      await this.persist();
    }
  }

  async persist() {
    await fs.mkdir(path.dirname(this.filePath), { recursive: true });
    await fs.writeFile(this.filePath, JSON.stringify(this.state, null, 2));
  }

  async transaction(mutator) {
    await this.init();
    const draft = clone(this.state);
    const result = await mutator(draft);
    this.state = draft;
    await this.persist();
    return result;
  }

  async read(selector) {
    await this.init();
    return selector(clone(this.state));
  }

  static createId(prefix) {
    return `${prefix}_${randomUUID().replaceAll("-", "").slice(0, 12)}`;
  }
}
