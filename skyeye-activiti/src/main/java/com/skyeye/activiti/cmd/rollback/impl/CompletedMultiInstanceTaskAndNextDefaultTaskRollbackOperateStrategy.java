package com.skyeye.activiti.cmd.rollback.impl;

import com.skyeye.activiti.cmd.rollback.AbstractRollbackOperateStrategy;
import com.skyeye.activiti.cmd.rollback.RollbackConstants;
import com.skyeye.activiti.cmd.rollback.RollbackParamsTemplate;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.flowable.engine.impl.bpmn.behavior.SequentialMultiInstanceBehavior;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.flowable.variable.service.impl.HistoricVariableInstanceQueryImpl;
import org.flowable.variable.service.impl.persistence.entity.VariableInstanceEntity;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: CompletedMultiInstanceTaskAndNextDefaultTaskRollbackOperateStrategy
 * @Author: huangrenhao
 * @Description: 已完成会签 ， 下一节点是普通节点 进行回退
 * @CreateTime： 2020/4/21 0021 上午 11:06
 * @Version： 1.0
 * {@link ParallelMultiInstanceBehavior}
 * {@link SequentialMultiInstanceBehavior}
 **/
@Slf4j
public class CompletedMultiInstanceTaskAndNextDefaultTaskRollbackOperateStrategy extends AbstractRollbackOperateStrategy {


    private boolean isSequence = false;

    /**
     * 会签任务 单个执行人 表达式
     */
    private String assigneeExpr = "assignee";

    /**
     * 会签任务 集合 表达式
     */
    private String assigneeListExpr = "assigneeList";

    public CompletedMultiInstanceTaskAndNextDefaultTaskRollbackOperateStrategy(RollbackParamsTemplate paramsTemplate) {
        super(paramsTemplate);
    }

    @Override
    public void createExecution() {

        if (paramsTemplate.getCurrentTaskElement().getBehavior() instanceof SequentialMultiInstanceBehavior) {
            isSequence = true;
        }

        // 获取 execution
        ExecutionEntity executionEntity = getExecutionEntity();

        VariableInstanceEntity obj = CommandContextUtil.getVariableService(commandContext)
                .findVariableInstanceByExecutionAndName(executionEntity.getProcessInstanceId(), assigneeListExpr);

        if (obj == null || !(obj.getValue() instanceof Collection)) {
            throw new FlowableException("没有可用会签参数:" + assigneeListExpr);
        }
        // 会签执行人变量
        Collection assignees = (Collection) obj.getValue();

        List<HistoricVariableInstance> historicVariableInstances = CommandContextUtil.getHistoricVariableService()
                .findHistoricVariableInstancesByQueryCriteria(
                        new HistoricVariableInstanceQueryImpl()
                                .executionId(paramsTemplate.getHisTask().getExecutionId())
                );

        if (historicVariableInstances.isEmpty()) {
            throw new FlowableException("没有可用会签任务参数");
        }

        // 历史变量
        Map<String, Object> hisVarMap = historicVariableInstances.stream().collect(Collectors.toMap(HistoricVariableInstance::getVariableName, item -> item.getValue()));

        if (hisVarMap.containsKey(assigneeExpr) && hisVarMap.containsKey(RollbackConstants.MultiInstanceConstants.LOOP_COUNTER)) {
            log.info("变量有效");
        } else {
            throw new FlowableException("缺少会签任务变量");
        }

        /**
         *  串行 最终的 loopCounter assignee 都会是最后一个人
         */
        if (isSequence) {

            List<String> assigneeList = (List<String>) runtimeService.getVariableLocal(paramsTemplate.getHisTask().getProcessInstanceId(), assigneeListExpr);
            if (!assigneeList.get(assigneeList.size() - 1).equals(paramsTemplate.getHisTask().getAssignee())) {
                String msg = "不是串行最后一个节点，无法进行回退 ";
                throw new FlowableException(msg);
            }
            // 替换任务执行变量
            assigneeList.set(assigneeList.size() - 1, assignee);
            runtimeService.setVariableLocal(paramsTemplate.getHisTask().getProcessInstanceId(), assigneeListExpr, assigneeList);
        }

        // 流程执行变量
        Integer loopCounter = (Integer) hisVarMap.get(RollbackConstants.MultiInstanceConstants.LOOP_COUNTER);

        // 会签主任务
        ExecutionEntity parentExecution = CommandContextUtil.getExecutionEntityManager(commandContext)
                .createChildExecution(executionEntity.getParent());

        parentExecution.setCurrentFlowElement(paramsTemplate.getCurrentTaskElement());
        parentExecution.setActive(false);
        // 配置 会签 root execution
        parentExecution.setMultiInstanceRoot(true);

        // 配置主 execution 变量
        Map<String, Object> parentVarMap = new HashMap<>();
        parentVarMap.put(RollbackConstants.MultiInstanceConstants.NR_OF_ACTIVE_INSTANCES, 1);
        parentVarMap.put(RollbackConstants.MultiInstanceConstants.NR_OF_COMPLETE_INSTANCES, assignees.size() - 1);
        parentVarMap.put(RollbackConstants.MultiInstanceConstants.NR_OF_INSTANCE, assignees.size());
        parentExecution.setVariablesLocal(parentVarMap);

        if (isSequence) {
            log.info("创建 串行 会签任务");
            createSequenceMultiInstance(parentExecution, assignees);
        } else {
            log.info("创建 并行 会签任务");
            createParallelMultiInstance(parentExecution, assignees, loopCounter);

        }

        removeHisTask(paramsTemplate.getHisTask());
    }

    private void createSequenceMultiInstance(ExecutionEntity parentExecution, Collection assignees) {

        ExecutionEntity newExecution = CommandContextUtil.getExecutionEntityManager(commandContext)
                .createChildExecution(parentExecution);

        Map<String, Object> varMap = new HashMap<>();
        varMap.put(assigneeExpr, assignee);
        varMap.put(RollbackConstants.MultiInstanceConstants.LOOP_COUNTER, assignees.size() - 1);
        newExecution.setCurrentFlowElement(paramsTemplate.getCurrentTaskElement());

        newExecution.setVariablesLocal(varMap);
        newExecution.setActive(true);
        CommandContextUtil.getAgenda(commandContext).planContinueMultiInstanceOperation(newExecution, parentExecution, assignees.size() - 1);

    }

    /**
     * 创建并行会签任务
     *
     * @param parentExecution
     * @param loopCounter
     */
    private void createParallelMultiInstance(ExecutionEntity parentExecution, Collection assignees, Integer loopCounter) {

        for (int i = 0; i < assignees.size(); i++) {
            if (i != loopCounter) {
                Map<String, Object> varMap = new HashMap<>();
                varMap.put(RollbackConstants.MultiInstanceConstants.LOOP_COUNTER, i);
                varMap.put(assigneeExpr, "已完成任务");

//                // 创建 新执行任务
                ExecutionEntity newExecution = newExecution = CommandContextUtil.getExecutionEntityManager(commandContext)
                        .createChildExecution(parentExecution);
                newExecution.setCurrentFlowElement(paramsTemplate.getCurrentTaskElement());
                newExecution.setActive(false);

                newExecution.setVariablesLocal(varMap);

//
//                CommandContextUtil.getExecutionEntityManager(commandContext)
//                        .update(newExecution);
            } else {
                ExecutionEntity newExecution = CommandContextUtil.getExecutionEntityManager(commandContext)
                        .createChildExecution(parentExecution);
                newExecution.setCurrentFlowElement(paramsTemplate.getCurrentTaskElement());

                Map<String, Object> varMap = new HashMap<>();

                varMap.put(assigneeExpr, assignee);
                varMap.put(RollbackConstants.MultiInstanceConstants.LOOP_COUNTER, i);

                newExecution.setVariablesLocal(varMap);
                newExecution.setActive(true);

                CommandContextUtil.getAgenda(commandContext).planContinueMultiInstanceOperation(newExecution, parentExecution, loopCounter);
            }
        }


    }


}
