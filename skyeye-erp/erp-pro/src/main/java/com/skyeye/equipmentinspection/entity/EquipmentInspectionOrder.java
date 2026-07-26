/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.equipment.entity.Equipment;
import com.skyeye.equipmentinspection.classenum.EquipmentInspectionAssignType;
import com.skyeye.equipmentinspection.classenum.EquipmentInspectionCheckResult;
import com.skyeye.equipmentinspection.classenum.EquipmentInspectionOrderState;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: EquipmentInspectionOrder
 * @Description: 设备巡检单实体类
 */
@Data
@RedisCacheField(name = "erp:equipmentInspectionOrder", cacheTime = RedisConstants.TOW_MONTH_SECONDS)
@TableName(value = "erp_equipment_inspection_order", autoResultMap = true)
@ApiModel("设备巡检单实体类")
public class EquipmentInspectionOrder extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "odd_number", updateStrategy = FieldStrategy.NEVER)
    @Property(value = "巡检单编号", fuzzyLike = true)
    private String oddNumber;

    @TableField(value = "plan_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "巡检方案id", required = "required")
    private String planId;

    @TableField(exist = false)
    @Property(value = "巡检方案信息")
    private EquipmentInspectionPlan planMation;

    @TableField(value = "equipment_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "设备id", required = "required")
    private String equipmentId;

    @TableField(exist = false)
    @Property(value = "设备信息")
    private Equipment equipmentMation;

    @TableField(value = "state")
    @ApiModelProperty(value = "状态", enumClass = EquipmentInspectionOrderState.class, required = "num")
    private Integer state;

    @TableField(exist = false)
    @Property(value = "状态信息")
    private Map<String, Object> stateMation;

    @TableField(value = "assign_type")
    @ApiModelProperty(value = "巡检员指派方式", enumClass = EquipmentInspectionAssignType.class)
    private String assignType;

    @TableField(exist = false)
    @Property(value = "巡检员指派方式信息")
    private Map<String, Object> assignTypeMation;

    @TableField(value = "service_user_id")
    @ApiModelProperty(value = "巡检员id")
    private String serviceUserId;

    @TableField(exist = false)
    @Property(value = "巡检员信息")
    private Map<String, Object> serviceUserMation;

    @TableField(value = "service_time")
    @Property(value = "派工时间")
    private String serviceTime;

    @TableField(value = "cooperation_user_id", typeHandler = JacksonTypeHandler.class)
    @ApiModelProperty(value = "协助巡检员id", required = "json")
    private List<String> cooperationUserId;

    @TableField(exist = false)
    @Property(value = "协助巡检员信息")
    private List<Map<String, Object>> cooperationUserMation;

    @TableField(value = "inspection_time")
    @ApiModelProperty(value = "巡检时间，格式yyyy-MM-dd HH:mm:ss")
    private String inspectionTime;

    @TableField(value = "check_result")
    @ApiModelProperty(value = "检查结果", enumClass = EquipmentInspectionCheckResult.class, required = "num")
    private Integer checkResult;

    @TableField(exist = false)
    @Property(value = "检查结果信息")
    private Map<String, Object> checkResultMation;

    @TableField(value = "summary")
    @ApiModelProperty(value = "本次巡检总结")
    private String summary;

    @TableField(value = "photo_urls")
    @ApiModelProperty(value = "拍照URL，多个逗号分隔")
    private String photoUrls;

    @TableField(value = "location_text")
    @ApiModelProperty(value = "定位文本")
    private String locationText;

    @TableField(value = "longitude")
    @ApiModelProperty(value = "经度")
    private String longitude;

    @TableField(value = "latitude")
    @ApiModelProperty(value = "纬度")
    private String latitude;

    @TableField(value = "address")
    @ApiModelProperty(value = "定位地址")
    private String address;

    @TableField(value = "plan_date")
    @ApiModelProperty(value = "计划所属日期 yyyy-MM-dd（可由计划开始时刻推导，便于按日统计）")
    private String planDate;

    @TableField(value = "planned_start_time")
    @ApiModelProperty(value = "计划开始执行时间，格式yyyy-MM-dd HH:mm:ss")
    private String plannedStartTime;

    @TableField(value = "slot_index")
    @ApiModelProperty(value = "当日第几次巡检槽位", required = "num")
    private Integer slotIndex;

    @TableField(value = "inspected_count")
    @ApiModelProperty(value = "本单已巡检次数（对齐套餐 useNum 累计；须达方案 inspectionsPerDay 才可提交结果）", required = "num")
    private Integer inspectedCount;

    @TableField(value = "repair_order_id")
    @Property(value = "转维修后的维修单id")
    private String repairOrderId;

    @TableField(value = "remark")
    @ApiModelProperty(value = "备注")
    private String remark;

}
