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
import com.skyeye.tenant.classenum.TenantUserApplyStatus;
import lombok.Data;

/**
 * @ClassName: TenantUserApply
 * @Description: 用户申请加入租户记录
 * 表 tenant_user_apply 需包含 tenant_id、staff_id、apply_message、state、audit_user_id、audit_time、audit_remark 等字段
 */
@Data
@TableName(value = "tenant_user_apply")
@ApiModel("用户申请加入租户记录")
public class TenantUserApply extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id")
    private String id;

    @TableField("tenant_id")
    @Property(value = "租户id")
    private String tenantId;

    @TableField(exist = false)
    @Property(value = "租户名称")
    private String tenantName;

    @TableField(exist = false)
    @Property(value = "租户Logo")
    private String tenantLogo;

    @TableField("staff_id")
    @ApiModelProperty(value = "申请人员工id", required = "required")
    private String staffId;

    @TableField(exist = false)
    @Property(value = "申请人信息")
    private Object staffMation;

    @TableField("apply_message")
    @ApiModelProperty(value = "申请留言")
    private String applyMessage;

    @TableField("state")
    @ApiModelProperty(value = "申请状态", enumClass = TenantUserApplyStatus.class, required = "required,num")
    private Integer state;

    @TableField(exist = false)
    @Property(value = "申请状态名称")
    private String stateName;

    @TableField("audit_user_id")
    @Property(value = "审核人用户id")
    private String auditUserId;

    @TableField("audit_time")
    @Property(value = "审核时间")
    private String auditTime;

    @TableField("audit_remark")
    @ApiModelProperty(value = "审核备注")
    private String auditRemark;

}
