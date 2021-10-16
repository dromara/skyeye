/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.activitifactory;

import com.skyeye.activiti.factory.ActivitiFactory;
import com.skyeye.activiti.factory.ActivitiFactoryResult;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.SpringUtils;
import com.skyeye.eve.dao.ConferenceRoomReserveDao;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ConferenceRoomReserveFactory
 * @Description: 会议室预定工厂类
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/1 16:46
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class ConferenceRoomReserveFactory extends ActivitiFactory implements ActivitiFactoryResult {

    private ConferenceRoomReserveDao conferenceRoomReserveDao;

    public ConferenceRoomReserveFactory(InputObject inputObject, OutputObject outputObject, String key) {
        super(inputObject, outputObject, key);
    }

    public ConferenceRoomReserveFactory(String key) {
        super(key);
    }

    @Override
    protected void initChildObject() {
        conferenceRoomReserveDao = SpringUtils.getBean(ConferenceRoomReserveDao.class);
    }

    /**
     * 获取和工作流相关操作的列表时获取数据
     *
     * @param inputParams 前台传递的入参
     * @return 结果
     * @throws Exception
     */
    @Override
    protected List<Map<String, Object>> queryWithActivitiListRunSql(Map<String, Object> inputParams) throws Exception {
        return conferenceRoomReserveDao.selectMyReserveConferenceRoomList(inputParams);
    }

    /**
     * 提交数据到工作流时获取数据
     *
     * @param id 需要提交到工作流的主单据id
     * @return
     * @throws Exception
     */
    @Override
    protected Map<String, Object> submitToActiviGetDate(String id) throws Exception {
        Map<String, Object> conferenceRoomReserve = conferenceRoomReserveDao.queryConferenceRoomReserveMationById(id);
        // 获取附件信息
        conferenceRoomReserve.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(conferenceRoomReserve.get("enclosureInfo").toString()));
        return conferenceRoomReserve;
    }

    /**
     * 提交数据到工作流后成功的回调函数
     *
     * @param id          需要提交到工作流的主单据id
     * @param processInId 提交成功后的流程实例id
     * @throws Exception
     */
    @Override
    protected void submitToActiviSuccess(String id, String processInId) throws Exception {
        // 修改为审核中
        conferenceRoomReserveDao.updateConferenceRoomReserveStateISInAudit(id, processInId);
    }

    /**
     * 撤销工作流成功后会执行的回调
     *
     * @param map 入参
     * @throws Exception
     */
    @Override
    protected void revokeActiviSuccess(Map<String, Object> map) throws Exception {
        String processInstanceId = map.get("processInstanceId").toString();
        map = conferenceRoomReserveDao.queryConferenceRoomReserveByProcessInstanceId(processInstanceId);
        // 撤销
        map.put("approvalState", '5');
        // 待审批
        map.put("reserveState", '0');
        conferenceRoomReserveDao.updateConferenceRoomReserveToRevokeById(map);
    }
}
