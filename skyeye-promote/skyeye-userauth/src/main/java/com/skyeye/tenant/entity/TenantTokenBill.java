/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.tenant.classenum.TenantTokenBillState;
import lombok.Data;

@Data
@TableName(value = "tenant_token_bill")
@ApiModel("租户 Token 月结账单")
public class TenantTokenBill extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("tenant_id")
    @ApiModelProperty(value = "租户id")
    private String tenantId;

    @TableField(exist = false)
    @Property("租户信息")
    private Tenant tenantMation;

    @TableField("bill_period")
    @Property("账期")
    private String billPeriod;

    @TableField("start_date")
    @Property("开始日期")
    private String startDate;

    @TableField("end_date")
    @Property("结束日期")
    private String endDate;

    @TableField("total_tokens")
    @Property("本期 Token")
    private Long totalTokens;

    @TableField("tokens_per_yuan")
    @Property("结算时 1 元兑换 Token 数")
    private String tokensPerYuan;

    @TableField("amount")
    @Property("应付金额(元)")
    private String amount;

    @TableField("state")
    @ApiModelProperty(value = "账单状态", enumClass = TenantTokenBillState.class)
    private Integer state;

    @TableField("settle_time")
    @Property("出账时间")
    private String settleTime;

    @TableField("pay_order_id")
    @Property("结清对应的购买订单id")
    private String payOrderId;

    @TableField("pay_time")
    @Property("结清时间")
    private String payTime;

}
