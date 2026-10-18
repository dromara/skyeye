/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.shopmaterial.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.common.enumeration.IsDefaultEnum;
import com.skyeye.material.entity.Material;
import com.skyeye.material.entity.MaterialNorms;
import com.skyeye.shopmaterial.enums.ShopMaterialNormsLogoType;
import lombok.Data;

/**
 * @ClassName: ShopMaterialNorms
 * @Description: 商城商品规格参数实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/9/4 16:29
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "erp_shop_material_norms", autoResultMap = true)
@ApiModel("商城商品规格参数实体类")
public class ShopMaterialNorms extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "material_id")
    @Property(value = "产品id")
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

    @TableField(value = "is_default")
    @ApiModelProperty(value = "是否默认", enumClass = IsDefaultEnum.class, required = "required,num")
    private Integer isDefault;

    @TableField(value = "estimate_purchase_price")
    @ApiModelProperty(value = "采购价/成本价", required = "required,double")
    private String estimatePurchasePrice;

    @TableField(value = "sale_price")
    @ApiModelProperty(value = "销售价", required = "required,double")
    private String salePrice;

    @TableField(value = "logo_type")
    @ApiModelProperty(value = "logo类型", enumClass = ShopMaterialNormsLogoType.class, required = "required,num")
    private Integer logoType;

    @TableField(value = "logo")
    @ApiModelProperty(value = "商品规格图片")
    private String logo;

    @TableField(value = "carousel_img")
    @ApiModelProperty(value = "商品轮播图，多个逗号隔开")
    private String carouselImg;

    @TableField(value = "real_sales")
    @Property(value = "实际销量")
    private String realSales;

    @TableField(value = "big_type_id")
    @Property(value = "大类id，对应的商城的 shop_material_type表 的id")
    private String bigTypeId;

}
