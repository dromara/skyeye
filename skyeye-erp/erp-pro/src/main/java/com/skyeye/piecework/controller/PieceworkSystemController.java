package com.skyeye.piecework.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.piecework.service.PieceworkSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "计件数量或工时统计信息", tags = "计件数量或工时统计信息", modelName = "计件数量或工时统计信息")
public class PieceworkSystemController {

    @Autowired
    private PieceworkSystemService pieceworkSystemService;

    @ApiOperation(id = "queryPieceworkSystem", value = "查询员工计件数量或工时统计信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/PieceworkSystemController/queryPieceworkSystem")
    public void queryPieceworkSystem(InputObject inputObject, OutputObject outputObject) {
        pieceworkSystemService.queryPieceworkSystem(inputObject, outputObject);
    }

    @ApiOperation(id = "queryPieceworkByStaffAndMonths", value = "按员工与月份区间查询计件（绩效取分）", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "staffId", name = "staffId", value = "员工id", required = "required"),
        @ApiImplicitParam(id = "farmId", name = "farmId", value = "车间id"),
        @ApiImplicitParam(id = "startMonth", name = "startMonth", value = "开始月 yyyy-MM", required = "required"),
        @ApiImplicitParam(id = "endMonth", name = "endMonth", value = "结束月 yyyy-MM", required = "required")})
    @RequestMapping("/post/PieceworkSystemController/queryPieceworkByStaffAndMonths")
    public void queryPieceworkByStaffAndMonths(InputObject inputObject, OutputObject outputObject) {
        pieceworkSystemService.queryPieceworkByStaffAndMonths(inputObject, outputObject);
    }

}
