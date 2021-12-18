package com.skyeye.activiti.cmd.rollback.impl;

import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.task.api.history.HistoricTaskInstance;

import com.skyeye.activiti.cmd.rollback.AbstractRollbackOperateStrategy;
import com.skyeye.activiti.cmd.rollback.RollbackConstants;
import com.skyeye.activiti.cmd.rollback.RollbackParamsTemplate;

/**
 * 
 * @ClassName: NextDefaultUserTaskRollbackOperateStrategy
 * @Description: 普通节点 ，兼容嵌入式子流程
 * @author: skyeye云系列--卫志强
 * @date: 2021/12/18 0:29
 *   
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class NextDefaultUserTaskRollbackOperateStrategy extends AbstractRollbackOperateStrategy {

    public NextDefaultUserTaskRollbackOperateStrategy(RollbackParamsTemplate paramsTemplate) {
        super(paramsTemplate);
    }

    @Override
    public void createExecution() {
        HistoricTaskInstance hisTask = paramsTemplate.getHisTask();

        // 获取正在执行 execution
        ExecutionEntity executionEntity = getExecutionEntity();

        ExecutionEntity newExecution = CommandContextUtil.getExecutionEntityManager(commandContext).createChildExecution(executionEntity.getParent());
        // 创建新任务
        createExecution(newExecution);
        // 移除历史任务
        removeHisTask(hisTask);
    }

    @Override
    public void setAssignee() {
        // 进行任务执行人配置,之后使用全局监听出发更新
        super.setAssignee();
        String type = RollbackConstants.TASK_TYPE_PREFIX_KEY + paramsTemplate.getHisTask().getProcessInstanceId()
            + paramsTemplate.getHisTask().getTaskDefinitionKey();
        variables.put(type, NextDefaultUserTaskRollbackOperateStrategy.class.getSimpleName());
    }

}
