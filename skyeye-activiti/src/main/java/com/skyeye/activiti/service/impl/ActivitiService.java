/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.activiti.service.impl;

import com.skyeye.activiti.service.ActivitiModelService;
import com.skyeye.common.util.FileUtil;
import com.skyeye.eve.dao.ActUserProcessInstanceIdDao;
import com.skyeye.jedis.JedisClientService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 流程撤回所需工具类
 * @author 卫志强
 *
 */
@Component
public class ActivitiService {
	
	@Autowired
	private HistoryService historyService;//查询历史信息的类。在一个流程执行完成后，这个对象为我们提供查询历史信息
	
	@Autowired
	private TaskService taskService;//任务服务类。可以从这个类中获取任务的信息
	
	@Autowired
	private RuntimeService runtimeService;
	
	@Autowired
    private RepositoryService repositoryService;
	
	@Autowired
    private ActUserProcessInstanceIdDao actUserProcessInstanceIdDao;
	
	@Autowired
	public JedisClientService jedisClient;

	@Value("${IMAGES_PATH}")
	private String tPath;

	@Autowired
	private ActivitiModelService activitiModelService;
	
	/**
	 * 取回流程
	 * 
	 * @param taskId 当前任务ID
	 * @param activityId 取回节点ID
	 * @throws Exception
	 */
	public void callBackProcess(String taskId, String activityId) throws Exception {
		//1.取回流程
  		if (StringUtils.isEmpty(activityId)) {
  			throw new Exception("目标节点ID为空！");
  		}
  		//查找所有并行任务节点，同时取回
  		List<Task> taskList = findTaskListByKey(findProcessInstanceByTaskId(taskId).getId(), findTaskById(taskId).getTaskDefinitionKey());
  		for (Task bean : taskList) {
  			commitProcess(bean.getId(), null, activityId);
  		}
	}


	/**
	 * 根据流程实例ID和任务key值查询所有同级任务集合
	 * 
	 * @param processInstanceId
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public List<Task> findTaskListByKey(String processInstanceId, String key) throws Exception {
		return taskService.createTaskQuery().processInstanceId(processInstanceId).taskDefinitionKey(key).list();
	}

	/**
	 * 提交流程/流程转向
	 * @param taskId 当前任务ID
	 * @param variables 流程变量
	 * @param activityId 流程转向执行任务节点ID<br>此参数为空，默认为提交操作
	 */
	public void commitProcess(String taskId, Map<String, Object> variables, String activityId) throws Exception {
		if (variables == null) {
			variables = new HashMap<String, Object>();
		}
		//跳转节点为空，默认提交操作
		if (StringUtils.isEmpty(activityId)) {
			taskService.complete(taskId, variables);
		}else{//流程转向操作
			turnTransition(taskId, activityId, variables);
		}
	}

	/**
	* 流程转向操作
	*
	* @param taskId 当前任务ID
	* @param activityId 目标节点任务ID
	* @param variables 流程变量
	* @throws Exception
	*/
	public void turnTransition(String taskId, String activityId, Map<String, Object> variables) throws Exception {
		// 当前节点
		ActivityImpl currActivity = findActivitiImpl(taskId, null);
		// 清空当前流向
		List<PvmTransition> oriPvmTransitionList = clearTransition(currActivity);
		// 创建新流向
		TransitionImpl newTransition = currActivity.createOutgoingTransition();
		// 目标节点
		ActivityImpl pointActivity = findActivitiImpl(taskId, activityId);
		// 设置新流向的目标节点
		newTransition.setDestination(pointActivity);
		// 执行转向任务
		taskService.complete(taskId, variables);
		// 删除目标节点新流入
		pointActivity.getIncomingTransitions().remove(newTransition);
		// 还原以前流向
		restoreTransition(currActivity, oriPvmTransitionList);
	}

	/** 
	* 还原指定活动节点流向
	* 
	* @param activityImpl 活动节点
	* @param oriPvmTransitionList 原有节点流向集合
	*/
	private void restoreTransition(ActivityImpl activityImpl, List<PvmTransition> oriPvmTransitionList) {
		// 清空现有流向
		List<PvmTransition> pvmTransitionList = activityImpl.getOutgoingTransitions();
		pvmTransitionList.clear();
		// 还原以前流向
		for (PvmTransition pvmTransition : oriPvmTransitionList) {
			pvmTransitionList.add(pvmTransition);
		}
	}

	/** 
	* 清空指定活动节点流向
	* 
	* @param activityImpl 活动节点
	* @return 节点流向集合
	*/
	public List<PvmTransition> clearTransition(ActivityImpl activityImpl) throws Exception {
		// 存储当前节点所有流向临时变量
		List<PvmTransition> oriPvmTransitionList = new ArrayList<PvmTransition>();
		// 获取当前节点所有流向，存储到临时变量，然后清空
		List<PvmTransition> pvmTransitionList = activityImpl.getOutgoingTransitions();
		for (PvmTransition pvmTransition : pvmTransitionList) {
			oriPvmTransitionList.add(pvmTransition);
		}
		pvmTransitionList.clear();
		return oriPvmTransitionList;
	}

	/** 
	* 根据任务ID和节点ID获取活动节点 <br>
	* 
	* @param taskId 任务ID
	* @param activityId
	*					活动节点ID <br>
	*					如果为null或""，则默认查询当前活动节点 <br>
	*					如果为"end"，则查询结束节点 <br>
	* 
	* @return
	* @throws Exception
	*/
	public ActivityImpl findActivitiImpl(String taskId, String activityId) throws Exception {
		// 取得流程定义
		ProcessDefinitionEntity processDefinition = findProcessDefinitionEntityByTaskId(taskId);
		// 获取当前活动节点ID
		if (StringUtils.isEmpty(activityId)) {
			activityId = findTaskById(taskId).getTaskDefinitionKey();
		}else{
			HistoricTaskInstance currTask = historyService.createHistoricTaskInstanceQuery().taskId(activityId).singleResult();
			activityId = currTask.getTaskDefinitionKey();
		}
		// 根据流程定义，获取该流程实例的结束节点
		if (activityId.toUpperCase().equals("END")) {
			for (ActivityImpl activityImpl : processDefinition.getActivities()) {
				List<PvmTransition> pvmTransitionList = activityImpl.getOutgoingTransitions();
				if (pvmTransitionList.isEmpty()) {
					return activityImpl;
				}
			}
		}
		// 根据节点ID，获取对应的活动节点
		ActivityImpl activityImpl = ((ProcessDefinitionImpl) processDefinition).findActivity(activityId);
		return activityImpl;
	}


	/** 
	* 根据任务ID获取流程定义
	* 
	* @param taskId 任务ID
	* @return
	* @throws Exception
	*/
	public ProcessDefinitionEntity findProcessDefinitionEntityByTaskId(String taskId) throws Exception {
		// 取得流程定义
		ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
				.getDeployedProcessDefinition(findTaskById(taskId).getProcessDefinitionId());
		if (processDefinition == null) {
			throw new Exception("流程定义未找到!");
		}
		return processDefinition;
	}

	/** 
	* 根据任务ID获取对应的流程实例
	* 
	* @param taskId 任务ID
	* @return
	* @throws Exception
	*/
	public ProcessInstance findProcessInstanceByTaskId(String taskId) throws Exception {
		// 找到流程实例
		ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(findTaskById(taskId).getProcessInstanceId()).singleResult();
		if (processInstance == null) {
			throw new Exception("流程实例未找到!");
		}
		return processInstance;
	}
	
	/**
	 * 根据任务ID获得任务实例
	 * @param taskId 任务ID
	 * @return
	 * @throws Exception
	 */
	public TaskEntity findTaskById(String taskId) throws Exception {
		TaskEntity task = (TaskEntity)taskService.createTaskQuery().taskId(taskId).singleResult();
		if(task == null){
			throw new Exception("任务实例未找到!");
		}
		return task;
	}
	
	/**
	 * @Title: getValueClass 
	 * 
	 * @Description: (根据值获取值类型) 
	 * @param obj
	 * @return
	 * @return Class<?> (这里用一句话描述返回结果说明) 
	 */
	public Class<?> getValueClass(Object obj) {
		if (obj instanceof Boolean) {
			return Boolean.class;
		} else if (obj instanceof Integer) {
			return Integer.class;
		} else if (obj instanceof String) {
			return String.class;
		} else if (obj instanceof Long) {
			return Long.class;
		} else if (obj instanceof Map) {
			return Map.class;
		} else if (obj instanceof Collection) {
			return Collection.class;
		} else if (obj instanceof java.util.List) {
			return java.util.List.class;
		} else {
			return String.class;
		}
	}
	
	/**
	 * 撤销流程
	 * @param map 必须包含processInstanceId和userId
	 * @return
	 * @throws Exception
	 */
	public void editDsFormContentToRevokeByProcessInstanceId(Map<String, Object> map) throws Exception{
		//判断是否删除成功的参数
		String code = "0";
		String message = "";
		//判断是否是该流程创建人
		Map<String, Object> processMation = actUserProcessInstanceIdDao.queryProcessMationByProcessInstanceId(map);
		if(processMation == null){
			code = "-1";
			message = "该流程不属于当前登录人.";
		}else{
			String processInstanceId = map.get("processInstanceId").toString();
			String deleteFilePath = String.format("%supload/activiti/%s.png", tPath, processInstanceId);
			FileUtil.deleteFile(deleteFilePath);
			String userId = map.get("userId").toString();
			Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
			if(task == null){
				code = "-1";
				message = "该流程未启动或者已结束.";
			}else{
				Map<String, Object> variables = taskService.getVariables(task.getId());
				Object o = variables.get("leaveOpinionList");
				boolean edit = true;
				if (o != null) {
					//获取历史审核信息
					List<Map<String, Object>> leaveList = (List<Map<String, Object>>) o;
					for(Map<String, Object> leave : leaveList){
						if(!userId.equals(leave.get("opId").toString())){
							edit = false;//不可编辑
							break;
						}
					}
				}
				if(edit){
					// 可以编辑
					runtimeService.deleteProcessInstance(processInstanceId, "用户撤销");
					// 删除流程历史信息
					historyService.deleteHistoricProcessInstance(processInstanceId);
					// 删除数据库流程信息
					actUserProcessInstanceIdDao.deleteProcessMationByProcessInstanceId(map);
					// 删除流程在redis中的缓存
					activitiModelService.deleteProcessInRedisMation(task.getProcessInstanceId());
				}else{
					code = "-1";
					message = "流程已有审批，不能撤回.";
				}
			}
		}
		map.put("code", code);
		map.put("message", message);
	}
	
}
