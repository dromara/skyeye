/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.constans.CacheConstants;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.tenant.classenum.TenantOrgType;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: Tenant
 * @Description: 租户实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/28 20:10
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField
@RedisCacheField(name = CacheConstants.BASE_TENANT, cacheTime = RedisConstants.HALF_A_YEAR_SECONDS)
@TableName(value = "tenant")
@ApiModel("租户实体类")
public class Tenant extends BaseGeneralInfo {

    @TableField("account_num")
    @Property(value = "允许的账号数量(席位数量)")
    private Integer accountNum;

    @TableField(value = "`logo`")
    @ApiModelProperty(value = "组织Logo")
    private String logo;

    @TableField("org_type")
    @ApiModelProperty(value = "组织类型", enumClass = TenantOrgType.class, required = "required,num")
    private Integer orgType;

    @TableField("contact_name")
    @ApiModelProperty(value = "联系人/负责人姓名")
    private String contactName;

    @TableField("contact_phone")
    @ApiModelProperty(value = "联系电话")
    private String contactPhone;

    @TableField("contact_email")
    @ApiModelProperty(value = "联系邮箱")
    private String contactEmail;

    @TableField("address")
    @ApiModelProperty(value = "地址")
    private String address;

    @TableField("website")
    @ApiModelProperty(value = "官网")
    private String website;

    @TableField("industry")
    @ApiModelProperty(value = "所属行业")
    private String industry;

    @TableField("credit_code")
    @ApiModelProperty(value = "统一社会信用代码")
    private String creditCode;

    @TableField("legal_person")
    @ApiModelProperty(value = "法定代表人")
    private String legalPerson;

    /**
     * 是否曾有过「审核通过」的应用购买订单；订单终审通过时由系统置为是。需在表 tenant 增加列 whether_has_passed_buy_order（int，默认 0）。
     */
    @TableField("whether_has_passed_buy_order")
    @ApiModelProperty(value = "是否存在已审批通过的应用购买订单（订单审核通过后由系统自动维护）", enumClass = WhetherEnum.class)
    @Property(value = "是否存在已审批通过的应用购买订单")
    private Integer whetherHasPassedBuyOrder;

    /**
     * 是否允许被搜索并申请加入。需在表 tenant 增加列 whether_searchable（int，默认 0）。
     */
    @TableField("whether_searchable")
    @ApiModelProperty(value = "是否允许被搜索", enumClass = WhetherEnum.class)
    private Integer whetherSearchable;

    /**
     * 用户申请加入时是否需要管理员审核。需在表 tenant 增加列 whether_join_need_audit（int，默认 1）。
     */
    @TableField("whether_join_need_audit")
    @ApiModelProperty(value = "加入是否需要审核", enumClass = WhetherEnum.class)
    private Integer whetherJoinNeedAudit;

    @TableField(exist = false)
    @ApiModelProperty("应用信息")
    private List<TenantAppLink> tenantAppLinkList;

    @TableField(exist = false)
    @ApiModelProperty(value = "当前用户在该租户是否是管理员", enumClass = WhetherEnum.class)
    private Integer isAdmin;

}
