package com.skyeye.finance.invaccounting.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.finance.invaccounting.entity.MfgCostAcctPost;
import com.skyeye.finance.invaccounting.service.MfgCostAcctService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "生产成本核算", tags = "生产成本核算", modelName = "财务中枢")
public class MfgCostAcctController {

    @Autowired
    private MfgCostAcctService mfgCostAcctService;

    @ApiOperation(id = "postProdFinishAcct", value = "完工入库记账", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = MfgCostAcctPost.class)
    @RequestMapping("/post/MfgCostAcctController/postProdFinishAcct")
    public void postProdFinishAcct(InputObject inputObject, OutputObject outputObject) {
        mfgCostAcctService.postProdFinish(inputObject, outputObject);
    }

    @ApiOperation(id = "postMfgOverheadAcct", value = "制造费用归集记账", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = MfgCostAcctPost.class)
    @RequestMapping("/post/MfgCostAcctController/postMfgOverheadAcct")
    public void postMfgOverheadAcct(InputObject inputObject, OutputObject outputObject) {
        mfgCostAcctService.postMfgOverhead(inputObject, outputObject);
    }
}
