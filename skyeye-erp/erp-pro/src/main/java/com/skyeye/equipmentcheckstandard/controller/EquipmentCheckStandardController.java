package com.skyeye.equipmentcheckstandard.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.features.SubmitSkyeyeFlowable;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.equipmentcheckstandard.entity.EquipmentCheckStandard;
import com.skyeye.equipmentcheckstandard.service.EquipmentCheckStandardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: EquipmentCheckStandardController
 * @Description: 设备点检标准控制层
 */
@RestController
@Api(value = "设备点检标准", tags = "设备点检标准", modelName = "设备点检")
public class EquipmentCheckStandardController {

    @Autowired
    private EquipmentCheckStandardService equipmentCheckStandardService;

    @ApiOperation(id = "queryEquipmentCheckStandardList", value = "获取设备点检标准列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/EquipmentCheckStandardController/queryEquipmentCheckStandardList")
    public void queryEquipmentCheckStandardList(InputObject inputObject, OutputObject outputObject) {
        equipmentCheckStandardService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeEquipmentCheckStandard", value = "新增/编辑设备点检标准", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = EquipmentCheckStandard.class)
    @RequestMapping("/post/EquipmentCheckStandardController/writeEquipmentCheckStandard")
    public void writeEquipmentCheckStandard(InputObject inputObject, OutputObject outputObject) {
        equipmentCheckStandardService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "submitEquipmentCheckStandardToApproval", value = "设备点检标准提交审批", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/EquipmentCheckStandardController/submitToApproval")
    public void submitToApproval(InputObject inputObject, OutputObject outputObject) {
        equipmentCheckStandardService.submitToApproval(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteEquipmentCheckStandardById", value = "删除设备点检标准", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping(value = "/post/EquipmentCheckStandardController/deleteEquipmentCheckStandardById", method = RequestMethod.DELETE)
    public void deleteEquipmentCheckStandardById(InputObject inputObject, OutputObject outputObject) {
        equipmentCheckStandardService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "revokeEquipmentCheckStandard", value = "撤销设备点检标准申请", method = "PUT", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/EquipmentCheckStandardController/revoke")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        equipmentCheckStandardService.revoke(inputObject, outputObject);
    }

    @ApiOperation(id = "queryEquipmentCheckStandardById", value = "根据id查询点检标准详情", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/EquipmentCheckStandardController/queryEquipmentCheckStandardById")
    public void queryEquipmentCheckStandardById(InputObject inputObject, OutputObject outputObject) {
        equipmentCheckStandardService.selectById(inputObject, outputObject);
    }

}

