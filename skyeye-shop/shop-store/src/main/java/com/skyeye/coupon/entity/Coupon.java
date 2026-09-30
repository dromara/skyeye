/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.coupon.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.coupon.enums.*;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: Coupon
 * @Description: 优惠券/模版信息管理实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/10/23 10:08
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "shop_coupon", autoResultMap = true)
@RedisCacheField(name = "shop:coupon", cacheTime = RedisConstants.THIRTY_DAY_SECONDS)
@ApiModel(value = "优惠券/模版信息管理实体类")
public class Coupon extends BaseGeneralInfo {

    @TableField(exist = false)
    @ApiModelProperty(value = "发布门店id列表", required = "json")
    private List<String> storeIdList;

    @TableField(exist = false)
    @ApiModelProperty(value = "优惠券与门店关联表列表")
    private List<CouponStore> couponStoreList;

    @TableField(value = "store_id")
    @ApiModelProperty(value = "归属门店id（门店自建时写入；管理端为空）")
    private String storeId;

    @TableField(value = "template_id")
    @ApiModelProperty(value = "模板id")
    private String templateId;

    @TableField(value = "enabled")
    @ApiModelProperty(value = "状态", required = "required,num", enumClass = EnableEnum.class)
    private Integer enabled;

    @TableField(value = "total_count")
    @ApiModelProperty(value = "发放数量，-1表示不限制发放数量", required = "required")
    private Integer totalCount;

    @TableField(value = "take_limit_count")
    @ApiModelProperty(value = "每人限领个数，-1表示不限制", required = "required")
    private Integer takeLimitCount;

    @TableField(value = "take_type")
    @ApiModelProperty(value = "领取方式", required = "required,num", enumClass = CouponTakeType.class)
    private Integer takeType;

    @TableField(value = "use_price")
    @ApiModelProperty(value = "是否设置满多少金额可用，单位：分。0 - 不限制，大于 0 - 多少金额可用", required = "required")
    private String usePrice;

    @TableField(value = "product_scope")
    @ApiModelProperty(value = "商品范围", required = "required,num", enumClass = PromotionMaterialScope.class)
    private Integer productScope;

    @TableField(value = "validity_type")
    @ApiModelProperty(value = "生效日期类型", required = "required,num", enumClass = CouponValidityType.class)
    private Integer validityType;

    @TableField(value = "valid_start_time")
    @ApiModelProperty(value = "固定日期 - 生效开始时间，日期格式yyyy-MM-dd HH:mm:ss")
    private String validStartTime;

    @TableField(value = "valid_end_time")
    @ApiModelProperty(value = "固定日期 - 生效结束时间，日期格式yyyy-MM-dd HH:mm:ss")
    private String validEndTime;

    @TableField(value = "fixed_start_time")
    @ApiModelProperty(value = "领取日期 - 领取几天后可以开始使用")
    private Integer fixedStartTime;

    @TableField(value = "fixed_end_time")
    @ApiModelProperty(value = "领取日期 - 领取开始使用时几天后结束")
    private Integer fixedEndTime;

    @TableField(value = "discount_type")
    @ApiModelProperty(value = "折扣类型", required = "required,num", enumClass = PromotionDiscountType.class)
    private Integer discountType;

    @TableField(value = "discount_percent")
    @ApiModelProperty(value = "折扣百分比，例如，80% 为 80")
    private Integer discountPercent;

    @TableField(value = "discount_price")
    @ApiModelProperty(value = "优惠金额，单位：分")
    private String discountPrice;

    @TableField(value = "discount_limit_price")
    @ApiModelProperty(value = "折扣上限，百分比折扣也受其约束")
    private String discountLimitPrice;

    @TableField(value = "take_count")
    @Property(value = "已经领取优惠券的数量")
    private Integer takeCount;

    @TableField(value = "use_count")
    @ApiModelProperty(value = "使用优惠券的总次数")
    private Integer useCount;

    @TableField(exist = false)
    @ApiModelProperty(value = "优惠券适用对象列表", required = "json")
    private List<CouponMaterial> couponMaterialList;

    @TableField(exist = false)
    @Property(value = "是否可领取")
    private Boolean canDraw;

    @TableField(value = "store_coverage")
    @ApiModelProperty(value = "门店适用范围", enumClass = CouponStoreCoverage.class)
    private Integer storeCoverage;

    @TableField(value = "coupon_source")
    @ApiModelProperty(value = "来源：管理端/门店", enumClass = CouponSource.class)
    private Integer couponSource;

    @TableField(value = "tenant_id", updateStrategy = FieldStrategy.NEVER)
    @Property(value = "租户id")
    private String tenantId;
}