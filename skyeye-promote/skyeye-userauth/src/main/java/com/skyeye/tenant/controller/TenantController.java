/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.tenant.classenum.TenantOrgType;
import com.skyeye.tenant.entity.Tenant;
import com.skyeye.tenant.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: TenantController
 * @Description: 租户控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/28 20:15
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "租户管理", tags = "租户管理", modelName = "租户管理")
public class TenantController {

    @Autowired
    private TenantService tenantService;

    @ApiOperation(id = "queryTenantList", value = "获取租户列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/TenantController/queryTenantList")
    public void queryTenantList(InputObject inputObject, OutputObject outputObject) {
        tenantService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeTenant", value = "新增/编辑租户", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = Tenant.class)
    @RequestMapping("/post/TenantController/writeTenant")
    public void writeTenant(InputObject inputObject, OutputObject outputObject) {
        tenantService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryTenantById", value = "根据id查询租户信息", method = "GET", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "租户ID", required = "required")})
    @RequestMapping("/post/TenantController/queryTenantById")
    public void queryTenantById(InputObject inputObject, OutputObject outputObject) {
        tenantService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryTenantByIds", value = "根据id批量查询租户信息", method = "POST", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "ids", name = "ids", value = "主键id，多个用逗号隔开", required = "required")})
    @RequestMapping("/post/TenantController/queryTenantByIds")
    public void queryTenantByIds(InputObject inputObject, OutputObject outputObject) {
        tenantService.selectByIds(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteTenantById", value = "删除租户", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "租户ID", required = "required")})
    @RequestMapping("/post/TenantController/deleteTenantById")
    public void deleteTenantById(InputObject inputObject, OutputObject outputObject) {
        tenantService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAllTenantList", value = "获取所有租户信息", method = "GET", allUse = "0")
    @RequestMapping("/post/TenantController/queryAllTenantList")
    public void queryAllTenantList(InputObject inputObject, OutputObject outputObject) {
        tenantService.queryAllTenantList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAllTenantListByKeyword", value = "根据关键字查询租户信息", method = "POST", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "keyword", name = "keyword", value = "关键词")})
    @RequestMapping("/post/TenantController/queryAllTenantListByKeyword")
    public void queryAllTenantListByKeyword(InputObject inputObject, OutputObject outputObject) {
        tenantService.queryAllTenantListByKeyword(inputObject, outputObject);
    }

    @ApiOperation(id = "queryCurrentTenantInfo", value = "查询当前租户信息（租户自管）", method = "GET", allUse = "2")
    @RequestMapping("/post/TenantController/queryCurrentTenantInfo")
    public void queryCurrentTenantInfo(InputObject inputObject, OutputObject outputObject) {
        tenantService.queryCurrentTenantInfo(inputObject, outputObject);
    }

    @ApiOperation(id = "updateCurrentTenantInfo", value = "更新当前组织信息（组织自管）", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "name", name = "name", value = "组织名称", required = "required"),
        @ApiImplicitParam(id = "logo", name = "logo", value = "组织Logo"),
        @ApiImplicitParam(id = "remark", name = "remark", value = "组织简介"),
        @ApiImplicitParam(id = "contactName", name = "contactName", value = "联系人/负责人"),
        @ApiImplicitParam(id = "contactPhone", name = "contactPhone", value = "联系电话"),
        @ApiImplicitParam(id = "contactEmail", name = "contactEmail", value = "联系邮箱"),
        @ApiImplicitParam(id = "address", name = "address", value = "地址"),
        @ApiImplicitParam(id = "website", name = "website", value = "官网"),
        @ApiImplicitParam(id = "industry", name = "industry", value = "所属行业"),
        @ApiImplicitParam(id = "creditCode", name = "creditCode", value = "统一社会信用代码"),
        @ApiImplicitParam(id = "legalPerson", name = "legalPerson", value = "法定代表人"),
        @ApiImplicitParam(id = "whetherSearchable", name = "whetherSearchable", value = "是否允许被搜索", enumClass = WhetherEnum.class),
        @ApiImplicitParam(id = "whetherJoinNeedAudit", name = "whetherJoinNeedAudit", value = "加入是否需要审核", enumClass = WhetherEnum.class)})
    @RequestMapping("/post/TenantController/updateCurrentTenantInfo")
    public void updateCurrentTenantInfo(InputObject inputObject, OutputObject outputObject) {
        tenantService.updateCurrentTenantInfo(inputObject, outputObject);
    }

    @ApiOperation(id = "createCurrentTenant", value = "当前用户创建组织", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "name", name = "name", value = "组织名称", required = "required"),
        @ApiImplicitParam(id = "orgType", name = "orgType", value = "组织类型", enumClass = TenantOrgType.class, required = "required,num"),
        @ApiImplicitParam(id = "logo", name = "logo", value = "组织Logo"),
        @ApiImplicitParam(id = "remark", name = "remark", value = "组织简介"),
        @ApiImplicitParam(id = "contactName", name = "contactName", value = "联系人/负责人"),
        @ApiImplicitParam(id = "contactPhone", name = "contactPhone", value = "联系电话"),
        @ApiImplicitParam(id = "contactEmail", name = "contactEmail", value = "联系邮箱"),
        @ApiImplicitParam(id = "address", name = "address", value = "地址"),
        @ApiImplicitParam(id = "website", name = "website", value = "官网"),
        @ApiImplicitParam(id = "industry", name = "industry", value = "所属行业"),
        @ApiImplicitParam(id = "creditCode", name = "creditCode", value = "统一社会信用代码"),
        @ApiImplicitParam(id = "legalPerson", name = "legalPerson", value = "法定代表人")})
    @RequestMapping("/post/TenantController/createCurrentTenant")
    public void createCurrentTenant(InputObject inputObject, OutputObject outputObject) {
        tenantService.createCurrentTenant(inputObject, outputObject);
    }

    @ApiOperation(id = "searchSearchableTenantList", value = "搜索可加入的组织", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "keyword", name = "keyword", value = "组织名称关键词", required = "required")})
    @RequestMapping("/post/TenantController/searchSearchableTenantList")
    public void searchSearchableTenantList(InputObject inputObject, OutputObject outputObject) {
        tenantService.searchSearchableTenantList(inputObject, outputObject);
    }

}
