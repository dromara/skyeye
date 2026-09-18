/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.browse.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

/**
 * 会员商品浏览足迹
 */
@Data
@TableName(value = "shop_member_browse_history")
@ApiModel("会员商品浏览足迹")
public class MemberBrowseHistory extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id")
    private String id;

    @TableField("member_id")
    @ApiModelProperty(value = "会员id")
    private String memberId;

    @TableField("material_store_id")
    @ApiModelProperty(value = "门店商品关联id（详情页id）", required = "required")
    private String materialStoreId;

    @TableField("material_id")
    @ApiModelProperty(value = "商品id")
    private String materialId;

    @TableField("store_id")
    @ApiModelProperty(value = "门店id")
    private String storeId;

    @TableField("goods_name")
    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    @TableField("goods_logo")
    @ApiModelProperty(value = "商品主图")
    private String goodsLogo;

    @TableField("price")
    @ApiModelProperty(value = "展示价格")
    private String price;

    @TableField("store_name")
    @ApiModelProperty(value = "门店名称")
    private String storeName;

    @TableField("view_count")
    @ApiModelProperty(value = "浏览次数")
    private Integer viewCount;

    @TableField("last_view_time")
    @ApiModelProperty(value = "最近浏览时间")
    private String lastViewTime;

}
