package com.skyeye.performance.assessment.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.performance.assessment.entity.PerfAssessment;
import com.skyeye.performance.cycle.entity.PerfCycle;

public interface PerfAssessmentService extends SkyeyeBusinessService<PerfAssessment> {

    int generateByCycle(PerfCycle cycle, String userId);

    void queryPerformanceDashboard(InputObject inputObject, OutputObject outputObject);

    long countByCycleId(String cycleId);

    long countBySchemeId(String schemeId);

    void fillAutoScores(InputObject inputObject, OutputObject outputObject);
}
