/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.browse.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

/**
 * 门店浏览日汇总（访客数 / 浏览量）
 * <p>表：shop_store_browse_stat_daily，唯一键 store_id + stat_date；仅保留近半年</p>
 */
@Data
@TableName(value = "shop_store_browse_stat_daily")
@ApiModel("门店浏览日汇总")
public class ShopStoreBrowseStatDaily extends CommonInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id")
    private String id;

    @TableField("store_id")
    @ApiModelProperty(value = "门店id")
    private String storeId;

    @TableField("stat_date")
    @ApiModelProperty(value = "统计日期 yyyy-MM-dd")
    private String statDate;

    @TableField("visitor_count")
    @ApiModelProperty(value = "访客数（UV）")
    private String visitorCount;

    @TableField("pv_count")
    @ApiModelProperty(value = "浏览量（PV）")
    private String pvCount;

    @TableField("create_time")
    @ApiModelProperty(value = "创建时间")
    private String createTime;
}
