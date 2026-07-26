/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.datafrom.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import lombok.Data;

import com.skyeye.datafrom.classenum.ReportDataFromType;

/**
 * @ClassName: ReportDataFrom
 * @Description: 数据来源实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/4/3 21:06
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField(value = {"name", "type"})
@RedisCacheField(name = "report:datafrom")
@TableName(value = "report_data_from", autoResultMap = true)
@ApiModel("数据来源实体类")
public class ReportDataFrom extends BaseGeneralInfo {

    @TableField(value = "type")
    @ApiModelProperty(value = "数据来源类型", required = "required,num", enumClass = ReportDataFromType.class)
    private Integer type;

    @TableField(exist = false)
    @ApiModelProperty(value = "JSON数据源")
    private ReportDataFromJson jsonEntity;

    @TableField(exist = false)
    @ApiModelProperty(value = "Rest数据源")
    private ReportDataFromRest restEntity;

    @TableField(exist = false)
    @ApiModelProperty(value = "SQL数据源")
    private ReportDataFromSQL sqlEntity;

    @TableField(exist = false)
    @ApiModelProperty(value = "XML数据源")
    private ReportDataFromXML xmlEntity;

}
