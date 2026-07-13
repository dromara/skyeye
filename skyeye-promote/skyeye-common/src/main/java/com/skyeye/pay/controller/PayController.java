/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.pay.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.pay.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: PayController
 * @Description: 统一支付
 * @author: skyeye云系列--卫志强
 * @date: 2024/11/21 8:45
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "统一支付", tags = "统一支付", modelName = "统一支付")
public class PayController {

    @Autowired
    private PayService payService;

    @ApiOperation(id = "payment", value = "统一支付", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "data", name = "data", value = "业务数据", required = "required,json"),
        @ApiImplicitParam(id = "channelCode", name = "channelCode", value = "支付渠道编码", required = "required"),
        @ApiImplicitParam(id = "appKey", name = "appKey", value = "支付应用标识，如 tenant-buy、mall-order", required = "required"),
        @ApiImplicitParam(id = "returnUrl", name = "returnUrl", value = "支付结果的return回调地址必须是URL格式"),
        @ApiImplicitParam(id = "channelExtras", name = "channelExtras", value = "支付渠道的额外参数，例如说，微信公众号需要传递 openid 参数", required = "json"),
        @ApiImplicitParam(id = "notifyUrl", name = "notifyUrl", value = "回调地址，支付成功后通知商户的地址，必须是URL格式")})
    @RequestMapping("/post/PayController/payment")
    public void payment(InputObject inputObject, OutputObject outputObject) {
        payService.payment(inputObject, outputObject);
    }

}
