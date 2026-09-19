/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.store.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.AreaInfo;
import com.skyeye.store.classenum.ShopStoreApplyStatus;
import lombok.Data;

/**
 * @ClassName: ShopStoreApply
 * @Description: 个人开店申请
 */
@Data
@TableName(value = "shop_store_apply")
@ApiModel("个人开店申请实体类")
public class ShopStoreApply extends AreaInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "tenant_id", updateStrategy = FieldStrategy.NEVER)
    @Property(value = "租户id，个人店固定为 TenantTypeEnum.SHOP")
    private String tenantId;

    @TableField("member_id")
    @ApiModelProperty(value = "申请会员id")
    private String memberId;

    @TableField(exist = false)
    @Property(value = "申请人会员信息")
    private Object memberMation;

    @TableField("store_name")
    @ApiModelProperty(value = "申请店铺名称", required = "required")
    private String storeName;

    @TableField("logo")
    @ApiModelProperty(value = "店铺logo")
    private String logo;

    @TableField("remark")
    @ApiModelProperty(value = "店铺简介/申请说明")
    private String remark;

    @TableField("contact_name")
    @ApiModelProperty(value = "联系人")
    private String contactName;

    @TableField("contact_phone")
    @ApiModelProperty(value = "联系电话")
    private String contactPhone;

    @TableField("state")
    @ApiModelProperty(value = "申请状态", enumClass = ShopStoreApplyStatus.class, required = "num")
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

    @TableField("store_id")
    @Property(value = "审核通过后生成的门店id；变更申请提交时即为要改的门店")
    private String storeId;

    @TableField("online_open")
    @ApiModelProperty(value = "线上门店是否开启", enumClass = com.skyeye.common.enumeration.WhetherEnum.class, required = "num")
    private Integer onlineOpen;

    @TableField("offline_open")
    @ApiModelProperty(value = "线下门店是否开启", enumClass = com.skyeye.common.enumeration.WhetherEnum.class, required = "num")
    private Integer offlineOpen;

    @TableField("apply_type")
    @Property(value = "申请类型：1开店 2变更线上线下或经营地址")
    private Integer applyType;

}
