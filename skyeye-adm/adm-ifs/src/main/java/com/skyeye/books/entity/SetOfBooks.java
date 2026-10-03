/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.books.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.common.enumeration.EnableEnum;
import lombok.Data;

/**
 * @ClassName: SetOfBooks
 * @Description: 账套信息实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/3/12 12:24
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField
@RedisCacheField(name = "ifs:setOfBooks")
@TableName(value = "ifs_set_of_books", autoResultMap = true)
@ApiModel("账套信息实体类")
public class SetOfBooks extends BaseGeneralInfo {

    @TableField(value = "start_time")
    @ApiModelProperty(value = "开始日期", required = "required")
    private String startTime;

    @TableField(value = "end_time")
    @ApiModelProperty(value = "结束日期", required = "required")
    private String endTime;

    @TableField("enabled")
    @ApiModelProperty(value = "状态", enumClass = EnableEnum.class, required = "required,num")
    private Integer enabled;

    @TableField("currency")
    @ApiModelProperty(value = "本位币")
    private String currency;

    @TableField("current_period")
    @ApiModelProperty(value = "当前期间YYYY-MM")
    private String currentPeriod;

    @TableField("cost_domain_id")
    @ApiModelProperty(value = "默认成本域")
    private String costDomainId;

}
