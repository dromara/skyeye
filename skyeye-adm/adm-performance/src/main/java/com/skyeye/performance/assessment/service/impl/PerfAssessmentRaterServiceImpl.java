package com.skyeye.performance.assessment.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.JsonConfigUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.performance.assessment.dao.PerfAssessmentRaterDao;
import com.skyeye.performance.assessment.entity.PerfAssessment;
import com.skyeye.performance.assessment.entity.PerfAssessmentDetail;
import com.skyeye.performance.assessment.entity.PerfAssessmentRater;
import com.skyeye.performance.assessment.entity.PerfAssessmentRaterScore;
import com.skyeye.performance.assessment.service.PerfAssessmentRaterScoreService;
import com.skyeye.performance.assessment.service.PerfAssessmentRaterService;
import com.skyeye.performance.assessment.service.PerfAssessmentService;
import com.skyeye.performance.classenum.PerfRaterRole;
import com.skyeye.performance.classenum.PerfSchemeType;
import com.skyeye.performance.scheme.entity.PerfScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "360评价人", groupName = "员工绩效", manageShow = false)
public class PerfAssessmentRaterServiceImpl
    extends SkyeyeBusinessServiceImpl<PerfAssessmentRaterDao, PerfAssessmentRater>
    implements PerfAssessmentRaterService {

    @Autowired
    private PerfAssessmentRaterScoreService perfAssessmentRaterScoreService;

    @Autowired
    @Lazy
    private PerfAssessmentService perfAssessmentService;

    @Override
    public List<PerfAssessmentRater> queryByAssessmentId(String assessmentId) {
        QueryWrapper<PerfAssessmentRater> wrapper = new QueryWrapper<>();
        wrapper.eq(MybatisPlusUtil.toColumns(PerfAssessmentRater::getAssessmentId), assessmentId);
        List<PerfAssessmentRater> list = list(wrapper);
        if (CollectionUtil.isEmpty(list)) {
            return list;
        }
        List<String> staffIds = list.stream().map(PerfAssessmentRater::getRaterStaffId).distinct().collect(Collectors.toList());
        Map<String, Map<String, Object>> staffMap = iAuthUserService.queryUserMationListByStaffIds(staffIds);
        List<PerfAssessmentRaterScore> allScores = perfAssessmentRaterScoreService.queryByAssessmentId(assessmentId);
        Map<String, List<PerfAssessmentRaterScore>> scoreMap = allScores.stream()
            .collect(Collectors.groupingBy(PerfAssessmentRaterScore::getRaterId));
        for (PerfAssessmentRater rater : list) {
            rater.setRaterStaffMation(staffMap.get(rater.getRaterStaffId()));
            rater.setRaterRoleMation(PerfRaterRole.getMation(rater.getRaterRole()));
            rater.setScoreList(scoreMap.getOrDefault(rater.getId(), new ArrayList<>()));
        }
        return list;
    }

    @Override
    public void deleteByAssessmentId(String assessmentId) {
        perfAssessmentRaterScoreService.deleteByAssessmentId(assessmentId);
        QueryWrapper<PerfAssessmentRater> wrapper = new QueryWrapper<>();
        wrapper.eq(MybatisPlusUtil.toColumns(PerfAssessmentRater::getAssessmentId), assessmentId);
        remove(wrapper);
    }

    @Override
    public void initRatersForAssessment(PerfAssessment assessment, PerfScheme scheme) {
        initRatersForAssessment(assessment, scheme, null);
    }

    @Override
    public void initRatersForAssessment(PerfAssessment assessment, PerfScheme scheme, Map<String, Object> staffInfo) {
        if (assessment == null || scheme == null
            || !PerfSchemeType.DEGREE360.getKey().equals(scheme.getSchemeType())) {
            return;
        }
        List<JSONObject> roles = JsonConfigUtil.parseObjList(scheme.getRaterRule());
        if (CollectionUtil.isEmpty(roles)) {
            roles = defaultRaterRule();
        }
        Map<String, Object> staff = staffInfo;
        if (staff == null) {
            Map<String, Map<String, Object>> staffMap =
                iAuthUserService.queryUserMationListByStaffIds(java.util.Arrays.asList(assessment.getObjectId()));
            staff = staffMap.getOrDefault(assessment.getObjectId(), new HashMap<>());
        }
        String userId = StrUtil.EMPTY;
        for (JSONObject roleCfg : roles) {
            String role = roleCfg.getStr("role", PerfRaterRole.SELF.getKey());
            BigDecimal weight = roleCfg.getBigDecimal("weight", BigDecimal.ZERO);
            String raterStaffId = null;
            if (PerfRaterRole.SELF.getKey().equals(role)) {
                raterStaffId = assessment.getObjectId();
            } else if (PerfRaterRole.SUPERIOR.getKey().equals(role)) {
                Object leader = staff.get("leaderStaffId");
                if (leader != null && StrUtil.isNotEmpty(leader.toString())) {
                    raterStaffId = leader.toString();
                }
            }
            if (StrUtil.isEmpty(raterStaffId)) {
                continue;
            }
            PerfAssessmentRater rater = new PerfAssessmentRater();
            rater.setAssessmentId(assessment.getId());
            rater.setRaterStaffId(raterStaffId);
            rater.setRaterRole(role);
            rater.setWeight(weight);
            rater.setState("draft");
            createEntity(rater, userId);
        }
    }

    @Override
    public void queryRaters(InputObject inputObject, OutputObject outputObject) {
        String assessmentId = inputObject.getParams().get("assessmentId").toString();
        List<PerfAssessmentRater> list = queryByAssessmentId(assessmentId);
        outputObject.setBeans(list);
        outputObject.settotal(list.size());
    }

    @Override
    public void saveRater(InputObject inputObject, OutputObject outputObject) {
        PerfAssessmentRater rater = inputObject.getParams(PerfAssessmentRater.class);
        if (StrUtil.isEmpty(rater.getState())) {
            rater.setState("draft");
        }
        String userId = inputObject.getLogParams().get("id").toString();
        if (StrUtil.isEmpty(rater.getId())) {
            createEntity(rater, userId);
        } else {
            updateEntity(rater, userId);
        }
        outputObject.setBean(selectById(rater.getId()));
    }

    @Override
    public void saveRaterScores(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String assessmentId = params.get("assessmentId").toString();
        String raterId = params.get("raterId").toString();
        Object scoreObj = params.get("scoreList");
        List<PerfAssessmentRaterScore> scoreList = JSONUtil.toList(JSONUtil.parseArray(JSONUtil.toJsonStr(scoreObj)),
            PerfAssessmentRaterScore.class);
        PerfAssessmentRater rater = selectById(raterId);
        if (rater == null || !assessmentId.equals(rater.getAssessmentId())) {
            throw new CustomException("评价人不存在");
        }
        perfAssessmentRaterScoreService.saveScores(assessmentId, raterId, scoreList);
        rater.setState("done");
        updateById(rater);
        List<PerfAssessmentRater> list = queryByAssessmentId(assessmentId);
        outputObject.setBeans(list);
        outputObject.settotal(list.size());
    }

    @Override
    public void deleteRaterById(String id) {
        perfAssessmentRaterScoreService.deleteByRaterId(id);
        deleteById(id);
    }

    @Override
    public void aggregateRaterScores(InputObject inputObject, OutputObject outputObject) {
        String assessmentId = inputObject.getParams().get("id").toString();
        String userId = inputObject.getLogParams().get("id").toString();
        PerfAssessment assessment = perfAssessmentService.selectById(assessmentId);
        if (assessment == null) {
            throw new CustomException("考核单不存在");
        }
        List<PerfAssessmentRater> raters = queryByAssessmentId(assessmentId);
        if (CollectionUtil.isEmpty(raters)) {
            throw new CustomException("请先配置评价人");
        }
        List<PerfAssessmentDetail> details = assessment.getAssessmentDetailList();
        if (CollectionUtil.isEmpty(details)) {
            throw new CustomException("考核明细为空");
        }
        for (PerfAssessmentDetail detail : details) {
            BigDecimal weightedSum = BigDecimal.ZERO;
            BigDecimal weightSum = BigDecimal.ZERO;
            for (PerfAssessmentRater rater : raters) {
                if (CollectionUtil.isEmpty(rater.getScoreList()) || rater.getWeight() == null
                    || rater.getWeight().compareTo(BigDecimal.ZERO) <= 0) {
                    continue;
                }
                PerfAssessmentRaterScore hit = rater.getScoreList().stream()
                    .filter(s -> StrUtil.equals(s.getSchemeItemId(), detail.getSchemeItemId())
                        || StrUtil.equals(s.getItemName(), detail.getName()))
                    .findFirst().orElse(null);
                if (hit == null || hit.getScore() == null) {
                    continue;
                }
                weightedSum = weightedSum.add(hit.getScore().multiply(rater.getWeight()));
                weightSum = weightSum.add(rater.getWeight());
            }
            if (weightSum.compareTo(BigDecimal.ZERO) > 0) {
                detail.setScore(weightedSum.divide(weightSum, 2, RoundingMode.HALF_UP));
                detail.setRemark(StrUtil.blankToDefault(detail.getRemark(), "360加权汇总"));
            }
        }
        assessment.setAssessmentDetailList(details);
        perfAssessmentService.updateEntity(assessment, userId);
        outputObject.setBean(perfAssessmentService.selectById(assessmentId));
        outputObject.setreturnMessage("success");
    }

    public static List<JSONObject> defaultRaterRule() {
        List<JSONObject> list = new ArrayList<>();
        list.add(JSONUtil.createObj().set("role", PerfRaterRole.SELF.getKey()).set("weight", 20));
        list.add(JSONUtil.createObj().set("role", PerfRaterRole.SUPERIOR.getKey()).set("weight", 50));
        list.add(JSONUtil.createObj().set("role", PerfRaterRole.PEER.getKey()).set("weight", 30));
        return list;
    }
}
