/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.store.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.SkyeyeLinkData;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: ProductTransferLink
 * @Description: 门店产品调拨明细
 */
@Data
@TableName(value = "shop_store_product_transfer_link", autoResultMap = true)
@ApiModel("门店产品调拨明细")
public class ProductTransferLink extends SkyeyeLinkData {

    @TableField(value = "material_id")
    @ApiModelProperty(value = "产品ID", required = "required")
    private String materialId;

    @TableField(exist = false)
    @Property(value = "产品信息")
    private Map<String, Object> materialMation;

    @TableField(value = "norms_id")
    @ApiModelProperty(value = "规格ID", required = "required")
    private String normsId;

    @TableField(exist = false)
    @Property(value = "规格信息")
    private Map<String, Object> normsMation;

    @TableField("oper_number")
    @ApiModelProperty(value = "调拨数量", required = "required,num")
    private String operNumber;

}
