/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.skill.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.skill.entity.AiSkillCategory;

import java.util.List;

public interface AiSkillCategoryService extends SkyeyeBusinessService<AiSkillCategory> {

    List<AiSkillCategory> queryEnabledList();
}
