/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.shop.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.enumeration.IsDefaultEnum;
import com.skyeye.depot.entity.Depot;
import lombok.Data;

/**
 * @ClassName: ShopStoreDepot
 * @Description: 个人门店与仓库关联（含优先级）
 */
@Data
@TableName(value = "erp_shop_store_depot")
@ApiModel("个人门店与仓库关联")
public class ShopStoreDepot extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "store_id")
    @ApiModelProperty(value = "门店id", required = "required")
    private String storeId;

    @TableField(value = "depot_id")
    @ApiModelProperty(value = "仓库id", required = "required")
    private String depotId;

    @TableField(exist = false)
    @Property(value = "仓库信息")
    private Depot depotMation;

    @TableField(value = "depot_code")
    @ApiModelProperty(value = "仓编码")
    private String depotCode;

    @TableField(value = "alias_name")
    @ApiModelProperty(value = "仓别名")
    private String aliasName;

    @TableField(value = "contact_name")
    @ApiModelProperty(value = "联系人")
    private String contactName;

    @TableField(value = "contact_phone")
    @ApiModelProperty(value = "联系电话")
    private String contactPhone;

    @TableField(value = "province_id")
    @ApiModelProperty(value = "省")
    private String provinceId;

    @TableField(value = "city_id")
    @ApiModelProperty(value = "市")
    private String cityId;

    @TableField(value = "area_id")
    @ApiModelProperty(value = "区县")
    private String areaId;

    @TableField(value = "township_id")
    @ApiModelProperty(value = "乡镇")
    private String townshipId;

    @TableField(value = "absolute_address")
    @ApiModelProperty(value = "详细地址")
    private String absoluteAddress;

    @TableField(value = "priority")
    @ApiModelProperty(value = "优先级，越小越优先")
    private Integer priority;

    @TableField(value = "is_default")
    @ApiModelProperty(value = "是否默认仓", enumClass = IsDefaultEnum.class)
    private Integer isDefault;

    @TableField(value = "enabled")
    @ApiModelProperty(value = "启用状态", enumClass = EnableEnum.class)
    private Integer enabled;

}
