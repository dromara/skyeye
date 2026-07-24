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
import lombok.Data;

import java.util.List;
import java.util.Map;

import com.skyeye.production.classenum.ProductionPlanFromType;
import com.skyeye.production.classenum.ProductionPlanProduceState;
import com.skyeye.production.classenum.ProductionPlanPurchaseState;

/**
 * @ClassName: ProductionPlan
 * @Description: 出货计划单
 * @author: skyeye云系列--卫志强
 * @date: 2023/3/29 10:53
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = CacheConstants.MES_PRODUCTION_PLAN_CACHE_KEY, cacheTime = RedisConstants.HALF_A_YEAR_SECONDS)
@TableName(value = "erp_production_plan_head", autoResultMap = true)
@ApiModel("出货计划单实体类")
public class ProductionPlan extends SkyeyeFlowable {

    @TableField(value = "from_type_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "来源单据类型", enumClass = ProductionPlanFromType.class)
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

    @TableField("purchase_state")
    @Property(value = "采购状态", enumClass = ProductionPlanPurchaseState.class)
    private Integer purchaseState;

    @TableField("produce_state")
    @Property(value = "生产状态", enumClass = ProductionPlanProduceState.class)
    private Integer produceState;

    @TableField(exist = false)
    @ApiModelProperty(value = "子单据信息", required = "required,json")
    private List<ProductionPlanChild> productionPlanChildList;

}
