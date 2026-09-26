/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.store.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.common.enumeration.WhetherEnum;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: ShopTradeCart
 * @Description: 品牌管理实体类
 * @author: skyeye云系列--卫志强
 * @date: 2022/2/4 10:12
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "shop_trade_cart")
@ApiModel("购物车管理实体类")
public class ShopTradeCart extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("store_id")
    @ApiModelProperty(value = "门店id", required = "required")
    private String storeId;

    @TableField(exist = false)
    @ApiModelProperty(value = "门店信息")
    private Map<String, Object> storeMation;

    @TableField("material_id")
    @ApiModelProperty(value = "商品id", required = "required")
    private String materialId;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品信息")
    private Map<String, Object> materialMation;

    @TableField(exist = false)
    @ApiModelProperty(value = "商城商品信息")
    private Map<String, Object> shopMaterialMation;

    @TableField("norms_id")
    @ApiModelProperty(value = "规格id", required = "required")
    private String normsId;

    @TableField(exist = false)
    @ApiModelProperty(value = "規格信息")
    private Map<String, Object> normsMation;

    @TableField("count")
    @ApiModelProperty(value = "数量", required = "required")
    private String count;

    @TableField("selected")
    @ApiModelProperty(value = "是否选中", required = "required,num", enumClass = WhetherEnum.class)
    private Integer selected;
}
