/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.sparepart.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

import java.util.Map;

/**
 * 设备维修-我的备件库存（备件申领单审核出库后入账）
 */
@Data
@TableName(value = "erp_equipment_user_stock")
@ApiModel("用户备件申领单审核通过后的库存信息")
public class EquipmentUserStock extends CommonInfo {

    @TableId("id")
    private String id;

    @TableField(value = "user_id")
    @Property(value = "用户id")
    private String userId;

    @TableField(value = "material_id")
    @Property(value = "商品id")
    private String materialId;

    @TableField(exist = false)
    @Property(value = "商品信息")
    private Map<String, Object> materialMation;

    @TableField(value = "norms_id")
    @Property(value = "规格id")
    private String normsId;

    @TableField(exist = false)
    @Property(value = "规格信息")
    private Map<String, Object> normsMation;

    @TableField(value = "stock")
    @Property(value = "库存数量")
    private String stock;

}
