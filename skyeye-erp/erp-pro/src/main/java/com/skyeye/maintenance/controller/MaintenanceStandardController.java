/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.maintenance.controller;

import com.skyeye.maintenance.entity.MaintenanceStandard;
import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.maintenance.service.MaintenanceStandardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description: 保养标准控制层
 */
@RestController
@Api(value = "保养标准", tags = "保养标准", modelName = "保养标准")
public class MaintenanceStandardController {

    @Autowired
    private MaintenanceStandardService maintenanceStandardService;

    @ApiOperation(id = "queryMaintenanceStandardList", value = "分页查询保养标准", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/MaintenanceStandardController/queryMaintenanceStandardList")
    public void queryMaintenanceStandardList(InputObject inputObject, OutputObject outputObject) {
        maintenanceStandardService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeMaintenanceStandard", value = "新增/编辑保养标准", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = MaintenanceStandard.class)
    @RequestMapping("/post/MaintenanceStandardController/writeMaintenanceStandard")
    public void writeMaintenanceStandard(InputObject inputObject, OutputObject outputObject) {
        maintenanceStandardService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteMaintenanceStandardById", value = "删除保养标准", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/MaintenanceStandardController/deleteMaintenanceStandardById")
    public void deleteMaintenanceStandardById(InputObject inputObject, OutputObject outputObject) {
        maintenanceStandardService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAllMaintenanceStandardList", value = "查询全部保养标准", method = "GET", allUse = "2")
    @RequestMapping("/post/MaintenanceStandardController/queryAllMaintenanceStandardList")
    public void queryAllMaintenanceStandardList(InputObject inputObject, OutputObject outputObject) {
        List<MaintenanceStandard> list = maintenanceStandardService.queryAllData();
        outputObject.setBeans(list);
        outputObject.settotal(list.size());
    }

}
