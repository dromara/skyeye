/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.assignment.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.base.handler.enclosure.bean.Enclosure;
import com.skyeye.common.base.handler.enclosure.bean.EnclosureFace;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.school.assignment.classenum.AssignmentTimeState;
import com.skyeye.school.chapter.entity.Chapter;
import lombok.Data;

import com.skyeye.school.assignment.classenum.AssignmentApplicationProcess;
import com.skyeye.school.assignment.classenum.AssignmentSubState;
import com.skyeye.school.assignment.classenum.AssignmentType;

/**
 * @ClassName: Assignment
 * @Description: 作业管理实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/2 10:35
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "school:assignment", cacheTime = RedisConstants.HALF_A_YEAR_SECONDS)
@TableName(value = "school_assignment")
@ApiModel(value = "作业管理实体类")
public class Assignment extends BaseGeneralInfo implements EnclosureFace {

    @TableField(value = "object_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "科目数据的id", required = "required")
    private String objectId;

    @TableField(value = "object_key", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "科目数据的serviceClassName", required = "required")
    private String objectKey;

    @TableField(value = "subject_classes_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "科目表与班级表的关系id", required = "required")
    private String subjectClassesId;

    @TableField("chapter_id")
    @ApiModelProperty(value = "所属章节id", required = "required")
    private String chapterId;

    @TableField(exist = false)
    @Property(value = "所属章节信息")
    private Chapter chapterMation;

    @TableField("application_process")
    @ApiModelProperty(value = "应用环节", required = "required", enumClass = AssignmentApplicationProcess.class)
    private String applicationProcess;

    @TableField("start_time")
    @ApiModelProperty(value = "开始时间", required = "required")
    private String startTime;

    @TableField("end_time")
    @ApiModelProperty(value = "结束时间", required = "required")
    private String endTime;

    @TableField("type")
    @ApiModelProperty(value = "类型", required = "required", enumClass = AssignmentType.class)
    private String type;

    @TableField("full_marks")
    @ApiModelProperty(value = "满分", required = "required")
    private String fullMarks;

    @TableField(value = "content")
    @ApiModelProperty(value = "内容")
    private String content;

    @TableField(exist = false)
    @Property(value = "时间状态", enumClass = AssignmentTimeState.class)
    private String timeState;

    @TableField(exist = false)
    @Property(value = "学生作业提交状态", enumClass = AssignmentSubState.class)
    private String subState;

    @TableField(exist = false)
    @Property(value = "需要提交作业的人数")
    private Long needNum;

    @TableField(exist = false)
    @Property(value = "已提交作业的人数")
    private Long subNum;

    @TableField(exist = false)
    @Property(value = "未提交作业的人数")
    private Long noSubNum;

    @TableField(exist = false)
    @Property(value = "已批改作业的人数")
    private Long correctNum;

    @TableField(exist = false)
    @Property(value = "未批改作业的人数")
    private Long noCorrectNum;

    @TableField(exist = false)
    @ApiModelProperty(value = "附件", required = "json")
    private Enclosure enclosureInfo;
}
