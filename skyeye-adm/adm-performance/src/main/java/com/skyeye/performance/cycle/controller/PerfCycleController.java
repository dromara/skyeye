package com.skyeye.performance.cycle.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.performance.cycle.entity.PerfCycle;
import com.skyeye.performance.cycle.service.PerfCycleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "绩效考核周期", tags = "绩效考核周期", modelName = "员工绩效")
public class PerfCycleController {

    @Autowired
    private PerfCycleService perfCycleService;

    @ApiOperation(id = "queryPerfCycleList", value = "查询绩效考核周期列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/PerfCycleController/queryPerfCycleList")
    public void queryPerfCycleList(InputObject inputObject, OutputObject outputObject) {
        perfCycleService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writePerfCycle", value = "新增/编辑绩效考核周期", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = PerfCycle.class)
    @RequestMapping("/post/PerfCycleController/writePerfCycle")
    public void writePerfCycle(InputObject inputObject, OutputObject outputObject) {
        perfCycleService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryPerfCycleById", value = "根据id查询绩效考核周期", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PerfCycleController/queryPerfCycleById")
    public void queryPerfCycleById(InputObject inputObject, OutputObject outputObject) {
        perfCycleService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "deletePerfCycleById", value = "删除绩效考核周期", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PerfCycleController/deletePerfCycleById")
    public void deletePerfCycleById(InputObject inputObject, OutputObject outputObject) {
        perfCycleService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "generatePerfAssessments", value = "按周期生成考核单", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "周期id", required = "required")})
    @RequestMapping("/post/PerfCycleController/generateAssessments")
    public void generateAssessments(InputObject inputObject, OutputObject outputObject) {
        perfCycleService.generateAssessments(inputObject, outputObject);
    }

    @ApiOperation(id = "closePerfCycle", value = "关闭绩效考核周期", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "周期id", required = "required")})
    @RequestMapping("/post/PerfCycleController/closeCycle")
    public void closeCycle(InputObject inputObject, OutputObject outputObject) {
        perfCycleService.closeCycle(inputObject, outputObject);
    }
}
