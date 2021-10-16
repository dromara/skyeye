/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: CheckWorkBusinessTripDao
 * @Description: 出差申请数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/6 22:02
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
public interface CheckWorkBusinessTripDao {

    List<Map<String, Object>> queryMyCheckWorkBusinessTripList(Map<String, Object> map) throws Exception;

    Map<String, Object> queryCheckWorkBusinessTripDetailById(@Param("businessTripId") String businessTripId) throws Exception;

    List<Map<String, Object>> queryCheckWorkBusinessTripDaysDetailByBusinessTripId(@Param("businessTripId") String businessTripId) throws Exception;

    void updateCheckWorkBusinessTripStateAndProcessInId(@Param("businessTripId") String businessTripId, @Param("processInId") String processInId, @Param("state") int state) throws Exception;

    void updateCheckWorkBusinessTripDaysStateByBusinessTripId(@Param("businessTripId") String businessTripId, @Param("state") int state) throws Exception;

    void insertCheckWorkBusinessTripMation(Map<String, Object> map) throws Exception;

    void insertCheckWorkBusinessTripDaysMation(List<Map<String, Object>> beans) throws Exception;

    Map<String, Object> queryCheckWorkBusinessTripToEditById(@Param("businessTripId") String businessTripId) throws Exception;

    List<Map<String, Object>> queryCheckWorkBusinessTripDaysMationToEditByBusinessTripId(@Param("businessTripId") String businessTripId) throws Exception;

    int deleteCheckWorkBusinessTripDaysMationByBusinessTripId(@Param("businessTripId") String businessTripId) throws Exception;

    void updateCheckWorkBusinessTripMation(Map<String, Object> map) throws Exception;

    Map<String, Object> queryCheckWorkBusinessTripByProcessInstanceId(@Param("processInstanceId") String processInstanceId) throws Exception;

    void updateCheckWorkBusinessTripStateById(@Param("businessTripId") String businessTripId, @Param("state") int state) throws Exception;

    void updateCheckWorkBusinessTripSoltStateByBusinessTripTimeId(@Param("businessTripTimeId") String businessTripTimeId, @Param("state") int state) throws Exception;

    /**
     * 获取指定员工在指定班次的指定天是否有审批通过的数据
     *
     * @param createId 创建人
     * @param timeId 班次id
     * @param businessTravelDay 指定天
     * @return
     * @throws Exception
     */
    List<Map<String, Object>> queryPassThisTimeAndDayAndCreateId(@Param("createId") String createId,
        @Param("timeId") String timeId, @Param("businessTravelDay") String businessTravelDay) throws Exception;

    /**
     * 获取指定员工在指定月份和班次的所有审核通过的出差申请数据
     * 
     * @param userId 用户id
     * @param timeId 班次id
     * @param months 指定月份，月格式（yyyy-MM）
     * @return
     * @throws Exception
     */
    List<Map<String, Object>> queryStateIsSuccessBusinessTripDayByUserIdAndMonths(@Param("userId") String userId,
        @Param("timeId") String timeId, @Param("months") List<String> months) throws Exception;
}
