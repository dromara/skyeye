/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.pay.service;

import javax.servlet.http.HttpServletRequest;

/**
 * 支付渠道异步回调（由 PayNotifyOpenController 调用）。
 */
public interface PayNotifyService {

    /**
     * 处理渠道支付结果通知：验签解析后，成功则转发至 PayApp.orderNotifyUrl。
     *
     * @return 返回给渠道的 ack 字符串
     */
    String notifyOrder(String channelId, HttpServletRequest request);

}
