/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import com.skyeye.activiti.factory.ActivitiRunFactory;
import com.skyeye.activiti.service.ActivitiUserService;
import com.skyeye.annotation.transaction.ActivitiAndBaseTransaction;
import com.skyeye.common.constans.ActivitiConstants;
import com.skyeye.common.constans.AdminAssistantConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.ConferenceRoomDao;
import com.skyeye.eve.dao.ConferenceRoomReserveDao;
import com.skyeye.eve.dao.SysEnclosureDao;
import com.skyeye.eve.service.ConferenceRoomReserveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ConferenceRoomReserveServiceImpl
 * @Description: 会议室预定申请服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/1 14:19
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class ConferenceRoomReserveServiceImpl implements ConferenceRoomReserveService {

    @Autowired
    private ConferenceRoomDao conferenceRoomDao;

    @Autowired
    private ConferenceRoomReserveDao conferenceRoomReserveDao;

    @Autowired
    private SysEnclosureDao sysEnclosureDao;

    @Autowired
    private ActivitiUserService activitiUserService;

    /**
     * 会议室预定关联的工作流的key
     */
    private static final String ACTIVITI_CONFERENCEROOM_USE_PAGE_KEY = ActivitiConstants.ActivitiObjectType.ACTIVITI_CONFERENCEROOM_USE_PAGE.getKey();

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
    @Override
    public void queryMyReserveConferenceRoomList(InputObject inputObject, OutputObject outputObject) throws Exception {
        ActivitiRunFactory.run(inputObject, outputObject, ACTIVITI_CONFERENCEROOM_USE_PAGE_KEY).queryWithActivitiList();
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
    @Override
    @ActivitiAndBaseTransaction(value = {"activitiTransactionManager", "transactionManager"})
    public void insertReserveConferenceRoomMation(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        // 获取该会议室预定时间段内已被其他人预定的数据
        List<Map<String, Object>> beans = conferenceRoomReserveDao.queryConferenceRoomReserveListByTime(map);
        if(beans == null || beans.isEmpty()){
            Map<String, Object> user = inputObject.getLogParams();//用户信息
            String rowId = ToolUtil.getSurFaceId();
            map.put("id", rowId);
            map.put("createId", user.get("id").toString());
            map.put("createTime", DateUtil.getTimeAndToString());
            map.put("oddNumber", AdminAssistantConstants.AdminAssistantType.CONFERENCE_ROOM_RESERVE_ODD_NUMBER.getOrderNum());
            map.put("approvalState", 0);//审批状态-草稿
            map.put("reserveState", 0);//预定状态-待审批
            conferenceRoomReserveDao.insertConferenceRoomReserveMation(map);
            // 操作工作流数据
            activitiUserService.addOrEditToSubmit(inputObject, outputObject, Integer.parseInt(map.get("subType").toString()),
                ACTIVITI_CONFERENCEROOM_USE_PAGE_KEY, rowId, map.get("approvalId").toString());
        }else{
            outputObject.setreturnMessage("会议室已被预定，请更换会议室或更改预定时间！");
        }
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
    @Override
    public void queryReserveConferenceRoomMationToDetails(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        // 获取预定信息
        Map<String, Object> bean = conferenceRoomReserveDao.queryConferenceRoomReserveMationById(id);
        Integer state = Integer.parseInt(bean.get("state").toString());
        bean.put("stateName", ActivitiConstants.ActivitiState.getStateNameByState(state));
        // 获取附件信息
        bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
        outputObject.setBean(bean);
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
    @Override
    public void queryReserveConferenceRoomMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        // 获取预定信息
        Map<String, Object> bean = conferenceRoomReserveDao.queryConferenceRoomReserveToEditById(map);
        // 获取附件信息
        bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
        outputObject.setBean(bean);
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
    @Override
    @ActivitiAndBaseTransaction(value = {"activitiTransactionManager", "transactionManager"})
    public void updateReserveConferenceRoomMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        // 获取该会议室预定时间段内已被其他人预定的数据
        List<Map<String, Object>> beans = conferenceRoomReserveDao.queryConferenceRoomReserveListByTime(map);
        if(beans == null || beans.isEmpty()){
            conferenceRoomDao.updateLicenceBorrowMation(map);
            // 操作工作流数据
            activitiUserService.addOrEditToSubmit(inputObject, outputObject, Integer.parseInt(map.get("subType").toString()),
                ACTIVITI_CONFERENCEROOM_USE_PAGE_KEY, id, map.get("approvalId").toString());
        }else{
            outputObject.setreturnMessage("会议室已被预定，请更换会议室或更改预定时间！");
        }
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
    @Override
    @ActivitiAndBaseTransaction(value = {"activitiTransactionManager", "transactionManager"})
    public void editReserveConferenceRoomToSubApproval(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        // 查询该预定申请的信息
        Map<String, Object> bean = conferenceRoomReserveDao.queryConferenceRoomReserveMationById(id);
        int state = Integer.parseInt(bean.get("state").toString());
        if(ActivitiConstants.ActivitiState.DRAFT.getState() == state
                || ActivitiConstants.ActivitiState.NO_PASS.getState() == state
                || ActivitiConstants.ActivitiState.REVOKE.getState() == state){
            // 草稿、审核不通过或者撤销状态下可以提交审批
            activitiUserService.addOrEditToSubmit(inputObject, outputObject, 2,
                ACTIVITI_CONFERENCEROOM_USE_PAGE_KEY, id, map.get("approvalId").toString());
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
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
    @Override
    @Transactional(value="transactionManager")
    public void updateReserveConferenceRoomToCancellation(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        // 查询该预定申请的信息
        Map<String, Object> bean = conferenceRoomReserveDao.queryConferenceRoomReserveMationById(id);
        int state = Integer.parseInt(bean.get("state").toString());
        if(ActivitiConstants.ActivitiState.DRAFT.getState() == state
                || ActivitiConstants.ActivitiState.NO_PASS.getState() == state
                || ActivitiConstants.ActivitiState.REVOKE.getState() == state){
            // 草稿、审核不通过或者撤销状态下可以作废
            conferenceRoomReserveDao.updateConferenceRoomReserveToCancellation(map);
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
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
    @Override
    @ActivitiAndBaseTransaction(value = {"activitiTransactionManager", "transactionManager"})
    public void editReserveConferenceRoomToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception {
        ActivitiRunFactory.run(inputObject, outputObject, ACTIVITI_CONFERENCEROOM_USE_PAGE_KEY).revokeActivi();
    }

}
