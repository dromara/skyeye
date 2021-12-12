/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.CheckWorkCancelLeaveService;

/**
 *
 * @ClassName: CheckWorkCancelLeaveController
 * @Description: 销假申请控制类
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/11 9:48
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Controller
public class CheckWorkCancelLeaveController {

    @Autowired
    private CheckWorkCancelLeaveService checkWorkCancelLeaveService;

    /**
     *
     * @Title: queryMyCheckWorkCancelLeaveList
     * @Description: 获取我的销假申请列表
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CheckWorkCancelLeaveController/queryMyCheckWorkCancelLeaveList")
    @ResponseBody
    public void queryMyCheckWorkCancelLeaveList(InputObject inputObject, OutputObject outputObject) throws Exception{
        checkWorkCancelLeaveService.queryMyCheckWorkCancelLeaveList(inputObject, outputObject);
    }
    
    /**
     *
     * @Title: insertCheckWorkCancelLeaveMation
     * @Description: 新增销假申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CheckWorkCancelLeaveController/insertCheckWorkCancelLeaveMation")
    @ResponseBody
    public void insertCheckWorkCancelLeaveMation(InputObject inputObject, OutputObject outputObject) throws Exception{
        checkWorkCancelLeaveService.insertCheckWorkCancelLeaveMation(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryCheckWorkCancelLeaveToEditById
     * @Description: 销假申请编辑时进行回显
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CheckWorkCancelLeaveController/queryCheckWorkCancelLeaveToEditById")
    @ResponseBody
    public void queryCheckWorkCancelLeaveToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
        checkWorkCancelLeaveService.queryCheckWorkCancelLeaveToEditById(inputObject, outputObject);
    }

    /**
     *
     * @Title: updateCheckWorkCancelLeaveById
     * @Description: 编辑销假申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CheckWorkCancelLeaveController/updateCheckWorkCancelLeaveById")
    @ResponseBody
    public void updateCheckWorkCancelLeaveById(InputObject inputObject, OutputObject outputObject) throws Exception{
        checkWorkCancelLeaveService.updateCheckWorkCancelLeaveById(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryCheckWorkCancelLeaveDetailsById
     * @Description: 销假申请详情
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CheckWorkCancelLeaveController/queryCheckWorkCancelLeaveDetailsById")
    @ResponseBody
    public void queryCheckWorkCancelLeaveDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception{
        checkWorkCancelLeaveService.queryCheckWorkCancelLeaveDetailsById(inputObject, outputObject);
    }

    /**
     *
     * @Title: editCheckWorkCancelLeaveToSubApproval
     * @Description: 提交审批销假申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CheckWorkCancelLeaveController/editCheckWorkCancelLeaveToSubApproval")
    @ResponseBody
    public void editCheckWorkCancelLeaveToSubApproval(InputObject inputObject, OutputObject outputObject) throws Exception{
        checkWorkCancelLeaveService.editCheckWorkCancelLeaveToSubApproval(inputObject, outputObject);
    }

    /**
     *
     * @Title: updateCheckWorkCancelLeaveToCancellation
     * @Description: 作废销假申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CheckWorkCancelLeaveController/updateCheckWorkCancelLeaveToCancellation")
    @ResponseBody
    public void updateCheckWorkCancelLeaveToCancellation(InputObject inputObject, OutputObject outputObject) throws Exception{
        checkWorkCancelLeaveService.updateCheckWorkCancelLeaveToCancellation(inputObject, outputObject);
    }

    /**
     *
     * @Title: editCheckWorkCancelLeaveToRevoke
     * @Description: 撤销销假申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CheckWorkCancelLeaveController/editCheckWorkCancelLeaveToRevoke")
    @ResponseBody
    public void editCheckWorkCancelLeaveToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception{
        checkWorkCancelLeaveService.editCheckWorkCancelLeaveToRevoke(inputObject, outputObject);
    }

}
