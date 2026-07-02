/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.pay.controller;

import com.skyeye.pay.service.PayNotifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 支付渠道开放回调入口（无需登录）。
 * <p>
 * 完整地址 = PayApp.channelNotifyUrl + /{channelId}，需在网关白名单放行 /pay/notify/**
 */
@RestController
@RequestMapping("/pay/notify")
public class PayNotifyOpenController {

    @Autowired
    private PayNotifyService payNotifyService;

    /**
     * 微信等平台通常 POST；支付宝部分场景走 GET，故两种都支持
     */
    @PostMapping("/order/{channelId}")
    public String notifyOrderPost(@PathVariable("channelId") String channelId, HttpServletRequest request) {
        return payNotifyService.notifyOrder(channelId, request);
    }

    @RequestMapping(value = "/order/{channelId}", method = org.springframework.web.bind.annotation.RequestMethod.GET)
    public String notifyOrderGet(@PathVariable("channelId") String channelId, HttpServletRequest request) {
        return payNotifyService.notifyOrder(channelId, request);
    }
}
