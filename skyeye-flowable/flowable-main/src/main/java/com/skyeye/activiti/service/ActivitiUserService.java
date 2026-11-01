/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.activiti.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * @ClassName: ActivitiUserService
 * @Description: 工作流用户相关内容
 * @author: skyeye云系列--卫志强
 * @date: 2021/12/2 20:45
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ActivitiUserService {

    void queryUserListToActiviti(InputObject inputObject, OutputObject outputObject);

    void queryUserGroupListToActiviti(InputObject inputObject, OutputObject outputObject);

    /**
     * 按用户/审批组 ID 批量解析展示名（工号_姓名 / 组名），供流程设计器审批人属性回显
     */
    void queryDisplayNameByIds(InputObject inputObject, OutputObject outputObject);

    /**
     * 岗位名称列表（去重），供「发起人同部门+岗位」规则选择
     */
    void queryJobNameListForActiviti(InputObject inputObject, OutputObject outputObject);

    /**
     * 按发起人同部门 + 岗位名称解析审批人
     */
    void queryUsersByInitiatorDeptAndJob(InputObject inputObject, OutputObject outputObject);

    /**
     * 运行时：按发起人用户ID + 岗位名称查同部门岗位用户
     */
    java.util.List<java.util.Map<String, Object>> resolveUsersByInitiatorDeptAndJob(String initiatorUserId, String jobName);

    /**
     * 运行时：按发起人向上查 N 级直属上级（N=1 为直属上级）
     */
    java.util.List<java.util.Map<String, Object>> resolveUsersByLeaderLevel(String initiatorUserId, int level);

}
