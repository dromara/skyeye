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
import com.skyeye.pay.core.dto.refund.PayRefundRespDTO;
import com.skyeye.pay.core.dto.refund.PayRefundUnifiedReqDTO;
import com.skyeye.pay.core.dto.transfer.PayTransferRespDTO;
import com.skyeye.pay.core.dto.transfer.PayTransferUnifiedReqDTO;
import com.skyeye.pay.entity.PayApp;
import com.skyeye.pay.entity.PayChannel;
import com.skyeye.pay.enums.PayOrderStatusResp;
import com.skyeye.pay.enums.PayTransferType;
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

    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void refund(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        Map<String, Object> data = JSONUtil.toBean(params.get("data").toString(), null);
        String channelCode = params.get("channelCode").toString();
        String notifyUrl = resolveOptionalParam(params, "notifyUrl");
        String appKey = resolveRequiredAppKey(params);
        Map<String, Object> result = executeRefund(data, channelCode, notifyUrl, appKey);
        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public Map<String, Object> executeRefund(Map<String, Object> data, String channelCode, String notifyUrl, String appKey) {
        payAppService.getEnabledPayAppByAppKey(appKey);
        PayChannel payChannel = payChannelService.getPayChannelByCode(appKey, channelCode);
        payAppService.setDataMation(payChannel, PayChannel::getAppId);
        PayClient client = payChannelService.getPayClient(payChannel.getId());

        if (data.get("oddNumber") == null || StrUtil.isBlank(data.get("oddNumber").toString())) {
            throw new CustomException("原支付单号(oddNumber)不能为空");
        }
        if (data.get("outRefundNo") == null || StrUtil.isBlank(data.get("outRefundNo").toString())) {
            throw new CustomException("退款单号(outRefundNo)不能为空");
        }
        if (data.get("refundPrice") == null || StrUtil.isBlank(data.get("refundPrice").toString())) {
            throw new CustomException("退款金额不能为空");
        }

        PayRefundUnifiedReqDTO reqDTO = new PayRefundUnifiedReqDTO();
        reqDTO.setOutTradeNo(data.get("oddNumber").toString());
        reqDTO.setOutRefundNo(data.get("outRefundNo").toString());
        reqDTO.setReason(data.get("reason") != null ? data.get("reason").toString() : "退款");
        reqDTO.setPayPrice(data.get("payPrice") != null ? data.get("payPrice").toString() : data.get("refundPrice").toString());
        reqDTO.setRefundPrice(data.get("refundPrice").toString());
        reqDTO.setNotifyUrl(resolveChannelRefundNotifyUrl(payChannel.getAppMation(), payChannel.getId(), notifyUrl));

        PayRefundRespDTO refundRespDTO = client.unifiedRefund(reqDTO);
        if (refundRespDTO == null) {
            throw new CustomException("发起退款失败，请稍后重试");
        }
        if (StrUtil.isNotEmpty(refundRespDTO.getChannelErrorCode())) {
            throw new CustomException(String.format("发起退款报错，错误码：%s，错误提示：%s",
                refundRespDTO.getChannelErrorCode(), refundRespDTO.getChannelErrorMsg()));
        }
        log.info("[executeRefund][appKey({}) outTradeNo({}) outRefundNo({}) status({})]",
            appKey, reqDTO.getOutTradeNo(), reqDTO.getOutRefundNo(), refundRespDTO.getStatus());

        Map<String, Object> result = new HashMap<>();
        result.put("payChannel", JSONUtil.toJsonStr(payChannel));
        result.put("payRefundRespDTO", JSONUtil.toJsonStr(refundRespDTO));
        return result;
    }

    private String resolveChannelRefundNotifyUrl(PayApp payApp, String channelId, String notifyUrlParam) {
        if (StrUtil.isNotBlank(notifyUrlParam)) {
            return notifyUrlParam;
        }
        return payAppService.buildChannelRefundNotifyUrl(payApp, channelId);
    }

    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void transfer(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        Map<String, Object> data = JSONUtil.toBean(params.get("data").toString(), null);
        String channelCode = params.get("channelCode").toString();
        String channelExtras = resolveOptionalParam(params, "channelExtras");
        String appKey = resolveRequiredAppKey(params);
        Map<String, Object> result = executeTransfer(data, channelCode, channelExtras, appKey);
        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public Map<String, Object> executeTransfer(Map<String, Object> data, String channelCode, String channelExtras, String appKey) {
        payAppService.getEnabledPayAppByAppKey(appKey);
        PayChannel payChannel = payChannelService.getPayChannelByCode(appKey, channelCode);
        payAppService.setDataMation(payChannel, PayChannel::getAppId);
        PayClient client = payChannelService.getPayClient(payChannel.getId());

        if (data.get("outTransferNo") == null || StrUtil.isBlank(data.get("outTransferNo").toString())) {
            throw new CustomException("转账单号(outTransferNo)不能为空");
        }
        if (data.get("price") == null || StrUtil.isBlank(data.get("price").toString())) {
            throw new CustomException("转账金额不能为空");
        }
        if (data.get("type") == null || StrUtil.isBlank(data.get("type").toString())) {
            throw new CustomException("转账类型不能为空");
        }
        if (data.get("userName") == null || StrUtil.isBlank(data.get("userName").toString())) {
            throw new CustomException("收款人姓名不能为空");
        }

        PayTransferUnifiedReqDTO reqDTO = new PayTransferUnifiedReqDTO();
        reqDTO.setOutTransferNo(data.get("outTransferNo").toString());
        reqDTO.setPrice(data.get("price").toString());
        reqDTO.setType(Integer.parseInt(data.get("type").toString()));
        reqDTO.setSubject(data.get("subject") != null ? data.get("subject").toString() : "门店提现");
        reqDTO.setUserName(data.get("userName").toString());
        reqDTO.setUserIp(data.get("userIp") != null ? data.get("userIp").toString() : "127.0.0.1");
        if (data.get("alipayLogonId") != null) {
            reqDTO.setAlipayLogonId(data.get("alipayLogonId").toString());
        }
        if (data.get("openid") != null) {
            reqDTO.setOpenid(data.get("openid").toString());
        }
        if (data.get("bankAccountNo") != null) {
            reqDTO.setBankAccountNo(data.get("bankAccountNo").toString());
        }
        Map<String, String> extras = StrUtil.isBlank(channelExtras) ? Maps.newHashMap() : JSONUtil.toBean(channelExtras, null);
        if (data.get("channelExtras") != null && StrUtil.isNotBlank(data.get("channelExtras").toString())) {
            Map<String, String> dataExtras = JSONUtil.toBean(data.get("channelExtras").toString(), null);
            if (dataExtras != null) {
                extras.putAll(dataExtras);
            }
        }
        reqDTO.setChannelExtras(extras);

        PayTransferRespDTO transferRespDTO = client.unifiedTransfer(reqDTO);
        if (transferRespDTO == null) {
            throw new CustomException("发起转账失败，请稍后重试");
        }
        log.info("[executeTransfer][appKey({}) outTransferNo({}) channel({}) status({})]",
            appKey, reqDTO.getOutTransferNo(), channelCode, transferRespDTO.getStatus());

        Map<String, Object> result = new HashMap<>();
        result.put("payChannel", JSONUtil.toJsonStr(payChannel));
        result.put("payTransferRespDTO", JSONUtil.toJsonStr(transferRespDTO));
        return result;
    }

    @Override
    public void getTransfer(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        Map<String, Object> data = JSONUtil.toBean(params.get("data").toString(), null);
        String channelCode = params.get("channelCode").toString();
        String appKey = resolveRequiredAppKey(params);
        Map<String, Object> result = executeGetTransfer(data, channelCode, appKey);
        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public Map<String, Object> executeGetTransfer(Map<String, Object> data, String channelCode, String appKey) {
        payAppService.getEnabledPayAppByAppKey(appKey);
        PayChannel payChannel = payChannelService.getPayChannelByCode(appKey, channelCode);
        PayClient client = payChannelService.getPayClient(payChannel.getId());

        if (data.get("outTransferNo") == null || StrUtil.isBlank(data.get("outTransferNo").toString())) {
            throw new CustomException("转账单号(outTransferNo)不能为空");
        }
        Integer type = data.get("type") != null ? Integer.parseInt(data.get("type").toString())
            : PayTransferType.BANK_CARD.getKey();
        PayTransferType transferType = PayTransferType.typeOf(type);
        if (transferType == null) {
            throw new CustomException("不支持的转账类型");
        }
        PayTransferRespDTO transferRespDTO = client.getTransfer(data.get("outTransferNo").toString(), transferType);
        if (transferRespDTO == null) {
            throw new CustomException("查询转账失败，请稍后重试");
        }
        Map<String, Object> result = new HashMap<>();
        result.put("payChannel", JSONUtil.toJsonStr(payChannel));
        result.put("payTransferRespDTO", JSONUtil.toJsonStr(transferRespDTO));
        return result;
    }
}
