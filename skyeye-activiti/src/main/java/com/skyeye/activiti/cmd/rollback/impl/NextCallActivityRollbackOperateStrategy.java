package com.skyeye.activiti.cmd.rollback.impl;

import com.skyeye.activiti.cmd.rollback.AbstractRollbackOperateStrategy;
import com.skyeye.activiti.cmd.rollback.RollbackConstants;
import com.skyeye.activiti.cmd.rollback.RollbackParamsTemplate;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.CallActivity;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.service.impl.HistoricTaskInstanceQueryImpl;
import org.flowable.task.service.impl.persistence.entity.HistoricTaskInstanceEntity;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: NextCallActivityRollbackOperateStrategy
 * @Description:
 * @author: skyeye云系列--卫志强
 * @date: 2021/12/18 0:27
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Slf4j
public class NextCallActivityRollbackOperateStrategy extends AbstractRollbackOperateStrategy {

    /**
     * 下一节点 callActivity
     */
    private CallActivity callActivity;

    private List<ExecutionEntity> callActivityExecutionList;

    private ExecutionEntity callActivityProcess;

    public NextCallActivityRollbackOperateStrategy(RollbackParamsTemplate paramsTemplate) {
        super(paramsTemplate);
    }

    @Override
    public void existNextFinishedTask() {

        HistoricTaskInstance hisTask = paramsTemplate.getHisTask();

        Map<String, CallActivity> callActivityMap = paramsTemplate.getCallActivityMap();
        String key = callActivityMap.keySet().iterator().next();
        this.callActivity = callActivityMap.get(key);

        // 下一节点callActivity的 flowId
        callActivityExecutionList = CommandContextUtil.getExecutionEntityManager(commandContext)
                .findExecutionsByParentExecutionAndActivityIds(hisTask.getProcessInstanceId(), Collections.singletonList(callActivity.getId()));

        // callActivity 在 父级流程的 executionId = 子流程的 processInstanceId
        ExecutionEntity executionEntity = callActivityExecutionList.get(0);

        // 子流程
        callActivityProcess = CommandContextUtil.getExecutionEntityManager(commandContext)
                .findSubProcessInstanceBySuperExecutionId(executionEntity.getId());

        List<HistoricTaskInstance> hisTaskList = CommandContextUtil.getHistoricTaskService(commandContext)
                .findHistoricTaskInstancesByQueryCriteria(
                        (HistoricTaskInstanceQueryImpl) new HistoricTaskInstanceQueryImpl()
                                .finished()
                                .processInstanceId(callActivityProcess.getId())
                );

        if (!hisTaskList.isEmpty()) {
            throw new FlowableException("子流程已经具有完成的任务,流程无法回退");
        }
    }

    @Override
    public void setAssignee() {
        // 进行任务执行人配置,之后使用全局监听出发更新
        super.setAssignee();
        String type = RollbackConstants.TASK_TYPE_PREFIX_KEY + paramsTemplate.getHisTask().getProcessInstanceId() + paramsTemplate.getHisTask().getTaskDefinitionKey();
        variables.put(type, NextCallActivityRollbackOperateStrategy.class.getSimpleName());
    }

    @Override
    public void createExecution() {
        HistoricTaskInstance hisTask = paramsTemplate.getHisTask();
        ExecutionEntity executionEntity = CommandContextUtil.getExecutionEntityManager(commandContext)
                .findById(hisTask.getExecutionId());

        if (null == executionEntity) {
            log.info("没有找到execution");
            executionEntity = callActivityExecutionList.get(0);
        }

        ExecutionEntity newExecution = CommandContextUtil.getExecutionEntityManager(commandContext)
                .createChildExecution(executionEntity.getParent());

        // 创建新任务
        createExecution(newExecution);
        // 移除历史任务
        removeHisTask(hisTask);
    }

    @Override
    public void deleteRuntimeTasks() {

        ExecutionEntity parentExecution = callActivityExecutionList.get(0);


        // 清理子流程
        cleanCallActivityProcessInstance(callActivityProcess);
        // 清理主流程记录
        CommandContextUtil.getExecutionEntityManager(commandContext)
                .delete(parentExecution);

    }

    /**
     * // 无效操作
     * CommandContextUtil.getExecutionEntityManager(commandContext)
     * .deleteProcessInstance(callActivityProcess.getId(), "进行流程撤回", false);
     * 清理 调用子流程 相关数据
     *
     * @param processInstance
     */
    private void cleanCallActivityProcessInstance(ExecutionEntity processInstance) {
        // 移除正在运行任务信息
        List<Task> list = CommandContextUtil.getTaskService(commandContext)
                .createTaskQuery()
                .processInstanceId(processInstance.getId())
                .list();
        list.forEach(obj->removeRuntimeTaskOperate((TaskEntity) obj));

        // 移除历史任务信息
        List<HistoricTaskInstanceEntity> historicTaskInstanceList = CommandContextUtil.getHistoricTaskService(commandContext)
                .findHistoricTasksByProcessInstanceId(processInstance.getId());
        historicTaskInstanceList.forEach(obj->CommandContextUtil.getHistoricTaskService(commandContext).deleteHistoricTask(obj));

        // 移除 子流程实例
        CommandContextUtil.getIdentityLinkService(commandContext).deleteIdentityLinksByProcessInstanceId(processInstance.getId());
        CommandContextUtil.getVariableService(commandContext).deleteVariablesByExecutionId(processInstance.getId());
        CommandContextUtil.getExecutionEntityManager(commandContext).delete(processInstance.getId());
    }


}
