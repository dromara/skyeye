/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.patrol.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: PatrolTaskItem
 * @Description: 巡检任务项目关联实体类
 */
@Data
@TableName(value = "crm_service_patrol_task_item")
@ApiModel("巡检任务项目关联实体类")
public class PatrolTaskItem extends CommonInfo {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "task_id")
    @ApiModelProperty(value = "任务ID", required = "required")
    private String taskId;

    @TableField(value = "item_id")
    @ApiModelProperty(value = "项目ID", required = "required")
    private String itemId;

    @TableField(exist = false)
    @ApiModelProperty(value = "项目信息")
    private Map<String, Object> itemMation;

}
