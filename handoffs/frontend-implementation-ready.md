# Frontend Implementation Ready

## 交付文件
- `client/`：Android Jetpack Compose 工程骨架，包含 `app` 模块、Gradle wrapper、主题、导航、假数据仓库与 MVP 页面实现。
- `client/app/src/main/java/com/aircabin/app/ui/AirCabinApp.kt`：首页、导入、航班、统计、相册、聊天室、我的六页实现，以及加载 / 空态 / 错误态 / 离线表达骨架。
- `client/app/src/main/java/com/aircabin/app/data/`：前端假数据模型与仓库，保留 `模拟航线 / 实时修正 / 离线回退` 状态表达。
- `runs/20260422-frontend-implementation-progress.md`：实施过程进度记录。

## 未完成项
- 未接入真实 OCR、Room、DataStore、Retrofit、WorkManager、MediaStore、WebSocket，仅保留前端壳与假数据主链路。
- 未实现真实 2D/3D 地图容器、航段算法、统计聚合、相册扫描与系统删除、聊天室收发与风控，仅实现符合设计基线的界面骨架。
- 未补客户端测试、CI、图标完善、多模块拆分；当前为单 `app` 模块 MVP 壳，便于继续开发。

## 联调依赖
- 航班导入：需要 OCR 识别结果结构与航班创建接口契约。
- 当前航班 / 统计：需要当前航班、路线、摘要、年度统计接口或本地数据源。
- 相册整理：需要 Android 媒体权限、MediaStore 扫描、系统删除确认流。
- 聊天室：需要入房校验接口、历史消息接口、WebSocket 通道与敏感词 / 举报反馈契约。

## 构建方式
- 前置条件：
  - 安装 Android SDK，并配置 `ANDROID_HOME` 或在 `client/local.properties` 中设置 `sdk.dir=/path/to/Android/Sdk`
  - 安装 JDK 11 及以上；当前工程已下调为兼容本机 Java 11 的工具链组合
- 构建命令：
  - `cd client`
  - `./gradlew assembleDebug`
- 当前验证结果：
  - `./gradlew -version` 已通过
  - `./gradlew assembleDebug` 已推进到 Android SDK 校验阶段，当前机器缺少 SDK 路径配置，因此未能完成最终 APK 构建
