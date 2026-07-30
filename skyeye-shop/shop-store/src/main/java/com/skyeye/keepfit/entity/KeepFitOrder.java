/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.keepfit.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.keepfit.classenum.KeepFitOrderState;
import com.skyeye.keepfit.classenum.KeepFitOrderUserType;
import com.skyeye.meal.entity.MealOrderChild;
import com.skyeye.store.entity.ShopStore;
import lombok.Data;

import java.util.List;
import java.util.Map;

import com.skyeye.meal.classenum.ShopMealOrderType;

/**
 * @ClassName: KeepFitOrderMation
 * @Description: 保养订单管理实体类
 * @author: skyeye云系列--卫志强
 * @date: 2022/2/8 15:16
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "shop:keepFitOrder", cacheTime = RedisConstants.THIRTY_DAY_SECONDS)
@TableName(value = "shop_keepfit_order")
@ApiModel("保养订单管理实体类")
public class KeepFitOrder extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("odd_number")
    @Property(value = "单据编号", fuzzyLike = true)
    private String oddNumber;

    @TableField("type")
    @ApiModelProperty(value = "订单来源，和套餐订单一样", required = "required,num", enumClass = ShopMealOrderType.class)
    private Integer type;

    @TableField("remark")
    @ApiModelProperty(value = "备注")
    private String remark;

    @TableField("user_type")
    @ApiModelProperty(value = "用户类型", enumClass = KeepFitOrderUserType.class, required = "required,num")
    private Integer userType;

    @TableField(value = "object_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "所属第三方业务数据id")
    private String objectId;

    @TableField(value = "object_key", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "所属第三方业务数据的key")
    private String objectKey;

    @TableField(exist = false)
    @Property(value = "适用对象信息")
    private Map<String, Object> objectMation;

    @TableField(value = "material_id")
    @ApiModelProperty(value = "商品id")
    private String materialId;

    @TableField(exist = false)
    @Property(value = "商品信息")
    private Map<String, Object> materialMation;

    @TableField(value = "norms_id")
    @ApiModelProperty(value = "规格id")
    private String normsId;

    @TableField(exist = false)
    @Property(value = "规格信息")
    private Map<String, Object> normsMation;

    @TableField(value = "code_num", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "规格物品编码", fuzzyLike = true)
    private String codeNum;

    @TableField(exist = false)
    @Property(value = "规格物品编码信息")
    private Map<String, Object> codeNumMation;

    @TableField("payable_price")
    @Property(value = "应付金额")
    private String payablePrice;

    @TableField("pay_price")
    @Property(value = "实付金额")
    private String payPrice;

    @TableField("pay_time")
    @Property(value = "实付日期")
    private String payTime;

    @TableField("service_price")
    @ApiModelProperty(value = "服务费", required = "double", defaultValue = "0.0")
    private String servicePrice;

    @TableField("meal_order_child_id")
    @ApiModelProperty(value = "如果使用套餐，则为套餐订单子表的id   如果user_type=2，该字段可能有值")
    private String mealOrderChildId;

    @TableField(exist = false)
    @Property(value = "套餐订单子表信息")
    private MealOrderChild mealOrderChildMation;

    @TableField(value = "store_id")
    @ApiModelProperty(value = "门店ID", required = "required")
    private String storeId;

    @TableField(exist = false)
    @Property(value = "门店信息")
    private ShopStore storeMation;

    @TableField(value = "state")
    @Property(value = "状态", enumClass = KeepFitOrderState.class)
    private Integer state;

    @TableField(value = "online_day")
    @ApiModelProperty(value = "线上预约日期   如果type=1，该字段必有值")
    private String onlineDay;

    @TableField(value = "online_time")
    @ApiModelProperty(value = "线上预约时间段   如果type=1，该字段必有值")
    private String onlineTime;

    @TableField(value = "complate_pay_user_id")
    @Property(value = "完成支付操作的操作人id")
    private String complatePayUserId;

    @TableField(exist = false)
    @Property(value = "完成支付操作的操作人信息")
    private Map<String, Object> complatePayUserMation;

    @TableField(value = "verification_user_id")
    @Property(value = "完成核销时的操作人id")
    private String verificationUserId;

    @TableField(exist = false)
    @Property(value = "完成核销时的操作人信息")
    private Map<String, Object> verificationUserMation;

    @TableField(value = "service_technician_id")
    @Property(value = "维修技师id(员工id)")
    private String serviceTechnicianId;

    @TableField(exist = false)
    @Property(value = "维修技师信息")
    private Map<String, Object> serviceTechnicianMation;

    @TableField(value = "next_service_mileage")
    @Property(value = "下次保养公里数")
    private String nextServiceMileage;

    @TableField(value = "next_service_time")
    @Property(value = "下次保养时间，格式为yyyy-MM-dd")
    private String nextServiceTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "保养订单耗材")
    private List<KeepFitOrderConsume> consumeMationList;

}
