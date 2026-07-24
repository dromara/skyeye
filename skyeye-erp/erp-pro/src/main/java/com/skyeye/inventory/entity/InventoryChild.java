/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.inventory.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.SkyeyeLinkData;
import com.skyeye.depot.entity.Depot;
import com.skyeye.material.entity.Material;
import com.skyeye.material.entity.MaterialNorms;
import lombok.Data;

import java.util.List;
import java.util.Map;

import com.skyeye.material.classenum.MaterialNormsCodeType;

/**
 * @ClassName: InventoryChild
 * @Description: 盘点任务表-子单据表信息
 * @author: skyeye云系列--卫志强
 * @date: 2022/3/23 16:19
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "erp_inventory_child")
@ApiModel("盘点任务表-子单据表信息")
public class InventoryChild extends SkyeyeLinkData {

    @TableField("odd_number")
    @Property(value = "单据编号", fuzzyLike = true)
    private String oddNumber;

    @TableField(value = "depot_id")
    @ApiModelProperty(value = "仓库id", required = "required")
    private String depotId;

    @TableField(exist = false)
    @Property(value = "仓库信息")
    private Depot depotMation;

    @TableField(value = "operator_id")
    @ApiModelProperty(value = "盘点人id", required = "required")
    private String operatorId;

    @TableField(exist = false)
    @Property(value = "盘点人信息")
    private Map<String, Object> operatorMation;

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

    @TableField(value = "plan_start_time")
    @ApiModelProperty(value = "计划开始时间", required = "required")
    private String planStartTime;

    @TableField(value = "plan_end_time")
    @ApiModelProperty(value = "计划结束时间", required = "required")
    private String planEndTime;

    @TableField(value = "plan_number")
    @Property(value = "计划盘点数量(账面数量)")
    private String planNumber;

    @TableField(value = "real_number")
    @Property(value = "实际盘点数量(实盘后的数量)")
    private String realNumber;

    @TableField(value = "profit_num")
    @Property(value = "盘盈数量")
    private String profitNum;

    @TableField(value = "loss_num")
    @Property(value = "盘亏数量")
    private String lossNum;

    @TableField(value = "unit_price")
    @ApiModelProperty(value = "单价", required = "double", defaultValue = "0")
    private String unitPrice;

    @TableField(value = "profit_price")
    @Property(value = "盘盈总金额")
    private String profitPrice;

    @TableField(value = "loss_price")
    @Property(value = "盘亏总金额")
    private String lossPrice;

    @TableField(value = "type")
    @ApiModelProperty(value = "盘点的商品的类型", enumClass = MaterialNormsCodeType.class)
    private Integer type;

    @TableField(exist = false)
    @Property(value = "盘点的商品的类型信息")
    private Map<String, Object> typeMation;

    @TableField(exist = false)
    @Property(value = "子单据关联的编码")
    private List<InventoryChildCode> inventoryChildCodeList;

    @TableField(value = "profit_norms_code")
    @ApiModelProperty(value = "盘盈明细的商品规格条形码编号")
    private String profitNormsCode;

    @TableField(value = "loss_norms_code")
    @ApiModelProperty(value = "盘亏明细的商品规格条形码编号")
    private String lossNormsCode;

}
