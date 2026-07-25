/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.pay.core.dto.transfer;

import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.Property;
import com.skyeye.pay.enums.PayTransferStatusResp;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @ClassName: PayTransferRespDTO
 * @Description: 统一转账
 * @author: skyeye云系列--卫志强
 * @date: 2024/9/10 8:48
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@ApiModel("统一转账")
public class PayTransferRespDTO {

    @Property(value = "转账状态", enumClass = PayTransferStatusResp.class)
    private Integer status;

    @Property(value = "外部转账单号")
    private String outTransferNo;

    @Property(value = "支付渠道编号")
    private String channelTransferNo;

    @Property(value = "支付成功时间")
    private LocalDateTime successTime;

    @Property(value = "原始的返回结果")
    private Object rawData;

    @Property(value = "调用渠道的错误码")
    private String channelErrorCode;

    @Property(value = "调用渠道的错误信息")
    private String channelErrorMsg;

    /**
     * 创建【WAITING】状态的转账返回
     */
    public static PayTransferRespDTO waitingOf(String channelTransferNo,
                                               String outTransferNo, Object rawData) {
        PayTransferRespDTO respDTO = new PayTransferRespDTO();
        respDTO.setStatus(PayTransferStatusResp.WAITING.getKey());
        respDTO.setChannelTransferNo(channelTransferNo);
        respDTO.setOutTransferNo(outTransferNo);
        respDTO.setRawData(rawData);
        return respDTO;
    }

    /**
     * 创建【IN_PROGRESS】状态的转账返回
     */
    public static PayTransferRespDTO dealingOf(String channelTransferNo,
                                               String outTransferNo, Object rawData) {
        PayTransferRespDTO respDTO = new PayTransferRespDTO();
        respDTO.setStatus(PayTransferStatusResp.IN_PROGRESS.getKey());
        respDTO.setChannelTransferNo(channelTransferNo);
        respDTO.setOutTransferNo(outTransferNo);
        respDTO.setRawData(rawData);
        return respDTO;
    }

    /**
     * 创建【CLOSED】状态的转账返回
     */
    public static PayTransferRespDTO closedOf(String channelErrorCode, String channelErrorMsg,
                                              String outTransferNo, Object rawData) {
        PayTransferRespDTO respDTO = new PayTransferRespDTO();
        respDTO.setStatus(PayTransferStatusResp.CLOSED.getKey());
        respDTO.setChannelErrorCode(channelErrorCode);
        respDTO.setChannelErrorMsg(channelErrorMsg);
        // 相对通用的字段
        respDTO.setOutTransferNo(outTransferNo);
        respDTO.setRawData(rawData);
        return respDTO;
    }

    /**
     * 创建【SUCCESS】状态的转账返回
     */
    public static PayTransferRespDTO successOf(String channelTransferNo, LocalDateTime successTime,
                                               String outTransferNo, Object rawData) {
        PayTransferRespDTO respDTO = new PayTransferRespDTO();
        respDTO.setStatus(PayTransferStatusResp.SUCCESS.getKey());
        respDTO.setChannelTransferNo(channelTransferNo);
        respDTO.setSuccessTime(successTime);
        // 相对通用的字段
        respDTO.setOutTransferNo(outTransferNo);
        respDTO.setRawData(rawData);
        return respDTO;
    }

}
