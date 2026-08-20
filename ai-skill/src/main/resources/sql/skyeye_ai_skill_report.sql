-- 第4步：本地镜像 Skyeye report_page，供 Skills 落大屏 JSON。
-- 在本地库 skyeye_ai_local 执行（不写业务库）。
USE `skyeye_ai_local`;

CREATE TABLE IF NOT EXISTS `report_page` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `name` varchar(100) NOT NULL COMMENT '报表页面名称',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `content` mediumtext COMMENT '页面报表json串（设计器 content）',
  `delete_flag` int(11) NOT NULL DEFAULT '0' COMMENT '删除标记 0正常',
  `create_id` varchar(32) DEFAULT NULL COMMENT '创建人',
  `create_time` varchar(19) DEFAULT NULL COMMENT '创建时间',
  `last_update_id` varchar(32) DEFAULT NULL COMMENT '最后修改人',
  `last_update_time` varchar(19) DEFAULT NULL COMMENT '最后修改时间',
  PRIMARY KEY (`id`),
  KEY `idx_report_page_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报表页面（Skills本地镜像，结构对齐 Skyeye report_page）';

-- 若下面两列已存在会报错，可忽略后继续
ALTER TABLE `skyeye_ai_skill_exec`
  ADD COLUMN `report_page_id` varchar(32) DEFAULT NULL COMMENT '关联 report_page.id' AFTER `screen_json`;

ALTER TABLE `skyeye_ai_skill_exec`
  ADD COLUMN `report_content` mediumtext COMMENT '写入报表页的 content JSON' AFTER `report_page_id`;
