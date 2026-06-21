/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.personnel.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.personnel.service.SysEveUserStaffTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: SysEveUserStaffTimeController
 * @Description: 员工绑定的考勤班次控制层
 * @author: skyeye云系列--卫志强
 * @date: 2025/4/30 9:25
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "员工绑定的考勤班次", tags = "员工绑定的考勤班次", modelName = "员工绑定的考勤班次")
public class SysEveUserStaffTimeController {

    @Autowired
    private SysEveUserStaffTimeService sysEveUserStaffTimeService;

    @ApiOperation(id = "querySysEveUserStaffTimeListByTimeId", value = "根据考勤班次id查询员工绑定的考勤班次列表", method = "POST", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "timeId", name = "timeId", value = "班次id", required = "required")})
    @RequestMapping("/post/SysEveUserStaffTimeController/querySysEveUserStaffTimeListByTimeId")
    public void querySysEveUserStaffTimeListByTimeId(InputObject inputObject, OutputObject outputObject) {
        sysEveUserStaffTimeService.querySysEveUserStaffTimeListByTimeId(inputObject, outputObject);
    }

    @ApiOperation(id = "countSysEveUserStaffTimeByTimeIds", value = "批量统计各班次绑定员工数", method = "POST", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "timeIds", name = "timeIds", value = "班次id，多个逗号隔开", required = "required")})
    @RequestMapping("/post/SysEveUserStaffTimeController/countSysEveUserStaffTimeByTimeIds")
    public void countSysEveUserStaffTimeByTimeIds(InputObject inputObject, OutputObject outputObject) {
        sysEveUserStaffTimeService.countSysEveUserStaffTimeByTimeIds(inputObject, outputObject);
    }

    @ApiOperation(id = "queryStaffCheckWorkTimeRelationNameByStaffId", value = "根据员工id获取该员工的考勤时间段", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "staffId", name = "staffId", value = "员工id", required = "required")})
    @RequestMapping("/post/SysEveUserStaffTimeController/queryStaffCheckWorkTimeRelationNameByStaffId")
    public void queryStaffCheckWorkTimeRelationNameByStaffId(InputObject inputObject, OutputObject outputObject) {
        sysEveUserStaffTimeService.getStaffCheckWorkTimeByStaffId(inputObject, outputObject);
    }

}
