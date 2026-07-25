/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.pay.core.dto.order;

import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.Property;
import com.skyeye.pay.enums.PayOrderStatusResp;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

import com.skyeye.pay.enums.PayOrderDisplayMode;

/**
 * @ClassName: PayOrderRespDTO
 * @Description: 渠道支付订单 Response DTO
 * @author: skyeye云系列--卫志强
 * @date: 2024/9/10 8:26
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@Accessors(chain = true)
@ApiModel("渠道支付订单 Response DTO")
public class PayOrderRespDTO {

    @Property(value = "支付状态", enumClass = PayOrderStatusResp.class)
    private Integer status;

    @Property("外部订单号，对应 PayOrderExtensionDO 的 no 字段")
    private String outTradeNo;

    @Property("支付渠道编号")
    private String channelOrderNo;

    @Property("支付渠道用户编号")
    private String channelUserId;

    @Property("支付成功时间")
    private LocalDateTime successTime;

    @Property("原始的同步/异步通知结果")
    private Object rawData;

    // ========== 主动发起支付时，会返回的字段 ==========

    @Property(value = "展示模式", enumClass = PayOrderDisplayMode.class)
    private String displayMode;

    @Property("展示内容")
    private String displayContent;

    @Property("调用渠道的错误码【这里返回的是业务异常，而是不系统异常。如果是系统异常，则会抛出】")
    private String channelErrorCode;

    @Property("调用渠道的错误信息")
    private String channelErrorMsg;

    /**
     * 创建【WAITING】状态的订单返回
     */
    public static PayOrderRespDTO waitingOf(String displayMode, String displayContent,
                                            String outTradeNo, Object rawData) {
        PayOrderRespDTO respDTO = new PayOrderRespDTO();
        respDTO.setStatus(PayOrderStatusResp.WAITING.getKey());
        respDTO.setDisplayMode(displayMode);
        respDTO.setDisplayContent(displayContent);
        // 相对通用的字段
        respDTO.setOutTradeNo(outTradeNo);
        respDTO.setRawData(rawData);
        return respDTO;
    }

    /**
     * 创建【SUCCESS】状态的订单返回
     */
    public static PayOrderRespDTO successOf(String channelOrderNo, String channelUserId, LocalDateTime successTime,
                                            String outTradeNo, Object rawData) {
        PayOrderRespDTO respDTO = new PayOrderRespDTO();
        respDTO.setStatus(PayOrderStatusResp.SUCCESS.getKey());
        respDTO.setChannelOrderNo(channelOrderNo);
        respDTO.setChannelUserId(channelUserId);
        respDTO.setSuccessTime(successTime);
        // 相对通用的字段
        respDTO.setOutTradeNo(outTradeNo);
        respDTO.setRawData(rawData);
        return respDTO;
    }

    /**
     * 创建指定状态的订单返回，适合支付渠道回调时
     */
    public static PayOrderRespDTO of(Integer status, String channelOrderNo, String channelUserId, LocalDateTime successTime,
                                     String outTradeNo, Object rawData) {
        PayOrderRespDTO respDTO = new PayOrderRespDTO();
        respDTO.setStatus(status);
        respDTO.setChannelOrderNo(channelOrderNo);
        respDTO.setChannelUserId(channelUserId);
        respDTO.setSuccessTime(successTime);
        // 相对通用的字段
        respDTO.setOutTradeNo(outTradeNo);
        respDTO.setRawData(rawData);
        return respDTO;
    }

    /**
     * 创建【CLOSED】状态的订单返回，适合调用支付渠道失败时
     */
    public static PayOrderRespDTO closedOf(String channelErrorCode, String channelErrorMsg,
                                           String outTradeNo, Object rawData) {
        PayOrderRespDTO respDTO = new PayOrderRespDTO();
        respDTO.setStatus(PayOrderStatusResp.CLOSED.getKey());
        respDTO.setChannelErrorCode(channelErrorCode);
        respDTO.setChannelErrorMsg(channelErrorMsg);
        // 相对通用的字段
        respDTO.setOutTradeNo(outTradeNo);
        respDTO.setRawData(rawData);
        return respDTO;
    }

}
