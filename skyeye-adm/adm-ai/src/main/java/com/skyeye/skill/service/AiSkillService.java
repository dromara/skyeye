/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.skill.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.skill.entity.AiSkill;

import java.util.List;

public interface AiSkillService extends SkyeyeBusinessService<AiSkill> {

    List<AiSkill> queryByBiz(String appId, String serviceClassName);

    List<AiSkill> queryEnabledList();

    List<AiSkill> queryBySuiteId(String suiteId);

    List<AiSkill> queryByCategoryId(String categoryId);

    void queryMatchList(InputObject inputObject, OutputObject outputObject);

    /**
     * 对话/场景 Assist：按 id 取技能（含提示词），跨租户可读，与 queryMatchList 一致。
     * 管理端配置的技能要给商城商家端用时，不能按当前请求租户隔离掉。
     */
    AiSkill selectByIdForPrompt(String id);

    /**
     * 按技能编码查询（启用校验在 Controller/调用方处理）
     */
    AiSkill queryByOddNumber(String oddNumber);

    /**
     * 按技能编码查询启用中的技能（供编码调用）
     */
    void queryAiSkillByOddNumber(InputObject inputObject, OutputObject outputObject);
}
