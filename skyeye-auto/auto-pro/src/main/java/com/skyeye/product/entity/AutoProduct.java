/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.product.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import lombok.Data;

import com.skyeye.product.classenum.AutoProductState;

/**
 * @ClassName: AutoProduct
 * @Description: 产品管理实体层
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/26 8:57
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "auto:product", cacheTime = RedisConstants.TOW_MONTH_SECONDS)
@TableName("auto_product")
@ApiModel(value = "产品实体类")
public class AutoProduct extends BaseGeneralInfo {

    @TableField("state")
    @Property(value = "状态", enumClass = AutoProductState.class)
    private String state;


}