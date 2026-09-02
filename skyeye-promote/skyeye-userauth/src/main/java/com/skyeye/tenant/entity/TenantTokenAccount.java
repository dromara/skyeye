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
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.tenant.classenum.TenantTokenBillingMode;
import lombok.Data;

@Data
@TableName(value = "tenant_token_account")
@ApiModel("租户 Token 账户")
public class TenantTokenAccount extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("tenant_id")
    @ApiModelProperty(value = "租户id", required = "required")
    private String tenantId;

    @TableField(exist = false)
    @Property("租户信息")
    private Tenant tenantMation;

    @TableField("billing_mode")
    @ApiModelProperty(value = "计费方式", enumClass = TenantTokenBillingMode.class, required = "required,num")
    private Integer billingMode;

    @TableField("token_balance")
    @Property("预付剩余 Token")
    private Long tokenBalance;

    @TableField("token_used")
    @Property("累计已用 Token")
    private Long tokenUsed;

    @TableField("stopped")
    @ApiModelProperty(value = "预付用尽是否停用", enumClass = WhetherEnum.class)
    private Integer stopped;

}
