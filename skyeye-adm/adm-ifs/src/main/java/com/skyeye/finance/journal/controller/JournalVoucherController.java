package com.skyeye.finance.journal.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.finance.journal.entity.JournalVoucher;
import com.skyeye.finance.journal.service.JournalVoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "会计凭证管理", tags = "会计凭证管理", modelName = "财务中枢")
public class JournalVoucherController {

    @Autowired
    private JournalVoucherService journalVoucherService;

    @ApiOperation(id = "queryJournalVoucherList", value = "分页查询会计凭证", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/JournalVoucherController/queryJournalVoucherList")
    public void queryJournalVoucherList(InputObject inputObject, OutputObject outputObject) {
        journalVoucherService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeJournalVoucher", value = "新增/编辑会计凭证", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = JournalVoucher.class)
    @RequestMapping("/post/JournalVoucherController/writeJournalVoucher")
    public void writeJournalVoucher(InputObject inputObject, OutputObject outputObject) {
        journalVoucherService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryJournalVoucherById", value = "会计凭证详情", method = "GET", allUse = "2")
    @ApiImplicitParams({@ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/JournalVoucherController/queryJournalVoucherById")
    public void queryJournalVoucherById(InputObject inputObject, OutputObject outputObject) {
        journalVoucherService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteJournalVoucher", value = "删除会计凭证", method = "DELETE", allUse = "1")
    @ApiImplicitParams({@ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/JournalVoucherController/deleteJournalVoucher")
    public void deleteJournalVoucher(InputObject inputObject, OutputObject outputObject) {
        journalVoucherService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "approveJournalVoucher", value = "审核会计凭证", method = "POST", allUse = "1")
    @ApiImplicitParams({@ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/JournalVoucherController/approveJournalVoucher")
    public void approveJournalVoucher(InputObject inputObject, OutputObject outputObject) {
        journalVoucherService.approveVoucher(inputObject, outputObject);
    }

    @ApiOperation(id = "postJournalVoucher", value = "过账会计凭证", method = "POST", allUse = "1")
    @ApiImplicitParams({@ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/JournalVoucherController/postJournalVoucher")
    public void postJournalVoucher(InputObject inputObject, OutputObject outputObject) {
        journalVoucherService.postVoucher(inputObject, outputObject);
    }

    @ApiOperation(id = "reverseJournalVoucher", value = "红字冲销会计凭证", method = "POST", allUse = "1")
    @ApiImplicitParams({@ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/JournalVoucherController/reverseJournalVoucher")
    public void reverseJournalVoucher(InputObject inputObject, OutputObject outputObject) {
        journalVoucherService.reverseVoucher(inputObject, outputObject);
    }

    @ApiOperation(id = "queryDetailLedger", value = "查询明细账", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "setOfBooksId", name = "setOfBooksId", value = "账套id", required = "required"),
        @ApiImplicitParam(id = "periodCode", name = "periodCode", value = "期间", required = "required"),
        @ApiImplicitParam(id = "subjectId", name = "subjectId", value = "科目id"),
        @ApiImplicitParam(id = "auxCustomerId", name = "auxCustomerId", value = "辅助客户id"),
        @ApiImplicitParam(id = "auxSupplierId", name = "auxSupplierId", value = "辅助供应商id")
    })
    @RequestMapping("/post/JournalVoucherController/queryDetailLedger")
    public void queryDetailLedger(InputObject inputObject, OutputObject outputObject) {
        journalVoucherService.queryDetailLedger(inputObject, outputObject);
    }
}
