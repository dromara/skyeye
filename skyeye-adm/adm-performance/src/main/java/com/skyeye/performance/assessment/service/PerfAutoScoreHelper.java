package com.skyeye.performance.assessment.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.checkwork.service.CheckWorkService;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.JsonConfigUtil;
import com.skyeye.common.util.NumberParseUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.servey.entity.DwSurveyAnswer;
import com.skyeye.eve.servey.entity.DwSurveyDirectory;
import com.skyeye.eve.servey.service.DwSurveyAnswerService;
import com.skyeye.eve.servey.service.DwSurveyDirectoryService;
import com.skyeye.eve.service.IAuthUserService;
import com.skyeye.performance.assessment.entity.PerfAssessmentDetail;
import com.skyeye.performance.classenum.PerfItemSourceType;
import com.skyeye.rest.erp.piecework.service.IPieceworkSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自动取分：考勤、问卷、计件。
 */
@Component
public class PerfAutoScoreHelper {

    @Autowired
    private CheckWorkService checkWorkService;

    @Autowired
    private DwSurveyAnswerService dwSurveyAnswerService;

    @Autowired
    private DwSurveyDirectoryService dwSurveyDirectoryService;

    @Autowired
    private IAuthUserService iAuthUserService;

    @Autowired(required = false)
    private IPieceworkSystemService iPieceworkSystemService;

    public int fillAutoScores(List<PerfAssessmentDetail> details, String staffId,
                              String startTime, String endTime, BigDecimal fullScore) {
        return fillAutoScores(details, staffId, null, startTime, endTime, fullScore);
    }

    public int fillAutoScores(List<PerfAssessmentDetail> details, String staffId, String bindUserId,
                              String startTime, String endTime, BigDecimal fullScore) {
        if (details == null || details.isEmpty() || StrUtil.isEmpty(staffId)) {
            return 0;
        }
        BigDecimal maxScore = normalizeFullScore(fullScore);
        Map<String, Object> checkSummary = null;
        String userId = StrUtil.isEmpty(bindUserId) ? null : bindUserId;
        int filled = 0;
        for (PerfAssessmentDetail detail : details) {
            String sourceType = detail.getSourceType();
            if (PerfItemSourceType.CHECKWORK.getKey().equals(sourceType)) {
                if (StrUtil.hasBlank(startTime, endTime)) {
                    continue;
                }
                if (checkSummary == null) {
                    checkSummary = checkWorkService.queryStaffCheckWorkSummary(staffId, startTime, endTime);
                }
                detail.setScore(calcCheckWorkScore(checkSummary, maxScore));
                detail.setRemark(buildCheckWorkRemark(checkSummary));
                filled++;
            } else if (PerfItemSourceType.SURVEY.getKey().equals(sourceType)) {
                if (userId == null) {
                    userId = resolveUserId(staffId);
                }
                if (fillSurveyScore(detail, userId, startTime, endTime, maxScore)) {
                    filled++;
                }
            } else if (PerfItemSourceType.PIECEWORK.getKey().equals(sourceType)) {
                if (fillPieceworkScore(detail, staffId, startTime, endTime, maxScore)) {
                    filled++;
                }
            }
        }
        return filled;
    }

    private boolean fillPieceworkScore(PerfAssessmentDetail detail, String staffId,
                                       String startTime, String endTime, BigDecimal maxScore) {
        if (iPieceworkSystemService == null) {
            detail.setRemark("计件自动取分失败：ERP 服务未接入");
            return false;
        }
        JSONObject cfg = JsonConfigUtil.parseConfig(detail.getSourceConfig());
        String farmId = cfg.getStr("farmId", StrUtil.EMPTY);
        BigDecimal targetNum = cfg.getBigDecimal("targetNum");
        Integer isNumTime = cfg.getInt("isNumTime", 1);
        if (targetNum == null || targetNum.compareTo(BigDecimal.ZERO) <= 0) {
            detail.setRemark("计件自动取分失败：请配置目标产量/工时 targetNum");
            return false;
        }
        String startMonth = DateUtil.toYearMonth(startTime);
        String endMonth = DateUtil.toYearMonth(endTime);
        if (StrUtil.hasBlank(startMonth, endMonth)) {
            detail.setRemark("计件自动取分失败：周期日期无效");
            return false;
        }
        List<Map<String, Object>> rows = iPieceworkSystemService.queryByStaffAndMonths(
            staffId, farmId, startMonth, endMonth);
        BigDecimal actual = BigDecimal.ZERO;
        if (CollectionUtil.isNotEmpty(rows)) {
            for (Map<String, Object> row : rows) {
                int rowType = NumberParseUtil.parseInt(row.get("isNumTime"));
                int useType = isNumTime == null ? (rowType > 0 ? rowType : 1) : isNumTime;
                if (useType == 2) {
                    actual = actual.add(NumberParseUtil.parseBigDecimal(row.get("allTime")));
                } else {
                    actual = actual.add(NumberParseUtil.parseBigDecimal(row.get("allNum")));
                }
            }
        }
        detail.setScore(NumberParseUtil.ratioToScore(actual, targetNum, maxScore, 2));
        detail.setRemark(String.format("计件自动：实际%s / 目标%s（%s~%s）",
            NumberParseUtil.toPlainString(actual), NumberParseUtil.toPlainString(targetNum),
            startMonth, endMonth));
        return true;
    }

    private boolean fillSurveyScore(PerfAssessmentDetail detail, String userId,
                                    String startTime, String endTime, BigDecimal maxScore) {
        String surveyId = JsonConfigUtil.getConfigStr(detail.getSourceConfig(), "surveyId");
        if (StrUtil.isEmpty(surveyId)) {
            detail.setRemark("问卷自动取分失败：未配置问卷ID");
            return false;
        }
        if (StrUtil.isEmpty(userId)) {
            detail.setScore(BigDecimal.ZERO);
            detail.setRemark("问卷自动取分：员工未绑定账号");
            return true;
        }
        QueryWrapper<DwSurveyAnswer> wrapper = new QueryWrapper<>();
        wrapper.eq(MybatisPlusUtil.toColumns(DwSurveyAnswer::getSurveyId), surveyId);
        wrapper.eq(MybatisPlusUtil.toColumns(DwSurveyAnswer::getCreateId), userId);
        List<DwSurveyAnswer> answers = dwSurveyAnswerService.list(wrapper);
        if (CollectionUtil.isEmpty(answers)) {
            detail.setScore(BigDecimal.ZERO);
            detail.setRemark("问卷自动取分：未找到答卷，记0分");
            return true;
        }
        DwSurveyAnswer matched = answers.stream()
            .filter(item -> DateUtil.inDayRange(item.getEndAnDate(), startTime, endTime)
                || DateUtil.inDayRange(item.getBgAnDate(), startTime, endTime))
            .max(Comparator.comparing(item -> StrUtil.blankToDefault(item.getEndAnDate(),
                StrUtil.blankToDefault(item.getBgAnDate(), ""))))
            .orElse(null);
        if (matched == null) {
            matched = answers.stream()
                .max(Comparator.comparing(item -> StrUtil.blankToDefault(item.getEndAnDate(),
                    StrUtil.blankToDefault(item.getBgAnDate(), ""))))
                .orElse(answers.get(0));
        }
        Integer mark = matched.getMarkFraction();
        if (mark == null) {
            detail.setScore(BigDecimal.ZERO);
            detail.setRemark("问卷自动取分：答卷未评分，记0分");
            return true;
        }
        int surveyFull = 100;
        try {
            DwSurveyDirectory directory = dwSurveyDirectoryService.selectById(surveyId);
            if (directory != null && directory.getFraction() != null && directory.getFraction() > 0) {
                surveyFull = directory.getFraction();
            }
        } catch (Exception ignored) {
            // ignore
        }
        detail.setScore(NumberParseUtil.ratioToScore(
            new BigDecimal(mark), new BigDecimal(surveyFull), maxScore, 2));
        detail.setRemark(String.format("问卷自动：得分%d/%d", mark, surveyFull));
        return true;
    }

    private String resolveUserId(String staffId) {
        Map<String, Map<String, Object>> staffMap =
            iAuthUserService.queryUserMationListByStaffIds(java.util.Arrays.asList(staffId));
        Map<String, Object> staff = staffMap.getOrDefault(staffId, new HashMap<>());
        Object userIdObj = staff.get("userId");
        if (userIdObj == null) {
            userIdObj = staff.get("id");
        }
        return userIdObj == null ? StrUtil.EMPTY : userIdObj.toString();
    }

    public BigDecimal calcCheckWorkScore(Map<String, Object> summary, BigDecimal fullScore) {
        if (summary == null || summary.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal maxScore = normalizeFullScore(fullScore);
        int shouldTime = NumberParseUtil.parseInt(summary.get("shouldTime"));
        int fullTime = NumberParseUtil.parseInt(summary.get("fullTime"));
        int late = NumberParseUtil.parseInt(summary.get("late"));
        int leaveEarly = NumberParseUtil.parseInt(summary.get("leaveEarly"));
        int absenteeism = NumberParseUtil.parseInt(summary.get("absenteeism"));
        int missing = NumberParseUtil.parseInt(summary.get("missing"));
        int lackTime = NumberParseUtil.parseInt(summary.get("lackTime"));

        BigDecimal base = shouldTime <= 0
            ? new BigDecimal("100")
            : new BigDecimal(fullTime)
            .multiply(new BigDecimal("100"))
            .divide(new BigDecimal(shouldTime), 2, RoundingMode.HALF_UP);
        BigDecimal penalty = new BigDecimal(late * 2L + leaveEarly * 2L + absenteeism * 5L + missing * 3L + lackTime * 3L);
        BigDecimal score100 = NumberParseUtil.clamp(base.subtract(penalty), BigDecimal.ZERO, new BigDecimal("100"));
        return score100.multiply(maxScore)
            .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }

    private String buildCheckWorkRemark(Map<String, Object> summary) {
        if (summary == null || summary.isEmpty()) {
            return "考勤自动取分：无考勤数据，记0分";
        }
        return String.format("考勤自动：应出%d 正常%d 旷工%d 迟到%d 早退%d 缺卡%d",
            NumberParseUtil.parseInt(summary.get("shouldTime")),
            NumberParseUtil.parseInt(summary.get("fullTime")),
            NumberParseUtil.parseInt(summary.get("absenteeism")),
            NumberParseUtil.parseInt(summary.get("late")),
            NumberParseUtil.parseInt(summary.get("leaveEarly")),
            NumberParseUtil.parseInt(summary.get("missing")));
    }

    private BigDecimal normalizeFullScore(BigDecimal fullScore) {
        return fullScore == null || fullScore.compareTo(BigDecimal.ZERO) <= 0
            ? new BigDecimal("100") : fullScore;
    }
}
