# Design Handoff

- current stage: design
- From: designer
- To: leader
- approved inputs:
  - `artifacts/proposal.md`
  - `artifacts/specs/v1-core.md`
  - `artifacts/specs/v1-wireframes.md`
  - `artifacts/specs/v1-android-delivery.md`
- changed artifacts:
  - `artifacts/design.md`
  - `artifacts/react_native_mockups/index.html`
  - `artifacts/react_native_mockups/styles.css`
  - `runs/20260422-design-revision-2-progress.md`
  - `handoffs/design-ready.md`
- blocked items:
  - 无阻塞项
- next owner: leader
- entry conditions for the next owner:
  - 审阅 `artifacts/design.md` 中第二轮新增的对比度、色值、短状态表达与留白规则
  - 打开 `artifacts/react_native_mockups/index.html` 评审六个核心页面 revision 2 稿
  - 确认首页与航班页中 `模拟航线`、`实时修正`、`离线回退` 已通过标签、caption、来源 chip 建立清晰区分
  - 确认去除长说明后，状态表达仍然不误导
  - 确认 390 x 844 与 360 x 800 设备上的留白、卡片层次与触控尺寸可接受
- execution evidence or related run ids:
  - `runs/20260422-design-revision-2-run.md`
  - `runs/20260422-design-revision-2-progress.md`
- explicit declaration:
  - 本次产物为 design 阶段 revision 2，仅用于设计评审与交付对齐，不代表实现完成

## 本轮 revision 变化点
- 整体背景继续压灰，主背景更新为 `#ECEEF1`，同时将主卡片提高到接近纯白的 `#FFFFFF`，显著拉开页面层次。
- 统一收紧冗长说明文案，首页、航班页、统计页等涉及状态判断的区域改为 `状态标签 + 短 caption + 来源/时间 chip` 表达。
- 六个核心页面范围保持不变，但卡片内边距、模块间距、底部操作区安全距离和列表行高度都进一步增加。
- 首页 hero card、航班页 mode banner、统计页缓存状态、相册页本机处理提示、聊天室弱网提示、我的页设置状态，均已改为更轻、更国际化的短状态形式。
- mockup 中的背景、卡片、浮层、chip 与按钮明度关系已重新整理，减少同色阶糊成一片的问题。

## 待评审点
- 新的灰背景与白卡片对比是否已经足够明显，同时仍保持高级感而非生硬割裂。
- 用短状态标签替代长说明后，用户是否仍能准确理解“这不是实时导航”。
- 首页与航班页的模式表达是否足够国际化、轻量且稳定，不会在不同页面口径漂移。
- 统计页、相册页、聊天室页在留白增加后，是否仍保持信息效率，不显得过空。
- 我的页的状态值缩短后，是否仍满足设置理解成本与品牌调性要求。
