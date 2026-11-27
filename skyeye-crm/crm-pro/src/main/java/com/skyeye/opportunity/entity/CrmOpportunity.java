/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.opportunity.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.SkyeyeFlowable;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: CrmOpportunity
 * @Description: 商机实体类
 * @author: skyeye云系列--卫志强
 * @date: 2022/10/24 15:58
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField(value = {"objectId", "title"})
@RedisCacheField(name = "crm:opportunity", cacheTime = RedisConstants.TOW_MONTH_SECONDS)
@TableName(value = "crm_opportunity", autoResultMap = true)
@ApiModel("商机实体类")
public class CrmOpportunity extends SkyeyeFlowable {

    @TableField(value = "object_id", fill = FieldFill.INSERT)
    @ApiModelProperty(value = "所属第三方业务数据id", required = "required")
    private String objectId;

    @TableField(value = "object_key", fill = FieldFill.INSERT)
    @ApiModelProperty(value = "所属第三方业务数据的key", required = "required")
    private String objectKey;

    @TableField(value = "title")
    @ApiModelProperty(value = "商机名称", required = "required", fuzzyLike = true)
    private String title;

    @TableField(value = "from_id")
    @ApiModelProperty(value = "商机来源ID", required = "required")
    private String fromId;

    @TableField(value = "estimate_price")
    @ApiModelProperty(value = "预计成交金额", required = "required,double")
    private String estimatePrice;

    @TableField(value = "estimate_end_time")
    @ApiModelProperty(value = "预计结单日期")
    private String estimateEndTime;

    @TableField(value = "contacts")
    @ApiModelProperty(value = "联系人ID", required = "required")
    private String contacts;

    @TableField(exist = false)
    @Property(value = "联系人")
    private Map<String, Object> contactsMation;

    @TableField(value = "business_need")
    @ApiModelProperty(value = "主要业务需求")
    private String businessNeed;

    @TableField(value = "department_id")
    @ApiModelProperty(value = "所属部门id", required = "required")
    private String departmentId;

    @TableField(exist = false)
    @Property(value = "所属部门信息")
    private Map<String, Object> departmentMation;

    @TableField(value = "respons_id")
    @ApiModelProperty(value = "商机负责人ID", required = "required")
    private String responsId;

    @TableField(exist = false)
    @Property(value = "商机负责人")
    private Map<String, Object> responsMation;

    @TableField(value = "part_id", typeHandler = JacksonTypeHandler.class)
    @ApiModelProperty(value = "商机参与人ID")
    private List<String> partId;

    @TableField(exist = false)
    @Property(value = "商机参与人")
    private List<Map<String, Object>> partMation;

    @TableField(value = "follow_id", typeHandler = JacksonTypeHandler.class)
    @ApiModelProperty(value = "商机关注人ID")
    private List<String> followId;

    @TableField(exist = false)
    @Property(value = "商机关注人")
    private List<Map<String, Object>> followMation;

}
