package com.skyeye.activiti.cmd.rollback.listeners;

import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.common.engine.impl.event.FlowableEntityEventImpl;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.skyeye.activiti.cmd.rollback.RollbackConstants;
import com.skyeye.activiti.cmd.rollback.impl.DefaultTaskNextGatewayRollbackOperateStrategy;
import com.skyeye.activiti.cmd.rollback.impl.NextCallActivityRollbackOperateStrategy;
import com.skyeye.activiti.cmd.rollback.impl.NextDefaultUserTaskRollbackOperateStrategy;
import com.skyeye.activiti.cmd.rollback.impl.NextSubProcessRollbackOperateStrategy;
import com.skyeye.common.util.SpringUtils;

/**
 *
 * @ClassName: RollbackEventListener
 * @Description:
 * @author: skyeye云系列--卫志强
 * @date: 2021/12/18 0:24
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Component
public class RollbackEventListener implements FlowableEventListener {

    private static Logger LOGGER = LoggerFactory.getLogger(RollbackEventListener.class);

    @Override
    public void onEvent(FlowableEvent event) {
        if (FlowableEngineEventType.TASK_CREATED.name().equals(event.getType().name())) {
            TaskEntity taskEntity = (TaskEntity)((FlowableEntityEventImpl)event).getEntity();

            RuntimeService runtimeService = SpringUtils.getBean(RuntimeService.class);
            TaskService taskService = SpringUtils.getBean(TaskService.class);

            String key = RollbackConstants.ASSIGNEE_PREFIX_KEY + taskEntity.getProcessInstanceId()
                + taskEntity.getTaskDefinitionKey();
            String type = RollbackConstants.TASK_TYPE_PREFIX_KEY + taskEntity.getProcessInstanceId()
                + taskEntity.getTaskDefinitionKey();

            Object assigneeValue = runtimeService.getVariable(taskEntity.getExecutionId(), key);
            Object assigneeType = runtimeService.getVariable(taskEntity.getExecutionId(), type);
            if (assigneeValue != null && assigneeType != null) {
                LOGGER.info("回滚任务处理");
                if (NextDefaultUserTaskRollbackOperateStrategy.class.getSimpleName().equals(assigneeType)
                    || NextSubProcessRollbackOperateStrategy.class.getSimpleName().equals(assigneeType)
                    || NextCallActivityRollbackOperateStrategy.class.getSimpleName().equals(assigneeType)
                    || DefaultTaskNextGatewayRollbackOperateStrategy.class.getSimpleName().equals(assigneeType)) {
                    LOGGER.info("设置普通任务执行人");
                    taskService.setAssignee(taskEntity.getId(), (String)assigneeValue);
                }
            }
        }
    }

    @Override
    public boolean isFailOnException() {
        return false;
    }

    @Override
    public boolean isFireOnTransactionLifecycleEvent() {
        return false;
    }

    @Override
    public String getOnTransaction() {
        return null;
    }
}
