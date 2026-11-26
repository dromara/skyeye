package com.skyeye.piecework.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.piecework.entity.PieceworkSystem;

import java.util.List;

public interface PieceworkSystemService extends SkyeyeBusinessService<PieceworkSystem> {

    void writePieceworkSystem();

    void queryPieceworkSystem(InputObject inputObject, OutputObject outputObject);

    /**
     * 按员工 + 可选车间 + 月份区间查询计件汇总行（供绩效自动取分）
     */
    void queryPieceworkByStaffAndMonths(InputObject inputObject, OutputObject outputObject);

    List<PieceworkSystem> listByStaffAndMonths(String staffId, String farmId, String startMonth, String endMonth);
}
