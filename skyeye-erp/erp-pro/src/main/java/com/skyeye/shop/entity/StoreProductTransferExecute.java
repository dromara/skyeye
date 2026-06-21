/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.shop.entity;

import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: StoreProductTransferExecute
 * @Description: 执行门店产品库存调拨参数（参照 EquipmentSparePartApplyChangeStock）
 */
@Data
@ApiModel("执行门店产品库存调拨参数")
public class StoreProductTransferExecute {

    @ApiModelProperty(value = "原门店ID", required = "required")
    private String fromStoreId;

    @ApiModelProperty(value = "目标门店ID", required = "required")
    private String toStoreId;

    @ApiModelProperty(value = "调拨明细", required = "required,json")
    private List<StoreProductTransferLink> applyLinkList;

}
