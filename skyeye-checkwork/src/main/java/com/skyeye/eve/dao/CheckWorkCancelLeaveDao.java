/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: CheckWorkCancelLeaveDao
 * @Description: 销假申请数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/11 9:48
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
public interface CheckWorkCancelLeaveDao {

    public List<Map<String, Object>> queryMyCheckWorkCancelLeaveList(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryCheckWorkCancelLeaveDetailById(@Param("cancelLeaveId") String cancelLeaveId) throws Exception;

    public List<Map<String, Object>> queryCheckWorkCancelLeaveDaysDetailByCancelLeaveId(@Param("cancelLeaveId") String cancelLeaveId) throws Exception;

    public void updateCheckWorkCancelLeaveStateAndProcessInId(@Param("cancelLeaveId") String cancelLeaveId, @Param("processInId") String processInId, @Param("state") int state) throws Exception;

    public void updateCheckWorkCancelLeaveDaysStateByCancelLeaveId(@Param("cancelLeaveId") String cancelLeaveId, @Param("state") int state) throws Exception;

    public void insertCheckWorkCancelLeaveMation(Map<String, Object> map) throws Exception;

    public void insertCheckWorkCancelLeaveDaysMation(List<Map<String, Object>> beans) throws Exception;

    public Map<String, Object> queryCheckWorkCancelLeaveToEditById(@Param("cancelLeaveId") String cancelLeaveId) throws Exception;

    public List<Map<String, Object>> queryCheckWorkCancelLeaveDaysMationToEditByCancelLeaveId(@Param("cancelLeaveId") String cancelLeaveId) throws Exception;

    public int deleteCheckWorkCancelLeaveDaysMationByCancelLeaveId(@Param("cancelLeaveId") String cancelLeaveId) throws Exception;

    public void updateCheckWorkCancelLeaveMation(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryCheckWorkCancelLeaveByProcessInstanceId(@Param("processInstanceId") String processInstanceId) throws Exception;

    public void updateCheckWorkCancelLeaveStateById(@Param("cancelLeaveId") String cancelLeaveId, @Param("state") int state) throws Exception;

    public void updateCheckWorkCancelLeaveSoltStateById(@Param("cancelLeaveTimeId") String cancelLeaveTimeId, @Param("state") int state) throws Exception;

    /**
     * 获取指定日期已经审核通过的信息
     *
     * @param createId 创建人
     * @param cancelDay 指定日期
     * @return
     * @throws Exception
     */
    public Map<String, Object> queryCheckWorkCancelLeaveByMation(@Param("createId") String createId,
        @Param("cancelDay") String cancelDay) throws Exception;
}
