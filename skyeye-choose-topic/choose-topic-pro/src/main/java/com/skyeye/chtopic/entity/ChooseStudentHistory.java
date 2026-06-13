/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.chtopic.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.chtopic.classenum.StudentChooseActionType;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

/**
 * 学生选题/选导操作历史
 */
@Data
@TableName(value = "choose_student_history")
@ApiModel(value = "学生选择历史")
public class ChooseStudentHistory extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty("主键id")
    private String id;

    @TableField("activity_id")
    @ApiModelProperty("活动id")
    private String activityId;

    @TableField("student_id")
    @ApiModelProperty("学生id")
    private String studentId;

    @TableField(exist = false)
    @Property(value = "学生姓名")
    private String studentName;

    @TableField(exist = false)
    @Property(value = "学生学号")
    private String studentStuNo;

    @TableField("action_type")
    @ApiModelProperty(value = "操作类型", enumClass = StudentChooseActionType.class)
    private Integer actionType;

    @TableField(exist = false)
    @Property(value = "操作类型名称")
    private String actionTypeName;

    @TableField("topic_id")
    @ApiModelProperty("课题id")
    private String topicId;

    @TableField("topic_title")
    @ApiModelProperty("课题标题")
    private String topicTitle;

    @TableField("teacher_id")
    @ApiModelProperty("导师id")
    private String teacherId;

    @TableField("teacher_name")
    @ApiModelProperty("导师姓名")
    private String teacherName;

    @TableField("remark")
    @ApiModelProperty("备注")
    private String remark;
}
