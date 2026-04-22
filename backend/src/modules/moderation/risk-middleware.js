const DEFAULT_BLOCKED_WORDS = ["wx", "wechat", "赌博", "博彩", "spam"];

export class RiskMiddleware {
  constructor(options = {}) {
    this.blockedWords = options.blockedWords ?? DEFAULT_BLOCKED_WORDS;
    this.rateLimitWindowMs = options.rateLimitWindowMs ?? 60_000;
    this.joinLimit = options.joinLimit ?? 10;
    this.reportLimit = options.reportLimit ?? 20;
    this.activity = new Map();
  }

  check(action, actorId, content = "") {
    const now = Date.now();
    const key = `${action}:${actorId}`;
    const current = this.activity.get(key) ?? [];
    const activeTimestamps = current.filter((timestamp) => now - timestamp < this.rateLimitWindowMs);
    const limit = action === "report" ? this.reportLimit : this.joinLimit;

    if (activeTimestamps.length >= limit) {
      return {
        ok: false,
        reason: "rate_limited",
        details: {
          action,
          limit,
          windowMs: this.rateLimitWindowMs
        }
      };
    }

    const normalized = content.toLowerCase();
    const matchedWord = this.blockedWords.find((word) => normalized.includes(word.toLowerCase()));
    if (matchedWord) {
      return {
        ok: false,
        reason: "blocked_keyword",
        details: {
          action,
          matchedWord
        }
      };
    }

    activeTimestamps.push(now);
    this.activity.set(key, activeTimestamps);
    return {
      ok: true
    };
  }
}
