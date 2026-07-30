
/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tms.cartype.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import lombok.Data;

import com.skyeye.common.enumeration.EnableEnum;

/**
 * @ClassName: TmsCarType
 * @Description: 车辆类型实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/9 20:14
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField
@RedisCacheField(name = "tms:carType", cacheTime = RedisConstants.HALF_A_YEAR_SECONDS)
@TableName(value = "tms_car_type")
@ApiModel(value = "车辆类型表")
public class TmsCarType extends BaseGeneralInfo {

    @TableField("odd_number")
    @Property(value = "编码")
    private String oddNumber;

    @TableField("load_capacity_ton")
    @ApiModelProperty(value = "核准载重(吨)", required = "required", defaultValue = "0.00")
    private String loadCapacityTon;

    @TableField("load_capacity_cbm")
    @ApiModelProperty(value = "核准载重体积(CBM)", required = "required", defaultValue = "0.00")
    private String loadCapacityCbm;

    @TableField("max_transporter_num")
    @ApiModelProperty(value = "最大运输件数")
    private String maxTransporterNum;

    @TableField(value = "enabled")
    @ApiModelProperty(value = "启用状态", required = "required,num", enumClass = EnableEnum.class)
    private Integer enabled;

}

