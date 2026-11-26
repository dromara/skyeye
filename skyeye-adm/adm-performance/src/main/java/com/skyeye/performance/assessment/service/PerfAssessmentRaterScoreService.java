package com.skyeye.performance.assessment.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.performance.assessment.entity.PerfAssessmentRaterScore;

import java.util.List;

public interface PerfAssessmentRaterScoreService extends SkyeyeBusinessService<PerfAssessmentRaterScore> {

    List<PerfAssessmentRaterScore> queryByAssessmentId(String assessmentId);

    List<PerfAssessmentRaterScore> queryByRaterId(String raterId);

    void deleteByAssessmentId(String assessmentId);

    void deleteByRaterId(String raterId);

    void saveScores(String assessmentId, String raterId, List<PerfAssessmentRaterScore> scoreList);
}
