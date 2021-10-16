/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: WagesPaymentHistoryDao
 * @Description: 薪资发放历史管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 23:35
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface WagesPaymentHistoryDao {

    public List<Map<String, Object>> queryAllWagesPaymentHistoryList(Map<String, Object> map) throws Exception;

    public List<Map<String, Object>> queryMyWagesPaymentHistoryList(Map<String, Object> map) throws Exception;

    public void insertWagesPaymentHistoryMation(Map<String, Object> map) throws Exception;

    /**
     * 随机获取一条未发放的员工薪资信息
     *
     * @param lastMonthDate 上个月的年月
     * @param staffId 不包含的员工id
     * @return
     * @throws Exception
     */
    public Map<String, Object> queryWaitPaymentStaffHistory(@Param("lastMonthDate") String lastMonthDate, @Param("list") List<String> staffId) throws Exception;

    /**
     * 根据员工id以及薪资年月修改薪资状态为已发放
     *
     * @param staffId 员工id
     * @param payMonth 薪资年月
     * @throws Exception
     */
    public void editToPaymentByStaffAndPayMonth(@Param("staffId") String staffId, @Param("payMonth") String payMonth) throws Exception;

}
