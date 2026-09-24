/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.shop.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

import java.util.List;

/**
 * 门店库存盘点历史主表
 */
@Data
@TableName(value = "shop_store_inventory_check")
@ApiModel("门店库存盘点历史")
public class ShopStoreInventoryCheck extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id")
    private String id;

    @TableField("store_id")
    @ApiModelProperty(value = "门店id", required = "required")
    private String storeId;

    @TableField("item_count")
    @ApiModelProperty(value = "调整明细条数")
    private Integer itemCount;

    @TableField("oper_name")
    @ApiModelProperty(value = "操作人姓名")
    private String operName;

    @TableField(exist = false)
    @ApiModelProperty(value = "盘点明细")
    private List<ShopStoreInventoryCheckDetail> detailList;
}
