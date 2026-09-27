/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.finance.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

/**
 * 个人门店资金账户
 */
@Data
@TableName("shop_store_account")
@ApiModel("个人门店资金账户")
public class ShopStoreAccount extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id")
    private String id;

    @TableField("store_id")
    @ApiModelProperty(value = "门店id", required = "required")
    private String storeId;

    @TableField("available_amount")
    @Property(value = "可用余额（分）")
    private Long availableAmount;

    @TableField("frozen_amount")
    @Property(value = "冻结金额（分）")
    private Long frozenAmount;

    @TableField("total_income")
    @Property(value = "累计入账（分）")
    private Long totalIncome;

    @TableField("total_refund")
    @Property(value = "累计退款出账（分）")
    private Long totalRefund;

    @TableField("total_withdraw")
    @Property(value = "累计提现成功（分）")
    private Long totalWithdraw;

    @Version
    @TableField("version")
    @Property(value = "乐观锁")
    private Integer version;
}
