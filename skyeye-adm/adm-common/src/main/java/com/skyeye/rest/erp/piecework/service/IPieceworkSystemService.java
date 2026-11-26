package com.skyeye.rest.erp.piecework.service;

import java.util.List;
import java.util.Map;

public interface IPieceworkSystemService {

    /**
     * 查询员工在月份区间内的计件记录
     */
    List<Map<String, Object>> queryByStaffAndMonths(String staffId, String farmId, String startMonth, String endMonth);
}
