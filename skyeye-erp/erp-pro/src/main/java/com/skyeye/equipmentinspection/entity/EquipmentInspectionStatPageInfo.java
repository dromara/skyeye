/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.entity;

import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.equipmentinspection.classenum.EquipmentInspectionResultType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName: EquipmentInspectionStatPageInfo
 * @Description: 设备巡检统计查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("设备巡检统计查询条件")
public class EquipmentInspectionStatPageInfo extends CommonPageInfo implements Serializable {

    @ApiModelProperty(value = "巡检单id")
    private String id;

    @ApiModelProperty(value = "巡检结果", enumClass = EquipmentInspectionResultType.class)
    private Integer overallResult;

    @ApiModelProperty(value = "设备id列表", required = "hidden")
    private List<String> equipmentIdList;

    @ApiModelProperty(value = "仅漏检", required = "hidden")
    private Integer onlyMissed;

    @ApiModelProperty(value = "分布类型", required = "hidden")
    private String distributionType;

}
