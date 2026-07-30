/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.store.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.constans.CacheConstants;
import com.skyeye.common.entity.features.AreaInfo;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.enumeration.WhetherEnum;
import lombok.Data;

import java.util.List;
import java.util.Map;

import com.skyeye.store.classenum.StoreOnlineBookType;

/**
 * @ClassName: ShopStore
 * @Description: 门店管理实体类
 * @author: skyeye云系列--卫志强
 * @date: 2022/2/4 12:39
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField
@RedisCacheField(name = CacheConstants.SHOP_STORE_CACHE_KEY)
@TableName(value = "shop_store", autoResultMap = true)
@ApiModel("门店管理实体类")
public class ShopStore extends AreaInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "`name`")
    @ApiModelProperty(value = "门店名称", required = "required", fuzzyLike = true)
    private String name;

    @TableField(value = "logo")
    @ApiModelProperty(value = "logo图片")
    private String logo;

    @TableField(value = "img")
    @ApiModelProperty(value = "背景图")
    private String img;

    @TableField(value = "shop_area_id")
    @ApiModelProperty(value = "区域ID", required = "required")
    private String shopAreaId;

    @TableField(exist = false)
    @Property(value = "区域信息")
    private ShopArea shopAreaMation;

    @TableField(value = "remark")
    @ApiModelProperty(value = "备注")
    private String remark;

    @TableField(value = "enabled")
    @ApiModelProperty(value = "启用状态", enumClass = EnableEnum.class, required = "required,num")
    private Integer enabled;

    @TableField(value = "longitude")
    @ApiModelProperty(value = "经度")
    private String longitude;

    @TableField(value = "latitude")
    @ApiModelProperty(value = "纬度")
    private String latitude;

    @TableField(exist = false)
    @Property(value = "两点之间的距离，单位：米")
    private Double distance;

    @TableField("start_time")
    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @TableField("end_time")
    @ApiModelProperty(value = "结束时间")
    private String endTime;

    @TableField(value = "online_book_appoint")
    @ApiModelProperty(value = "是否开启线上预约", enumClass = WhetherEnum.class, required = "num")
    private Integer onlineBookAppoint;

    @TableField(value = "online_book_radix")
    @ApiModelProperty(value = "线上预约基数，以分钟为单位，如果设置为30，则会自动计算在营业时间段内的可预约时间段", required = "num")
    private Integer onlineBookRadix;

    @TableField(value = "online_book_type")
    @ApiModelProperty(value = "线上预约类型的设定", required = "num", enumClass = StoreOnlineBookType.class)
    private Integer onlineBookType;

    @TableField(value = "online_book_json", typeHandler = JacksonTypeHandler.class)
    @ApiModelProperty(value = "设置线上预约时需要存储各个时间段内的信息，包含time和value属性", required = "json")
    private List<Map<String, Object>> onlineBookJson;

    @TableField(exist = false)
    @Property(value = "门店下的员工")
    private List<ShopStoreStaff> storeStaffList;

}
