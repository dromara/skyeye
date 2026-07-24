/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.inventory.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.CommonInfo;
import com.skyeye.material.entity.Material;
import com.skyeye.material.entity.MaterialNorms;
import lombok.Data;

import com.skyeye.material.classenum.MaterialNormsCodeInDepot;
import com.skyeye.material.classenum.MaterialNormsCodeType;

/**
 * @ClassName: InventoryChildCode
 * @Description: 盘点任务表-子单据关联的编码实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/18 17:14
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "erp_inventory_child_code")
@ApiModel("盘点任务表-子单据关联的编码实体类")
public class InventoryChildCode extends CommonInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("order_id")
    @Property("订单id")
    private String orderId;

    @TableField("parent_id")
    @Property("父节点id")
    private String parentId;

    @TableField("material_id")
    @Property(value = "产品id")
    private String materialId;

    @TableField(exist = false)
    @Property(value = "产品信息")
    private Material materialMation;

    @TableField("norms_id")
    @Property(value = "规格id")
    private String normsId;

    @TableField(exist = false)
    @Property(value = "规格信息")
    private MaterialNorms normsMation;

    @TableField(value = "code_num", updateStrategy = FieldStrategy.NEVER)
    @Property(value = "规格物品编码", fuzzyLike = true)
    private String codeNum;

    @TableField(value = "in_depot")
    @Property(value = "库存状态", enumClass = MaterialNormsCodeInDepot.class)
    private Integer inDepot;

    @TableField(value = "type")
    @Property(value = "类型", enumClass = MaterialNormsCodeType.class)
    private Integer type;

}
