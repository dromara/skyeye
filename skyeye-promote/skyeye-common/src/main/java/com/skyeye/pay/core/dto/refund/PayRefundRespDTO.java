/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.pay.core.dto.refund;

import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.Property;
import com.skyeye.pay.enums.PayRefundStatusResp;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @ClassName: PayRefundRespDTO
 * @Description: 渠道退款订单 Response DTO
 * @author: skyeye云系列--卫志强
 * @date: 2024/9/10 8:39
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@ApiModel("渠道退款订单 Response DTO")
public class PayRefundRespDTO {

    @Property(value = "退款状态", enumClass = PayRefundStatusResp.class)
    private Integer status;

    @Property(value = "外部退款号，对应 PayRefundDO 的 no 字段")
    private String outRefundNo;

    @Property(value = "渠道退款单号，对应 PayRefundDO.channelRefundNo 字段")
    private String channelRefundNo;

    @Property(value = "退款成功时间")
    private LocalDateTime successTime;

    @Property(value = "原始的异步通知结果")
    private Object rawData;

    @Property(value = "调用渠道的错误码。这里返回的是业务异常，而是不系统异常。如果是系统异常，则会抛出异常")
    private String channelErrorCode;

    @Property(value = "调用渠道的错误信息")
    private String channelErrorMsg;

    /**
     * 创建【WAITING】状态的退款返回
     */
    public static PayRefundRespDTO waitingOf(String channelRefundNo,
                                             String outRefundNo, Object rawData) {
        PayRefundRespDTO respDTO = new PayRefundRespDTO();
        respDTO.setStatus(PayRefundStatusResp.WAITING.getKey());
        respDTO.setChannelRefundNo(channelRefundNo);
        // 相对通用的字段
        respDTO.setOutRefundNo(outRefundNo);
        respDTO.setRawData(rawData);
        return respDTO;
    }

    /**
     * 创建【SUCCESS】状态的退款返回
     */
    public static PayRefundRespDTO successOf(String channelRefundNo, LocalDateTime successTime,
                                             String outRefundNo, Object rawData) {
        PayRefundRespDTO respDTO = new PayRefundRespDTO();
        respDTO.setStatus(PayRefundStatusResp.SUCCESS.getKey());
        respDTO.setChannelRefundNo(channelRefundNo);
        respDTO.setSuccessTime(successTime);
        // 相对通用的字段
        respDTO.setOutRefundNo(outRefundNo);
        respDTO.setRawData(rawData);
        return respDTO;
    }

    /**
     * 创建【FAILURE】状态的退款返回
     */
    public static PayRefundRespDTO failureOf(String outRefundNo, Object rawData) {
        return failureOf(null, null,
            outRefundNo, rawData);
    }

    /**
     * 创建【FAILURE】状态的退款返回
     */
    public static PayRefundRespDTO failureOf(String channelErrorCode, String channelErrorMsg,
                                             String outRefundNo, Object rawData) {
        PayRefundRespDTO respDTO = new PayRefundRespDTO();
        respDTO.setStatus(PayRefundStatusResp.FAILURE.getKey());
        respDTO.setChannelErrorCode(channelErrorCode);
        respDTO.setChannelErrorMsg(channelErrorMsg);
        // 相对通用的字段
        respDTO.setOutRefundNo(outRefundNo);
        respDTO.setRawData(rawData);
        return respDTO;
    }

}
