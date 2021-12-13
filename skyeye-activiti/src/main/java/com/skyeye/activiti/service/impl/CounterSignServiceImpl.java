/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.activiti.service.impl;

import com.skyeye.activiti.service.CounterSignService;
import com.skyeye.common.constans.ActivitiConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.MultiInstanceLoopCharacteristics;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.bpmn.behavior.MultiInstanceActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.activiti.engine.impl.bpmn.behavior.SequentialMultiInstanceBehavior;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.el.ExpressionManager;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: CounterSignServiceImpl
 * @Description: 会签相关服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/12/13 20:45
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class CounterSignServiceImpl implements CounterSignService {

    @Autowired
    protected ProcessEngine processEngine;

    @Autowired
    protected TaskService taskService;

    @Autowired
    protected RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    /**
     * 将 普通节点转换成为会签 任务
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void covertToMultiInstance(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();

    }

    /**
     * 创建多实例 循环解释器
     *
     * @param isSequential    是否串行
     * @param assigneeListExp 用户组表达
     * @param assignee        用户标识
     * @return
     */
    @Override
    public MultiInstanceLoopCharacteristics createMultiInstanceLoopCharacteristics(boolean isSequential, String assigneeListExp, String assignee) {
        MultiInstanceLoopCharacteristics multiInstanceLoopCharacteristics = new MultiInstanceLoopCharacteristics();
        multiInstanceLoopCharacteristics.setSequential(isSequential);
        multiInstanceLoopCharacteristics.setInputDataItem(assigneeListExp);
        multiInstanceLoopCharacteristics.setElementVariable(assignee);
        return multiInstanceLoopCharacteristics;
    }

    /**
     * 创建多实例 循环解释器
     *
     * @param isSequential 是否 串行
     * @return
     */
    @Override
    public MultiInstanceLoopCharacteristics createMultiInstanceLoopCharacteristics(boolean isSequential) {
        return this.createMultiInstanceLoopCharacteristics(isSequential, ActivitiConstants.DEFAULT_ASSIGNEE_LIST_EXP, ActivitiConstants.ASSIGNEE_USER);
    }

    /**
     * 创建 多实例 行为解释器
     *
     * @param processInstanceId 流程id
     * @param sequential
     * @return
     */
    @Override
    public MultiInstanceActivityBehavior createMultiInstanceBehavior(String processInstanceId, boolean sequential) {
        return createMultiInstanceBehavior(processInstanceId, sequential, ActivitiConstants.DEFAULT_ASSIGNEE_LIST_EXP, ActivitiConstants.ASSIGNEE_USER);
    }

    /**
     * 创建多实例行为解释器
     *
     * @param processInstanceId 流程id
     * @param sequential        是否串行
     * @param assigneeListExp   用户组表达
     * @param assigneeExp       用户标识
     * @return
     */
    @Override
    public MultiInstanceActivityBehavior createMultiInstanceBehavior(String processInstanceId, boolean sequential, String assigneeListExp, String assigneeExp) {
        ActivityImpl activityNode = this.getCurrentActivityNode(processInstanceId);
        if(activityNode == null){
            return null;
        }
        UserTask currentNode = this.getCurrentUserTask(processInstanceId);
        ProcessEngineConfigurationImpl processEngineConfiguration = (ProcessEngineConfigurationImpl) processEngine.getProcessEngineConfiguration();
        /**
         *  创建解释器
         */
        UserTaskActivityBehavior userTaskActivityBehavior = processEngineConfiguration.getActivityBehaviorFactory()
                .createUserTaskActivityBehavior(currentNode, ((UserTaskActivityBehavior) activityNode.getActivityBehavior()).getTaskDefinition());

        MultiInstanceActivityBehavior behavior = null;
        if (sequential) {
            behavior = new SequentialMultiInstanceBehavior(activityNode, userTaskActivityBehavior);
        } else {
            behavior = new ParallelMultiInstanceBehavior(activityNode, userTaskActivityBehavior);
        }

        /**
         *   注入表达式 解释器
         */
        ExpressionManager expressionManager = processEngineConfiguration.getExpressionManager();

        /**
         * 设置表达式变量
         */
        behavior.setCollectionExpression(expressionManager.createExpression(assigneeListExp));
        behavior.setCollectionElementVariable(assigneeExp);

        return behavior;
    }

    private UserTask getCurrentUserTask(String processInstanceId) {
        Task cueerntTask = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(cueerntTask.getProcessDefinitionId());
        org.activiti.bpmn.model.Process process = bpmnModel.getProcesses().get(0);
        // 当前节点
        UserTask currentNode = (UserTask) process.getFlowElement(cueerntTask.getTaskDefinitionKey());
        return currentNode;
    }

    /**
     * 获取当前的activityImpl节点
     *
     * @param processInstanceId processInstanceId
     * @return
     */
    private ActivityImpl getCurrentActivityNode(String processInstanceId){
        // 获取流程发布Id信息
        String definitionId = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult().getProcessDefinitionId();
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService).getDeployedProcessDefinition(definitionId);
        ExecutionEntity execution = (ExecutionEntity) runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();

        // 当前流程节点Id信息
        String activitiId = execution.getActivityId();
        // 获取流程所有节点信息
        List<ActivityImpl> activitiList = processDefinitionEntity.getActivities();
        // 遍历所有节点信息
        for (ActivityImpl activityImpl : activitiList) {
            String id = activityImpl.getId();
            if (activitiId.equals(id)) {
                return activityImpl;
            }
        }
        return null;
    }

    /**
     * 将 普通节点转换成为会签 任务
     *
     * @param taskId
     * @param sequential
     * @param data
     */
    @Override
    public void covertToMultiInstance(String taskId, boolean sequential, Map<String, Object> data) {

    }

    /**
     * 将 普通节点转换成为会签 任务
     *
     * @param taskId
     * @param sequential
     * @param assigneeExp 任务执行人表达式
     * @param data
     */
    @Override
    public void covertToMultiInstance(String taskId, boolean sequential, String assigneeExp, Map<String, Object> data) {

    }
}
