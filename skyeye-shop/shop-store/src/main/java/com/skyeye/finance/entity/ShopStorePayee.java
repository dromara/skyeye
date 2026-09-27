/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.finance.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

/**
 * 门店默认收款账户（每店一条，提现表单回填）
 */
@Data
@TableName("shop_store_payee")
@ApiModel("门店默认收款账户")
public class ShopStorePayee extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id")
    private String id;

    @TableField("store_id")
    @ApiModelProperty(value = "门店id", required = "required")
    private String storeId;

    @TableField("account_name")
    @ApiModelProperty(value = "收款户名", required = "required")
    private String accountName;

    @TableField("account_no")
    @ApiModelProperty(value = "收款账号", required = "required")
    private String accountNo;

    @TableField("bank_name")
    @ApiModelProperty(value = "开户行", required = "required")
    private String bankName;
}
