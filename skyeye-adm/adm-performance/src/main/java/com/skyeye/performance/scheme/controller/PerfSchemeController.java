package com.skyeye.performance.scheme.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.performance.scheme.entity.PerfScheme;
import com.skyeye.performance.scheme.service.PerfSchemeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "绩效考核方案", tags = "绩效考核方案", modelName = "员工绩效")
public class PerfSchemeController {

    @Autowired
    private PerfSchemeService perfSchemeService;

    @ApiOperation(id = "queryPerfSchemeList", value = "查询绩效考核方案列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/PerfSchemeController/queryPerfSchemeList")
    public void queryPerfSchemeList(InputObject inputObject, OutputObject outputObject) {
        perfSchemeService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writePerfScheme", value = "新增/编辑绩效考核方案", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = PerfScheme.class)
    @RequestMapping("/post/PerfSchemeController/writePerfScheme")
    public void writePerfScheme(InputObject inputObject, OutputObject outputObject) {
        perfSchemeService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryPerfSchemeById", value = "根据id查询绩效考核方案", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PerfSchemeController/queryPerfSchemeById")
    public void queryPerfSchemeById(InputObject inputObject, OutputObject outputObject) {
        perfSchemeService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "deletePerfSchemeById", value = "删除绩效考核方案", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PerfSchemeController/deletePerfSchemeById")
    public void deletePerfSchemeById(InputObject inputObject, OutputObject outputObject) {
        perfSchemeService.deleteById(inputObject, outputObject);
    }
}
