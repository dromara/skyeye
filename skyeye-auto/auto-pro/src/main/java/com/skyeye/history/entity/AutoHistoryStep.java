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

import java.util.List;

import com.skyeye.history.classenum.AutoHistoryCaseExecuteResult;
import com.skyeye.usercase.classenum.AutoStepTypeEnum;

/**
 * @ClassName: AutoHistoryStep
 * @Description: 步骤执行历史实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/2/28 12:54
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Data
@TableName(value = "auto_history_step")
@ApiModel(value = "步骤执行历史实体类")
public class AutoHistoryStep extends CommonInfo {

    @TableId("id")
    @Property(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "`name`")
    @Property(value = "名称")
    private String name;

    @TableField("history_case_id")
    @Property(value = "用例id")
    private String historyCaseId;

    @TableField("order_by")
    @Property(value = "顺序")
    private Integer orderBy;

    @TableField("type")
    @Property(value = "步骤类型", enumClass = AutoStepTypeEnum.class)
    private Integer type;

    @TableField("result_key")
    @Property(value = "结果的key")
    private String resultKey;

    @TableField("execute_result")
    @Property(value = "执行结果", enumClass = AutoHistoryCaseExecuteResult.class)
    private Integer executeResult;

    @TableField(exist = false)
    @Property(value = "断言")
    private List<AutoHistoryStepAssert> autoHistoryStepAssertList;

    @TableField(exist = false)
    @Property(value = "API")
    private AutoHistoryStepApi autoHistoryStepApi;

    @TableField(exist = false)
    @Property(value = "用例")
    private AutoHistoryStepCase autoHistoryStepCase;

    @TableField(exist = false)
    @Property(value = "数据库")
    private AutoHistoryStepDatabase autoHistoryStepDatabase;

}
