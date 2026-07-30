/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tms.path.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.tms.address.entity.BillingAddress;
import lombok.Data;

import com.skyeye.common.enumeration.EnableEnum;

/**
 * @ClassName: TransportationPath
 * @Description: 运输路径实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/11 22:05
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField
@RedisCacheField(name = "tms:path", cacheTime = RedisConstants.A_YEAR_SECONDS)
@TableName(value = "tms_transportation_path")
@ApiModel("运输路径实体类")
public class TransportationPath extends BaseGeneralInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "departure_id")
    @ApiModelProperty(value = "出发地（计费地址id）", required = "required")
    private String departureId;

    @TableField(exist = false)
    @Property(value = "出发地信息")
    private BillingAddress departureMation;

    @TableField(value = "destination_id")
    @ApiModelProperty(value = "目的地（计费地址id）", required = "required")
    private String destinationId;

    @TableField(exist = false)
    @Property(value = "目的地信息")
    private BillingAddress destinationMation;

    @TableField(value = "billable_mileage")
    @ApiModelProperty(value = "计费里程(千米)")
    private String billableMileage;

    @TableField(value = "actual_mileage")
    @ApiModelProperty(value = "实际里程(千米)")
    private String actualMileage;

    @TableField(value = "enabled")
    @ApiModelProperty(value = "启用状态", required = "required,num", enumClass = EnableEnum.class)
    private Integer enabled;

}
