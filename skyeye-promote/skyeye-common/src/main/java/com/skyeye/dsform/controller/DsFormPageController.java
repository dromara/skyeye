/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dsform.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.dsform.entity.DsFormPage;
import com.skyeye.dsform.entity.DsFormPageContentVo;
import com.skyeye.dsform.entity.TableColumnVo;
import com.skyeye.dsform.service.DsFormPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: DsFormPageController
 * @Description: 表单布局管理控制类
 * @author: skyeye云系列--卫志强
 * @date: 2022/5/24 18:59
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本组件仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "表单布局管理", tags = "表单布局管理", modelName = "动态表单模块")
public class DsFormPageController {

    @Autowired
    private DsFormPageService dsFormPageService;

    @ApiOperation(id = "queryDsFormPageList", value = "获取表单布局列表", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "className", name = "className", value = "service的className", required = "required"),
        @ApiImplicitParam(id = "appId", name = "appId", value = "服务的appId", required = "required")})
    @RequestMapping("/post/DsFormPageController/queryDsFormPageList")
    public void queryDsFormPageList(InputObject inputObject, OutputObject outputObject) {
        dsFormPageService.queryDsFormPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryDsFormPagePageList", value = "分页查询表单布局（可按业务对象过滤，不传则查全部）", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/DsFormPageController/queryDsFormPagePageList")
    public void queryDsFormPagePageList(InputObject inputObject, OutputObject outputObject) {
        dsFormPageService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeDsFormPage", value = "新增/编辑表单布局", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = DsFormPage.class)
    @RequestMapping("/post/DsFormPageController/writeDsFormPage")
    public void writeDsFormPage(InputObject inputObject, OutputObject outputObject) {
        dsFormPageService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteDsFormPage", value = "删除表单布局", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/DsFormPageController/deleteDsFormPage")
    public void deleteDsFormPage(InputObject inputObject, OutputObject outputObject) {
        dsFormPageService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "dsformpage006", value = "根据id查找表单布局", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/DsFormPageController/selectDsFormPageById")
    public void selectDsFormPageById(InputObject inputObject, OutputObject outputObject) {
        dsFormPageService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryDsFormPageForProcess", value = "根据业务对象的serviceClassName和流程模型id查找表单布局", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "serviceClassName", name = "serviceClassName", value = "业务对象的serviceClassName", required = "required")})
    @RequestMapping("/post/DsFormPageController/queryDsFormPageForProcess")
    public void queryDsFormPageForProcess(InputObject inputObject, OutputObject outputObject) {
        dsFormPageService.queryDsFormPageForProcess(inputObject, outputObject);
    }

    @ApiOperation(id = "writeDsFormPageContent", value = "保存表单布局关联的组件信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = DsFormPageContentVo.class)
    @RequestMapping("/post/DsFormPageController/writeDsFormPageContent")
    public void writeDsFormPageContent(InputObject inputObject, OutputObject outputObject) {
        dsFormPageService.writeDsFormPageContent(inputObject, outputObject);
    }

    @ApiOperation(id = "writeDsFormPageTable", value = "保存表格类型的布局的信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableColumnVo.class)
    @RequestMapping("/post/DsFormPageController/writeDsFormPageTable")
    public void writeDsFormPageTable(InputObject inputObject, OutputObject outputObject) {
        dsFormPageService.writeDsFormPageTable(inputObject, outputObject);
    }

    @ApiOperation(id = "writeDsFormPageContentAttr", value = "保存tab选项卡布局、小卡片布局等布局的自定义信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "表单布局id", required = "required"),
        @ApiImplicitParam(id = "content", name = "content", value = "布局的自定义内容")})
    @RequestMapping("/post/DsFormPageController/writeDsFormPageContentAttr")
    public void writeDsFormPageContentAttr(InputObject inputObject, OutputObject outputObject) {
        dsFormPageService.writeDsFormPageContentAttr(inputObject, outputObject);
    }

    @ApiOperation(id = "queryBusinessDataByObject", value = "根据业务数据id获取业务数据信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "objectId", name = "objectId", value = "所属第三方业务数据id", required = "required"),
        @ApiImplicitParam(id = "objectKey", name = "objectKey", value = "所属第三方业务数据的key", required = "required")})
    @RequestMapping("/post/DsFormPageController/queryBusinessDataByObject")
    public void queryBusinessDataByObject(InputObject inputObject, OutputObject outputObject) {
        dsFormPageService.queryBusinessDataByObject(inputObject, outputObject);
    }

}
