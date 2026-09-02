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
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

@Data
@TableName(value = "tenant_app_buy_order_token")
@ApiModel("订单-购买 Token 明细")
public class TenantAppBuyOrderToken extends CommonInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("parent_id")
    @Property("单据ID")
    private String parentId;

    @TableField("buy_amount")
    @ApiModelProperty(value = "购买金额(元)", required = "required")
    private String buyAmount;

    @TableField("token_qty")
    @Property("兑换 Token 数量")
    private Long tokenQty;

    @TableField("tokens_per_yuan")
    @Property("1 元兑换 Token 数")
    private String tokensPerYuan;

    @TableField("all_price")
    @Property("总价")
    private String allPrice;

}
