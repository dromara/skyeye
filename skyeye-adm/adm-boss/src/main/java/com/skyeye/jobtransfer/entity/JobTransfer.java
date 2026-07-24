/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.jobtransfer.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.SkyeyeFlowable;
import lombok.Data;

import java.util.Map;

import com.skyeye.interviewee.classenum.UserTransferType;

/**
 * @ClassName: JobTransfer
 * @Description: 岗位调动申请实体类
 * @author: skyeye云系列--卫志强
 * @date: 2022-04-27 15:57:58
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "boss:jobTransfer", cacheTime = RedisConstants.TOW_MONTH_SECONDS)
@TableName(value = "boss_interview_job_transfer", autoResultMap = true)
@ApiModel("岗位调动申请实体类")
public class JobTransfer extends SkyeyeFlowable {

    @TableField(value = "transfer_type")
    @ApiModelProperty(value = "调动类型", required = "required", enumClass = UserTransferType.class)
    private Integer transferType;

    @TableField(value = "transfer_staff_id")
    @ApiModelProperty(value = "申请人id(员工id)", required = "required")
    private String transferStaffId;

    @TableField(exist = false)
    @Property(value = "申请人信息")
    private Map<String, Object> transferStaffMation;

    @TableField(value = "primary_company_id")
    @ApiModelProperty(value = "原企业id")
    private String primaryCompanyId;

    @TableField(exist = false)
    @Property(value = "原企业信息")
    private Map<String, Object> primaryCompanyMation;

    @TableField(value = "primary_department_id")
    @ApiModelProperty(value = "原部门id")
    private String primaryDepartmentId;

    @TableField(exist = false)
    @Property(value = "原部门信息")
    private Map<String, Object> primaryDepartmentMation;

    @TableField(value = "primary_job_id")
    @ApiModelProperty(value = "原岗位id")
    private String primaryJobId;

    @TableField(exist = false)
    @Property(value = "原岗位信息")
    private Map<String, Object> primaryJobMation;

    @TableField(value = "primary_job_score_id")
    @ApiModelProperty(value = "原岗位定级id")
    private String primaryJobScoreId;

    @TableField(exist = false)
    @Property(value = "原岗位定级信息")
    private Map<String, Object> primaryJobScoreMation;

    @TableField(value = "current_company_id")
    @ApiModelProperty(value = "现企业id", required = "required")
    private String currentCompanyId;

    @TableField(exist = false)
    @Property(value = "现企业信息")
    private Map<String, Object> currentCompanyMation;

    @TableField(value = "current_department_id")
    @ApiModelProperty(value = "现部门id", required = "required")
    private String currentDepartmentId;

    @TableField(exist = false)
    @Property(value = "现部门信息")
    private Map<String, Object> currentDepartmentMation;

    @TableField(value = "current_job_id")
    @ApiModelProperty(value = "现岗位id", required = "required")
    private String currentJobId;

    @TableField(exist = false)
    @Property(value = "现岗位信息")
    private Map<String, Object> currentJobMation;

    @TableField(value = "current_job_score_id")
    @ApiModelProperty(value = "现岗位定级")
    private String currentJobScoreId;

    @TableField(exist = false)
    @Property(value = "现岗位定级信息")
    private Map<String, Object> currentJobScoreMation;

    @TableField(value = "remark")
    @ApiModelProperty(value = "备注说明", required = "required")
    private String remark;

}
