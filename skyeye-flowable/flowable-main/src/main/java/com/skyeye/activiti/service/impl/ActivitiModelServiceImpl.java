/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.activiti.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.skyeye.activiti.flowimg.FlowImgService;
import com.skyeye.activiti.mapper.ActivityMapper;
import com.skyeye.activiti.mapper.FlowReModelDao;
import com.skyeye.activiti.mapper.FlowReProcdefDao;
import com.skyeye.activiti.service.ActivitiModelService;
import com.skyeye.activiti.service.ActivitiTaskService;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.common.constans.ActivitiConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.object.PutObject;
import com.skyeye.common.tenant.TenantTypeEnum;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.FileUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.entity.ActFlowMation;
import com.skyeye.eve.flowable.entity.FlowableSubData;
import com.skyeye.exception.CustomException;
import com.skyeye.userprocess.service.ActUserProcessService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.editor.constants.ModelDataJsonConstants;
import org.flowable.editor.language.json.converter.BpmnJsonConverter;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.impl.persistence.entity.ModelEntityImpl;
import org.flowable.engine.impl.persistence.entity.ProcessDefinitionEntityImpl;
import org.flowable.engine.repository.*;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: ActivitiModelServiceImpl
 * @Description: 工作流模型操作
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/17 20:53
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Slf4j
@Service
@SkyeyeService(name = "流程模型设计", groupName = "流程模型管理")
public class ActivitiModelServiceImpl implements ActivitiModelService {

    @Autowired
    private ProcessEngine processEngine;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Value("${IMAGES_PATH}")
    private String tPath;

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private ActivitiTaskService activitiTaskService;

    @Autowired
    private FlowImgService flowImgService;

    @Autowired
    private FlowReModelDao flowReModelDao;

    @Autowired
    private FlowReProcdefDao flowReProcdefDao;

    @Value("${skyeye.author}")
    private String author;

    @Autowired
    private ActUserProcessService actUserProcessService;

    @Value("${skyeye.tenant.enable}")
    private boolean tenantEnable;

    /**
     * 新建一个空模型
     *
     * @param modelName 模型名称
     * @param modelKey  流程id
     * @return
     */
    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public String insertNewActivitiModel(String modelName, String modelKey) {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        // 初始化一个空模型
        Model model = repositoryService.newModel();
        // 设置一些默认信息
        ObjectNode modelNode = getMateInfo(modelName);

        model.setName(modelName);
        model.setKey(ToolUtil.getSurFaceId());
        model.setMetaInfo(modelNode.toString());
        if (tenantEnable) {
            String tenantId = TenantContext.getTenantId();
            model.setTenantId(tenantId);
        } else {
            model.setTenantId(TenantTypeEnum.PLATFORM.getCode());
        }

        ObjectNode editorNode = objectMapper.createObjectNode();
        editorNode.put("id", "canvas");
        editorNode.put("resourceId", "canvas");

        ObjectNode processProperties = getProcessProperties(modelName, modelKey, null);
        editorNode.put("properties", processProperties);

        ObjectNode stencilSetNode = objectMapper.createObjectNode();
        stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
        editorNode.put("stencilset", stencilSetNode);

        repositoryService.saveModel(model);
        try {
            repositoryService.addModelEditorSource(model.getId(), editorNode.toString().getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            throw new CustomException(e);
        }
        return model.getId();
    }

    private ObjectNode getProcessProperties(String modelName, String modelKey, ObjectNode processProperties) {
        if (processProperties == null) {
            processProperties = objectMapper.createObjectNode();
        }
        processProperties.put("process_author", author);
        processProperties.put("process_id", modelKey);
        processProperties.put("name", modelName);
        return processProperties;
    }

    private ObjectNode getMateInfo(String modelName) {
        ObjectNode modelNode = objectMapper.createObjectNode();
        modelNode.put(ModelDataJsonConstants.MODEL_NAME, modelName);
        modelNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, StringUtils.EMPTY);
        modelNode.put(ModelDataJsonConstants.MODEL_REVISION, CommonNumConstants.NUM_ONE);
        return modelNode;
    }

    /** metaInfo 中标记：已发布模型又改了草案，尚未重新部署 */
    private static final String META_UNPUBLISHED_DRAFT = "unpublishedDraft";

    private void markModelUnpublishedDraft(Model model) {
        if (model == null) {
            return;
        }
        try {
            ObjectNode modelJson = StrUtil.isBlank(model.getMetaInfo())
                ? objectMapper.createObjectNode()
                : (ObjectNode) objectMapper.readTree(model.getMetaInfo());
            if (StrUtil.isNotBlank(model.getDeploymentId())) {
                modelJson.put(META_UNPUBLISHED_DRAFT, true);
            }
            model.setMetaInfo(modelJson.toString());
        } catch (Exception e) {
            log.warn("标记未发布草案失败 modelId={}", model.getId(), e);
        }
    }

    private void clearModelUnpublishedDraft(Model model) {
        if (model == null || StrUtil.isBlank(model.getMetaInfo())) {
            return;
        }
        try {
            ObjectNode modelJson = (ObjectNode) objectMapper.readTree(model.getMetaInfo());
            modelJson.put(META_UNPUBLISHED_DRAFT, false);
            model.setMetaInfo(modelJson.toString());
        } catch (Exception e) {
            log.warn("清除未发布草案标记失败 modelId={}", model.getId(), e);
        }
    }

    private boolean isUnpublishedDraft(Model model) {
        if (model == null || StrUtil.isBlank(model.getDeploymentId()) || StrUtil.isBlank(model.getMetaInfo())) {
            return false;
        }
        try {
            JsonNode meta = objectMapper.readTree(model.getMetaInfo());
            JsonNode flag = meta.get(META_UNPUBLISHED_DRAFT);
            return flag != null && flag.asBoolean(false);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 设置流程模型信息
     *
     * @param actFlowList
     */
    @Override
    public void setActivitiModelList(List<Map<String, Object>> actFlowList) {
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        // 查询模型
        List<String> modelIds = actFlowList.stream().map(bean -> bean.get("modelId").toString()).collect(Collectors.toList());
        List<ModelEntityImpl> modelList = flowReModelDao.getModelByIds(modelIds, tenantId);
        Map<String, Model> modelMap = modelList.stream().collect(Collectors.toMap(bean -> bean.getId(), bean -> bean));
        // 查询发布流程
        List<String> deploymentIds = modelList.stream().filter(bean -> !ToolUtil.isBlank(bean.getDeploymentId())).map(bean -> bean.getDeploymentId()).collect(Collectors.toList());
        List<ProcessDefinitionEntityImpl> procdefList = flowReProcdefDao.getProcdefByDeploymentIds(deploymentIds);
        Map<String, ProcessDefinition> procdefMap = procdefList.stream().collect(Collectors.toMap(bean -> bean.getDeploymentId(), bean -> bean));

        actFlowList.forEach(bean -> fillActivitiModelInfo(bean, modelMap, procdefMap));
    }

    @Override
    public void setActivitiModelListForTenant(List<Map<String, Object>> actFlowList) {
        // 查询模型
        List<ModelEntityImpl> modelList = flowReModelDao.getModelByIdsForTenant(actFlowList);
        Map<String, Model> modelMap = modelList.stream().collect(Collectors.toMap(bean -> bean.getId(), bean -> bean));
        // 查询发布流程
        List<String> deploymentIds = modelList.stream().filter(bean -> !ToolUtil.isBlank(bean.getDeploymentId())).map(bean -> bean.getDeploymentId()).collect(Collectors.toList());
        List<ProcessDefinitionEntityImpl> procdefList = flowReProcdefDao.getProcdefByDeploymentIds(deploymentIds);
        Map<String, ProcessDefinition> procdefMap = procdefList.stream().collect(Collectors.toMap(bean -> bean.getDeploymentId(), bean -> bean));

        actFlowList.forEach(bean -> fillActivitiModelInfo(bean, modelMap, procdefMap));
    }

    private void fillActivitiModelInfo(Map<String, Object> bean, Map<String, Model> modelMap,
                                       Map<String, ProcessDefinition> procdefMap) {
        Model model = modelMap.get(bean.get("modelId").toString());
        if (model == null) {
            bean.put("model", new HashMap<>());
            bean.put("procdef", new HashMap<>());
            return;
        }
        Map<String, Object> modelInfo = BeanUtil.beanToMap(model);
        modelInfo.put("hasUnpublishedChanges", isUnpublishedDraft(model));
        bean.put("model", modelInfo);
        ProcessDefinition processDefinition = procdefMap.get(model.getDeploymentId());
        bean.put("procdef", processDefinition != null ? BeanUtil.beanToMap(processDefinition) : new HashMap<>());
    }

    /**
     * 发布模型为流程定义
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void editActivitiModelToDeploy(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String modelId = map.get("modelId").toString();
        // 获取模型
        Model modelData = repositoryService.getModel(modelId);
        byte[] bytes = repositoryService.getModelEditorSource(modelData.getId());
        if (bytes == null) {
            outputObject.setreturnMessage("模型数据为空，请先设计流程并成功保存，再进行发布。");
            return;
        }
        try {
            JsonNode modelNode = new ObjectMapper().readTree(bytes);
            log.info("modelNode = {}", modelNode.toString());
            BpmnModel model = new BpmnJsonConverter().convertToBpmnModel(modelNode);
            if (model.getProcesses().size() == 0) {
                outputObject.setreturnMessage("数据模型不符要求，请至少设计一条主线流程。");
                return;
            }
            byte[] bpmnBytes = new BpmnXMLConverter().convertToXML(model);

            // 发布流程
            String processName = modelData.getName() + ".bpmn20.xml";
            Deployment deployment;
            if (tenantEnable) {
                // 添加租户信息
                processName = modelData.getTenantId() + ":" + processName;
                deployment = repositoryService.createDeployment()
                    .name(modelData.getName())
                    .tenantId(modelData.getTenantId())
                    .addString(processName, new String(bpmnBytes, "UTF-8")).deploy();
            } else {
                processName = TenantTypeEnum.PLATFORM.getCode() + ":" + processName;
                deployment = repositoryService.createDeployment()
                    .name(modelData.getName())
                    .tenantId(TenantTypeEnum.PLATFORM.getCode())
                    .addString(processName, new String(bpmnBytes, "UTF-8")).deploy();
            }

            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();
            // 模型版本与流程定义版本对齐（仅发布/版本升级时变更）
            modelData.setVersion(processDefinition.getVersion());
            modelData.setDeploymentId(deployment.getId());
            clearModelUnpublishedDraft(modelData);
            repositoryService.saveModel(modelData);

            if (!StrUtil.isBlank(processName)) {
                activityMapper.updateProcessDefinitionName(processName, processDefinition.getId(), deployment.getTenantId());
                log.info("流程模型【{}】没有配置流程名称，默认使用流程模型名称作为流程名称", processName);
            }
            log.info("流程【{}】成功发布，版本={}", processName, processDefinition.getVersion());
        } catch (Exception ee) {
            throw new CustomException(ee);
        }
    }

    @Override
    public String startProcess(FlowableSubData flowableSubData, ActFlowMation actFlowMation) {
        if (!judgeProcessKeyIsLive(actFlowMation)) {
            throw new CustomException("任务发起失败,不存在该流程模型.");
        }
        log.info("approvalId is : " + flowableSubData.getApprovalId());
        // 业务对象
        Map<String, Object> varables = new HashMap<>();
        // 默认设置审批人为会签人
        varables.put(ActivitiConstants.ASSIGNEE_USER_LIST, Arrays.asList(flowableSubData.getApprovalId()));
        log.info("startProcessInstanceByKey start.");

        String tenantId = TenantTypeEnum.PLATFORM.getCode();
        if (tenantEnable) {
            // 添加租户信息
            tenantId = TenantContext.getTenantId();
        }
        ProcessInstance process = runtimeService.createProcessInstanceBuilder()
            .processDefinitionKey(actFlowMation.getModelKey())
            .tenantId(tenantId)
            .variables(varables)
            .start();
        // 启动流程---流程图id，业务表id
        String processInstanceId = process.getProcessInstanceId();

        queryProHighLighted(processInstanceId);
        // 存储用户启动的流程
        actUserProcessService.saveActUserProcess(processInstanceId, actFlowMation.getId(), flowableSubData.getObjectId(), flowableSubData.getObjectKey(),
            flowableSubData.getAppId());

        // 设置第一个userTask任务的审批人
        activitiTaskService.setNextUserTaskApproval(processInstanceId, flowableSubData.getApprovalId());
        log.info("start process success, processInstanceId is: {}", processInstanceId);
        return processInstanceId;
    }

    /**
     * 判断该key的流程是否还存在
     *
     * @param actFlowMation skyeye的工作流模型
     * @return
     */
    private boolean judgeProcessKeyIsLive(ActFlowMation actFlowMation) {
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery().processDefinitionKey(actFlowMation.getModelKey());
        if (tenantEnable) {
            processDefinitionQuery.processDefinitionTenantId(tenantId);
        }
        List<ProcessDefinition> processDefinition = processDefinitionQuery.list();
        if (processDefinition != null) {
            List<String> deploymentIds = processDefinition.stream().map(p -> p.getDeploymentId()).collect(Collectors.toList());
            ModelQuery modelQuery = repositoryService.createModelQuery();
            if (tenantEnable) {
                modelQuery.modelTenantId(tenantId);
            }
            List<Model> beans = modelQuery.latestVersion().orderByLastUpdateTime().desc().list();
            beans = beans.stream().filter(bean -> deploymentIds.contains(bean.getDeploymentId())).collect(Collectors.toList());
            if (beans != null && !beans.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取指定任务节点的审批信息
     *
     * @param approvedId   审批人id
     * @param approvedName 审批人名字
     * @param opinion      审批意见
     * @param flag         该节点是否审批通过，true:通过，false:不通过，null: 委派/转办历史
     * @param task         任务
     * @return
     */
    @Override
    public List<Map<String, Object>> getUpLeaveList(String approvedId, String approvedName, String opinion, Boolean flag, Task task, int type) {
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
        leaveOpinion.put("type", type);
        // 任务id
        leaveOpinion.put("taskId", task.getId());
        // 获取该任务所有的流程变量
        Object o = runtimeService.getVariable(task.getProcessInstanceId(), ActivitiConstants.PROCESSINSTANCEID_TASK_LEAVE_OPINION_LIST_VARABLES);
        List<Map<String, Object>> leaveList = new ArrayList<>();
        if (o != null) {
            leaveList = (List<Map<String, Object>>) o;
        }
        leaveList.add(leaveOpinion);
        return leaveList;
    }

    /**
     * 流程图高亮显示
     *
     * @param processInstanceId
     */
    @Override
    public void queryProHighLighted(String processInstanceId) {
        byte[] b = flowImgService.generateImageByProcInstId(processInstanceId);
        ByteArrayInputStream imageStream = new ByteArrayInputStream(b);
        FileOutputStream fos = null;
        try {
            BufferedImage bi = ImageIO.read(imageStream);
            FileUtil.createDirs(tPath + "upload/activiti/");

            File file = new File(tPath + "upload/activiti/" + processInstanceId + ".png");
            if (!file.exists()) {
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            ImageIO.write(bi, "png", fos);
        } catch (Exception ee) {
            throw new CustomException(ee);
        } finally {
            FileUtil.close(fos);
            FileUtil.close(imageStream);
        }
    }

    /**
     * 删除模型
     *
     * @param modelId 模型id
     */
    @Override
    public void deleteActivitiModelById(String modelId) {
        try {
            ModelQuery modelQuery = repositoryService.createModelQuery().modelId(modelId);
            Model model = modelQuery.singleResult();
            String deploymentId = model.getDeploymentId();
            // 已发布的模型需要删除流程定义和流程发布表
            if (StrUtil.isNotBlank(deploymentId)) {
                repositoryService.deleteDeployment(deploymentId);
            }
            // 删除流程模型表
            repositoryService.deleteModel(modelId);
        } catch (Exception e) {
            throw new CustomException("删除失败");
        }
    }

    /**
     * 取消发布
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void deleteReleasedActivitiModelById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String deploymentId = map.get("deploymentId").toString();
        try {
            repositoryService.deleteDeployment(deploymentId, true);
        } catch (Exception e) {
            outputObject.setreturnMessage("存在正在进行的流程，无法取消发布。");
        }
    }

    /**
     * 导出model的xml文件
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void editApprovalActivitiTaskListByUserId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String modelId = map.get("modelId").toString();
        Model modelData = repositoryService.getModel(modelId);
        BpmnJsonConverter jsonConverter = new BpmnJsonConverter();
        try {
            JsonNode editorNode = new ObjectMapper().readTree(repositoryService.getModelEditorSource(modelData.getId()));
            BpmnModel bpmnModel = jsonConverter.convertToBpmnModel(editorNode);
            BpmnXMLConverter xmlConverter = new BpmnXMLConverter();
            byte[] bpmnBytes = xmlConverter.convertToXML(bpmnModel);
            ByteArrayInputStream in = new ByteArrayInputStream(bpmnBytes);
            String filename = bpmnModel.getMainProcess().getId() + ".bpmn20.xml";
            PutObject.getResponse().setHeader("REQUESTMATION", "DOWNLOAD");
            PutObject.getResponse().setHeader("Content-Disposition", "attachment;filename=" + filename);
            // 1.设置文件ContentType类型，这样设置，会自动判断下载文件类型
            PutObject.getResponse().setContentType("multipart/form-data");
            BufferedOutputStream out1 = new BufferedOutputStream(PutObject.getResponse().getOutputStream());
            int len = 0;
            while ((len = in.read()) != -1) {
                out1.write(len);
                out1.flush();
            }
        } catch (IOException ee) {
            throw new CustomException(ee);
        }
    }

    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void editModelByModelId(String modelId, String modelName, String modelKey) {
        Model model = repositoryService.getModel(modelId);

        if (model != null) {
            model.setName(modelName);
            markModelUnpublishedDraft(model);
            repositoryService.saveModel(model);

            try {
                // 获取模型配置信息并重新设置模型key
                ObjectNode editorJsonNode = (ObjectNode) objectMapper.readTree(new String(repositoryService.getModelEditorSource(model.getId()), "utf-8"));
                ObjectNode processProperties = (ObjectNode) editorJsonNode.get("properties");
                processProperties = getProcessProperties(modelName, modelKey, processProperties);
                editorJsonNode.set("properties", processProperties);

                repositoryService.addModelEditorSource(model.getId(), editorJsonNode.toString().getBytes("utf-8"));
            } catch (Exception e) {
                log.error("Error get model JSON", e);
                throw new FlowableException("Error get model JSON", e);
            }
        }
    }

    /**
     * 复制现有模型
     *
     * @param sourceModelId 源模型ID
     * @param newModelName  新模型名称
     * @param newModelKey   新模型Key
     * @return 新模型的ID
     */
    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public String copyActivitiModel(String sourceModelId, String newModelName, String newModelKey) {
        try {
            // 获取源模型
            Model sourceModel = repositoryService.getModel(sourceModelId);
            if (sourceModel == null) {
                throw new CustomException("源模型不存在，无法复制");
            }

            // 获取源模型的编辑器源码
            byte[] sourceEditorSource = repositoryService.getModelEditorSource(sourceModelId);
            if (sourceEditorSource == null) {
                throw new CustomException("源模型数据为空，无法复制");
            }

            // 创建新模型
            Model newModel = repositoryService.newModel();

            // 设置新模型的基本信息
            newModel.setName(newModelName);
            newModel.setKey(ToolUtil.getSurFaceId());

            // 设置租户信息
            if (tenantEnable) {
                String tenantId = TenantContext.getTenantId();
                newModel.setTenantId(tenantId);
            } else {
                newModel.setTenantId(TenantTypeEnum.PLATFORM.getCode());
            }

            // 复制源模型的元信息并更新
            ObjectNode sourceMetaInfo = (ObjectNode) objectMapper.readTree(sourceModel.getMetaInfo());
            ObjectNode newMetaInfo = sourceMetaInfo.deepCopy();
            newMetaInfo.put(ModelDataJsonConstants.MODEL_NAME, newModelName);
            newMetaInfo.put(ModelDataJsonConstants.MODEL_DESCRIPTION, StringUtils.EMPTY);
            newMetaInfo.put(ModelDataJsonConstants.MODEL_REVISION, CommonNumConstants.NUM_ONE);
            newModel.setMetaInfo(newMetaInfo.toString());

            // 保存新模型
            repositoryService.saveModel(newModel);

            // 复制并修改编辑器源码
            String sourceEditorJson = new String(sourceEditorSource, "utf-8");
            ObjectNode editorJsonNode = (ObjectNode) objectMapper.readTree(sourceEditorJson);

            // 更新流程属性
            ObjectNode processProperties = (ObjectNode) editorJsonNode.get("properties");
            if (processProperties != null) {
                processProperties = getProcessProperties(newModelName, newModelKey, processProperties);
                editorJsonNode.set("properties", processProperties);
            }

            // 保存新模型的编辑器源码
            repositoryService.addModelEditorSource(newModel.getId(), editorJsonNode.toString().getBytes("utf-8"));

            log.info("成功复制模型，源模型ID: {}, 新模型ID: {}, 新模型名称: {}", sourceModelId, newModel.getId(), newModelName);
            return newModel.getId();

        } catch (Exception e) {
            log.error("复制模型失败，源模型ID: {}, 错误信息: {}", sourceModelId, e.getMessage(), e);
            throw new CustomException("复制模型失败: " + e.getMessage());
        }
    }

    /**
     * 根据BPMN XML创建模型到指定租户
     *
     * @param bpmnXml   BPMN XML内容
     * @param modelName 模型名称
     * @param modelKey  模型Key
     * @param tenantId  租户ID
     * @return 新模型的ID
     */
    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public String createModelFromBpmnXml(String bpmnXml, String modelName, String modelKey, String tenantId) {
        try {
            log.info("开始根据BPMN XML创建模型，模型名称: {}, 模型Key: {}, 租户ID: {}", modelName, modelKey, tenantId);

            Model model = repositoryService.newModel();
            model.setName(modelName);
            model.setKey(ToolUtil.getSurFaceId());
            if (StrUtil.isNotBlank(tenantId)) {
                model.setTenantId(tenantId);
            } else {
                model.setTenantId(TenantTypeEnum.PLATFORM.getCode());
            }
            model.setMetaInfo(getMateInfo(modelName).toString());
            repositoryService.saveModel(model);

            writeEditorSourceFromBpmnXml(model, bpmnXml, modelName, modelKey);

            log.info("成功根据BPMN XML创建模型，模型ID: {}, 模型名称: {}, 租户ID: {}", model.getId(), modelName, tenantId);
            return model.getId();
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("根据BPMN XML创建模型失败，模型名称: {}, 错误信息: {}", modelName, e.getMessage(), e);
            throw new CustomException("根据BPMN XML创建模型失败: " + e.getMessage());
        }
    }

    /**
     * 覆盖已有模型编辑器源：与 createModelFromBpmnXml、设计器保存同一套
     * repositoryService.saveModel + addModelEditorSource（不走发布）。
     */
    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void updateModelFromBpmnXml(String modelId, String bpmnXml, String modelName, String modelKey) {
        if (StrUtil.isBlank(modelId)) {
            throw new CustomException("模型ID不能为空");
        }
        if (StrUtil.isBlank(bpmnXml)) {
            throw new CustomException("BPMN XML不能为空");
        }
        Model model = repositoryService.getModel(modelId);
        if (model == null) {
            throw new CustomException("流程模型不存在");
        }
        // 已发布也可改编辑器草案（与设计器手工保存一致），不自动重新部署；线上定义仍以当前 deployment 为准
        String name = StrUtil.blankToDefault(modelName, model.getName());
        String key = StrUtil.blankToDefault(modelKey, model.getKey());
        try {
            model.setName(name);
            ObjectNode metaInfo = getMateInfo(name);
            if (StrUtil.isNotBlank(model.getDeploymentId())) {
                metaInfo.put(META_UNPUBLISHED_DRAFT, true);
            }
            model.setMetaInfo(metaInfo.toString());
            writeEditorSourceFromBpmnXml(model, bpmnXml, name, key);
            log.info("已用 BPMN 覆盖模型编辑器源 modelId={}, published={}", modelId, StrUtil.isNotBlank(model.getDeploymentId()));
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("覆盖模型编辑器源失败 modelId={}", modelId, e);
            throw new CustomException("写入流程草案失败: " + e.getMessage());
        }
    }

    /**
     * BPMN XML → 编辑器 JSON → saveModel + addModelEditorSource（与租户同步建模型同源）
     */
    private void writeEditorSourceFromBpmnXml(Model model, String bpmnXml, String modelName, String modelKey) throws Exception {
        BpmnXMLConverter xmlConverter = new BpmnXMLConverter();
        InputStream xmlInputStream = new ByteArrayInputStream(bpmnXml.getBytes("UTF-8"));
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(xmlInputStream);
        BpmnModel bpmnModel = xmlConverter.convertToBpmnModel(xmlStreamReader);
        if (bpmnModel.getProcesses().isEmpty()) {
            throw new CustomException("BPMN XML中没有有效的流程定义");
        }

        BpmnJsonConverter jsonConverter = new BpmnJsonConverter();
        ObjectNode editorJsonNode = jsonConverter.convertToJson(bpmnModel);
        ObjectNode processProperties = getProcessProperties(modelName, modelKey, null);
        editorJsonNode.put("properties", processProperties);

        repositoryService.saveModel(model);
        repositoryService.addModelEditorSource(model.getId(), editorJsonNode.toString().getBytes("utf-8"));
    }

    /**
     * AI 流程草案：拼 BPMN 后复用 {@link #updateModelFromBpmnXml}（同 createModelFromBpmnXml 写入方式）。
     * 结构对齐 workflow-template：start → N×userTask → exclusiveGateway → 同意/拒绝 → end
     */
    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void applyAiDraftToModel(String modelId, String processName, String modelKey, List<String> approveStepNames) {
        if (approveStepNames == null || approveStepNames.isEmpty()) {
            throw new CustomException("请至少提供一个审批节点");
        }
        List<String> steps = approveStepNames.stream()
            .map(StrUtil::trim)
            .filter(StrUtil::isNotBlank)
            .collect(Collectors.toList());
        if (steps.isEmpty()) {
            throw new CustomException("请至少提供一个审批节点");
        }
        if (steps.size() > 20) {
            throw new CustomException("审批节点过多，请控制在 20 个以内");
        }
        Model model = repositoryService.getModel(modelId);
        if (model == null) {
            throw new CustomException("流程模型不存在");
        }
        String name = StrUtil.blankToDefault(processName, model.getName());
        String key = StrUtil.blankToDefault(modelKey, model.getKey());
        String bpmnXml = buildApproveChainBpmnXml(key, name, steps);
        updateModelFromBpmnXml(modelId, bpmnXml, name, key);
        log.info("AI 流程草案已写入模型 modelId={}, steps={}", modelId, steps.size());
    }

    @Override
    public void queryActivitiModelVersionList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String modelId = map.get("modelId").toString();
        String modelKey = map.get("modelKey").toString();
        Model model = repositoryService.getModel(modelId);
        if (model == null) {
            outputObject.setreturnMessage("流程模型不存在");
            return;
        }
        ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery()
            .processDefinitionKey(modelKey)
            .orderByProcessDefinitionVersion()
            .desc();
        if (tenantEnable) {
            query.processDefinitionTenantId(TenantContext.getTenantId());
        } else if (StrUtil.isNotBlank(model.getTenantId())) {
            query.processDefinitionTenantId(model.getTenantId());
        }
        List<ProcessDefinition> definitions = query.list();
        String currentDeploymentId = model.getDeploymentId();
        Set<String> deploymentIds = definitions.stream()
            .map(ProcessDefinition::getDeploymentId)
            .filter(StrUtil::isNotBlank)
            .collect(Collectors.toSet());
        Map<String, Date> deployTimeMap = new HashMap<>();
        for (String deploymentId : deploymentIds) {
            Deployment deployment = repositoryService.createDeploymentQuery().deploymentId(deploymentId).singleResult();
            if (deployment != null && deployment.getDeploymentTime() != null) {
                deployTimeMap.put(deploymentId, deployment.getDeploymentTime());
            }
        }
        List<Map<String, Object>> beans = new ArrayList<>();
        for (ProcessDefinition definition : definitions) {
            Map<String, Object> row = new HashMap<>();
            row.put("id", definition.getId());
            row.put("name", definition.getName());
            row.put("key", definition.getKey());
            row.put("version", definition.getVersion());
            row.put("deploymentId", definition.getDeploymentId());
            row.put("current", StrUtil.isNotBlank(currentDeploymentId)
                && currentDeploymentId.equals(definition.getDeploymentId()));
            Date deployTime = deployTimeMap.get(definition.getDeploymentId());
            row.put("deploymentTime", deployTime == null
                ? StrUtil.EMPTY
                : DateUtil.formatDate2Str(deployTime, DateUtil.YYYY_MM_DD_HH_MM_SS));
            beans.add(row);
        }
        Map<String, Object> extra = new HashMap<>();
        extra.put("hasUnpublishedChanges", isUnpublishedDraft(model));
        extra.put("currentDeploymentId", currentDeploymentId);
        outputObject.setBean(extra);
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void deleteActivitiModelVersion(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String modelId = map.get("modelId").toString();
        String deploymentId = map.get("deploymentId").toString();
        Model model = repositoryService.getModel(modelId);
        if (model == null) {
            outputObject.setreturnMessage("流程模型不存在");
            return;
        }
        if (StrUtil.isBlank(deploymentId)) {
            outputObject.setreturnMessage("部署id不能为空");
            return;
        }
        if (deploymentId.equals(model.getDeploymentId())) {
            outputObject.setreturnMessage("当前线上版本不可删除，请使用取消发布");
            return;
        }
        try {
            repositoryService.deleteDeployment(deploymentId);
            log.info("已删除历史流程定义版本 modelId={}, deploymentId={}", modelId, deploymentId);
        } catch (Exception e) {
            log.warn("删除历史流程定义版本失败 modelId={}, deploymentId={}", modelId, deploymentId, e);
            outputObject.setreturnMessage("存在流程实例，无法删除");
        }
    }

    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void discardActivitiModelDraft(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String modelId = map.get("modelId").toString();
        Model model = repositoryService.getModel(modelId);
        if (model == null) {
            outputObject.setreturnMessage("流程模型不存在");
            return;
        }
        if (StrUtil.isBlank(model.getDeploymentId())) {
            outputObject.setreturnMessage("模型未发布，无线上版本可还原");
            return;
        }
        if (!isUnpublishedDraft(model)) {
            outputObject.setreturnMessage("当前没有未发布修改");
            return;
        }
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
            .deploymentId(model.getDeploymentId())
            .singleResult();
        if (processDefinition == null) {
            outputObject.setreturnMessage("当前发布的流程定义不存在");
            return;
        }
        try {
            BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
            if (bpmnModel == null || bpmnModel.getProcesses().isEmpty()) {
                outputObject.setreturnMessage("当前发布的流程定义为空，无法还原");
                return;
            }
            byte[] bpmnBytes = new BpmnXMLConverter().convertToXML(bpmnModel);
            String bpmnXml = new String(bpmnBytes, "UTF-8");
            clearModelUnpublishedDraft(model);
            writeEditorSourceFromBpmnXml(model, bpmnXml, model.getName(), processDefinition.getKey());
            log.info("已丢弃未发布草案并还原为线上版本 modelId={}, deploymentId={}", modelId, model.getDeploymentId());
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("丢弃未发布草案失败 modelId={}", modelId, e);
            throw new CustomException("删除未发布修改失败: " + e.getMessage());
        }
    }

    /**
     * 生成与租户模板同构的串行审批 BPMN XML。
     * 必须带齐 BPMNShape + BPMNEdge（含 waypoint），否则 BpmnJsonConverter 转序列流会 NPE，设计器也看不到连线。
     */
    private String buildApproveChainBpmnXml(String processId, String processName, List<String> stepNames) {
        String safeProcessId = escapeXml(processId);
        String safeProcessName = escapeXml(processName);
        String startId = newSid();
        String gatewayId = newSid();
        String passId = newSid();
        String rejectId = newSid();
        String endId = newSid();

        List<String> taskIds = new ArrayList<>();
        for (int i = 0; i < stepNames.size(); i++) {
            taskIds.add(newSid());
        }

        // flowId -> [sourceId, targetId]；边 waypoint 按节点 bounds 推算
        List<String[]> flows = new ArrayList<>();
        Map<String, double[]> bounds = new LinkedHashMap<>();

        StringBuilder process = new StringBuilder();
        process.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        process.append("<definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" ");
        process.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
        process.append("xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" ");
        process.append("xmlns:flowable=\"http://flowable.org/bpmn\" ");
        process.append("xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" ");
        process.append("xmlns:omgdc=\"http://www.omg.org/spec/DD/20100524/DC\" ");
        process.append("xmlns:omgdi=\"http://www.omg.org/spec/DD/20100524/DI\" ");
        process.append("typeLanguage=\"http://www.w3.org/2001/XMLSchema\" ");
        process.append("expressionLanguage=\"http://www.w3.org/1999/XPath\" ");
        process.append("targetNamespace=\"http://www.activiti.org/processdef\">\n");
        process.append("  <process id=\"").append(safeProcessId).append("\" name=\"").append(safeProcessName)
            .append("\" isExecutable=\"true\">\n");
        process.append("    <startEvent id=\"").append(startId).append("\"></startEvent>\n");

        for (int i = 0; i < stepNames.size(); i++) {
            String taskId = taskIds.get(i);
            process.append("    <userTask id=\"").append(taskId).append("\" name=\"")
                .append(escapeXml(stepNames.get(i))).append("\"></userTask>\n");
            String from = i == 0 ? startId : taskIds.get(i - 1);
            String flowId = newSid();
            process.append("    <sequenceFlow id=\"").append(flowId).append("\" sourceRef=\"")
                .append(from).append("\" targetRef=\"").append(taskId).append("\"></sequenceFlow>\n");
            flows.add(new String[]{flowId, from, taskId});
        }

        String lastTaskId = taskIds.get(taskIds.size() - 1);
        process.append("    <exclusiveGateway id=\"").append(gatewayId).append("\"></exclusiveGateway>\n");
        String toGw = newSid();
        process.append("    <sequenceFlow id=\"").append(toGw).append("\" sourceRef=\"")
            .append(lastTaskId).append("\" targetRef=\"").append(gatewayId).append("\"></sequenceFlow>\n");
        flows.add(new String[]{toGw, lastTaskId, gatewayId});

        process.append("    <serviceTask id=\"").append(passId)
            .append("\" name=\"同意\" flowable:class=\"com.skyeye.listener.ApprovalIsEndListener\">\n");
        process.append("      <extensionElements>\n");
        process.append("        <flowable:field name=\"state\"><flowable:string><![CDATA[pass]]></flowable:string></flowable:field>\n");
        process.append("      </extensionElements>\n");
        process.append("    </serviceTask>\n");
        String passFlow = newSid();
        process.append("    <sequenceFlow id=\"").append(passFlow).append("\" name=\"同意\" sourceRef=\"")
            .append(gatewayId).append("\" targetRef=\"").append(passId).append("\">\n");
        process.append("      <conditionExpression xsi:type=\"tFormalExpression\"><![CDATA[${flag==1}]]></conditionExpression>\n");
        process.append("    </sequenceFlow>\n");
        flows.add(new String[]{passFlow, gatewayId, passId});

        process.append("    <serviceTask id=\"").append(rejectId)
            .append("\" name=\"拒绝\" flowable:class=\"com.skyeye.listener.ApprovalIsEndListener\">\n");
        process.append("      <extensionElements>\n");
        process.append("        <flowable:field name=\"state\"><flowable:string><![CDATA[onpass]]></flowable:string></flowable:field>\n");
        process.append("      </extensionElements>\n");
        process.append("    </serviceTask>\n");
        String rejectFlow = newSid();
        process.append("    <sequenceFlow id=\"").append(rejectFlow).append("\" name=\"拒绝\" sourceRef=\"")
            .append(gatewayId).append("\" targetRef=\"").append(rejectId).append("\">\n");
        process.append("      <conditionExpression xsi:type=\"tFormalExpression\"><![CDATA[${flag==2}]]></conditionExpression>\n");
        process.append("    </sequenceFlow>\n");
        flows.add(new String[]{rejectFlow, gatewayId, rejectId});

        process.append("    <endEvent id=\"").append(endId).append("\"></endEvent>\n");
        String passEnd = newSid();
        process.append("    <sequenceFlow id=\"").append(passEnd).append("\" sourceRef=\"")
            .append(passId).append("\" targetRef=\"").append(endId).append("\"></sequenceFlow>\n");
        flows.add(new String[]{passEnd, passId, endId});
        String rejectEnd = newSid();
        process.append("    <sequenceFlow id=\"").append(rejectEnd).append("\" sourceRef=\"")
            .append(rejectId).append("\" targetRef=\"").append(endId).append("\"></sequenceFlow>\n");
        flows.add(new String[]{rejectEnd, rejectId, endId});
        process.append("  </process>\n");

        // 简易横向布局（与 workflow-template 同构）
        double startX = 100;
        double y = 200;
        bounds.put(startId, new double[]{startX, y + 25, 30, 30});
        double x = startX + 80;
        for (String taskId : taskIds) {
            bounds.put(taskId, new double[]{x, y, 100, 80});
            x += 140;
        }
        bounds.put(gatewayId, new double[]{x, y + 20, 40, 40});
        double branchX = x + 80;
        bounds.put(passId, new double[]{branchX, y - 90, 100, 80});
        bounds.put(rejectId, new double[]{branchX, y + 90, 100, 80});
        bounds.put(endId, new double[]{branchX + 140, y + 26, 28, 28});

        process.append("  <bpmndi:BPMNDiagram id=\"BPMNDiagram_").append(safeProcessId).append("\">\n");
        process.append("    <bpmndi:BPMNPlane bpmnElement=\"").append(safeProcessId)
            .append("\" id=\"BPMNPlane_").append(safeProcessId).append("\">\n");
        for (Map.Entry<String, double[]> entry : bounds.entrySet()) {
            double[] b = entry.getValue();
            appendShape(process, entry.getKey(), b[0], b[1], b[2], b[3]);
        }
        for (String[] flow : flows) {
            appendEdge(process, flow[0], bounds.get(flow[1]), bounds.get(flow[2]));
        }
        process.append("    </bpmndi:BPMNPlane>\n");
        process.append("  </bpmndi:BPMNDiagram>\n");
        process.append("</definitions>");
        return process.toString();
    }

    private void appendShape(StringBuilder sb, String elementId, double x, double y, double w, double h) {
        sb.append("      <bpmndi:BPMNShape bpmnElement=\"").append(elementId)
            .append("\" id=\"BPMNShape_").append(elementId).append("\">\n");
        sb.append("        <omgdc:Bounds height=\"").append(h).append("\" width=\"").append(w)
            .append("\" x=\"").append(x).append("\" y=\"").append(y).append("\"></omgdc:Bounds>\n");
        sb.append("      </bpmndi:BPMNShape>\n");
    }

    /** 源节点右中 → 目标节点左中；上下分支时加中间折点，避免线叠在一起 */
    private void appendEdge(StringBuilder sb, String flowId, double[] source, double[] target) {
        if (source == null || target == null) {
            return;
        }
        double sx = source[0] + source[2];
        double sy = source[1] + source[3] / 2;
        double tx = target[0];
        double ty = target[1] + target[3] / 2;
        sb.append("      <bpmndi:BPMNEdge bpmnElement=\"").append(flowId)
            .append("\" id=\"BPMNEdge_").append(flowId).append("\">\n");
        sb.append("        <omgdi:waypoint x=\"").append(sx).append("\" y=\"").append(sy).append("\"></omgdi:waypoint>\n");
        if (Math.abs(sy - ty) > 1) {
            double midX = (sx + tx) / 2;
            sb.append("        <omgdi:waypoint x=\"").append(midX).append("\" y=\"").append(sy).append("\"></omgdi:waypoint>\n");
            sb.append("        <omgdi:waypoint x=\"").append(midX).append("\" y=\"").append(ty).append("\"></omgdi:waypoint>\n");
        }
        sb.append("        <omgdi:waypoint x=\"").append(tx).append("\" y=\"").append(ty).append("\"></omgdi:waypoint>\n");
        sb.append("      </bpmndi:BPMNEdge>\n");
    }

    private static String newSid() {
        return "sid-" + UUID.randomUUID().toString().toUpperCase();
    }

    private static String escapeXml(String raw) {
        if (raw == null) {
            return "";
        }
        return raw.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;");
    }

}
