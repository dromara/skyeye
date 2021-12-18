package com.skyeye.activiti.cmd.rollback;

import com.skyeye.activiti.cmd.rollback.impl.*;
import org.flowable.bpmn.model.*;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.TaskService;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 *
 * @ClassName: DefaultRollbackStrategyFactoryBean
 * @Description:
 * @author: skyeye云系列--卫志强
 * @date: 2021/12/18 0:28
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Component
public class DefaultRollbackStrategyFactoryBean implements RollbackStrategyFactory {

    private Logger LOGGER = LoggerFactory.getLogger(DefaultRollbackStrategyFactoryBean.class);

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private TaskService taskService;


    @Override
    public RollbackOperateStrategy createStrategy(HistoricTaskInstance hisTask) {

        BpmnModel bpmnModel = repositoryService.getBpmnModel(hisTask.getProcessDefinitionId());

        RollbackParamsTemplate template = new RollbackParamsTemplate();

        template.setHisTask(hisTask);
        // 获取当前任务的节点信息
        getThisUserTask(template, bpmnModel, hisTask);
        // 获取下一节点信息
        getNextElementInfo(template, bpmnModel);
        // 创建策略
        RollbackOperateStrategy strategy = createStrategyInstance(template);

        return strategy;
    }

    /**
     * @param template
     * @return
     */
    @Override
    public boolean currentMultiInstanceTaskUnfinished(RollbackParamsTemplate template) {

        if (template.getCurrentTaskElement().getLoopCharacteristics() == null) {
            LOGGER.info("当前任务节点不是会签节点");
            return false;
        }

        long count = taskService.createTaskQuery()
                .processInstanceId(template.getHisTask().getProcessInstanceId())
                .taskDefinitionKey(template.getHisTask().getTaskDefinitionKey())
                .count();
        if (count > 0) {
            LOGGER.info("具有未完成当前节点任务");
            return true;
        }

        return false;
    }

    /**
     * 生成策略
     *
     * @param template
     * @return
     */
    private RollbackOperateStrategy createStrategyInstance(RollbackParamsTemplate template) {

        // 处理正在执行会签节点
        if (currentMultiInstanceTaskUnfinished(template)) {
            LOGGER.info("-回退 正在执行会签 策略");
            return new ActiveMultiInstanceTaskRollbackOperateStrategy(template);
        }

        // 默认节点处理策略
        if (template.getCurrentTaskElement().getLoopCharacteristics() == null
                && template.getGatewayMap().isEmpty()
                && !template.getNextUserTaskList().isEmpty()) {
            LOGGER.info("-回退 普通任务 策略");
            return new NextDefaultUserTaskRollbackOperateStrategy(template);
        }

        // 下一节点 嵌入式子流程
        if (template.getCurrentTaskElement().getLoopCharacteristics() == null
                && template.getGatewayMap().isEmpty()
                && !template.getSubProcessMap().isEmpty()) {
            LOGGER.info("-回退 嵌入式子流程 策略");
            return new NextSubProcessRollbackOperateStrategy(template);
        }

        // 下一节点 调用式子流程
        if (template.getCurrentTaskElement().getLoopCharacteristics() == null
                && template.getGatewayMap().isEmpty()
                && !template.getCallActivityMap().isEmpty()) {
            LOGGER.info("-回退 调用式子流程 策略");
            return new NextCallActivityRollbackOperateStrategy(template);
        }

        // 下一节点 网关,多级网关
        if (template.getCurrentTaskElement().getLoopCharacteristics() == null
                && !template.getGatewayMap().isEmpty()) {
            LOGGER.info("-回退 网关, 多级网关 策略");
            return new DefaultTaskNextGatewayRollbackOperateStrategy(template);
        }

        // 会签已完成
        if (template.getCurrentTaskElement().getLoopCharacteristics() != null) {

            if ( template.getGatewayMap().isEmpty()
                    && !template.getNextUserTaskList().isEmpty()) {
                LOGGER.info("-回退 已完成会签,下一节点普通任务 策略");
                return new CompletedMultiInstanceTaskAndNextDefaultTaskRollbackOperateStrategy(template);
            }

            return null;
        }
        return null;
    }


    /**
     * 获取下一节点任务
     *
     * @param template
     * @param bpmnModel
     */
    private void getNextElementInfo(RollbackParamsTemplate template, BpmnModel bpmnModel) {

        if (null != template.getCurrentSubProcess()) {
            LOGGER.info("当前任务存在于 SubProcess");
            getNextElementInfo(template, template.getCurrentSubProcess(), template.getCurrentTaskElement().getOutgoingFlows());
            return;
        }
        LOGGER.info("当前任务存在于 bpmnModel");
        // 主线流程图
        getNextElementInfo(template, bpmnModel.getMainProcess(), template.getCurrentTaskElement().getOutgoingFlows());
    }

    /**
     * 获取下一节点网关任务
     *
     * @param template
     * @param flowElementsContainer
     * @param outgoingFlows
     */
    private void getNextElementInfo(RollbackParamsTemplate template, FlowElementsContainer flowElementsContainer, List<SequenceFlow> outgoingFlows) {

        for (SequenceFlow flow : outgoingFlows) {

            template.getNextFlowIdList().add(flow.getId());
            template.getOutGoingMap().put(flow.getId(), flow);

            // 下一节点
            FlowElement flowElement = flowElementsContainer.getFlowElement(flow.getTargetRef());
            template.getNextFlowIdList().add(flowElement.getId());

            if (flowElement instanceof UserTask) {
                LOGGER.info("下一节点：UserTask");
                template.getNextUserTaskList().add((UserTask) flowElement);
            } else if (flowElement instanceof Gateway) {
                LOGGER.info("下一节点：Gateway");
                Gateway gateway = ((Gateway) flowElement);
                template.getGatewayMap().put(gateway.getId(), gateway);
                getNextElementInfo(template, flowElementsContainer, gateway.getOutgoingFlows());
            } else if (flowElement instanceof SubProcess) {
                LOGGER.info("下一节点：SubProcess");
                SubProcess subProcess = (SubProcess) flowElement;
                template.getSubProcessMap().put(subProcess.getId(), subProcess);
            } else if (flowElement instanceof CallActivity) {
                LOGGER.info("下一节点：CallActivity");
                CallActivity callActivity = (CallActivity) flowElement;
                template.getCallActivityMap().put(callActivity.getId(), callActivity);
            }
        }
    }

    /**
     * 获取当前任务
     *
     * @param template
     * @param bpmnModel
     * @param hisTask
     */
    private void getThisUserTask(RollbackParamsTemplate template, BpmnModel bpmnModel, HistoricTaskInstance hisTask) {

        FlowElement flowElement = bpmnModel.getMainProcess().getFlowElement(hisTask.getTaskDefinitionKey());
        if (null != flowElement && flowElement instanceof UserTask) {
            LOGGER.info("获取回退任务节点");
            template.setCurrentTaskElement((UserTask) flowElement);
            return;
        }

        for (FlowElement item : bpmnModel.getMainProcess().getFlowElements()) {
            if (item instanceof SubProcess) {
                flowElement = ((SubProcess) item).getFlowElement(hisTask.getTaskDefinitionKey());
                if (null != flowElement) {
                    LOGGER.info("当前节点存在于嵌入式子流程");
                    template.setCurrentTaskElement((UserTask) flowElement);
                    template.setCurrentSubProcess((SubProcess) item);
                    return;
                }
            }
        }

        LOGGER.error("没有获取回退任务节点");
    }

}
