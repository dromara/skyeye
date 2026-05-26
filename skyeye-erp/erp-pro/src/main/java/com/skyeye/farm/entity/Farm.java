/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.farm.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.CacheConstants;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.common.enumeration.EnableEnum;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: Farm
 * @Description: 车间管理实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/3/24 22:43
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = CacheConstants.MES_FARM_CACHE_KEY)
@TableName(value = "erp_farm")
@ApiModel("车间管理实体类")
public class Farm extends BaseGeneralInfo {

    @TableField(value = "number")
    @ApiModelProperty(value = "车间编号", required = "required", fuzzyLike = true)
    private String number;

    @TableField(value = "enabled")
    @ApiModelProperty(value = "启用状态", enumClass = EnableEnum.class, required = "required,num")
    private Integer enabled;

    @TableField(value = "department_id")
    @ApiModelProperty(value = "部门id", required = "required")
    private String departmentId;

    @TableField(exist = false)
    @Property(value = "部门信息")
    private Map<String, Object> departmentMation;

    @TableField(value = "charge_person")
    @ApiModelProperty(value = "车间负责人", required = "required")
    private String chargePerson;

    /**
     * 每日可用工时(分钟)，用于APS排产计算车间产能。默认480(8小时)。
     * 扩展：复杂场景使用 erp_farm_calendar 表，按日/按星期/节假日配置，本字段作为默认值。
     */
    @TableField(value = "daily_work_minutes")
    @ApiModelProperty(value = "每日可用工时(分钟)，APS排产用", required = "required")
    private Integer dailyWorkMinutes;

    @TableField(exist = false)
    @Property(value = "车间负责人")
    private Map<String, Object> chargePersonMation;

    @TableField(value = "delete_flag")
    private Integer deleteFlag;

}
