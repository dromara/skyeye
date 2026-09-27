/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.finance.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.finance.enums.ShopStoreLedgerBizType;
import com.skyeye.finance.enums.ShopStoreLedgerDirection;
import com.skyeye.finance.enums.ShopStoreSettleStatus;
import lombok.Data;

/**
 * 个人门店资金流水
 */
@Data
@TableName("shop_store_ledger")
@ApiModel("个人门店资金流水")
public class ShopStoreLedger extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id")
    private String id;

    @TableField("store_id")
    @ApiModelProperty(value = "门店id")
    private String storeId;

    @TableField("biz_type")
    @ApiModelProperty(value = "业务类型", enumClass = ShopStoreLedgerBizType.class)
    private Integer bizType;

    @TableField("biz_id")
    @ApiModelProperty(value = "业务单号")
    private String bizId;

    @TableField("amount")
    @Property(value = "变动金额（分，正数）")
    private Long amount;

    @TableField("direction")
    @ApiModelProperty(value = "变动方向", enumClass = ShopStoreLedgerDirection.class)
    private Integer direction;

    @TableField("available_after")
    @Property(value = "变动后可用余额")
    private Long availableAfter;

    @TableField("frozen_after")
    @Property(value = "变动后冻结余额")
    private Long frozenAfter;

    @TableField("remark")
    @ApiModelProperty(value = "备注")
    private String remark;

    @TableField(exist = false)
    @Property(value = "关联订单子单号")
    private String oddNumber;

    /**
     * 订单到账状态；非订单类流水为空
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "到账状态", enumClass = ShopStoreSettleStatus.class)
    private Integer settleStatus;

    @TableField(exist = false)
    @Property(value = "预计到账时间")
    private String settleExpectTime;
}
