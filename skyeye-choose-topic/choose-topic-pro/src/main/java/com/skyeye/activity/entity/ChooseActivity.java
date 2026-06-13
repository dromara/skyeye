/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.activity.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.chtopic.entity.ChooseTopic;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.common.enumeration.WhetherEnum;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: Activity
 * @Description: 选题活动实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/8 10:13
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "choose:activity", cacheTime = RedisConstants.ONE_WEEK_SECONDS)
@TableName(value = "choose_activity")
@ApiModel(value = "选题活动实体类")
public class ChooseActivity extends BaseGeneralInfo {

    @TableField("start_time")
    @ApiModelProperty(value = "开始时间", required = "required")
    private String startTime;

    @TableField(value = "end_time")
    @ApiModelProperty(value = "结束时间", required = "required")
    private String endTime;

    @TableField("topic_enable")
    @ApiModelProperty(value = "是否开启选题", enumClass = WhetherEnum.class)
    private Integer topicEnable;

    @TableField("teacher_enable")
    @ApiModelProperty(value = "是否开启导师选择", enumClass = WhetherEnum.class)
    private Integer teacherEnable;

    @TableField(exist = false)
    @ApiModelProperty(value = "课题列表")
    private List<ChooseTopic> chooseTopicList;

    @TableField(exist = false)
    @ApiModelProperty(value = "当前用户是否为活动参选人员")
    private Boolean inActivity;
}
