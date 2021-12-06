/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.activiti.controller;

import com.skyeye.activiti.service.ActivitiProcessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ClassName: ActivitiProcessController
 * @Description: 工作流流程相关操作
 * @author: skyeye云系列--卫志强
 * @date: 2021/12/2 21:28
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Controller
public class ActivitiProcessController {

    @Autowired
    private ActivitiProcessService activitiProcessService;

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
    @RequestMapping("/post/ActivitiProcessController/updateProcessToHangUp")
    @ResponseBody
    public void updateProcessToHangUp(InputObject inputObject, OutputObject outputObject) throws Exception{
        activitiProcessService.updateProcessToHangUp(inputObject, outputObject);
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
    @RequestMapping("/post/ActivitiProcessController/updateProcessToActivation")
    @ResponseBody
    public void updateProcessToActivation(InputObject inputObject, OutputObject outputObject) throws Exception{
        activitiProcessService.updateProcessToActivation(inputObject, outputObject);
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
    @RequestMapping("/post/ActivitiProcessController/editProcessInstanceWithDraw")
    @ResponseBody
    public void editProcessInstanceWithDraw(InputObject inputObject, OutputObject outputObject) throws Exception{
        activitiProcessService.editProcessInstanceWithDraw(inputObject, outputObject);
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
    @RequestMapping("/post/ActivitiProcessController/editProcessInstancePicToRefresh")
    @ResponseBody
    public void editProcessInstancePicToRefresh(InputObject inputObject, OutputObject outputObject) throws Exception{
        activitiProcessService.editProcessInstancePicToRefresh(inputObject, outputObject);
    }

    /**
     *
     * @Title: nextPrcessApprover
     * @Description: 获取流程下一个节点的审批人
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ActivitiProcessController/nextPrcessApprover")
    @ResponseBody
    public void nextPrcessApprover(InputObject inputObject, OutputObject outputObject) throws Exception{
        activitiProcessService.nextPrcessApprover(inputObject, outputObject);
    }

    /**
     *
     * @Title: nextPrcessApproverByProcessDefinitionKey
     * @Description: 根据processDefinitionKey获取流程下一个用户节点的审批人
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ActivitiProcessController/nextPrcessApproverByProcessDefinitionKey")
    @ResponseBody
    public void nextPrcessApproverByProcessDefinitionKey(InputObject inputObject, OutputObject outputObject) throws Exception{
        activitiProcessService.nextPrcessApproverByProcessDefinitionKey(inputObject, outputObject);
    }

}
