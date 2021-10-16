/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.CheckWorkLeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CheckWorkLeaveController {

    @Autowired
    private CheckWorkLeaveService checkWorkLeaveService;

    /**
     *
     * @Title: queryMyCheckWorkLeaveList
     * @Description: 获取我的请假申请列表
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CheckWorkLeaveController/queryMyCheckWorkLeaveList")
    @ResponseBody
    public void queryMyCheckWorkLeaveList(InputObject inputObject, OutputObject outputObject) throws Exception{
        checkWorkLeaveService.queryMyCheckWorkLeaveList(inputObject, outputObject);
    }
    
    /**
     *
     * @Title: insertCheckWorkLeaveMation
     * @Description: 新增请假申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CheckWorkLeaveController/insertCheckWorkLeaveMation")
    @ResponseBody
    public void insertCheckWorkLeaveMation(InputObject inputObject, OutputObject outputObject) throws Exception{
        checkWorkLeaveService.insertCheckWorkLeaveMation(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryCheckWorkLeaveToEditById
     * @Description: 请假申请编辑时进行回显
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CheckWorkLeaveController/queryCheckWorkLeaveToEditById")
    @ResponseBody
    public void queryCheckWorkLeaveToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
        checkWorkLeaveService.queryCheckWorkLeaveToEditById(inputObject, outputObject);
    }

    /**
     *
     * @Title: updateCheckWorkLeaveById
     * @Description: 编辑请假申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CheckWorkLeaveController/updateCheckWorkLeaveById")
    @ResponseBody
    public void updateCheckWorkLeaveById(InputObject inputObject, OutputObject outputObject) throws Exception{
        checkWorkLeaveService.updateCheckWorkLeaveById(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryCheckWorkLeaveDetailsById
     * @Description: 请假申请详情
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CheckWorkLeaveController/queryCheckWorkLeaveDetailsById")
    @ResponseBody
    public void queryCheckWorkLeaveDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception{
        checkWorkLeaveService.queryCheckWorkLeaveDetailsById(inputObject, outputObject);
    }

    /**
     *
     * @Title: updateCheckWorkLeaveByIdInProcess
     * @Description: 在工作流中编辑请假申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CheckWorkLeaveController/updateCheckWorkLeaveByIdInProcess")
    @ResponseBody
    public void updateCheckWorkLeaveByIdInProcess(InputObject inputObject, OutputObject outputObject) throws Exception{
        checkWorkLeaveService.updateCheckWorkLeaveByIdInProcess(inputObject, outputObject);
    }

    /**
     *
     * @Title: editCheckWorkLeaveToSubApproval
     * @Description: 提交审批请假申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CheckWorkLeaveController/editCheckWorkLeaveToSubApproval")
    @ResponseBody
    public void editCheckWorkLeaveToSubApproval(InputObject inputObject, OutputObject outputObject) throws Exception{
        checkWorkLeaveService.editCheckWorkLeaveToSubApproval(inputObject, outputObject);
    }

    /**
     *
     * @Title: updateCheckWorkLeaveToCancellation
     * @Description: 作废请假申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CheckWorkLeaveController/updateCheckWorkLeaveToCancellation")
    @ResponseBody
    public void updateCheckWorkLeaveToCancellation(InputObject inputObject, OutputObject outputObject) throws Exception{
        checkWorkLeaveService.updateCheckWorkLeaveToCancellation(inputObject, outputObject);
    }

    /**
     *
     * @Title: editCheckWorkLeaveToRevoke
     * @Description: 撤销请假申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CheckWorkLeaveController/editCheckWorkLeaveToRevoke")
    @ResponseBody
    public void editCheckWorkLeaveToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception{
        checkWorkLeaveService.editCheckWorkLeaveToRevoke(inputObject, outputObject);
    }

}
