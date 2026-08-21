# ai-knowledge

独立 AI 知识库服务骨架（对齐 `ai-skill`）。

> 当前为**框架占位**：Controller / Service 接口路径已保留，业务实现（向量化、检索等）已清空，调用会返回「功能待实现，后续恢复」。

## 已保留

- 工程启动：`AiKnowledgeApplication`（端口 **8100**）
- 表结构 SQL：`src/main/resources/sql/skyeye_ai_knowledge.sql`
- 实体 / 枚举 / DAO / 通用返回与异常
- 接口路径（Controller 方法签名）

## 已清空（待恢复）

- 向量化客户端（TongYi / YiYan）
- 文档切段、embedding、命中测试等 Service 实现

## 启动

1. 执行 SQL 建库（可选，骨架阶段可不连库验证编译）
2. 修改 `application.yml` 数据库账号密码
3. 运行 `com.skyeye.knowledge.AiKnowledgeApplication`

## 当前保留的接口（占位）

仅保留知识库 Controller：`/post/KnowledgeController/*`  
（`EmbedModelController`、`KnowledgeDocController` 已删，后续可再恢复）
