/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.common.enumeration.UserStaffState;
import com.skyeye.common.enumeration.UserStaffWorkstationType;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.organization.entity.Company;
import com.skyeye.organization.entity.CompanyJob;
import com.skyeye.organization.entity.Department;
import com.skyeye.organization.entity.JobScore;
import com.skyeye.personnel.classenum.StaffWagesStateEnum;
import com.skyeye.personnel.classenum.UserStaffType;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: TenantUser
 * @Description: 租户下的用户实体类
 * @author: skyeye云系列--卫志强
 * @date: 2025/4/26 22:42
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField(value = {"staffId"})
@TableName(value = "tenant_user")
@ApiModel("租户下的用户实体类")
public class TenantUser extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "job_number", updateStrategy = FieldStrategy.NEVER)
    @Property(value = "员工工号", fuzzyLike = true)
    private String jobNumber;

    @TableField(value = "staff_id")
    @ApiModelProperty(value = "员工id", required = "required")
    private String staffId;

    @TableField(exist = false)
    @Property(value = "员工的serviceClassName")
    private String staffServiceClassName;

    @TableField(exist = false)
    @Property(value = "员工信息")
    private Map<String, Object> staffMation;

    @TableField(exist = false)
    @Property(value = "用户id")
    private String userId;

    @TableField(value = "email")
    @ApiModelProperty(value = "邮箱")
    private String email;

    @TableField(value = "company_id")
    @ApiModelProperty(value = "企业id", required = "required")
    private String companyId;

    @TableField(exist = false)
    @Property(value = "企业信息")
    private Company companyMation;

    @TableField(value = "department_id")
    @ApiModelProperty(value = "部门id", required = "required")
    private String departmentId;

    @TableField(exist = false)
    @Property(value = "部门信息")
    private Department departmentMation;

    @TableField(value = "job_id")
    @ApiModelProperty(value = "岗位id", required = "required")
    private String jobId;

    @TableField(exist = false)
    @Property(value = "岗位信息")
    private CompanyJob jobMation;

    @TableField("job_score_id")
    @ApiModelProperty(value = "职位定级id")
    private String jobScoreId;

    @TableField(exist = false)
    @Property(value = "职位定级信息")
    private JobScore jobScoreMation;

    @TableField("state")
    @ApiModelProperty(value = "员工在职状态", enumClass = UserStaffState.class, required = "required,num")
    private Integer state;

    @TableField(exist = false)
    @Property(value = "员工在职状态名称")
    private String stateName;

    @TableField("quit_time")
    @ApiModelProperty(value = "离职时间")
    private String quitTime;

    @TableField("quit_reason")
    @ApiModelProperty(value = "离职原因 最多50字")
    private String quitReason;

    @TableField("work_time")
    @ApiModelProperty(value = "参加工作时间")
    private String workTime;

    @TableField("entry_time")
    @ApiModelProperty(value = "入职时间，也是到岗时间")
    private String entryTime;

    @TableField("trial_time")
    @ApiModelProperty(value = "如果有试用期，则为试用期到期时间。当state=4时，该字段必填")
    private String trialTime;

    @TableField("type")
    @ApiModelProperty(value = "员工类型", enumClass = UserStaffType.class, defaultValue = "1")
    private Integer type;

    @TableField("native_place")
    @ApiModelProperty(value = "籍贯")
    private String nativePlace;

    @TableField("marital_status")
    @ApiModelProperty(value = "婚姻状况  1.已婚  2.未婚")
    private Integer maritalStatus;

    @TableField("politic_id")
    @ApiModelProperty(value = "政治面貌id")
    private String politicId;

    @TableField("highest_education")
    @ApiModelProperty(value = "最高学历id，数据来源：数据字典")
    private String highestEducation;

    @TableField("design_wages")
    @ApiModelProperty(value = "薪资设定情况", enumClass = StaffWagesStateEnum.class, defaultValue = "1")
    private Integer designWages;

    @TableField("act_wages")
    @ApiModelProperty(value = "员工的月标准薪资")
    private String actWages;

    @TableField("annual_leave")
    @ApiModelProperty(value = "员工剩余年假")
    private String annualLeave;

    @TableField("annual_leave_statis_time")
    @ApiModelProperty(value = "员工剩余年假最近的刷新日期")
    private String annualLeaveStatisTime;

    @TableField("holiday_number")
    @ApiModelProperty(value = "当前员工剩余补休天数")
    private String holidayNumber;

    @TableField("holiday_statis_time")
    @ApiModelProperty(value = "补休池剩余补休天数数据刷新时间")
    private String holidayStatisTime;

    @TableField("retired_holiday_number")
    @ApiModelProperty(value = "当前员工已休补休天数")
    private String retiredHolidayNumber;

    @TableField("retired_holiday_statis_time")
    @ApiModelProperty(value = "补休池已休补休天数数据刷新时间")
    private String retiredHolidayStatisTime;

    @TableField("interview_arrangement_id")
    @ApiModelProperty(value = "关联的面试安排信息id")
    private String interviewArrangementId;

    @TableField("tenant_user_invite_id")
    @ApiModelProperty(value = "用户邀请信息id")
    private String tenantUserInviteId;

    @TableField("tenant_user_apply_id")
    @ApiModelProperty(value = "用户申请加入信息id")
    private String tenantUserApplyId;

    @TableField(exist = false)
    @Property(value = "用户邀请信息")
    private Map<String, Object> tenantUserInviteMation;

    @TableField("role_id")
    @ApiModelProperty(value = "角色ID，多个逗号隔开")
    private String roleId;

    @TableField(value = "tenant_id")
    @ApiModelProperty(value = "租户id", required = "required")
    private String tenantId;

    @TableField(exist = false)
    @ApiModelProperty(value = "员工考勤时间段", required = "json")
    private List<String> timeIdList;

    @TableField(exist = false)
    @Property(value = "员工考勤时间段信息")
    private List<Map<String, Object>> timeList;

    @TableField("is_admin")
    @ApiModelProperty(value = "是否是管理员", enumClass = WhetherEnum.class, required = "required,num")
    private Integer isAdmin;

    @TableField("workstation_type")
    @ApiModelProperty(value = "员工工种类型", enumClass = UserStaffWorkstationType.class, required = "required,num")
    private Integer workstationType;

    @TableField("hourly_price")
    @ApiModelProperty(value = "工种类型是小时工的时候的单价")
    private String hourlyPrice;

}
