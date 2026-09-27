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
import com.skyeye.finance.enums.ShopStoreWithdrawStatus;
import lombok.Data;

/**
 * 个人门店提现申请
 */
@Data
@TableName("shop_store_withdraw")
@ApiModel("个人门店提现申请")
public class ShopStoreWithdraw extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id")
    private String id;

    @TableField("store_id")
    @ApiModelProperty(value = "门店id", required = "required")
    private String storeId;

    @TableField(exist = false)
    @Property(value = "门店信息")
    private Object storeMation;

    @TableField("member_id")
    @ApiModelProperty(value = "申请人会员id")
    private String memberId;

    @TableField(exist = false)
    @Property(value = "申请人信息")
    private Object memberMation;

    @TableField("amount")
    @ApiModelProperty(value = "提现金额（分）", required = "required")
    private Long amount;

    @TableField("state")
    @ApiModelProperty(value = "状态", enumClass = ShopStoreWithdrawStatus.class)
    private Integer state;

    @TableField("account_name")
    @ApiModelProperty(value = "收款户名", required = "required")
    private String accountName;

    @TableField("account_no")
    @ApiModelProperty(value = "收款账号", required = "required")
    private String accountNo;

    @TableField("bank_name")
    @ApiModelProperty(value = "开户行")
    private String bankName;

    @TableField("audit_user_id")
    @Property(value = "审核人")
    private String auditUserId;

    @TableField("audit_time")
    @Property(value = "审核时间")
    private String auditTime;

    @TableField("audit_remark")
    @ApiModelProperty(value = "审核备注")
    private String auditRemark;

    @TableField("out_transfer_no")
    @Property(value = "商户转账单号")
    private String outTransferNo;

    @TableField("channel_transfer_no")
    @Property(value = "渠道转账单号")
    private String channelTransferNo;

    @TableField("transfer_error")
    @Property(value = "转账失败原因")
    private String transferError;
}
