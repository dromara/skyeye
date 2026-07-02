/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.pay.service.impl;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.pay.core.PayClient;
import com.skyeye.pay.core.dto.order.PayOrderRespDTO;
import com.skyeye.pay.entity.PayApp;
import com.skyeye.pay.entity.PayChannel;
import com.skyeye.pay.enums.PayOrderStatusResp;
import com.skyeye.pay.enums.PayType;
import com.skyeye.pay.service.PayAppService;
import com.skyeye.pay.service.PayChannelService;
import com.skyeye.pay.service.PayNotifyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 支付渠道异步回调处理：验签 → 转发业务 notify。
 * <p>
 * 本类只做「支付网关」职责，不直接改业务订单；订单状态变更由各业务 notify 接口完成。
 */
@Slf4j
@Service
public class PayNotifyServiceImpl implements PayNotifyService {

    @Autowired
    private PayChannelService payChannelService;

    @Autowired
    private PayAppService payAppService;

    @Override
    public String notifyOrder(String channelId, HttpServletRequest request) {
        // channelId 来自 URL 路径，与发起支付时注册到渠道的 notify 地址一致
        PayChannel payChannel = payChannelService.selectById(channelId);
        if (ObjectUtil.isEmpty(payChannel)) {
            throw new CustomException("支付渠道不存在");
        }
        PayClient client = payChannelService.getPayClient(channelId);
        PayOrderRespDTO notify = client.parseOrderNotify(getRequestParams(request), readRequestBody(request));
        log.info("[notifyOrder][channelId({}) outTradeNo({}) status({})]", channelId,
            notify.getOutTradeNo(), notify.getStatus());

        if (PayOrderStatusResp.isSuccess(notify.getStatus())) {
            // 仅支付成功时转发；WAITING/CLOSED 等状态不通知业务，避免误改订单
            payAppService.setDataMation(payChannel, PayChannel::getAppId);
            forwardToBusiness(payChannel.getAppMation(), payChannel.getCodeNum(), notify);
        }
        // 无论业务转发是否异常，均需按渠道要求返回 ack，否则渠道会重复回调
        return buildNotifySuccessResponse(payChannel.getCodeNum());
    }

    private void forwardToBusiness(PayApp payApp, String channelCode, PayOrderRespDTO notify) {
        // outTradeNo 即业务 oddNumber，各业务 notify 接口按此字段查单
        String businessNotifyUrl = payAppService.getBusinessOrderNotifyUrl(payApp);
        Map<String, Object> notifyParams = new HashMap<>();
        notifyParams.put("outTradeNo", notify.getOutTradeNo());
        notifyParams.put("channelCode", channelCode);
        notifyParams.put("channelOrderNo", notify.getChannelOrderNo());
        if (notify.getSuccessTime() != null) {
            notifyParams.put("successTime", notify.getSuccessTime().toString());
        }
        try {
            String response = HttpUtil.post(businessNotifyUrl, notifyParams);
            log.info("[forwardToBusiness][url({}) outTradeNo({}) response({})]",
                businessNotifyUrl, notify.getOutTradeNo(), response);
        } catch (Exception ex) {
            log.error("[forwardToBusiness][url({}) outTradeNo({}) 转发失败]", businessNotifyUrl, notify.getOutTradeNo(), ex);
            throw new CustomException("支付业务回调失败");
        }
    }

    private Map<String, String> getRequestParams(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Enumeration<String> names = request.getParameterNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            params.put(name, request.getParameter(name));
        }
        return params;
    }

    private String readRequestBody(HttpServletRequest request) {
        try {
            return IoUtil.read(request.getInputStream(), StandardCharsets.UTF_8);
        } catch (Exception ex) {
            return StrUtil.EMPTY;
        }
    }

    private String buildNotifySuccessResponse(String channelCode) {
        // 各支付渠道要求的成功应答格式不同，需原样返回以免重复通知
        if (PayType.isAlipay(channelCode)) {
            return "success";
        }
        if (channelCode != null && channelCode.startsWith("wx_")) {
            Map<String, String> result = new HashMap<>();
            result.put("code", "SUCCESS");
            result.put("message", "成功");
            return JSONUtil.toJsonStr(result);
        }
        return "success";
    }
}
