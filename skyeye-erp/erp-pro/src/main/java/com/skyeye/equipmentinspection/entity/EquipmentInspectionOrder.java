/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.SkyeyeFlowable;
import com.skyeye.equipmentinspection.classenum.EquipmentInspectionResultType;
import com.skyeye.equipmentinspection.classenum.EquipmentInspectionRunStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: EquipmentInspectionOrder
 * @Description: 设备巡检单实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@UniqueField(value = {"oddNumber"})
@RedisCacheField(name = "erp:equipmentInspectionOrder:zcdemo", cacheTime = RedisConstants.TOW_MONTH_SECONDS)
@TableName(value = "erp_equipment_inspection_order_zcdemo", autoResultMap = true)
@ApiModel("设备巡检单实体类")
public class EquipmentInspectionOrder extends SkyeyeFlowable {

    @TableField(value = "odd_number", updateStrategy = FieldStrategy.NEVER)
    @Property(value = "设备巡检单编号", fuzzyLike = true)
    private String oddNumber;

    @TableField("equipment_id")
    @ApiModelProperty(value = "设备档案id", required = "required")
    private String equipmentId;

    @TableField("inspection_time")
    @ApiModelProperty(value = "巡检时间", required = "required")
    private String inspectionTime;

    @TableField("inspector_user_id")
    @ApiModelProperty(value = "巡检员用户id")
    private String inspectorUserId;

    @TableField(exist = false)
    @Property(value = "巡检员信息")
    private Map<String, Object> inspectorUserMation;

    @TableField("overall_result")
    @ApiModelProperty(value = "巡检结果", enumClass = EquipmentInspectionResultType.class, required = "num")
    private Integer overallResult;

    @TableField(exist = false)
    @Property(value = "巡检结果名称")
    private String overallResultName;

    @TableField("equipment_run_status")
    @ApiModelProperty(value = "本次巡检设备运行状态", enumClass = EquipmentInspectionRunStatus.class, required = "num")
    private Integer equipmentRunStatus;

    @TableField(exist = false)
    @Property(value = "设备运行状态名称")
    private String equipmentRunStatusName;

    @TableField("summary_richtext")
    @ApiModelProperty(value = "本次巡检总结")
    private String summaryRichtext;

    @TableField("header_location_text")
    @ApiModelProperty(value = "整单定位")
    private String headerLocationText;

    @TableField("header_longitude")
    @ApiModelProperty(value = "定位经度")
    private String headerLongitude;

    @TableField("header_latitude")
    @ApiModelProperty(value = "定位纬度")
    private String headerLatitude;

    @TableField("header_address")
    @ApiModelProperty(value = "定位地址")
    private String headerAddress;

    @TableField("header_photo_urls")
    @ApiModelProperty(value = "整单拍照URL，逗号分隔")
    private String headerPhotoUrls;

    @TableField("seq_in_day")
    @ApiModelProperty(value = "当日第几次巡检", required = "required,num")
    private Integer seqInDay;

    @TableField("biz_key_composite")
    @ApiModelProperty(value = "业务组合键")
    private String bizKeyComposite;

    @TableField("remark")
    @ApiModelProperty(value = "备注")
    private String remark;

    @TableField(exist = false)
    @Property(value = "设备档案信息")
    private Map<String, Object> equipmentMation;

    @TableField(exist = false)
    @ApiModelProperty(value = "巡检项目明细", required = "required,json")
    private List<EquipmentInspectionOrderItem> equipmentInspectionOrderItemList;

}
