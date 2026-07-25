/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.procedure.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.CacheConstants;
import com.skyeye.common.entity.features.Version;
import lombok.Data;

import java.util.List;

import com.skyeye.common.enumeration.EnableEnum;

/**
 * @ClassName: WayProcedure
 * @Description: 工艺路线实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/3/24 13:05
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = CacheConstants.MES_WAY_PROCEDURE_CACHE_KEY)
@TableName(value = "erp_way_procedure")
@ApiModel("工艺路线信息实体类")
public class WayProcedure extends Version {

    @TableField("`name`")
    @ApiModelProperty(value = "名称", required = "required", fuzzyLike = true)
    private String name;

    @TableField("remark")
    @ApiModelProperty("相关描述")
    private String remark;

    @TableField(value = "number")
    @ApiModelProperty(value = "工艺编号", required = "required", fuzzyLike = true)
    private String number;

    @TableField(value = "enabled")
    @ApiModelProperty(value = "启用状态", required = "required,num", enumClass = EnableEnum.class)
    private Integer enabled;

    @TableField(value = "all_price")
    @Property(value = "工艺总价")
    private String allPrice;

    @TableField(value = "delete_flag")
    private Integer deleteFlag;

    @TableField(exist = false)
    @ApiModelProperty(value = "关联的工序信息", required = "required,json")
    private List<WayProcedureChild> workProcedureList;

}
