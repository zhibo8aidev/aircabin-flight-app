# AirCabin Backend MVP

最小可运行后端，覆盖 AirCabin MVP 当前需要的核心接口：

- 航班记录创建
- 当前航班摘要与航线信息
- 年度统计
- 聊天室入房与历史消息
- 举报接口
- 年度报告元数据接口

当前实现不依赖第三方包，基于 Node.js 22 原生 `http` 模块，方便在空仓库内直接启动验证。后续可按相同模块边界迁移到 NestJS。

## 目录

```text
backend/
  package.json
  src/
    app.js
    server.js
    modules/
      auth/
      chat/
      flights/
      moderation/
      reports/
      stats/
    storage/
```

## 本地启动

要求：

- Node.js >= 22

启动：

```bash
cd backend
npm start
```

默认监听：`http://127.0.0.1:8787`

可选环境变量：

- `HOST`，默认 `0.0.0.0`
- `PORT`，默认 `8787`
- `AIRCABIN_DATA_FILE`，默认 `backend/data/store.json`

## 鉴权约定

MVP 使用轻量设备鉴权，所有业务接口都要求请求头：

```text
x-device-token: demo-device-token
x-user-id: user_demo
```

内置 demo 用户会在首次启动时自动写入数据文件。

## 已实现接口

### 健康检查

```bash
curl http://127.0.0.1:8787/health
```

### 创建航班记录

```bash
curl -X POST http://127.0.0.1:8787/api/v1/flights \
  -H 'content-type: application/json' \
  -H 'x-device-token: demo-device-token' \
  -H 'x-user-id: user_demo' \
  -d '{
    "flightNo": "MU5101",
    "airline": "China Eastern",
    "departureAirport": "SHA",
    "arrivalAirport": "CAN",
    "departureCity": "Shanghai",
    "arrivalCity": "Guangzhou",
    "departureTime": "2026-05-01T01:00:00.000Z",
    "arrivalTime": "2026-05-01T03:20:00.000Z",
    "departureDate": "2026-05-01",
    "cabinClass": "business",
    "seatNo": "3A",
    "distanceKm": 1211,
    "isInternational": false
  }'
```

### 当前航班摘要

```bash
curl http://127.0.0.1:8787/api/v1/flights/current \
  -H 'x-device-token: demo-device-token' \
  -H 'x-user-id: user_demo'
```

### 单航班摘要

```bash
curl http://127.0.0.1:8787/api/v1/flights/flight_demo_ca123/summary \
  -H 'x-device-token: demo-device-token' \
  -H 'x-user-id: user_demo'
```

### 单航班航线

```bash
curl http://127.0.0.1:8787/api/v1/flights/flight_demo_ca123/route \
  -H 'x-device-token: demo-device-token' \
  -H 'x-user-id: user_demo'
```

### 年度统计

```bash
curl 'http://127.0.0.1:8787/api/v1/stats/yearly?year=2026' \
  -H 'x-device-token: demo-device-token' \
  -H 'x-user-id: user_demo'
```

### 年度报告元数据

```bash
curl -X POST http://127.0.0.1:8787/api/v1/reports/yearly \
  -H 'content-type: application/json' \
  -H 'x-device-token: demo-device-token' \
  -H 'x-user-id: user_demo' \
  -d '{"year": 2026}'
```

### 聊天室入房

```bash
curl -X POST http://127.0.0.1:8787/api/v1/chat/rooms/join \
  -H 'content-type: application/json' \
  -H 'x-device-token: demo-device-token' \
  -H 'x-user-id: user_demo' \
  -d '{"flightRecordId": "flight_demo_ca123"}'
```

### 聊天历史消息

```bash
curl http://127.0.0.1:8787/api/v1/chat/rooms/<roomId>/messages \
  -H 'x-device-token: demo-device-token' \
  -H 'x-user-id: user_demo'
```

### 举报接口

```bash
curl -X POST http://127.0.0.1:8787/api/v1/reports \
  -H 'content-type: application/json' \
  -H 'x-device-token: demo-device-token' \
  -H 'x-user-id: user_demo' \
  -d '{
    "roomId": "room_xxx",
    "messageId": "msg_xxx",
    "reportedUserId": "user_other",
    "category": "abuse",
    "reason": "辱骂内容"
  }'
```

## 当前设计取舍

- 航班数据 provider 使用 `FlightProvider` 抽象 + `MockFlightProvider` 默认实现，预留接第三方航班计划/状态源。
- 聊天风控使用 `RiskMiddleware` 统一入口，当前提供关键词拦截和入房/举报频控。
- 数据存储使用 JSON 文件，便于本地 MVP 验证；后续可替换为 PostgreSQL + Redis。
- WebSocket 消息通道本轮只预留模块边界，未实现实时消息收发。

## 关键限制

- 当前无真实航班供应商接入，航线与阶段均为计划数据模拟。
- 聊天历史仅提供读取，未实现发送消息与 ack。
- 频控与关键词规则为进程内内存态，重启后会重置。
