package com.skyeye.finance.invaccounting.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.finance.invaccounting.entity.InvAcctBill;
import com.skyeye.finance.invaccounting.service.InvAcctBillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "存货核算管理", tags = "存货核算管理", modelName = "财务中枢")
public class InvAcctBillController {

    @Autowired
    private InvAcctBillService invAcctBillService;

    @ApiOperation(id = "queryInvAcctBillList", value = "分页查询存货核算单", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/InvAcctBillController/queryInvAcctBillList")
    public void queryInvAcctBillList(InputObject inputObject, OutputObject outputObject) {
        invAcctBillService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeInvAcctBill", value = "新增/编辑存货核算单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = InvAcctBill.class)
    @RequestMapping("/post/InvAcctBillController/writeInvAcctBill")
    public void writeInvAcctBill(InputObject inputObject, OutputObject outputObject) {
        invAcctBillService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryInvAcctBillById", value = "存货核算单详情", method = "GET", allUse = "2")
    @ApiImplicitParams({@ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/InvAcctBillController/queryInvAcctBillById")
    public void queryInvAcctBillById(InputObject inputObject, OutputObject outputObject) {
        invAcctBillService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "valueAndPostInvAcctBill", value = "存货核算计价并生成凭证", method = "POST", allUse = "1")
    @ApiImplicitParams({@ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/InvAcctBillController/valueAndPostInvAcctBill")
    public void valueAndPostInvAcctBill(InputObject inputObject, OutputObject outputObject) {
        invAcctBillService.valueAndPost(inputObject, outputObject);
    }

    @ApiOperation(id = "createInvAcctFromBiz", value = "业务单据驱动创建存货核算并记账", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = InvAcctBill.class)
    @RequestMapping("/post/InvAcctBillController/createInvAcctFromBiz")
    public void createInvAcctFromBiz(InputObject inputObject, OutputObject outputObject) {
        outputObject.setBean(invAcctBillService.createFromBizEvent(inputObject.getParams()));
    }
}
