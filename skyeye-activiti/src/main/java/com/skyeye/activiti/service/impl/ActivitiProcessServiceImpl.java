/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.activiti.service.impl;

import com.skyeye.activiti.service.ActivitiModelService;
import com.skyeye.activiti.service.ActivitiProcessService;
import com.skyeye.activiti.service.ActivitiTaskService;
import com.skyeye.common.constans.ActivitiConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.ActGroupUserDao;
import com.skyeye.eve.dao.ActModelDao;
import com.skyeye.eve.service.SysEveUserService;
import net.sf.json.JSONObject;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.javax.el.ExpressionFactory;
import org.activiti.engine.impl.javax.el.ValueExpression;
import org.activiti.engine.impl.juel.ExpressionFactoryImpl;
import org.activiti.engine.impl.juel.SimpleContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.text.ParseException;
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

    /**
     * 查询历史信息的类。在一个流程执行完成后，这个对象为我们提供查询历史信息
     */
    @Autowired
    private HistoryService historyService;

    @Autowired
    private RuntimeService runtimeService;

    /**
     * 任务服务类。可以从这个类中获取任务的信息
     */
    @Autowired
    private TaskService taskService;

    @Autowired
    private ActivitiService activitiService;

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
     * @Description: 流程撤回
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
        //根据流程id查询代办任务中流程信息
        Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
        if(task == null){
            outputObject.setreturnMessage("流程未启动或已执行完成，无法撤回");
            return;
        }
        //处理任务
        Map<String, Object> variables = taskService.getVariables(task.getId());
        //获取审批信息
        List<Map<String, Object>> leaveList = new ArrayList<>();
        Object o = variables.get("leaveOpinionList");
        if (o != null) {
            leaveList = (List<Map<String, Object>>) o;
        }
        //根据时间倒叙排序
        Collections.sort(leaveList, new Comparator<Map<String, Object>>() {
            public int compare(Map<String, Object> p1, Map<String, Object> p2) {
                String a = p1.get("createTime").toString();
                String b = p2.get("createTime").toString();
                try {
                    if(DateUtil.compare(a, b)){
                        return 1;
                    }
                } catch (ParseException e) {
                }
                return -1;
            }
        });
        //如果最后一个审批人不是当前登录人
        if(!userId.equals(leaveList.get(0).get("opId").toString())){
            outputObject.setreturnMessage("该任务非当前用户提交，无法撤回");
            return;
        }
        //获取历史任务
        HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().taskId(hisTaskId).singleResult();
        //取回流程接点 当前任务id 取回任务id
        activitiService.callBackProcess(task.getId(), historicTaskInstance.getId());
        //删除历史流程走向记录
        historyService.deleteHistoricTaskInstance(historicTaskInstance.getId());
        historyService.deleteHistoricTaskInstance(task.getId());

        // 删除指定流程在redis中的缓存信息
        activitiModelService.deleteProcessInRedisMation(processInstanceId);

        //审批信息
        Map<String, Object> leaveOpinion = new HashMap<>();
        leaveOpinion.put("opId", user.get("id"));//审批人id
        leaveOpinion.put("title", "撤回");//操作节点
        leaveOpinion.put("opName", user.get("userName"));//审批人姓名
        leaveOpinion.put("opinion", map.get("opinion"));//审批意见
        leaveOpinion.put("createTime", DateUtil.getTimeAndToString());//审批时间
        leaveOpinion.put("flag", true);
        leaveOpinion.put("taskId", hisTaskId);//任务id
        leaveList.add(leaveOpinion);
        runtimeService.setVariable(processInstanceId, "leaveOpinionList", leaveList);
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
        TaskDefinition taskDefinition = getNextTaskInfo(processInstanceId, map);
        if(taskDefinition != null){
            // 1.获取下个节点的所有可选审批人
            List<Map<String, Object>> user = new ArrayList<>();
            this.getNextTaskApprove(taskDefinition, user);
            outputObject.setBeans(user);
            // 2.获取节点信息
            Map<String, Object> nodeMation = new HashMap<>();
            nodeMation.put("nodeName", taskDefinition.getNameExpression().getExpressionText());
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

    private void getNextTaskApprove(TaskDefinition taskDefinition, List<Map<String, Object>> user) throws Exception {
        // 1.候选组人员获取
        Set<Expression> groupIdSet = taskDefinition.getCandidateGroupIdExpressions();
        List<String> groupIds = new ArrayList<>(groupIdSet.stream().map(Expression::getExpressionText).collect(Collectors.toList()));
        if(CollectionUtils.isNotEmpty(groupIds)){
            List<Map<String, Object>> groupUsers = actGroupUserDao.queryActGroupUserByGroupId(groupIds);
            groupUsers.forEach(bean -> {
                user.add(sysEveUserService.getUserMationByUserId(bean.get("userId").toString()));
            });
        }
        // 2.候选人员获取
        Set<Expression> userIdSet = taskDefinition.getCandidateUserIdExpressions();
        List<String> userIds = new ArrayList<>(userIdSet.stream().map(Expression::getExpressionText).collect(Collectors.toList()));
        if(CollectionUtils.isNotEmpty(userIds)){
            userIds.forEach(userId -> {
                user.add(sysEveUserService.getUserMationByUserId(userId));
            });
        }
        // 3.代理人获取
        Expression userIdExpression = taskDefinition.getAssigneeExpression();
        if(userIdExpression != null){
            user.add(sysEveUserService.getUserMationByUserId(userIdExpression.getExpressionText()));
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
    public TaskDefinition getNextTaskInfo(String processInstanceId, Map<String, Object> map) throws Exception {
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
                // 获取下一个节点信息
                TaskDefinition task = nextTaskDefinition(activityImpl, activityImpl.getId(), processInstanceId, map);
                return task;
            }
        }
        return null;
    }

    /**
     * 下一个任务节点信息,
     *
     * 如果下一个节点为用户任务则直接返回,
     *
     * 如果下一个节点为排他网关, 获取排他网关Id信息, 根据排他网关Id信息和execution获取流程实例排他网关Id为key的变量值,
     * 根据变量值分别执行排他网关后线路中的el表达式, 并找到el表达式通过的线路后的用户任务
     *
     * @param activityImpl 流程节点信息
     * @param activityId 当前流程节点Id信息
     * @param processInstanceId 流程实例Id信息
     * @param map 校验参数
     * @return
     */
    private TaskDefinition nextTaskDefinition(ActivityImpl activityImpl, String activityId, String processInstanceId, Map<String, Object> map) {
        // 如果遍历节点为用户任务并且节点不是当前节点信息
        if (ActivitiConstants.USER_TASK.equals(activityImpl.getProperty("type")) && !activityId.equals(activityImpl.getId())) {
            // 获取该节点下一个节点信息
            TaskDefinition taskDefinition = ((UserTaskActivityBehavior) activityImpl.getActivityBehavior()).getTaskDefinition();
            return taskDefinition;
        } else if (ActivitiConstants.EXCLUSIVE_GATEWAY.equals(activityImpl.getProperty("type"))) {// 当前节点为exclusiveGateway
            List<PvmTransition> outTransitions = activityImpl.getOutgoingTransitions();
            // 如果排他网关只有一条线路信息
            if (outTransitions.size() == 1) {
                return nextTaskDefinition((ActivityImpl) outTransitions.get(0).getDestination(), activityId, processInstanceId, map);
            } else if (outTransitions.size() > 1) { // 如果排他网关有多条线路信息
                for (PvmTransition tr1 : outTransitions) {
                    Object s = tr1.getProperty("conditionText"); // 获取排他网关线路判断条件信息
                    // 判断el表达式是否成立
                    if (isCondition(activityImpl.getId(), StringUtils.trim(s.toString()), map)) {
                        return nextTaskDefinition((ActivityImpl) tr1.getDestination(), activityId, processInstanceId, map);
                    }
                }
            }
        } else {
            // 获取节点所有流向线路信息
            List<PvmTransition> outTransitions = activityImpl.getOutgoingTransitions();
            for (PvmTransition tr : outTransitions) {
                PvmActivity ac = tr.getDestination(); // 获取线路的终点节点
                // 如果流向线路为排他网关
                if (ActivitiConstants.EXCLUSIVE_GATEWAY.equals(ac.getProperty("type"))) {
                    List<PvmTransition> outTransitionsTemp = ac.getOutgoingTransitions();
                    // 如果排他网关只有一条线路信息
                    if (outTransitionsTemp.size() == 1) {
                        return nextTaskDefinition((ActivityImpl) outTransitionsTemp.get(0).getDestination(), activityId, processInstanceId, map);
                    } else if (outTransitionsTemp.size() > 1) { // 如果排他网关有多条线路信息
                        for (PvmTransition tr1 : outTransitionsTemp) {
                            Object s = tr1.getProperty("conditionText"); // 获取排他网关线路判断条件信息
                            // 判断el表达式是否成立
                            if (isCondition(ac.getId(), StringUtils.trim(s.toString()), map)) {
                                return nextTaskDefinition((ActivityImpl) tr1.getDestination(), activityId, processInstanceId, map);
                            }
                        }
                    }
                } else if (ActivitiConstants.USER_TASK.equals(ac.getProperty("type"))) {
                    return ((UserTaskActivityBehavior) ((ActivityImpl) ac).getActivityBehavior()).getTaskDefinition();
                } else {
                }
            }
            return null;
        }
        return null;
    }

    /**
     * 根据key和value判断el表达式是否通过信息
     *
     * @param key el表达式key信息
     * @param el el表达式信息
     * @param map el表达式传入值信息
     * @return
     */
    public boolean isCondition(String key, String el, Map<String, Object> map) {
        ExpressionFactory factory = new ExpressionFactoryImpl();
        SimpleContext context = new SimpleContext();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            context.setVariable(entry.getKey(), factory.createValueExpression(entry.getValue(), activitiService.getValueClass(entry.getValue())));
        }
        ValueExpression e = factory.createValueExpression(context, el, boolean.class);
        return (Boolean) e.getValue(context);
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
