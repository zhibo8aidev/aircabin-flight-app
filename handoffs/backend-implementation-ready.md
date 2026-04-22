# Backend Implementation Ready

## 交付文件

- `backend/package.json`
- `backend/README.md`
- `backend/data/store.json`
- `backend/src/server.js`
- `backend/src/app.js`
- `backend/src/config.js`
- `backend/src/lib/http.js`
- `backend/src/lib/router.js`
- `backend/src/storage/data-store.js`
- `backend/src/storage/seed.js`
- `backend/src/modules/auth/auth-service.js`
- `backend/src/modules/flights/flight-provider.js`
- `backend/src/modules/flights/mock-flight-provider.js`
- `backend/src/modules/flights/flight-service.js`
- `backend/src/modules/stats/stats-service.js`
- `backend/src/modules/chat/chat-service.js`
- `backend/src/modules/moderation/risk-middleware.js`
- `backend/src/modules/reports/report-service.js`
- `scripts/validate_handoff.py`
- `runs/20260422-backend-implementation-progress.md`

## 接口范围

已实现 REST 接口：

- `GET /health`
- `POST /api/v1/flights`
- `GET /api/v1/flights/current`
- `GET /api/v1/flights/:flightId/summary`
- `GET /api/v1/flights/:flightId/route`
- `GET /api/v1/stats/yearly`
- `POST /api/v1/reports/yearly`
- `POST /api/v1/chat/rooms/join`
- `GET /api/v1/chat/rooms/:roomId/messages`
- `POST /api/v1/reports`

已落边界：

- 航班 provider 抽象：`FlightProvider`
- 默认 provider：`MockFlightProvider`
- 聊天风控入口：`RiskMiddleware`
- 轻量鉴权：`x-device-token` + `x-user-id`
- 通用响应结构与错误码返回

## 未完成项

- 未接入 PostgreSQL / Redis，当前使用本地 JSON 文件存储
- 未实现 WebSocket 实时消息通道、发送消息、ack、断线重连
- 未实现拉黑、禁言、审核后台消费队列
- 未接入真实航班供应商，仅支持计划数据模拟
- 未实现 migration、生产级配置管理和自动化测试

## 联调依赖

- Node.js 22+
- 请求头需带 demo 鉴权：
  - `x-device-token: demo-device-token`
  - `x-user-id: user_demo`
- 聊天室入房依赖用户先持有可匹配的 `flightRecordId`
- 当前房间生命周期校验为：起飞前 2 小时到落地后 6 小时

## 启动方式

```bash
cd backend
npm start
```

默认地址：`http://127.0.0.1:8787`

接口示例见：`backend/README.md`

## 自检结果

- 已确认 `backend/README.md`、`backend/src/server.js`、`handoffs/backend-implementation-ready.md`、`runs/20260422-backend-implementation-progress.md` 存在
- 已本地启动服务并验证健康检查、航班记录、当前航班、年度统计、聊天室入房/历史消息、举报、年度报告元数据接口
- 已补充 `scripts/validate_handoff.py` 用于交付文件存在性检查
