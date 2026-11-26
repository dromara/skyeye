package com.skyeye.performance.assessment.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.util.NumberParseUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.performance.assessment.dao.PerfAssessmentDetailDao;
import com.skyeye.performance.assessment.entity.PerfAssessment;
import com.skyeye.performance.assessment.entity.PerfAssessmentDetail;
import com.skyeye.performance.assessment.service.PerfAssessmentDetailService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@SkyeyeService(name = "绩效考核明细", groupName = "员工绩效", manageShow = false)
public class PerfAssessmentDetailServiceImpl
    extends SkyeyeBusinessServiceImpl<PerfAssessmentDetailDao, PerfAssessmentDetail>
    implements PerfAssessmentDetailService {

    @Override
    public void deleteByAssessmentId(String assessmentId) {
        QueryWrapper<PerfAssessmentDetail> wrapper = new QueryWrapper<>();
        wrapper.eq(MybatisPlusUtil.toColumns(PerfAssessmentDetail::getAssessmentId), assessmentId);
        remove(wrapper);
    }

    @Override
    public void saveAssessmentDetails(String userId, PerfAssessment assessment) {
        deleteByAssessmentId(assessment.getId());
        if (CollectionUtil.isEmpty(assessment.getAssessmentDetailList())) {
            return;
        }
        for (PerfAssessmentDetail detail : assessment.getAssessmentDetailList()) {
            detail.setAssessmentId(assessment.getId());
            if (detail.getScore() != null && detail.getWeight() != null) {
                detail.setWeightedScore(NumberParseUtil.weightedByPercent(
                    detail.getWeight(), detail.getScore(), 2));
            }
        }
        createEntity(assessment.getAssessmentDetailList(), userId);
    }

    @Override
    public List<PerfAssessmentDetail> queryByAssessmentId(String assessmentId) {
        QueryWrapper<PerfAssessmentDetail> wrapper = new QueryWrapper<>();
        wrapper.eq(MybatisPlusUtil.toColumns(PerfAssessmentDetail::getAssessmentId), assessmentId);
        wrapper.orderByAsc(MybatisPlusUtil.toColumns(PerfAssessmentDetail::getOrderBy));
        return list(wrapper);
    }
}
