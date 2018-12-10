package com.skyeye.activiti.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.skyeye.activiti.entity.DeploymentResponse;
import com.skyeye.activiti.service.ActivitiModelService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ToolUtil;

@Service
public class ActivitiModelServiceImpl implements ActivitiModelService{
	
	@Autowired
    private ProcessEngine processEngine;
	
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private RepositoryService repositoryService;

    /**
	 * 
	     * @Title: insertNewActivitiModel
	     * @Description: 新建一个空模型
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void insertNewActivitiModel(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		RepositoryService repositoryService = processEngine.getRepositoryService();
        //初始化一个空模型
        Model model = repositoryService.newModel();
        //设置一些默认信息
        String name = "new-process";
        String description = "";
        int revision = 1;
        String key = "process";
        ObjectNode modelNode = objectMapper.createObjectNode();
        modelNode.put(ModelDataJsonConstants.MODEL_NAME, name);
        modelNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description);
        modelNode.put(ModelDataJsonConstants.MODEL_REVISION, revision);
 
        model.setName(name);
        model.setKey(key);
        model.setMetaInfo(modelNode.toString());
 
        repositoryService.saveModel(model);
        String id = model.getId();
        ObjectNode editorNode = objectMapper.createObjectNode();
        editorNode.put("id", "canvas");
        editorNode.put("resourceId", "canvas");
        ObjectNode stencilSetNode = objectMapper.createObjectNode();
        stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
        editorNode.put("stencilset", stencilSetNode);
        repositoryService.addModelEditorSource(id,editorNode.toString().getBytes("utf-8"));
        map.put("id", model.getId());
        outputObject.setBean(map);
	}
	
	/**
	 * 
	     * @Title: queryActivitiModelList
	     * @Description: 获取所有模型
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryActivitiModelList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		RepositoryService repositoryService = processEngine.getRepositoryService();
		List<Model> beans = repositoryService.createModelQuery()
				.listPage(Integer.parseInt(map.get("limit").toString()) * (Integer.parseInt(map.get("page").toString()) - 1), Integer.parseInt(map.get("limit").toString()));
		long count = repositoryService.createModelQuery().count() - repositoryService.createDeploymentQuery().count();
		List<Map<String, Object>> rows = new ArrayList<>();
		for(Model model : beans){
			if(ToolUtil.isBlank(model.getDeploymentId())){
				rows.add(ToolUtil.javaBean2Map(model));
			}
		}
		outputObject.setBeans(rows);
		outputObject.settotal(count);
	}

	/**
	 * 
	     * @Title: editActivitiModelToDeploy
	     * @Description: 发布模型为流程定义
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void editActivitiModelToDeploy(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String modelId = map.get("modelId").toString();
		//获取模型
        RepositoryService repositoryService = processEngine.getRepositoryService();
        Model modelData = repositoryService.getModel(modelId);
        byte[] bytes = repositoryService.getModelEditorSource(modelData.getId());
 
        if (bytes == null) {
        	outputObject.setreturnMessage("模型数据为空，请先设计流程并成功保存，再进行发布。");
        }else{
        	JsonNode modelNode = new ObjectMapper().readTree(bytes);
        	
        	BpmnModel model = new BpmnJsonConverter().convertToBpmnModel(modelNode);
        	if(model.getProcesses().size()==0){
        		outputObject.setreturnMessage("数据模型不符要求，请至少设计一条主线流程。");
        	}else{
        		byte[] bpmnBytes = new BpmnXMLConverter().convertToXML(model);
        		
        		//发布流程
        		String processName = modelData.getName() + ".bpmn20.xml";
        		Deployment deployment = repositoryService.createDeployment()
        				.name(modelData.getName())
        				.addString(processName, new String(bpmnBytes, "UTF-8"))
        				.deploy();
        		modelData.setDeploymentId(deployment.getId());
        		repositoryService.saveModel(modelData);
        	}
        }
	}

	/**
	 * 
	     * @Title: editActivitiModelToStartProcess
	     * @Description: 启动流程
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void editActivitiModelToStartProcess(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String keyName = map.get("keyName").toString();
		ProcessInstance process = processEngine.getRuntimeService().startProcessInstanceByKey(keyName);
		map.clear();
		map.put("id", process.getId());
		map.put("processDefinitionId", process.getProcessDefinitionId());
		outputObject.setBean(map);
	}

	/**
	 * 
	     * @Title: editActivitiModelToRun
	     * @Description: 提交任务
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void editActivitiModelToRun(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String processInstanceId = map.get("processInstanceId").toString();
		Task task = processEngine.getTaskService().createTaskQuery().processInstanceId(processInstanceId).singleResult();
        processEngine.getTaskService().complete(task.getId());
	}

	/**
	 * 
	     * @Title: deleteActivitiModelById
	     * @Description: 删除模型
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void deleteActivitiModelById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String id = map.get("id").toString();
		repositoryService.deleteModel(id);
	}

	/**
	 * 
	     * @Title: queryReleasedActivitiModelList
	     * @Description: 获取已经发布的模型
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryReleasedActivitiModelList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Deployment> deployments = repositoryService.createDeploymentQuery()
				.listPage(Integer.parseInt(map.get("limit").toString()) * (Integer.parseInt(map.get("page").toString()) - 1), Integer.parseInt(map.get("limit").toString()));
		long count = repositoryService.createDeploymentQuery().count();
		List<DeploymentResponse> list = new ArrayList<>();
        for(Deployment deployment: deployments){
            list.add(new DeploymentResponse(deployment));
        }
		List<Map<String, Object>> rows = new ArrayList<>();
		for(DeploymentResponse deploymentResponse : list){
			rows.add(ToolUtil.javaBean2Map(deploymentResponse));
		}
		outputObject.setBeans(rows);
		outputObject.settotal(count);
	}
    
	
}
