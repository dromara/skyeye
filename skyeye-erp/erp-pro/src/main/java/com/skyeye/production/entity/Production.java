/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.production.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.CacheConstants;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.SkyeyeFlowable;
import com.skyeye.production.classenum.ProductionMachinOrderState;
import com.skyeye.production.classenum.ProductionOutState;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: Production
 * @Description: 生产计划单
 * 1. 状态(state)只有工作流(FlowableStateEnum)的状态
 * @author: skyeye云系列--卫志强
 * @date: 2023/3/29 10:53
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = CacheConstants.MES_PRODUCTION_CACHE_KEY, cacheTime = RedisConstants.HALF_A_YEAR_SECONDS)
@TableName(value = "erp_production_head", autoResultMap = true)
@ApiModel("生产计划单实体类")
public class Production extends SkyeyeFlowable {

    @TableField(exist = false)
    @Property(value = "树的名称(订单编号)")
    private String name;

    @TableField(value = "from_type_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "来源单据类型，参考#ProductionFromType")
    private Integer fromTypeId;

    @TableField(value = "from_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "来源单据id")
    private String fromId;

    @TableField(exist = false)
    @Property(value = "来源单据信息")
    private Map<String, Object> fromMation;

    @TableField("oper_time")
    @ApiModelProperty(value = "单据日期", required = "required")
    private String operTime;

    @TableField("remark")
    @ApiModelProperty(value = "备注")
    private String remark;

    @TableField("out_state")
    @Property(value = "委外状态", enumClass = ProductionOutState.class)
    private Integer outState;

    @TableField("machin_order_state")
    @Property(value = "加工单的下达状态", enumClass = ProductionMachinOrderState.class)
    private Integer machinOrderState;

    @TableField(exist = false)
    @ApiModelProperty(value = "子单据信息", required = "required,json")
    private List<ProductionChild> productionChildList;

}
