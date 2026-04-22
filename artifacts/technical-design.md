# Technical Design - 云舱 AirCabin

## 1. Architecture Overview

### 1.1 Client
- Platform: Android first
- Language: Kotlin
- UI: Jetpack Compose
- Architecture: MVVM + Clean Architecture + Repository pattern
- Navigation: Navigation Compose
- Local persistence: Room + DataStore
- Media access: MediaStore
- Background jobs: WorkManager
- Networking: Retrofit + OkHttp + Kotlinx Serialization
- Realtime: WebSocket
- OCR: ML Kit Text Recognition
- Charts: Compose chart library
- Image loading: Coil

### 1.2 Server
- API style: REST + WebSocket
- Suggested stack: Node.js/NestJS or Kotlin Ktor, MVP 推荐 NestJS 便于 API、WebSocket、风控中间件统一管理
- Storage:
  - PostgreSQL: user / flight / chat / report metadata
  - Redis: chat room presence / rate limit / ephemeral session state
  - Object storage: only for optional exported report assets, not for raw photo upload
- Integrations:
  - flight schedule / flight status provider abstraction
  - content moderation / keyword rules

### 1.3 Core Principle
- Offline first for non-social core flows
- Cloud enhanced for sync and chat
- Photo analysis stays on device by default
- Flight map is estimation-first, realtime-enhanced when data is available

## 2. Module Boundaries

### 2.1 Android modules
- `app`: app shell, DI, navigation, theme
- `feature-flight-import`: OCR, manual import, flight record creation
- `feature-flight-live`: current flight card, route simulation, phase calculation
- `feature-stats`: current / monthly / yearly statistics and report card
- `feature-gallery-cleanup`: media scan, classification, selection, delete flow
- `feature-chat`: room join, message list, send, moderation feedback
- `feature-profile`: history, settings, privacy, permission state
- `core-data`: repositories, DTOs, Room tables
- `core-network`: APIs, websocket client, auth headers
- `core-ui`: design system, components, loading/empty/error states
- `core-utils`: time, formatting, logging, connectivity

### 2.2 Server modules
- auth/device identity
- flight records
- flight route/status adapter
- yearly stats aggregation
- chat room and message relay
- moderation / risk control
- report generation metadata

## 3. Key Technical Decisions

### 3.1 Flight route simulation
When realtime data is unavailable:
- Use departure airport, destination airport, scheduled departure/arrival time as baseline
- Generate great-circle polyline or simplified route polyline
- Compute current phase from elapsed ratio + phase rules
- Display `模拟航线` / `实时修正` / `离线回退` as lightweight state chips

### 3.2 Realtime flight data abstraction
Expose a provider interface:
- `getFlightSchedule(flightNo, date)`
- `getFlightStatus(flightNo, date)`
- `getRouteGeometry(flightNo, date)`

This avoids coupling MVP to a single supplier.

### 3.3 Photo processing
- Scan local metadata via MediaStore
- Build local `media_asset` index
- Classify screenshot / large video by metadata rules
- Similar-photo grouping: first version uses pHash or lightweight perceptual clustering
- Delete via Android system confirmation flow, never silent delete

### 3.4 Chat architecture
- Room key: `flight_no + departure_date + cabin_class`
- Join requires verified flight record and room validity window
- WebSocket for realtime messaging
- Redis or in-memory layer for room fan-out in MVP
- Moderation pipeline:
  - pre-send keyword check
  - rate limit
  - report queue
  - mute / blocklist enforcement

### 3.5 Sync strategy
- Flight records, stats summaries, room metadata can sync to cloud
- Raw photos do not sync by default
- Pending writes use retry queue via WorkManager
- Offline-created flight records can be synced after connectivity recovery

## 4. Data Model Summary

### Core entities
- `user_profile`
- `flight_record`
- `flight_session`
- `boarding_import_task`
- `media_asset`
- `media_similarity_group`
- `yearly_flight_summary`
- `chat_room`
- `chat_member`
- `chat_message`
- `report_ticket`

### Important relationships
- One user has many flight records
- One flight record may map to one current flight session
- One chat room belongs to one flight number + date + cabin
- Many media assets may belong to one similarity group

## 5. API Contract Summary
- `POST /api/v1/flights`
- `GET /api/v1/flights/current`
- `GET /api/v1/flights/{flightId}/route`
- `GET /api/v1/flights/{flightId}/summary`
- `GET /api/v1/stats/yearly`
- `POST /api/v1/reports/yearly`
- `POST /api/v1/chat/rooms/join`
- `GET /api/v1/chat/rooms/{roomId}/messages`
- `WS /ws/chat`

## 6. State Strategy

### App-level state
- current flight
- connectivity state
- permission state
- user preference / theme / sync toggle

### Cached domain state
- imported flights
- yearly summaries
- media asset index
- room message cache

### UI states
Every screen must implement:
- loading
- empty
- error
- offline fallback
- partial data / stale cache indicator when applicable

## 7. Security and Privacy Strategy
- Boarding pass sensitive fields are masked in UI and logs
- Photo data processed locally, not uploaded by default
- Chat identity is anonymized per room
- API transport uses HTTPS only
- Local sensitive tokens stored with Android secure storage
- Delete actions and moderation actions require audit logging on server side

## 8. Risks and Mitigations
- Flight supplier uncertainty -> abstract provider layer and simulation fallback
- OCR accuracy variance -> mandatory confirmation step after parse
- Chat abuse risk -> closed rooms + moderation + rate limit + block/report
- Gallery delete risk -> preview + system confirm + result feedback
- Offline complexity -> keep server dependency out of core offline flows

## 9. QA Entry Criteria
Before entering QA:
- Android app must complete import -> current flight -> stats -> gallery cleanup primary path
- Chat room join/send/report path must be testable in network-on environment
- All declared scripts and handoff files must exist
- `scripts/validate_handoff.py` must pass for implementation handoff

## 10. Implementation Constraints
- Do not start implementation until `tasks.md` is explicitly approved
- Keep first release Android-only
- Keep chat as network-dependent enhancement, not hard dependency for core value
- Preserve visual distinction between estimation and realtime states in all relevant screens
