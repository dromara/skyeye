package com.skyeye.activiti.cmd.rollback;

import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.Gateway;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.service.impl.HistoricTaskInstanceQueryImpl;
import org.flowable.task.service.impl.persistence.entity.HistoricTaskInstanceEntity;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @ClassName: AbstractGateWayRollbackOperateStrategy
 * @Description:
 * @author: skyeye云系列--卫志强
 * @date: 2021/12/18 0:29
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Slf4j
public abstract class AbstractGateWayRollbackOperateStrategy extends AbstractRollbackOperateStrategy{

    public AbstractGateWayRollbackOperateStrategy(RollbackParamsTemplate paramsTemplate) {
        super(paramsTemplate);
    }

    @Override
    public void existNextFinishedTask() {
        HistoricTaskInstance hisTask = paramsTemplate.getHisTask();

        List<HistoricTaskInstance> historicTaskInstanceList = CommandContextUtil.getHistoricTaskService(commandContext)
                .findHistoricTaskInstancesByQueryCriteria(
                        (HistoricTaskInstanceQueryImpl) new HistoricTaskInstanceQueryImpl()
                                .finished()
                                .processInstanceId(hisTask.getProcessInstanceId())
                                .taskCompletedAfter(hisTask.getEndTime())
                );

        if (!historicTaskInstanceList
                .stream()
                .filter(obj -> paramsTemplate.getNextFlowIdList().contains(obj.getTaskDefinitionKey()))
                .collect(Collectors.toList())
                .isEmpty()) {
            String msg = "存在已完成下一节点任务";
            throw new FlowableException(msg);
        }
    }



    @Override
    public void deleteRuntimeTasks() {

        HistoricTaskInstance hisTask = paramsTemplate.getHisTask();

        // 删除正在执行任务
        List<Task> taskList = CommandContextUtil.getTaskService(commandContext)
                .createTaskQuery()
                .processInstanceId(hisTask.getProcessInstanceId())
                .taskCreatedAfter(hisTask.getEndTime())
                .list();
        taskList.stream()
                .filter(obj -> paramsTemplate.getNextFlowIdList().contains(obj.getTaskDefinitionKey()))
                .forEach(obj -> {
                    log.info("删除运行时任务：" + obj);
                    removeRuntimeTaskOperate((TaskEntity) obj);
                });

        // 删除历史任务
        List<HistoricTaskInstanceEntity> historicTaskInstances = CommandContextUtil.getHistoricTaskService(commandContext)
                .findHistoricTasksByProcessInstanceId(hisTask.getProcessInstanceId());
        historicTaskInstances.forEach(obj -> {
            if (obj.getCreateTime().getTime() <= hisTask.getEndTime().getTime() && paramsTemplate.getNextFlowIdList().contains(obj.getTaskDefinitionKey())) {
                log.info("删除历史任务：" + obj);
                CommandContextUtil.getHistoricTaskService(commandContext).deleteHistoricTask(obj);
            }
        });
    }

    /**
     * 使用并行网关进行 线条流汇总时候,会出现 特殊bug
     *
     * @param parent
     */
    protected void processGateway(ExecutionEntity parent) {

        // 当前正在运行所以任务
        List<TaskEntity> taskEntityList = CommandContextUtil.getTaskService(commandContext)
                .findTasksByProcessInstanceId(parent.getProcessInstanceId());

        boolean isExistPassGatewayTask = false;

        // 下一节点任务
        List<TaskEntity> nextTaskList = taskEntityList.stream()
                .filter(obj -> paramsTemplate.getNextFlowIdList().contains(obj.getTaskDefinitionKey()))
                .collect(Collectors.toList());

        if (!nextTaskList.isEmpty()) {
            log.info("已经生成过网关任务");
            isExistPassGatewayTask = true;

            // 网关的连线
            Map<String, SequenceFlow> sqFlowMap = new HashMap<>();

            paramsTemplate.getGatewayMap().values().forEach(obj -> {
                obj.getIncomingFlows().forEach(item -> {

                    if (null != paramsTemplate.getGatewayMap().get(item.getSourceRef())) {
                        log.info("跳过gateway 间连线:" + item);
                        return;
                    }
                    if (paramsTemplate.getHisTask().getTaskDefinitionKey().equals(item.getSourceRef())) {
                        log.info("跳过当前回退历史任务:" + item);
                        return;
                    }
                    sqFlowMap.put(item.getSourceRef(), item);
                });
            });

            // 创建网关相关连线 execution
            createCompleteGatewayExecution(parent, sqFlowMap);

            // 删除当前正在执行任务
            Set<String> nestTaskIdSet = new HashSet<>();
            nextTaskList.forEach(obj -> {
                removeRuntimeTaskOperate(obj);
                nestTaskIdSet.add(obj.getId());
            });

            // 移除正在执行下一节点历史任务
            List<HistoricTaskInstanceEntity> historicTaskInstanceList = CommandContextUtil.getHistoricTaskService(commandContext)
                    .findHistoricTasksByProcessInstanceId(parent.getProcessInstanceId());
            historicTaskInstanceList.forEach(obj -> {
                if (nestTaskIdSet.contains(obj.getId())) {
                    CommandContextUtil.getHistoricTaskService(commandContext).deleteHistoricTask(obj);
                }
            });
        }

        if (isExistPassGatewayTask) {


        } else {
            log.info("移除网关连线");

            List<ExecutionEntity> executionEntityList = CommandContextUtil.getExecutionEntityManager(commandContext)
                    .findExecutionsByParentExecutionAndActivityIds(parent.getProcessInstanceId(), paramsTemplate.getGatewayMap().keySet());

            Map<String, FlowElement> targetGatewayMap = paramsTemplate.getCurrentTaskElement()
                    .getOutgoingFlows().stream().filter(obj -> obj.getTargetFlowElement() instanceof Gateway)
                    .map(obj -> obj.getTargetFlowElement())
                    .collect(Collectors.toMap(FlowElement::getId, obj -> obj));

            List<ExecutionEntity> toRemoveList = new ArrayList<>();

            executionEntityList.forEach(obj -> {
                if (null != targetGatewayMap.get(obj.getActivityId())) {
                    toRemoveList.add(obj);
                    targetGatewayMap.put(obj.getActivityId(), null);
                }
            });

            if (!toRemoveList.isEmpty()) {
                toRemoveList.forEach(obj -> {
                    log.info("移除连线:" + obj);
                    CommandContextUtil.getExecutionEntityManager(commandContext).delete(obj);
                });
            }
        }
    }

    /**
     * 创建 Gateway 相关连线
     *
     * @param parent
     * @param sqFlowMap
     */
    protected void createCompleteGatewayExecution(ExecutionEntity parent, Map<String, SequenceFlow> sqFlowMap) {

        sqFlowMap.values().forEach(obj -> {
            ExecutionEntity newExecution = CommandContextUtil.getExecutionEntityManager(commandContext)
                    .createChildExecution(parent);

            newExecution.setCurrentFlowElement(obj.getTargetFlowElement());
            newExecution.setActive(false);

            log.debug("创建 gateway 连线 execution");
            CommandContextUtil.getAgenda(commandContext).planContinueProcessInCompensation(newExecution);
        });
    }

}
