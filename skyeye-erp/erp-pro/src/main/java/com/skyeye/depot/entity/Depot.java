/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.depot.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.constans.CacheConstants;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.enumeration.IsDefaultEnum;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: DepotMation
 * @Description: 仓库管理实体类
 * @author: skyeye云系列--卫志强
 * @date: 2022/8/14 9:20
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField
@RedisCacheField(name = CacheConstants.ERP_DEPOT_CACHE_KEY)
@TableName(value = "erp_depot", autoResultMap = true)
@ApiModel("仓库管理实体类")
public class Depot extends BaseGeneralInfo {

    @TableField(value = "address")
    @ApiModelProperty(value = "仓库地址")
    private String address;

    @TableField(value = "warehousing")
    @ApiModelProperty(value = "仓储费")
    private String warehousing;

    @TableField(value = "truckage")
    @ApiModelProperty(value = "搬运费")
    private String truckage;

    @TableField(value = "principal", typeHandler = JacksonTypeHandler.class)
    @ApiModelProperty(value = "负责人id")
    private List<String> principal;

    @TableField(exist = false)
    @Property(value = "负责人对象")
    private List<Map<String, Object>> principalMation;

    @TableField(value = "delete_flag")
    private Integer deleteFlag;

    @TableField(value = "is_default")
    @ApiModelProperty(value = "是否默认", enumClass = IsDefaultEnum.class, required = "required,num")
    private Integer isDefault;

    @TableField(value = "enabled")
    @ApiModelProperty(value = "启用状态", enumClass = EnableEnum.class, required = "required,num")
    private Integer enabled;

    @TableField(value = "store_id")
    @ApiModelProperty(value = "个人门店id，空表示企业仓")
    private String storeId;

}
