# SkyEye 五块 AI 能力落地计划

> 用途：对照产品愿景（智能进销存 / 智能办公 / 智能制造 / 智能审批 / 智能商城），结合现有 AI 底座，跟踪落地进度。  
> 维护约定：每完成一项，将 `- [ ]` 改为 `- [x]`，并在「进度备注」补一行日期与说明。  
> 创建日期：2026-10-18  
> 关联前端：`skyeyeCloud_Vue`、`skyeyeCloud`、`skyeyeShop`

---

## 进度总览

| 阶段 | 说明 | 状态 |
|------|------|------|
| P0 | 技能包 + 表单开关 + 办公向导深化 + 知识库同步 + 风控约定 | ⬜ 未开始 |
| P1 | 领域 Assist（进销存 / 审批只读 / 商城文案售后 / 办公拟稿纪要） | ⬜ 未开始 |
| P2 | 深领域（补货预测 / 排产根因 / 一句话配流程 / 多端上架） | ⬜ 未开始 |

**状态图例**：⬜ 未开始　🟡 进行中　✅ 已完成　⏸ 暂缓

---

## 1. 现有 AI 架构（已落地，勿重复造轮子）

```text
┌─────────────────────────────────────────────────────────────────┐
│ 前端入口                                                         │
│  PC：AiGuideDrawer / SkDsFormAiAssist / SkDsFormLayoutAiAssist   │
│  移动：pages/aiChat、sk-ds-form-ai-assist                         │
│  流式：useAiStream / dsFormAiStream + WebSocket                   │
└────────────────────────────┬────────────────────────────────────┘
                             │ aiGenerate* / syncChatCompletion
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│ 编排层 skyeye-userauth（com.skyeye.ai.*）                          │
│  PlatformAiGuideService           bizType=chat                   │
│  PlatformDsFormAiDraftService     bizType=dsFormAssist           │
│                                   bizType=dsFormLayoutAssist     │
│  PlatformAiSkillPromptBuilder / PlatformAiChatHelper             │
└────────────────────────────┬────────────────────────────────────┘
                             │ Feign IAiChatRest / IAiSkillRest
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│ 底座 adm-ai                                                      │
│  Chat + 多模型工厂 + 知识库 RAG + Role + ApiKey                    │
│  Agent 工具（方案C：豆包 tools 循环）+ AiSkill + WebSocket + Token  │
└─────────────────────────────────────────────────────────────────┘
```

> **方案 C（Agent 业务工具）**：详见仓库根目录 [`AI-Agent方案C-业务工具.md`](./AI-Agent方案C-业务工具.md)。豆包闲聊不再预检索/工具检索塞知识库片段；按需调用 `knowledge_doc_stats` / `biz_count_*`。知识库问答待旗舰版官方检索或其它方案。

### 1.1 已落地能力清单

- [x] 多模型聊天底座（通义 / 豆包 / 讯飞 / 千帆等）
- [x] AI 角色 + 绑定知识库 RAG
- [x] 豆包 Agent 工具底座（C0：`AiAgentTool` + tools 循环；业务 count 待 C1）
- [x] API Key / 平台配置（含 OA AI 角色绑定）
- [x] AI 技能积木 / 套件 / 分类（`useScene`：聊天=1 / 表单=2 / 仅编码=3）
- [x] 办公 AI 向导（找菜单、办事、知识库问答）
- [x] 表单填写 AI（回填 `fieldValues`，业务对象开关 `aiFormAssist`）
- [x] 布局设计器 AI（`layoutPatches`，按技能编码调用）
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
| PC 布局 AI | `.../components/SkDsFormLayoutAiAssist/` |
| PC 技能管理 | `.../views/system/business/components/ai-skill/` |
| 移动聊天 | `skyeyeCloud/pages/aiChat/index.vue` |
| 移动表单 AI | `skyeyeCloud/components/sk-ds-form-ai-assist/` |
| Draft 范例 | `skyeye-auto/auto-pro/.../*AiDraft*` |

### 1.3 关键抽象（扩展必须对齐）

1. **AiSkill**：`appId` + `applyServiceClassName` 绑定业务对象；积木编译为 `instruction`
2. **bizType**：流式会话类型（`chat` / `dsFormAssist` / `dsFormLayoutAssist` / 新建领域类型）
3. **PromptBuilder**：`PlatformAiSkillPromptBuilder`（聊天 / 表单 / 布局）
4. **Draft 模式**：拼 prompt → `PlatformAiChatHelper.startStreamingChat` → 前端收流 → 解析 JSON 回填或展示建议
5. **风控默认**：金额、库存扣减、审批通过/驳回 **禁止 AI 直接落库**，只给建议 + 人工确认

---

## 2. 产品愿景 ↔ 落地映射

### 2.1 AI 智能进销存（SkyEye ERP）

| 愿景能力 | 落地方式 | 阶段 | 状态 |
|----------|----------|------|------|
| 智能单据打印解读 | 打印/单据页挂只读 Assist，解读模板与缺项 | P1 | ⬜ |
| 库存预警解读 | 新 `bizType`（如 `erpStockWarningAssist`），只读解释原因 | P1 | ⬜ |
| 采购补货建议 | 读库存+销量上下文 → 建议清单（不自动下单） | P2 | ⬜ |
| 对账摘要 | 对账单据摘要 draft | P1 | ⬜ |
| 设备维保预测 | 设备履历 + 保养数据 → 建议（需历史数据） | P2 | ⬜ |
| 一句话入库 | 自然语言 → `dsFormAssist` 回填入库单 | P0～P1 | ⬜ |

### 2.2 AI 智能办公（SkyEye OA）

| 愿景能力 | 落地方式 | 阶段 | 状态 |
|----------|----------|------|------|
| 一句话拟稿公文/报告 | 新 `bizType`（如 `oaDocDraft`） | P1 | ⬜ |
| 自动会议纪要 | 依赖转写输入 → 纪要 draft | P1 | ⬜ |
| 知识库问答 | 复用现有 RAG + 向导（深化技能包） | P0 | ⬜ |
| 智能待办清理建议 | 向导/技能建议，不自动删待办 | P1 | ⬜ |
| 聊天发起办事 | 向导找菜单 + 表单 AI 填单 | P0 | ⬜ |

### 2.3 AI 智能制造（SkyEye MES）

| 愿景能力 | 落地方式 | 阶段 | 状态 |
|----------|----------|------|------|
| 智能排产建议 | 只读建议 draft（不直接改计划） | P2 | ⬜ |
| 异常根因分析 | 异常上下文 → 分析 Assist | P2 | ⬜ |
| 质检数据备注 / 工序指导 | 表单 AI + 技能说明书 | P1 | ⬜ |
| 一句话报工 | 自然语言 → 报工表单回填 | P1 | ⬜ |

### 2.4 AI 智能审批（SkyEye Workflow）

| 愿景能力 | 落地方式 | 阶段 | 状态 |
|----------|----------|------|------|
| 一句话流程配置 | 自然语言 → 流程模型草案（高风险后置） | P2 | ⬜ |
| 审批意见预判 / 风险点提醒 | 审批详情页只读 Assist | P1 | ⬜ |
| 智能驳回建议 | 只读建议文案，人工点驳回 | P1 | ⬜ |
| 一句话提交并自动流转 | **不做自动通过**；可辅助填申请单 | P0～P1 | ⬜ |

### 2.5 AI 智能商城（SkyEye 商城）

| 愿景能力 | 落地方式 | 阶段 | 状态 |
|----------|----------|------|------|
| AI 生成商品文案 | 商品编辑页 draft（`shopGoodsCopyDraft`） | P1 | ⬜ |
| 售后工单摘要 | 售后详情只读摘要 | P1 | ⬜ |
| 评价销售分析 / 补货建议 | 分析型 Assist | P2 | ⬜ |
| 一键上架多端售卖 | 表单 AI + 渠道字段回填 | P2 | ⬜ |

---

## 3. 分期任务明细（打勾跟踪）

### P0 — 高复用、低改造（建议首批）

#### P0-1 业务对象技能包与表单开关

- [ ] 梳理 ERP 高频单据业务对象清单（采购、入库、出库、销售等）
- [ ] 梳理 OA 高频单据业务对象清单（请假、报销、公文相关等）
- [ ] 为上述对象配置 AiSkill（`useScene` 含表单 / 聊天）
- [ ] 开启对应业务对象 `aiFormAssist`
- [ ] PC 写页确认已挂 `SkDsFormAiAssist`
- [ ] 移动写页确认已挂 `sk-ds-form-ai-assist`

#### P0-2 办公向导深化

- [ ] 补齐办事/找菜单相关技能与知识库条目
- [ ] 验证 PC `AiGuideDrawer`、移动 `aiChat` 主路径
- [ ] 确认平台已绑定 OA AI 角色（`oaRoleId`）

#### P0-3 知识库同步

- [ ] 进销存主数据/操作说明同步进 AI 知识库
- [ ] 办公制度/流程说明同步进 AI 知识库
- [ ] （可选）制造/商城基础说明入库

#### P0-4 风控与产品约定（文档级完成即打勾）

- [ ] 约定：金额字段 AI 回填后必须人工确认
- [ ] 约定：库存扣减/出入库过账禁止 AI 直接调用业务接口
- [ ] 约定：审批通过/驳回禁止 AI 自动执行
- [ ] 前端对「建议类」结果统一展示「仅供参考」提示

**P0 进度备注**

| 日期 | 说明 |
|------|------|
|  |  |

---

### P1 — 领域 Assist

#### P1-A 智能进销存

- [ ] 一句话入库：自然语言 → 入库单表单回填（复用 `dsFormAssist` 或专用 `bizType`）
- [ ] 出库/采购单据一句话填单（同模式）
- [ ] 库存预警解读 Assist（只读）
- [ ] 对账摘要 Assist（只读）
- [ ] 单据打印/详情页解读入口（可选）

#### P1-B 智能审批（只读）

- [ ] 审批详情页挂载「AI 摘要」
- [ ] 「风险点提醒」
- [ ] 「建议通过/驳回意见」文案（写入意见框需用户确认）
- [ ] 后端新 `bizType`（建议：`flowApproveAssist`）+ PromptBuilder
- [ ] 明确不调用「同意/驳回」业务 API

#### P1-C 智能商城

- [ ] 商品文案生成 Assist（PC 商城 / 商家端择一先做）
- [ ] 售后工单摘要 Assist
- [ ] 独立商城技能套件或角色策略（避免硬绑 OA 人设）
- [ ] skyeyeShop / ShopPC 入口挂载点确认

#### P1-D 智能办公增强

- [ ] 公文/报告拟稿 draft（`oaDocDraft`）
- [ ] 会议纪要 draft（输入：转写文本/录音结果）
- [ ] 待办清理「建议列表」（不自动删除）

#### P1-E 智能制造（浅层）

- [ ] 报工单一句话填单（表单 AI）
- [ ] 质检备注 / 工序指导技能说明书

**P1 进度备注**

| 日期 | 说明 |
|------|------|
|  |  |

---

### P2 — 深领域

#### P2-A 进销存深水区

- [ ] 采购补货建议（库存+销量+提前期）
- [ ] 设备维保预测
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
|  |  |

---

## 4. 技术落地约定

### 4.1 新建领域 Assist 标准步骤

1. 定 `bizType` 常量与用途（只读建议 / 表单回填 / 文案生成）
2. userauth 新增 `*AiDraftService` + Controller（仿 `PlatformDsFormAiDraftService` 或 auto `*AiDraft`）
3. Prompt：业务上下文 +（可选）AiSkill.instruction + 输出 JSON Schema
4. 走 `PlatformAiChatHelper` 流式；前端 `useAiStream` 按 `bizType` 订阅
5. 解析结果：回填表单字段 **或** 展示建议面板；落库必须走原有业务校验接口
6. 权限、`allUse`、租户 Token 与现有 AI 一致

### 4.2 技能配置约定

| useScene | 用途 |
|----------|------|
| 1 聊天 | 向导、办事问答 |
| 2 表单 | 写页 AI 辅助填单 |
| 3 仅编码 | 布局 AI、后台按 `oddNumber` 调用的场景 |

- 绑定字段：`appId` + `applyServiceClassName`
- 同业务对象同 UI 场景注意技能互斥（启用策略以现网为准）

### 4.3 建议新增 bizType 命名（草案，落地时以代码常量为准）

| bizType | 用途 | 阶段 |
|---------|------|------|
| `erpStockWarningAssist` | 库存预警解读 | P1 |
| `erpReconcileSummary` | 对账摘要 | P1 |
| `flowApproveAssist` | 审批摘要/风险/建议意见 | P1 |
| `shopGoodsCopyDraft` | 商品文案 | P1 |
| `shopAfterSaleSummary` | 售后摘要 | P1 |
| `oaDocDraft` | 公文/报告拟稿 | P1 |
| `oaMeetingMinutes` | 会议纪要 | P1 |
| `mesScheduleSuggest` | 排产建议 | P2 |
| `erpReplenishSuggest` | 补货建议 | P2 |

### 4.4 前端挂载建议

| 场景 | 组件/入口 |
|------|-----------|
| 单据填写 | 已有 `SkDsFormAiAssist` / 移动同款 |
| 只读分析/摘要 | 轻量抽屉或详情页「AI 助手」按钮 + 流式面板 |
| 办事找功能 | `AiGuideDrawer` / `aiChat` |
| 商家端 | `skyeyeShop/pages/my/merchant/*` 按需挂 |
| 门店工作台 | `skyeyeCloud/page-SHOP/storeDesign/*` 按需挂 |

---

## 5. 首批可立即开工清单（推荐顺序）

完成一项勾一项：

1. [ ] **ERP**：采购 / 入库 / 出库 — 表单技能 + 开 `aiFormAssist` + 一句话填单验证
2. [ ] **OA**：请假 / 常用审批申请单 — 表单技能 + 向导办事技能
3. [ ] **审批**：详情页「AI 摘要 + 建议意见」（只读，新 `flowApproveAssist`）
4. [ ] **商城**：商品文案生成（新 `shopGoodsCopyDraft`）

**首批进度备注**

| 日期 | 项 | 说明 |
|------|----|------|
|  |  |  |

---

## 6. 风险与依赖

| 风险 | 应对 |
|------|------|
| 幻觉导致金额/数量错误 | 只建议；提交走原校验；关键字段二次确认 |
| 审批自动通过 | 产品禁止；接口层不提供 AI 调同意/驳回 |
| 未绑定 OA AI 角色 | 向导不可用；上线检查平台设置 |
| Token 耗尽 | 租户用量提示；降级文案 |
| 商城人设与 OA 混用 | P1 起商城用独立技能套件/角色策略 |
| 知识库 ≠ OA 企业知识库 | RAG 用 AI 知识库；内容库需单独同步策略 |

---

## 7. 变更记录

| 日期 | 变更 |
|------|------|
| 2026-10-18 | 初稿：五块愿景映射 + P0/P1/P2 任务拆解 + 打勾跟踪约定 |
| 2026-10-20 | 选定方案 C；挂接 `AI-Agent方案C-业务工具.md`；C0 Agent 底座落地 |

---

## 8. 使用说明（给后续的自己）

1. 开工前先看 **§5 首批清单** 和 **§3 对应阶段**。  
2. 代码合入并验证后：勾选 `- [x]`，在对应「进度备注」写日期。  
3. 若某能力改为不做：将状态改为 ⏸，并在备注写原因。  
4. 新增 `bizType` / 技能编码时：同步更新 **§4.3**。  
5. 架构大变时：同步更新 **§1**，避免后人按过时路径开发。
