package com.skyeye.activiti.cmd.rollback.impl;

import com.skyeye.activiti.cmd.rollback.AbstractRollbackOperateStrategy;
import com.skyeye.activiti.cmd.rollback.RollbackConstants;
import com.skyeye.activiti.cmd.rollback.RollbackParamsTemplate;
import org.flowable.bpmn.model.SubProcess;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.engine.impl.ActivityInstanceQueryImpl;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.engine.runtime.ActivityInstance;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.service.impl.HistoricTaskInstanceQueryImpl;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 *
 * @ClassName: NextSubProcessRollbackOperateStrategy
 * @Description: 下一节点是 嵌入式子流程
 * @author: skyeye云系列--卫志强
 * @date: 2021/12/18 0:31
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class NextSubProcessRollbackOperateStrategy extends AbstractRollbackOperateStrategy {

    private Logger LOGGER = LoggerFactory.getLogger(NextSubProcessRollbackOperateStrategy.class);

    /**
     * 嵌入子流程中所有节点ID集合
     */
    private Set<String> subProcessItemKeySet = new HashSet<>();

    private SubProcess subProcess;

    public NextSubProcessRollbackOperateStrategy(RollbackParamsTemplate paramsTemplate) {
        super(paramsTemplate);
    }

    @Override
    public void existNextFinishedTask() {
        Map<String, SubProcess> subProcessMap = paramsTemplate.getSubProcessMap();
        List<HistoricTaskInstance> historicTaskInstances = CommandContextUtil.getHistoricTaskService(commandContext)
                .findHistoricTaskInstancesByQueryCriteria(
                        (HistoricTaskInstanceQueryImpl) new HistoricTaskInstanceQueryImpl()
                                .taskCompletedAfter(paramsTemplate.getHisTask().getEndTime())
                                .finished()
                );

        String key = subProcessMap.keySet().iterator().next();
        this.subProcess = subProcessMap.get(key);

        subProcess.getFlowElements().forEach(obj -> subProcessItemKeySet.add(obj.getId()));
        if (!historicTaskInstances.isEmpty()) {
            historicTaskInstances.forEach(obj -> {
                if (subProcessItemKeySet.contains(obj.getTaskDefinitionKey())) {
                    LOGGER.error("出现已完成任务，无法进行流程节点撤回: [" + obj + "]");
                    throw new FlowableException("出现已完成任务，无法进行流程节点撤回");
                }
            });
        }

    }

    @Override
    public void setAssignee() {
        // 进行任务执行人配置,之后使用全局监听出发更新
        super.setAssignee();
        String type = RollbackConstants.TASK_TYPE_PREFIX_KEY + paramsTemplate.getHisTask().getProcessInstanceId() + paramsTemplate.getHisTask().getTaskDefinitionKey();
        variables.put(type, NextSubProcessRollbackOperateStrategy.class.getSimpleName());
    }


    @Override
    public void createExecution() {

        HistoricTaskInstance hisTask = paramsTemplate.getHisTask();
        ExecutionEntity executionEntity = CommandContextUtil.getExecutionEntityManager(commandContext).findById(hisTask.getExecutionId());

        /**
         *  subProcess 作为下一节点的时候，hisTask的execution会被关闭调。所以需要重新创建
         */
        if (null == executionEntity) {
            LOGGER.info("hisTask:execution 为 null");

            List<ExecutionEntity> executionEntityList = CommandContextUtil
                    .getExecutionEntityManager(commandContext)
                    .findExecutionsByParentExecutionAndActivityIds(hisTask.getProcessInstanceId(), paramsTemplate.getNextFlowIdList());
            if (executionEntityList.isEmpty()) {
                throw new FlowableException("没有找到临近节点");
            }
            executionEntity = executionEntityList.get(0);
        }
        // 创建主线
        ExecutionEntity newExecution = CommandContextUtil.getExecutionEntityManager(commandContext).createChildExecution(executionEntity.getParent());

        // 创建新任务
        createExecution(newExecution);
        // 移除历史任务
        removeHisTask(hisTask);
    }

    @Override
    public void deleteHisActInstance() {

        List<ActivityInstance> activityInstanceEntityList = CommandContextUtil.getActivityInstanceEntityManager(commandContext)
                .findActivityInstancesByQueryCriteria(
                        new ActivityInstanceQueryImpl()
                                .processInstanceId(paramsTemplate.getHisTask().getProcessInstanceId())
                );


        List<String> ids = new ArrayList<>();
        activityInstanceEntityList.forEach(obj -> {
            // 时间大于 任务创建时间 之后线条
            if (obj.getStartTime().getTime() > paramsTemplate.getHisTask().getCreateTime().getTime()
                    && subProcessItemKeySet.contains(obj.getActivityId())) {
                ids.add(obj.getId());
            }
            // 当前任务的连线 ID
            if (paramsTemplate.getHisTask().getTaskDefinitionKey().equals(obj.getActivityId())
                    && obj.getEndTime().getTime() > paramsTemplate.getHisTask().getCreateTime().getTime()
            ) {
                ids.add(obj.getId());
            }
        });

        LOGGER.debug("移除历史任务连线");
        ids.forEach(obj -> historyActivityInstanceMapper.delete(obj));

    }

    @Override
    public void deleteRuntimeTasks() {
        HistoricTaskInstance hisTask = paramsTemplate.getHisTask();

        List<TaskEntity> taskEntityList = CommandContextUtil.getTaskService(commandContext).findTasksByProcessInstanceId(hisTask.getProcessInstanceId());

        taskEntityList.forEach(obj -> {
            if (subProcessItemKeySet.contains(obj.getTaskDefinitionKey())) {
                LOGGER.info("移除正在执行的下一节点任务");
                // 移除任务
                removeRuntimeTaskOperate(obj);
            }
        });

        // 获取 subProcess 的 ExecutionEntity
        Collection<ExecutionEntity> executionEntities = CommandContextUtil
                .getExecutionEntityManager(commandContext)
                .findExecutionsByParentExecutionAndActivityIds(hisTask.getProcessInstanceId(), Collections.singletonList(subProcess.getId()));

        executionEntities.forEach(obj -> {
            LOGGER.info("移除 subProcess 层级execution");
            List<ExecutionEntity> children = CommandContextUtil
                    .getExecutionEntityManager(commandContext)
                    .findChildExecutionsByParentExecutionId(obj.getId());

            // 移除级联子节点
            children.forEach(item -> CommandContextUtil
                    .getExecutionEntityManager(commandContext)
                    .delete(item));

            // 移除 subProcess 顶级
            CommandContextUtil
                    .getExecutionEntityManager(commandContext)
                    .delete(obj);
        });

    }

}
