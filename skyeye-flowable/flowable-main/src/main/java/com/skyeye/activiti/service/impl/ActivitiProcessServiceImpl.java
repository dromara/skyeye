/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.activiti.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.google.common.base.Joiner;
import com.skyeye.activiti.cmd.nextusertask.FindFirstUserTaskByConditionCmd;
import com.skyeye.activiti.cmd.nextusertask.FindNextUserTaskNodeCmd;
import com.skyeye.activiti.cmd.rollback.RollbackCmd;
import com.skyeye.activiti.entity.NextTaskInfo;
import com.skyeye.activiti.service.ActivitiModelService;
import com.skyeye.activiti.service.ActivitiProcessService;
import com.skyeye.activiti.service.ActivitiTaskService;
import com.skyeye.activiti.service.ActivitiUserService;
import com.skyeye.common.constans.ActivitiConstants;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.entity.ActFlowMation;
import com.skyeye.eve.flowable.classenum.FormSubType;
import com.skyeye.eve.flowable.entity.FlowableSubData;
import com.skyeye.eve.service.ActFlowService;
import com.skyeye.eve.service.ActGroupUserService;
import com.skyeye.eve.service.IAuthUserService;
import com.skyeye.exception.CustomException;
import com.skyeye.userprocess.entity.ActUserProcess;
import com.skyeye.userprocess.service.ActUserProcessService;
import com.skyeye.util.FlowableUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.ManagementService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.repository.Model;
import org.flowable.engine.repository.ModelQuery;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.flowable.task.api.Task;
import org.nutz.trans.Trans;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: ActivitiProcessServiceImpl
 * @Description: 工作流流程相关操作
 * @author: skyeye云系列--卫志强
 * @date: 2021/12/2 21:29
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Slf4j
@Service
public class ActivitiProcessServiceImpl implements ActivitiProcessService {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ActivitiModelService activitiModelService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private IAuthUserService iAuthUserService;

    @Autowired
    private ActivitiTaskService activitiTaskService;

    @Autowired
    private ManagementService managementService;

    @Autowired
    private ActFlowService actFlowService;

    @Autowired
    private ActivitiService activitiService;

    @Autowired
    private ActUserProcessService actUserProcessService;

    @Autowired
    private ActGroupUserService actGroupUserService;

    @Autowired
    private ActivitiUserService activitiUserService;

    @Value("${skyeye.tenant.enable}")
    protected boolean tenantEnable;

    /**
     * 确保业务数据中带有发起人（用于 ${initiator} / 部门岗位规则）
     */
    private void ensureInitiatorInBusinessData(Map<String, Object> businessData, String processInstanceId) {
        if (businessData == null) {
            return;
        }
        Object existing = businessData.get(ActivitiConstants.INITIATOR);
        if (existing != null && StrUtil.isNotBlank(existing.toString())) {
            return;
        }
        if (StrUtil.isNotBlank(processInstanceId)) {
            try {
                Object initiator = runtimeService.getVariable(processInstanceId, ActivitiConstants.INITIATOR);
                if (initiator != null && StrUtil.isNotBlank(initiator.toString())) {
                    businessData.put(ActivitiConstants.INITIATOR, initiator.toString());
                    return;
                }
            } catch (Exception e) {
                log.warn("读取流程发起人变量失败: {}", e.getMessage());
            }
        }
        try {
            Map<String, Object> logParams = InputObject.getLogParamsStatic();
            if (logParams != null && logParams.get("id") != null) {
                businessData.put(ActivitiConstants.INITIATOR, logParams.get("id").toString());
            }
        } catch (Exception e) {
            log.warn("回填当前用户为发起人失败: {}", e.getMessage());
        }
    }

    /**
     * 流程挂起
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void updateProcessToHangUp(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String processInstanceId = map.get("processInstanceId").toString();
        // 根据一个流程实例的id挂起该流程实例
        runtimeService.suspendProcessInstanceById(processInstanceId);
    }

    /**
     * 流程激活
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void updateProcessToActivation(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String processInstanceId = map.get("processInstanceId").toString();
        // 根据一个流程实例的id激活该流程实例
        runtimeService.activateProcessInstanceById(processInstanceId);
    }

    /**
     * 流程撤回(撤回审批过的流程)
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void editProcessInstanceWithDraw(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> user = inputObject.getLogParams();
        String userId = user.get("id").toString();
        String processInstanceId = map.get("processInstanceId").toString();
        String hisTaskId = map.get("hisTaskId").toString();
        // 根据流程id查询代办任务中流程信息
        Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
        if (task == null) {
            outputObject.setreturnMessage("流程未启动或已执行完成，无法撤回");
            return;
        }
        // 撤回
        managementService.executeCommand(new RollbackCmd(hisTaskId, userId));
        // 绘制图像
        activitiModelService.queryProHighLighted(processInstanceId);
    }

    /**
     * 刷新流程图
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void editProcessInstancePicToRefresh(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        // 绘制图像
        activitiModelService.queryProHighLighted(map.get("processInstanceId").toString());
    }

    /**
     * 获取流程下一个节点的审批人列表
     *
     * <p><b>功能说明：</b></p>
     * <ul>
     *   <li>根据当前任务ID和业务数据，获取下一个用户任务节点</li>
     *   <li>根据业务数据判断条件表达式，找到符合条件的下一个节点</li>
     *   <li>返回下一个用户任务节点的所有审批人列表（包括 assignee、candidateUsers、candidateGroups）</li>
     * </ul>
     *
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>任务审批前，需要查看下一个节点的审批人</li>
     *   <li>根据业务数据，确定下一个审批路径</li>
     * </ul>
     *
     * <p><b>与 nextProcessDefaultApprover 的区别：</b></p>
     * <ul>
     *   <li>本方法不需要 flag 参数，用于审批前查询（还未确定审批结果）</li>
     *   <li>nextProcessDefaultApprover 需要 flag 参数，用于审批中查询（已确定审批结果）</li>
     * </ul>
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void nextProcessApprover(InputObject inputObject, OutputObject outputObject) {
        try {
            Map<String, Object> params = inputObject.getParams();
            String taskId = params.get("taskId").toString();
            String processInstanceId = params.get("processInstanceId").toString();
            String flag = params.get("flag").toString();

            // 获取业务数据
            Map<String, Object> businessData = new HashMap<>();
            String businessDataStr = params.get("businessData") != null ? params.get("businessData").toString() : null;
            if (!ToolUtil.isBlank(businessDataStr)) {
                businessData = JSONObject.fromObject(businessDataStr);
            }

            // 确保审批结果（flag）在业务数据中
            if (!ToolUtil.isBlank(flag)) {
                businessData.put("flag", flag);
            }
            ensureInitiatorInBusinessData(businessData, processInstanceId);

            // 获取下一个用户任务节点
            NextTaskInfo nextTaskInfo = this.getNextTaskInfo(taskId, businessData);

            if (nextTaskInfo != null && nextTaskInfo.getUserTask() != null) {
                UserTask nextUserTask = nextTaskInfo.getUserTask();
                // 使用批量查询方式获取审批人列表
                List<Map<String, Object>> userList = FlowableUtil.getNextTaskApproveBatch(
                    nextUserTask, businessData, actGroupUserService, iAuthUserService, activitiUserService);
                // 设置返回结果
                outputObject.setBeans(userList);
                // 设置节点信息
                Map<String, Object> nodeMation = new HashMap<>();
                nodeMation.put("nodeName", nextUserTask.getName());
                nodeMation.put("nodeType", ActivitiConstants.USER_TASK);
                nodeMation.put("nodeId", nextUserTask.getId());
                outputObject.setBean(nodeMation);
            } else {
                // 如果没有下一个用户任务节点，返回空列表
                log.warn("未找到下一个用户任务节点，taskId: {}, processInstanceId: {}", taskId, processInstanceId);
            }
        } catch (Exception e) {
            log.error("查询下一个节点审批人异常，taskId: {}", inputObject.getParams().get("taskId"), e);
            throw new CustomException("查询下一个节点审批人失败: " + e.getMessage());
        }
    }

    /**
     * 流程运行过程中查询下一个用户节点默认审批人
     *
     * <p><b>功能说明：</b></p>
     * <ul>
     *   <li>根据当前任务ID和审批结果，获取下一个用户任务节点</li>
     *   <li>根据业务数据判断条件表达式，找到符合条件的下一个节点</li>
     *   <li>返回下一个用户任务节点的默认审批人</li>
     * </ul>
     *
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>任务审批时，需要查看下一个节点的默认审批人</li>
     *   <li>根据审批结果（同意/拒绝）和业务数据，确定下一个审批路径</li>
     * </ul>
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void nextProcessDefaultApprover(InputObject inputObject, OutputObject outputObject) {
        try {
            Map<String, Object> params = inputObject.getParams();
            String taskId = params.get("taskId").toString();
            String processInstanceId = params.get("processInstanceId").toString();
            String flag = params.get("flag").toString();

            // 获取业务数据
            Map<String, Object> businessData = new HashMap<>();
            String businessDataStr = params.get("businessData") != null ? params.get("businessData").toString() : null;
            if (!ToolUtil.isBlank(businessDataStr)) {
                businessData = JSONObject.fromObject(businessDataStr);
            }

            // 确保审批结果（flag）在业务数据中
            if (!ToolUtil.isBlank(flag)) {
                businessData.put("flag", flag);
            }
            ensureInitiatorInBusinessData(businessData, processInstanceId);

            // 获取下一个用户任务节点
            NextTaskInfo nextTaskInfo = this.getNextTaskInfo(taskId, businessData);

            if (nextTaskInfo != null && nextTaskInfo.getUserTask() != null) {
                UserTask nextUserTask = nextTaskInfo.getUserTask();
                // 使用批量查询方式获取审批人列表
                Map<String, Object> user = FlowableUtil.findDefaultApprover(
                    nextUserTask, businessData, iAuthUserService, activitiUserService);
                // 设置返回结果
                outputObject.setBean(user);
                if ((user == null || user.isEmpty()) && FlowableUtil.isExpression(nextUserTask.getAssignee())) {
                    String tip;
                    if (FlowableUtil.isSkyDeptJobExpression(nextUserTask.getAssignee())) {
                        tip = "下一节点审批人规则（发起人同部门+岗位）未解析到人员，请检查发起人部门与岗位配置";
                    } else if (FlowableUtil.isSkyLeaderExpression(nextUserTask.getAssignee())) {
                        tip = "下一节点审批人规则（连续上级）未解析到人员，请在员工档案维护直属上级";
                    } else {
                        tip = "下一节点审批人表达式未解析到人员：" + nextUserTask.getAssignee();
                    }
                    outputObject.setreturnMessage(tip);
                }
            } else {
                // 如果没有下一个用户任务节点，返回空
                log.warn("未找到下一个用户任务节点，taskId: {}, processInstanceId: {}", taskId, processInstanceId);
            }
        } catch (Exception e) {
            log.error("查询下一个节点默认审批人异常，taskId: {}", inputObject.getParams().get("taskId"), e);
            throw new CustomException("查询下一个节点默认审批人失败: " + e.getMessage());
        }
    }


    /**
     * 获取下一个用户任务信息
     *
     * @param taskId 任务Id信息
     * @param map    表单数据
     * @return 下一个用户任务用户组信息
     */
    @Override
    public NextTaskInfo getNextTaskInfo(String taskId, Map<String, Object> map) {
        NextTaskInfo nextTaskInfo = new NextTaskInfo();
        try {
            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
            if (task == null) {
                log.warn("getNextTaskInfo return empty, task is null, taskId: {}, vars: {}", taskId, map);
                return nextTaskInfo;
            }
            if (StringUtils.isNotEmpty(task.getParentTaskId())) {
                // 如果是加签节点
                task = getRealTask(task);
                if (task == null) {
                    log.warn("getNextTaskInfo return empty, real task is null after getRealTask, taskId: {}, vars: {}", taskId, map);
                    return nextTaskInfo;
                }
            }
            String executionId = task.getExecutionId();
            if (StringUtils.isEmpty(executionId)) {
                log.warn("getNextTaskInfo return empty, executionId is empty, taskId: {}, taskKey: {}, vars: {}",
                    taskId, task.getTaskDefinitionKey(), map);
                return nextTaskInfo;
            }
            ExecutionEntity execution = (ExecutionEntity) runtimeService.createExecutionQuery().executionId(executionId).singleResult();
            if (execution == null) {
                log.warn("getNextTaskInfo return empty, execution is null, taskId: {}, executionId: {}, vars: {}",
                    taskId, executionId, map);
                return nextTaskInfo;
            }
            BpmnModel bpmnModel = repositoryService.getBpmnModel(execution.getProcessDefinitionId());
            if (bpmnModel == null) {
                log.warn("getNextTaskInfo return empty, bpmnModel is null, taskId: {}, processDefinitionId: {}, vars: {}",
                    taskId, execution.getProcessDefinitionId(), map);
                return nextTaskInfo;
            }
            Trans.begin();
            nextTaskInfo.setUserTask(managementService.executeCommand(new FindNextUserTaskNodeCmd(execution, bpmnModel, map)));
            return nextTaskInfo;
        } catch (Exception ee) {
            log.error("getNextTaskInfo error, taskId: {}, vars: {}", taskId, map, ee);
        } finally {
            try {
                Trans.clear(true);
            } catch (Exception clearEx) {
                log.warn("Trans.clear error in getNextTaskInfo, taskId: {}, error: {}", taskId, clearEx.getMessage());
            }
        }
        return nextTaskInfo;
    }

    private Task getRealTask(Task task) {
        String parentTaskId = task.getParentTaskId();
        long subTaskCount = activitiTaskService.getSubTaskCount(parentTaskId);
        if ((subTaskCount - 1) == 0) {
            // 说明是最后一个后加签子任务
            Task parentTask = taskService.createTaskQuery().taskId(parentTaskId).singleResult();
            if (ActivitiConstants.AFTER_ADDSIGN.equals(parentTask.getScopeType())) {
                return parentTask;
            }
        }
        return task;
    }

    @Override
    public void nextProcessApproverByProcessDefinitionKey(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String modelKey = params.get("modelKey").toString();
        // 获取业务数据
        String businessDataStr = params.get("businessData").toString();
        Map<String, Object> businessData = null;
        if (!ToolUtil.isBlank(businessDataStr)) {
            businessData = JSONObject.fromObject(businessDataStr);
        }

        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        List<Map<String, Object>> user = new ArrayList<>();
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery().processDefinitionKey(modelKey);
        if (tenantEnable) {
            processDefinitionQuery.processDefinitionTenantId(tenantId);
        }
        List<ProcessDefinition> processDefinition = processDefinitionQuery.list();
        if (processDefinition != null) {
            ModelQuery modelQuery = repositoryService.createModelQuery();
            if (tenantEnable) {
                modelQuery.modelTenantId(tenantId);
            }
            List<Model> beans = modelQuery.latestVersion().orderByLastUpdateTime().desc().list();
            List<String> deploymentIds = beans.stream().map(p -> p.getDeploymentId()).collect(Collectors.toList());
            processDefinition = processDefinition.stream().filter(bean -> deploymentIds.contains(bean.getDeploymentId())).collect(Collectors.toList());
            if (processDefinition != null && !processDefinition.isEmpty()) {
                user = getFistUserTaskUserList(processDefinition.get(0), businessData);
            }
        }
        outputObject.setBeans(user);
    }

    @Override
    public void nextProcessDefaultApproverByProcessDefinitionKey(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String modelKey = params.get("modelKey").toString();
        // 获取业务数据
        String businessDataStr = params.get("businessData").toString();
        Map<String, Object> businessData = null;
        if (!ToolUtil.isBlank(businessDataStr)) {
            businessData = JSONObject.fromObject(businessDataStr);
        }
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        Map<String, Object> user = new HashMap<>();
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery().processDefinitionKey(modelKey);
        if (tenantEnable) {
            processDefinitionQuery.processDefinitionTenantId(tenantId);
        }
        List<ProcessDefinition> processDefinition = processDefinitionQuery.list();
        if (processDefinition != null) {
            ModelQuery modelQuery = repositoryService.createModelQuery();
            if (tenantEnable) {
                modelQuery.modelTenantId(tenantId);
            }
            List<Model> beans = modelQuery.latestVersion().orderByLastUpdateTime().desc().list();
            List<String> deploymentIds = beans.stream().map(p -> p.getDeploymentId()).collect(Collectors.toList());
            processDefinition = processDefinition.stream().filter(bean -> deploymentIds.contains(bean.getDeploymentId())).collect(Collectors.toList());
            if (processDefinition != null && !processDefinition.isEmpty()) {
                user = getFirstUserTaskDefaultUser(processDefinition.get(0), businessData);
            }
        }
        outputObject.setBean(user);
    }

    /**
     * 根据业务数据获取第一个符合条件的用户任务节点的所有审批人列表
     *
     * <p><b>功能说明：</b></p>
     * <ul>
     *   <li>从流程的开始节点开始，根据 businessData 中的值判断条件表达式</li>
     *   <li>找到第一个符合条件的用户任务节点</li>
     *   <li>返回该用户任务节点的所有审批人（包括 assignee、candidateUsers、candidateGroups）</li>
     * </ul>
     *
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>流程启动前，需要根据业务数据确定第一个审批人列表</li>
     *   <li>流程中有多个分支，需要根据条件选择不同的审批路径</li>
     * </ul>
     *
     * @param processDefinition 流程定义
     * @param businessData      业务数据，用于判断条件表达式。key 为变量名，value 为变量值
     * @return 审批人列表，如果未找到则返回空列表
     */
    public List<Map<String, Object>> getFistUserTaskUserList(ProcessDefinition processDefinition, Map<String, Object> businessData) {
        try {
            // 获取 BPMN 模型
            BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
            if (bpmnModel == null || bpmnModel.getProcesses().isEmpty()) {
                log.warn("无法获取 BPMN 模型，流程定义ID: {}", processDefinition.getId());
                return new ArrayList<>();
            }

            // 使用 ManagementService 执行 Command 来查找第一个用户任务节点
            FindFirstUserTaskByConditionCmd cmd = new FindFirstUserTaskByConditionCmd(
                bpmnModel,
                businessData != null ? businessData : new HashMap<>(),
                processDefinition.getId()
            );

            UserTask firstUserTask = managementService.executeCommand(cmd);
            if (firstUserTask == null) {
                return new ArrayList<>();
            }
            if (businessData == null) {
                businessData = new HashMap<>();
            }
            ensureInitiatorInBusinessData(businessData, null);
            return FlowableUtil.getNextTaskApproveBatch(
                firstUserTask, businessData, actGroupUserService, iAuthUserService, activitiUserService);
        } catch (Exception e) {
            log.error("获取第一个用户任务节点审批人列表异常，流程定义ID: {}", processDefinition.getId(), e);
            return new ArrayList<>();
        }
    }

    /**
     * 根据业务数据获取第一个符合条件的用户任务节点的默认审批人
     *
     * <p><b>功能说明：</b></p>
     * <ul>
     *   <li>从流程的开始节点开始，根据 businessData 中的值判断条件表达式</li>
     *   <li>找到第一个符合条件的用户任务节点</li>
     *   <li>返回该用户任务节点的默认审批人</li>
     * </ul>
     *
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>流程启动前，需要根据业务数据确定第一个审批人</li>
     *   <li>流程中有多个分支，需要根据条件选择不同的审批路径</li>
     * </ul>
     *
     * <p><b>示例：</b></p>
     * <pre>{@code
     * // businessData 示例
     * Map<String, Object> businessData = new HashMap<>();
     * businessData.put("amount", 5000);  // 金额
     * businessData.put("dept", "finance");  // 部门
     *
     * // 如果流程中有条件：${amount > 1000}，会根据 businessData 中的 amount 值判断
     * Map<String, Object> user = getFirstUserTaskDefaultUser(processDefinition, businessData);
     * }</pre>
     *
     * @param processDefinition 流程定义
     * @param businessData      业务数据，用于判断条件表达式。key 为变量名，value 为变量值
     * @return 用户信息，如果未找到则返回null
     */
    public Map<String, Object> getFirstUserTaskDefaultUser(ProcessDefinition processDefinition, Map<String, Object> businessData) {
        try {
            // 获取 BPMN 模型
            BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
            if (bpmnModel == null || bpmnModel.getProcesses().isEmpty()) {
                log.warn("无法获取 BPMN 模型，流程定义ID: {}", processDefinition.getId());
                return null;
            }

            // 使用 ManagementService 执行 Command 来查找第一个用户任务节点
            // 这样可以确保 ExecutionEntity 在正确的上下文中创建
            FindFirstUserTaskByConditionCmd cmd = new FindFirstUserTaskByConditionCmd(
                bpmnModel,
                businessData != null ? businessData : new HashMap<>(),
                processDefinition.getId()
            );

            UserTask firstUserTask = managementService.executeCommand(cmd);

            if (businessData == null) {
                businessData = new HashMap<>();
            }
            ensureInitiatorInBusinessData(businessData, null);
            return FlowableUtil.findDefaultApprover(
                firstUserTask, businessData, iAuthUserService, activitiUserService);
        } catch (Exception e) {
            log.error("获取第一个用户任务节点默认审批人异常，流程定义ID: {}", processDefinition.getId(), e);
            throw new CustomException("获取第一个用户任务节点默认审批人失败: " + e.getMessage());
        }
    }


    /**
     * 启动流程
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void startProcess(InputObject inputObject, OutputObject outputObject) {
        FlowableSubData flowableSubData = inputObject.getParams(FlowableSubData.class);
        Integer formSubType = flowableSubData.getFormSubType();
        if (!formSubType.equals(FormSubType.SUB_FLOWABLE.getKey())) {
            return;
        }
        // 根据业务数据和className获取配置的工作流key,如果actModel没有配置，则无法提交审批
        ActFlowMation actFlowMation = actFlowService.getActFlowByModelKey(flowableSubData.getModelKey());
        if (actFlowMation != null) {
            log.info("actFlow mation is: " + JSONUtil.toJsonStr(actFlowMation));
            // 提交审批
            String processInstanceId = activitiModelService.startProcess(flowableSubData, actFlowMation);
            Map<String, Object> result = new HashMap<>();
            result.put("processInstanceId", processInstanceId);
            outputObject.setBean(result);
        } else {
            throw new CustomException("该功能暂未配置工作流，请联系管理员进行配置。");
        }
    }

    /**
     * 撤销流程
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void revokeProcess(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String processInstanceId = params.get("processInstanceId").toString();
        String userId = inputObject.getLogParams().get("id").toString();
        activitiService.revokeByProcessInstanceId(processInstanceId, userId);
    }

    /**
     * 根据流程实例id获取流程信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryProcessInstance(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String processInstanceId = params.get("processInstanceId").toString();
        // 获取流程信息
        ActUserProcess userProcess = actUserProcessService.selectByProcessInstanceId(processInstanceId);

        // 任务信息
        Map<String, Object> task = new HashMap<>();
        // 任务-当前审批人
        List<String> assignee = activitiTaskService.getTaskAssignee(processInstanceId);
        task.put("taskCurrentAssignee", assignee);
        if (CollectionUtil.isNotEmpty(assignee)) {
            task.put("taskCurrentAssigneeMation", iAuthUserService.queryDataMationByIds(Joiner.on(CommonCharConstants.COMMA_MARK).join(assignee)));
        }
        userProcess.setTask(task);

        outputObject.setBean(userProcess);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

}
