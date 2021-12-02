/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.activiti.service.impl;

import com.skyeye.activiti.service.ActivitiModelService;
import com.skyeye.activiti.service.ActivitiProcessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;

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
        //根据一个流程实例的id激活该流程实例
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

}
