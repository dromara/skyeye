/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.shop.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

/**
 * 门店库存盘点历史明细
 */
@Data
@TableName(value = "shop_store_inventory_check_detail")
@ApiModel("门店库存盘点历史明细")
public class ShopStoreInventoryCheckDetail extends CommonInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id")
    private String id;

    @TableField("parent_id")
    @ApiModelProperty(value = "盘点主表id", required = "required")
    private String parentId;

    @TableField("material_id")
    @ApiModelProperty(value = "商品id", required = "required")
    private String materialId;

    @TableField("material_name")
    @ApiModelProperty(value = "商品名称")
    private String materialName;

    @TableField("norms_id")
    @ApiModelProperty(value = "规格id", required = "required")
    private String normsId;

    @TableField("norms_name")
    @ApiModelProperty(value = "规格名称")
    private String normsName;

    @TableField("book_stock")
    @ApiModelProperty(value = "账面库存")
    private String bookStock;

    @TableField("real_number")
    @ApiModelProperty(value = "实盘数量")
    private String realNumber;

    @TableField("diff_number")
    @ApiModelProperty(value = "差异数量")
    private String diffNumber;

    @TableField("profit_num")
    @ApiModelProperty(value = "盘盈数量")
    private String profitNum;

    @TableField("loss_num")
    @ApiModelProperty(value = "盘亏数量")
    private String lossNum;

    @TableField("profit_norms_code")
    @ApiModelProperty(value = "盘盈条形码")
    private String profitNormsCode;

    @TableField("loss_norms_code")
    @ApiModelProperty(value = "盘亏条形码")
    private String lossNormsCode;
}
