-- ============================================================
-- ai-knowledge 本地测试建库脚本
-- 库名需与 application.yml 中一致：skyeye_ai_local
-- 用法：在 MySQL 客户端整段执行即可
-- ============================================================

CREATE DATABASE IF NOT EXISTS `skyeye_ai_local`
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_general_ci;

USE `skyeye_ai_local`;

-- 向量模型配置
CREATE TABLE IF NOT EXISTS `skyeye_ai_embed_model` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `name` varchar(100) NOT NULL COMMENT '名称',
  `platform` varchar(32) NOT NULL COMMENT '平台: TongYi/YiYan',
  `api_key` varchar(255) DEFAULT NULL COMMENT 'apiKey',
  `secret_key` varchar(255) DEFAULT NULL COMMENT 'secretKey',
  `model` varchar(100) DEFAULT NULL COMMENT '模型名: text-embedding-v3 / Embedding-V1',
  `enabled` int DEFAULT 1 COMMENT '1启用 0禁用',
  `remark` varchar(255) DEFAULT NULL,
  `create_id` varchar(32) DEFAULT NULL,
  `create_time` varchar(30) DEFAULT NULL,
  `last_update_id` varchar(32) DEFAULT NULL,
  `last_update_time` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_platform` (`platform`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI向量模型配置';

-- 知识库
CREATE TABLE IF NOT EXISTS `skyeye_ai_knowledge` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `name` varchar(100) NOT NULL COMMENT '知识库名称',
  `descr` varchar(500) DEFAULT NULL COMMENT '描述',
  `embed_id` varchar(32) NOT NULL COMMENT '向量模型配置id',
  `status` varchar(20) NOT NULL DEFAULT 'enable' COMMENT 'enable/disable',
  `type` varchar(20) NOT NULL DEFAULT 'knowledge' COMMENT 'knowledge/memory',
  `metadata` text COMMENT '元数据JSON',
  `create_id` varchar(32) DEFAULT NULL,
  `create_time` varchar(30) DEFAULT NULL,
  `last_update_id` varchar(32) DEFAULT NULL,
  `last_update_time` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_embed_id` (`embed_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI知识库';

-- 知识库文档
CREATE TABLE IF NOT EXISTS `skyeye_ai_knowledge_doc` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `knowledge_id` varchar(32) NOT NULL COMMENT '知识库id',
  `title` varchar(200) NOT NULL COMMENT '标题',
  `type` varchar(20) NOT NULL COMMENT 'text/file/web',
  `content` longtext COMMENT '内容',
  `metadata` text COMMENT '元数据JSON',
  `status` varchar(20) DEFAULT 'draft' COMMENT 'draft/building/complete/failed',
  `create_id` varchar(32) DEFAULT NULL,
  `create_time` varchar(30) DEFAULT NULL,
  `last_update_id` varchar(32) DEFAULT NULL,
  `last_update_time` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_knowledge_id` (`knowledge_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI知识库文档';

-- 知识库分段向量
CREATE TABLE IF NOT EXISTS `skyeye_ai_knowledge_segment` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `knowledge_id` varchar(32) NOT NULL COMMENT '知识库id',
  `doc_id` varchar(32) NOT NULL COMMENT '文档id',
  `doc_name` varchar(200) DEFAULT NULL COMMENT '文档标题',
  `content` longtext NOT NULL COMMENT '分段文本',
  `embedding` longtext COMMENT '向量JSON',
  `dimension` int DEFAULT NULL COMMENT '向量维度',
  `segment_index` int DEFAULT 0 COMMENT '分段序号',
  `create_id` varchar(32) DEFAULT NULL,
  `create_time` varchar(30) DEFAULT NULL,
  `last_update_id` varchar(32) DEFAULT NULL,
  `last_update_time` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_knowledge_id` (`knowledge_id`),
  KEY `idx_doc_id` (`doc_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI知识库分段向量';
