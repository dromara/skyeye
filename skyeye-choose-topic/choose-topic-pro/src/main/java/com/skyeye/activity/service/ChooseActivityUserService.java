/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.activity.service;

import com.skyeye.activity.entity.ChooseActivityUser;
import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * @ClassName: ActivityUserService
 * @Description: 活动可参与用户服务接口层
 * @author: xqz
 * @date: 2024/3/9 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ChooseActivityUserService extends SkyeyeBusinessService<ChooseActivityUser> {
    void insertActivityUser(InputObject inputObject, OutputObject outputObject);

    void deleteByActivityId(String activityId);

    void queryActivityUserList(InputObject inputObject, OutputObject outputObject);

    void queryTeacherActivityUserList(InputObject inputObject, OutputObject outputObject);

    void deleteActivityUserByUserId(InputObject inputObject, OutputObject outputObject);

    boolean isActivityParticipant(String activityId, String userId);
}
