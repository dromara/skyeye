/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.pay.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.common.enumeration.EnableEnum;
import lombok.Data;

/**
 * 支付应用：一次支付业务的配置入口（租户购买、商城订单等各自对应一个 PayApp）。
 * <p>
 * 回调链路：
 * 微信/支付宝 → channelNotifyUrl/{channelId}（PayNotifyOpenController）
 * → 验签解析 → orderNotifyUrl（各业务 notifyXxxPaySuccess 接口）
 */
@Data
@UniqueField(value = "appKey")
@TableName(value = "skyeye_pay_app")
@ApiModel("支付应用实体类")
public class PayApp extends BaseGeneralInfo {

    @TableField("app_key")
    @ApiModelProperty(value = "应用标识（例如：租户购买填 tenant-buy，商城填 mall-order）", required = "required",
        exampleDefault = "tenant-buy")
    private String appKey;

    @TableField("enabled")
    @ApiModelProperty(value = "状态", required = "required", enumClass = EnableEnum.class)
    private Integer enabled;

    /**
     * 支付渠道回调地址（公网可访问），微信/支付宝回调 pay 模块的入口。
     * 填写基础路径即可，如：http://domain/pay/notify/order，系统会自动拼接 /{channelId}
     */
    @TableField("channel_notify_url")
    @ApiModelProperty(value = "支付渠道回调地址", required = "required")
    private String channelNotifyUrl;

    /**
     * 业务支付成功回调地址。pay 模块验签后会转发到此地址（租户订单、商城订单等各自配置）。
     */
    @TableField("order_notify_url")
    @ApiModelProperty(value = "业务支付成功回调地址", required = "required")
    private String orderNotifyUrl;

    @TableField("refund_notify_url")
    @ApiModelProperty(value = "业务退款成功回调地址", required = "required")
    private String refundNotifyUrl;

    /**
     * 业务转账结果回调，按需配置
     */
    @TableField("transfer_notify_url")
    @ApiModelProperty(value = "业务转账结果回调地址")
    private String transferNotifyUrl;
}
