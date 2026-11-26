package com.skyeye.performance.assessment.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.enumeration.NoticeUserMessageTypeEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.NumberParseUtil;
import com.skyeye.common.util.ScoreBandUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.rest.notice.NoticeDispatchBody;
import com.skyeye.eve.service.INoticeDispatchService;
import com.skyeye.exception.CustomException;
import com.skyeye.performance.assessment.service.PerfAutoScoreHelper;
import com.skyeye.performance.classenum.PerfCycleState;
import com.skyeye.performance.assessment.dao.PerfAssessmentDao;
import com.skyeye.performance.assessment.entity.PerfAssessment;
import com.skyeye.performance.assessment.entity.PerfAssessmentDetail;
import com.skyeye.performance.assessment.service.PerfAssessmentDetailService;
import com.skyeye.performance.assessment.service.PerfAssessmentRaterService;
import com.skyeye.performance.assessment.service.PerfAssessmentService;
import com.skyeye.performance.classenum.PerfItemSourceType;
import com.skyeye.performance.cycle.entity.PerfCycle;
import com.skyeye.performance.cycle.service.PerfCycleService;
import com.skyeye.performance.scheme.entity.PerfScheme;
import com.skyeye.performance.scheme.entity.PerfSchemeItem;
import com.skyeye.performance.scheme.service.PerfSchemeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "员工绩效考核单", groupName = "员工绩效", flowable = true)
public class PerfAssessmentServiceImpl extends SkyeyeBusinessServiceImpl<PerfAssessmentDao, PerfAssessment>
    implements PerfAssessmentService {

    @Autowired
    private PerfAssessmentDetailService perfAssessmentDetailService;

    @Autowired
    private PerfSchemeService perfSchemeService;

    @Autowired
    @Lazy
    private PerfCycleService perfCycleService;

    @Autowired(required = false)
    private INoticeDispatchService iNoticeDispatchService;

    @Autowired
    private PerfAutoScoreHelper perfAutoScoreHelper;

    @Autowired
    @Lazy
    private PerfAssessmentRaterService perfAssessmentRaterService;

    private static final String STAFF_SERVICE_KEY = "com.skyeye.personnel.service.impl.SysEveUserStaffServiceImpl";

    @Override
    protected void createPrepose(PerfAssessment entity) {
        Map<String, Object> business = BeanUtil.beanToMap(entity);
        String oddNumber = iCodeRuleService.getNextCodeByClassName(getServiceClassName(), business);
        entity.setOddNumber(oddNumber);
        calcScoreAndGrade(entity);
    }

    @Override
    protected void updatePrepose(PerfAssessment entity) {
        PerfAssessment old = selectById(entity.getId());
        if (old != null && !FlowableStateEnum.DRAFT.getKey().equals(old.getState())
            && !FlowableStateEnum.REJECT.getKey().equals(old.getState())
            && !FlowableStateEnum.REVOKE.getKey().equals(old.getState())) {
            throw new CustomException("当前状态不允许修改打分");
        }
        calcScoreAndGrade(entity);
    }

    @Override
    protected void submitToApprovalPostpose(String id, String processInstanceId) {
        PerfAssessment assessment = selectById(id);
        validateScores(assessment);
        calcScoreAndGrade(assessment);
        updateById(assessment);
        super.submitToApprovalPostpose(id, processInstanceId);
    }

    @Override
    protected QueryWrapper<PerfAssessment> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<PerfAssessment> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getObjectId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(PerfAssessment::getObjectId), commonPageInfo.getObjectId());
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getState())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(PerfAssessment::getState), commonPageInfo.getState());
        }
        String cycleId = commonPageInfo.getCustomParamsMapStr("cycleId");
        if (StrUtil.isNotEmpty(cycleId)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(PerfAssessment::getCycleId), cycleId);
        }
        String schemeId = commonPageInfo.getCustomParamsMapStr("schemeId");
        if (StrUtil.isNotEmpty(schemeId)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(PerfAssessment::getSchemeId), schemeId);
        }
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        if (CollectionUtil.isEmpty(beans)) {
            return beans;
        }
        List<String> staffIds = beans.stream()
            .filter(bean -> bean.get("objectId") != null)
            .map(bean -> bean.get("objectId").toString())
            .distinct()
            .collect(Collectors.toList());
        Map<String, Map<String, Object>> staffMap = iAuthUserService.queryUserMationListByStaffIds(staffIds);
        List<String> cycleIds = beans.stream()
            .filter(bean -> bean.get("cycleId") != null)
            .map(bean -> bean.get("cycleId").toString())
            .distinct()
            .collect(Collectors.toList());
        Map<String, PerfCycle> cycleMap = CollectionUtil.isEmpty(cycleIds)
            ? new HashMap<>()
            : perfCycleService.selectByIds(cycleIds.toArray(new String[0])).stream()
            .collect(Collectors.toMap(PerfCycle::getId, item -> item, (a, b) -> a));
        beans.forEach(bean -> {
            String objectId = String.valueOf(bean.getOrDefault("objectId", StrUtil.EMPTY));
            bean.put("objectMation", staffMap.get(objectId));
            String cycleId = String.valueOf(bean.getOrDefault("cycleId", StrUtil.EMPTY));
            bean.put("cycleMation", cycleMap.get(cycleId));
        });
        return beans;
    }

    @Override
    public void writePostpose(PerfAssessment entity, String userId) {
        perfAssessmentDetailService.saveAssessmentDetails(userId, entity);
        super.writePostpose(entity, userId);
    }

    @Override
    public PerfAssessment getDataFromDb(String id) {
        PerfAssessment assessment = super.getDataFromDb(id);
        assessment.setAssessmentDetailList(perfAssessmentDetailService.queryByAssessmentId(id));
        return assessment;
    }

    @Override
    public PerfAssessment selectById(String id) {
        PerfAssessment assessment = super.selectById(id);
        perfSchemeService.setDataMation(assessment, PerfAssessment::getSchemeId);
        perfCycleService.setDataMation(assessment, PerfAssessment::getCycleId);
        Map<String, Map<String, Object>> staffMap =
            iAuthUserService.queryUserMationListByStaffIds(java.util.Arrays.asList(assessment.getObjectId()));
        assessment.setObjectMation(staffMap.get(assessment.getObjectId()));
        if (CollectionUtil.isNotEmpty(assessment.getAssessmentDetailList())) {
            assessment.getAssessmentDetailList().forEach(detail ->
                detail.setSourceTypeMation(PerfItemSourceType.getMation(detail.getSourceType())));
        }
        return assessment;
    }

    @Override
    public void deletePostpose(String id) {
        perfAssessmentDetailService.deleteByAssessmentId(id);
        perfAssessmentRaterService.deleteByAssessmentId(id);
    }

    @Override
    protected void approvalEndIsSuccess(PerfAssessment entity) {
        super.approvalEndIsSuccess(entity);
        dispatchResultNotice(entity);
    }

    private void dispatchResultNotice(PerfAssessment assessment) {
        if (iNoticeDispatchService == null || assessment == null || StrUtil.isEmpty(assessment.getObjectId())) {
            return;
        }
        try {
            Map<String, Map<String, Object>> staffMap =
                iAuthUserService.queryUserMationListByStaffIds(java.util.Arrays.asList(assessment.getObjectId()));
            Map<String, Object> staff = staffMap.getOrDefault(assessment.getObjectId(), new HashMap<>());
            Object userIdObj = staff.get("userId");
            if (userIdObj == null) {
                userIdObj = staff.get("id");
            }
            String userId = userIdObj == null ? StrUtil.EMPTY : userIdObj.toString();
            if (StrUtil.isEmpty(userId)) {
                return;
            }
            Map<String, Object> params = new HashMap<>();
            params.put("id", assessment.getId());
            params.put("title", StrUtil.blankToDefault(assessment.getName(), "绩效考核"));
            params.put("orderNum", StrUtil.blankToDefault(assessment.getOddNumber(), assessment.getId()));
            params.put("totalScore", assessment.getTotalScore() == null ? "0" : assessment.getTotalScore().toPlainString());
            params.put("grade", StrUtil.blankToDefault(assessment.getGrade(), "-"));
            params.put("userName", staff.getOrDefault("name", StrUtil.EMPTY));
            NoticeDispatchBody body = new NoticeDispatchBody();
            body.setTypeCode(NoticeUserMessageTypeEnum.PERFORMANCE_RESULT.getKey());
            body.setReceiveUserIds(java.util.Arrays.asList(userId));
            body.setParams(params);
            body.setCreateUserId(CommonConstants.ADMIN_USER_ID);
            body.setObjectData(JSONUtil.toJsonStr(assessment));
            body.setPcPath("/performance/assessment/list");
            iNoticeDispatchService.dispatch(body);
        } catch (Exception e) {
            // 通知失败不影响审批主流程
        }
    }

    @Override
    public int generateByCycle(PerfCycle cycle, String userId) {
        if (PerfCycleState.CLOSED.getKey().equals(cycle.getCycleState())) {
            throw new CustomException("周期已关闭，无法生成考核单");
        }
        PerfScheme scheme = perfSchemeService.selectById(cycle.getSchemeId());
        if (scheme == null) {
            throw new CustomException("考核方案不存在");
        }
        if (!EnableEnum.ENABLE_USING.getKey().equals(scheme.getEnabled())) {
            throw new CustomException("考核方案未启用，无法生成考核单");
        }
        if (CollectionUtil.isEmpty(scheme.getSchemeItemList())) {
            throw new CustomException("方案未配置指标，无法生成考核单");
        }
        List<String> staffIds = perfSchemeService.resolveStaffIds(scheme.getId());
        if (CollectionUtil.isEmpty(staffIds)) {
            return 0;
        }
        Map<String, Map<String, Object>> staffMap = iAuthUserService.queryUserMationListByStaffIds(staffIds);
        // 一次查出本周期已生成的考核对象，避免循环内逐人 count
        QueryWrapper<PerfAssessment> existWrapper = new QueryWrapper<>();
        existWrapper.eq(MybatisPlusUtil.toColumns(PerfAssessment::getCycleId), cycle.getId());
        existWrapper.select(MybatisPlusUtil.toColumns(PerfAssessment::getObjectId));
        Set<String> existedObjectIds = list(existWrapper).stream()
            .map(PerfAssessment::getObjectId)
            .filter(StrUtil::isNotEmpty)
            .collect(Collectors.toSet());

        int created = 0;
        for (String staffId : staffIds) {
            if (StrUtil.isEmpty(staffId) || existedObjectIds.contains(staffId)) {
                continue;
            }
            Map<String, Object> staffInfo = staffMap.getOrDefault(staffId, new HashMap<>());
            String staffName = staffInfo.getOrDefault("name", staffId).toString();
            PerfAssessment assessment = new PerfAssessment();
            assessment.setName(cycle.getName() + "-" + staffName);
            assessment.setCycleId(cycle.getId());
            assessment.setSchemeId(scheme.getId());
            assessment.setObjectId(staffId);
            assessment.setObjectKey(STAFF_SERVICE_KEY);
            assessment.setTotalScore(BigDecimal.ZERO);
            List<PerfAssessmentDetail> details = buildDetailsFromSchemeItems(scheme.getSchemeItemList());
            String bindUserId = resolveStaffUserId(staffInfo);
            perfAutoScoreHelper.fillAutoScores(details, staffId, bindUserId, cycle.getStartTime(), cycle.getEndTime(),
                scheme.getFullScore());
            assessment.setAssessmentDetailList(details);
            createEntity(assessment, userId);
            perfAssessmentRaterService.initRatersForAssessment(assessment, scheme, staffInfo);
            existedObjectIds.add(staffId);
            created++;
        }
        return created;
    }

    private List<PerfAssessmentDetail> buildDetailsFromSchemeItems(List<PerfSchemeItem> schemeItems) {
        List<PerfAssessmentDetail> details = new ArrayList<>();
        int order = 1;
        for (PerfSchemeItem item : schemeItems) {
            PerfAssessmentDetail detail = new PerfAssessmentDetail();
            detail.setSchemeItemId(item.getId());
            detail.setName(item.getName());
            detail.setSourceType(StrUtil.blankToDefault(item.getSourceType(), "manual"));
            detail.setSourceConfig(item.getSourceConfig());
            detail.setWeight(item.getWeight());
            detail.setScore(null);
            detail.setWeightedScore(BigDecimal.ZERO);
            detail.setRemark(item.getRemark());
            detail.setOrderBy(item.getOrderBy() == null ? order++ : item.getOrderBy());
            details.add(detail);
        }
        return details;
    }

    private String resolveStaffUserId(Map<String, Object> staffInfo) {
        if (staffInfo == null || staffInfo.isEmpty()) {
            return StrUtil.EMPTY;
        }
        Object userIdObj = staffInfo.get("userId");
        if (userIdObj == null) {
            userIdObj = staffInfo.get("id");
        }
        return userIdObj == null ? StrUtil.EMPTY : userIdObj.toString();
    }

    @Override
    public void fillAutoScores(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        PerfAssessment assessment = selectById(id);
        if (assessment == null) {
            throw new CustomException("考核单不存在");
        }
        if (!FlowableStateEnum.DRAFT.getKey().equals(assessment.getState())
            && !FlowableStateEnum.REJECT.getKey().equals(assessment.getState())
            && !FlowableStateEnum.REVOKE.getKey().equals(assessment.getState())) {
            throw new CustomException("当前状态不允许自动取分");
        }
        PerfCycle cycle = perfCycleService.selectById(assessment.getCycleId());
        if (cycle == null) {
            throw new CustomException("考核周期不存在");
        }
        PerfScheme scheme = perfSchemeService.selectById(assessment.getSchemeId());
        BigDecimal fullScore = scheme == null ? new BigDecimal("100") : scheme.getFullScore();
        int filled = perfAutoScoreHelper.fillAutoScores(assessment.getAssessmentDetailList(),
            assessment.getObjectId(), cycle.getStartTime(), cycle.getEndTime(), fullScore);
        if (filled <= 0) {
            throw new CustomException("没有可自动取分的指标（请将来源设为考勤/问卷/计件）");
        }
        calcScoreAndGrade(assessment);
        String userId = inputObject.getLogParams().get("id").toString();
        updateEntity(assessment, userId);
        outputObject.setBean(selectById(id));
        outputObject.setreturnMessage("success");
    }

    private void validateScores(PerfAssessment entity) {
        if (CollectionUtil.isEmpty(entity.getAssessmentDetailList())) {
            throw new CustomException("考核明细不能为空");
        }
        BigDecimal fullScore = new BigDecimal("100");
        if (StrUtil.isNotEmpty(entity.getSchemeId())) {
            PerfScheme scheme = perfSchemeService.selectById(entity.getSchemeId());
            if (scheme != null && scheme.getFullScore() != null
                && scheme.getFullScore().compareTo(BigDecimal.ZERO) > 0) {
                fullScore = scheme.getFullScore();
            }
        }
        for (PerfAssessmentDetail detail : entity.getAssessmentDetailList()) {
            if (detail.getScore() == null) {
                throw new CustomException("请为指标「" + detail.getName() + "」打分");
            }
            if (detail.getScore().compareTo(BigDecimal.ZERO) < 0) {
                throw new CustomException("指标「" + detail.getName() + "」得分不能为负数");
            }
            if (detail.getScore().compareTo(fullScore) > 0) {
                throw new CustomException("指标「" + detail.getName() + "」得分不能超过满分" + fullScore);
            }
        }
    }

    private void calcScoreAndGrade(PerfAssessment entity) {
        if (CollectionUtil.isEmpty(entity.getAssessmentDetailList())) {
            entity.setTotalScore(BigDecimal.ZERO);
            return;
        }
        BigDecimal total = BigDecimal.ZERO;
        for (PerfAssessmentDetail detail : entity.getAssessmentDetailList()) {
            if (detail.getScore() == null || detail.getWeight() == null) {
                continue;
            }
            BigDecimal weighted = NumberParseUtil.weightedByPercent(detail.getWeight(), detail.getScore(), 2);
            detail.setWeightedScore(weighted);
            total = total.add(weighted);
        }
        entity.setTotalScore(total);
        PerfScheme scheme = null;
        if (StrUtil.isNotEmpty(entity.getSchemeId())) {
            scheme = perfSchemeService.selectById(entity.getSchemeId());
        }
        String grade = ScoreBandUtil.matchGrade(scheme == null ? null : scheme.getGradeRule(), total);
        if (StrUtil.isNotEmpty(grade)) {
            entity.setGrade(grade);
        }
    }

    @Override
    public long countByCycleId(String cycleId) {
        if (StrUtil.isEmpty(cycleId)) {
            return 0;
        }
        QueryWrapper<PerfAssessment> wrapper = new QueryWrapper<>();
        wrapper.eq(MybatisPlusUtil.toColumns(PerfAssessment::getCycleId), cycleId);
        return count(wrapper);
    }

    @Override
    public long countBySchemeId(String schemeId) {
        if (StrUtil.isEmpty(schemeId)) {
            return 0;
        }
        QueryWrapper<PerfAssessment> wrapper = new QueryWrapper<>();
        wrapper.eq(MybatisPlusUtil.toColumns(PerfAssessment::getSchemeId), schemeId);
        return count(wrapper);
    }

    @Override
    public void queryPerformanceDashboard(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String cycleId = params.getOrDefault("cycleId", StrUtil.EMPTY).toString();

        QueryWrapper<PerfAssessment> allWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(cycleId)) {
            allWrapper.eq(MybatisPlusUtil.toColumns(PerfAssessment::getCycleId), cycleId);
        }
        List<PerfAssessment> allList = list(allWrapper);
        Map<String, Integer> stateDist = new HashMap<>();
        for (PerfAssessment item : allList) {
            String state = StrUtil.blankToDefault(item.getState(), "unknown");
            stateDist.put(state, stateDist.getOrDefault(state, 0) + 1);
        }

        List<PerfAssessment> list = allList.stream()
            .filter(item -> FlowableStateEnum.PASS.getKey().equals(item.getState()))
            .sorted((a, b) -> {
                BigDecimal sa = a.getTotalScore() == null ? BigDecimal.ZERO : a.getTotalScore();
                BigDecimal sb = b.getTotalScore() == null ? BigDecimal.ZERO : b.getTotalScore();
                return sb.compareTo(sa);
            })
            .collect(Collectors.toList());
        List<String> staffIds = list.stream().map(PerfAssessment::getObjectId).distinct().collect(Collectors.toList());
        Map<String, Map<String, Object>> staffMap = CollectionUtil.isEmpty(staffIds)
            ? new HashMap<>() : iAuthUserService.queryUserMationListByStaffIds(staffIds);

        Map<String, PerfScheme> schemeCache = new HashMap<>();
        String preferredGradeRule = null;
        if (StrUtil.isNotEmpty(cycleId)) {
            PerfCycle cycle = perfCycleService.selectById(cycleId);
            if (cycle != null && StrUtil.isNotEmpty(cycle.getSchemeId())) {
                PerfScheme scheme = getSchemeCached(cycle.getSchemeId(), schemeCache);
                preferredGradeRule = scheme == null ? null : scheme.getGradeRule();
            }
        }
        Map<String, Integer> scoreBand = ScoreBandUtil.initBandMap(preferredGradeRule);

        List<Map<String, Object>> ranking = new ArrayList<>();
        Map<String, Integer> gradeDist = new HashMap<>();
        BigDecimal totalScoreSum = BigDecimal.ZERO;
        BigDecimal maxScore = BigDecimal.ZERO;
        BigDecimal minScore = null;
        for (PerfAssessment item : list) {
            Map<String, Object> row = new HashMap<>();
            row.put("id", item.getId());
            row.put("objectId", item.getObjectId());
            row.put("staffName", staffMap.getOrDefault(item.getObjectId(), new HashMap<>()).get("name"));
            row.put("departmentName", staffMap.getOrDefault(item.getObjectId(), new HashMap<>()).get("departmentName"));
            row.put("totalScore", item.getTotalScore());
            row.put("grade", item.getGrade());
            ranking.add(row);
            String grade = StrUtil.blankToDefault(item.getGrade(), "未定级");
            gradeDist.put(grade, gradeDist.getOrDefault(grade, 0) + 1);
            if (item.getTotalScore() != null) {
                BigDecimal score = item.getTotalScore();
                totalScoreSum = totalScoreSum.add(score);
                if (score.compareTo(maxScore) > 0) {
                    maxScore = score;
                }
                if (minScore == null || score.compareTo(minScore) < 0) {
                    minScore = score;
                }
                PerfScheme scheme = getSchemeCached(item.getSchemeId(), schemeCache);
                String gradeRule = preferredGradeRule != null
                    ? preferredGradeRule
                    : (scheme == null ? null : scheme.getGradeRule());
                if (scoreBand.isEmpty() && StrUtil.isNotEmpty(gradeRule)) {
                    scoreBand.putAll(ScoreBandUtil.initBandMap(gradeRule));
                }
                ScoreBandUtil.incrBand(scoreBand, gradeRule, score);
            }
        }
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalCount", list.size());
        summary.put("allCount", allList.size());
        summary.put("avgScore", list.isEmpty() ? BigDecimal.ZERO
            : totalScoreSum.divide(new BigDecimal(list.size()), 2, RoundingMode.HALF_UP));
        summary.put("maxScore", maxScore);
        summary.put("minScore", minScore == null ? BigDecimal.ZERO : minScore);
        summary.put("gradeDist", gradeDist);
        summary.put("scoreBand", scoreBand);
        summary.put("stateDist", stateDist);

        Map<String, Object> bean = new HashMap<>();
        bean.put("summary", summary);
        bean.put("ranking", ranking);
        outputObject.setBean(bean);
        outputObject.settotal(ranking.size());
    }

    private PerfScheme getSchemeCached(String schemeId, Map<String, PerfScheme> schemeCache) {
        if (StrUtil.isEmpty(schemeId)) {
            return null;
        }
        if (schemeCache.containsKey(schemeId)) {
            return schemeCache.get(schemeId);
        }
        PerfScheme scheme = perfSchemeService.selectById(schemeId);
        schemeCache.put(schemeId, scheme);
        return scheme;
    }

}
