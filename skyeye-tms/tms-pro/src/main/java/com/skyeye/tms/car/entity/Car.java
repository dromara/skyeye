/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tms.car.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.tms.cartype.entity.TmsCarType;
import lombok.Data;

import java.util.Map;

import com.skyeye.tms.car.classenum.CarAttribute;
import com.skyeye.tms.car.classenum.CarState;
import com.skyeye.common.enumeration.EnableEnum;

/**
 * @ClassName: Car
 * @Description: 车辆管理实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/9 11:59
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField("plate")
@RedisCacheField(name = "tms:car", cacheTime = RedisConstants.HALF_A_YEAR_SECONDS)
@TableName(value = "tms_car")
@ApiModel("车辆管理实体类")
public class Car extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("odd_number")
    @Property(value = "车辆编号", fuzzyLike = true)
    private String oddNumber;

    @TableField(exist = false)
    @Property(value = "名称")
    private String name;

    @TableField("plate")
    @ApiModelProperty(value = "车牌号", required = "required", fuzzyLike = true)
    private String plate;

    @TableField(value = "logo")
    @ApiModelProperty(value = "车辆图片")
    private String logo;

    @TableField(value = "type_id")
    @ApiModelProperty(value = "车辆类型id", required = "required")
    private String typeId;

    @TableField(exist = false)
    @Property(value = "车辆类型信息")
    private TmsCarType typeMation;

    @TableField(value = "attribute_id")
    @ApiModelProperty(value = "车辆属性", required = "required", enumClass = CarAttribute.class)
    private Integer attributeId;

    @TableField(value = "state")
    @ApiModelProperty(value = "当前状态", required = "required", enumClass = CarState.class)
    private Integer state;

    @TableField(value = "common_driver_id")
    @ApiModelProperty(value = "常用司机id")
    private String commonDriverId;

    @TableField(exist = false)
    @Property(value = "常用司机信息")
    private Map<String, Object> commonDriverMation;

    @TableField(value = "auxiliary_driver_id")
    @ApiModelProperty(value = "辅助司机id")
    private String auxiliaryDriverId;

    @TableField(exist = false)
    @Property(value = "辅助司机信息")
    private Map<String, Object> auxiliaryDriverMation;

    @TableField(value = "settlement_method")
    @ApiModelProperty(value = "结算方式，参考财务-财务账号")
    private String settlementMethod;

    @TableField(exist = false)
    @Property(value = "结算方式信息")
    private Map<String, Object> settlementMethodMation;

    @TableField(value = "load_capacity_ton")
    @ApiModelProperty(value = "核准载重(吨)", defaultValue = "0")
    private String loadCapacityTon;

    @TableField(value = "load_capacity_cbm")
    @ApiModelProperty(value = "核准载重体积(CBM)", defaultValue = "0")
    private String loadCapacityCbm;

    @TableField(value = "plate_color_id")
    @ApiModelProperty(value = "车辆颜色，数据字典")
    private String plateColorId;

    @TableField(value = "buy_time")
    @ApiModelProperty(value = "购买日期")
    private String buyTime;

    @TableField(value = "vehicle_model")
    @ApiModelProperty(value = "车辆型号")
    private String vehicleModel;

    @TableField(value = "engine_number")
    @ApiModelProperty(value = "发动机号")
    private String engineNumber;

    @TableField(value = "frame_number")
    @ApiModelProperty(value = "车架号")
    private String frameNumber;

    @TableField(value = "category_id")
    @ApiModelProperty(value = "货车类别，数据字典")
    private String categoryId;

    @TableField(value = "cabinet_id")
    @ApiModelProperty(value = "柜型，数据字典")
    private String cabinetId;

    @TableField(value = "object_id")
    @ApiModelProperty(value = "所属车队(供应商id)")
    private String objectId;

    @TableField(value = "object_key")
    @ApiModelProperty(value = "所属车队(供应商key)")
    private String objectKey;

    @TableField(exist = false)
    @Property(value = "所属车队(供应商信息)")
    private Map<String, Object> objectMation;

    @TableField(value = "enabled")
    @ApiModelProperty(value = "启用状态", required = "required,num", enumClass = EnableEnum.class)
    private Integer enabled;

}
