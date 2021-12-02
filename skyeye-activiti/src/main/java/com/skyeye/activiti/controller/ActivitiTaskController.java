/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.activiti.controller;

import com.skyeye.activiti.service.ActivitiTaskService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ClassName: ActivitiTaskController
 * @Description: 工作流用户任务相关
 * @author: skyeye云系列--卫志强
 * @date: 2021/12/2 20:55
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Controller
public class ActivitiTaskController {

    @Autowired
    private ActivitiTaskService activitiTaskService;

    /**
     *
     * @Title: queryUserAgencyTasksListByUserId
     * @Description: 获取我的待办任务
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ActivitiTaskController/queryUserAgencyTasksListByUserId")
    @ResponseBody
    public void queryUserAgencyTasksListByUserId(InputObject inputObject, OutputObject outputObject) throws Exception{
        activitiTaskService.queryUserAgencyTasksListByUserId(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryStartProcessNotSubByUserId
     * @Description: 获取我的流程
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ActivitiTaskController/queryStartProcessNotSubByUserId")
    @ResponseBody
    public void queryStartProcessNotSubByUserId(InputObject inputObject, OutputObject outputObject) throws Exception{
        activitiTaskService.queryStartProcessNotSubByUserId(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryMyHistoryTaskByUserId
     * @Description: 获取我的历史任务
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ActivitiTaskController/queryMyHistoryTaskByUserId")
    @ResponseBody
    public void queryMyHistoryTaskByUserId(InputObject inputObject, OutputObject outputObject) throws Exception{
        activitiTaskService.queryMyHistoryTaskByUserId(inputObject, outputObject);
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
    @RequestMapping("/post/ActivitiTaskController/queryApprovalTasksHistoryByProcessInstanceId")
    @ResponseBody
    public void queryApprovalTasksHistoryByProcessInstanceId(InputObject inputObject, OutputObject outputObject) throws Exception{
        activitiTaskService.queryApprovalTasksHistoryByProcessInstanceId(inputObject, outputObject);
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
    @RequestMapping("/post/ActivitiTaskController/queryAllComplateProcessList")
    @ResponseBody
    public void queryAllComplateProcessList(InputObject inputObject, OutputObject outputObject) throws Exception{
        activitiTaskService.queryAllComplateProcessList(inputObject, outputObject);
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
    @RequestMapping("/post/ActivitiTaskController/queryAllConductProcessList")
    @ResponseBody
    public void queryAllConductProcessList(InputObject inputObject, OutputObject outputObject) throws Exception{
        activitiTaskService.queryAllConductProcessList(inputObject, outputObject);
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
    @RequestMapping("/post/ActivitiTaskController/querySubFormMationByProcessInstanceId")
    @ResponseBody
    public void querySubFormMationByProcessInstanceId(InputObject inputObject, OutputObject outputObject) throws Exception{
        activitiTaskService.querySubFormMationByProcessInstanceId(inputObject, outputObject);
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
    @RequestMapping("/post/ActivitiTaskController/querySubFormMationByTaskId")
    @ResponseBody
    public void querySubFormMationByTaskId(InputObject inputObject, OutputObject outputObject) throws Exception{
        activitiTaskService.querySubFormMationByTaskId(inputObject, outputObject);
    }

    /**
     *
     * @Title: editActivitiModelToRun
     * @Description: 提交审批结果
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ActivitiTaskController/editActivitiModelToRun")
    @ResponseBody
    public void editActivitiModelToRun(InputObject inputObject, OutputObject outputObject) throws Exception{
        activitiTaskService.editActivitiModelToRun(inputObject, outputObject);
    }

}
