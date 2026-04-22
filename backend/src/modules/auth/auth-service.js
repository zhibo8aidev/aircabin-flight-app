import { unauthorized } from "../../lib/http.js";

export class AuthService {
  constructor(store) {
    this.store = store;
  }

  async requireUser(req, res) {
    const deviceToken = req.headers["x-device-token"];
    if (!deviceToken || typeof deviceToken !== "string") {
      unauthorized(res);
      return null;
    }

    const explicitUserId = req.headers["x-user-id"];
    const user = await this.store.read((state) => {
      if (typeof explicitUserId === "string") {
        return state.users.find((item) => item.id === explicitUserId && item.deviceToken === deviceToken) ?? null;
      }

      return state.users.find((item) => item.deviceToken === deviceToken) ?? null;
    });

    if (!user) {
      unauthorized(res, "Unknown device token or user.");
      return null;
    }

    return user;
  }
}
