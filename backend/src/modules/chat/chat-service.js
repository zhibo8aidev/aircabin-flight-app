import { DataStore } from "../../storage/data-store.js";
import { config } from "../../config.js";

function addHours(isoTime, hours) {
  const date = new Date(isoTime);
  date.setHours(date.getHours() + hours);
  return date;
}

function buildRoomKey(record) {
  return `${record.flightNo}:${record.departureDate}:${record.cabinClass}`;
}

function buildAnonymousAlias(userId, roomKey) {
  return `Seat-${Buffer.from(`${userId}:${roomKey}`).toString("hex").slice(0, 4).toUpperCase()}`;
}

export class ChatService {
  constructor(store, riskMiddleware) {
    this.store = store;
    this.riskMiddleware = riskMiddleware;
  }

  async joinRoom(user, payload, flightRecord) {
    const roomKey = buildRoomKey(flightRecord);
    const roomStart = addHours(flightRecord.departureTime, -config.roomWindowHours.beforeDeparture);
    const roomEnd = addHours(flightRecord.arrivalTime, config.roomWindowHours.afterArrival);
    const now = new Date();

    if (now < roomStart || now > roomEnd) {
      return {
        error: {
          code: "ROOM_WINDOW_CLOSED",
          message: "Room is not available outside the allowed lifecycle window."
        }
      };
    }

    const risk = this.riskMiddleware.check("join", user.id);
    if (!risk.ok) {
      return {
        error: {
          code: risk.reason === "rate_limited" ? "ROOM_JOIN_RATE_LIMITED" : "ROOM_JOIN_BLOCKED",
          message: "Room join request was blocked by risk control.",
          details: risk.details
        }
      };
    }

    const result = await this.store.transaction((state) => {
      let room = state.chatRooms.find((item) => item.roomKey === roomKey);
      if (!room) {
        room = {
          id: DataStore.createId("room"),
          roomKey,
          flightRecordId: flightRecord.id,
          flightNo: flightRecord.flightNo,
          departureDate: flightRecord.departureDate,
          cabinClass: flightRecord.cabinClass,
          announcement: "封闭匿名，仅限同舱位",
          createdAt: new Date().toISOString()
        };
        state.chatRooms.push(room);
      }

      const existingMessageCount = state.chatMessages.filter((item) => item.roomId === room.id).length;
      if (!state.chatMessages.some((item) => item.roomId === room.id)) {
        state.chatMessages.push({
          id: DataStore.createId("msg"),
          roomId: room.id,
          senderUserId: "system",
          senderAlias: "AirCabin",
          kind: "system",
          content: `欢迎进入 ${flightRecord.flightNo} ${flightRecord.cabinClass} 聊天室`,
          createdAt: new Date().toISOString()
        });
      }

      return {
        room,
        membership: {
          userId: user.id,
          alias: buildAnonymousAlias(user.id, roomKey),
          joinedAt: new Date().toISOString()
        },
        existingMessageCount
      };
    });

    return {
      roomId: result.room.id,
      roomKey: result.room.roomKey,
      announcement: result.room.announcement,
      flightNo: result.room.flightNo,
      departureDate: result.room.departureDate,
      cabinClass: result.room.cabinClass,
      membership: result.membership
    };
  }

  async getMessages(roomId, limit = 50) {
    return this.store.read((state) => state.chatMessages.filter((item) => item.roomId === roomId).sort((left, right) => new Date(left.createdAt).getTime() - new Date(right.createdAt).getTime()).slice(-limit));
  }
}
