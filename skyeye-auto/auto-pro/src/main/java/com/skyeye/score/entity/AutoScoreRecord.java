/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.score.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.score.classenum.AutoScoreTypeEnum;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: AutoScoreRecord
 * @Description: 需求积分流水
 * @author: skyeye云系列--卫志强
 * @date: 2026/8/18
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "auto_score_record")
@ApiModel("需求积分流水")
public class AutoScoreRecord extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("object_id")
    @ApiModelProperty(value = "项目id")
    private String objectId;

    @TableField("object_key")
    @ApiModelProperty(value = "项目key")
    private String objectKey;

    @TableField("version_id")
    @ApiModelProperty(value = "版本id")
    private String versionId;

    @TableField("user_id")
    @ApiModelProperty(value = "积分所属人id")
    private String userId;

    @TableField(exist = false)
    @Property(value = "积分所属人信息")
    private Map<String, Object> userMation;

    @TableField("demand_id")
    @ApiModelProperty(value = "需求id")
    private String demandId;

    @TableField("bug_id")
    @ApiModelProperty(value = "bug id")
    private String bugId;

    @TableField("score_type")
    @ApiModelProperty(value = "积分类型", enumClass = AutoScoreTypeEnum.class)
    private String scoreType;

    @TableField("score")
    @ApiModelProperty(value = "积分，扣分为负数")
    private String score;

    @TableField("role_key")
    @ApiModelProperty(value = "角色：front/back/test")
    private String roleKey;

    @TableField("remark")
    @ApiModelProperty(value = "备注")
    private String remark;

}
