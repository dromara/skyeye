# 方案 C：Agent 工具（业务查询 + 可选知识库检索）

> 状态：✅ C0～C2 已合入并联调通过（配置驱动 `biz_board` / `biz_list` + 平台指引配置）  
> 创建：2026-10-20  
> 关联：`AI能力落地计划.md`、豆包对话 / 知识库 RAG

---

## 1. 为什么要做方案 C

当前豆包路径是：

```text
先 search_knowledge（topN 有限）→ 拼进 prompt → chat/completions
```

问题：
- 召回条数有限，**数目 / 全量列表**类问题易错
- 大段资料塞 prompt，贵且易截断
- 模型看不到「可查询的业务库」，只能看片段

方案 C 目标：

```text
用户提问 → 模型决定调工具 → 业务接口查数/列表 / 知识库检索 → 再生成回答
```

---

## 2. 目标架构

```text
┌─────────────┐     tools + messages      ┌──────────────────┐
│ 前端 Ai 聊天 │ ◄──── WebSocket 流式 ─────│ ChatServiceImpl   │
└─────────────┘                           │  (豆包 Agent 循环) │
                                          └────────┬─────────┘
                                                   │
                    ┌──────────────────────────────┼──────────────────────────────┐
                    ▼                              ▼                              ▼
           ┌────────────────┐            ┌─────────────────� ┌──────────────────────────────┼──────────────────────────────┐
                    ▼                              ▼                              ▼
           ┌────────────────┐            ┌─────────────────┐            ┌─────────────────┐
           │ knowledge_search│            │ biz_* 业务工具   │            │ （后续）更多工具  │
           │ 文档向量检索    │            │ count/list/detail│            │ ERP/OA/商城…     │
           └────────────────┘            └─────────────────┘            └─────────────────┘
```

### 2.1 两轮交互（OpenAI 兼容 tools）

仍用方舟 **`/chat/completions`**（与现有 `DouBaoChatClient` 一致），不强制先上 Responses API：

1. **Round A（可非流式）**：带 `tools` 定义请求；若返回 `tool_calls`，本地执行工具  
2. **Round B（流式）**：把 `tool` 结果写回 `messages`，再请求最终回答并 SSE 推前端  

知识库检索改为**可选工具**，默认不再「无脑先搜再塞 prompt」。

### 2.2 与方案 B（Responses + knowledge_search）关系

| 项 | 方案 B | 方案 C（本方案） |
|----|--------|------------------|
| 知识库 | 方舟托管工具，需旗舰版 | 继续用现有 Viking `search`，或以后再接官方 tool |
| 业务数目 | 不擅长 | **主战场**：count/list 走业务 API |
| 改造面 | 换 Responses 协议 | 在现有 Chat 上加 tools 循环 |

方案 C 为主；旗舰版就绪后，知识库工具可升级为官方 `knowledge_search`，业务工具不变。

---

## 3. 工具规范

### 3.1 接口约定（adm-ai）

```text
com.skyeye.ai.agent
  AiAgentTool          # name / description / parametersJsonSchema / execute
  AiAgentToolContext   # userId, tenantId, role, apiKey, bizType, ...
  AiAgentToolRegistry  # Spring 收集全部 Tool Bean
  DouBaoAgentRunner    # tools 循环编排
```

每个工具：

| 字段 | 说明 |
|------|------|
| `name` | 英文蛇形，如 `knowledge_doc_stats`、`biz_board` |
| `description` | 给模型看的用途说明（中文可） |
| `parameters` | JSON Schema（OpenAI tools 格式） |
| `execute(args, ctx)` | 返回给模型的字符串（建议 JSON） |

### 3.2 首批工具清单

#### A. 知识库（可选）

| name | 作用 | 状态 |
|------|------|------|
| `knowledge_search` | 按问题检索角色绑定知识库片段（Viking） | ❌ 已下线（不准/套餐问题） |
| `knowledge_doc_stats` | 查本地知识库文档数量/同步状态（结构化） | ✅ |

#### B. 业务查询（示例 → 再扩）

| name | 作用 | 模块 | 状态 |
|------|------|------|------|
| `direct_reply` | 无需查数时可直接回答（闲聊/解释/引导） | adm-ai | ✅ |
| `biz_board` | 业务看板笔数（配置驱动；domain=oa/erp/mall） | `AiAgentDynListRest` | ✅ |
| `biz_list` | 业务列表（配置驱动；仅 `appId+serviceClassName` 解析 URI，单 Feign） | `AiAgentDynListRest` | ✅ |

**路由**：闲聊 Agent 统一 `tool_choice=required`，由模型在查询工具与 `direct_reply` 间选择，**不做用户话术关键词匹配**。

**原则**：
- 数目类 → **按域一次拉看板**，禁止「一指标一工具 / 一指标一 Feign」膨胀  
- **禁止**靠知识库片段猜数  
- 列表 → 强制 `page/limit`，默认 limit≤5、最大 10；最终 JSON 带 `list` 供前端卡片  

- 写操作（过账/审批同意）→ **不提供工具**

### 3.3 风控

- [x] 约定写入落地计划：金额/库存/审批禁止 AI 直接落库  
- [ ] 工具执行鉴权：沿用当前登录用户 + 原接口数据权限  
- [x] 工具结果长度截断（8k）  
- [x] 最大 tool 轮次（3），防死循环  
- [x] 前端可展示「正在查询业务数据…」状态片（复用 `STREAM_TYPE_STATUS`）

---

## 4. 豆包接入改造点

| 文件 | 改动 |
|------|------|
| `DouBaoChatClient` | 支持 `tools`；非流式拿 `tool_calls`；流式最终答 |
| `ChatServiceImpl` | `bizType=chat` 且豆包时走 `DouBaoAgentRunner`；弱化强制 `appendRoleKnowledge` |
| `ai.agent.*` | 新包：工具与编排 |
| 业务模块 | 后续新增 `AiAgentTool` Bean（或 Feign 适配器） |

### 4.1 何时走 Agent

建议开关（配置或角色级，首期写死规则）：

- 平台 = 豆包  
- `bizType = chat`  
- 角色已配置（有 prompt；知识库可选）  

表单 Assist / 布局 Assist **暂不走** Agent（仍用现有 draft）。

### 4.2 Prompt（直接用 AI 角色 `prompt`）

工具使用规则写在角色 **`prompt`** 里即可（已并入 `Role.DEFAULT_PROMPT`），不再单独字段、不再代码硬拼。

可在「AI角色」编辑提示词，自行增删例如：

```text
你可以使用工具获取准确信息。
涉及数量、统计、列表、待办笔数时，必须调用业务查询工具，不要根据知识库片段臆测。
文档数量/同步状态用 knowledge_doc_stats（勿再写 knowledge_search，该工具已下线）。
```

---

## 5. 分期落地（打勾）

### C0 — 底座（本迭代）

- [x] 设计文档（本文）
- [x] `AiAgentTool` / `Registry` / `Context`
- [x] `knowledge_doc_stats`（`knowledge_search` 已下线）
- [x] `DouBaoChatClient` tools 非流式 + 最终流式
- [x] `DouBaoAgentRunner` + `ChatServiceImpl` 豆包 chat 接入
- [x] 去掉「必先 RAG 拼 prompt」；改为工具可选调用
- [x] 状态流：「正在检索知识库… / 正在查询业务数据…」

### C1 — 首批业务工具

- [x] 统一 `biz_board`（配置驱动 oa/erp/mall；30s 短缓存；erppage005 / 待办 total / 商城 stat）

### C2 — 增强

- [x] 列表工具 + 前端卡片展示（`biz_list` OA 三类 + `parseGuidePayload.list` / AiGuideDrawer）
- [x] 商城买家订单列表（`biz_list` domain=mall，对齐 queryOrderPageList）
- [x] 门店侧订单列表（`storeId` + queryOrderItemByStoreId）
- [x] ERP 待出库/待入库列表（`domain=erp`，depotOut/depotIn，对齐 E01/E03）
- [ ] 旗舰版时知识库切官方 Responses `knowledge_search`
- [ ] 多平台（通义/文心）复用同一 Tool 层

---

## 6. 验收用例

> 2026-10-24 联调：#3/#5/#6/#7/#10 等核心路径已通过；门店 #9 视环境有 storeId 再验。

| # | 用户问法 | 期望 |
|---|----------|------|
| 1 | 请假制度是怎样的？ | 暂无向量检索工具；旗舰版/官方检索就绪后再接 |
| 2 | 知识库里同步了多少文档？ | 调 `knowledge_doc_stats`，给准确数字 |
| 3 | 当前待出库有多少单？ | 调 `biz_board` domain=erp，取 counts.E01 |
| 5 | 我有多少审批待办？ | 调 `biz_board` domain=oa，取 counts.pending |
| 6 | 我有多少待发货/待收货订单？ | 调 `biz_board` domain=mall，取 waitDeliver / waitReceive |
| 7 | 列出我的审批待办 | 调 `biz_list`(oa/pending)，最终 JSON 带 list，前端出卡片 |
| 8 | 列出我的待发货订单 | 调 `biz_list`(mall/waitDeliver)，最终 JSON 带 list |
| 9 | 列出某门店待发货（已知 storeId） | 调 `biz_list`(mall/waitDeliver + storeId) |
| 10 | 列出待出库单据 | 调 `biz_list`(erp/depotOut)，与 E01 同口径 |
| 4 | 把库存扣掉 | 无写工具，提示人工到业务页操作 |

---

## 7. 进度备注

| 日期 | 说明 |
|------|------|
| 2026-10-20 | 选定方案 C；完成设计文档；开始 C0 底座 |
| 2026-10-20 | C0 底座合入：`com.skyeye.ai.agent.*`、豆包 tools 循环、闲聊路径取消强制预 RAG |
| 2026-10-21 | C1：ERP 待出/待入 Feign `erppage005` + `biz_count_depot_*` / generic |
| 2026-10-22 | C1 收敛：改为 `biz_erp_board` 整板快照 + 30s 缓存；删除按指标拆分工具 |
| 2026-10-22 | C1：`biz_oa_board` OA 待办看板（Flowable+日程） |
| 2026-10-22 | C1：`biz_mall_board` 商城看板；C1 业务看板收口完成 |
| 2026-10-22 | Agent 路由改为 tool_choice=required + direct_reply（去掉关键词硬匹配） |
| 2026-10-22 | C2：`biz_list`（OA 待办三类）+ 前端 list 卡片；prompt/输出规范补充 list |
| 2026-10-22 | C2：`biz_list` 扩 mall 买家订单（queryOrderPageList） |
| 2026-10-22 | C2：`biz_list` mall + storeId 门店售出列表 |
| 2026-10-22 | C2：`biz_list` erp depotOut/depotIn（对齐 E01/E03） |
| 2026-10-23 | `biz_list` 配置驱动：`ai-biz-list-endpoints.json` + 动态 Feign 执行器 |
| 2026-10-23 | `biz_list` 路由对齐 `IDataServiceImpl`：仅 `appId+serviceClassName`，单一 `AiAgentDynListRest` |
| 2026-10-23 | `biz_list` 可在平台信息「AI业务列表」配置；优先 DB，空则 classpath JSON |
| 2026-10-24 | 三看板 Tool 收敛为 `biz_board` + `ai-biz-board-endpoints.json`；平台可配 |
| 2026-10-24 | 路由支持 `springApplicationName`（非 SkyeyeBusinessService 可直连 Discovery）；待出库 NPE 修复；异常不再吞成 0 |
| 2026-10-24 | 平台「AI业务接入」指引式表单 + 内置 docs；联调通过（erp 待出库笔数/明细、OA/mall） |

---

## 8. 使用说明

完成一项将 `- [ ]` 改为 `- [x]`。  
业务工具新增时：实现 `AiAgentTool` 并注册为 Spring Bean，**无需改** Runner 核心循环。

### 8.1 扩展 `biz_list`（优先改配置，不写 Tool）

1. 编辑 `adm-ai/src/main/resources/ai-biz-list-endpoints.json`，追加一条 `endpoints`。  
2. 填 `domain` / `type` / `service`（`flowable`|`adm`|`erp`|`shop`）/ `apiId` / `fields`。  
3. 若需新微服务：在 `adm-common` 增加一个 `AiAgentDynXxxRest`，并在 `AiBizListRemoteInvoker` 加一行分支。  
4. 重启 adm-ai；**不必**改 `BizListTool`。  
5. 角色 prompt 里补一句新 type 说明（库里自定义 prompt 需回贴）。
