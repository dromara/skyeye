/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.sparepart.entity;

import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

import java.util.List;

/**
 * 备件申领单出库后修改我的库存
 */
@Data
@ApiModel("备件申领单修改库存实体类")
public class EquipmentSparePartApplyChangeStock extends CommonInfo {

    @ApiModelProperty(value = "主键id", required = "required")
    private String id;

    @ApiModelProperty(value = "创建人id", required = "required")
    private String createId;

    @ApiModelProperty(value = "备件申领明细", required = "required,json")
    private List<EquipmentSparePartApplyLink> applyLinkList;

}
