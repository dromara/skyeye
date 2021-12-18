/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.activiti.service.impl;

import com.skyeye.activiti.cmd.rollback.RollbackCmd;
import com.skyeye.activiti.service.ActivitiModelService;
import com.skyeye.activiti.service.ActivitiProcessService;
import com.skyeye.activiti.service.ActivitiTaskService;
import com.skyeye.common.constans.ActivitiConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.ActGroupUserDao;
import com.skyeye.eve.dao.ActModelDao;
import com.skyeye.eve.service.SysEveUserService;
import net.sf.json.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.flowable.bpmn.model.*;
import org.flowable.engine.*;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.util.condition.ConditionUtil;
import org.flowable.engine.repository.Model;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: ActivitiProcessServiceImpl
 * @Description: 工作流流程相关操作
 * @author: skyeye云系列--卫志强
 * @date: 2021/12/2 21:29
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class ActivitiProcessServiceImpl implements ActivitiProcessService {

    @Autowired
    private RuntimeService runtimeService;

    /**
     * 任务服务类。可以从这个类中获取任务的信息
     */
    @Autowired
    private TaskService taskService;

    @Autowired
    private ActivitiModelService activitiModelService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private SysEveUserService sysEveUserService;

    @Autowired
    private ActGroupUserDao actGroupUserDao;

    @Autowired
    private ActivitiTaskService activitiTaskService;

    @Autowired
    private ActModelDao actModelDao;

    @Autowired
    private ManagementService managementService;

    /**
     *
     * @Title: updateProcessToHangUp
     * @Description: 流程挂起
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    public void updateProcessToHangUp(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String processInstanceId = map.get("processInstanceId").toString();
        //根据一个流程实例的id挂起该流程实例
        runtimeService.suspendProcessInstanceById(processInstanceId);
        // 删除指定流程在redis中的缓存信息
        activitiModelService.deleteProcessInRedisMation(processInstanceId);
    }

    /**
     *
     * @Title: updateProcessToActivation
     * @Description: 流程激活
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    public void updateProcessToActivation(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String processInstanceId = map.get("processInstanceId").toString();
        // 根据一个流程实例的id激活该流程实例
        runtimeService.activateProcessInstanceById(processInstanceId);
        // 删除指定流程在redis中的缓存信息
        activitiModelService.deleteProcessInRedisMation(processInstanceId);
    }

    /**
     *
     * @Title: editProcessInstanceWithDraw
     * @Description: 流程撤回(撤回审批过的流程)
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    public void editProcessInstanceWithDraw(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> user = inputObject.getLogParams();
        String userId = user.get("id").toString();
        String processInstanceId = map.get("processInstanceId").toString();
        String hisTaskId = map.get("hisTaskId").toString();
        // 根据流程id查询代办任务中流程信息
        Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
        if(task == null){
            outputObject.setreturnMessage("流程未启动或已执行完成，无法撤回");
            return;
        }
        // 撤回
        managementService.executeCommand(new RollbackCmd(hisTaskId, userId));
        activitiModelService.queryProHighLighted(processInstanceId);//绘制图像
    }

    /**
     *
     * @Title: editProcessInstancePicToRefresh
     * @Description: 刷新流程图
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    public void editProcessInstancePicToRefresh(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        activitiModelService.queryProHighLighted(map.get("processInstanceId").toString());//绘制图像
    }

    /**
     * 获取流程下一个节点的审批人
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void nextPrcessApprover(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        String processInstanceId = params.get("processInstanceId").toString();
        String taskId = params.get("taskId").toString();
        // 获取表单数据用于排他网关的参数校验
        Map<String, Object> map = getFormVariable(taskId, params);
        UserTask userTask = getNextTaskInfo(processInstanceId, map);
        if(userTask != null){
            // 1.获取下个节点的所有可选审批人
            List<Map<String, Object>> user = new ArrayList<>();
            this.getNextTaskApprove(userTask, user);
            outputObject.setBeans(user);
            // 2.获取节点信息
            Map<String, Object> nodeMation = new HashMap<>();
            nodeMation.put("nodeName", userTask.getName());
            nodeMation.put("nodeType", ActivitiConstants.USER_TASK);
            outputObject.setBean(nodeMation);
        }
    }

    /**
     * 获取表单数据用于排他网关的参数校验
     *
     * @param taskId 当前任务节点的任务id
     * @param inputParams 入参
     * @return
     */
    private Map<String, Object> getFormVariable(String taskId, Map<String, Object> inputParams){
        Map<String, Object> variable = new HashMap<>();
        Map<String, Object> params = activitiTaskService.getCurrentTaskParamsByTaskId(taskId);
        for (String key : params.keySet()) {
            if(params.get(key) == null){
                continue;
            }
            String str = params.get(key).toString();
            if(ToolUtil.isJson(str)){
                Map<String, Object> formItemMation = JSONObject.fromObject(str);
                variable.put(key, formItemMation.containsKey("value") ? formItemMation.get("value") : StringUtils.EMPTY);
            }
        }
        // 审批结果
        if(!ToolUtil.isBlank(inputParams.get("flag").toString())){
            variable.put("flag", inputParams.get("flag"));
        }
        return variable;
    }

    private void getNextTaskApprove(UserTask userTask, List<Map<String, Object>> user) throws Exception {
        // 1.候选组人员获取
        List<String> groupIds = userTask.getCandidateGroups();
        if(CollectionUtils.isNotEmpty(groupIds)){
            List<Map<String, Object>> groupUsers = actGroupUserDao.queryActGroupUserByGroupId(groupIds);
            groupUsers.forEach(bean -> {
                user.add(sysEveUserService.getUserMationByUserId(bean.get("userId").toString()));
            });
        }
        // 2.候选人员获取
        List<String> userIds = userTask.getCandidateUsers();
        if(CollectionUtils.isNotEmpty(userIds)){
            userIds.forEach(userId -> {
                user.add(sysEveUserService.getUserMationByUserId(userId));
            });
        }
        // 3.代理人获取
        String assignee = userTask.getAssignee();
        if(assignee != null){
            user.add(sysEveUserService.getUserMationByUserId(assignee));
        }
    }

    /**
     * 获取下一个用户任务信息
     *
     * @param processInstanceId 流程Id信息
     * @param map 表单数据
     * @return 下一个用户任务用户组信息
     * @throws Exception
     */
    public UserTask getNextTaskInfo(String processInstanceId, Map<String, Object> map) throws Exception {
        // 获取流程发布Id信息
        String definitionId = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult().getProcessDefinitionId();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(definitionId);
        ExecutionEntity execution = (ExecutionEntity) runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        execution.setVariablesLocal(map);
        // 当前流程节点信息
        FlowElement currentNode = bpmnModel.getFlowElement(execution.getActivityId());
        // 获取流程曲线走向
        List<SequenceFlow> outgoingFlows = ((FlowNode) currentNode).getOutgoingFlows();
        if (CollectionUtils.isNotEmpty(outgoingFlows)) {
            return this.findNextUserTaskNode(outgoingFlows, execution);
        }
        return null;
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
    private UserTask findNextUserTaskNode(List<SequenceFlow> outgoingFlows, ExecutionEntity execution) {
        for (SequenceFlow outgoingFlow : outgoingFlows) {
            if (ConditionUtil.hasTrueCondition(outgoingFlow, execution)) {
                if (outgoingFlow.getTargetFlowElement() instanceof ExclusiveGateway) {
                    // 只有排他网关才继续
                    ExclusiveGateway exclusiveGateway = (ExclusiveGateway) outgoingFlow.getTargetFlowElement();
                    return findNextUserTaskNode(exclusiveGateway.getOutgoingFlows(), execution);
                } else if (outgoingFlow.getTargetFlowElement() instanceof UserTask) {
                    UserTask nextUserTask = (UserTask) outgoingFlow.getTargetFlowElement();
                    // 找到第一个符合条件的userTask就跳出循环
                    return nextUserTask;
                }
            }
        }
        return null;
    }

    /**
     * 根据processDefinitionKey获取流程下一个用户节点的审批人
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void nextPrcessApproverByProcessDefinitionKey(InputObject inputObject, OutputObject outputObject) throws Exception {
        String pageUrl = inputObject.getParams().get("pageUrl").toString();
        Map<String, Object> actModel = actModelDao.queryActModelMationByPageUrl(pageUrl);
        if(actModel == null || actModel.isEmpty()){
            outputObject.setreturnMessage("流程不存在或未启动.");
            return;
        }
        String processDefinitionKey = actModel.get("actKey").toString();
        List<Map<String, Object>> user = this.nextPrcessApproverByProcessDefinitionKey(processDefinitionKey);
        outputObject.setBeans(user);
    }

    public List<Map<String, Object>> nextPrcessApproverByProcessDefinitionKey(String processDefinitionKey) throws Exception {
        List<Map<String, Object>> user = new ArrayList<>();
        List<ProcessDefinition> processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey(processDefinitionKey).list();
        if(processDefinition != null){
            List<Model> beans = repositoryService.createModelQuery().latestVersion().orderByLastUpdateTime().desc().list();
            List<String> deploymentIds = beans.stream().map(p -> p.getDeploymentId()).collect(Collectors.toList());
            processDefinition = processDefinition.stream().filter(bean -> deploymentIds.contains(bean.getDeploymentId())).collect(Collectors.toList());
            if(processDefinition != null && !processDefinition.isEmpty()){
                user = getUserTaskList(processDefinition.get(0));
            }
        }
        return user;
    }

    public List<Map<String, Object>> getUserTaskList(ProcessDefinition processDefinition) throws Exception {
        String deploymentId = processDefinition.getDeploymentId();
        // 实现读写bpmn文件信息
        InputStream bpmnIs = repositoryService.getResourceAsStream(deploymentId, processDefinition.getResourceName());
        SAXReader saxReader = new SAXReader();
        // 获取流程图文件中的userTask节点的所有属性
        Document document = saxReader.read(bpmnIs);
        Element rootElement = document.getRootElement();
        Element process = rootElement.element("process");
        List<Element> userTaskList = process.elements("userTask");

        List<Map<String, Object>> list = new ArrayList<>();
        // 获取第一个用户任务节点
        if(userTaskList != null && !userTaskList.isEmpty()){
            Element element = userTaskList.get(0);
            String assignee = element.attributeValue("assignee");
            String candidateUsers = element.attributeValue("candidateUsers");
            String candidateGroups = element.attributeValue("candidateGroups");

            if(!ToolUtil.isBlank(candidateGroups)){
                List<Map<String, Object>> groupUsers = actGroupUserDao.queryActGroupUserByGroupId(Arrays.asList(candidateGroups.split(",")));
                groupUsers.forEach(bean -> {
                    list.add(sysEveUserService.getUserMationByUserId(bean.get("userId").toString()));
                });
            }
            // 2.候选人员获取
            if(!ToolUtil.isBlank(candidateUsers)){
                Arrays.asList(candidateUsers.split(",")).forEach(userId -> {
                    list.add(sysEveUserService.getUserMationByUserId(userId));
                });
            }
            // 3.代理人获取
            if(!ToolUtil.isBlank(assignee)){
                list.add(sysEveUserService.getUserMationByUserId(assignee));
            }
        }
        return list;
    }

}
