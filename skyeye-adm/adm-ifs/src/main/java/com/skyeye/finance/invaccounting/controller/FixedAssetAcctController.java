package com.skyeye.finance.invaccounting.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.finance.invaccounting.entity.FixedAssetAcctPost;
import com.skyeye.finance.invaccounting.service.FixedAssetAcctService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "固定资产核算", tags = "固定资产核算", modelName = "财务中枢")
public class FixedAssetAcctController {

    @Autowired
    private FixedAssetAcctService fixedAssetAcctService;

    /**
     * 例外补账入口。日常月结请用资产模块「月结批量折旧」runAssetDeprecMonthly。
     */
    @ApiOperation(id = "postFixedAssetDeprecAcct", value = "固定资产折旧手工补账", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = FixedAssetAcctPost.class)
    @RequestMapping("/post/FixedAssetAcctController/postFixedAssetDeprecAcct")
    public void postFixedAssetDeprecAcct(InputObject inputObject, OutputObject outputObject) {
        fixedAssetAcctService.postDepreciation(inputObject, outputObject);
    }
}
