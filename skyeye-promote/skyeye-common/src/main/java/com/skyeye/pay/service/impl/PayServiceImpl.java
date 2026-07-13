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
 * 业务侧传入 oddNumber、payPrice（分）、appKey 等，notifyUrl 留空时由渠道所属 PayApp 自动解析回调地址。
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
        String returnUrl = resolveOptionalParam(params, "returnUrl");
        String channelExtrasStr = resolveOptionalParam(params, "channelExtras");
        String notifyUrl = resolveOptionalParam(params, "notifyUrl");
        String appKey = resolveRequiredAppKey(params);
        String userId = inputObject.getLogParams().get(CommonConstants.ID).toString();
        Map<String, Object> result = executePayment(data, channelCode, returnUrl, channelExtrasStr, notifyUrl, appKey, userId);
        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public Map<String, Object> executePayment(Map<String, Object> data, String channelCode, String returnUrl,
                                              String channelExtras, String notifyUrl, String appKey) {
        return executePayment(data, channelCode, returnUrl, channelExtras, notifyUrl, appKey, null);
    }

    private Map<String, Object> executePayment(Map<String, Object> data, String channelCode, String returnUrl,
                                               String channelExtrasStr, String notifyUrl, String appKey, String userId) {
        payAppService.getEnabledPayAppByAppKey(appKey);
        PayChannel payChannel = payChannelService.getPayChannelByCode(appKey, channelCode);
        payAppService.setDataMation(payChannel, PayChannel::getAppId);
        PayClient client = payChannelService.getPayClient(payChannel.getId());

        PayOrderUnifiedReqDTO unifiedReqDTO = new PayOrderUnifiedReqDTO();
        unifiedReqDTO.setOutTradeNo(data.get("oddNumber").toString());
        unifiedReqDTO.setSubject(getPaySubject(data));
        unifiedReqDTO.setBody(getPayBody(data));
        unifiedReqDTO.setNotifyUrl(resolveChannelNotifyUrl(payChannel.getAppMation(), payChannel.getId(), notifyUrl));
        unifiedReqDTO.setReturnUrl(returnUrl);
        unifiedReqDTO.setPrice(data.get("payPrice").toString());
        unifiedReqDTO.setExpireTime(LocalDateTimeUtils.addTime(Duration.ofHours(24L)));

        if (Objects.equals(channelCode, PayType.WALLET.getKey()) && StrUtil.isNotBlank(userId)) {
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
        log.info("[executePayment][appKey({}) outTradeNo({}) channel({}) status({})]",
            appKey, data.get("oddNumber"), channelCode, payOrderRespDTO.getStatus());

        Map<String, Object> result = new HashMap<>();
        result.put("payChannel", JSONUtil.toJsonStr(payChannel));
        result.put("payOrderRespDTO", JSONUtil.toJsonStr(payOrderRespDTO));
        return result;
    }

    private String resolveOptionalParam(Map<String, Object> params, String key) {
        if (params.containsKey(key) && params.get(key) != null) {
            return params.get(key).toString();
        }
        return StrUtil.EMPTY;
    }

    private String resolveRequiredAppKey(Map<String, Object> params) {
        if (!params.containsKey("appKey") || StrUtil.isBlank(params.get("appKey").toString())) {
            throw new CustomException("支付应用标识(appKey)不能为空");
        }
        return params.get("appKey").toString().trim();
    }

    private String resolveChannelNotifyUrl(PayApp payApp, String channelId, String notifyUrlParam) {
        if (StrUtil.isNotBlank(notifyUrlParam)) {
            return notifyUrlParam;
        }
        return payAppService.buildChannelOrderNotifyUrl(payApp, channelId);
    }

    private void validatePayResponse(PayOrderRespDTO payOrderRespDTO) {
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
