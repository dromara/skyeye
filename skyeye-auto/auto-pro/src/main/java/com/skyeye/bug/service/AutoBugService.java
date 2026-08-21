/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.bug.service;

import com.skyeye.base.business.service.SkyeyeTeamAuthService;
import com.skyeye.bug.entity.AutoBug;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * @ClassName: AutoBugService
 * @Description: bug管理服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/18 22:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface AutoBugService extends SkyeyeTeamAuthService<AutoBug> {

    void aiGenerateBugDraft(InputObject inputObject, OutputObject outputObject);

    void aiParseBugDraft(InputObject inputObject, OutputObject outputObject);
}
