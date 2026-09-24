/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.shop.entity;

import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: StoreInventoryCheckConfirm
 * @Description: 门店库存盘点确认参数
 */
@Data
@ApiModel("门店库存盘点确认参数")
public class StoreInventoryCheckConfirm {

    @ApiModelProperty(value = "门店ID", required = "required")
    private String storeId;

    @ApiModelProperty(value = "盘点明细", required = "required,json")
    private List<StoreInventoryCheckItem> itemList;

}
