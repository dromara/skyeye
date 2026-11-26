/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.servey.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.eve.question.entity.DwQuestion;
import com.skyeye.eve.servey.classenum.DwSurveyState;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: DwSurveyDirectory
 * @Description:问卷目录及问卷实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/8 14:35
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "dw_survey_directory")
@ApiModel(value = "问卷目录及问卷实体类")
public class DwSurveyDirectory extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("survey_name")
    @ApiModelProperty(value = "问卷名称", required = "required")
    private String surveyName;

    @TableField("survey_note")
    @ApiModelProperty(value = "问卷说明")
    private String surveyNote;

    @TableField("survey_qu_num")
    @ApiModelProperty(value = "问卷下面有多少题目数")
    private Integer surveyQuNum;

    @TableField("survey_state")
    @ApiModelProperty(value = "问卷状态", enumClass = DwSurveyState.class)
    private Integer surveyState;

    @TableField("real_start_time")
    @ApiModelProperty(value = "实际开始时间")
    private String realStartTime;

    @TableField("real_end_time")
    @ApiModelProperty(value = "实际结束时间")
    private String realEndTime;

    @TableField("survey_model")
    @ApiModelProperty(value = "问卷所属的问卷模块  1问卷模块  2试题模块")
    private Integer surveyModel;

    @TableField("an_item_least_num")
    @ApiModelProperty(value = "可以回答的最少选项数目")
    private Integer anItemLeastNum;

    @TableField("an_item_most_num")
    @ApiModelProperty(value = "可以回答的最多选项数目")
    private Integer anItemMostNum;

    @TableField("is_effective")
    @ApiModelProperty(value = " 是否有问卷限制 1是 0否")
    private Integer isEffective;

    @TableField("effective")
    @ApiModelProperty(value = " 每台电脑或手机只能答一次 1是 0否")
    private Integer effective;

    @TableField("effective_ip")
    @ApiModelProperty(value = "每个IP只能答一次 1是 0否")
    private Integer effectiveIp;

    @TableField("rule")
    @ApiModelProperty(value = "调查规则 1公开, 2私有, 3令牌(表示启用访问密码)")
    private Integer rule;

    @TableField("rule_code")
    @ApiModelProperty(value = "rule为3时的令牌密码")
    private String ruleCode;

    @TableField("effective_time")
    @ApiModelProperty(value = "如果每个IP能答多次，每隔多长时间可以答一次(分钟)")
    private Integer effectiveTime;

    @TableField("yn_end_num")
    @ApiModelProperty(value = "是否依据收到的份数结束   1是   0否")
    private Integer ynEndNum;

    @TableField("mail_only")
    @ApiModelProperty(value = "只有邮件邀请唯一链接的受访者可回答  1启用 0不启用")
    private Integer mailOnly;

    @TableField("end_num")
    @ApiModelProperty(value = "收到的份数")
    private Integer endNum;

    @TableField("yn_end_time")
    @ApiModelProperty(value = "是否依据时间结束  1是   0否")
    private Integer ynEndTime;

    @TableField("end_time")
    @ApiModelProperty(value = "手动设置的结束时间")
    private String endTime;

    @TableField("answer_num")
    @ApiModelProperty(value = "回答次数")
    private Integer answerNum;

    @TableField("refresh")
    @ApiModelProperty(value = "有重复回答启用验证码   1是  0否")
    private Integer refresh;

    @TableField("excerpt_num")
    @ApiModelProperty(value = "引用次数")
    private Integer excerptNum;

    @TableField("html_path")
    @ApiModelProperty(value = "静态HTML保存路径")
    private String htmlPath;

    @TableField("is_share")
    @ApiModelProperty(value = "是否共享问卷  0不共享 1共享")
    private Integer isShare;

    @TableField("sid")
    @ApiModelProperty(value = "用于短链接的ID")
    private String sid;

    @TableField("dir_type")
    @ApiModelProperty(value = "1目录   2问卷")
    private Integer dirType;

    @TableField("end_type")
    @ApiModelProperty(value = "结束方式   1手动结束,2依据结束时间,3依据收到的份数")
    private Integer endType;

    @TableField("survey_tag")
    @ApiModelProperty(value = "问卷标识 默认 0待审核  1审核通过  2审核未通过  3审核中")
    private Integer surveyTag;

    @TableField("view_answer")
    @ApiModelProperty(value = "是否公开结果  0不  1公开")
    private Integer viewAnswer;

    @TableField("visibility")
    @Property(value = "是否被删除  1.未删除  0.已删除")
    private Integer visibility;

    @TableField("fraction")
    @ApiModelProperty(value = "总分数")
    private Integer fraction;

    @TableField("whether_delete")
    @ApiModelProperty(value = "是否删除  1.未删除  2.删除")
    private Integer whetherDelete;

    @TableField(exist = false)
    @ApiModelProperty(value = "题目信息", required = "json")
    private List<DwQuestion> dwQuestionMation;

    @TableField(exist = false)
    @ApiModelProperty(value = "分页数量")
    private int pageNo;

    @TableField(exist = false)
    @ApiModelProperty(value = "是否回答过 1.是  0.否")
    private Integer isAnswered;
}

