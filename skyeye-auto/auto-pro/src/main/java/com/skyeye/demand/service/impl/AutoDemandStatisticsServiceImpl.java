/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.demand.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.bug.classenum.BugState;
import com.skyeye.bug.dao.AutoBugDao;
import com.skyeye.bug.entity.AutoBug;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.NumberParseUtil;
import com.skyeye.common.util.StatQueryUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.demand.classenum.AutoDemandRoleStateEnum;
import com.skyeye.demand.classenum.AutoDemandStateEnum;
import com.skyeye.demand.dao.AutoDemandDao;
import com.skyeye.demand.entity.AutoDemand;
import com.skyeye.demand.service.AutoDemandStatisticsService;
import com.skyeye.eve.service.IAuthUserService;
import com.skyeye.module.entity.AutoModule;
import com.skyeye.module.service.AutoModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ClassName: AutoDemandStatisticsServiceImpl
 * @Description: 需求管理统计服务实现层
 * @author: skyeye云系列--卫志强
 * @Copyright: https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class AutoDemandStatisticsServiceImpl implements AutoDemandStatisticsService {

    private static final int DEFAULT_STAT_DAYS = 30;

    private static final AutoDemandStateEnum[] STATE_ORDER = {
        AutoDemandStateEnum.WAIT_RESEARCH,
        AutoDemandStateEnum.RESEARCH,
        AutoDemandStateEnum.WAIT_TEST,
        AutoDemandStateEnum.FINISH,
        AutoDemandStateEnum.INVALID
    };

    private static final BugState[] BUG_STATE_ORDER = {
        BugState.UNRESOLVED,
        BugState.TO_BE_RETURNED,
        BugState.RESOLVED
    };

    @Autowired
    private AutoDemandDao autoDemandDao;

    @Autowired
    private AutoBugDao autoBugDao;

    @Autowired
    private AutoModuleService autoModuleService;

    @Autowired
    private IAuthUserService iAuthUserService;

    @Override
    public void queryOverviewAutoDemand(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        QueryWrapper<AutoDemand> demandWrapper = buildDemandWrapper(tableSelectInfo);
        List<AutoDemand> demandList = autoDemandDao.selectList(demandWrapper);

        long totalDemands = demandList.size();
        Map<String, Long> stateCountMap = demandList.stream()
            .filter(demand -> StrUtil.isNotEmpty(demand.getState()))
            .collect(Collectors.groupingBy(AutoDemand::getState, Collectors.counting()));

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("totalDemands", totalDemands);
        for (AutoDemandStateEnum state : STATE_ORDER) {
            resultMap.put(state.getKey(), stateCountMap.getOrDefault(state.getKey(), 0L));
        }

        outputObject.setBean(resultMap);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void queryAutoDemandTrendStats(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo params = inputObject.getParams(TableSelectInfo.class);
        String[] range = StatQueryUtil.resolveStatTimeRange(params, DEFAULT_STAT_DAYS);
        String fromTime = range[0];
        String endTime = range[1];
        String fromDate = fromTime.substring(0, 10);
        String toDate = endTime.substring(0, 10);

        String createTimeColumn = MybatisPlusUtil.toColumns(AutoDemand::getCreateTime);
        QueryWrapper<AutoDemand> queryWrapper = new QueryWrapper<>();
        applyObjectScope(queryWrapper, params.getObjectId(), params.getObjectKey());
        queryWrapper.select(
                "substr(" + createTimeColumn + ", 1, 10) as statDate",
                "count(1) as totalCount")
            .ge(createTimeColumn, fromTime)
            .le(createTimeColumn, endTime)
            .groupBy("substr(" + createTimeColumn + ", 1, 10)")
            .orderByAsc("statDate");

        List<Map<String, Object>> rows = autoDemandDao.selectMaps(queryWrapper);
        Map<String, Map<String, Object>> dayMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(rows)) {
            for (Map<String, Object> row : rows) {
                dayMap.put(String.valueOf(row.get("statDate")), row);
            }
        }

        List<String> dayList = DateUtil.getDays(fromDate, toDate);
        List<Long> seriesData = new ArrayList<>();
        long total = 0L;
        for (String day : dayList) {
            Map<String, Object> row = dayMap.get(day);
            long totalCount = row == null ? 0L : NumberParseUtil.parseLong(row.get("totalCount"));
            total += totalCount;
            seriesData.add(totalCount);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("xAxisData", dayList);
        result.put("seriesData", seriesData);
        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void queryDemandStatsByState(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        QueryWrapper<AutoDemand> demandWrapper = buildDemandWrapper(tableSelectInfo);
        List<AutoDemand> demandList = autoDemandDao.selectList(demandWrapper);
        long total = demandList.size();

        Map<String, Long> stateCountMap = demandList.stream()
            .filter(demand -> StrUtil.isNotEmpty(demand.getState()))
            .collect(Collectors.groupingBy(AutoDemand::getState, Collectors.counting()));

        List<String> xAxisData = new ArrayList<>();
        List<Long> seriesData = new ArrayList<>();
        for (AutoDemandStateEnum state : STATE_ORDER) {
            xAxisData.add(state.getValue());
            seriesData.add(stateCountMap.getOrDefault(state.getKey(), 0L));
        }

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("xAxisData", xAxisData);
        result.put("seriesData", seriesData);
        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void queryVersionDashboard(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo params = inputObject.getParams(TableSelectInfo.class);
        String objectId = params.getObjectId();
        String objectKey = params.getObjectKey();
        Map<String, Object> rawParams = inputObject.getParams();
        String versionId = resolveVersionId(params, rawParams);

        List<AutoDemand> demandList = listDashboardDemands(objectId, objectKey, versionId);
        List<AutoBug> bugList = listDashboardBugs(objectId, objectKey, versionId);

        Map<String, Object> score = buildScoreSummary(demandList);
        Map<String, Object> demand = buildDemandSummary(demandList);
        List<Map<String, Object>> demandByState = buildDemandByState(demandList);
        List<Map<String, Object>> demandByModule = buildDemandByModule(demandList);
        List<Map<String, Object>> roleLoad = buildRoleLoad(demandList);
        List<Map<String, Object>> memberRank = buildMemberRank(demandList);
        Map<String, Object> bug = buildBugSummary(bugList);
        List<Map<String, Object>> bugByState = buildBugByState(bugList);

        Map<String, Object> result = new HashMap<>();
        result.put("score", score);
        result.put("demand", demand);
        result.put("demandByState", demandByState);
        result.put("demandByModule", demandByModule);
        result.put("roleLoad", roleLoad);
        result.put("memberRank", memberRank);
        result.put("bug", bug);
        result.put("bugByState", bugByState);
        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    private String resolveVersionId(TableSelectInfo params, Map<String, Object> rawParams) {
        String versionId = params.getCustomParamsMapStr("versionId");
        if (StrUtil.isNotBlank(versionId)) {
            return versionId;
        }
        if (rawParams != null) {
            Object custom = rawParams.get("customParamsMap");
            if (custom instanceof Map) {
                Object value = ((Map<?, ?>) custom).get("versionId");
                if (value != null && StrUtil.isNotBlank(value.toString())) {
                    return value.toString();
                }
            } else if (custom instanceof String && StrUtil.isNotBlank(custom.toString())) {
                try {
                    JSONObject json = JSONUtil.parseObj(custom.toString());
                    String fromJson = json.getStr("versionId");
                    if (StrUtil.isNotBlank(fromJson)) {
                        return fromJson;
                    }
                } catch (Exception ignored) {
                    // ignore parse error
                }
            }
            String topLevel = NumberParseUtil.toStr(rawParams.get("versionId"));
            if (StrUtil.isNotBlank(topLevel) && !"null".equals(topLevel)) {
                return topLevel;
            }
        }
        return null;
    }

    private List<AutoDemand> listDashboardDemands(String objectId, String objectKey, String versionId) {
        QueryWrapper<AutoDemand> queryWrapper = new QueryWrapper<>();
        applyObjectScope(queryWrapper, objectId, objectKey);
        if (StrUtil.isNotEmpty(versionId) && !"null".equals(versionId)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(AutoDemand::getVersionId), versionId);
        }
        return autoDemandDao.selectList(queryWrapper);
    }

    private List<AutoBug> listDashboardBugs(String objectId, String objectKey, String versionId) {
        QueryWrapper<AutoBug> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(objectId)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(AutoBug::getObjectId), objectId);
        }
        if (StrUtil.isNotEmpty(objectKey) && !"null".equals(objectKey)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(AutoBug::getObjectKey), objectKey);
        }
        if (StrUtil.isNotEmpty(versionId) && !"null".equals(versionId)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(AutoBug::getVersionId), versionId);
        }
        return autoBugDao.selectList(queryWrapper);
    }

    private Map<String, Object> buildScoreSummary(List<AutoDemand> demandList) {
        String totalScore = "0";
        String frontInit = "0";
        String backInit = "0";
        String testInit = "0";
        String frontEarned = "0";
        String backEarned = "0";
        String testEarned = "0";
        for (AutoDemand demand : demandList) {
            if (AutoDemandStateEnum.INVALID.getKey().equals(demand.getState())) {
                continue;
            }
            totalScore = CalculationUtil.add(totalScore, nvlScore(demand.getTotalScore()), 2);
            frontInit = CalculationUtil.add(frontInit, nvlScore(demand.getFrontInitScore()), 2);
            backInit = CalculationUtil.add(backInit, nvlScore(demand.getBackInitScore()), 2);
            testInit = CalculationUtil.add(testInit, nvlScore(demand.getTestInitScore()), 2);
            frontEarned = CalculationUtil.add(frontEarned, nvlScore(demand.getFrontEarnedScore()), 2);
            backEarned = CalculationUtil.add(backEarned, nvlScore(demand.getBackEarnedScore()), 2);
            testEarned = CalculationUtil.add(testEarned, nvlScore(demand.getTestEarnedScore()), 2);
        }
        String allocatedScore = CalculationUtil.add(2, frontInit, backInit, testInit);
        String unallocatedScore = CalculationUtil.subtract(totalScore, allocatedScore, 2);
        String earnedScore = CalculationUtil.add(2, frontEarned, backEarned, testEarned);
        String allocateRate = percent(allocatedScore, totalScore);
        String earnRate = percent(earnedScore, allocatedScore);

        Map<String, Object> score = new LinkedHashMap<>();
        score.put("totalScore", totalScore);
        score.put("allocatedScore", allocatedScore);
        score.put("unallocatedScore", unallocatedScore);
        score.put("earnedScore", earnedScore);
        score.put("allocateRate", allocateRate);
        score.put("earnRate", earnRate);
        score.put("frontInitScore", frontInit);
        score.put("backInitScore", backInit);
        score.put("testInitScore", testInit);
        score.put("frontEarnedScore", frontEarned);
        score.put("backEarnedScore", backEarned);
        score.put("testEarnedScore", testEarned);
        return score;
    }

    private Map<String, Object> buildDemandSummary(List<AutoDemand> demandList) {
        long total = 0;
        long invalid = 0;
        long finish = 0;
        long delay = 0;
        String today = DateUtil.getTimeAndToString().substring(0, 10);
        for (AutoDemand demand : demandList) {
            if (AutoDemandStateEnum.INVALID.getKey().equals(demand.getState())) {
                invalid++;
                continue;
            }
            total++;
            if (AutoDemandStateEnum.FINISH.getKey().equals(demand.getState())) {
                finish++;
            }
            if (isDemandDelayed(demand, today)) {
                delay++;
            }
        }
        long doing = total - finish;
        Map<String, Object> demand = new LinkedHashMap<>();
        demand.put("total", total);
        demand.put("finish", finish);
        demand.put("doing", Math.max(doing, 0));
        demand.put("invalid", invalid);
        demand.put("delay", delay);
        demand.put("finishRate", total == 0 ? "0.00" : CalculationUtil.divide(
            CalculationUtil.multiply(String.valueOf(finish), "100", 4), String.valueOf(total), 2, RoundingMode.HALF_UP));
        return demand;
    }

    private boolean isDemandDelayed(AutoDemand demand, String today) {
        return isRoleDelayed(demand.getFrontHandleId(), demand.getFrontState(), demand.getFrontEstimateEndTime(), today)
            || isRoleDelayed(demand.getBackHandleId(), demand.getBackState(), demand.getBackEstimateEndTime(), today)
            || isRoleDelayed(demand.getTestHandleId(), demand.getTestState(), demand.getTestEstimateEndTime(), today);
    }

    private boolean isRoleDelayed(String handleId, String roleState, String estimateEnd, String today) {
        if (StrUtil.isBlank(handleId) || StrUtil.isBlank(estimateEnd)) {
            return false;
        }
        if (AutoDemandRoleStateEnum.FINISH.getKey().equals(roleState)) {
            return false;
        }
        String endDay = estimateEnd.length() >= 10 ? estimateEnd.substring(0, 10) : estimateEnd;
        return endDay.compareTo(today) < 0;
    }

    private List<Map<String, Object>> buildDemandByState(List<AutoDemand> demandList) {
        Map<String, Long> stateCountMap = demandList.stream()
            .filter(item -> StrUtil.isNotEmpty(item.getState()))
            .collect(Collectors.groupingBy(AutoDemand::getState, Collectors.counting()));
        long total = demandList.size();
        List<Map<String, Object>> rows = new ArrayList<>();
        for (AutoDemandStateEnum state : STATE_ORDER) {
            long count = stateCountMap.getOrDefault(state.getKey(), 0L);
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("key", state.getKey());
            row.put("name", state.getValue());
            row.put("count", count);
            row.put("rate", total == 0 ? "0.00" : CalculationUtil.divide(
                CalculationUtil.multiply(String.valueOf(count), "100", 4), String.valueOf(total), 2, RoundingMode.HALF_UP));
            rows.add(row);
        }
        return rows;
    }

    private List<Map<String, Object>> buildDemandByModule(List<AutoDemand> demandList) {
        Map<String, Long> moduleCount = new HashMap<>();
        Map<String, String> moduleScore = new HashMap<>();
        for (AutoDemand demand : demandList) {
            if (AutoDemandStateEnum.INVALID.getKey().equals(demand.getState()) || StrUtil.isBlank(demand.getModuleId())) {
                continue;
            }
            moduleCount.merge(demand.getModuleId(), 1L, Long::sum);
            moduleScore.put(demand.getModuleId(),
                CalculationUtil.add(nvlScore(moduleScore.get(demand.getModuleId())), nvlScore(demand.getTotalScore()), 2));
        }
        Map<String, String> moduleNameMap = loadModuleNames(moduleCount.keySet());
        return moduleCount.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(8)
            .map(entry -> {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("moduleId", entry.getKey());
                row.put("name", moduleNameMap.getOrDefault(entry.getKey(), "未命名模块"));
                row.put("count", entry.getValue());
                row.put("totalScore", moduleScore.getOrDefault(entry.getKey(), "0"));
                return row;
            })
            .collect(Collectors.toList());
    }

    private Map<String, String> loadModuleNames(Set<String> moduleIds) {
        Map<String, String> nameMap = new HashMap<>();
        if (CollectionUtil.isEmpty(moduleIds)) {
            return nameMap;
        }
        List<AutoModule> modules = autoModuleService.selectByIds(moduleIds.toArray(new String[0]));
        if (CollectionUtil.isEmpty(modules)) {
            return nameMap;
        }
        for (AutoModule module : modules) {
            if (module != null && StrUtil.isNotBlank(module.getId())) {
                nameMap.put(module.getId(), StrUtil.blankToDefault(module.getName(), module.getId()));
            }
        }
        return nameMap;
    }

    private List<Map<String, Object>> buildRoleLoad(List<AutoDemand> demandList) {
        long front = 0;
        long back = 0;
        long test = 0;
        long frontFinish = 0;
        long backFinish = 0;
        long testFinish = 0;
        for (AutoDemand demand : demandList) {
            if (AutoDemandStateEnum.INVALID.getKey().equals(demand.getState())) {
                continue;
            }
            if (StrUtil.isNotBlank(demand.getFrontHandleId())) {
                front++;
                if (AutoDemandRoleStateEnum.FINISH.getKey().equals(demand.getFrontState())) {
                    frontFinish++;
                }
            }
            if (StrUtil.isNotBlank(demand.getBackHandleId())) {
                back++;
                if (AutoDemandRoleStateEnum.FINISH.getKey().equals(demand.getBackState())) {
                    backFinish++;
                }
            }
            if (StrUtil.isNotBlank(demand.getTestHandleId())) {
                test++;
                if (AutoDemandRoleStateEnum.FINISH.getKey().equals(demand.getTestState())) {
                    testFinish++;
                }
            }
        }
        List<Map<String, Object>> rows = new ArrayList<>();
        rows.add(roleLoadRow("front", "前端", front, frontFinish));
        rows.add(roleLoadRow("back", "后端", back, backFinish));
        rows.add(roleLoadRow("test", "测试", test, testFinish));
        return rows;
    }

    private Map<String, Object> roleLoadRow(String key, String name, long total, long finish) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("key", key);
        row.put("name", name);
        row.put("total", total);
        row.put("finish", finish);
        row.put("doing", Math.max(total - finish, 0));
        row.put("finishRate", total == 0 ? "0.00" : CalculationUtil.divide(
            CalculationUtil.multiply(String.valueOf(finish), "100", 4), String.valueOf(total), 2, RoundingMode.HALF_UP));
        return row;
    }

    private List<Map<String, Object>> buildMemberRank(List<AutoDemand> demandList) {
        Map<String, MemberAgg> aggMap = new HashMap<>();
        for (AutoDemand demand : demandList) {
            if (AutoDemandStateEnum.INVALID.getKey().equals(demand.getState())) {
                continue;
            }
            accumulateMember(aggMap, demand.getFrontHandleId(), demand.getFrontInitScore(),
                AutoDemandRoleStateEnum.FINISH.getKey().equals(demand.getFrontState()));
            accumulateMember(aggMap, demand.getBackHandleId(), demand.getBackInitScore(),
                AutoDemandRoleStateEnum.FINISH.getKey().equals(demand.getBackState()));
            accumulateMember(aggMap, demand.getTestHandleId(), demand.getTestInitScore(),
                AutoDemandRoleStateEnum.FINISH.getKey().equals(demand.getTestState()));
        }
        Map<String, String> userNameMap = loadUserNames(aggMap.keySet());
        return aggMap.entrySet().stream()
            .sorted(Comparator
                .comparing((Map.Entry<String, MemberAgg> e) -> Double.parseDouble(e.getValue().score), Comparator.reverseOrder())
                .thenComparing(e -> e.getValue().demandCount, Comparator.reverseOrder()))
            .limit(10)
            .map(entry -> {
                MemberAgg agg = entry.getValue();
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("userId", entry.getKey());
                row.put("name", userNameMap.getOrDefault(entry.getKey(), "未知成员"));
                row.put("demandCount", agg.demandCount);
                row.put("finishCount", agg.finishCount);
                row.put("score", agg.score);
                return row;
            })
            .collect(Collectors.toList());
    }

    private void accumulateMember(Map<String, MemberAgg> aggMap, String userId, String initScore, boolean finished) {
        if (StrUtil.isBlank(userId)) {
            return;
        }
        MemberAgg agg = aggMap.computeIfAbsent(userId, key -> new MemberAgg());
        agg.demandCount++;
        if (finished) {
            agg.finishCount++;
        }
        agg.score = CalculationUtil.add(agg.score, nvlScore(initScore), 2);
    }

    private Map<String, String> loadUserNames(Set<String> userIds) {
        Map<String, String> nameMap = new HashMap<>();
        if (CollectionUtil.isEmpty(userIds)) {
            return nameMap;
        }
        List<Map<String, Object>> rows = new ArrayList<>();
        for (String userId : userIds) {
            Map<String, Object> row = new HashMap<>();
            row.put("userId", userId);
            rows.add(row);
        }
        iAuthUserService.setMationForMap(rows, "userId", "userMation");
        for (Map<String, Object> row : rows) {
            Object mation = row.get("userMation");
            if (mation instanceof Map) {
                Object name = ((Map<?, ?>) mation).get("name");
                if (name != null) {
                    nameMap.put(String.valueOf(row.get("userId")), String.valueOf(name).replaceAll("\\s+", ""));
                }
            }
        }
        return nameMap;
    }

    private Map<String, Object> buildBugSummary(List<AutoBug> bugList) {
        long total = bugList.size();
        long unresolved = 0;
        long toBeReturned = 0;
        long resolved = 0;
        long nonIssue = 0;
        for (AutoBug bug : bugList) {
            if (BugState.UNRESOLVED.getKey().equals(bug.getState())) {
                unresolved++;
            } else if (BugState.TO_BE_RETURNED.getKey().equals(bug.getState())) {
                toBeReturned++;
            } else if (BugState.RESOLVED.getKey().equals(bug.getState())) {
                resolved++;
            }
            if (bug.getIsNonIssue() != null && bug.getIsNonIssue() == 1) {
                nonIssue++;
            }
        }
        Map<String, Object> bug = new LinkedHashMap<>();
        bug.put("total", total);
        bug.put("unresolved", unresolved);
        bug.put("toBeReturned", toBeReturned);
        bug.put("resolved", resolved);
        bug.put("nonIssue", nonIssue);
        bug.put("resolveRate", total == 0 ? "0.00" : CalculationUtil.divide(
            CalculationUtil.multiply(String.valueOf(resolved), "100", 4), String.valueOf(total), 2, RoundingMode.HALF_UP));
        return bug;
    }

    private List<Map<String, Object>> buildBugByState(List<AutoBug> bugList) {
        Map<String, Long> stateCountMap = bugList.stream()
            .filter(item -> StrUtil.isNotEmpty(item.getState()))
            .collect(Collectors.groupingBy(AutoBug::getState, Collectors.counting()));
        long total = bugList.size();
        List<Map<String, Object>> rows = new ArrayList<>();
        for (BugState state : BUG_STATE_ORDER) {
            long count = stateCountMap.getOrDefault(state.getKey(), 0L);
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("key", state.getKey());
            row.put("name", state.getValue());
            row.put("count", count);
            row.put("rate", total == 0 ? "0.00" : CalculationUtil.divide(
                CalculationUtil.multiply(String.valueOf(count), "100", 4), String.valueOf(total), 2, RoundingMode.HALF_UP));
            rows.add(row);
        }
        return rows;
    }

    private String percent(String part, String total) {
        if (CalculationUtil.compareTo(nvlScore(total), "0", 2, RoundingMode.HALF_UP) <= 0) {
            return "0.00";
        }
        return CalculationUtil.divide(
            CalculationUtil.multiply(nvlScore(part), "100", 4), nvlScore(total), 2, RoundingMode.HALF_UP);
    }

    private String nvlScore(Object value) {
        if (value == null || StrUtil.isBlank(value.toString())) {
            return "0";
        }
        return value.toString();
    }

    private QueryWrapper<AutoDemand> buildDemandWrapper(TableSelectInfo tableSelectInfo) {
        QueryWrapper<AutoDemand> queryWrapper = new QueryWrapper<>();
        applyObjectScope(queryWrapper, tableSelectInfo.getObjectId(), tableSelectInfo.getObjectKey());
        applyCreateTimeRange(queryWrapper, MybatisPlusUtil.toColumns(AutoDemand::getCreateTime),
            tableSelectInfo.getStartTime(), tableSelectInfo.getEndTime());
        return queryWrapper;
    }

    private void applyObjectScope(QueryWrapper<AutoDemand> queryWrapper, String objectId, String objectKey) {
        if (StrUtil.isNotEmpty(objectId)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(AutoDemand::getObjectId), objectId);
        }
        if (StrUtil.isNotEmpty(objectKey) && !"null".equals(objectKey)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(AutoDemand::getObjectKey), objectKey);
        }
    }

    private void applyCreateTimeRange(QueryWrapper<?> queryWrapper, String createTimeColumn,
                                      String startTime, String endTime) {
        if (StrUtil.isNotEmpty(startTime)) {
            queryWrapper.ge(createTimeColumn, startTime.length() == 10 ? startTime + " 00:00:00" : startTime);
        }
        if (StrUtil.isNotEmpty(endTime)) {
            queryWrapper.le(createTimeColumn, endTime.length() == 10 ? endTime + " 23:59:59" : endTime);
        }
    }

    private static class MemberAgg {
        private long demandCount;
        private long finishCount;
        private String score = "0";
    }

}
