package com.skyeye.activiti.cmd.rollback;

import com.skyeye.activiti.mapper.HistoryActivityInstanceMapper;
import com.skyeye.common.util.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.impl.ActivityInstanceQueryImpl;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.engine.runtime.ActivityInstance;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.service.impl.HistoricTaskInstanceQueryImpl;
import org.flowable.task.service.impl.persistence.entity.HistoricTaskInstanceEntity;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: AbstractRollbackOperateStrategy
 * @Description:
 * @author: skyeye云系列--卫志强
 * @date: 2021/12/18 0:30
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Slf4j
public abstract class AbstractRollbackOperateStrategy implements RollbackOperateStrategy {

    protected RollbackParamsTemplate paramsTemplate;
    /**
     * 任务处理人
     */
    protected String assignee;

    protected Map<String, Object> variables;

    protected CommandContext commandContext;

    /**
     * 无法操作
     */
    protected HistoryActivityInstanceMapper historyActivityInstanceMapper;

    protected RuntimeService runtimeService;

    public AbstractRollbackOperateStrategy(RollbackParamsTemplate paramsTemplate) {
        this.paramsTemplate = paramsTemplate;
        this.historyActivityInstanceMapper = SpringUtils.getBean(HistoryActivityInstanceMapper.class);
        this.runtimeService = SpringUtils.getBean(RuntimeService.class);
    }

    @Override
    public void process(CommandContext commandContext, String assignee) {
        process(commandContext, assignee, new HashMap<>());
    }

    @Override
    public void process(CommandContext commandContext, String assignee, Map<String, Object> variables) {

        this.commandContext = commandContext;
        this.assignee = assignee;
        this.variables = variables;

        log.info("处理 existNextFinishedTask");
        existNextFinishedTask();
        log.info("配置任务执行人 setAssignee");
        setAssignee();
        log.info("处理 createExecution");
        createExecution();
        log.info("处理 deleteRuntimeTasks");
        deleteRuntimeTasks();
        log.info("处理 deleteHisActInstance");
        deleteHisActInstance();
    }

    /**
     *  获取 当前执行 execution
     * @return
     */
    protected ExecutionEntity getExecutionEntity() {

        ExecutionEntity executionEntity = CommandContextUtil.getExecutionEntityManager(commandContext)
                .findById(paramsTemplate.getHisTask().getExecutionId());

        if (null == executionEntity) {

            log.info("没找到回退任务的 execution,从同级任务处获取");
            List<ExecutionEntity> executionEntityList = CommandContextUtil
                    .getExecutionEntityManager(commandContext)
                    .findExecutionsByParentExecutionAndActivityIds(paramsTemplate.getHisTask().getProcessInstanceId(), paramsTemplate.getNextFlowIdList());
            if (executionEntityList.isEmpty()) {
                throw new FlowableException("没有找到临近节点");
            }
            executionEntity = executionEntityList.get(0);
        }

        return executionEntity;
    }

    protected void removeHisTask(HistoricTaskInstance hisTask) {
        // 移除历史任务列表
        log.info("移除历史任务 [ id = " + hisTask.getId() + " ]");
        CommandContextUtil.getHistoricTaskService().deleteHistoricTask((HistoricTaskInstanceEntity) hisTask);
    }

    /**
     *  移除 ru_ 相关数据
     * @param obj
     */
    protected void removeRuntimeTaskOperate(TaskEntity obj) {
        log.debug("移除 IdentityLink: " + obj.getId());
        CommandContextUtil.getIdentityLinkService(commandContext).deleteIdentityLinksByTaskId(obj.getId());
        log.debug("移除 Variable: " + obj.getId());
        CommandContextUtil.getVariableService(commandContext).deleteVariablesByExecutionId(obj.getExecutionId());
        log.debug("移除 Task: " + obj.getId());
        CommandContextUtil.getTaskService(commandContext).deleteTasksByExecutionId(obj.getExecutionId());
        log.debug("移除 execution: " + obj.getExecutionId());
        CommandContextUtil.getExecutionEntityManager(commandContext).delete(obj.getExecutionId());
    }


    protected void createExecution(ExecutionEntity newExecution) {
        newExecution.setActive(true);
        // 测试设置变量
        newExecution.setVariablesLocal(variables);
        newExecution.setCurrentFlowElement(paramsTemplate.getCurrentTaskElement());

        // 创建新任务
        log.debug("创建新任务");
        CommandContextUtil.getAgenda(commandContext).planContinueProcessInCompensation(newExecution);
    }

    @Override
    public void existNextFinishedTask() {
        HistoricTaskInstance hisTask = paramsTemplate.getHisTask();

        List<HistoricTaskInstance> hisTaskList = CommandContextUtil.getHistoricTaskService().findHistoricTaskInstancesByQueryCriteria(
                (HistoricTaskInstanceQueryImpl) new HistoricTaskInstanceQueryImpl()
                        .processInstanceId(hisTask.getProcessInstanceId())
                        .taskCompletedAfter(hisTask.getEndTime())
        );

        if (!hisTaskList.isEmpty()) {
            hisTaskList.forEach(obj -> {
                if (paramsTemplate.getNextFlowIdList().contains(obj.getTaskDefinitionKey())) {
                    String msg = "存在已完成下一节点任务";
                    throw new FlowableException(msg);
                }
            });
        }
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
                    && paramsTemplate.getNextFlowIdList().contains(obj.getActivityId())) {
                ids.add(obj.getId());
            }
            // 当前任务的连线 ID
            if (paramsTemplate.getHisTask().getTaskDefinitionKey().equals(obj.getActivityId())
                    && obj.getEndTime().getTime() > paramsTemplate.getHisTask().getCreateTime().getTime()
            ) {
                ids.add(obj.getId());
            }
        });

        // 移除当前任务连线
//        LOGGER.debug("移除当前任务连线");
//        ids.forEach(obj -> CommandContextUtil.getActivityInstanceEntityManager(commandContext).delete(obj));
        // 移除历史任务连线
        // 历史任务删除失败，改用自己写mapper 完成删除功能
//        ids.forEach(obj -> CommandContextUtil.getHistoricActivityInstanceEntityManager(commandContext).delete(obj));
        log.debug("移除历史任务连线");
        ids.forEach(obj -> historyActivityInstanceMapper.delete(obj));

    }

    @Override
    public void deleteRuntimeTasks() {
        HistoricTaskInstance hisTask = paramsTemplate.getHisTask();

        List<TaskEntity> taskEntityList = CommandContextUtil.getTaskService(commandContext).findTasksByProcessInstanceId(hisTask.getProcessInstanceId());
        taskEntityList.forEach(obj -> {
            if (paramsTemplate.getNextFlowIdList().contains(obj.getTaskDefinitionKey())){
                log.info("移除正在执行的下一节点任务");
                // 移除任务
                removeRuntimeTaskOperate(obj);
            }
        });


        // 移除历史任务信息
        List<HistoricTaskInstanceEntity> historicTaskInstanceList = CommandContextUtil.getHistoricTaskService(commandContext)
                .findHistoricTasksByProcessInstanceId(hisTask.getProcessInstanceId());
        historicTaskInstanceList.forEach(obj->{
            if (paramsTemplate.getNextFlowIdList().contains(obj.getTaskDefinitionKey())){
                CommandContextUtil.getHistoricTaskService(commandContext).deleteHistoricTask(obj);
            }
        });
    }

    @Override
    public void setAssignee() {
        HistoricTaskInstance hisTask = paramsTemplate.getHisTask();
        String key = RollbackConstants.ASSIGNEE_PREFIX_KEY + hisTask.getProcessInstanceId() + hisTask.getTaskDefinitionKey();
        variables.put(key, assignee);
    }

    @Override
    public void setAssigneeExpr(String assigneeExpr, String assigneeListExpr) {
        // to override
    }

}
