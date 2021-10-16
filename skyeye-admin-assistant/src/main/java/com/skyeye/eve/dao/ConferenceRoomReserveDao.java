/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ConferenceRoomReserveDao
 * @Description: 会议室预定申请数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/1 14:18
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ConferenceRoomReserveDao {

    public List<Map<String, Object>> selectMyReserveConferenceRoomList(Map<String, Object> map) throws Exception;

    public int insertConferenceRoomReserveMation(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryConferenceRoomReserveMationById(@Param("id") String id) throws Exception;

    public Map<String, Object> queryConferenceRoomReserveToEditById(Map<String, Object> map) throws Exception;

    public int updateConferenceRoomReserveToCancellation(Map<String, Object> map) throws Exception;

    public int updateConferenceRoomReserveStateISInAudit(@Param("id") String id, @Param("processInId") String processInId) throws Exception;

    public Map<String, Object> queryConferenceRoomReserveByProcessInstanceId(@Param("processInstanceId") String processInstanceId) throws Exception;

    public List<Map<String, Object>> queryConferenceRoomReserveListByTime(Map<String, Object> map) throws Exception;

    public int updateConferenceRoomReserveStateById(Map<String, Object> map) throws Exception;

    public int updateConferenceRoomReserveToRevokeById(Map<String, Object> map) throws Exception;

}
