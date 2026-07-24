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
import com.skyeye.usercase.classenum.AutoValueFromTypeEnum;

/**
 * @ClassName: AutoHistoryStepAssert
 * @Description: 断言执行历史实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/2/28 12:54
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Data
@TableName(value = "auto_history_assert")
@ApiModel(value = "断言执行历史实体类")
public class AutoHistoryStepAssert extends CommonInfo {

    @TableId("id")
    @Property("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "history_step_id")
    @Property(value = "历史步骤id")
    private String historyStepId;

    @TableField("`key`")
    @Property(value = "断言的取值参数")
    private String key;

    @TableField("operator")
    @Property(value = "操作符")
    private String operator;

    @TableField("value_from")
    @Property(value = "值的数据来源", enumClass = AutoValueFromTypeEnum.class)
    private Integer valueFrom;

    @TableField("value")
    @Property(value = "断言对比得值")
    private String value;

    @TableField("real_value")
    @Property(value = "真实值")
    private String realValue;

    @TableField("order_by")
    @Property(value = "排序")
    private Integer orderBy;

    @TableField("history_case_id")
    @Property(value = "用例id")
    private String historyCaseId;

    @TableField("execute_result")
    @Property(value = "执行结果", enumClass = AutoHistoryCaseExecuteResult.class)
    private Integer executeResult;

}
