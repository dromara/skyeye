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
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: AutoScoreSettle
 * @Description: 积分结算记录
 * @author: skyeye云系列--卫志强
 * @date: 2026/8/18
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "auto_score_settle")
@ApiModel("积分结算记录")
public class AutoScoreSettle extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("object_id")
    @ApiModelProperty(value = "项目id")
    private String objectId;

    @TableField("object_key")
    @ApiModelProperty(value = "项目key")
    private String objectKey;

    @TableField("user_id")
    @ApiModelProperty(value = "被结算人id")
    private String userId;

    @TableField(exist = false)
    @Property(value = "被结算人信息")
    private Map<String, Object> userMation;

    @TableField("score")
    @ApiModelProperty(value = "本次结算积分")
    private String score;

    @TableField("remark")
    @ApiModelProperty(value = "备注")
    private String remark;

}
