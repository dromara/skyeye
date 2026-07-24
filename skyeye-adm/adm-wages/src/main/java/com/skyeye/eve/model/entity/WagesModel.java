/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.constans.CacheConstants;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import lombok.Data;

import java.util.List;

import com.skyeye.common.enumeration.EnableEnum;

/**
 * @ClassName: WagesModel
 * @Description: 薪资模板
 * @author: skyeye云系列--卫志强
 * @date: 2024/1/21 11:08
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Data
@ApiModel("薪资模板实体类")
@UniqueField
@TableName(value = "wages_model")
@RedisCacheField(name = CacheConstants.WAGES_MODEL_CACHE_KEY)
public class WagesModel extends BaseGeneralInfo {

    @TableField(value = "type_id")
    @ApiModelProperty(value = "模板类型，数据来源数据字典", required = "required")
    private String typeId;

    @TableField(value = "start_time")
    @ApiModelProperty(value = "开始日期", required = "required")
    private String startTime;

    @TableField(value = "end_time")
    @ApiModelProperty(value = "结束日期", required = "required")
    private String endTime;

    @TableField(value = "order_by")
    @ApiModelProperty(value = "排序", required = "required,num")
    private Integer orderBy;

    @TableField("enabled")
    @ApiModelProperty(value = "状态", required = "required,num", enumClass = EnableEnum.class)
    private Integer enabled;

    @TableField(exist = false)
    @ApiModelProperty(value = "适用对象", required = "json")
    private List<ModelApplicableObjects> applicableObjectsList;

    @TableField(exist = false)
    @ApiModelProperty(value = "模板关联的薪资字段", required = "required,json")
    private List<WagesModelField> wagesModelFieldList;

}
