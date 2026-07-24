/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.interviewee.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.base.handler.enclosure.bean.Enclosure;
import com.skyeye.common.base.handler.enclosure.bean.EnclosureFace;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.interviewee.classenum.IntervieweeStatusEnum;
import lombok.Data;

import java.util.Map;

import com.skyeye.common.enumeration.SexEnum;

/**
 * @ClassName: Interviewee
 * @Description: 面试者实体类
 * @author: skyeye云系列--卫志强
 * @date: 2022/1/22 20:06
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "boss:interviewee", cacheTime = RedisConstants.THIRTY_DAY_SECONDS)
@TableName(value = "boss_interview")
@ApiModel("面试者实体类")
public class Interviewee extends OperatorUserInfo implements EnclosureFace {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "`name`")
    @ApiModelProperty(value = "姓名", required = "required")
    private String name;

    @TableField(value = "sex")
    @ApiModelProperty(value = "性别", required = "required,num", enumClass = SexEnum.class)
    private Integer sex;

    @TableField(value = "idcard")
    @ApiModelProperty(value = "身份证号", required = "idcard")
    private String idcard;

    @TableField(value = "phone")
    @ApiModelProperty(value = "联系方式", required = "required,phone")
    private String phone;

    @TableField(value = "from_id")
    @ApiModelProperty(value = "来源id", required = "required")
    private String fromId;

    @TableField(exist = false)
    @Property(value = "来源信息")
    private IntervieweeFrom fromMation;

    @TableField(value = "favorite_job")
    @ApiModelProperty(value = "心意岗位", required = "required")
    private String favoriteJob;

    @TableField(value = "basic_resume")
    @ApiModelProperty(value = "基本简历", required = "required")
    private String basicResume;

    @TableField(value = "work_years")
    @ApiModelProperty(value = "工作年限", required = "required")
    private String workYears;

    @TableField(value = "state")
    @Property(value = "状态", enumClass = IntervieweeStatusEnum.class)
    private Integer state;

    @TableField(value = "charge_person_id")
    @ApiModelProperty(value = "负责人id", required = "required")
    private String chargePersonId;

    @TableField(exist = false)
    @Property(value = "负责人信息")
    private Map<String, Object> chargePersonMation;

    @TableField(value = "refuse_reason")
    @ApiModelProperty(value = "当state=4时，需要填写该内容，拒绝入职的原因")
    private String refuseReason;

    @TableField(value = "refuse_time")
    @ApiModelProperty(value = "当state=4时，拒绝的日期，格式为yyyy-MM-dd")
    private String refuseTime;

    @TableField(value = "last_join_department_id")
    @ApiModelProperty(value = "当state=2时，最后入职的部门id")
    private String lastJoinDepartmentId;

    @TableField(exist = false)
    @Property(value = "最后入职的部门信息")
    private Map<String, Object> lastJoinDepartmentMation;

    @TableField(value = "last_join_time")
    @ApiModelProperty(value = "当state=2时，最后入职的日期，格式为yyyy-MM-dd")
    private String lastJoinTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "附件简历", required = "json")
    private Enclosure enclosureResume;

}
