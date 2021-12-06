/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.activiti.service.impl;

import cn.hutool.json.JSONUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.activiti.service.ActivitiModelService;
import com.skyeye.activiti.service.ActivitiTaskService;
import com.skyeye.annotation.transaction.ActivitiAndBaseTransaction;
import com.skyeye.cache.redis.RedisCache;
import com.skyeye.common.constans.ActivitiConstants;
import com.skyeye.common.constans.Constants;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.ActModleTypeDao;
import com.skyeye.eve.dao.ActUserProcessInstanceIdDao;
import com.skyeye.eve.dao.SysEveUserDao;
import com.skyeye.exception.CustomException;
import com.skyeye.jedis.JedisClientService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;

/**
 * @ClassName: ActivitiTaskServiceImpl
 * @Description: 工作流用户任务相关
 * @author: skyeye云系列--卫志强
 * @date: 2021/12/2 20:55
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class ActivitiTaskServiceImpl implements ActivitiTaskService {

    private static Logger LOGGER = LoggerFactory.getLogger(ActivitiTaskServiceImpl.class);

    /**
     * 任务服务类。可以从这个类中获取任务的信息
     */
    @Autowired
    private TaskService taskService;

    @Autowired
    public JedisClientService jedisClient;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private ActUserProcessInstanceIdDao actUserProcessInstanceIdDao;

    @Autowired
    private SysEveUserDao sysEveUserDao;

    /**
     * 查询历史信息的类。在一个流程执行完成后，这个对象为我们提供查询历史信息
     */
    @Autowired
    private HistoryService historyService;

    @Autowired
    private ActModleTypeDao actModleTypeDao;

    @Autowired
    private ActivitiModelService activitiModelService;

    @Autowired
    private RedisCache redisCache;

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
        List<Task> taskList = taskQuery.orderByTaskId().desc()
                .listPage(Integer.parseInt(map.get("limit").toString()) * (Integer.parseInt(map.get("page").toString()) - 1), Integer.parseInt(map.get("limit").toString()));
        // 整理数据
        List<Map<String, Object>> rows = new ArrayList<>();
        for (Task task : taskList) {
            //流程待办在redis中存储的key
            String cacheKey = Constants.getActProcessInstanceRedisCacheKey(task.getProcessInstanceId());
            Map<String, Object> bean = redisCache.getMap(cacheKey, key -> {
                try {
                    Map<String, Object> taskModel = this.getTaskModelMationByProcessInstanceId(task.getProcessInstanceId());
                    return taskModel;
                } catch (Exception ee) {
                    LOGGER.warn("queryUserAgencyTasksListByUserId get processInstanceId {} is error.", task.getProcessInstanceId(), ee);
                }
                return null;
            }, RedisConstants.TEN_DAY_SECONDS);
            rows.add(bean);
        }
        outputObject.setBeans(rows);
        outputObject.settotal(count);
    }

    private Map<String, Object> getTaskModelMationByProcessInstanceId(String processInstanceId) throws Exception {
        Map<String, Object> taskModel = new HashMap<>();
        ProcessInstance instance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if (instance != null) {
            // 保证流程还没结束
            Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).list().get(0);
            // 获取流程创建信息
            Map<String, Object> process = actUserProcessInstanceIdDao.queryProcessInstanceMationByProcessInstanceId(task.getProcessInstanceId());
            taskModel.put("assignee", task.getAssignee());
            taskModel.put("createName", (process == null || process.isEmpty()) ? "" : process.get("createName"));//申请人姓名
            taskModel.put("createTime", (process == null || process.isEmpty()) ? "" : process.get("createTime"));//申请时间
            taskModel.put("taskType", (process == null || process.isEmpty()) ? "" : process.get("title"));//任务类型
            // 任务id
            taskModel.put("id", task.getId());
            // 当前任务节点名称
            taskModel.put("name", ToolUtil.isBlank(task.getName()) ? "" : task.getName());
            taskModel.put("suspended", instance.isSuspended());//流程状态
            taskModel.put("processInstanceId", task.getProcessInstanceId());
        }
        return taskModel;
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
        for (Map<String, Object> bean : beans) {
            // 该结束流程在redis中存储的key
            String processInstanceId = bean.get("processInstanceId").toString();
            String cacheKey = Constants.getActProcessInstanceRedisCacheKey(processInstanceId);
            Map<String, Object> item = redisCache.getMap(cacheKey, key -> {
                try {
                    ProcessInstance instance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
                    // 保证运行ing
                    Map<String, Object> taskModel;
                    if (instance != null) {
                        Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).list().get(0);
                        taskModel = this.getTaskModelMationByProcessInstanceId(processInstanceId);
                        // 获取当前任务节点的审批人
                        Map<String, Object> userMation = sysEveUserDao.queryUserMationByUserId(task.getAssignee());
                        if(userMation != null && !userMation.isEmpty()){
                            taskModel.put("agencyName", userMation.get("userName"));//审批人
                        }else{
                            taskModel.put("agencyName", "未设置");//审批人
                        }
                        taskModel.putAll(bean);//将从数据库查出来的数据返回给前台
                        //判断是否可编辑
                        Map<String, Object> variables = taskService.getVariables(task.getId());
                        taskModel.put("editRow", "1");//可编辑
                        taskModel.put("weatherEnd", 0);//标记流程是否结束；1：结束，0.未结束
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
                        taskModel.putAll(bean);//将从数据库查出来的数据返回给前台
                        taskModel.put("editRow", "-1");//不可编辑
                        taskModel.put("weatherEnd", 1);//标记流程是否结束；1：结束，0.未结束
                        taskModel.put("suspended", false);//流程状态-正常
                    }
                    return taskModel;
                } catch (Exception ee) {
                    LOGGER.warn("queryStartProcessNotSubByUserId get processInstanceId {} is error.", processInstanceId, ee);
                }
                return null;
            }, RedisConstants.TEN_DAY_SECONDS);
            item.put("pageUrl", bean.get("pageUrl"));
            item.put("revokeMapping", bean.get("revokeMapping"));
            items.add(item);
        }
        outputObject.setBeans(items);
        outputObject.settotal(pages.getTotal());
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
        for (HistoricTaskInstance hisTask : hisTaskList) {
            // 该流程在redis中存储的key
            String cacheKey = Constants.getActProcessInstanceRedisCacheKey(hisTask.getProcessInstanceId());
            Map<String, Object> item = redisCache.getMap(cacheKey, key -> {
                try {
                    ProcessInstance instance = runtimeService.createProcessInstanceQuery().processInstanceId(hisTask.getProcessInstanceId()).singleResult();
                    Map<String, Object> hisModel;
                    if(instance != null){
                        hisModel = this.getTaskModelMationByProcessInstanceId(hisTask.getProcessInstanceId());
                        // 获取当前任务节点的审批人
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
                        hisModel.put("agencyName", operatorName);//受理人
                        hisModel.put("name", hisTask.getName());//审批节点
                        hisModel.put("startTime", hisTask.getStartTime());//申请时间
                        hisModel.put("endTime", hisTask.getEndTime());//受理时间
                        hisModel.put("weatherEnd", 0);//标记流程是否结束；1：结束，0.未结束
                    }else{
                        hisModel = getHistoricProcessInstance(hisTask.getProcessInstanceId(), hisTask.getId());
                        hisModel.put("name", hisTask.getName());//我处理的任务
                        hisModel.put("weatherEnd", 1);//标记流程是否结束；1：结束，0.未结束
                    }
                    return hisModel;
                } catch (Exception ee) {
                    LOGGER.warn("queryMyHistoryTaskByUserId get processInstanceId {} is error.", hisTask.getProcessInstanceId(), ee);
                }
                return null;
            }, RedisConstants.TEN_DAY_SECONDS);
            // 历史审批任务id
            item.put("hisTaskId", hisTask.getId());
            beans.add(item);
        }
        outputObject.setBeans(beans);
        outputObject.settotal(count);
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
            rows.add(instanceModel);
        }
        outputObject.setBeans(rows);
        outputObject.settotal(historyService.createHistoricProcessInstanceQuery().orderByProcessInstanceEndTime().desc().finished().count());
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
        // 获取流程创建信息
        Map<String, Object> process = actUserProcessInstanceIdDao.queryProcessInstanceMationByProcessInstanceId(processInstanceId);
        Map<String, Object> taskModel = new HashMap<>();
        taskModel.put("createName", (process == null || process.isEmpty()) ? "" : process.get("createName"));//申请人
        taskModel.put("createTime", (process == null || process.isEmpty()) ? "" : process.get("createTime"));//申请时间
        taskModel.put("taskType", (process == null || process.isEmpty()) ? "" : process.get("title"));//任务类型
        taskModel.put("agencyName", assignee);//受理人
        taskModel.put("name", "结束");//当前任务节点名称
        taskModel.put("processInstanceId", processInstanceId);//流程id
        taskModel.put("startTime", instance != null ? instance.getStartTime() : "");//开始时间
        taskModel.put("endTime", instance != null ? instance.getEndTime() : "");//结束时间
        return taskModel;
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
        for(ProcessInstance instance : beans){
            Map<String, Object> taskModel = this.getTaskModelMationByProcessInstanceId(instance.getProcessInstanceId());
            rows.add(taskModel);
        }
        outputObject.setBeans(rows);
        outputObject.settotal(runtimeService.createProcessInstanceQuery().count());
    }

    /**
     *
     * @Title: querySubFormMationByProcessInstanceId
     * @Description: 根据流程id获取流程详情信息
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
                .variableName(ActivitiConstants.PROCESSINSTANCEID_TASK_VARABLES).list();
        if(!hisTaskList.isEmpty()){
            Map<String, Object> params = (Map<String, Object>) hisTaskList.get(0).getValue();
            List<Map<String, Object>> beans = getParamsToDSFormShow(params);
            outputObject.setBeans(beans);
        }else{
            outputObject.setreturnMessage("数据信息错误");
        }
    }

    /**
     *
     * @Title: querySubFormMationByTaskId
     * @Description: 根据taskId获取表单信息
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
        activitiModelService.deleteProcessInRedisMation("647645");
        // 获取任务自定义id和名称
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        map.put("taskKey", task.getTaskDefinitionKey());
        map.put("taskKeyName", task.getName());

        // 获取流程关联页面类型
        Map<String, Object> process = actUserProcessInstanceIdDao.queryProcessInstanceMationByProcessInstanceId(task.getProcessInstanceId());
        map.put("pageTypes", process.get("pageTypes"));

        // 获取提交时候的信息
        Map<String, Object> params = this.getCurrentTaskParamsByTaskId(taskId);
        List<Map<String, Object>> beans = getParamsToDSFormShow(params);
        outputObject.setBean(map);
        outputObject.setBeans(beans);
    }

    /**
     * 获取当前任务节点填写的表单数据
     *
     * @param taskId 任务id
     * @return 当前任务节点填写的表单数据
     */
    @Override
    public Map<String, Object> getCurrentTaskParamsByTaskId(String taskId){
        Map<String, Object> params = (Map<String, Object>) taskService.getVariable(taskId, ActivitiConstants.PROCESSINSTANCEID_TASK_VARABLES);
        return params;
    }

    /**
     * 将工作流数据转为form表单类型的数据并作展示
     *
     * @return
     */
    private List<Map<String, Object>> getParamsToDSFormShow(Map<String, Object> params){
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
     * @Title: editActivitiModelToRun
     * @Description: 提交审批结果
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

        // 设置审批人修改的信息
        setApprovalEditMation(map, processInstanceId, taskId);
        Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
        // 处理任务
        Map<String, Object> bean = new HashMap<>();
        // 判断节点是否已经拒绝过一次了
        setWhetherNeedEnd(bean, taskId, flag);
        // 获取指定任务节点的审批信息
        List<Map<String, Object>> leaveList = activitiModelService.getUpLeaveList(user.get("id").toString(), user.get("userName").toString(), map.get("opinion").toString(), flag, task);
        bean.put("leaveOpinionList", leaveList);
        bean.put("flag", map.get("flag"));//校验参数
        taskService.complete(taskId, bean);
        LOGGER.info("complete success, processInstanceId is {}.", processInstanceId);
        // 绘制图像
        activitiModelService.queryProHighLighted(processInstanceId);
        // 删除指定流程在redis中的缓存信息
        activitiModelService.deleteProcessInRedisMation(processInstanceId);
        // 设置下个节点的审批人
        setNextUserTaskApproval(processInstanceId, map.get("approverId").toString());
    }

    @Override
    public void setNextUserTaskApproval(String processInstanceId, String approverId){
        if(!ToolUtil.isBlank(approverId)){
            Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).active().singleResult();
            taskService.setAssignee(task.getId(), approverId);
        }
    }

    private void setApprovalEditMation(Map<String, Object> map, String processInstanceId, String taskId) throws Exception {
        // 获取审批人编辑的信息
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
     * 设置审批人编辑的信息到流程中
     *
     * @param taskId 任务id
     * @param processInstanceId 流程id
     * @param jArray 编辑的form表单信息
     */
    private void resetEditFormElement(String taskId, String processInstanceId, List<Map<String, Object>> jArray){
        Map<String, Object> params = (Map<String, Object>) taskService.getVariable(taskId, ActivitiConstants.PROCESSINSTANCEID_TASK_VARABLES);
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
        runtimeService.setVariable(processInstanceId, ActivitiConstants.PROCESSINSTANCEID_TASK_VARABLES, params);
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

}
