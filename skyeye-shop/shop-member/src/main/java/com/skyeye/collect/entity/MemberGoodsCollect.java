/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.collect.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

import java.util.Map;

/**
 * 会员商品收藏
 */
@Data
@TableName(value = "shop_member_goods_collect")
@ApiModel("会员商品收藏")
public class MemberGoodsCollect extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id")
    private String id;

    @TableField("member_id")
    @ApiModelProperty(value = "会员id")
    private String memberId;

    @TableField("material_store_id")
    @ApiModelProperty(value = "门店商品关联id（详情页id）", required = "required")
    private String materialStoreId;

    @TableField(exist = false)
    @Property(value = "门店id（由商品关联回填）")
    private String storeId;

    @TableField(exist = false)
    @Property(value = "商城商品信息")
    private Map<String, Object> shopMaterial;

    @TableField(exist = false)
    @Property(value = "门店信息")
    private Map<String, Object> storeMation;

}
