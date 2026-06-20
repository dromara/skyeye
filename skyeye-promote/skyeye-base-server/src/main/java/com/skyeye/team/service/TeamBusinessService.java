/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.team.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.team.entity.TeamBusiness;

/**
 * @ClassName: TeamBusinessService
 * @Description: 团队管理服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2022/11/13 19:36
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface TeamBusinessService extends AbstractTeamService<TeamBusiness> {

    void createTeamBusiness(InputObject inputObject, OutputObject outputObject);

    void queryTeamBusiness(InputObject inputObject, OutputObject outputObject);

    void deleteTeamBusiness(InputObject inputObject, OutputObject outputObject);

    void checkTeamBusinessAuthPermission(InputObject inputObject, OutputObject outputObject);

    void getMyTeamIds(InputObject inputObject, OutputObject outputObject);

    void queryMyBusinessTeamList(InputObject inputObject, OutputObject outputObject);

    void transferAllChargeUser(InputObject inputObject, OutputObject outputObject);
}
