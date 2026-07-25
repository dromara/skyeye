/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.pay.core.dto.transfer;

import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import lombok.Data;

import java.util.Map;

import com.skyeye.pay.enums.PayTransferType;

/**
 * @ClassName: PayTransferUnifiedReqDTO
 * @Description: 统一转账
 * @author: skyeye云系列--卫志强
 * @date: 2024/9/10 9:11
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@ApiModel("统一转账")
public class PayTransferUnifiedReqDTO {

    @ApiModelProperty(value = "转账类型", required = "required", enumClass = PayTransferType.class)
    private Integer type;

    @ApiModelProperty(value = "用户 IP", required = "required")
    private String userIp;

    @ApiModelProperty(value = "外部转账单编号", required = "required")
    private String outTransferNo;

    @ApiModelProperty(value = "转账金额，单位：分", required = "required")
    private String price;

    @ApiModelProperty(value = "转账标题，长度不能超过 128", required = "required")
    private String subject;

    @ApiModelProperty(value = "收款人姓名", required = "required")
    private String userName;

    @ApiModelProperty(value = "支付宝登录号", required = "required")
    private String alipayLogonId;

    @ApiModelProperty(value = "微信 openId", required = "required")
    private String openid;

    @ApiModelProperty(value = "支付渠道的额外参数")
    private Map<String, String> channelExtras;
}
