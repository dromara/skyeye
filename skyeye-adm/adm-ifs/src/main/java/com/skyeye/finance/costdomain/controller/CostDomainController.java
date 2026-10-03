package com.skyeye.finance.costdomain.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.finance.costdomain.entity.CostDomain;
import com.skyeye.finance.costdomain.service.CostDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "成本域管理", tags = "成本域管理", modelName = "财务中枢")
public class CostDomainController {

    @Autowired
    private CostDomainService costDomainService;

    @ApiOperation(id = "queryCostDomainList", value = "分页查询成本域", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/CostDomainController/queryCostDomainList")
    public void queryCostDomainList(InputObject inputObject, OutputObject outputObject) {
        costDomainService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryCostDomainById", value = "成本域详情", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/CostDomainController/queryCostDomainById")
    public void queryCostDomainById(InputObject inputObject, OutputObject outputObject) {
        costDomainService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "writeCostDomain", value = "新增/编辑成本域", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CostDomain.class)
    @RequestMapping("/post/CostDomainController/writeCostDomain")
    public void writeCostDomain(InputObject inputObject, OutputObject outputObject) {
        costDomainService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteCostDomain", value = "删除成本域", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/CostDomainController/deleteCostDomain")
    public void deleteCostDomain(InputObject inputObject, OutputObject outputObject) {
        costDomainService.deleteById(inputObject, outputObject);
    }
}
