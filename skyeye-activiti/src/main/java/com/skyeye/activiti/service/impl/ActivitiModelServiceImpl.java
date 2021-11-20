/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.activiti.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.activiti.mapper.ActivityMapper;
import com.skyeye.activiti.service.ActAssigneeService;
import com.skyeye.activiti.service.ActivitiModelService;
import com.skyeye.common.constans.ActivitiConstants;
import com.skyeye.common.constans.Constants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.*;
import com.skyeye.eve.service.DsFormPageService;
import com.skyeye.exception.CustomException;
import com.skyeye.jedis.JedisClientService;
import com.skyeye.annotation.transaction.ActivitiAndBaseTransaction;
import net.sf.json.JSONObject;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.javax.el.ExpressionFactory;
import org.activiti.engine.impl.javax.el.ValueExpression;
import org.activiti.engine.impl.juel.ExpressionFactoryImpl;
import org.activiti.engine.impl.juel.SimpleContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.GroupEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.UserEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ModelQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.activiti.image.ProcessDiagramGenerator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @ClassName: ActivitiModelServiceImpl
 * @Description: 工作流管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/17 20:53
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class ActivitiModelServiceImpl implements ActivitiModelService{

	private static final Logger LOGGER = LoggerFactory.getLogger(ActivitiModelServiceImpl.class);
	
	@Autowired
    private ProcessEngine processEngine;
	
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private RepositoryService repositoryService;

	/**
	 * 查询历史信息的类。在一个流程执行完成后，这个对象为我们提供查询历史信息
	 */
	@Autowired
	private HistoryService historyService;

	/**
	 * 任务服务类。可以从这个类中获取任务的信息
	 */
	@Autowired
	private TaskService taskService;
    
    @Autowired
	private RuntimeService runtimeService;
    
    @Autowired
    private IdentityService identityService;
    
    @Autowired
    private SysEveUserDao sysEveUserDao;
    
    @Autowired
	private ProcessEngineConfigurationImpl processEngineConfiguration;
    
    @Autowired
    private ActAssigneeService actAssigneeService;
    
    @Autowired
    private ActUserProcessInstanceIdDao actUserProcessInstanceIdDao;

    @Autowired
	private ActModleTypeDao actModleTypeDao;
    
    @Autowired
    private DsFormPageDataDao dsFormPageDataDao;

    @Autowired
	private DsFormPageSequenceDao dsFormPageSequenceDao;
    
    @Autowired
	public JedisClientService jedisClient;
    
    @Value("${IMAGES_PATH}")
	private String tPath;
    
    @Autowired
    private ActivitiService activitiService;

    @Autowired
	private ActivityMapper activityMapper;

	@Autowired
	private ActModelDao actModelDao;

	@Autowired
	private DsFormPageService dsFormPageService;

	/**
	 * form表单数据存储在task的varables的key
	 */
	private static final String PROCESSINSTANCEID_TASK_VARABLES = "baseTask";
    
    /**
	     * @Title: insertNewActivitiModel
	     * @Description: 新建一个空模型
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@ActivitiAndBaseTransaction(value = {"activitiTransactionManager", "transactionManager"})
	public void insertNewActivitiModel(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		RepositoryService repositoryService = processEngine.getRepositoryService();
        //初始化一个空模型
        Model model = repositoryService.newModel();
        //设置一些默认信息
        String name = "new-process";
        String description = "";
        int revision = 1;
        String key = ToolUtil.getSurFaceId();
        ObjectNode modelNode = objectMapper.createObjectNode();
        modelNode.put(ModelDataJsonConstants.MODEL_NAME, name);
        modelNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description);
        modelNode.put(ModelDataJsonConstants.MODEL_REVISION, revision);
 
        model.setName(name);
        model.setKey(key);
        model.setMetaInfo(modelNode.toString());
 
        ObjectNode editorNode = objectMapper.createObjectNode();
        editorNode.put("id", "canvas");
        editorNode.put("resourceId", "canvas");
        ObjectNode properties = objectMapper.createObjectNode();
		properties.put("process_author", "skyeye");
		editorNode.put("properties", properties);
        ObjectNode stencilSetNode = objectMapper.createObjectNode();
        stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
        editorNode.put("stencilset", stencilSetNode);
        
        repositoryService.saveModel(model);
        repositoryService.addModelEditorSource(model.getId(), editorNode.toString().getBytes("utf-8"));
        
        map.put("id", model.getId());
        outputObject.setBean(map);
	}
	
	/**
	     * @Title: queryActivitiModelList
	     * @Description: 获取所有模型
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryActivitiModelList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Integer limit = Integer.parseInt(map.get("limit").toString());
		Integer page = Integer.parseInt(map.get("page").toString());
		List<Model> beans = this.queryActivitiModelListByParams(map)
				.listPage(limit * (page - 1), limit);
		long count = repositoryService.createModelQuery().count();
		List<Map<String, Object>> rows = new ArrayList<>();
		for(Model model : beans){
			Map<String, Object> row = ToolUtil.javaBean2Map(model);
			if(!ToolUtil.isBlank(model.getDeploymentId())){
				ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(model.getDeploymentId()).singleResult();
				if(processDefinition != null){
					row.put("processDefinitionVersion", processDefinition.getVersion());
					row.put("processKey", processDefinition.getKey());
				}
			}
			rows.add(row);
		}
		outputObject.setBeans(rows);
		outputObject.settotal(count);
	}

	private ModelQuery queryActivitiModelListByParams(Map<String, Object> map){
		ModelQuery modelQuery = repositoryService.createModelQuery().latestVersion().orderByCreateTime();
		String modelName = map.get("modelName").toString();
		if(!ToolUtil.isBlank(modelName)){
            modelQuery = modelQuery.modelNameLike(
                String.format(Locale.ROOT, "%s%s%s", Constants.PERCENT_SIGN, modelName, Constants.PERCENT_SIGN));
		}
		return modelQuery.desc();
	}

	/**
	     * @Title: editActivitiModelToDeploy
	     * @Description: 发布模型为流程定义
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void editActivitiModelToDeploy(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String modelId = map.get("modelId").toString();
		// 获取模型
        Model modelData = repositoryService.getModel(modelId);
        byte[] bytes = repositoryService.getModelEditorSource(modelData.getId());
        if (bytes == null) {
        	outputObject.setreturnMessage("模型数据为空，请先设计流程并成功保存，再进行发布。");
        	return;
        }
		JsonNode modelNode = new ObjectMapper().readTree(bytes);
		LOGGER.info("modelNode = {}", modelNode.toString());
		BpmnModel model = new BpmnJsonConverter().convertToBpmnModel(modelNode);
		if (model.getProcesses().size() == 0) {
			outputObject.setreturnMessage("数据模型不符要求，请至少设计一条主线流程。");
			return;
		}
		byte[] bpmnBytes = new BpmnXMLConverter().convertToXML(model);

		// 发布流程
		String processName = modelData.getName() + ".bpmn20.xml";
		Deployment deployment = repositoryService.createDeployment()
				.name(modelData.getName())
				.addString(processName, new String(bpmnBytes, "UTF-8"))
				.deploy();

		// 发布版本+1
		Integer version = modelData.getVersion();
		modelData.setVersion(StrUtil.isBlank(modelData.getDeploymentId()) ? version : version + 1);
		modelData.setDeploymentId(deployment.getId());
		repositoryService.saveModel(modelData);

		ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();
		if (!StrUtil.isBlank(processName)) {
			activityMapper.updateProcessDefinitionName(processName, processDefinition.getId());
			LOGGER.info("流程模型【{}】没有配置流程名称，默认使用流程模型名称作为流程名称", processName);
		}
		LOGGER.info("流程【{}】成功发布", processName);
	}

	/**
	     * @Title: editActivitiModelToStartProcess
	     * @Description: 启动流程
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@ActivitiAndBaseTransaction(value = {"activitiTransactionManager", "transactionManager"})
	public void editActivitiModelToStartProcess(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		// 流程定义的key
		String keyName = map.get("keyName").toString();
		/**
		 * jsonStr参数介绍
		 * name: "",----标题
         * value: "",----值
         * orderBy: 1,----排序，值越大越往后
         * showType: 1----展示类型：1.文本展示；2.附件展示；3.富文本展示；4.图片上传；5.表格展示
         * proportion: 6----展示比例，前端界面百分比分为12份
         * editableNodeId：可编辑节点Id
         * editableNodeName：可编辑节点名称
         * formItemType: 原始表单类型
         * formItem：表单内容
		 */
		String str = map.get("jsonStr").toString();
		if(ToolUtil.isBlank(str)){
			outputObject.setreturnMessage("数据不能为空.");
		}else{
			try{
				// 流程存在的时候才能执行
				if(judgeProcessKeyIsLive(keyName)){
					// 启动流程
					startProcess(str, user, keyName, "");
					outputObject.setBean(map);
				}else{
					LOGGER.info("this processDefinitionKey's [{}] process is non-exits", keyName);
					outputObject.setreturnMessage("任务发起失败,不存在该流程模型.");
				}
			}catch(Exception e){
				LOGGER.warn("start Process failed", e);
				outputObject.setreturnMessage("任务发起失败.");
			}
		}
	}

	/**
	 * 启动流程
	 *
	 * @param str 前端传来的数据json串
	 * @param user 用户信息
	 * @param keyName 流程定义的key
	 * @param dataId 数据id
	 * @return
	 * @throws Exception
	 */
	private String startProcess(String str, Map<String, Object> user, String keyName, String dataId) throws Exception {
		Map<String, Object> baseTask = JSONUtil.toBean(str, null);
		String userId = user.get("id").toString();
		baseTask.put("createId", userId);//创建人id
		baseTask.put("createName", user.get("userName"));//创建人姓名
		// 业务对象
		Map<String, Object> varables = new HashMap<>();
		// form表单数据
		varables.put(PROCESSINSTANCEID_TASK_VARABLES, baseTask);
		// 启动流程---流程图id，业务表id
		ProcessInstance process = runtimeService.startProcessInstanceByKey(keyName, varables);
		String processInstanceId = process.getProcessInstanceId();
		this.queryProHighLighted(processInstanceId);
		// 存储用户启动的流程
		saveActUserProInsId(processInstanceId, userId, keyName, dataId);

		// 完成第一个任务
		Task task = taskService.createTaskQuery().taskAssignee(userId).processInstanceId(processInstanceId).active().singleResult();
		if (task != null){
			// 获取指定任务节点的审批信息
			List<Map<String, Object>> leaveList = getUpLeaveList(userId, user.get("userName").toString(), "", true, task);
			Map<String, Object> bean = new HashMap<>();
			bean.put("needfinish", 1);//通过下一个节点
			bean.put("leaveOpinionList", leaveList);
			bean.put("flag", "1");//校验参数
			taskService.complete(task.getId(), bean);
			this.queryProHighLighted(processInstanceId);
			// 删除指定流程在redis中的缓存信息
			deleteProcessInRedisMation(task.getProcessInstanceId());
		}
		LOGGER.info("start process success, processInstanceId is: {}", processInstanceId);
		return processInstanceId;
	}

	/**
	 * 判断该key的流程是否还存在
	 *
	 * @param processDefinitionKey processDefinitionKey
	 * @return
	 */
	private boolean judgeProcessKeyIsLive(String processDefinitionKey){
		List<ProcessDefinition> processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey(processDefinitionKey).list();
		if(processDefinition != null){
			List<String> deploymentIds = processDefinition.stream().map(p -> p.getDeploymentId()).collect(Collectors.toList());
			List<Model> beans = repositoryService.createModelQuery().latestVersion().orderByLastUpdateTime().desc().list();
			beans = beans.stream().filter(bean -> deploymentIds.contains(bean.getDeploymentId())).collect(Collectors.toList());
			if(beans != null && !beans.isEmpty()){
				return true;
			}
		}
		return false;
	}

	/**
	 * 存储用户启动的流程
	 *
	 * @param processInstanceId 流程id
	 * @param createId 创建人
	 * @param keyName 流程定义的key
	 * @param dataId 当流程需要关联数据库表数据时，存放的那条数据的id
	 * @throws Exception
	 */
	private void saveActUserProInsId(String processInstanceId, String createId, String keyName, String dataId) throws Exception {
		Map<String, Object> actUserProInsId = new HashMap<>();
		actUserProInsId.put("id", ToolUtil.getSurFaceId());
		actUserProInsId.put("processInstanceId", processInstanceId);
		actUserProInsId.put("keyName", keyName);
		// 当流程需要关联数据库表数据时，存放的那条数据的id
		actUserProInsId.put("dataId", dataId);
		actUserProInsId.put("createId", createId);
		actUserProInsId.put("createTime", DateUtil.getTimeAndToString());
		actUserProcessInstanceIdDao.insertActUserProInsIdMation(actUserProInsId);
	}

	/**
	 * 删除指定流程在redis中的缓存信息
	 *
	 * @param processInstanceId 流程id
	 */
	private void deleteProcessInRedisMation(String processInstanceId){
		// 删除流程在redis中的待办存储
		jedisClient.delKeys(Constants.PROJECT_ACT_PROCESS_INSTANCE_USERAGENCYTASKS_ITEM + processInstanceId + "*");
		// 删除流程在redis中的已办存储
		jedisClient.delKeys(Constants.PROJECT_ACT_PROCESS_HIS_INSTANCE_ITEM + processInstanceId + "*");
		// 删除流程在redis中的我的请求存储
		jedisClient.delKeys(Constants.PROJECT_ACT_PROCESS_INSTANCE_ITEM + processInstanceId + "*");
	}

	/**
	 * 获取指定任务节点的审批信息
	 *
	 * @param approvedId 审批人id
	 * @param approvedName 审批人名字
	 * @param opinion 审批意见
	 * @param flag 该节点是否审批通过，true:通过，false:不通过
	 * @param task 任务
	 * @return
	 */
	private List<Map<String, Object>> getUpLeaveList(String approvedId, String approvedName, String opinion, boolean flag, Task task){
		Map<String, Object> leaveOpinion = new HashMap<>();
		// 审批人id
		leaveOpinion.put("opId", approvedId);
		// 审批人姓名
		leaveOpinion.put("opName", approvedName);
		// 操作节点
		leaveOpinion.put("title", task.getName());
		// 审批意见
		leaveOpinion.put("opinion", opinion);
		// 审批时间
		leaveOpinion.put("createTime", DateUtil.getTimeAndToString());
		leaveOpinion.put("flag", flag);
		// 任务id
		leaveOpinion.put("taskId", task.getId());
		// 获取该任务所有的流程变量
		Map<String, Object> variables = taskService.getVariables(task.getId());
		List<Map<String, Object>> leaveList = new ArrayList<>();
		Object o = variables.get("leaveOpinionList");
		if (o != null) {
			leaveList = (List<Map<String, Object>>) o;
		}
		leaveList.add(leaveOpinion);
		return leaveList;
	}
	
	/**
	 * 启动流程--其他方法调用
	 *
	 * @param map
	 * @param user 用户信息
	 * @param id 数据id
	 * @throws Exception
	 */
	@Override
	public void editActivitiModelToStartProcessByMap(Map<String, Object> map, Map<String, Object> user, String id) throws Exception {
		// 流程定义的key
		String keyName = map.get("keyName").toString();
		map.put("code", "-1");
		String message = "";
		/**
		 * jsonStr参数介绍
		 * name: "",----标题
         * value: "",----值
         * orderBy: 1,----排序，值越大越往后
         * showType: 1----展示类型：1.文本展示；2.附件展示；3.富文本展示；4.图片上传；5.表格展示
         * proportion: 6----展示比例，前端界面百分比分为12份
         * editableNodeId：可编辑节点Id
         * editableNodeName：可编辑节点名称
         * formItemType: 原始表单类型
         * formItem：表单内容
		 */
		String str = JSONUtil.toJsonStr(map.get("jsonStr"));//前端传来的数据json串
		if(ToolUtil.isBlank(str)){//如果数据为空
			message = "数据不能为空.";
		}else{
			try{
				// 流程存在的时候才能执行
				if(judgeProcessKeyIsLive(keyName)){
					// 返回信息
					map.put("code", "0");
					// 启动流程
					message = startProcess(str, user, keyName, id);
				}else{
					LOGGER.info("this processDefinitionKey's [{}] process is non-exits", keyName);
					message = "任务发起失败,不存在该流程模型.";
				}
			}catch(Exception e){
				LOGGER.warn("start Process failed", e);
				map.put("code", "-1");
				message = "任务发起失败.";
			}
		}
		map.put("message", message);
	}

	/**
	     * @Title: editActivitiModelToRun
	     * @Description: 提交任务
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@ActivitiAndBaseTransaction(value = {"activitiTransactionManager", "transactionManager"})
	public void editActivitiModelToRun(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String processInstanceId = map.get("processInstanceId").toString();
		ProcessInstance instance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
		// 是否挂起
		if(instance.isSuspended()){
			outputObject.setreturnMessage("该流程已被挂起，无法操作。");
			return;
		}
		Map<String, Object> user = inputObject.getLogParams();
		String taskId = map.get("taskId").toString();//当前任务节点
		// 获取审批结果
		boolean flag = getApprovedResult(map.get("flag").toString());

		//获取审批人编辑的信息
		String editStr = map.get("editStr").toString();
		if(!ToolUtil.isBlank(editStr) && ToolUtil.isJson(editStr)){
			List<Map<String, Object>> jArray = JSONUtil.toList(editStr, null);
			// 设置审批人编辑的信息到流程中
			resetEditFormElement(taskId, processInstanceId, jArray);
			// 页面类型 1.指定页面，2.动态表单
			if("2".equals(map.get("pageTypes").toString())){
				// 修改动态表单数据
			    for(int i = 0; i < jArray.size(); i++){
					Map<String, Object> job = jArray.get(i);
			    	job.put("processInstanceId", processInstanceId);
			    	actModleTypeDao.editDsFormMationBySequenceIdAndProcessInstanceId(job);
			    }
			}
		}
		Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
		//处理任务
		Map<String, Object> bean = new HashMap<>();
		// 判断节点是否已经拒绝过一次了
		setWhetherNeedEnd(bean, taskId, flag);
		// 获取指定任务节点的审批信息
		List<Map<String, Object>> leaveList = getUpLeaveList(user.get("id").toString(), user.get("userName").toString(), map.get("opinion").toString(), flag, task);
        bean.put("leaveOpinionList", leaveList);
        bean.put("flag", map.get("flag"));//校验参数
        taskService.complete(taskId, bean);
		this.queryProHighLighted(processInstanceId);//绘制图像
		// 删除指定流程在redis中的缓存信息
		deleteProcessInRedisMation(task.getProcessInstanceId());
	}

	/**
	 * 设置审批人编辑的信息到流程中
	 *
	 * @param taskId 任务id
	 * @param processInstanceId 流程id
	 * @param jArray 编辑的form表单信息
	 */
	private void resetEditFormElement(String taskId, String processInstanceId, List<Map<String, Object>> jArray){
		Map<String, Object> params = (Map<String, Object>) taskService.getVariable(taskId, PROCESSINSTANCEID_TASK_VARABLES);
		for(int i = 0; i < jArray.size(); i++){
			Map<String, Object> jObject = jArray.get(i);
			String rowId = jObject.get("rowId").toString();
			if(params.containsKey(rowId)){
				Map<String, Object> cenBean = (Map<String, Object>) params.get(rowId);
				cenBean.put("text", jObject.get("text"));
				cenBean.put("value", jObject.get("value"));
				params.put(rowId, cenBean);
			}
		}
		runtimeService.setVariable(processInstanceId, PROCESSINSTANCEID_TASK_VARABLES, params);
	}

	/**
	 * 判断节点是否已经拒绝过一次了，如果是，则结束流程
	 *
	 * @param bean
	 * @param taskId
	 * @param flag
	 */
	private void setWhetherNeedEnd(Map<String, Object> bean, String taskId, boolean flag){
		Map<String, Object> variables = taskService.getVariables(taskId);
		// 判断节点是否已经拒绝过一次了
		Object needend = variables.get("needend");
		if (needend != null && (boolean) needend && (!flag)) {
			// 结束
			bean.put("needfinish", -1);
		} else {
			if (flag) {
				// 通过下一个节点
				bean.put("needfinish", 1);
			} else {
				// 不通过
				bean.put("needfinish", 0);
			}
		}
	}

	/**
	 * 获取审批人的审批结果，并转成boolean类型
	 *
	 * @param flag 审批结果
	 * @return
	 */
	private boolean getApprovedResult(String flag){
		// 是否通过
		if("1".equals(flag)){
			// 通过
			return true;
		}else if("2".equals(flag)){
			// 不通过
			return false;
		}else{
			throw new CustomException("approve result 'flag' value is wrong");
		}
	}

	/**
	 * 流程图高亮显示
	 *
	 * @param processInstanceId
	 * @throws Exception
	 */
	private void queryProHighLighted(String processInstanceId) throws Exception {
		// 获取历史流程实例
		HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
		// 获取流程图
		BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());

		ProcessDiagramGenerator diagramGenerator = processEngineConfiguration.getProcessDiagramGenerator();
		ProcessDefinitionEntity definitionEntity = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(processInstance.getProcessDefinitionId());
		//获取历史流程节点
		List<HistoricActivityInstance> highLightedActivitList = historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).list();
		// 高亮环节id集合
		List<String> highLightedActivitis = new ArrayList<String>();

		// 高亮线路id集合
		List<String> highLightedFlows = getHighLightedFlows(definitionEntity, highLightedActivitList);

		for (HistoricActivityInstance tempActivity : highLightedActivitList) {
			String activityId = tempActivity.getActivityId();
			highLightedActivitis.add(activityId);
		}
		// 配置字体
		InputStream imageStream = diagramGenerator.generateDiagram(bpmnModel, "png", highLightedActivitis, highLightedFlows, "宋体", "微软雅黑", "黑体", null, 2.0);
		BufferedImage bi = ImageIO.read(imageStream);
		File fileFolder = new File(tPath + "upload/activiti/");
		if(!fileFolder.isDirectory())//目录不存在
			fileFolder.mkdirs();//创建目录
		File file = new File(tPath + "upload/activiti/" + processInstanceId + ".png");
		if (!file.exists())
			file.createNewFile();
		FileOutputStream fos = new FileOutputStream(file);
		ImageIO.write(bi, "png", fos);
		fos.close();
		imageStream.close();
	}

	/**
	 * 获取需要高亮的线
	 *
	 * @param processDefinitionEntity
	 * @param historicActivityInstances
	 * @return
	 */
	private List<String> getHighLightedFlows(ProcessDefinitionEntity processDefinitionEntity, List<HistoricActivityInstance> historicActivityInstances) {
		List<String> highFlows = new ArrayList<String>();// 用以保存高亮的线flowId
		for (int i = 0; i < historicActivityInstances.size() - 1; i++) {// 对历史流程节点进行遍历
			ActivityImpl activityImpl = processDefinitionEntity.findActivity(historicActivityInstances.get(i).getActivityId());// 得到节点定义的详细信息
			List<ActivityImpl> sameStartTimeNodes = new ArrayList<ActivityImpl>();// 用来保存后需开始时间相同的节点
			ActivityImpl sameActivityImpl1 = processDefinitionEntity.findActivity(historicActivityInstances.get(i + 1).getActivityId());
			// 将后面第一个节点放在时间相同节点的集合里
			sameStartTimeNodes.add(sameActivityImpl1);
			for (int j = i + 1; j < historicActivityInstances.size() - 1; j++) {
				HistoricActivityInstance activityImpl1 = historicActivityInstances.get(j);// 后续第一个节点
				HistoricActivityInstance activityImpl2 = historicActivityInstances.get(j + 1);// 后续第二个节点
				if (activityImpl1.getStartTime().equals(activityImpl2.getStartTime())) {
					// 如果第一个节点和第二个节点开始时间相同保存
					ActivityImpl sameActivityImpl2 = processDefinitionEntity.findActivity(activityImpl2.getActivityId());
					sameStartTimeNodes.add(sameActivityImpl2);
				} else {
					// 有不相同跳出循环
					break;
				}
			}
			List<PvmTransition> pvmTransitions = activityImpl.getOutgoingTransitions();// 取出节点的所有出去的线
			for (PvmTransition pvmTransition : pvmTransitions) {
				// 对所有的线进行遍历
				ActivityImpl pvmActivityImpl = (ActivityImpl) pvmTransition.getDestination();
				// 如果取出的线的目标节点存在时间相同的节点里，保存该线的id，进行高亮显示
				if (sameStartTimeNodes.contains(pvmActivityImpl)) {
					highFlows.add(pvmTransition.getId());
				}
			}
		}
		return highFlows;
	}

	/**
	     * @Title: deleteActivitiModelById
	     * @Description: 删除模型
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void deleteActivitiModelById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String modelId = map.get("id").toString();
		try{
			ModelQuery modelQuery = repositoryService.createModelQuery().modelId(modelId);
			Model model = modelQuery.singleResult();
			String deploymentId = model.getDeploymentId();
			// 已发布的模型需要多删除流程定义和流程发布表
			if (StrUtil.isNotBlank(deploymentId)) {
				repositoryService.deleteDeployment(deploymentId);
			}
			// 删除流程模型表
			repositoryService.deleteModel(modelId);
		}catch(Exception e){
			outputObject.setreturnMessage("删除失败");
		}
	}

	/**
	     * @Title: deleteReleasedActivitiModelById
	     * @Description: 取消发布
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void deleteReleasedActivitiModelById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String deploymentId = map.get("deploymentId").toString();
		try{
			List<ActivityImpl> activityList = actAssigneeService.getActivityList(deploymentId);
			if(activityList != null){
				for (ActivityImpl activity : activityList) {
					String nodeId = activity.getId();
					if (StringUtils.isEmpty(nodeId) || "start".equals(nodeId) || "end".equals(nodeId)) {
						continue;
					}
					/**接触节点和代办关联*/
					actAssigneeService.deleteByNodeId(nodeId);
				}
			}
			repositoryService.deleteDeployment(deploymentId, true);
		}catch(Exception e){
			outputObject.setreturnMessage("存在正在进行的流程，无法取消发布。");
		}
	}

	/**
	     * @Title: queryUserAgencyTasksListByUserId
	     * @Description: 获取用户待办任务
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryUserAgencyTasksListByUserId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
        // 获取用户id
		String userId = user.get("id").toString();
        // 查询代理人,候选人待办
		TaskQuery taskQuery = taskService.createTaskQuery().taskCandidateOrAssigned(userId);
		
		// 1.任务名称查询
		if(!ToolUtil.isBlank(map.get("taskName").toString())){
			taskQuery.taskNameLike("%" + map.get("taskName").toString() + "%");
		}
		// 2.流程id查询
		if(!ToolUtil.isBlank(map.get("processInstanceId").toString())){
			taskQuery.processInstanceId(map.get("processInstanceId").toString());
		}
		// 获取总条数
		int count = taskQuery.list().size();
		List<Task> taskList = taskQuery.orderByProcessInstanceId().desc()
				.listPage(Integer.parseInt(map.get("limit").toString()) * (Integer.parseInt(map.get("page").toString()) - 1), Integer.parseInt(map.get("limit").toString()));
        //整理数据
        List<Map<String, Object>> rows = new ArrayList<>();
		//for循环中使用的变量
		ProcessInstance instance;
		Map<String, Object> params, taskModel, actModel, creater;
		String taskType, key;
		for (Task task : taskList) {
			//流程待办在redis中存储的key
			key = Constants.getProjectActProcessInstanceUserAgencyTasksItemById(task.getProcessInstanceId(), userId);
        	if(ToolUtil.isBlank(jedisClient.get(key))){
        		instance = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
    			//获取流程自定义所属名称
    			taskType = "未知流程";
    			if(instance != null){
                	map.put("actKey", instance.getProcessDefinitionKey());
                	actModel = actModelDao.queryActModelByActKey(map);
                	if(actModel != null && !actModel.isEmpty()){
                		taskType = actModel.get("title").toString();
                	}
    			}
    			//获取流程创建时间
    			Map<String, Object> process = new HashMap<>();
    			process.put("processInstanceId", task.getProcessInstanceId());
    			process = actUserProcessInstanceIdDao.queryProcessInstanceMationByProcessInstanceId(process);
    			
    			//获取提交时候的信息
            	params = (Map<String, Object>) taskService.getVariable(task.getId(), PROCESSINSTANCEID_TASK_VARABLES);
    			taskModel = new HashMap<>();
    			taskModel.put("assignee", task.getAssignee());
    			taskModel.put("createName", params.get("createName"));//申请人姓名
    			taskModel.put("createTime", (process == null || process.isEmpty()) ? "" : process.get("createTime"));//申请时间
    			taskModel.put("taskType", taskType);//任务类型
    			taskModel.put("id", task.getId());
    			taskModel.put("name", ToolUtil.isBlank(task.getName()) ? "" : task.getName());
    			taskModel.put("suspended", instance.isSuspended());//流程状态
    			taskModel.put("processInstanceId", task.getProcessInstanceId());
    			// 获取流程变量
    			creater = (Map<String, Object>) map.get(PROCESSINSTANCEID_TASK_VARABLES);//用户提交的form表单数据
    			if(creater != null && !creater.isEmpty() && creater.containsKey("createName")){//创建人
    				taskModel.put("userName", creater.get("createName"));
    			}else{
    				taskModel.put("userName", "");
    			}
    			jedisClient.set(key, JSONUtil.toJsonStr(taskModel));
    		}else{
    			taskModel = JSONUtil.toBean(jedisClient.get(key), null);
    		}
			rows.add(taskModel);
		}
		outputObject.setBeans(rows);
		outputObject.settotal(count);
	}

	/**
	     * @Title: queryReleaseActivitiModelList
	     * @Description: 获取已发布模型
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryReleaseActivitiModelList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		//获取已经绑定的模型流程
		List<Map<String, Object>> hasModelList = actModelDao.queryHasModelList(map);
		//获取已发布模型流程
		List<ProcessDefinition> processList = repositoryService.createProcessDefinitionQuery().list();
		long count = processList.size();
		List<Map<String, Object>> beans = new ArrayList<>();
		boolean inHas = false;
		for (ProcessDefinition pro : processList) {
			inHas = false;
			for(Map<String, Object> b : hasModelList){
				if(pro.getKey().equals(b.get("actId").toString())){
					inHas = true;
					break;
				}
			}
			if(!inHas){
				Model model = repositoryService.createModelQuery().deploymentId(pro.getDeploymentId()).singleResult();
				if(model != null){
					Map<String, Object> processModel = new HashMap<>();
					processModel.put("id", pro.getKey());
					processModel.put("name", model.getName());
					beans.add(processModel);
				}
			}
		}
		outputObject.setBeans(beans);
		outputObject.settotal(count);
	}
	
	/**
	     * @Title: editApprovalActivitiTaskListByUserId
	     * @Description: 导出model的xml文件
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void editApprovalActivitiTaskListByUserId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String modelId = map.get("modelId").toString();
		Model modelData = repositoryService.getModel(modelId);
		BpmnJsonConverter jsonConverter = new BpmnJsonConverter();
		JsonNode editorNode = new ObjectMapper().readTree(repositoryService.getModelEditorSource(modelData.getId()));
		BpmnModel bpmnModel = jsonConverter.convertToBpmnModel(editorNode);
		BpmnXMLConverter xmlConverter = new BpmnXMLConverter();
		byte[] bpmnBytes = xmlConverter.convertToXML(bpmnModel);
		ByteArrayInputStream in = new ByteArrayInputStream(bpmnBytes);
		String filename = bpmnModel.getMainProcess().getId() + ".bpmn20.xml";
		inputObject.getResponse().setHeader("REQUESTMATION", "DOWNLOAD");
		inputObject.getResponse().setHeader("Content-Disposition", "attachment;filename=" + filename);
		// 1.设置文件ContentType类型，这样设置，会自动判断下载文件类型
		inputObject.getResponse().setContentType("multipart/form-data");
		BufferedOutputStream out1 = new BufferedOutputStream(inputObject.getResponse().getOutputStream());
		int len = 0;
		while ((len = in.read()) != -1) {
			out1.write(len);
			out1.flush();
		}
	}

	/**
	     * @Title: queryUserListToActiviti
	     * @Description: 获取人员选择
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryUserListToActiviti(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String reqObjStr = map.get("reqObj").toString();
		Map<String, Object> reqObj = JSONUtil.toBean(reqObjStr, null);
		
		// 获取参数信息
		List<Map<String, Object>> conditions = JSONUtil.toList(reqObj.get("conditions").toString(), null);
		
		// 查询参数
		Map<String, Object> parmter = new HashMap<>();
		if(conditions.size() > 0){//参数信息
			parmter.put(conditions.get(0).get("key").toString(), conditions.get(0).get("value"));
		}
		initPagingMation(reqObj, parmter);

		Page pages = PageHelper.startPage(Integer.parseInt(parmter.get("page").toString()), Integer.parseInt(parmter.get("limit").toString()));
		List<Map<String, Object>> beans = sysEveUserDao.queryUserListToActiviti(parmter);
		long total = pages.getTotal();
		
		// 表信息
		Map<String, Object> query = new HashMap<>();
		setCommonUserTableElement(query, reqObj.get("queryId").toString(), parmter.get("limit").toString());
		query.put("columnList", ActivitiConstants.getActivitiUserColumnList());
		query.put("columnMap", ActivitiConstants.getActivitiUserColumnMap());
		
		// 分页信息
		Map<String, Object> pageInfo = getTablePageMation(total, parmter);
		
		outputObject.setCustomBean("query", query);
		outputObject.setCustomBean("pageInfo", pageInfo);
		outputObject.setBeans(beans);
		outputObject.settotal(total);
	}

	/**
	 * 人员选择的表格公共部分
	 *
	 * @param query table信息
	 * @param queryId 表格id
	 * @param limit 每页多少条数据
	 */
	private void setCommonUserTableElement(Map<String, Object> query, String queryId, String limit){
		query.put("id", queryId);
		query.put("key", "id");
		query.put("tableName", "流程用户列表");
		query.put("pagesize", limit);
		query.put("pagesInGrp", "5");
		query.put("widthType", "px");
		query.put("allowPaging", true);
		query.put("enableMultiline", true);
		query.put("isServerFilter", false);
		query.put("enableMultiHeader", false);
		query.put("simpleSearch", false);
		query.put("startRow", 1);
	}

	/**
	 * 初始化分页信息
	 *
	 * @param reqObj 请求参数信息
	 * @param parmter 查询参数
	 */
	private void initPagingMation(Map<String, Object> reqObj, Map<String, Object> parmter){
		Map<String, Object> page = JSONObject.fromObject(reqObj.get("pageInfo").toString());
		if(page == null || page.isEmpty()){
			parmter.put("page", 1);
			parmter.put("limit", 10);
		}else{
			parmter.put("page", page.get("pageNum"));
			parmter.put("limit", page.get("pageSize"));
		}
	}

	/**
	 * 获取表格的分页信息
	 *
	 * @param total 数据总条数
	 * @param parmter 分页信息
	 * @return
	 */
	private Map<String, Object> getTablePageMation(long total, Map<String, Object> parmter){
		Map<String, Object> pageInfo = new HashMap<>();
		String limit = parmter.get("limit").toString();
		pageInfo.put("pageNum", parmter.get("page"));
		pageInfo.put("pageSize", parmter.get("limit"));
		pageInfo.put("count", total);
		long pageCount = total / Integer.parseInt(limit);
		if (total % Integer.parseInt(limit) != 0){
			pageCount++;
		}
		pageInfo.put("pageCount", pageCount);
		return pageInfo;
	}

	/**
	     * @Title: queryUserGroupListToActiviti
	     * @Description: 获取组人员选择
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryUserGroupListToActiviti(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String reqObjStr = map.get("reqObj").toString();
		Map<String, Object> reqObj = JSONUtil.toBean(reqObjStr, null);
		String queryId = reqObj.get("queryId").toString();
		//获取参数信息
		List<Map<String, Object>> conditions = JSONUtil.toList(reqObj.get("conditions").toString(), null);
		
		//查询参数
		Map<String, Object> parmter = new HashMap<>();
		for(Map<String, Object> condition : conditions){//参数信息
			parmter.put(condition.get("key").toString(), condition.get("value"));
		}
		initPagingMation(reqObj, parmter);

		Page pages = PageHelper.startPage(Integer.parseInt(parmter.get("page").toString()), Integer.parseInt(parmter.get("limit").toString()));
		List<Map<String, Object>> beans = null;
		if("id_group_list".equals(queryId)){//分组
			beans = sysEveUserDao.queryGroupListToActiviti(parmter);//人员
		}else{
			beans = sysEveUserDao.queryUserListToActivitiByGroup(parmter);//人员
		}
		long total = pages.getTotal();
		
		// 表信息
		Map<String, Object> query = new HashMap<>();
		setCommonUserTableElement(query, reqObj.get("queryId").toString(), parmter.get("limit").toString());
		if("id_group_list".equals(queryId)){//分组
			query.put("columnList", ActivitiConstants.getActivitiGroupColumnList());
			query.put("columnMap", ActivitiConstants.getActivitiGroupColumnMap());
		}else{//人员
			query.put("columnList", ActivitiConstants.getActivitiUserColumnListByGroupId());
			query.put("columnMap", ActivitiConstants.getActivitiUserColumnMapByGroupId());
		}
		
		// 分页信息
		Map<String, Object> pageInfo = getTablePageMation(total, parmter);
		
		outputObject.setCustomBean("query", query);
		outputObject.setCustomBean("pageInfo", pageInfo);
		outputObject.setBeans(beans);
		outputObject.settotal(total);
	}

	/**
	     * @Title: queryStartProcessNotSubByUserId
	     * @Description: 获取我启动的流程
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryStartProcessNotSubByUserId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		String userId = user.get("id").toString();
		map.put("createId", userId);
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = actUserProcessInstanceIdDao.queryStartProcessNotSubByUserId(map);
		// 创建返回前台的集合
		List<Map<String, Object>> items = new ArrayList<>();
		Map<String, Object> taskModel;
		for (Map<String, Object> bean : beans) {
			// 该结束流程在redis中存储的key
			String processInstanceId = bean.get("processInstanceId").toString();
			String key = Constants.getProjectActProcessInstanceItemById(processInstanceId, userId);
        	if(ToolUtil.isBlank(jedisClient.get(key))){
				ProcessInstance instance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
				//保证运行ing
	            if (instance != null) {
	            	Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).list().get(0);
	            	//获取提交时候的信息
	            	Map<String, Object> params = (Map<String, Object>) taskService.getVariable(task.getId(), PROCESSINSTANCEID_TASK_VARABLES);
	            	//获取当前任务节点的审批人
	            	Map<String, Object> userMation = new HashMap<>();
	            	userMation.put("userId", task.getAssignee());
	            	userMation = sysEveUserDao.queryUserMationByUserId(userMation);

	            	taskModel = new HashMap<>();
	            	taskModel.put("id", task.getId());//任务id
	            	taskModel.put("name", task.getName());//当前任务节点名称
	            	if(userMation != null && !userMation.isEmpty()){
	            		taskModel.put("agencyName", userMation.get("userName"));//审批人
	            	}else{
	            		taskModel.put("agencyName", "未设置");//审批人
	            	}
	            	taskModel.put("createName", params.get("createName"));//申请人姓名
	            	taskModel.put("processDefId", task.getProcessDefinitionId());
	            	taskModel.putAll(bean);//将从数据库查出来的数据返回给前台
	            	//判断是否可编辑
	            	Map<String, Object> variables = taskService.getVariables(task.getId());
	                taskModel.put("editRow", "1");//可编辑
					taskModel.put("weatherEnd", 0);//标记流程是否结束；1：结束，0.未结束
					taskModel.put("suspended", instance.isSuspended());//流程状态
					Object o = variables.get("leaveOpinionList");
					if (o != null) {
	                	//获取历史审核信息
	                	List<Map<String, Object>> leaveList = (List<Map<String, Object>>) o;
	                	for(Map<String, Object> leave : leaveList){
	                		if(!userId.equals(leave.get("opId").toString())){
	                    		taskModel.put("editRow", "-1");//不可编辑
	                    		break;
	                    	}
	                	}
	                }
	            }else{
	            	//已结束流程
	            	taskModel = getHistoricProcessInstance(processInstanceId, null);
	            	taskModel.put("createName", user.get("userName"));//申请人
	            	taskModel.putAll(bean);//将从数据库查出来的数据返回给前台
        			taskModel.put("editRow", "-1");//不可编辑
        			taskModel.put("weatherEnd", 1);//标记流程是否结束；1：结束，0.未结束
        			taskModel.put("suspended", false);//流程状态-正常
	            }
	            jedisClient.set(key, JSONUtil.toJsonStr(taskModel));
			}else{
				taskModel = JSONUtil.toBean(jedisClient.get(key), null);
			}
        	taskModel.put("pageUrl", bean.get("pageUrl"));
        	taskModel.put("revokeMapping", bean.get("revokeMapping"));
        	items.add(taskModel);
		}
		outputObject.setBeans(items);
		outputObject.settotal(pages.getTotal());
	}
	
	/**
	 * 获取历史流程信息
	 *
	 * @param processInstanceId 流程id
	 * @param hisTaskId 历史任务id
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> getHistoricProcessInstance(String processInstanceId, String hisTaskId) throws Exception{
		Map<String, Object> map = new HashMap<>();
		HistoricProcessInstance instance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
		//获取当前最后一个节点的受理人
		List<HistoricVariableInstance> hisTaskList = historyService.createHistoricVariableInstanceQuery().processInstanceId(processInstanceId).list();
		String assignee = "";
		for (HistoricVariableInstance historicDetail : hisTaskList) {
			if ("leaveOpinionList".equals(historicDetail.getVariableName())) {
				List<Map<String, Object>> bs = (List<Map<String, Object>>) historicDetail.getValue();
				if(bs == null)
					bs = new ArrayList<>();
				//根据时间排序
				Collections.sort(bs, new Comparator<Map<String, Object>>() {
					public int compare(Map<String, Object> p1, Map<String, Object> p2) {
						try {
							if(DateUtil.compare(p1.get("createTime").toString(), p2.get("createTime").toString()))
								return 1;
						} catch (ParseException e) {
						}
						return -1;
					}
				});
				if(!ToolUtil.isBlank(hisTaskId)){
					//获取task名称
					for(Map<String, Object> leave : bs){
						if(hisTaskId.equals(leave.get("taskId").toString())){
							assignee = leave.get("opName").toString();
							break;
						}
					}
				}else{
					assignee = bs.get(0).get("opName").toString();
				}
			}
		}
		//获取流程自定义所属名称
    	String taskType = "未知流程";
    	map.put("actKey", instance != null ? instance.getProcessDefinitionKey() : "");
    	Map<String, Object> actModel = actModelDao.queryActModelByActKey(map);
    	if(actModel != null && !actModel.isEmpty()){
    		taskType = actModel.get("title").toString();
    	}
    	Map<String, Object> taskModel = new HashMap<>();
    	taskModel.put("agencyName", assignee);//受理人
    	taskModel.put("taskType", taskType);//流程所属名称
    	taskModel.put("createTime", instance != null ? instance.getStartTime() : "");//申请时间
    	taskModel.put("name", "结束");//当前任务节点名称
    	taskModel.put("processInstanceId", processInstanceId);//流程id
    	taskModel.put("startTime", instance != null ? instance.getStartTime() : "");//开始时间
    	taskModel.put("endTime", instance != null ? instance.getEndTime() : "");//结束时间
		return taskModel;
	}
	
	/**
	 * 
	     * @Title: queryMyHistoryTaskByUserId
	     * @Description: 获取我的历史任务
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * 历史表中存在并非是单一类型的数据，就拿历史任务表来说，里边既有已经结束的任务，也有还没有结束的任务。 如果要单独查询结束了的任务，就可以调用finished()方法，查询的就是已经结束的任务
	 	 * @throws
	 */
	@Override
	public void queryMyHistoryTaskByUserId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		String userId = user.get("id").toString();
		//获取我的已办历史
		List<HistoricTaskInstance> hisTaskList = historyService.createHistoricTaskInstanceQuery().taskAssignee(userId).orderByTaskCreateTime().desc().finished().list();
		List<HistoricTaskInstance> hisGroupTaskList = historyService.createHistoricTaskInstanceQuery().taskCandidateUser(userId).orderByTaskCreateTime().desc().finished().list();
		hisTaskList.addAll(hisGroupTaskList);
		int count = hisTaskList.size();
		
		int pageMaxSize = Integer.parseInt(map.get("limit").toString()) * (Integer.parseInt(map.get("page").toString()));
		if(count < pageMaxSize){
			pageMaxSize = count;
		}
		//我的历史任务集合进行分页
		hisTaskList = hisTaskList.subList(Integer.parseInt(map.get("limit").toString()) * (Integer.parseInt(map.get("page").toString()) - 1), pageMaxSize);
		
		List<Map<String, Object>> beans = new ArrayList<>();
		Map<String, Object> hisModel;
		ProcessInstance instance;
		String key;
		for (HistoricTaskInstance hisTask : hisTaskList) {
			//该流程在redis中存储的key
        	key = Constants.getProjectActProcessHisInstanceItemById(hisTask.getProcessInstanceId(), userId);
        	if(ToolUtil.isBlank(jedisClient.get(key))){
				instance = runtimeService.createProcessInstanceQuery().processInstanceId(hisTask.getProcessInstanceId()).singleResult();
				if(instance != null){
					hisModel = new HashMap<>();
					//获取流程自定义所属名称
					String taskType = "未知流程";
	            	map.put("actKey", instance.getProcessDefinitionKey());
	            	Map<String, Object> actModel = actModelDao.queryActModelByActKey(map);
	            	if(actModel != null && !actModel.isEmpty()){
	            		taskType = actModel.get("title").toString();
	            	}
	            	//获取当前任务节点的审批人
	            	String operatorName = "";
	            	List<HistoricVariableInstance> list = historyService.createHistoricVariableInstanceQuery().processInstanceId(instance.getProcessInstanceId()).list();
	    			for (HistoricVariableInstance historicDetail : list) {
	    				if ("leaveOpinionList".equals(historicDetail.getVariableName())) {
	    					List<Map<String, Object>> leaveList = (List<Map<String, Object>>) historicDetail.getValue();
	    					//获取task名称
	    					for(Map<String, Object> leave : leaveList){
	    						String taskId = leave.get("taskId").toString();
	    						if(hisTask.getId().equals(taskId)){
	    							operatorName = leave.get("opName").toString();
	    							break;
	    						}
	    					}
	    				}
	    			}
	            	//获取提交时候的信息
	            	Task task = taskService.createTaskQuery().processInstanceId(hisTask.getProcessInstanceId()).singleResult();
	            	Map<String, Object> params = (Map<String, Object>) taskService.getVariable(task.getId(), PROCESSINSTANCEID_TASK_VARABLES);
	            	
	            	hisModel.put("createName", params.get("createName"));//申请人姓名
	            	hisModel.put("taskType", taskType);//类型
	            	hisModel.put("createTime", hisTask.getStartTime());//申请时间
	            	hisModel.put("agencyName", operatorName);//受理人
	            	hisModel.put("name", hisTask.getName());//审批节点
	            	hisModel.put("processInstanceId", hisTask.getProcessInstanceId());//流程id
	            	hisModel.put("startTime", hisTask.getStartTime());//申请时间
	            	hisModel.put("endTime", hisTask.getEndTime());//受理时间
	            	hisModel.put("weatherEnd", 0);//标记流程是否结束；1：结束，0.未结束
				}else{
					//流程id
	            	String processInstanceId = hisTask.getProcessInstanceId();
            		hisModel = getHistoricProcessInstance(processInstanceId, hisTask.getId());
                	//获取申请人
                	Map<String, Object> applicant = new HashMap<>();
                	applicant.put("processInstanceId", hisTask.getProcessInstanceId());
                	applicant = sysEveUserDao.queryUserNameByProcessInstanceId(applicant);
                	hisModel.put("name", hisTask.getName());//我处理的任务
                	hisModel.put("createName", applicant == null ? "" : applicant.get("userName"));//申请人
                	hisModel.put("weatherEnd", 1);//标记流程是否结束；1：结束，0.未结束
				}
				jedisClient.set(key, JSONUtil.toJsonStr(hisModel));
        	}else{
        		hisModel = JSONUtil.toBean(jedisClient.get(key), null);
        	}
			//历史审批任务id
			hisModel.put("hisTaskId", hisTask.getId());
			beans.add(hisModel);
		}
		outputObject.setBeans(beans);
		outputObject.settotal(count);
	}

	/**
	     * @Title: insertSyncUserListMationToAct
	     * @Description: 用户以及用户组信息同步到act表中
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void insertSyncUserListMationToAct(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		//同步用户组信息
		List<Map<String, Object>> groupList = sysEveUserDao.queryActGroupList(map);
		Group group = null;
		for(Map<String, Object> bean : groupList){
			group = new GroupEntity();
            group.setId(bean.get("id").toString());
            group.setName(bean.get("name").toString());
            identityService.deleteGroup(group.getId());
            identityService.saveGroup(group);
		}
		//同步用户信息
		List<Map<String, Object>> userList = sysEveUserDao.queryActUserList(map);
		User user = null;
		for(Map<String, Object> bean : userList){
			user = new UserEntity();
			user.setId(bean.get("id").toString());
			user.setFirstName(bean.get("firstName").toString());
			user.setLastName(bean.get("lastName").toString());
			user.setEmail(bean.get("email").toString());
            identityService.deleteUser(user.getId());
            identityService.saveUser(user);
		}
		//同步用户和用户组的关系信息
		List<Map<String, Object>> userGroupList = sysEveUserDao.queryActUserGroupList(map);
		for(Map<String, Object> bean : userGroupList){
			identityService.deleteMembership(bean.get("userId").toString(), bean.get("groupId").toString());
            identityService.createMembership(bean.get("userId").toString(), bean.get("groupId").toString());
		}
	}

	/**
	 * 
	     * @Title: querySubFormMationByTaskId
	     * @Description: 获取用户提交的表单信息根据taskid
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySubFormMationByTaskId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String taskId = map.get("taskId").toString();
		
		//获取任务自定义id和名称
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		map.put("taskKey", task.getTaskDefinitionKey());
		map.put("taskKeyName", task.getName());
		
		//获取流程关联页面类型
		Map<String, Object> process = new HashMap<>();
		process.put("processInstanceId", task.getProcessInstanceId());
		process = actUserProcessInstanceIdDao.queryProcessInstanceMationByProcessInstanceId(process);
		map.put("pageTypes", process.get("pageTypes"));
		
		//获取提交时候的信息
    	Map<String, Object> params = (Map<String, Object>) taskService.getVariable(taskId, PROCESSINSTANCEID_TASK_VARABLES);
    	List<Map<String, Object>> beans = getParamsToDSFormShow(params);
    	outputObject.setBean(map);
    	outputObject.setBeans(beans);
	}
	
	/**
	 * 获取下一个用户任务信息
	 * 
	 * @param processInstanceId 流程Id信息
	 * @return 下一个用户任务用户组信息
	 * @throws Exception
	 */
	public TaskDefinition getNextTaskInfo(String processInstanceId, Map<String, Object> map) throws Exception {
		ProcessDefinitionEntity processDefinitionEntity = null;
		String id = null;
		TaskDefinition task = null;

		// 获取流程发布Id信息
		String definitionId = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult().getProcessDefinitionId();
		processDefinitionEntity = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService).getDeployedProcessDefinition(definitionId);
		ExecutionEntity execution = (ExecutionEntity) runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();

		// 当前流程节点Id信息
		String activitiId = execution.getActivityId();
		// 获取流程所有节点信息
		List<ActivityImpl> activitiList = processDefinitionEntity.getActivities();
		// 遍历所有节点信息
		for (ActivityImpl activityImpl : activitiList) {
			id = activityImpl.getId();
			if (activitiId.equals(id)) {
				// 获取下一个节点信息
				task = nextTaskDefinition(activityImpl, activityImpl.getId(), processInstanceId, map);
				break;
			}
		}
		return task;
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

		PvmActivity ac = null;
		Object s = null;
		System.out.println(activityImpl.getProperty("type"));
		// 如果遍历节点为用户任务并且节点不是当前节点信息
		if ("userTask".equals(activityImpl.getProperty("type")) && !activityId.equals(activityImpl.getId())) {
			// 获取该节点下一个节点信息
			TaskDefinition taskDefinition = ((UserTaskActivityBehavior) activityImpl.getActivityBehavior()).getTaskDefinition();
			return taskDefinition;
		} else if ("exclusiveGateway".equals(activityImpl.getProperty("type"))) {// 当前节点为exclusiveGateway
			List<PvmTransition> outTransitions = activityImpl.getOutgoingTransitions();
			// 如果排他网关只有一条线路信息
			if (outTransitions.size() == 1) {
				return nextTaskDefinition((ActivityImpl) outTransitions.get(0).getDestination(), activityId, processInstanceId, map);
			} else if (outTransitions.size() > 1) { // 如果排他网关有多条线路信息
				for (PvmTransition tr1 : outTransitions) {
					s = tr1.getProperty("conditionText"); // 获取排他网关线路判断条件信息
					// 判断el表达式是否成立
					if (isCondition(activityImpl.getId(), StringUtils.trim(s.toString()), map)) {
						return nextTaskDefinition((ActivityImpl) tr1.getDestination(), activityId, processInstanceId, map);
					}

				}
			}
		} else {
			// 获取节点所有流向线路信息
			List<PvmTransition> outTransitions = activityImpl.getOutgoingTransitions();
			List<PvmTransition> outTransitionsTemp = null;
			for (PvmTransition tr : outTransitions) {
				ac = tr.getDestination(); // 获取线路的终点节点
				// 如果流向线路为排他网关
				if ("exclusiveGateway".equals(ac.getProperty("type"))) {
					outTransitionsTemp = ac.getOutgoingTransitions();

					// 如果排他网关只有一条线路信息
					if (outTransitionsTemp.size() == 1) {
						return nextTaskDefinition((ActivityImpl) outTransitionsTemp.get(0).getDestination(), activityId, processInstanceId, map);
					} else if (outTransitionsTemp.size() > 1) { // 如果排他网关有多条线路信息
						for (PvmTransition tr1 : outTransitionsTemp) {
							s = tr1.getProperty("conditionText"); // 获取排他网关线路判断条件信息
							// 判断el表达式是否成立
							if (isCondition(ac.getId(), StringUtils.trim(s.toString()), map)) {
								return nextTaskDefinition((ActivityImpl) tr1.getDestination(), activityId, processInstanceId, map);
							}
						}
					}
				} else if ("userTask".equals(ac.getProperty("type"))) {
					return ((UserTaskActivityBehavior) ((ActivityImpl) ac).getActivityBehavior()).getTaskDefinition();
				} else {
				}
			}
			return null;
		}
		return null;
	}

	/**
	 * 查询流程启动时设置排他网关判断条件信息
	 * 
	 * @param gatewayId 排他网关Id信息, 流程启动时设置网关路线判断条件key为网关Id信息
	 * @param processInstanceId 流程实例Id信息
	 * @return
	 */
	public String getGatewayCondition(String gatewayId, String processInstanceId) {
		Execution execution = runtimeService.createExecutionQuery().processInstanceId(processInstanceId).singleResult();
		Object object = runtimeService.getVariable(execution.getId(), gatewayId);
		return object == null ? "" : object.toString();
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
	 * 
	     * @Title: queryApprovalTasksHistoryByProcessInstanceId
	     * @Description: 获取历史审批列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryApprovalTasksHistoryByProcessInstanceId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String processInstanceId = map.get("processInstanceId").toString();
		ProcessInstance instance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
		// 保证运行ing
		List<Map<String, Object>> leaveList = null;
		if (instance != null) {
			Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).list().get(0);
			Map<String, Object> variables = taskService.getVariables(task.getId());
			Object o = variables.get("leaveOpinionList");
			if (o != null) {
				/* 获取历史审核信息 */
				leaveList = (List<Map<String, Object>>) o;
			}
		} else {
			leaveList = new ArrayList<>();
			List<HistoricVariableInstance> list = historyService.createHistoricVariableInstanceQuery().processInstanceId(processInstanceId).list();
			for (HistoricVariableInstance historicDetail : list) {
				if ("leaveOpinionList".equals(historicDetail.getVariableName())) {
					leaveList.clear();
					leaveList.addAll((List<Map<String, Object>>) historicDetail.getValue());
				}
			}
		}
		if(leaveList == null)
			leaveList = new ArrayList<>();
		//根据时间排序
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
		for(Map<String, Object> leave : leaveList){
			leave.put("flagName", (boolean) leave.get("flag") ? "通过" : "拒绝");
			leave.put("opinion", ToolUtil.isBlank(leave.get("opinion").toString()) ? "暂无审批意见" : leave.get("opinion").toString());
		}
		outputObject.setBeans(leaveList);
		outputObject.settotal(leaveList.size());
	}

	/**
	 * 
	     * @Title: queryAllComplateProcessList
	     * @Description: 获取所有已完成的流程信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryAllComplateProcessList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<HistoricProcessInstance> beans = historyService.createHistoricProcessInstanceQuery().orderByProcessInstanceEndTime().desc().finished()
				.listPage(Integer.parseInt(map.get("limit").toString()) * (Integer.parseInt(map.get("page").toString()) - 1), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> rows = new ArrayList<>();
		Map<String, Object> instanceModel;
		for(HistoricProcessInstance bean : beans){
			String processInstanceId = bean.getId();
			instanceModel = getHistoricProcessInstance(processInstanceId, null);
			//获取申请人
        	Map<String, Object> applicant = new HashMap<>();
        	applicant.put("processInstanceId", processInstanceId);
        	applicant = sysEveUserDao.queryUserNameByProcessInstanceId(applicant);
        	instanceModel.put("createName", applicant == null ? "" : applicant.get("userName"));//申请人
        	rows.add(instanceModel);
		}
		outputObject.setBeans(rows);
		outputObject.settotal(historyService.createHistoricProcessInstanceQuery().orderByProcessInstanceEndTime().desc().finished().count());
	}

	/**
	 * 
	     * @Title: queryAllConductProcessList
	     * @Description: 获取所有待办的流程信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryAllConductProcessList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<ProcessInstance> beans = runtimeService.createProcessInstanceQuery().orderByProcessInstanceId().desc()
				.listPage(Integer.parseInt(map.get("limit").toString()) * (Integer.parseInt(map.get("page").toString()) - 1), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> rows = new ArrayList<>();
		Map<String, Object> taskModel;
		for(ProcessInstance instance : beans){
			//获取流程自定义所属名称
			String taskType = "未知流程";
			if(instance != null){
            	map.put("actKey", instance.getProcessDefinitionKey());
            	Map<String, Object> actModel = actModelDao.queryActModelByActKey(map);
            	if(actModel != null && !actModel.isEmpty()){
            		taskType = actModel.get("title").toString();
            	}
			}
			Task task = taskService.createTaskQuery().processInstanceId(instance.getProcessInstanceId()).singleResult();
			//获取提交时候的信息
        	Map<String, Object> params = (Map<String, Object>) taskService.getVariable(task.getId(), PROCESSINSTANCEID_TASK_VARABLES);
			taskModel = new HashMap<>();
			taskModel.put("assignee", task.getAssignee());
			taskModel.put("createName", params.get("createName"));//申请人姓名
			taskModel.put("createTime", task.getCreateTime());//申请时间
			taskModel.put("taskType", taskType);//任务类型
			taskModel.put("id", task.getId());
			taskModel.put("name", task.getName());
			taskModel.put("suspended", instance.isSuspended());//流程状态
			taskModel.put("processInstanceId", task.getProcessInstanceId());
			// 获取流程变量
			Map<String, Object> creater = (Map<String, Object>) map.get(PROCESSINSTANCEID_TASK_VARABLES);//用户提交的form表单数据
			if(creater != null && !creater.isEmpty() && creater.containsKey("createName")){//创建人
				taskModel.put("userName", creater.get("createName"));
			}else{
				taskModel.put("userName", "");
			}
			
			rows.add(taskModel);
		}
		outputObject.setBeans(rows);
		outputObject.settotal(runtimeService.createProcessInstanceQuery().count());
	}

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
		deleteProcessInRedisMation(processInstanceId);
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
		//根据一个流程实例的id激活该流程实例
		runtimeService.activateProcessInstanceById(processInstanceId);
		// 删除指定流程在redis中的缓存信息
		deleteProcessInRedisMation(processInstanceId);
		
	}

	/**
	 * 
	     * @Title: insertDSFormProcess
	     * @Description: 提交审批
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@ActivitiAndBaseTransaction(value = {"activitiTransactionManager", "transactionManager"})
	public void insertDSFormProcess(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		String str = map.get("jsonStr").toString();//前端传来的数据json串
		if(ToolUtil.isBlank(str)){//如果数据为空
			outputObject.setreturnMessage("数据不能为空.");
		}else{
			try{
				List<Map<String, Object>> beans = new ArrayList<Map<String,Object>>();
				List<Map<String, Object>> jsonArray = new ArrayList<>();
				Map<String, Map<String, Object>> jOb = JSONUtil.toBean(str, null);
				//遍历数据存入JSONArray集合
				for(String key : jOb.keySet()){
					jsonArray.add(jOb.get(key));
				}
				String sequenceId = ToolUtil.getSurFaceId();
				String userId = user.get("id").toString();
				String pageId = "";
				for (Map<String, Object> item : jsonArray) {
					String pageContentId = item.get("rowId").toString();
					String value = item.containsKey("value") == true ? item.get("value").toString() : "";
					String text = item.containsKey("text") == true ? item.get("text").toString() : "";
					Map<String, Object> m = dsFormPageService.getDsFormPageData(pageContentId, value, text, item.get("showType").toString(), sequenceId, userId);
					pageId = m.get("pageId").toString();
					beans.add(m);
				}
				editActivitiModelToStartProcessByMap(map, user, sequenceId);
				if("0".equals(map.get("code").toString())){//启动流程成功
					dsFormPageDataDao.insertDsFormPageData(beans);//插入DsFormPageData表
					Map<String, Object> entity = dsFormPageService.getDsFormPageSequence(userId, pageId, map.get("message").toString(), StringUtils.EMPTY);
					entity.put("sequenceId", sequenceId);
					dsFormPageSequenceDao.insertDsFormPageSequence(Arrays.asList(entity));
				}else{
					outputObject.setreturnMessage(map.get("message").toString());
				}
			}catch(Exception e){
				outputObject.setreturnMessage("任务发起失败.");
			}
		}
	}

	/**
	 * 
	     * @Title: querySubFormMationByProcessInstanceId
	     * @Description: 获取用户提交的表单信息根据流程id
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySubFormMationByProcessInstanceId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String processInstanceId = map.get("processInstanceId").toString();
		// 获取提交时候的信息
		List<HistoricVariableInstance> hisTaskList = historyService.createHistoricVariableInstanceQuery().processInstanceId(processInstanceId)
				.variableName(PROCESSINSTANCEID_TASK_VARABLES).list();
		if(!hisTaskList.isEmpty()){
			Map<String, Object> params = (Map<String, Object>) hisTaskList.get(0).getValue();
			List<Map<String, Object>> beans = getParamsToDSFormShow(params);
			outputObject.setBeans(beans);
		}else{
			outputObject.setreturnMessage("数据信息错误");
		}
	}
	
	/**
	 * 将工作流数据转为form表单类型的数据并作展示
	 *
	 * @return
	 */
	public List<Map<String, Object>> getParamsToDSFormShow(Map<String, Object> params){
		List<Map<String, Object>> beans = new ArrayList<>();
    	// 遍历数据存入list集合
    	for (String key : params.keySet()) {
    		if(params.get(key) == null){
    			continue;
			}
    		String str = params.get(key).toString();
    		if(ToolUtil.isJson(str)){
				beans.add((Map<String, Object>) params.get(key));
			}
    	}
		Collections.sort(beans, new Comparator<Map<String, Object>>() {
			public int compare(Map<String, Object> p1, Map<String, Object> p2) {
				int a = Integer.parseInt(p1.get("orderBy").toString());
				int b = Integer.parseInt(p2.get("orderBy").toString());
				if(a > b)
					return 1;
				if(a == b)
					return 0;
				return -1;
			}
		});
		return beans;
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
		deleteProcessInRedisMation(processInstanceId);

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
        this.queryProHighLighted(processInstanceId);//绘制图像
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
		this.queryProHighLighted(map.get("processInstanceId").toString());//绘制图像
	}
	
	/**
	 * 
	     * @Title: editDsFormContentToRevokeByProcessInstanceId
	     * @Description: 动态表单提交项进行撤销操作
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void editDsFormContentToRevokeByProcessInstanceId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("userId", user.get("id"));
		activitiService.editDsFormContentToRevokeByProcessInstanceId(map);
		if("0".equals(map.get("code").toString())){//成功
			//修改表状态
			actUserProcessInstanceIdDao.editDsFormStateIsDraftByProcessInstanceId(map);
		}else{
			outputObject.setreturnMessage(map.get("message").toString());
		}
	}

	/**
	 * 设置该流程是否可以编辑
	 *
	 * @param map 参数
	 * @param processInstanceId 流程id
	 * @param userId 当前登陆人id
	 */
	@Override
	public void setWhetherEditByProcessInstanceId(Map<String, Object> map, String processInstanceId, String userId) throws Exception {
		Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
		if(task == null){
			return;
		}
		// 判断是否可编辑
		Map<String, Object> variables = taskService.getVariables(task.getId());
		Object o = variables.get("leaveOpinionList");
		if (o != null) {
			// 获取历史审核信息
			List<Map<String, Object>> leaveList = (List<Map<String, Object>>) o;
			boolean exist = false;
			// 判断是否存在除本人以外的人审核过
			for(Map<String, Object> l : leaveList){
				if (!l.get("opId").toString().equals(userId)){
					exist = true;
					break;
				}
			}
			if (exist){
				map.put("editRow", "-1");// 不可编辑
			} else {
				map.put("editRow", "2");// 可编辑可撤销
			}
		} else {
			map.put("editRow", "2");// 可编辑可撤销
		}
	}

	/**
	 * 判断是否提交到工作流
	 *
	 * @param bean 单据信息
	 * @return
	 * @throws Exception
	 */
	@Override
	public String judgeSubmitActiviti(Map<String, Object> bean) throws Exception {
		if(bean != null && !bean.isEmpty()){
			int state = Integer.parseInt(bean.get("state").toString());
			if(state == 0 || state == 3 || state == 5){
				// 草稿，审核不通过，撤销的可以提交审批
				return null;
			}else{
				return "该数据状态已改变，请刷新页面";
			}
		}else{
			return "该数据不存在，请刷新页面";
		}
	}

	/**
	 * 流程模型拷贝
	 *
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	@ActivitiAndBaseTransaction(value = {"activitiTransactionManager", "transactionManager"})
	public void copyModelByModelId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String modelId = map.get("modelId").toString();
		// 1.拷贝模型信息
		Model sourceModel = repositoryService.getModel(modelId);
		String description = "";
		int revision = 1;
		String name = sourceModel.getName()+"_copy";
		Model model = repositoryService.newModel();
		String key = ToolUtil.getSurFaceId();
		model.setKey(key);
		model.setName(name);
		model.setCategory(sourceModel.getCategory());
		model.setVersion(1);

		ObjectNode modelNode = objectMapper.createObjectNode();
		modelNode.put(ModelDataJsonConstants.MODEL_NAME, name);
		modelNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description);
		modelNode.put(ModelDataJsonConstants.MODEL_REVISION, revision);
		model.setMetaInfo(modelNode.toString());
		repositoryService.saveModel(model);

		// 拷贝流程图信息
		ObjectNode sourceObjectNode = (ObjectNode) new ObjectMapper().readTree(repositoryService.getModelEditorSource(modelId));
		ObjectNode editorNode = sourceObjectNode.deepCopy();
		ObjectNode properties = objectMapper.createObjectNode();
		properties.put("process_author", "skyeye");
		editorNode.set("properties", properties);

		repositoryService.addModelEditorSource(model.getId(), editorNode.toString().getBytes("utf-8"));
	}

}
