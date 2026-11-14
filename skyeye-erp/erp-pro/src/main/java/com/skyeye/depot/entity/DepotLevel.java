/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.depot.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.depot.classenum.DepotLevelVisualTypeEnum;
import lombok.Data;

/**
 * @ClassName: DepotLevel
 * @Description: 仓库级别表
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/5 21:53
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField(value = {"depotId", "name"})
@RedisCacheField(name = "erp:depotLevel")
@TableName(value = "erp_depot_level", autoResultMap = true)
@ApiModel("仓库级别表")
public class DepotLevel extends BaseGeneralInfo {

    @TableField(value = "parent_id")
    @ApiModelProperty(value = "父节点id", required = "required", defaultValue = "0")
    private String parentId;

    @TableField(value = "depot_id")
    @ApiModelProperty(value = "仓库id", required = "required")
    private String depotId;

    @TableField(exist = false)
    @Property(value = "仓库信息")
    private Depot depotMation;

    @TableField(value = "visual_type")
    @ApiModelProperty(value = "3D可视化类型", enumClass = DepotLevelVisualTypeEnum.class, required = "required,num")
    private Integer visualType;

}
