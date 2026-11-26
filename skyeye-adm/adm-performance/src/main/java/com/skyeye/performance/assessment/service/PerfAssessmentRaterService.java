package com.skyeye.performance.assessment.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.performance.assessment.entity.PerfAssessment;
import com.skyeye.performance.assessment.entity.PerfAssessmentRater;
import com.skyeye.performance.scheme.entity.PerfScheme;

import java.util.List;
import java.util.Map;

public interface PerfAssessmentRaterService extends SkyeyeBusinessService<PerfAssessmentRater> {

    List<PerfAssessmentRater> queryByAssessmentId(String assessmentId);

    void deleteByAssessmentId(String assessmentId);

    void initRatersForAssessment(PerfAssessment assessment, PerfScheme scheme);

    void initRatersForAssessment(PerfAssessment assessment, PerfScheme scheme, Map<String, Object> staffInfo);

    void queryRaters(InputObject inputObject, OutputObject outputObject);

    void saveRater(InputObject inputObject, OutputObject outputObject);

    void saveRaterScores(InputObject inputObject, OutputObject outputObject);

    void aggregateRaterScores(InputObject inputObject, OutputObject outputObject);

    void deleteRaterById(String id);
}
