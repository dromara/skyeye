package com.skyeye.activiti.cmd.rollback;

import org.flowable.common.engine.impl.interceptor.CommandContext;

import java.util.Map;

/**
 *
 * @ClassName: RollbackOperateStrategy
 * @Description:
 * @author: skyeye云系列--卫志强
 * @date: 2021/12/18 0:31
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface RollbackOperateStrategy {


    /**
     * 处理
     */
    void process(CommandContext commandContext, String assignee);

    /**
     * 处理
     */
    void process(CommandContext commandContext, String assignee, Map<String, Object> variables);


    /**
     *  配置处理标识
     * @param assigneeExpr
     * @param assigneeListExpr
     */
    void setAssigneeExpr(String assigneeExpr, String assigneeListExpr);

    /**
     *  配置任务处理人
     */
    void setAssignee();

    /**
     * 移除相关关联
     */
    void existNextFinishedTask();

    /**
     * 移除历史痕迹
     */
    void deleteHisActInstance();

    /**
     * 移除正在运行的任务
     */
    void deleteRuntimeTasks();

    /**
     * 创建任务
     */
    void createExecution();

}
