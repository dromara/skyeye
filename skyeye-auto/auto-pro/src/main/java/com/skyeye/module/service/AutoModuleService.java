/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.module.service;

import com.skyeye.base.business.service.SkyeyeTeamAuthService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.module.entity.AutoModule;

import java.util.List;

/**
 * @ClassName: AutoModuleService
 * @Description: 项目模块管理服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/19 8:21
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface AutoModuleService extends SkyeyeTeamAuthService<AutoModule> {

    void queryAutoModuleForTree(InputObject inputObject, OutputObject outputObject);

    void queryFirstAutoModuleList(InputObject inputObject, OutputObject outputObject);

    List<String> queryAllChildIdsByParentId(List<String> ids);
}
