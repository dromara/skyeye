/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.activiti.controller;

import com.skyeye.activiti.service.ActivitiUserService;
import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ActivitiUserController
 * @Description: 工作流用户相关内容
 * @author: skyeye云系列--卫志强
 * @date: 2021/12/2 20:45
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "工作流用户相关操作", tags = "工作流用户相关操作", modelName = "工作流用户相关操作")
public class ActivitiUserController {

    @Autowired
    private ActivitiUserService activitiUserService;

    @ApiOperation(id = "activitimode011", value = "获取人员选择", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "reqObj", name = "reqObj", value = "参数", required = "required")})
    @RequestMapping("/post/ActivitiUserController/queryUserListToActiviti")
    public void queryUserListToActiviti(InputObject inputObject, OutputObject outputObject) {
        activitiUserService.queryUserListToActiviti(inputObject, outputObject);
    }

    @ApiOperation(id = "activitimode012", value = "获取组人员选择", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "reqObj", name = "reqObj", value = "参数", required = "required")})
    @RequestMapping("/post/ActivitiUserController/queryUserGroupListToActiviti")
    public void queryUserGroupListToActiviti(InputObject inputObject, OutputObject outputObject) {
        activitiUserService.queryUserGroupListToActiviti(inputObject, outputObject);
    }

    @ApiOperation(id = "queryActivitiDisplayNameByIds", value = "按ID批量解析审批人/审批组展示名", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "userIds", name = "userIds", value = "用户ID，逗号分隔"),
        @ApiImplicitParam(id = "groupIds", name = "groupIds", value = "审批组ID，逗号分隔")})
    @RequestMapping("/post/ActivitiUserController/queryDisplayNameByIds")
    public void queryDisplayNameByIds(InputObject inputObject, OutputObject outputObject) {
        activitiUserService.queryDisplayNameByIds(inputObject, outputObject);
    }

    @ApiOperation(id = "queryActivitiJobNameList", value = "岗位名称列表（审批人规则）", method = "POST", allUse = "2")
    @RequestMapping("/post/ActivitiUserController/queryJobNameListForActiviti")
    public void queryJobNameListForActiviti(InputObject inputObject, OutputObject outputObject) {
        activitiUserService.queryJobNameListForActiviti(inputObject, outputObject);
    }

    @ApiOperation(id = "queryActivitiUsersByInitiatorDeptJob", value = "按发起人同部门岗位查审批人", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "initiatorUserId", name = "initiatorUserId", value = "发起人用户ID", required = "required"),
        @ApiImplicitParam(id = "jobName", name = "jobName", value = "岗位名称", required = "required")})
    @RequestMapping("/post/ActivitiUserController/queryUsersByInitiatorDeptAndJob")
    public void queryUsersByInitiatorDeptAndJob(InputObject inputObject, OutputObject outputObject) {
        activitiUserService.queryUsersByInitiatorDeptAndJob(inputObject, outputObject);
    }

}
