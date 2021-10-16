/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.CheckWorkBusinessTripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ClassName: CheckWorkBusinessTripController
 * @Description: 出差申请
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/6 22:02
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Controller
public class CheckWorkBusinessTripController {

    @Autowired
    private CheckWorkBusinessTripService checkWorkBusinessTripService;

    /**
     *
     * @Title: queryMyCheckWorkBusinessTripList
     * @Description: 获取我的出差申请列表
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CheckWorkBusinessTripController/queryMyCheckWorkBusinessTripList")
    @ResponseBody
    public void queryMyCheckWorkBusinessTripList(InputObject inputObject, OutputObject outputObject) throws Exception{
        checkWorkBusinessTripService.queryMyCheckWorkBusinessTripList(inputObject, outputObject);
    }

    /**
     *
     * @Title: insertCheckWorkBusinessTripMation
     * @Description: 新增出差申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CheckWorkBusinessTripController/insertCheckWorkBusinessTripMation")
    @ResponseBody
    public void insertCheckWorkBusinessTripMation(InputObject inputObject, OutputObject outputObject) throws Exception{
        checkWorkBusinessTripService.insertCheckWorkBusinessTripMation(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryCheckWorkBusinessTripToEditById
     * @Description: 出差申请编辑时进行回显
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CheckWorkBusinessTripController/queryCheckWorkBusinessTripToEditById")
    @ResponseBody
    public void queryCheckWorkBusinessTripToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
        checkWorkBusinessTripService.queryCheckWorkBusinessTripToEditById(inputObject, outputObject);
    }

    /**
     *
     * @Title: updateCheckWorkBusinessTripById
     * @Description: 编辑出差申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CheckWorkBusinessTripController/updateCheckWorkBusinessTripById")
    @ResponseBody
    public void updateCheckWorkBusinessTripById(InputObject inputObject, OutputObject outputObject) throws Exception{
        checkWorkBusinessTripService.updateCheckWorkBusinessTripById(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryCheckWorkBusinessTripDetailsById
     * @Description: 出差申请详情
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CheckWorkBusinessTripController/queryCheckWorkBusinessTripDetailsById")
    @ResponseBody
    public void queryCheckWorkBusinessTripDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception{
        checkWorkBusinessTripService.queryCheckWorkBusinessTripDetailsById(inputObject, outputObject);
    }

    /**
     *
     * @Title: updateCheckWorkBusinessTripByIdInProcess
     * @Description: 在工作流中编辑出差申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CheckWorkBusinessTripController/updateCheckWorkBusinessTripByIdInProcess")
    @ResponseBody
    public void updateCheckWorkBusinessTripByIdInProcess(InputObject inputObject, OutputObject outputObject) throws Exception{
        checkWorkBusinessTripService.updateCheckWorkBusinessTripByIdInProcess(inputObject, outputObject);
    }

    /**
     *
     * @Title: editCheckWorkBusinessTripToSubApproval
     * @Description: 提交审批出差申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CheckWorkBusinessTripController/editCheckWorkBusinessTripToSubApproval")
    @ResponseBody
    public void editCheckWorkBusinessTripToSubApproval(InputObject inputObject, OutputObject outputObject) throws Exception{
        checkWorkBusinessTripService.editCheckWorkBusinessTripToSubApproval(inputObject, outputObject);
    }

    /**
     *
     * @Title: updateCheckWorkBusinessTripToCancellation
     * @Description: 作废出差申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CheckWorkBusinessTripController/updateCheckWorkBusinessTripToCancellation")
    @ResponseBody
    public void updateCheckWorkBusinessTripToCancellation(InputObject inputObject, OutputObject outputObject) throws Exception{
        checkWorkBusinessTripService.updateCheckWorkBusinessTripToCancellation(inputObject, outputObject);
    }

    /**
     *
     * @Title: editCheckWorkBusinessTripToRevoke
     * @Description: 撤销出差申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CheckWorkBusinessTripController/editCheckWorkBusinessTripToRevoke")
    @ResponseBody
    public void editCheckWorkBusinessTripToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception{
        checkWorkBusinessTripService.editCheckWorkBusinessTripToRevoke(inputObject, outputObject);
    }

}
