/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.activiti.cmd.nextusertask;

import org.apache.commons.collections.CollectionUtils;
import org.flowable.bpmn.model.*;
import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.util.condition.ConditionUtil;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: FindNextUserTaskNodeCmd
 * @Description: 获取下一个UserTask的信息
 * @author: skyeye云系列--卫志强
 * @date: 2021/12/18 23:49
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class FindNextUserTaskNodeCmd implements Command<UserTask> {

    private final ExecutionEntity execution;
    private final BpmnModel bpmnModel;
    private Map<String, Object> vars;

    /**
     * 返回下一用户节点
     */
    private UserTask nextUserTask;

    /**
     * @param execution 当前执行实例
     * @param bpmnModel 当前执行实例的模型
     * @param vars      参与计算流程条件的变量
     */
    public FindNextUserTaskNodeCmd(ExecutionEntity execution, BpmnModel bpmnModel, Map<String, Object> vars) {
        this.execution = execution;
        this.bpmnModel = bpmnModel;
        this.vars = vars;
    }

    @Override
    public UserTask execute(CommandContext commandContext) {
        // 当前流程节点信息
        FlowElement currentNode = bpmnModel.getFlowElement(execution.getActivityId());
        execution.setVariables(vars);
        // 获取流程曲线走向
        List<SequenceFlow> outgoingFlows = ((FlowNode) currentNode).getOutgoingFlows();
        if (CollectionUtils.isNotEmpty(outgoingFlows)) {
            this.findNextUserTaskNode(outgoingFlows, execution);
        }
        return nextUserTask;
    }

    /**
     * 下一个任务节点信息,
     *
     * 如果下一个节点为用户任务则直接返回,
     * 如果下一个节点为排他网关, 获取排他网关Id信息, 根据排他网关Id信息和execution获取流程实例排他网关Id为key的变量值,
     * 根据变量值分别执行排他网关后线路中的el表达式, 并找到el表达式通过的线路后的用户任务
     *
     * @param outgoingFlows 曲线走向
     * @param execution 执行器，包含form表单参数信息
     * @return
     */
    void findNextUserTaskNode(List<SequenceFlow> outgoingFlows, ExecutionEntity execution) {
        sw:
        for (SequenceFlow outgoingFlow : outgoingFlows) {
            if (ConditionUtil.hasTrueCondition(outgoingFlow, execution)) {
                if (outgoingFlow.getTargetFlowElement() instanceof ExclusiveGateway) {
                    // 只有排他网关才继续
                    ExclusiveGateway exclusiveGateway = (ExclusiveGateway) outgoingFlow.getTargetFlowElement();
                    findNextUserTaskNode(exclusiveGateway.getOutgoingFlows(), execution);
                } else if (outgoingFlow.getTargetFlowElement() instanceof UserTask) {
                    nextUserTask = (UserTask) outgoingFlow.getTargetFlowElement();
                    // 找到第一个符合条件的userTask就跳出循环
                    break sw;
                }
            }
        }
    }

}
