/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: CheckWorkLeaveDao
 * @Description: 请假申请数据交互层
 * @author: skyeye云系列--卫志强
 * @date: 2021/5/1 17:55
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface CheckWorkLeaveDao {

    public List<Map<String, Object>> queryMyCheckWorkLeaveList(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryCheckWorkLeaveStateById(@Param("leaveId") String id) throws Exception;

    public Map<String, Object> queryCheckWorkLeaveDetailById(@Param("leaveId") String id) throws Exception;

    public List<Map<String, Object>> queryCheckWorkLeaveDaysDetailByLeaveId(@Param("leaveId") String id) throws Exception;

    public void updateCheckWorkLeaveStateAndProcessInId(@Param("leaveId") String id, @Param("processInId") String processInId, @Param("state") int state) throws Exception;

    public void updateCheckWorkLeaveDaysStateByLeaveId(@Param("leaveId") String id, @Param("state") int state) throws Exception;

    public void insertCheckWorkLeaveMation(Map<String, Object> map) throws Exception;

    public void insertCheckWorkLeaveDaysMation(List<Map<String, Object>> beans) throws Exception;

    public Map<String, Object> queryCheckWorkLeaveToEditById(@Param("leaveId") String id) throws Exception;

    public List<Map<String, Object>> queryCheckWorkLeaveDaysMationToEditByLeaveId(@Param("leaveId") String id) throws Exception;

    public int deleteCheckWorkLeaveDaysMationByLeaveId(@Param("leaveId") String id) throws Exception;

    public void updateCheckWorkLeaveMation(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryCheckWorkLeaveByProcessInstanceId(@Param("processInstanceId") String processInstanceId) throws Exception;

    public void updateCheckWorkLeaveStateById(@Param("leaveId") String id, @Param("state") int state) throws Exception;

    public void updateCheckWorkLeaveSoltStateByLeaveId(@Param("leaveTimeId") String leaveTimeId, @Param("state") int state,
                                                       @Param("useYearHoliday") Integer useYearHoliday) throws Exception;

    /**
     * 获取指定日期已经审核通过的信息
     *
     *
     * @param timeId 班次id
     * @param createId 创建人
     * @param leaveDay 指定日期,格式为：yyyy-MM-dd
     * @return
     * @throws Exception
     */
    public Map<String, Object> queryCheckWorkLeaveByMation(@Param("timeId") String timeId,
        @Param("createId") String createId, @Param("leaveDay") String leaveDay) throws Exception;

    /**
     * 获取指定员工在指定月份和班次的所有审核通过的请假申请数据
     *
     * @param userId 用户id
     * @param timeId 班次id
     * @param months 指定月份，月格式（yyyy-MM）
     * @return
     * @throws Exception
     */
    List<Map<String, Object>> queryStateIsSuccessLeaveDayByUserIdAndMonths(@Param("userId") String userId,
        @Param("timeId") String timeId, @Param("months") List<String> months) throws Exception;

}
