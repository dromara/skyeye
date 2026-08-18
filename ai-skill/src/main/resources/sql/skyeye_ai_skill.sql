-- 在本地 MySQL 执行，不要在 erp/Nacos 业务库执行。
-- 库名必须和 application.yml 里 spring.datasource.url 的库名一致。

CREATE DATABASE IF NOT EXISTS `skyeye_ai_local`
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci;

USE `skyeye_ai_local`;

CREATE TABLE IF NOT EXISTS `skyeye_ai_skill` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `code` varchar(64) NOT NULL COMMENT '技能编码，如 skyeye-bigscreen',
  `name` varchar(100) NOT NULL COMMENT '技能名称',
  `description` varchar(1000) DEFAULT NULL COMMENT '何时使用（触发说明）',
  `instruction` mediumtext COMMENT '技能说明书，空壳可为空',
  `enabled` int(11) NOT NULL DEFAULT '1' COMMENT '启用状态 1启用 2禁用',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_id` varchar(32) DEFAULT NULL COMMENT '创建人',
  `create_time` varchar(19) DEFAULT NULL COMMENT '创建时间',
  `last_update_id` varchar(32) DEFAULT NULL COMMENT '最后修改人',
  `last_update_time` varchar(19) DEFAULT NULL COMMENT '最后修改时间',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户id',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_skyeye_ai_skill_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI技能（本地）';

INSERT INTO `skyeye_ai_skill`
(`id`, `code`, `name`, `description`, `instruction`, `enabled`, `remark`, `create_id`, `create_time`, `last_update_id`, `last_update_time`)
SELECT
  '26cb98e06588474bbe9f664333a167d4',
  'skyeye-bigscreen',
  'AI一句话创建数据大屏',
  '用自然语言描述大屏需求，自动生成全屏数据可视化大屏。适用于监控室/展厅/展示墙。',
  '用自然语言描述大屏需求，按 1920x1080 深色全屏模板生成大屏JSON（含KPI、趋势图、排行榜）。当前用模板生成，未接大模型。',
  1,
  '已支持 executeSkill 生成大屏JSON',
  'local',
  DATE_FORMAT(NOW(), '%Y-%m-%d %H:%i:%s'),
  'local',
  DATE_FORMAT(NOW(), '%Y-%m-%d %H:%i:%s')
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `skyeye_ai_skill` WHERE `code` = 'skyeye-bigscreen'
);

CREATE TABLE IF NOT EXISTS `skyeye_ai_skill_exec` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `skill_id` varchar(32) NOT NULL COMMENT '技能id',
  `skill_code` varchar(64) NOT NULL COMMENT '技能编码',
  `skill_name` varchar(100) DEFAULT NULL COMMENT '技能名称',
  `user_input` varchar(1000) DEFAULT NULL COMMENT '用户一句话需求',
  `screen_json` mediumtext COMMENT '生成的大屏JSON',
  `status` int(11) NOT NULL DEFAULT '1' COMMENT '1成功',
  `create_id` varchar(32) DEFAULT NULL COMMENT '创建人',
  `create_time` varchar(19) DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_skill_exec_code` (`skill_code`),
  KEY `idx_skill_exec_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI技能执行记录';
