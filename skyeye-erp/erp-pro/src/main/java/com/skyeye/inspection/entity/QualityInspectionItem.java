/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.inspection.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.CommonInfo;
import com.skyeye.depot.entity.Depot;
import com.skyeye.material.entity.Material;
import com.skyeye.material.entity.MaterialNorms;
import lombok.Data;

import java.util.Map;

import com.skyeye.business.classenum.OrderItemQualityInspectionType;

/**
 * @ClassName: QualityInspectionItem
 * @Description: 质检单子单据实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/22 8:22
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "erp_quality_inspection_child", autoResultMap = true)
@ApiModel("质检单子单据实体类")
public class QualityInspectionItem extends CommonInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("parent_id")
    @Property("单据id")
    private String parentId;

    @TableField("material_id")
    @ApiModelProperty(value = "产品id", required = "required")
    private String materialId;

    @TableField(exist = false)
    @Property(value = "产品信息")
    private Material materialMation;

    @TableField("norms_id")
    @ApiModelProperty(value = "规格id", required = "required")
    private String normsId;

    @TableField(exist = false)
    @Property(value = "规格信息")
    private MaterialNorms normsMation;

    @TableField(value = "depot_id")
    @ApiModelProperty(value = "仓库id")
    private String depotId;

    @TableField(exist = false)
    @Property(value = "仓库信息")
    private Depot depotMation;

    @TableField(value = "unit_price")
    @ApiModelProperty(value = "单价", required = "double", defaultValue = "0")
    private String unitPrice;

    @TableField(value = "all_price")
    @ApiModelProperty(value = "不含税的总金额", defaultValue = "0")
    private String allPrice;

    @TableField(value = "tax_rate")
    @ApiModelProperty(value = "税率", defaultValue = "0")
    private String taxRate;

    @TableField(value = "tax_money")
    @ApiModelProperty(value = "税额", required = "double", defaultValue = "0")
    private String taxMoney;

    @TableField(value = "tax_unit_price")
    @ApiModelProperty(value = "含税单价", required = "double", defaultValue = "0")
    private String taxUnitPrice;

    @TableField(value = "tax_last_money")
    @ApiModelProperty(value = "价税合计", defaultValue = "0")
    private String taxLastMoney;

    @TableField("quality_inspection")
    @ApiModelProperty(value = "质检类型", required = "num", enumClass = OrderItemQualityInspectionType.class)
    private Integer qualityInspection;

    @TableField(exist = false)
    @Property(value = "质检类型信息")
    private Map<String, Object> qualityInspectionMation;

    @TableField("quality_inspection_ratio")
    @ApiModelProperty(value = "质检比例(%)，质检类型为抽检时才生效")
    private String qualityInspectionRatio;

    @TableField("oper_number")
    @ApiModelProperty(value = "质检数量", required = "required,num")
    private String operNumber;

    @TableField("qualified_number")
    @ApiModelProperty(value = "合格数量", required = "required,num")
    private String qualifiedNumber;

    @TableField("return_number")
    @ApiModelProperty(value = "验收退回数量", required = "required,num")
    private String returnNumber;

    @TableField("return_reason")
    @ApiModelProperty(value = "验收退回原因")
    private String returnReason;

    @TableField("concession_number")
    @ApiModelProperty(value = "让步接收数量", required = "required,num")
    private String concessionNumber;

    @TableField(value = "inspector_id")
    @ApiModelProperty(value = "质检员id")
    private String inspectorId;

    @TableField(exist = false)
    @Property(value = "质检员信息")
    private Map<String, Object> inspectorMation;

    @TableField("remark")
    @ApiModelProperty(value = "备注")
    private String remark;

    @TableField("exchanges_number")
    @ApiModelProperty(value = "换货数量", required = "required,num")
    private String exchangesNumber;

    @TableField("exchanges_reason")
    @ApiModelProperty(value = "换货原因")
    private String exchangesReason;
}
