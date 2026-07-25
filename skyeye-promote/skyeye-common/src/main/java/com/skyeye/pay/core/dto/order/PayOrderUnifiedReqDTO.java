/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.pay.core.dto.order;

import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

import com.skyeye.pay.enums.PayOrderDisplayMode;

/**
 * @ClassName: PayOrderUnifiedReqDTO
 * @Description: 统一下单 Request DTO
 * @author: skyeye云系列--卫志强
 * @date: 2024/9/10 8:37
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@ApiModel("统一下单 Request DTO")
public class PayOrderUnifiedReqDTO {

    @ApiModelProperty(value = "用户 IP", required = "required")
    private String userIp;

    // ========== 商户相关字段 ==========

    @ApiModelProperty(value = "外部订单编号，对应 PayOrderExtensionDO 的 no 字段", required = "required")
    private String outTradeNo;

    @ApiModelProperty(value = "商品标题", required = "required")
    private String subject;

    @ApiModelProperty(value = "商品描述信息，长度不能超过128")
    private String body;

    @ApiModelProperty(value = "支付结果的 notify 回调地址", required = "required")
    private String notifyUrl;

    @ApiModelProperty(value = "支付结果的 return 回调地址")
    private String returnUrl;

    // ========== 订单相关字段 ==========

    @ApiModelProperty(value = "支付金额，单位：分", required = "required")
    private String price;

    @ApiModelProperty(value = "支付过期时间", required = "required")
    private LocalDateTime expireTime;

    // ========== 拓展参数 ==========
    @ApiModelProperty(value = "支付渠道的额外参数，例如说，微信公众号需要传递 openid 参数")
    private Map<String, String> channelExtras;

    @ApiModelProperty(value = "展示模式", enumClass = PayOrderDisplayMode.class)
    private String displayMode;

}
