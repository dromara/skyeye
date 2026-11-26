package com.skyeye.performance.assessment.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.performance.assessment.entity.PerfAssessment;
import com.skyeye.performance.assessment.entity.PerfAssessmentDetail;

import java.util.List;

public interface PerfAssessmentDetailService extends SkyeyeBusinessService<PerfAssessmentDetail> {

    void deleteByAssessmentId(String assessmentId);

    void saveAssessmentDetails(String userId, PerfAssessment assessment);

    List<PerfAssessmentDetail> queryByAssessmentId(String assessmentId);
}
