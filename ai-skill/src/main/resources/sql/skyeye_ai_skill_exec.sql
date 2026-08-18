-- 在本地库 skyeye_ai_local 执行（已有技能表的基础上补执行记录表）
USE `skyeye_ai_local`;

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

UPDATE `skyeye_ai_skill`
SET `instruction` = '用自然语言描述大屏需求，按 1920x1080 深色全屏模板生成大屏JSON（含KPI、趋势图、排行榜）。当前用模板生成，未接大模型。',
    `remark` = '已支持 executeSkill 生成大屏JSON'
WHERE `code` = 'skyeye-bigscreen';
