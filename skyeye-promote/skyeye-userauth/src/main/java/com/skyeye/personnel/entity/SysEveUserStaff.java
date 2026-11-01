/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.personnel.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.common.enumeration.SexEnum;
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
 * @ClassName: SysEveUserStaff
 * @Description: 员工管理实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/18 11:24
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "sys_eve_user_staff")
@ApiModel("员工管理实体类")
public class SysEveUserStaff extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "job_number", updateStrategy = FieldStrategy.NEVER)
    @Property(value = "员工工号", fuzzyLike = true)
    private String jobNumber;

    @TableField("user_name")
    @ApiModelProperty(value = "员工姓名", required = "required", fuzzyLike = true)
    private String userName;

    @TableField(exist = false)
    @Property("员工工号+姓名")
    private String name;

    @TableField("user_photo")
    @ApiModelProperty(value = "员工头像", required = "required")
    private String userPhoto;

    @TableField("user_idcard")
    @ApiModelProperty(value = "员工身份证", required = "idcard")
    private String userIdCard;

    @TableField("user_sex")
    @ApiModelProperty(value = "员工性别", enumClass = SexEnum.class, required = "required,num")
    private Integer userSex;

    @TableField("email")
    @ApiModelProperty(value = "邮箱", required = "email")
    private String email;

    @TableField("phone")
    @ApiModelProperty(value = "手机号", required = "required,phone")
    private String phone;

    @TableField("home_phone")
    @ApiModelProperty(value = "家庭电话")
    private String homePhone;

    @TableField("qq")
    @ApiModelProperty(value = "QQ")
    private String qq;

    @TableField("user_sign")
    @ApiModelProperty(value = "个性签名")
    private String userSign;

    @TableField("state")
    @ApiModelProperty(value = "员工在职状态", enumClass = UserStaffState.class)
    private Integer state;

    @TableField(exist = false)
    @Property(value = "员工在职状态名称")
    private String stateName;

    @TableField("user_id")
    @ApiModelProperty(value = "用户id")
    private String userId;

    @TableField("company_id")
    @ApiModelProperty(value = "所属公司id")
    private String companyId;

    @TableField(exist = false)
    @Property(value = "所属公司信息")
    private Company companyMation;

    @TableField(exist = false)
    @Property(value = "所属公司名称")
    private String companyName;

    @TableField("department_id")
    @ApiModelProperty(value = "所属部门id")
    private String departmentId;

    @TableField(exist = false)
    @Property(value = "所属部门信息")
    private Department departmentMation;

    @TableField(exist = false)
    @Property(value = "所属部门名称")
    private String departmentName;

    @TableField("job_id")
    @ApiModelProperty(value = "所属职位id")
    private String jobId;

    @TableField(exist = false)
    @Property(value = "所属职位信息")
    private CompanyJob jobMation;

    @TableField(exist = false)
    @Property(value = "所属职位名称")
    private String jobName;

    /** 直属上级员工ID（用于审批「连续上级」规则） */
    @TableField("leader_staff_id")
    @ApiModelProperty(value = "直属上级员工id")
    private String leaderStaffId;

    @TableField(exist = false)
    @Property(value = "直属上级员工信息")
    private Map<String, Object> leaderStaffMation;

    @TableField("job_score_id")
    @ApiModelProperty(value = "职位定级id")
    private String jobScoreId;

    @TableField(exist = false)
    @Property(value = "职位定级信息")
    private JobScore jobScoreMation;

    @TableField(exist = false)
    @Property(value = "职位定级名称")
    private String jobScoreName;

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

    @TableField("become_worker_time")
    @ApiModelProperty(value = "如果有试用期，则为转正日期")
    private String becomeWorkerTime;

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
    @ApiModelProperty(value = "最高学历id")
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

    @TableField("background_image")
    @ApiModelProperty(value = "学校模块-表白墙背景图片")
    private String backgroundImage;

    @TableField(exist = false)
    @ApiModelProperty(value = "员工考勤时间段", required = "json")
    private List<String> timeIdList;

    @TableField(exist = false)
    @Property(value = "员工考勤时间段信息")
    private List<Map<String, Object>> timeList;

    @TableField(exist = false)
    @ApiModelProperty(value = "是否自动注册账号", enumClass = WhetherEnum.class, defaultValue = "0")
    private Integer whetherRegister;

    @TableField(exist = false)
    @ApiModelProperty(value = "当开启自动注册账号时，密码必填")
    private String password;

    @TableField("workstation_type")
    @ApiModelProperty(value = "员工工种类型", enumClass = UserStaffWorkstationType.class, required = "required,num")
    private Integer workstationType;

    @TableField("hourly_price")
    @ApiModelProperty(value = "工种类型是小时工的时候的单价")
    private String hourlyPrice;

}
