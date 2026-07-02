/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.pay.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Maps;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.LocalDateTimeUtils;
import com.skyeye.exception.CustomException;
import com.skyeye.pay.core.PayClient;
import com.skyeye.pay.core.dto.order.PayOrderRespDTO;
import com.skyeye.pay.core.dto.order.PayOrderUnifiedReqDTO;
import com.skyeye.pay.entity.PayApp;
import com.skyeye.pay.entity.PayChannel;
import com.skyeye.pay.enums.PayOrderStatusResp;
import com.skyeye.pay.enums.PayType;
import com.skyeye.pay.service.PayAppService;
import com.skyeye.pay.service.PayChannelService;
import com.skyeye.pay.service.PayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 统一支付：租户购买、商城订单等共用。
 * <p>
 * 业务侧传入 oddNumber、payPrice（分）等，notifyUrl 留空时由渠道所属 PayApp 自动解析回调地址。
 * 同步成功（如 mock）由业务方立即落单；异步（二维码等）依赖 PayNotify → 业务 notify 接口。
 */
@Service
@SkyeyeService(name = "统一支付", groupName = "统一支付", tenant = TenantEnum.NO_ISOLATION)
public class PayServiceImpl implements PayService {

    private static final Logger log = LoggerFactory.getLogger(PayServiceImpl.class);

    @Autowired
    private PayChannelService payChannelService;

    @Autowired
    private PayAppService payAppService;

    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void payment(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        Map<String, Object> data = JSONUtil.toBean(params.get("data").toString(), null);
        String channelCode = params.get("channelCode").toString();
        String returnUrl = params.get("returnUrl").toString();
        String channelExtrasStr = params.get("channelExtras").toString();
        String notifyUrl = params.get("notifyUrl").toString();
        String userId = inputObject.getLogParams().get(CommonConstants.ID).toString();
        Map<String, Object> result = executePayment(data, channelCode, returnUrl, channelExtrasStr, notifyUrl, userId);
        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void generatePayRrCode(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        Map<String, Object> data = JSONUtil.toBean(params.get("data").toString(), null);
        String channelCode = params.get("channelCode").toString();
        String notifyUrl = params.get("notifyUrl").toString();
        String ip = params.get("ip").toString();
        Map<String, Object> result = executeGeneratePayQrCode(data, channelCode, ip, notifyUrl);
        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public Map<String, Object> executePayment(Map<String, Object> data, String channelCode, String returnUrl,
                                              String channelExtras, String notifyUrl) {
        return executePayment(data, channelCode, returnUrl, channelExtras, notifyUrl, null);
    }

    private Map<String, Object> executePayment(Map<String, Object> data, String channelCode, String returnUrl,
                                               String channelExtrasStr, String notifyUrl, String userId) {
        // 渠道必须归属已启用的 PayApp，且渠道本身处于启用状态
        PayChannel payChannel = payChannelService.getPayChannelByCode(channelCode);
        payAppService.setDataMation(payChannel, PayChannel::getAppId);
        PayClient client = payChannelService.getPayClient(payChannel.getId());

        PayOrderUnifiedReqDTO unifiedReqDTO = new PayOrderUnifiedReqDTO();
        // outTradeNo 与各业务 oddNumber 一致，回调时用于反查订单
        unifiedReqDTO.setOutTradeNo(data.get("oddNumber").toString());
        unifiedReqDTO.setSubject(getPaySubject(data));
        unifiedReqDTO.setBody(getPayBody(data));
        unifiedReqDTO.setNotifyUrl(resolveChannelNotifyUrl(payChannel.getAppMation(), payChannel.getId(), notifyUrl));
        unifiedReqDTO.setReturnUrl(returnUrl);
        // payPrice 单位：分，与各业务传给渠道的金额约定一致
        unifiedReqDTO.setPrice(data.get("payPrice").toString());
        unifiedReqDTO.setExpireTime(LocalDateTimeUtils.addTime(Duration.ofHours(24L)));

        if (Objects.equals(channelCode, PayType.WALLET.getKey()) && StrUtil.isNotBlank(userId)) {
            // 钱包支付需携带当前用户 id
            Map<String, String> channelExtras = StrUtil.isBlank(channelExtrasStr) ?
                Maps.newHashMapWithExpectedSize(1) : JSONUtil.toBean(channelExtrasStr, null);
            channelExtras.put(CommonConstants.USER_ID_KEY, userId);
            unifiedReqDTO.setChannelExtras(channelExtras);
        } else if (StrUtil.isNotBlank(channelExtrasStr)) {
            unifiedReqDTO.setChannelExtras(JSONUtil.toBean(channelExtrasStr, null));
        }

        PayOrderRespDTO payOrderRespDTO = client.unifiedOrder(unifiedReqDTO);
        if (payOrderRespDTO == null) {
            throw new CustomException("发起支付失败，请稍后重试");
        }
        validatePayResponse(payOrderRespDTO);
        log.info("[executePayment][outTradeNo({}) channel({}) status({})]",
            data.get("oddNumber"), channelCode, payOrderRespDTO.getStatus());

        Map<String, Object> result = new HashMap<>();
        result.put("payChannel", JSONUtil.toJsonStr(payChannel));
        result.put("payOrderRespDTO", JSONUtil.toJsonStr(payOrderRespDTO));
        return result;
    }

    @Override
    public Map<String, Object> executeGeneratePayQrCode(Map<String, Object> data, String channelCode, String ip,
                                                        String notifyUrl) {
        PayChannel payChannel = payChannelService.getPayChannelByCode(channelCode);
        payAppService.setDataMation(payChannel, PayChannel::getAppId);
        PayClient client = payChannelService.getPayClient(payChannel.getId());
        String qrCodeUrl = client.generateRrCode(data.get("oddNumber").toString(), getPayBody(data),
            data.get("payPrice").toString(), ip, resolveChannelNotifyUrl(payChannel.getAppMation(), payChannel.getId(), notifyUrl));
        Map<String, Object> result = new HashMap<>();
        result.put("qrCodeUrl", qrCodeUrl);
        result.put("payChannel", JSONUtil.toJsonStr(payChannel));
        return result;
    }

    /**
     * 解析注册到第三方渠道的 notify 地址。
     * 优先使用调用方显式传入（兼容商城 yml 旧配置）；否则从渠道所属 PayApp.channelNotifyUrl 拼接 channelId。
     */
    private String resolveChannelNotifyUrl(PayApp payApp, String channelId, String notifyUrlParam) {
        if (StrUtil.isNotBlank(notifyUrlParam)) {
            return notifyUrlParam;
        }
        return payAppService.buildChannelOrderNotifyUrl(payApp, channelId);
    }

    private void validatePayResponse(PayOrderRespDTO payOrderRespDTO) {
        // CLOSED 表示渠道侧已明确失败；WAITING 表示待用户支付，由业务方根据 status 决定是否轮询或展示二维码
        if (StrUtil.isNotEmpty(payOrderRespDTO.getChannelErrorCode())) {
            throw new CustomException(String.format("发起支付报错，错误码：%s，错误提示：%s",
                payOrderRespDTO.getChannelErrorCode(), payOrderRespDTO.getChannelErrorMsg()));
        }
        if (PayOrderStatusResp.isClosed(payOrderRespDTO.getStatus())) {
            throw new CustomException("支付失败，请稍后重试");
        }
    }

    private String getPaySubject(Map<String, Object> data) {
        Object subject = data.get("subject");
        return subject != null ? subject.toString() : "购买商品";
    }

    private String getPayBody(Map<String, Object> data) {
        Object body = data.get("body");
        return body != null ? body.toString() : "购买商品信息";
    }
}
