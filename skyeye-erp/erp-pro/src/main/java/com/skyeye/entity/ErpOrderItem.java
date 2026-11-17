/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.business.classenum.OrderItemQualityInspectionType;
import com.skyeye.common.entity.features.SkyeyeLinkData;
import com.skyeye.depot.entity.Depot;
import com.skyeye.material.classenum.MaterialInOrderType;
import com.skyeye.material.entity.Material;
import com.skyeye.material.entity.MaterialNorms;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ErpOrderItem
 * @Description: ERP相关订单关联的产品信息
 * @author: skyeye云系列--卫志强
 * @date: 2022/3/23 16:19
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "erp_depotitem")
@ApiModel("ERP相关订单关联的产品信息实体类")
public class ErpOrderItem extends SkyeyeLinkData {

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

    @TableField("oper_number")
    @ApiModelProperty(value = "数量", required = "required,num")
    private String operNumber;

    @TableField(exist = false)
    @ApiModelProperty(value = "未出入库数量")
    private String notUseNumber;

    @TableField(value = "unit_price")
    @ApiModelProperty(value = "单价", required = "double", defaultValue = "0")
    private String unitPrice;

    @TableField(value = "all_price")
    @ApiModelProperty(value = "不含税的总金额", defaultValue = "0")
    private String allPrice;

    @TableField(value = "depot_id")
    @ApiModelProperty(value = "仓库id")
    private String depotId;

    @TableField(exist = false)
    @Property(value = "仓库信息")
    private Depot depotMation;

    @TableField(value = "level_val_id")
    @ApiModelProperty(value = "库位值id（3D库位；可选，审核出入库后回写库位占用）")
    private String levelValId;

    @TableField(value = "another_depot_id")
    @ApiModelProperty(value = "调拨时，对方仓库Id")
    private String anotherDepotId;

    @TableField(exist = false)
    @Property(value = "调拨时，对方仓库信息")
    private Depot anotherDepotMation;

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

    @TableField("m_type")
    @ApiModelProperty(value = "商品在单据中的类型", enumClass = MaterialInOrderType.class, required = "num")
    private Integer mType;

    @TableField(exist = false)
    @Property(value = "商品在单据中的类型信息")
    private Map<String, Object> mTypeMation;

    @TableField("quality_inspection")
    @ApiModelProperty(value = "质检类型", enumClass = OrderItemQualityInspectionType.class, required = "num")
    private Integer qualityInspection;

    @TableField(exist = false)
    @Property(value = "质检类型信息")
    private Map<String, Object> qualityInspectionMation;

    @TableField("quality_inspection_ratio")
    @ApiModelProperty(value = "质检比例(%)，质检类型为抽检时才生效")
    private String qualityInspectionRatio;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品规格条形码编号")
    private String normsCode;

    @TableField(exist = false)
    @Property(value = "商品规格条形码编号集合")
    private List<String> normsCodeList;

    @TableField("delivery_time")
    @ApiModelProperty(value = "交货日期")
    private String deliveryTime;

}
