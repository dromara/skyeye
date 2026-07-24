/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.repair.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.equipment.classenum.EquipmentState;
import com.skyeye.repair.classenum.EquipmentFaultCategory;
import com.skyeye.repair.classenum.EquipmentRepairAuditOpinion;
import com.skyeye.repair.classenum.EquipmentRepairFaultReason;
import com.skyeye.repair.classenum.EquipmentRepairFromType;
import com.skyeye.repair.classenum.EquipmentRepairOrderState;
import com.skyeye.repair.classenum.EquipmentRepairTeam;
import com.skyeye.supplier.entity.Supplier;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: EquipmentRepairOrder
 * @Description: 设备维修单实体类
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/19
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "erp_equipment_repair_order", autoResultMap = true)
@ApiModel("设备维修单实体类")
public class EquipmentRepairOrder extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("odd_number")
    @Property(value = "单据编号", fuzzyLike = true)
    private String oddNumber;

    @TableField(value = "state")
    @ApiModelProperty(value = "状态", enumClass = EquipmentRepairOrderState.class, required = "num")
    private Integer state;

    @TableField(exist = false)
    @Property(value = "状态信息")
    private Map<String, Object> stateMation;

    @TableField(value = "urgency_id")
    @ApiModelProperty(value = "紧急程度，参考数据字典", required = "required")
    private String urgencyId;

    @TableField(exist = false)
    @Property(value = "紧急程度字典信息")
    private Map<String, Object> urgencyMation;

    @TableField(value = "equipment_id")
    @ApiModelProperty(value = "设备id", required = "required")
    private String equipmentId;

    @TableField(exist = false)
    @Property(value = "设备信息")
    private Map<String, Object> equipmentMation;

    @TableField(value = "equipment_state")
    @ApiModelProperty(value = "设备状态", enumClass = EquipmentState.class)
    private Integer equipmentState;

    @TableField(value = "from_type_id")
    @ApiModelProperty(value = "来源单据类型", enumClass = EquipmentRepairFromType.class)
    private Integer fromTypeId;

    @TableField(exist = false)
    @Property(value = "来源单据类型信息")
    private Map<String, Object> fromTypeMation;

    @TableField(value = "from_id")
    @ApiModelProperty(value = "来源单据id")
    private String fromId;

    @TableField(exist = false)
    @Property(value = "来源单据信息")
    private Map<String, Object> fromMation;

    @TableField(value = "fault_brief")
    @ApiModelProperty(value = "故障描述", required = "required")
    private String faultBrief;

    @TableField(value = "fault_photo")
    @ApiModelProperty(value = "故障情况拍照")
    private String faultPhoto;

    @TableField(value = "fault_video")
    @ApiModelProperty(value = "故障情况视频")
    private String faultVideo;

    @TableField(value = "report_time")
    @Property(value = "报修时间")
    private String reportTime;

    @TableField(value = "user_id")
    @Property(value = "报修人")
    private String userId;

    @TableField(exist = false)
    @Property(value = "报修人信息")
    private Map<String, Object> userMation;

    @TableField(value = "service_user_id")
    @ApiModelProperty(value = "维修负责人")
    private String serviceUserId;

    @TableField(exist = false)
    @Property(value = "维修负责人信息")
    private Map<String, Object> serviceUserMation;

    @TableField(value = "fault_type")
    @ApiModelProperty(value = "故障类别", enumClass = EquipmentFaultCategory.class, required = "required,num")
    private Integer faultType;

    @TableField(exist = false)
    @Property(value = "故障类别信息")
    private Map<String, Object> faultTypeMation;

    @TableField(value = "repair_team")
    @ApiModelProperty(value = "维修班组", enumClass = EquipmentRepairTeam.class, required = "num")
    private Integer repairTeam;

    @TableField(exist = false)
    @Property(value = "维修班组信息")
    private Map<String, Object> repairTeamMation;

    @TableField(value = "service_time")
    @ApiModelProperty(value = "派工时间")
    private String serviceTime;

    @TableField(value = "response_hours")
    @ApiModelProperty(value = "故障响应时长(小时)", required = "double")
    private String responseHours;

    @TableField(value = "audit_opinion")
    @ApiModelProperty(value = "维修类型", enumClass = EquipmentRepairAuditOpinion.class, required = "num")
    private Integer auditOpinion;

    @TableField(exist = false)
    @Property(value = "审核意见信息")
    private Map<String, Object> auditOpinionMation;

    @TableField(value = "is_repaired")
    @ApiModelProperty(value = "是否进行维修", enumClass = WhetherEnum.class, required = "num")
    private Integer isRepaired;

    @TableField(value = "fault_reason")
    @ApiModelProperty(value = "故障原因", enumClass = EquipmentRepairFaultReason.class, required = "num")
    private Integer faultReason;

    @TableField(exist = false)
    @Property(value = "故障原因信息")
    private Map<String, Object> faultReasonMation;

    @TableField(value = "is_replace_spare")
    @ApiModelProperty(value = "是否更换配件", enumClass = WhetherEnum.class, required = "num")
    private Integer isReplaceSpare;

    @TableField(value = "supplier_id")
    @ApiModelProperty(value = "供应商")
    private String supplierId;

    @TableField(exist = false)
    @Property(value = "供应商信息")
    private Supplier supplierMation;

    @TableField(value = "cancel_reason")
    @ApiModelProperty(value = "作废原因")
    private String cancelReason;

    @TableField(value = "repair_desc")
    @ApiModelProperty(value = "维修情况说明")
    private String repairDesc;

    @TableField(value = "repair_finish_photo")
    @ApiModelProperty(value = "维修完成拍照")
    private String repairFinishPhoto;

    @TableField(value = "repair_finish_time")
    @ApiModelProperty(value = "维修完成时间")
    private String repairFinishTime;

    @TableField(value = "evaluate_type_id")
    @ApiModelProperty(value = "评价类型，参考数据字典")
    private String evaluateTypeId;

    @TableField(exist = false)
    @Property(value = "评价类型字典信息")
    private Map<String, Object> evaluateTypeMation;

    @TableField(value = "evaluate_content")
    @ApiModelProperty(value = "评价内容")
    private String evaluateContent;

    @TableField(value = "is_fixed")
    @ApiModelProperty(value = "是否修复", enumClass = WhetherEnum.class, required = "num")
    private Integer isFixed;

    @TableField(exist = false)
    @ApiModelProperty(value = "备件使用明细列表", required = "json")
    private List<EquipmentSparePartUsageDetail> sparePartUsageList;

    @TableField(exist = false)
    @Property(value = "维修失败履历列表")
    private List<EquipmentRepairFailRecord> failRecordList;

}
