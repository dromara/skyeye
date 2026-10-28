# SkyEye 五块 AI 能力落地计划

> 用途：对照产品愿景（智能进销存 / 智能办公 / 智能制造 / 智能审批 / 智能商城），结合现有 AI 底座，跟踪落地进度。  
> 维护约定：每完成一项，将 `- [ ]` 改为 `- [x]`，并在「进度备注」补一行日期与说明。  
> 创建日期：2026-10-18  
> 关联前端：`skyeyeCloud_Vue`、`skyeyeCloud`、`skyeyeShop`

---

## 进度总览

| 阶段 | 说明 | 状态 |
|------|------|------|
| P0 | 技能包 + 表单开关 + 办公向导深化 + 知识库同步 + 风控约定 | ✅ 已完成 |
| P1 | 领域 Assist（进销存 / 审批只读 / 商城文案售后 / 办公拟稿纪要） | 🟡 进行中（填单类与审批、文案、售后、待办清理、库存预警、对账摘要、打印解读已落地；纪要未做） |
| P2 | 深领域（补货预测 / 排产根因 / 一句话配流程 / 多端上架） | 🟡 进行中（采购补货已验证；维保列表暂缓） |

**状态图例**：⬜ 未开始　🟡 进行中　✅ 已完成　⏸ 暂缓

---

## 1. 现有 AI 架构（已落地，勿重复造轮子）

```text
┌─────────────────────────────────────────────────────────────────┐
│ 前端入口                                                         │
│  PC：AiGuideDrawer / SkDsFormAiAssist（表单+布局）                 │
│      / SkFlowApproveAiAssist / SkShopGoodsCopyAiAssist           │
│      / SkShopAfterSaleSummaryAiAssist                            │
│  移动：pages/aiChat、sk-ds-form-ai-assist、sk-flow-approve-ai-assist │
│  流式：useAiStream / dsFormAiStream + WebSocket（按 bizType）      │
└────────────────────────────┬────────────────────────────────────┘
                             │
              表单/布局：aiGenerateDsFormAssist（userauth）
              审批/文案/售后：syncChatCompletion（adm-ai，技能提示词即人设）
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│ 编排层 skyeye-userauth（com.skyeye.ai.*）                          │
│  PlatformAiGuideService           bizType=chat                   │
│  PlatformDsFormAiDraftService     bizType=dsFormAssist           │
│                                   （表单 fieldValues + 布局 layoutPatches）│
│  PlatformAiSkillPromptBuilder / PlatformAiChatHelper             │
│  审批 / 商品文案 / 售后：不再单独建 Draft 服务                       │
└────────────────────────────┬────────────────────────────────────┘
                             │ Feign IAiChatRest / IAiSkillRest
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│ 底座 adm-ai                                                      │
│  Chat + 多模型工厂 + 知识库 RAG + Role + ApiKey                    │
│  Agent 工具（方案C：豆包 tools 循环）+ AiSkill + WebSocket + Token  │
│  syncChatCompletion：skill.prompt 优先于角色 prompt               │
└─────────────────────────────────────────────────────────────────┘
```

> **方案 C（Agent 业务工具）**：详见仓库根目录 [`AI-Agent方案C-业务工具.md`](./AI-Agent方案C-业务工具.md)。豆包闲聊不再预检索/工具检索塞知识库片段；按需调用 `knowledge_doc_stats` / `biz_*_board` / `biz_list`。知识库问答待旗舰版官方检索或其它方案。

### 1.1 已落地能力清单

- [x] 多模型聊天底座（通义 / 豆包 / 讯飞 / 千帆等）
- [x] AI 角色 + 绑定知识库 RAG
- [x] 豆包 Agent 工具底座（C0 + C1 `biz_board` + C2 `biz_list` 配置驱动 + 平台指引配置；2026-10-24 联调通过）
- [x] API Key / 平台配置（含 OA AI 角色绑定）
- [x] AI 技能积木 / 套件 / 分类（`useScene`：聊天=1 / 表单=2 / 仅编码=3）
- [x] 办公 AI 向导（找菜单、办事、知识库问答）
- [x] 表单填写 AI（回填 `fieldValues`，业务对象开关 `aiFormAssist`）
- [x] 布局设计器 AI（`layoutPatches`，与表单填写共用 `bizType=dsFormAssist`；人设在技能提示词）
- [x] 技能辅助面板 `assistConfig`（标题 / 按钮 / 占位 / 快捷问法 / 免责声明 / 思考中提示）；PC 与移动表单 AI 都读这份配置
- [x] 场景 Assist：审批 `flowApproveAssist`、商品文案 `shopGoodsCopyDraft`、售后 `shopAfterSaleSummary`（技能提示词 + `syncChatCompletion`，前端解析 JSON）
- [x] 流式通道 WebSocket（按 `bizType` 推送）
- [x] AI 知识库 + DB 同步
- [x] 租户 Token 计费 / 停用
- [x] 领域 Draft 范例（`skyeye-auto` 的 demand/bug/case 等，可仿）

### 1.2 关键路径速查

| 层级 | 路径 |
|------|------|
| 底座 | `skyeye-adm/adm-ai/`（chat / skill / knowledge / role / key / websocket） |
| 编排 | `skyeye-promote/skyeye-userauth/src/main/java/com/skyeye/ai/` |
| Feign | `.../rest/ai/IAiChatRest.java`、`IAiSkillRest.java` |
| PC 向导 | `skyeyeCloud_Vue/src/views/layout/components/AiGuideDrawer.vue` |
| PC 表单 AI | `.../components/SkDsFormAiAssist/`、`composables/useDsFormAi.js` |
| PC 布局 AI | `views/dsForm/design/baseLayout.vue` 使用 `SkDsFormAiAssist`（`resultMode=layoutPatches`） |
| PC 审批 AI | `.../components/SkFlowApproveAiAssist/` → `views/activiti/myActiviti/approve.vue` |
| PC 商品文案 | `SkShopGoodsCopyAiAssist` → groundShop/basic；ShopPC `merchant/goods.vue` |
| PC 售后摘要 | `SkShopAfterSaleSummaryAiAssist` → storeDesign afterSale；ShopPC OrderAfterSaleModal |
| PC 技能管理 | `.../views/system/business/components/ai-skill/` |
| 移动聊天 | `skyeyeCloud/pages/aiChat/index.vue` |
| 移动表单 AI | `skyeyeCloud/components/sk-ds-form-ai-assist/`（读技能 `assistConfig`） |
| 移动审批 AI | `skyeyeCloud/components/sk-flow-approve-ai-assist/` → `page-LAYOUT/dsForm/basicInformation.vue` |
| Draft 范例 | `skyeye-auto/auto-pro/.../*AiDraft*` |

### 1.3 关键抽象（扩展必须对齐）

1. **AiSkill**：`appId` + `applyServiceClassName` 绑定业务对象；积木编译为 `instruction`
2. **bizType**：流式会话类型。`chat`；`dsFormAssist`（表单回填与布局补丁共用）；场景类 `flowApproveAssist` / `shopGoodsCopyDraft` / `shopAfterSaleSummary`。不再使用 `dsFormLayoutAssist`
3. **人设**：AI 技能的「技能提示词」作为 system prompt。表单/布局仍由 `PlatformAiSkillPromptBuilder` 把字段或画布上下文和 JSON 约定拼进用户消息。审批/文案/售后不经 PromptBuilder，页面自己拼上下文，调用 `syncChatCompletion`。商城与办公共用平台 `oaRoleId`，不另建商城角色
4. **结果**：表单回填 `fieldValues`；布局写 `layoutPatches`；场景类前端解析 JSON 后展示建议。落库必须走原有业务校验接口
5. **风控默认**：金额、库存扣减、审批通过/驳回 **禁止 AI 直接落库**，只给建议 + 人工确认

### 1.4 办公 OA AI 角色提示词（粘贴用）

> **用法**：打开「AI角色」→ 编辑办公助手角色的 **角色定位与能力提示词（prompt）** → 整段替换为下文 → 保存。  
> **注意**：库里是自定义文案，**不会**自动同步 `Role.DEFAULT_PROMPT`；Agent 工具增减后请改本节并回贴角色。  
> **关联**：方案 C 工具清单见 [`AI-Agent方案C-业务工具.md`](./AI-Agent方案C-业务工具.md)。  
> **维护日期**：2026-10-21（已去掉 `knowledge_search`；已接 ERP 待出/待入 count）

````markdown
# 角色
你是 SkyEye 云（Skyeye云）的系统级办公 AI。SkyEye 云是企业级「AI + 零代码」快速开发平台，面向智能制造产业供应链，一体化覆盖客户、项目、生产、仓储、办公与人事。
用户可能在 PC 或移动端提问。帮他们把事情办成：讲清怎么做、填什么、卡在哪、下一步是什么。不要只丢一个菜单名。
用简洁、友好的中文直接回答。先给结论和可执行步骤。

## 能力范围
后续功能都落在这些域内，不要拒答「不是办公的事」：
- 客户与项目：CRM、项目管理（PM）
- 供应链与生产：ERP、MES、仓储进销存、采购销售、车间任务/领料/工序
- 行政与人事：OA、ADM、EHR、多班次考勤、薪资、招聘、请假/加班/出差等
- 经营与服务：商城、财务、云售后、论坛、问卷
- 平台能力：零代码表单/页面、工作流、报表设计、SaaS 多租户、知识库管理、AI角色、AI配置
用户问到上列任一模块，都按本平台助手处理；问到平台外产品或未提供的能力时，说明没有依据，不要编造。

## 阶段一：意图分流
先判断用户要什么，再按场景回答：
- 办事：对应模块的业务流程、必填项、状态流转、审批节点、常见卡点。
- 当前页：用户在某页提问时优先结合本页。讲用途、关键字段、可做操作、易错点；已在目标页就不要再让他去找菜单。
- 找入口：仅当用户明确问「在哪」「打开某功能」，或办完这件事必须进某菜单时，才给菜单与跳转。不要把所有问题默认理解成找菜单。
- 业务数据：解释单据、表、字段、关联；实时数量必须用工具，不要猜。
- 零代码/工作流/报表：讲如何用平台能力配置或使用，不代替用户保存设计、不声称已发布流程。
- AI 管理：知识库与文件/库表同步、AI角色与提示词、AI配置启用与绑定，如何设置和排查。
- 办公协助：归纳要点、对比方案、起草说明、列出检查清单。
- 技能：若对话中给出了「已启用技能」说明书，该类业务严格按说明书走。没有命中技能时，用本提示词 + 当前页 + 工具结果作答。
信息不完整时，先给可执行建议，并明确还缺什么（模块、单据状态、权限、所在页面、租户/组织等）。

## 阶段二：依据优先级
- 优先依据：已启用技能说明书 > 当前页面 > 工具查询结果 > 通用建议。
- 当前**没有**文档向量检索工具：不要假装已检索知识库；也不要声称「知识库检索服务不可用」除非工具明确报错。
- 制度/操作说明若对话里未给出资料：给通用建议并标明「非本系统已确认事实」，并建议用户打开对应菜单或补充技能/资料。
- 库存/订单/待办/待出库/待入库等**实时数量**：必须以工具查询结果为准。
- 复杂问题拆成：怎么办理 → 填什么 → 注意什么 → 下一步。

## 阶段三：打开页面（辅助能力）
- 仅当用户明确要去某功能，或把事办完必须进入某菜单时，才给跳转（menuId）。
- menuId 必须来自对话上下文或技能说明书中已给出的真实菜单；没有就说明可能没权限、菜单名不同或资料未提供，**不要编造 id**，可改用文字路径引导（如「系统管理 → APP菜单管理」）。
- 不需要打开页面时不要给 menuId。

## 回答原则
- 先给结论，再给必要步骤与注意点；避免空话和堆砌模块名。
- 以技能说明书、当前页面、工具结果为准；无依据不编造。
- 步骤可执行：按钮名称、必填项、先后顺序说清楚；资料有区分时说明 PC 与移动端差异。
- 多租户/权限/审批：按当前用户能做的来说，不要假设他是管理员。
- 涉及薪酬、客户、财务、生产数据时提醒合规，不输出他人隐私。
- 不代替用户提交单据、发布流程、同步知识库或做最终审批，可给风险提示与核对建议。

## Agent 工具（有工具可用时）
- 每一轮须选工具：查数用业务/知识库工具；闲聊解释用 **direct_reply**。由模型按工具说明路由，不靠关键词硬匹配。
- 查数须先拿工具结果再回答；禁止臆测；禁止未查询就说「正在查询」。
- 笔数看板：**biz_board**（domain=oa/erp/mall；商城可选 storeId）。
- 明细列表：**biz_list**（最终 JSON 填 list）：OA；ERP domain=erp（depotOut/depotIn，对齐 E01/E03）；商城 domain=mall（可选 storeId）。文档数：**knowledge_doc_stats**。
- 不要调用 knowledge_search（已下线）。无写操作工具时不要声称已提交/审批/过账；引导到业务页。
- 工具 ok=false 时如实说明，并建议到对应业务页面查看。

## 提醒
- 你是问答助手，不是自动落库机器人：未确认前不要声称已提交、已审批、已创建单据或已改配置。
- 资料缺失、过期或冲突时明确指出，并建议找对应模块负责人或补充技能/资料。
- 高频问题可提醒沉淀成技能或业务说明，而不是把长流程写进本提示词。
````

---

## 2. 产品愿景 ↔ 落地映射

### 2.1 AI 智能进销存（SkyEye ERP）

| 愿景能力 | 落地方式 | 阶段 | 状态 |
|----------|----------|------|------|
| 智能单据打印解读 | `erpPrintRead`，挂单据打印预览；技能绑 `PrintTemplateServiceImpl`，只指出模板缺项，不改模板、不代打印 | P1 | ✅ |
| 库存预警解读 | `erpStockWarningAssist`，挂库存预警列表；技能绑 `MaterialNormsStockServiceImpl`，只解读当前页，不改库存 | P1 | ✅ |
| 采购补货建议 | 并进库存预警同一条技能。上下文增加本月销量；无提前期，不编天数、不生成采购单 | P2 | ✅ |
| 对账摘要 | `erpReconcileSummary`，挂往来账龄列表和对账单弹窗；技能绑 `CounterpartAgingServiceImpl`，只解读当前页，不改账 | P1 | ✅ |
| 设备维保预测 | 列表不做。和新建任务的表单技能都绑设备保养任务，启用会互关。新建任务继续用表单 AI | P2 | ⏸ |
| 一句话采购 / 入库 / 出库 | 自然语言 → `dsFormAssist` 回填对应单据 | P0～P1 | ✅ |

### 2.2 AI 智能办公（SkyEye OA）

| 愿景能力 | 落地方式 | 阶段 | 状态 |
|----------|----------|------|------|
| 一句话拟稿公文/报告 | 复用 `dsFormAssist` + 表单技能（发文/收文），不新建 `oaDocDraft` | P1 | ✅ |
| 自动会议纪要 | 依赖转写输入 → 纪要 draft | P1 | ⬜ |
| 知识库问答 | 复用现有 RAG + 向导（深化技能包） | P0 | ✅ |
| 智能待办清理建议 | 聊天技能（只勾聊天），绑定日程管理 `ScheduleDayServiceImpl`；用已有 `biz_board` / `biz_list` 出建议清单，不自动删 | P1 | ✅ |
| 聊天发起办事 | 向导找菜单 + 表单 AI 填单 | P0 | ✅ |

### 2.3 AI 智能制造（SkyEye MES）

| 愿景能力 | 落地方式 | 阶段 | 状态 |
|----------|----------|------|------|
| 智能排产建议 | 只读建议 draft（不直接改计划） | P2 | ⬜ |
| 异常根因分析 | 异常上下文 → 分析 Assist | P2 | ⬜ |
| 质检数据备注 / 工序指导 | 复用 `dsFormAssist` + 表单技能（质检单、工序管理） | P1 | ✅ |
| 一句话报工 | 自然语言 → 工序验收单回填（`MachinProcedureAcceptServiceImpl`） | P1 | ✅ |

### 2.4 AI 智能审批（SkyEye Workflow）

| 愿景能力 | 落地方式 | 阶段 | 状态 |
|----------|----------|------|------|
| 一句话流程配置 | 自然语言 → 流程模型草案（高风险后置） | P2 | ⬜ |
| 审批意见预判 / 风险点提醒 | 审批详情页只读 Assist | P1 | ✅ |
| 智能驳回建议 | 只读建议文案，人工点驳回 | P1 | ✅ |
| 一句话提交并自动流转 | **不做自动通过**；可辅助填申请单 | P0～P1 | ⬜ |

### 2.5 AI 智能商城（SkyEye 商城）

| 愿景能力 | 落地方式 | 阶段 | 状态 |
|----------|----------|------|------|
| AI 生成商品文案 | 商品编辑页 draft（`shopGoodsCopyDraft`） | P1 | ✅ |
| 售后工单摘要 | 售后详情只读摘要 | P1 | ✅ |
| 评价销售分析 / 补货建议 | 分析型 Assist | P2 | ⬜ |
| 一键上架多端售卖 | 表单 AI + 渠道字段回填 | P2 | ⬜ |

---

## 3. 分期任务明细（打勾跟踪）

### P0 — 高复用、低改造（建议首批）

#### P0-1 业务对象技能包与表单开关

- [x] 梳理 ERP 高频单据业务对象清单（采购、入库、出库；首批）
  - 采购：`com.skyeye.purchase.entity.PurchaseOrder`（及采购申请/到货可后续扩）
  - 入库：`DepotPut` / `PurchasePut`
  - 出库：`DepotOut`
- [x] 梳理 OA 高频单据业务对象清单（请假、报销、公文）
  - 公文发文：`com.skyeye.eve.gw.service.impl.GwSendDocumentServiceImpl`（`com.wzq.skyeye.adm`）
  - 公文收文：`com.skyeye.eve.gw.service.impl.GwReceiveDocumentServiceImpl`
  - 请假：`com.skyeye.leave.service.impl.LeaveServiceImpl`
  - 报销：`com.skyeye.reimbursement.service.impl.ReimbursementServiceImpl`
- [x] 为上述对象配置 AiSkill（`useScene` 表单）← **公文发文/收文、采购、入库、出库、请假、报销已在后台启用（2026-10-25）**
- [x] 开启对应业务对象 `aiFormAssist` ← **未单独关闭时默认开启；上述单据写页已挂表单 AI**
- [x] PC 写页确认已挂 `SkDsFormAiAssist`（`writeShow.vue`，受 `aiFormAssist` 控制）
- [x] 移动写页确认已挂 `sk-ds-form-ai-assist`（`page-LAYOUT/dsForm/write.vue`）
- [x] ERP 表单快捷问法：采购/入出库按 className 分流（`buildFormAiQuickPrompts`）

#### P0-2 办公向导深化

- [x] 补齐办事/找菜单相关技能与知识库条目（2026-10-27，已有实现，不再另做）
- [x] 验证 PC `AiGuideDrawer` 主路径（Agent 工具 + list 卡片已通）
- [x] 验证移动 `aiChat` 主路径（与 PC 同一套接口；选技能只列聊天场景）（2026-10-27）
- [x] 确认平台已绑定 OA AI 角色（`oaRoleId`；PC 向导与移动 AI 助手可用）（2026-10-27）

#### P0-3 知识库同步

- [x] 进销存主数据/操作说明同步进 AI 知识库（2026-10-27，AI 知识库自动同步，不再另做）
- [x] 办公制度/流程说明同步进 AI 知识库（2026-10-27，AI 知识库自动同步，不再另做）
- [x] （可选）制造/商城基础说明入库（2026-10-27，随 AI 知识库自动同步）

#### P0-4 风控与产品约定（文档级完成即打勾）

- [x] 约定：金额字段 AI 回填后必须人工确认（表单 prompt 强制提醒；前端文案）
- [x] 约定：库存扣减/出入库过账禁止 AI 直接调用业务接口（Agent 不提供写工具）
- [x] 约定：审批通过/驳回禁止 AI 自动执行（同上）
- [x] 前端对「建议类」结果统一展示「仅供参考」提示（表单 AI / 办公 AI 抽屉）

**P0 进度备注**

| 日期 | 说明 |
|------|------|
| 2026-10-22 | P0-4 风控约定落地（prompt + 仅供参考）；确认 PC/移动表单 AI 挂载；ERP 快捷问法 |
| 2026-10-25 | OA 单据类名整理；公文发文/收文技能已在后台启用 |
| 2026-10-25 | 采购、入库、出库、请假、报销的表单技能已配置 |
| 2026-10-27 | 移动 `aiChat` 可用，与 PC 向导同一套接口；选技能只列聊天场景。平台已绑定办公 AI 角色 |
| 2026-10-27 | 办事/找菜单技能与知识库条目已有实现，P0-2 不再另做 |
| 2026-10-27 | P0-3：AI 知识库自动同步业务数据，进销存/办公/制造/商城不再另做同步 |

---

### P1 — 领域 Assist

#### P1-A 智能进销存

- [x] 一句话入库：自然语言 → 入库单表单回填（复用 `dsFormAssist` + 表单技能）
- [x] 出库/采购单据一句话填单（同模式，技能已配）
- [x] 库存预警解读 Assist（只读；PC / 移动预警列表，`erpStockWarningAssist`，技能绑商品规格库存）
- [x] 对账摘要 Assist（只读；PC 账龄列表 + 对账单弹窗、移动账龄列表，`erpReconcileSummary`，技能绑往来账龄）
- [x] 单据打印解读 Assist（只读；PC 打印预览，`erpPrintRead`，技能绑打印模板）

#### P1-B 智能审批（只读）

- [x] 审批详情页挂载「AI 摘要」（`approve.vue` → `SkFlowApproveAiAssist`）
- [x] 「风险点提醒」
- [x] 「建议通过/驳回意见」文案（写入意见框需用户确认；可一键填入，不代提交）
- [x] `bizType=flowApproveAssist`：走 `syncChatCompletion` + 技能提示词（已删除 `PlatformFlowApproveAiDraftService`）
- [x] 移动待办审批挂载 `sk-flow-approve-ai-assist`（`basicInformation.vue`）
- [x] 明确不调用「同意/驳回」业务 API（仅建议 + 人工提交）

**P1-B 进度备注**

| 日期 | 说明 |
|------|------|
| 2026-10-24 | 审批详情只读 Assist 上线：PC `approve.vue` |
| 2026-10-25 | 去掉独立审批 Draft 服务，改为技能提示词 + `syncChatCompletion`；移动待办同步挂载 |

#### P1-C 智能商城

- [x] 商品文案生成 Assist（`shopGoodsCopyDraft`；Cloud_Vue 上架商城 + ShopPC 自建商品）
- [x] 售后工单摘要 Assist（`shopAfterSaleSummary`；门店 afterSale + ShopPC OrderAfterSaleModal）
- [x] 商城与办公共用平台 OA AI 角色（`oaRoleId`）。不另建商城角色或技能套件；文案、售后等人设写在技能提示词（2026-10-27）
- [x] skyeyeShopPC / groundShop 入口挂载点确认

**P1-C 进度备注**

| 日期 | 说明 |
|------|------|
| 2026-10-24 | `PlatformShopGoodsCopyAi*` + `SkShopGoodsCopyAiAssist`；ShopPC 接通 `$aiWs` |
| 2026-10-25 | `shopAfterSaleSummary`：只读摘要/风险/建议；可填入拒绝原因，不代操作 |
| 2026-10-25 | 文案/售后独立 Draft 服务删除，人设改放技能提示词；前端仍按原 JSON 解析 |
| 2026-10-27 | 商城与办公共用 `oaRoleId`，不再单独做商城 AI 角色 |

#### P1-D 智能办公增强

- [x] 公文/报告拟稿：复用写页 `dsFormAssist` + 表单技能（发文拟稿、收文登记）。不新建 `oaDocDraft`
- [ ] 会议纪要 draft（输入：转写文本/录音结果）
- [x] 待办清理「建议列表」（聊天技能，绑定日程管理；只出清单，不自动删除）

#### P1-E 智能制造（浅层）

- [x] 报工单一句话填单：工序验收（`com.skyeye.machinprocedure.service.impl.MachinProcedureAcceptServiceImpl`）表单技能已配
- [x] 质检备注：质检单（`com.skyeye.inspection.service.impl.QualityInspectionServiceImpl`）表单技能已配
- [x] 工序指导：工序管理（`com.skyeye.procedure.service.impl.WorkProcedureServiceImpl`）表单技能已配，写入工序内容

**P1 进度备注**

| 日期 | 说明 |
|------|------|
| 2026-10-25 | 公文拟稿走现有表单 AI；移动表单面板改为读取技能 `assistConfig`，与 PC 一致 |
| 2026-10-25 | 采购、入库、出库、请假、报销表单技能已配，一句话填单走现有写页 |
| 2026-10-25 | 工序验收、质检单、工序管理表单技能已配 |
| 2026-10-25 | 待办清理：聊天技能已配，绑定日程管理，不新建接口 |
| 2026-10-26 | 库存预警：PC / 移动列表挂只读分析，技能绑 `MaterialNormsStockServiceImpl`，不占用商品写页 |
| 2026-10-27 | 对账摘要：PC 往来账龄列表和对账单弹窗、移动账龄列表挂只读分析，技能绑 `CounterpartAgingServiceImpl`，不改账 |
| 2026-10-27 | 打印解读：PC 单据打印预览挂只读分析，技能绑 `PrintTemplateServiceImpl`，不改模板、不代打印 |

---

### P2 — 深领域

#### P2-A 进销存深水区

- [x] 采购补货建议（并进库存预警技能，上下文带本月销量；无提前期，不生成采购单）（2026-10-28 已验证）
- [ ] 设备维保预测 ⏸ 列表不做（2026-10-28）。和新建任务表单技能互斥，新建任务继续用表单 AI
- [ ] 价税/库存约束写入 Prompt + 服务端强校验回写

#### P2-B 制造深水区

- [ ] 智能排产建议
- [ ] 异常根因分析
- [ ] 报工全链路可视（与 MES 看板联动）

#### P2-C 审批深水区

- [ ] 一句话流程配置（生成流程草案，人工发布）
- [ ] 与 Flowable 模型设计器对接评审

#### P2-D 商城深水区

- [ ] 评价销售分析
- [ ] 补货建议
- [ ] 一键多端上架字段适配

**P2 进度备注**

| 日期 | 说明 |
|------|------|
| 2026-10-27 | 采购补货不另挂面板。库存预警上下文增加本月销量，沿用原技能。无提前期，不生成采购单 |
| 2026-10-28 | 采购补货提示词已改并验证通过 |
| 2026-10-28 | 设备维保列表建议撤掉。和新建任务的表单技能都绑设备保养任务，启用会互关 |

---

## 4. 技术落地约定

### 4.1 新建领域 Assist 标准步骤

表单回填、布局补丁继续走 `PlatformDsFormAiDraftService`。只读分析、文案生成优先：**技能提示词写人设和 JSON 约定，页面拼上下文，直接调 `syncChatCompletion`**。不要再为每个场景新建 Controller。

仍要新页面时：

1. 定 `bizType`（与前端 `useAiStream` 监听一致）
2. 技能使用位置选「表单AI辅助」，提示词要求只输出一个 JSON 对象
3. 页面用已有 `SkDsFormAiAssist`（`panelPreset=scene`）或薄封装，只负责拼上下文和结果区
4. 落库必须走原有业务校验接口；金额、库存、审批不代提交

### 4.2 技能配置约定

| useScene | 用途 |
|----------|------|
| 1 聊天 | 向导、办事问答 |
| 2 表单 | 写页 AI 辅助填单 |
| 3 仅编码 | 不进选用列表，业务按技能编码调用。布局设计器已改走「表单AI辅助」 |

- 绑定字段：`appId` + `applyServiceClassName`
- 同业务对象同 UI 场景注意技能互斥（启用策略以现网为准）

### 4.3 建议新增 bizType 命名（草案，落地时以代码常量为准）

| bizType | 用途 | 阶段 |
|---------|------|------|
| `dsFormAssist` | 表单回填 `fieldValues`、布局 `layoutPatches` | 已落地 |
| `flowApproveAssist` | 审批摘要/风险/建议意见（技能提示词，无独立 Draft） | 已落地 |
| `shopGoodsCopyDraft` | 商品文案（同上） | 已落地 |
| `shopAfterSaleSummary` | 售后摘要（同上） | 已落地 |
| `erpStockWarningAssist` | 库存预警解读（技能提示词，无独立 Draft） | 已落地 |
| `erpReconcileSummary` | 对账摘要（技能提示词，无独立 Draft） | 已落地 |
| `erpPrintRead` | 单据打印解读（技能提示词，无独立 Draft） | 已落地 |
| `oaMeetingMinutes` | 会议纪要 | P1 未做 |
| `mesScheduleSuggest` | 排产建议 | P2 |
| `erpReplenishSuggest` | 不单列。补货建议并进 `erpStockWarningAssist` | 已验证（2026-10-28） |
| `erpMaintainSuggest` | 不单列。列表与新建任务表单技能互斥，列表不做 | 暂缓（2026-10-28） |

公文拟稿不单列 bizType，走 `dsFormAssist`。待办清理不单列 bizType，走聊天技能和已有 `biz_board` / `biz_list`。`oaDocDraft`、`dsFormLayoutAssist` 不新建。

### 4.4 前端挂载建议

| 场景 | 组件/入口 |
|------|-----------|
| 单据填写 | 已有 `SkDsFormAiAssist` / 移动同款 |
| 只读分析/摘要 | 审批：`SkFlowApproveAiAssist`；其它详情可仿 |
| 办事找功能 | `AiGuideDrawer` / `aiChat` |
| 商家端 | `skyeyeShop/pages/my/merchant/*` 按需挂 |
| 门店工作台 | `skyeyeCloud/page-SHOP/storeDesign/*` 按需挂 |

---

## 5. 首批可立即开工清单（推荐顺序）

完成一项勾一项：

- [x] **ERP**：采购 / 入库 / 出库 — 表单技能已配，走现有写页（2026-10-25）
- [x] **OA**：请假、报销 — 表单技能已配（2026-10-25）
- [x] **审批**：详情页「AI 摘要 + 建议意见」（`flowApproveAssist`；PC 2026-10-24，移动 2026-10-25）
- [x] **商城**：商品文案（2026-10-24）+ 售后摘要（2026-10-25）
- [x] **公文**：发文拟稿 / 收文登记，复用表单 AI（2026-10-25）

下一批（仍不新写填单接口；有现成单据页的继续只配技能）：

- [x] **制造**：工序验收报工、质检备注、工序指导 — 表单技能已配（2026-10-25）

下一批：

- [x] **办公**：待办清理建议（聊天技能，绑定日程管理，只出清单，不自动删）（2026-10-25）

下一批：

- [x] **进销存只读**：库存预警解读（PC / 移动预警列表，技能绑商品规格库存）（2026-10-26）

下一批：

- [x] **进销存只读**：对账摘要（往来账龄列表 + PC 对账单弹窗，技能绑往来账龄）（2026-10-27）
- [ ] **办公**：会议纪要（现只有会议室预定，没有可贴转写或正文的纪要页，先不要配空技能）

下一批：

- [x] **进销存只读**：单据打印解读（PC 打印预览，技能绑打印模板）（2026-10-27）

**首批进度备注**

| 日期 | 项 | 说明 |
|------|----|------|
| 2026-10-22 | ERP | 代码侧：写页挂载确认、ERP 快捷问法、仅供参考；技能内容需后台配置后验证一句话填单 |
| 2026-10-24 | 审批 | PC `approve.vue` 挂载 `SkFlowApproveAiAssist` |
| 2026-10-24 | 商城 | `shopGoodsCopyDraft`：groundShop + ShopPC 自建商品；ShopPC `$aiWs` |
| 2026-10-25 | 商城 | 售后摘要；文案/售后/审批改为技能提示词，独立 Draft 服务删除 |
| 2026-10-25 | 公文 | 发文/收文技能 + 现有表单 AI；移动面板对齐 PC 的 `assistConfig` |
| 2026-10-25 | 审批 | 移动待办 `basicInformation.vue` 挂审批助手 |
| 2026-10-25 | ERP / OA | 采购、入库、出库、请假、报销表单技能已配置 |
| 2026-10-25 | 制造 | 工序验收、质检单、工序管理表单技能已配置 |
| 2026-10-25 | 办公 | 待办清理聊天技能已配置，绑定日程管理，不自动删除 |
| 2026-10-26 | 进销存 | 库存预警只读分析已挂 PC / 移动列表，技能绑商品规格库存 |
| 2026-10-27 | 进销存 | 对账摘要只读分析已挂 PC 往来账龄和对账单、移动账龄列表，技能绑往来账龄 |
| 2026-10-27 | 进销存 | 打印解读只读分析已挂 PC 单据打印预览，技能绑打印模板 |

---

## 6. 风险与依赖

| 风险 | 应对 |
|------|------|
| 幻觉导致金额/数量错误 | 只建议；提交走原校验；关键字段二次确认 |
| 审批自动通过 | 产品禁止；接口层不提供 AI 调同意/驳回 |
| 未绑定 OA AI 角色 | 向导不可用；上线检查平台设置 |
| Token 耗尽 | 租户用量提示；降级文案 |
| 商城与办公共用一个角色 | 人设差异写在技能提示词；角色只负责选启用的 AI 配置 |
| 知识库内容 | RAG 用 AI 知识库；业务数据由知识库自动同步，不再另做同步任务 |

---

## 7. 变更记录

| 日期 | 变更 |
|------|------|
| 2026-10-18 | 初稿：五块愿景映射 + P0/P1/P2 任务拆解 + 打勾跟踪约定 |
| 2026-10-20 | 选定方案 C；挂接 `AI-Agent方案C-业务工具.md`；C0 Agent 底座落地 |
| 2026-10-21 | 增补 §1.4 办公 OA 角色完整提示词；C1 ERP 待出/待入工具 |
| 2026-10-22 | C1 收敛看板工具；C2 `biz_list` OA + mall 列表卡片 |
| 2026-10-22 | 切入落地计划 P0：风控文案/prompt、表单挂载确认、ERP 快捷问法 |
| 2026-10-23 | Agent `biz_list` 改为 JSON 配置驱动，新列表优先只改配置 |
| 2026-10-23 | `biz_list` 路由：仅 `appId+serviceClassName`（对齐 IDataServiceImpl）；单动态 Feign |
| 2026-10-23 | `ai-biz-list-endpoints.json` 增加 `docs` 字段说明；界面配置（平台信息）待做 |
| 2026-10-23 | 平台信息「AI业务列表」可配 biz_list endpoints；运行时优先平台配置，空则内置 JSON |
| 2026-10-24 | 看板收敛为单一 `biz_board`（配置驱动）；平台可配 bizBoardEndpoints；删 oa/erp/mall 三 Tool |
| 2026-10-24 | Agent 路由可配 `springApplicationName`；待出库 NPE 根因修复；平台「AI业务接入」表单化；方案 C C0～C2 联调通过 |
| 2026-10-24 | P1-B：审批只读 Assist（`flowApproveAssist` + `SkFlowApproveAiAssist` 挂 `approve.vue`） |
| 2026-10-24 | P1-C：商品文案草稿（`shopGoodsCopyDraft` + groundShop / ShopPC） |
| 2026-10-25 | P1-C：售后摘要（`shopAfterSaleSummary` + 门店/商家售后弹窗） |
| 2026-10-25 | 审批/文案/售后去掉独立 Draft 服务，人设改放技能提示词；布局与表单填写仍走 `dsFormAssist` |
| 2026-10-25 | P1-D：公文拟稿复用表单 AI（发文/收文技能），不新建 `oaDocDraft`；移动表单面板读取 `assistConfig`；移动待办挂审批助手 |
| 2026-10-25 | P0-1 / P1-A：采购、入库、出库、请假、报销表单技能已配，一句话填单不再单开接口 |
| 2026-10-25 | P1-E：工序验收报工、质检备注、工序指导改为表单技能，不新建接口 |
| 2026-10-25 | P1-D：待办清理改为聊天技能，绑定 `ScheduleDayServiceImpl`，复用 `biz_board` / `biz_list`，不自动删除 |
| 2026-10-26 | P1-A：库存预警解读挂 PC / 移动列表（`erpStockWarningAssist`），技能绑 `MaterialNormsStockServiceImpl`，不改库存、不占用商品写页 |
| 2026-10-27 | P1-A：对账摘要挂往来账龄（`erpReconcileSummary`），技能绑 `CounterpartAgingServiceImpl`，不改账、不过账 |
| 2026-10-27 | P1-A：单据打印解读挂打印预览（`erpPrintRead`），技能绑 `PrintTemplateServiceImpl`，不改模板、不代打印 |
| 2026-10-27 | P0-2：移动 AI 助手主路径可用；平台办公 AI 角色已绑定 |
| 2026-10-27 | P1-C：商城与办公共用 `oaRoleId`，不另建商城 AI 角色 |
| 2026-10-27 | P0-2：办事/找菜单技能与知识库条目已有实现，不再另做 |
| 2026-10-27 | P0-3：AI 知识库自动同步，知识库同步整块勾完；P0 收口 |
| 2026-10-27 | P2-A：采购补货并进库存预警同一条技能，上下文增加本月销量；不另挂面板、不生成采购单 |
| 2026-10-28 | P2-A：采购补货提示词验证通过 |
| 2026-10-28 | P2-A：设备维保列表建议撤掉，避免和新建任务表单技能互斥 |

---

## 8. 使用说明（给后续的自己）

1. 开工前先看 **§5 首批清单** 和 **§3 对应阶段**。  
2. 代码合入并验证后：勾选 `- [x]`，在对应「进度备注」写日期。  
3. 若某能力改为不做：将状态改为 ⏸，并在备注写原因。  
4. 新增 `bizType` / 技能编码时：同步更新 **§4.3**。  
5. 架构大变时：同步更新 **§1**，避免后人按过时路径开发。  
6. Agent 工具增减后：同步改 **§1.4** 提示词，并回贴到「AI角色」的 prompt。
