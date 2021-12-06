/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.activiti.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.skyeye.activiti.mapper.ActivityMapper;
import com.skyeye.activiti.service.ActAssigneeService;
import com.skyeye.activiti.service.ActivitiModelService;
import com.skyeye.activiti.service.ActivitiTaskService;
import com.skyeye.annotation.transaction.ActivitiAndBaseTransaction;
import com.skyeye.common.constans.ActivitiConstants;
import com.skyeye.common.constans.Constants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.ActModelDao;
import com.skyeye.eve.dao.ActUserProcessInstanceIdDao;
import com.skyeye.jedis.JedisClientService;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ModelQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
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
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @ClassName: ActivitiModelServiceImpl
 * @Description: 工作流模型操作
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
	private ProcessEngineConfigurationImpl processEngineConfiguration;
    
    @Autowired
    private ActAssigneeService actAssigneeService;
    
    @Autowired
    private ActUserProcessInstanceIdDao actUserProcessInstanceIdDao;

    @Autowired
	public JedisClientService jedisClient;
    
    @Value("${IMAGES_PATH}")
	private String tPath;

    @Autowired
	private ActivityMapper activityMapper;

	@Autowired
	private ActModelDao actModelDao;

	@Autowired
	private ActivitiTaskService activitiTaskService;

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
	 * 启动流程
	 *
	 * @param str 前端传来的数据json串
	 * @param user 用户信息
	 * @param keyName 流程定义的key
	 * @param dataId 数据id
	 * @param approvalId 审批人id
	 * @return
	 * @throws Exception
	 */
	private String startProcess(String str, Map<String, Object> user, String keyName, String dataId, String approvalId) throws Exception {
		Map<String, Object> baseTask = JSONUtil.toBean(str, null);
		String userId = user.get("id").toString();
		baseTask.put("createId", userId);//创建人id
		baseTask.put("createName", user.get("userName"));//创建人姓名
		// 业务对象
		Map<String, Object> varables = new HashMap<>();
		// form表单数据
		varables.put(ActivitiConstants.PROCESSINSTANCEID_TASK_VARABLES, baseTask);
		// 启动流程---流程图id，业务表id
		ProcessInstance process = runtimeService.startProcessInstanceByKey(keyName, varables);
		String processInstanceId = process.getProcessInstanceId();
		this.queryProHighLighted(processInstanceId);
		// 存储用户启动的流程
		saveActUserProInsId(processInstanceId, userId, keyName, dataId);

		// 设置第一个userTask任务的审批人
		activitiTaskService.setNextUserTaskApproval(processInstanceId, approvalId);
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
	@Override
	public void deleteProcessInRedisMation(String processInstanceId) {
		// 删除流程在redis中的存储信息
		jedisClient.delKeys(Constants.PROCESS_REDIS_CACHE_KEY + processInstanceId + "*");
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
	@Override
	public List<Map<String, Object>> getUpLeaveList(String approvedId, String approvedName, String opinion, boolean flag, Task task){
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
	 * 			jsonStr参数介绍
	 * 		 	* name: "",----标题
	 *          * value: "",----值
	 *          * orderBy: 1,----排序，值越大越往后
	 *          * showType: 1----展示类型：1.文本展示；2.附件展示；3.富文本展示；4.图片上传；5.表格展示
	 *          * proportion: 6----展示比例，前端界面百分比分为12份
	 *          * editableNodeId：可编辑节点Id
	 *          * editableNodeName：可编辑节点名称
	 *          * formItemType: 原始表单类型
	 *          * formItem：表单内容
	 *
	 * @param map
	 * @param user 用户信息
	 * @param id 数据id
	 * @param approvalId 审批人id
	 * @throws Exception
	 */
	@Override
	@ActivitiAndBaseTransaction(value = {"activitiTransactionManager", "transactionManager"})
	public void editActivitiModelToStartProcessByMap(Map<String, Object> map, Map<String, Object> user, String id, String approvalId) throws Exception {
		// 流程定义的key
		String keyName = map.get("keyName").toString();
		map.put("code", "-1");
		String message = "";
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
					message = startProcess(str, user, keyName, id, approvalId);
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
	 * 流程图高亮显示
	 *
	 * @param processInstanceId
	 * @throws Exception
	 */
	@Override
	public void queryProHighLighted(String processInstanceId) throws Exception {
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
