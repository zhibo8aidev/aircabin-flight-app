# Spec: 云舱 AirCabin Android Delivery Plan

## 1. Android 研发任务拆解

### 1.1 客户端架构
- 技术栈：Kotlin + Jetpack Compose + MVVM + Clean Architecture
- 网络层：Retrofit + OkHttp + Kotlinx Serialization
- 本地存储：Room + DataStore
- 媒体访问：MediaStore + 系统删除确认 API
- 后台任务：WorkManager
- 地图方案：Mapbox / MapLibre，离线缓存航线底图和机场基础数据
- OCR：ML Kit 文本识别，复杂场景可预留 PaddleOCR
- 图表：Compose 图表库或 MPAndroidChart
- 即时通信：WebSocket

### 1.2 客户端模块拆分
#### 模块 A，基础框架
- App Shell、导航、主题系统、埋点基建
- 登录/本地初始化能力（首版可匿名本地账号）
- 权限管理与隐私弹窗

#### 模块 B，航班导入与档案
- 登机牌拍照/截图导入
- OCR 结构化解析
- 航班记录增删改查
- 历史航班列表与详情

#### 模块 C，航线模拟与当前航班
- 当前航班卡片
- 航线图渲染
- 飞行阶段计算
- ETA、速度、海拔、剩余航程展示
- 离线模拟逻辑

#### 模块 D，飞行统计
- 当前航班统计
- 月/季/年统计聚合
- 图表页
- 年度飞行报告卡片生成

#### 模块 E，相册整理
- 相册扫描与本地索引
- 分类聚合（截图、大视频、相似图、连拍）
- 预览、多选、滑选、批量删除
- 释放空间反馈

#### 模块 F，聊天室
- 房间加入与验证
- 消息列表与发送
- 话题标签
- 举报、拉黑、敏感词拦截反馈
- 网络状态感知与重连

### 1.3 服务端模块拆分
- 用户与设备绑定服务
- 航班记录服务
- 航班状态/模拟辅助服务
- 聊天房间服务
- 消息审核与风控服务
- 统计汇总服务

## 2. 开发任务列表（研发视角）

### Sprint 0，1 周，技术准备
- 搭建 Android 工程、CI、环境配置
- 设计本地数据库 schema
- 确定地图 SDK、OCR SDK、聊天协议
- 定义埋点和日志规范

### Sprint 1，2 周，基础能力 + 航班导入
- 首页骨架与底部导航
- 登机牌导入页
- OCR 结果确认页
- 航班记录本地存储
- 当前航班卡片展示

### Sprint 2，2 周，航线模拟 + 统计
- 航班页地图容器
- 模拟航线算法
- 飞行阶段与时间计算
- 本次飞行统计页
- 年度统计基础图表

### Sprint 3，2 周，相册整理
- 相册权限与扫描
- 媒体索引表
- 分类卡片
- 多选/滑选/预览/删除流程
- 释放空间统计

### Sprint 4，2 周，聊天室 + 完整联调
- 聊天室 UI 与 WebSocket 接入
- 房间验证逻辑
- 举报、拉黑、敏感词反馈
- 弱网重连与降级提示
- 全链路联调与 Bug 修复

### Sprint 5，1 周，测试与预发布
- 回归测试
- 权限与隐私专项测试
- 弱网/飞行模式专项测试
- 崩溃监控与性能调优

## 3. 接口文档草案

## 3.1 鉴权约定
首版可采用 `device_token` + `user_id` 的轻鉴权模型，后续再切换手机号或正式账号体系。

通用响应：
```json
{
  "code": 0,
  "message": "ok",
  "data": {}
}
```

## 3.2 航班记录接口

### 1）创建航班记录
`POST /api/v1/flights`

Request:
```json
{
  "flightNo": "CA1234",
  "departureDate": "2026-04-22",
  "departureAirport": "PEK",
  "arrivalAirport": "SZX",
  "airlineCode": "CA",
  "cabinClass": "economy",
  "seatNo": "12A",
  "sourceType": "ocr"
}
```

Response:
```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "flightId": "flt_xxx",
    "verifyStatus": "pending"
  }
}
```

### 2）获取当前航班详情
`GET /api/v1/flights/current`

Response fields:
- flightId
- flightNo
- departureAirport
- arrivalAirport
- scheduledDepartureTs
- scheduledArrivalTs
- phase
- etaTs
- simulationMode

### 3）获取历史航班列表
`GET /api/v1/flights?year=2026&page=1&pageSize=20`

## 3.3 OCR 解析接口

### 4）上传 OCR 解析请求
`POST /api/v1/boarding/parse`

Request:
```json
{
  "imageBase64": "...",
  "sourceType": "camera"
}
```

Response:
```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "flightNo": "CA1234",
    "departureAirport": "PEK",
    "arrivalAirport": "SZX",
    "departureDate": "2026-04-22",
    "cabinClass": "economy",
    "seatNo": "12A",
    "confidence": 0.86
  }
}
```

> 说明：如 OCR 全端侧实现，此接口可延后，仅保留本地解析结果上传接口。

## 3.4 航线模拟与状态接口

### 5）获取航线与基础轨迹
`GET /api/v1/flights/{flightId}/route`

Response:
```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "origin": {"code": "PEK", "lat": 40.08, "lng": 116.58},
    "destination": {"code": "SZX", "lat": 22.64, "lng": 113.81},
    "polyline": [
      [40.08, 116.58],
      [35.12, 115.20],
      [30.20, 114.01],
      [22.64, 113.81]
    ],
    "simulationMode": true
  }
}
```

### 6）获取飞行统计摘要
`GET /api/v1/flights/{flightId}/summary`

Fields:
- elapsedMinutes
- remainMinutes
- totalMinutes
- speedKmh
- altitudeM
- etaTs

## 3.5 统计接口

### 7）获取年度统计
`GET /api/v1/stats/yearly?year=2026`

Response fields:
- totalFlights
- totalHours
- totalDistanceKm
- domesticCount
- internationalCount
- topAirlines
- topRoutes
- monthlyTrend

### 8）生成年度报告
`POST /api/v1/reports/yearly`

Request:
```json
{
  "year": 2026,
  "theme": "dark"
}
```

## 3.6 聊天室接口

### 9）加入聊天室
`POST /api/v1/chat/rooms/join`

Request:
```json
{
  "flightId": "flt_xxx",
  "cabinClass": "economy"
}
```

Response:
```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "roomId": "room_xxx",
    "anonymousName": "12A旅客",
    "wsToken": "token_xxx"
  }
}
```

### 10）获取历史消息
`GET /api/v1/chat/rooms/{roomId}/messages?cursor=xxx`

### 11）WebSocket 消息协议
连接：`wss://api.xxx.com/ws/chat?token=...`

客户端发送：
```json
{
  "type": "message.send",
  "roomId": "room_xxx",
  "content": "落地后有人一起拼车吗？",
  "topicTag": "拼车"
}
```

服务端回包：
```json
{
  "type": "message.ack",
  "messageId": "msg_xxx",
  "status": "accepted"
}
```

审核拦截：
```json
{
  "type": "message.reject",
  "reason": "risk_control"
}
```

## 4. 数据结构建议
客户端 Room 表建议：
- user_profile
- flight_record
- flight_session
- boarding_import_task
- media_asset
- media_similarity_group
- yearly_flight_summary
- chat_room_cache
- chat_message_cache

## 5. 风险清单与技术注意事项
- Android 相册删除必须走系统删除确认能力，不可静默删图
- 相似图识别首版建议采用轻量 pHash，不要一开始就上重模型
- 航线 3D 效果不建议卡首版工期，先以伪 3D 视角满足评审
- 聊天室必须明确依赖网络条件，飞行模式无 Wi-Fi 不可承诺实时通信
- 航班实时数据供应商接入需预留替换层，避免供应商耦合

## 6. 技术排期建议
总排期建议：**8 到 10 周**

### 方案 A，标准节奏，推荐
- 第 1 周：技术准备 + 原型冻结
- 第 2-3 周：航班导入 + 首页 + 本地数据层
- 第 4-5 周：航线模拟 + 飞行统计
- 第 6-7 周：相册整理
- 第 8 周：聊天室 + 联调
- 第 9 周：测试修复
- 第 10 周：灰度发布准备

### 资源建议
- Android 研发：2 人
- 后端研发：1 人
- 产品：1 人
- 设计：1 人
- 测试：1 人

### 里程碑
- M1：PRD/Spec 评审通过
- M2：设计评审通过
- M3：技术方案与任务拆解通过
- M4：Android 可运行 Demo
- M5：提测版本
- M6：灰度版本
