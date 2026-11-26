package com.skyeye.performance.cycle.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.performance.cycle.entity.PerfCycle;

public interface PerfCycleService extends SkyeyeBusinessService<PerfCycle> {

    /**
     * 按周期生成考核单
     */
    void generateAssessments(InputObject inputObject, OutputObject outputObject);

    void closeCycle(InputObject inputObject, OutputObject outputObject);
}
