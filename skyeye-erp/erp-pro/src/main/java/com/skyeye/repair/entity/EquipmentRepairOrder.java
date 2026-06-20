/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.repair.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.SkyeyeFlowable;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.repair.classenum.EquipmentFaultCategory;
import com.skyeye.repair.classenum.EquipmentRepairAuditOpinion;
import com.skyeye.repair.classenum.EquipmentRepairCancelReason;
import com.skyeye.repair.classenum.EquipmentRepairEquipmentStatus;
import com.skyeye.repair.classenum.EquipmentRepairFaultReason;
import com.skyeye.repair.classenum.EquipmentRepairTeam;
import com.skyeye.repair.classenum.EquipmentRepairUrgency;
import com.skyeye.sparepart.entity.EquipmentSparePartRequisition;
import com.skyeye.supplier.entity.Supplier;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: EquipmentRepairOrder
 * @Description: 设备维修主表实体类
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/19
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "erp_equipment_repair_order", autoResultMap = true)
@ApiModel("设备维修主表实体类")
public class EquipmentRepairOrder extends SkyeyeFlowable {

    @TableField(value = "urgency_level")
    @ApiModelProperty(value = "紧急程度", enumClass = EquipmentRepairUrgency.class, required = "required,num")
    private Integer urgencyLevel;

    @TableField(value = "equipment_id")
    @ApiModelProperty(value = "设备id", required = "required")
    private String equipmentId;

    @TableField(exist = false)
    @Property(value = "设备信息")
    private Map<String, Object> equipmentMation;

    @TableField(value = "fault_desc")
    @ApiModelProperty(value = "故障描述", fuzzyLike = true)
    private String faultBrief;

    @TableField(value = "fault_photo")
    @ApiModelProperty(value = "故障情况拍照")
    private String faultPhoto;

    @TableField(value = "fault_video")
    @ApiModelProperty(value = "故障情况视频")
    private String faultVideo;

    @TableField(value = "report_time")
    @ApiModelProperty(value = "报修时间")
    private String reportTime;

    @TableField(value = "user_id")
    @ApiModelProperty(value = "报修人用户ID")
    private String userId;

    @TableField(exist = false)
    @Property(value = "报修人信息")
    private Map<String, Object> userMation;

    @TableField(value = "reject_reason")
    @ApiModelProperty(value = "驳回原因")
    private String rejectReason;

    @TableField(value = "staff_id")
    @ApiModelProperty(value = "维修负责人员工ID")
    private String staffId;

    @TableField(exist = false)
    @Property(value = "维修负责人信息")
    private Map<String, Object> staffMation;

    @TableField(value = "fault_type")
    @ApiModelProperty(value = "故障类别", enumClass = EquipmentFaultCategory.class, required = "num")
    private Integer faultCategory;

    @TableField(value = "repairTeam")
    @ApiModelProperty(value = "维修班组", enumClass = EquipmentRepairTeam.class, required = "num")
    private Integer repairTeam;

    @TableField(value = "dispatch_time")
    @ApiModelProperty(value = "派工时间")
    private String dispatchTime;

    @TableField(value = "response_hours")
    @ApiModelProperty(value = "故障响应时长(小时)")
    private Double responseHours;

    @TableField(value = "is_repaired")
    @ApiModelProperty(value = "是否已进行维修", enumClass = WhetherEnum.class, required = "num")
    private Integer isRepaired;

    @TableField(value = "fault_reason")
    @ApiModelProperty(value = "故障原因", enumClass = EquipmentRepairFaultReason.class, required = "num")
    private Integer faultReasonType;

    @TableField(value = "is_replace_spare")
    @ApiModelProperty(value = "是否已更换配件", enumClass = WhetherEnum.class, required = "num")
    private Integer isReplaceSpare;

    @TableField(value = "audit_opinion")
    @ApiModelProperty(value = "审核意见", enumClass = EquipmentRepairAuditOpinion.class, required = "num")
    private Integer auditOpinion;

    
    
    @TableField(value = "supplier_id")
    @ApiModelProperty(value = "供应商ID")
    private String supplierId;


    @TableField(exist = false)
    @Property(value = "供应商信息")
    private Supplier supplierMation;
     

    @TableField(value = "cancel_reason")
    @ApiModelProperty(value = "作废原因", enumClass = EquipmentRepairCancelReason.class, required = "num")
    private Integer cancelReasonType;

    @TableField(value = "repair_desc")
    @ApiModelProperty(value = "维修情况说明")
    private String repairDesc;

    @TableField(value = "repair_finish_photo")
    @ApiModelProperty(value = "维修完成拍照")
    private String repairFinishPhoto;

    @TableField(value = "repair_finish_time")
    @ApiModelProperty(value = "维修完成时间")
    private String repairFinishTime;

    @TableField(value = "repair_hours")
    @ApiModelProperty(value = "维修时长(小时)")
    private Double repairHours;

    @TableField(value = "is_fixed")
    @ApiModelProperty(value = "是否修复", enumClass = WhetherEnum.class, required = "num")
    private Integer isFixed;

    @TableField(value = "repair_score")
    @ApiModelProperty(value = "维修评分1-10分")
    private Integer repairScore;

    @TableField(value = "repair_comment")
    @ApiModelProperty(value = "维修评价")
    private String repairComment;

    @TableField(exist = false)
    @Property(value = "备件信息")
    @ApiModelProperty(value = "备件领用单列表", required = "json")
    private List<EquipmentSparePartRequisition> sparePartRequisitionList;
}