/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.store.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.SkyeyeFlowable;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: ProductTransfer
 * @Description: 门店产品调拨申请实体类（参照工单配件申领 SealApply）
 */
@Data
@RedisCacheField(name = "store:productTransfer", cacheTime = RedisConstants.TOW_MONTH_SECONDS)
@TableName(value = "shop_store_product_transfer", autoResultMap = true)
@ApiModel("门店产品调拨申请实体类")
public class ProductTransfer extends SkyeyeFlowable {

    @TableField(value = "`name`")
    @ApiModelProperty(value = "标题", required = "required")
    private String name;

    @TableField(value = "from_store_id")
    @ApiModelProperty(value = "原门店ID", required = "required")
    private String fromStoreId;

    @TableField(exist = false)
    @Property(value = "原门店信息")
    private ShopStore fromStoreMation;

    @TableField(value = "to_store_id")
    @ApiModelProperty(value = "目标门店ID", required = "required")
    private String toStoreId;

    @TableField(exist = false)
    @Property(value = "目标门店信息")
    private ShopStore toStoreMation;

    @TableField(value = "remark")
    @ApiModelProperty(value = "调拨原因")
    private String remark;

    @TableField(exist = false)
    @ApiModelProperty(value = "调拨明细", required = "required,json")
    private List<ProductTransferLink> applyLinkList;

}
