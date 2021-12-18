package com.skyeye.activiti.cmd.rollback;

import lombok.Data;
import org.flowable.bpmn.model.*;
import org.flowable.task.api.history.HistoricTaskInstance;

import java.util.*;

/**
 * @ClassName: RollbackParamsTemplate
 * @Author: huangrenhao
 * @Description:
 * @CreateTime： 2020/3/23 0023 下午 3:52
 * @Version：
 **/
@Data
public class RollbackParamsTemplate {

    /**
     *  回滚任务
     */
    private HistoricTaskInstance hisTask;

    /**
     *  当前任务节点
     */
    private UserTask currentTaskElement;

    /**
     *  当前节点到下一任务节点间的连线（不包含当前任务节点）
     */
    private Set<String> nextFlowIdList = new HashSet<>();

    /**
     *  当前任务节点到 下一节点 之间线条
     */
    private Map<String, SequenceFlow> outGoingMap = new HashMap<>();

    /**
     *  下一任务节点 集合
     */
    private List<UserTask> nextUserTaskList = new ArrayList<>();

    /**
     *  到下一任务节点 之间的网关集合
     */
    private Map<String, Gateway> gatewayMap = new HashMap<>();

    /**
     *  下一节点是否为 嵌入式子流程
     */
    private Map<String, SubProcess> subProcessMap = new HashMap<>();

    /**
     *  下一节点是否为 调用子流程
     */
    private Map<String, CallActivity> callActivityMap = new HashMap<>();

    /**
     *  当前 嵌入子流程
     */
    private SubProcess currentSubProcess;
}
