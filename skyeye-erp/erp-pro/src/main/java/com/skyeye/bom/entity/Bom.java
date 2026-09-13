/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.bom.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.CacheConstants;
import com.skyeye.common.entity.features.Version;
import com.skyeye.material.entity.Material;
import com.skyeye.material.entity.MaterialNorms;
import com.skyeye.procedure.entity.WayProcedure;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: Bom
 * @Description: bom表实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/3/27 14:09
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = CacheConstants.MES_BOM_CACHE_KEY)
@TableName(value = "erp_bom")
@ApiModel("bom表实体类")
public class Bom extends Version {

    @TableField("`name`")
    @ApiModelProperty(value = "名称", required = "required", fuzzyLike = true)
    private String name;

    @TableField("remark")
    @ApiModelProperty("相关描述")
    private String remark;

    @TableField(value = "cad_content")
    @ApiModelProperty(value = "CAD图纸json")
    private String cadContent;

    @TableField(value = "material_id")
    @ApiModelProperty(value = "商品id", required = "required")
    private String materialId;

    @TableField(exist = false)
    @Property(value = "商品信息")
    private Material materialMation;

    @TableField(value = "norms_id")
    @ApiModelProperty(value = "规格id", required = "required")
    private String normsId;

    @TableField(exist = false)
    @Property(value = "规格信息")
    private MaterialNorms normsMation;

    @TableField(value = "make_num")
    @ApiModelProperty(value = "制造的数量", required = "required,num")
    private Integer makeNum;

    @TableField(value = "consumables_price")
    @Property(value = "耗材总费用")
    private String consumablesPrice;

    @TableField(value = "procedure_price")
    @Property(value = "工序总费用")
    private String procedurePrice;

    @TableField(value = "all_price")
    @Property(value = "总费用")
    private String allPrice;

    @TableField(exist = false)
    @ApiModelProperty(value = "子件清单", required = "required,json")
    private List<BomChild> bomChildList;

    @TableField(exist = false)
    @ApiModelProperty(value = "工序耗材列表", required = "json")
    private List<BomProcedureConsumables> procedureConsumablesList;

    @TableField(value = "way_procedure_id")
    @ApiModelProperty(value = "工艺id", required = "required")
    private String wayProcedureId;

    @TableField(exist = false)
    @Property(value = "工艺信息")
    private WayProcedure wayProcedureMation;

    @TableField(value = "delete_flag")
    private Integer deleteFlag;

}
