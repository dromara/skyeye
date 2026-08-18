-- 已有库升级：更新技能说明书。本地库 skyeye_ai_local 执行。
USE `skyeye_ai_local`;

UPDATE `skyeye_ai_skill`
SET `instruction` = '用自然语言描述大屏需求，生成 1920x1080 深色全屏大屏JSON。已配置大模型 Key 时由模型排版，否则回退固定模板。',
    `remark` = '已支持 executeSkill 调大模型生成大屏JSON，失败回退模板'
WHERE `code` = 'skyeye-bigscreen';
