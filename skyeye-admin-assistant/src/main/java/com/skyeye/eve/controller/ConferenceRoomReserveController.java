/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.ConferenceRoomReserveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ClassName: ConferenceRoomReserveController
 * @Description: 会议室预定申请控制类
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/1 14:17
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Controller
public class ConferenceRoomReserveController {

    @Autowired
    private ConferenceRoomReserveService conferenceRoomReserveService;

    /**
     *
     * @Title: queryMyReserveConferenceRoomList
     * @Description: 获取我预定的会议室列表
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ConferenceRoomReserveController/queryMyReserveConferenceRoomList")
    @ResponseBody
    public void queryMyReserveConferenceRoomList(InputObject inputObject, OutputObject outputObject) throws Exception{
        conferenceRoomReserveService.queryMyReserveConferenceRoomList(inputObject, outputObject);
    }
    /**
     *
     * @Title: insertReserveConferenceRoomMation
     * @Description: 会议室预定申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ConferenceRoomReserveController/insertReserveConferenceRoomMation")
    @ResponseBody
    public void insertReserveConferenceRoomMation(InputObject inputObject, OutputObject outputObject) throws Exception{
        conferenceRoomReserveService.insertReserveConferenceRoomMation(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryReserveConferenceRoomMationToDetails
     * @Description: 会议室预定申请详情
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ConferenceRoomReserveController/queryReserveConferenceRoomMationToDetails")
    @ResponseBody
    public void queryReserveConferenceRoomMationToDetails(InputObject inputObject, OutputObject outputObject) throws Exception{
        conferenceRoomReserveService.queryReserveConferenceRoomMationToDetails(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryReserveConferenceRoomMationToEdit
     * @Description: 会议室预定申请编辑时进行回显
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ConferenceRoomReserveController/queryReserveConferenceRoomMationToEdit")
    @ResponseBody
    public void queryReserveConferenceRoomMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception{
        conferenceRoomReserveService.queryReserveConferenceRoomMationToEdit(inputObject, outputObject);
    }

    /**
     *
     * @Title: updateReserveConferenceRoomMationById
     * @Description: 编辑会议室预定申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ConferenceRoomReserveController/updateReserveConferenceRoomMationById")
    @ResponseBody
    public void updateReserveConferenceRoomMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
        conferenceRoomReserveService.updateReserveConferenceRoomMationById(inputObject, outputObject);
    }

    /**
     *
     * @Title: editReserveConferenceRoomToSubApproval
     * @Description: 会议室预定申请提交审批
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ConferenceRoomReserveController/editReserveConferenceRoomToSubApproval")
    @ResponseBody
    public void editReserveConferenceRoomToSubApproval(InputObject inputObject, OutputObject outputObject) throws Exception{
        conferenceRoomReserveService.editReserveConferenceRoomToSubApproval(inputObject, outputObject);
    }

    /**
     *
     * @Title: updateReserveConferenceRoomToCancellation
     * @Description: 作废会议室预定申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ConferenceRoomReserveController/updateReserveConferenceRoomToCancellation")
    @ResponseBody
    public void updateReserveConferenceRoomToCancellation(InputObject inputObject, OutputObject outputObject) throws Exception{
        conferenceRoomReserveService.updateReserveConferenceRoomToCancellation(inputObject, outputObject);
    }

    /**
     *
     * @Title: editReserveConferenceRoomToRevoke
     * @Description: 撤销会议室预定申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ConferenceRoomReserveController/editReserveConferenceRoomToRevoke")
    @ResponseBody
    public void editReserveConferenceRoomToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception{
        conferenceRoomReserveService.editReserveConferenceRoomToRevoke(inputObject, outputObject);
    }

}
