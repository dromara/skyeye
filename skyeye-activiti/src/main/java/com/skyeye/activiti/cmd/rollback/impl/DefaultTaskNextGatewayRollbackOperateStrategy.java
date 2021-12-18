package com.skyeye.activiti.cmd.rollback.impl;

import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.task.api.history.HistoricTaskInstance;

import com.skyeye.activiti.cmd.rollback.AbstractGateWayRollbackOperateStrategy;
import com.skyeye.activiti.cmd.rollback.RollbackConstants;
import com.skyeye.activiti.cmd.rollback.RollbackParamsTemplate;

import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName: DefaultTaskNextGatewayRollbackOperateStrategy
 * @Author: huangrenhao
 * @Description: 处理多级网关
 * @CreateTime： 2020/4/16 0016 上午 11:45
 * @Version：
 **/
@Slf4j
public class DefaultTaskNextGatewayRollbackOperateStrategy extends AbstractGateWayRollbackOperateStrategy {

    public DefaultTaskNextGatewayRollbackOperateStrategy(RollbackParamsTemplate paramsTemplate) {
        super(paramsTemplate);
    }

    @Override
    public void createExecution() {
        HistoricTaskInstance hisTask = paramsTemplate.getHisTask();

        // 获取正在执行 execution
        ExecutionEntity executionEntity = getExecutionEntity();

        ExecutionEntity newExecution = CommandContextUtil.getExecutionEntityManager(commandContext)
            .createChildExecution(executionEntity.getParent());
        // 创建新任务
        createExecution(newExecution);
        // 特殊处理并行网关
        processGateway(executionEntity.getParent());

        // 移除历史任务
        removeHisTask(hisTask);

    }


    @Override
    public void setAssignee() {
        // 进行任务执行人配置,之后使用全局监听出发更新
        super.setAssignee();
        String type = RollbackConstants.TASK_TYPE_PREFIX_KEY + paramsTemplate.getHisTask().getProcessInstanceId()
            + paramsTemplate.getHisTask().getTaskDefinitionKey();
        variables.put(type, DefaultTaskNextGatewayRollbackOperateStrategy.class.getSimpleName());
    }

}
