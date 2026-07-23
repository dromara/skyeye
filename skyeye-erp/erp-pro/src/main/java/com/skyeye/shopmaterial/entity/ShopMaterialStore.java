/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.shopmaterial.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.enumeration.ShopMaterialDeliveryMethod;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.material.entity.Material;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ShopMaterialStore
 * @Description: 商城商品上线的门店实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/9/4 16:29
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "erp_shop_material_store", autoResultMap = true)
@ApiModel("商城商品上线的门店实体类")
public class ShopMaterialStore extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "material_id")
    @Property(value = "产品id")
    private String materialId;

    @TableField(exist = false)
    @Property(value = "商品信息")
    private Material materialMation;

    @TableField(value = "store_id")
    @Property(value = "门店id")
    private String storeId;

    @TableField(exist = false)
    @Property(value = "门店信息")
    private Map<String, Object> storeMation;

    @TableField(exist = false)
    @Property(value = "上架的商品信息")
    private ShopMaterial shopMaterial;

    @TableField(value = "is_launch_store")
    @ApiModelProperty(value = "是否添加到门店", enumClass = WhetherEnum.class)
    private Integer isLaunchStore;

    @TableField(value = "is_launch_shop")
    @ApiModelProperty(value = "是否上架到商城", enumClass = WhetherEnum.class)
    private Integer isLaunchShop;

    @TableField(value = "store_enabled")
    @ApiModelProperty(value = "门店状态", enumClass = EnableEnum.class)
    private Integer storeEnabled;

    @TableField(value = "big_type_id")
    @Property(value = "大类id，对应的商城的 shop_material_type表 的id")
    private String bigTypeId;

    @TableField(value = "delivery_method", typeHandler = JacksonTypeHandler.class)
    @ApiModelProperty(value = "配送方式，给门店选择的配送方式", enumClass = ShopMaterialDeliveryMethod.class, required = "json")
    private List<String> deliveryMethod;

}
