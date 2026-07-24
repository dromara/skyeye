/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.licence.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import lombok.Data;

import java.util.List;
import java.util.Map;

import com.skyeye.common.enumeration.WhetherEnum;

/**
 * @ClassName: Licence
 * @Description: 证照实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/2/6 9:22
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField
@RedisCacheField(name = "assistant:licence", cacheTime = RedisConstants.HALF_A_YEAR_SECONDS)
@TableName(value = "licence")
@ApiModel("证照实体类")
public class Licence extends BaseGeneralInfo {

    @TableField(value = "licence_num")
    @ApiModelProperty(value = "证照编号", required = "required", fuzzyLike = true)
    private String licenceNum;

    @TableField(value = "issuing_organization")
    @ApiModelProperty(value = "签发机关", required = "required")
    private String issuingOrganization;

    @TableField(value = "issue_time")
    @ApiModelProperty(value = "签发时间", required = "required")
    private String issueTime;

    @TableField(value = "annual_review")
    @ApiModelProperty(value = "是否年审", required = "required,num", enumClass = WhetherEnum.class)
    private Integer annualReview;

    @TableField(value = "next_annual_review")
    @ApiModelProperty(value = "下次年审时间，当annualReview=1时")
    private String nextAnnualReview;

    @TableField(value = "term_of_validity")
    @ApiModelProperty(value = "有效期是否永久", required = "required,num", enumClass = WhetherEnum.class)
    private Integer termOfValidity;

    @TableField(value = "term_of_validity_time")
    @ApiModelProperty(value = "有效期，当termOfValidity为0时有值")
    private String termOfValidityTime;

    @TableField(value = "licence_admin")
    @ApiModelProperty(value = "管理人id")
    private String licenceAdmin;

    @TableField(exist = false)
    @Property(value = "管理人")
    private Map<String, Object> licenceAdminMation;

    @TableField(value = "borrow_id")
    @Property(value = "借用人id")
    private String borrowId;

    @TableField(exist = false)
    @Property(value = "借用人")
    private Map<String, Object> borrowMation;

}
