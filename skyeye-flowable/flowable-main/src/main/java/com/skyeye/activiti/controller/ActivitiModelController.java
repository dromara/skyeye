/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.activiti.controller;

import com.skyeye.activiti.service.ActivitiModelService;
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
 * @ClassName: ActivitiModelController
 * @Description: 工作流模型操作
 * @author: skyeye云系列--卫志强
 * @date: 2021/12/2 21:37
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "工作流模型", tags = "工作流模型", modelName = "工作流模型")
public class ActivitiModelController {

    @Autowired
    private ActivitiModelService activitiModelService;

    @ApiOperation(id = "activitimode003", value = "发布模型为流程定义", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "modelId", name = "modelId", value = "模型id", required = "required")})
    @RequestMapping("/post/ActivitiModelController/editActivitiModelToDeploy")
    public void editActivitiModelToDeploy(InputObject inputObject, OutputObject outputObject) {
        activitiModelService.editActivitiModelToDeploy(inputObject, outputObject);
    }

    @ApiOperation(id = "activitimode007", value = "取消发布", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "deploymentId", name = "deploymentId", value = "部署流程id", required = "required")})
    @RequestMapping("/post/ActivitiModelController/deleteReleasedActivitiModelById")
    public void deleteReleasedActivitiModelById(InputObject inputObject, OutputObject outputObject) {
        activitiModelService.deleteReleasedActivitiModelById(inputObject, outputObject);
    }

    @ApiOperation(id = "activitimode010", value = "导出model的xml文件", method = "GET", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "modelId", name = "modelId", value = "模型id", required = "required")})
    @RequestMapping("/post/ActivitiModelController/editApprovalActivitiTaskListByUserId")
    public void editApprovalActivitiTaskListByUserId(InputObject inputObject, OutputObject outputObject) {
        activitiModelService.editApprovalActivitiTaskListByUserId(inputObject, outputObject);
    }

    @ApiOperation(id = "queryActivitiModelVersionList", value = "查询流程定义版本历史", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "modelId", name = "modelId", value = "模型id", required = "required"),
        @ApiImplicitParam(id = "modelKey", name = "modelKey", value = "模型key", required = "required")})
    @RequestMapping("/post/ActivitiModelController/queryActivitiModelVersionList")
    public void queryActivitiModelVersionList(InputObject inputObject, OutputObject outputObject) {
        activitiModelService.queryActivitiModelVersionList(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteActivitiModelVersion", value = "删除历史流程定义版本", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "modelId", name = "modelId", value = "模型id", required = "required"),
        @ApiImplicitParam(id = "deploymentId", name = "deploymentId", value = "部署id", required = "required")})
    @RequestMapping("/post/ActivitiModelController/deleteActivitiModelVersion")
    public void deleteActivitiModelVersion(InputObject inputObject, OutputObject outputObject) {
        activitiModelService.deleteActivitiModelVersion(inputObject, outputObject);
    }

    @ApiOperation(id = "discardActivitiModelDraft", value = "删除未发布修改并还原为当前线上版本", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "modelId", name = "modelId", value = "模型id", required = "required")})
    @RequestMapping("/post/ActivitiModelController/discardActivitiModelDraft")
    public void discardActivitiModelDraft(InputObject inputObject, OutputObject outputObject) {
        activitiModelService.discardActivitiModelDraft(inputObject, outputObject);
    }

}
