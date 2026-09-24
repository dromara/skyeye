/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.shop.entity;

import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName: StoreInventoryCheckItem
 * @Description: 门店库存盘点明细
 */
@Data
@ApiModel("门店库存盘点明细")
public class StoreInventoryCheckItem {

    @ApiModelProperty(value = "商品ID", required = "required")
    private String materialId;

    @ApiModelProperty(value = "规格ID", required = "required")
    private String normsId;

    @ApiModelProperty(value = "实盘数量（账面 + 增减后的数量）", required = "required")
    private String realNumber;

    @ApiModelProperty(value = "账面库存")
    private String bookStock;

    @ApiModelProperty(value = "盘盈数量")
    private String profitNum;

    @ApiModelProperty(value = "盘亏数量")
    private String lossNum;

    @ApiModelProperty(value = "盘盈条形码，多个换行分隔")
    private String profitNormsCode;

    @ApiModelProperty(value = "盘亏条形码，多个换行分隔")
    private String lossNormsCode;

    @ApiModelProperty(value = "商品名称")
    private String materialName;

    @ApiModelProperty(value = "规格名称")
    private String normsName;

}
