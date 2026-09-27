/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.pay.core.service.impl.mock;

import com.skyeye.pay.core.dto.order.PayOrderRespDTO;
import com.skyeye.pay.core.dto.order.PayOrderUnifiedReqDTO;
import com.skyeye.pay.core.dto.refund.PayRefundRespDTO;
import com.skyeye.pay.core.dto.refund.PayRefundUnifiedReqDTO;
import com.skyeye.pay.core.dto.transfer.PayTransferRespDTO;
import com.skyeye.pay.core.dto.transfer.PayTransferUnifiedReqDTO;
import com.skyeye.pay.core.service.AbstractPayClient;
import com.skyeye.pay.core.service.NonePayClientConfig;
import com.skyeye.pay.enums.PayTransferType;
import com.skyeye.pay.enums.PayType;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @ClassName: MockPayClient
 * @Description: 模拟支付的 PayClient 实现类
 * 模拟支付返回结果都是成功，方便大家日常流畅
 * @author: skyeye云系列--卫志强
 * @date: 2024/9/10 19:22
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class MockPayClient extends AbstractPayClient<NonePayClientConfig> {

    private static final String MOCK_RESP_SUCCESS_DATA = "MOCK_SUCCESS";

    public MockPayClient(String channelId, NonePayClientConfig config) {
        super(channelId, PayType.MOCK.getKey(), config);
    }

    @Override
    protected void doInit() {
    }

    @Override
    protected PayOrderRespDTO doUnifiedOrder(PayOrderUnifiedReqDTO reqDTO) {
        return PayOrderRespDTO.successOf("MOCK-P-" + reqDTO.getOutTradeNo(), "", LocalDateTime.now(),
            reqDTO.getOutTradeNo(), MOCK_RESP_SUCCESS_DATA);
    }

    @Override
    protected PayOrderRespDTO doGetOrder(String outTradeNo) {
        return PayOrderRespDTO.successOf("MOCK-P-" + outTradeNo, "", LocalDateTime.now(),
            outTradeNo, MOCK_RESP_SUCCESS_DATA);
    }

    @Override
    protected PayRefundRespDTO doUnifiedRefund(PayRefundUnifiedReqDTO reqDTO) {
        return PayRefundRespDTO.successOf("MOCK-R-" + reqDTO.getOutRefundNo(), LocalDateTime.now(),
            reqDTO.getOutRefundNo(), MOCK_RESP_SUCCESS_DATA);
    }

    @Override
    protected PayRefundRespDTO doGetRefund(String outTradeNo, String outRefundNo) {
        return PayRefundRespDTO.successOf("MOCK-R-" + outRefundNo, LocalDateTime.now(),
            outRefundNo, MOCK_RESP_SUCCESS_DATA);
    }

    @Override
    protected PayRefundRespDTO doParseRefundNotify(Map<String, String> params, String body) {
        throw new UnsupportedOperationException("模拟支付无退款回调");
    }

    @Override
    protected PayOrderRespDTO doParseOrderNotify(Map<String, String> params, String body) {
        throw new UnsupportedOperationException("模拟支付无支付回调");
    }

    @Override
    protected PayTransferRespDTO doUnifiedTransfer(PayTransferUnifiedReqDTO reqDTO) {
        return PayTransferRespDTO.successOf("mock_transfer_" + reqDTO.getOutTransferNo(),
            java.time.LocalDateTime.now(), reqDTO.getOutTransferNo(), "mock-transfer-success");
    }

    @Override
    protected PayTransferRespDTO doGetTransfer(String outTradeNo, PayTransferType type) {
        return PayTransferRespDTO.successOf("mock_transfer_" + outTradeNo,
            java.time.LocalDateTime.now(), outTradeNo, "mock-transfer-query");
    }

    @Override
    public String generateRrCode(String outTradeNo, String body, String totalFee, String ip, String notifyUrl) {
        return null;
    }
}
