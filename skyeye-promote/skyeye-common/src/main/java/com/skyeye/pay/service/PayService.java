/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.pay.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

import java.util.Map;

/**
 * 统一支付接口（租户购买、商城订单等共用）。
 * <p>
 * data 约定：oddNumber（外部订单号）、payPrice（金额，单位分）；可选 subject、body。
 */
public interface PayService {

    void payment(InputObject inputObject, OutputObject outputObject);

    void generatePayRrCode(InputObject inputObject, OutputObject outputObject);

    /**
     * 发起统一支付。
     *
     * @param notifyUrl 注册到渠道的回调地址；传空则按渠道所属 PayApp.channelNotifyUrl 自动拼接
     */
    Map<String, Object> executePayment(Map<String, Object> data, String channelCode, String returnUrl,
                                       String channelExtras, String notifyUrl);

    /**
     * 生成支付二维码。notifyUrl 为空时自动从 PayApp.channelNotifyUrl 解析。
     */
    Map<String, Object> executeGeneratePayQrCode(Map<String, Object> data, String channelCode, String ip,
                                                 String notifyUrl);

}
