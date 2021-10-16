/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * @ClassName: ConferenceRoomReserveService
 * @Description: 会议室预定申请服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/1 14:18
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ConferenceRoomReserveService {

    public void queryMyReserveConferenceRoomList(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void insertReserveConferenceRoomMation(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryReserveConferenceRoomMationToDetails(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryReserveConferenceRoomMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void updateReserveConferenceRoomMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void updateReserveConferenceRoomMationToSave(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editReserveConferenceRoomToSubApproval(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void updateReserveConferenceRoomToCancellation(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editReserveConferenceRoomToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception;

}
