/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.history.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

import com.skyeye.history.classenum.AutoHistoryCaseExecuteResult;

/**
 * @ClassName: AutoHistoryStepApi
 * @Description: 步骤为用例的执行历史实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/2/28 12:54
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Data
@TableName(value = "auto_history_step_case")
@ApiModel(value = "步骤为用例的执行历史实体类")
public class AutoHistoryStepCase extends CommonInfo {

    @TableId("id")
    @Property(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "history_step_id")
    @Property(value = "历史步骤id")
    private String historyStepId;

    @TableField(value = "execute_case_id")
    @Property(value = "执行的用例id")
    private String executeCaseId;

    @TableField("input_value")
    @Property(value = "入参")
    private String inputValue;

    @TableField("output_value")
    @Property(value = "出参")
    private String outputValue;

    @TableField("history_case_id")
    @Property(value = "用例id")
    private String historyCaseId;

    @TableField("execute_result")
    @Property(value = "执行结果", enumClass = AutoHistoryCaseExecuteResult.class)
    private Integer executeResult;

}
