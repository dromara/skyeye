/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: CheckWorkOvertimeDao
 * @Description: 加班申请数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/8 22:29
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
public interface CheckWorkOvertimeDao {

    public List<Map<String, Object>> queryMyCheckWorkOvertimeList(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryCheckWorkOvertimeStateById(@Param("overtimeId") String overtimeId) throws Exception;

    public Map<String, Object> queryCheckWorkOvertimeDetailById(@Param("overtimeId") String overtimeId) throws Exception;

    public List<Map<String, Object>> queryCheckWorkOvertimeDaysDetailByOvertimeId(@Param("overtimeId") String overtimeId) throws Exception;

    public void updateCheckWorkOvertimeStateAndProcessInId(@Param("overtimeId") String overtimeId, @Param("processInId") String processInId, @Param("state") int state) throws Exception;

    public void updateCheckWorkOvertimeDaysStateByOvertimeId(@Param("overtimeId") String overtimeId, @Param("state") int state) throws Exception;

    public void insertCheckWorkOvertimeMation(Map<String, Object> map) throws Exception;

    public void insertCheckWorkOvertimeDaysMation(List<Map<String, Object>> beans) throws Exception;

    public Map<String, Object> queryCheckWorkOvertimeToEditById(@Param("overtimeId") String overtimeId) throws Exception;

    public List<Map<String, Object>> queryCheckWorkOvertimeDaysMationToEditByOvertimeId(@Param("overtimeId") String overtimeId) throws Exception;

    public int deleteCheckWorkOvertimeDaysMationByOvertimeId(@Param("overtimeId") String overtimeId) throws Exception;

    public void updateCheckWorkOvertimeMation(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryCheckWorkOvertimeByProcessInstanceId(@Param("processInstanceId") String processInstanceId) throws Exception;

    public void updateCheckWorkOvertimeStateById(@Param("overtimeId") String overtimeId, @Param("state") int state) throws Exception;

    public void updateCheckWorkOvertimeSoltStateByOvertimeId(@Param("overtimeSoltId") String overtimeSoltId, @Param("state") int state) throws Exception;

    /**
     * 获取指定员工在指定天是否有审批通过的数据
     *
     * @param createId 创建人
     * @param overtimeDay 指定天
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> queryPassThisDayAndCreateId(@Param("createId") String createId,
                                                                 @Param("overtimeDay") String overtimeDay) throws Exception;

    /**
     * 获取指定员工在指定月份的所有审核通过的加班申请数据
     *
     * @param userId 用户id
     * @param months 指定月份，月格式（yyyy-MM）
     * @return
     * @throws Exception
     */
    List<Map<String, Object>> queryStateIsSuccessWorkOvertimeDayByUserIdAndMonths(@Param("userId") String userId,
        @Param("months") List<String> months) throws Exception;

    /**
     * 修改加班电子流的结算类型
     *
     * @param overtimeSoltId 加班id
     * @param overtimeSettlementType 结算类型
     * @throws Exception
     */
    void updateOvertimeSettlementType(@Param("overtimeSoltId") String overtimeSoltId,
                                      @Param("overtimeSettlementType") int overtimeSettlementType) throws Exception;

    /**
     * 修改加班电子流的结算状态
     *
     * @param overtimeSoltId 加班id
     * @param settleState 加班是否计入补休/薪资结算状态  1.待计入统计  2.已计入统计
     * @throws Exception
     */
    void updateOvertimeSettleState(@Param("overtimeSoltId") String overtimeSoltId,
                                   @Param("settleState") int settleState) throws Exception;

    /**
     * 获取所有待结算的加班数据
     *
     * @return
     * @throws Exception
     */
    List<Map<String, Object>> queryCheckWorkOvertimeWaitSettlement() throws Exception;

}
