# ai-knowledge

独立 AI 知识库服务（对齐 `ai-skill`）。

> **业务实现已恢复**：Controller + Service + embedding（TongYi / YiYan 向量化、文档切段、余弦相似度检索）可完整跑通。

## 能力

1. `writeEmbedModel` → 向量模型 CRUD（TongYi / YiYan）
2. `writeKnowledge`（需 `embedId`）→ 知识库 CRUD；更换模型会触发文档重建
3. `writeKnowledgeDoc` → 保存后异步切段 + embedding，状态 `draft → building → complete/failed`
4. `knowledgeHitTest` / `knowledgeEmbeddingSearch` → MySQL 存向量 JSON + 余弦相似度检索（无 PgVector）

## 工程说明

- 启动类：`AiKnowledgeApplication`（端口 **8100**）
- 表结构 SQL：`src/main/resources/sql/skyeye_ai_knowledge.sql`
- 向量客户端：`com.skyeye.knowledge.embedding`（TongYi DashScope / YiYan Qianfan；XunFei 不支持 embedding）

## 启动

1. 执行 SQL 建库
2. 修改 `application.yml` 数据库账号密码
3. 运行 `com.skyeye.knowledge.AiKnowledgeApplication`

## 接口

- `EmbedModelController`：`/post/EmbedModelController/*`
- `KnowledgeController`：`/post/KnowledgeController/*`
- `KnowledgeDocController`：`/post/KnowledgeDocController/*`
