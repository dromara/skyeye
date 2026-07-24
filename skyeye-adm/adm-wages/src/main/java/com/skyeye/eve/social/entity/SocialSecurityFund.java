/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.social.entity;

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
 * @ClassName: SocialSecurityFund
 * @Description: 社保公积金实体类
 * @author: skyeye云系列--卫志强
 * @date: 2022/11/26 9:18
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@ApiModel("社保公积金实体类")
@UniqueField
@TableName(value = "wages_social_security_fund")
@RedisCacheField(name = CacheConstants.WAGES_SOCIAL_SECURITY_FUND_CACHE_KEY)
public class SocialSecurityFund extends BaseGeneralInfo {

    @TableField("start_time")
    @ApiModelProperty(value = "开始时间", required = "required")
    private String startTime;

    @TableField("end_time")
    @ApiModelProperty(value = "结束时间", required = "required")
    private String endTime;

    @TableField("enabled")
    @ApiModelProperty(value = "状态", required = "required,num", enumClass = EnableEnum.class)
    private Integer enabled;

    @TableField("endowment_base")
    @ApiModelProperty(value = "养老保险基数", required = "double", defaultValue = "0")
    private String endowmentBase;

    @TableField("endowment_person")
    @ApiModelProperty(value = "养老个人比例(%)", required = "double", defaultValue = "0")
    private String endowmentPerson;

    @TableField("endowment_company")
    @ApiModelProperty(value = "养老单位比例(%)", required = "double", defaultValue = "0")
    private String endowmentCompany;

    @TableField("unemployment_base")
    @ApiModelProperty(value = "失业保险基数", required = "double", defaultValue = "0")
    private String unemploymentBase;

    @TableField("unemployment_person")
    @ApiModelProperty(value = "失业个人比例(%)", required = "double", defaultValue = "0")
    private String unemploymentPerson;

    @TableField("unemployment_company")
    @ApiModelProperty(value = "失业单位比例(%)", required = "double", defaultValue = "0")
    private String unemploymentCompany;

    @TableField("employment_base")
    @ApiModelProperty(value = "工伤保险基数", required = "double", defaultValue = "0")
    private String employmentBase;

    @TableField("employment_person")
    @ApiModelProperty(value = "工伤个人比例(%)", required = "double", defaultValue = "0")
    private String employmentPerson;

    @TableField("employment_company")
    @ApiModelProperty(value = "工伤单位比例(%)", required = "double", defaultValue = "0")
    private String employmentCompany;

    @TableField("maternity_base")
    @ApiModelProperty(value = "生育保险基数", required = "double", defaultValue = "0")
    private String maternityBase;

    @TableField("maternity_person")
    @ApiModelProperty(value = "生育个人比例(%)", required = "double", defaultValue = "0")
    private String maternityPerson;

    @TableField("maternity_company")
    @ApiModelProperty(value = "生育单位比例(%)", required = "double", defaultValue = "0")
    private String maternityCompany;

    @TableField("medical_base")
    @ApiModelProperty(value = "医疗保险基数", required = "double", defaultValue = "0")
    private String medicalBase;

    @TableField("medical_person")
    @ApiModelProperty(value = "医疗个人比例(%)", required = "double", defaultValue = "0")
    private String medicalPerson;

    @TableField("medical_company")
    @ApiModelProperty(value = "医疗单位比例(%)", required = "double", defaultValue = "0")
    private String medicalCompany;

    @TableField("ins_total_seriously_ill_individual")
    @ApiModelProperty(value = "保险合计：大病个人", required = "double", defaultValue = "0")
    private String insTotalSeriouslyIllIndividual;

    @TableField("ins_total_person")
    @ApiModelProperty(value = "保险合计：个人社保缴费", required = "double", defaultValue = "0")
    private String insTotalPerson;

    @TableField("ins_total_company")
    @ApiModelProperty(value = "保险合计：单位社保缴费", required = "double", defaultValue = "0")
    private String insTotalCompany;

    @TableField("accumulation_base")
    @ApiModelProperty(value = "公积金基数", required = "double", defaultValue = "0")
    private String accumulationBase;

    @TableField("accumulation_person_scale")
    @ApiModelProperty(value = "公积金个人比例(%)", required = "double", defaultValue = "0")
    private String accumulationPersonScale;

    @TableField("accumulation_company_scale")
    @ApiModelProperty(value = "公积金单位比例(%)", required = "double", defaultValue = "0")
    private String accumulationCompanyScale;

    @TableField("accumulation_person_amount")
    @ApiModelProperty(value = "公积金个人(元)", required = "double", defaultValue = "0")
    private String accumulationPersonAmount;

    @TableField("accumulation_company_amount")
    @ApiModelProperty(value = "公积金单位(元)", required = "double", defaultValue = "0")
    private String accumulationCompanyAmount;

    @TableField(value = "order_by")
    @ApiModelProperty(value = "排序", required = "required,num")
    private Integer orderBy;

    @TableField(value = "delete_flag")
    private Integer deleteFlag;

    @TableField(exist = false)
    @ApiModelProperty(value = "适用对象", required = "json")
    private List<ApplicableObjects> applicableObjectsList;

}
