/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.history.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.entity.CommonInfo;
import com.skyeye.history.classenum.AutoHistoryCaseExecuteResult;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: AutoHistoryCase
 * @Description: 用例执行历史实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/2/28 12:54
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Data
@RedisCacheField(name = "auto:history:case")
@TableName(value = "auto_history_case")
@ApiModel(value = "用例执行历史实体类")
public class AutoHistoryCase extends CommonInfo {

    @TableId("id")
    @Property(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "`name`")
    @Property(value = "名称", fuzzyLike = true)
    private String name;

    @TableField("module_id")
    @Property(value = "模块id")
    private String moduleId;

    @TableField("result_key")
    @Property(value = "结果的key")
    private String resultKey;

    @TableField("case_id")
    @Property(value = "用例id")
    private String caseId;

    @TableField("schedule_task_history_id")
    @Property(value = "定时任务执行记录id，非定时触发为空")
    private String scheduleTaskHistoryId;

    @TableField("execute_start_time")
    @Property(value = "执行开始时间")
    private String executeStartTime;

    @TableField("execute_end_time")
    @Property(value = "执行结束时间")
    private String executeEndTime;

    @TableField("execute_time")
    @Property(value = "执行耗时   毫秒")
    private String executeTime;

    @TableField("execute_result")
    @Property(value = "执行结果", enumClass = AutoHistoryCaseExecuteResult.class)
    private Integer executeResult;

    @TableField(exist = false)
    @Property(value = "步骤信息")
    private List<AutoHistoryStep> stepList;


}
