package com.skyeye.finance.template.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.finance.template.entity.VoucherTemplate;
import com.skyeye.finance.template.service.VoucherTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Api(value = "凭证模板管理", tags = "凭证模板管理", modelName = "财务中枢")
public class VoucherTemplateController {

    @Autowired
    private VoucherTemplateService voucherTemplateService;

    @ApiOperation(id = "queryVoucherTemplateList", value = "分页查询凭证模板", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/VoucherTemplateController/queryVoucherTemplateList")
    public void queryVoucherTemplateList(InputObject inputObject, OutputObject outputObject) {
        voucherTemplateService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeVoucherTemplate", value = "新增/编辑凭证模板", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = VoucherTemplate.class)
    @RequestMapping("/post/VoucherTemplateController/writeVoucherTemplate")
    public void writeVoucherTemplate(InputObject inputObject, OutputObject outputObject) {
        voucherTemplateService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryVoucherTemplateById", value = "凭证模板详情", method = "GET", allUse = "2")
    @ApiImplicitParams({@ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/VoucherTemplateController/queryVoucherTemplateById")
    public void queryVoucherTemplateById(InputObject inputObject, OutputObject outputObject) {
        voucherTemplateService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryEnabledVoucherTemplate", value = "按事项类型查询启用中的凭证模板", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "eventType", name = "eventType", value = "事项类型", required = "required"),
        @ApiImplicitParam(id = "setOfBooksId", name = "setOfBooksId", value = "账套id")})
    @RequestMapping("/post/VoucherTemplateController/queryEnabledVoucherTemplate")
    public void queryEnabledVoucherTemplate(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        Object eventType = params.get("eventType");
        Object setOfBooksId = params.get("setOfBooksId");
        VoucherTemplate template = voucherTemplateService.findEnabledTemplate(
            eventType == null ? "" : eventType.toString(),
            setOfBooksId == null ? "" : setOfBooksId.toString());
        if (template != null) {
            outputObject.setBean(template);
            outputObject.settotal(1);
        }
    }

    @ApiOperation(id = "deleteVoucherTemplate", value = "删除凭证模板", method = "DELETE", allUse = "1")
    @ApiImplicitParams({@ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/VoucherTemplateController/deleteVoucherTemplate")
    public void deleteVoucherTemplate(InputObject inputObject, OutputObject outputObject) {
        voucherTemplateService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "initDefaultVoucherTemplates", value = "初始化默认凭证模板", method = "POST", allUse = "1")
    @RequestMapping("/post/VoucherTemplateController/initDefaultVoucherTemplates")
    public void initDefaultVoucherTemplates(InputObject inputObject, OutputObject outputObject) {
        voucherTemplateService.initDefaultTemplates(inputObject, outputObject);
    }
}
