/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.seal.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.entity.ErpOrderHead;
import com.skyeye.seal.classenum.SalesOrderPurchaseState;
import lombok.Data;

/**
 * @ClassName: SalesOrder
 * @Description: 销售订单实体类
 * @author: skyeye云系列--卫志强
 * @date: 2022/3/23 16:19
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "erp:order:sales", cacheTime = RedisConstants.TOW_MONTH_SECONDS)
@TableName(value = "erp_depothead", autoResultMap = true)
@ApiModel("销售订单实体类")
public class SalesOrder extends ErpOrderHead {

    @TableField(exist = false)
    @Property(value = "树的名称(单号)")
    private String name;

    @TableField(exist = false)
    @Property(value = "树的父节点ID")
    private String pId;

    @TableField("purchase_state")
    @Property(value = "采购状态", enumClass = SalesOrderPurchaseState.class)
    private Integer purchaseState;

}
