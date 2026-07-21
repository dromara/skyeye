/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.schedule.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

/**
 * @Description: 定时任务与用例关联
 */
@Data
@TableName(value = "auto_schedule_task_case")
@ApiModel("定时任务与用例关联")
public class AutoScheduleTaskCase extends CommonInfo {

    @TableId("id")
    @Property(value = "主键id")
    private String id;

    @TableField("parent_id")
    @ApiModelProperty(value = "定时任务id")
    private String parentId;

    @TableField(value = "case_id")
    @ApiModelProperty(value = "用例ID", required = "required")
    private String caseId;

}
