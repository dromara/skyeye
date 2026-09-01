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
}
