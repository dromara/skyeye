package com.skyeye.performance.assessment.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.performance.assessment.dao.PerfAssessmentRaterScoreDao;
import com.skyeye.performance.assessment.entity.PerfAssessmentRaterScore;
import com.skyeye.performance.assessment.service.PerfAssessmentRaterScoreService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@SkyeyeService(name = "360评价得分", groupName = "员工绩效", manageShow = false)
public class PerfAssessmentRaterScoreServiceImpl
    extends SkyeyeBusinessServiceImpl<PerfAssessmentRaterScoreDao, PerfAssessmentRaterScore>
    implements PerfAssessmentRaterScoreService {

    @Override
    public List<PerfAssessmentRaterScore> queryByAssessmentId(String assessmentId) {
        QueryWrapper<PerfAssessmentRaterScore> wrapper = new QueryWrapper<>();
        wrapper.eq(MybatisPlusUtil.toColumns(PerfAssessmentRaterScore::getAssessmentId), assessmentId);
        return list(wrapper);
    }

    @Override
    public List<PerfAssessmentRaterScore> queryByRaterId(String raterId) {
        QueryWrapper<PerfAssessmentRaterScore> wrapper = new QueryWrapper<>();
        wrapper.eq(MybatisPlusUtil.toColumns(PerfAssessmentRaterScore::getRaterId), raterId);
        return list(wrapper);
    }

    @Override
    public void deleteByAssessmentId(String assessmentId) {
        QueryWrapper<PerfAssessmentRaterScore> wrapper = new QueryWrapper<>();
        wrapper.eq(MybatisPlusUtil.toColumns(PerfAssessmentRaterScore::getAssessmentId), assessmentId);
        remove(wrapper);
    }

    @Override
    public void deleteByRaterId(String raterId) {
        QueryWrapper<PerfAssessmentRaterScore> wrapper = new QueryWrapper<>();
        wrapper.eq(MybatisPlusUtil.toColumns(PerfAssessmentRaterScore::getRaterId), raterId);
        remove(wrapper);
    }

    @Override
    public void saveScores(String assessmentId, String raterId, List<PerfAssessmentRaterScore> scoreList) {
        deleteByRaterId(raterId);
        if (CollectionUtil.isEmpty(scoreList)) {
            return;
        }
        for (PerfAssessmentRaterScore score : scoreList) {
            score.setId(null);
            score.setAssessmentId(assessmentId);
            score.setRaterId(raterId);
        }
        createEntity(scoreList, StrUtil.EMPTY);
    }
}
