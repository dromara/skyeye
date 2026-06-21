/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.shop.entity;

import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName: StoreProductTransferLink
 * @Description: 门店产品库存调拨明细（Feign / 直连 ERP 入参，参照 EquipmentSparePartApplyLink）
 */
@Data
@ApiModel("门店产品库存调拨明细")
public class StoreProductTransferLink {

    @ApiModelProperty(value = "产品ID", required = "required")
    private String materialId;

    @ApiModelProperty(value = "规格ID", required = "required")
    private String normsId;

    @ApiModelProperty(value = "调拨数量", required = "required,num")
    private String operNumber;

}
