/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.score.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.bug.classenum.BugState;
import com.skyeye.bug.dao.AutoBugDao;
import com.skyeye.bug.entity.AutoBug;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.enumeration.IsDefaultEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.NumberParseUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.demand.classenum.AutoDemandRoleStateEnum;
import com.skyeye.demand.classenum.AutoDemandStateEnum;
import com.skyeye.demand.dao.AutoDemandDao;
import com.skyeye.demand.entity.AutoDemand;
import com.skyeye.exception.CustomException;
import com.skyeye.score.classenum.AutoScoreTypeEnum;
import com.skyeye.score.dao.AutoScoreRecordDao;
import com.skyeye.score.entity.AutoScoreRecord;
import com.skyeye.score.entity.AutoScoreSettle;
import com.skyeye.score.service.AutoScoreRecordService;
import com.skyeye.score.service.AutoScoreSettleService;
import com.skyeye.team.rest.ITeamBusinessRest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: AutoScoreRecordServiceImpl
 * @Description: 需求积分流水服务层
 * @author: skyeye云系列--卫志强
 * @date: 2026/8/18
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "需求积分", groupName = "需求积分")
public class AutoScoreRecordServiceImpl extends SkyeyeBusinessServiceImpl<AutoScoreRecordDao, AutoScoreRecord> implements AutoScoreRecordService {

    private static final String BUG_SCORE = "2.00";
    /** 延期扣分：实际完成日晚于预计完成日，每自然日扣 2 分 */
    private static final String LATE_PENALTY_PER_DAY = "2.00";

    @Autowired
    private AutoDemandDao autoDemandDao;

    @Autowired
    private AutoBugDao autoBugDao;

    @Autowired
    private ITeamBusinessRest iTeamBusinessRest;

    @Autowired
    private AutoScoreSettleService autoScoreSettleService;

    @Override
    public void grantDemandScoreByState(AutoDemand demand) {
        grantDemandScoreByStateInternal(demand);
    }

    /**
     * 按需求主状态发放角色积分：
     * - 待测试 / 已完成：发放前端、后端初始积分（幂等，已发放则跳过）
     * - 已完成：发放测试初始积分
     * 发放成功后会按预计/实际结束时间尝试延期扣分。
     */
    private int grantDemandScoreByStateInternal(AutoDemand demand) {
        if (demand == null || StrUtil.isEmpty(demand.getState())) {
            return 0;
        }
        if (StrUtil.equals(demand.getState(), AutoDemandStateEnum.INVALID.getKey())) {
            return 0;
        }
        // 研发侧：需求进入待测试或已完成时，前端/后端可领初始积分
        boolean grantDev = StrUtil.equals(demand.getState(), AutoDemandStateEnum.WAIT_TEST.getKey())
            || StrUtil.equals(demand.getState(), AutoDemandStateEnum.FINISH.getKey());
        // 测试侧：需求整体完成时才发放测试积分
        boolean grantTest = StrUtil.equals(demand.getState(), AutoDemandStateEnum.FINISH.getKey());
        int count = 0;
        if (grantDev) {
            count += grantRoleScore(demand, "front", AutoScoreTypeEnum.FRONT_GRANT);
            count += grantRoleScore(demand, "back", AutoScoreTypeEnum.BACK_GRANT);
        }
        if (grantTest) {
            count += grantRoleScore(demand, "test", AutoScoreTypeEnum.TEST_GRANT);
        }
        return count;
    }

    /**
     * 为单个角色写入初始积分流水，并回写角色「已完成」与已获得积分。
     * 已获得积分 = 初始积分 + 延期扣分（扣分为负数，未延期则为 0）。
     */
    private int grantRoleScore(AutoDemand demand, String roleKey, AutoScoreTypeEnum scoreType) {
        String handleId;
        String initScore;
        String estimateEndTime;
        String actualEndTime;
        if ("front".equals(roleKey)) {
            handleId = demand.getFrontHandleId();
            initScore = nvlScore(demand.getFrontInitScore());
            estimateEndTime = demand.getFrontEstimateEndTime();
            actualEndTime = demand.getFrontActualEndTime();
        } else if ("back".equals(roleKey)) {
            handleId = demand.getBackHandleId();
            initScore = nvlScore(demand.getBackInitScore());
            estimateEndTime = demand.getBackEstimateEndTime();
            actualEndTime = demand.getBackActualEndTime();
        } else {
            handleId = demand.getTestHandleId();
            initScore = nvlScore(demand.getTestInitScore());
            estimateEndTime = demand.getTestEstimateEndTime();
            actualEndTime = demand.getTestActualEndTime();
        }
        // 无负责人或初始积分为 0，不发分
        if (StrUtil.isEmpty(handleId) || CalculationUtil.compareTo(initScore, "0", 2, RoundingMode.HALF_UP) <= 0) {
            return 0;
        }
        // 同需求+用户+类型已发过则跳过（避免重复发放）
        boolean created = createIfAbsent(buildDemandRecord(demand, handleId, scoreType.getKey(), roleKey, initScore,
            "需求开发完成，获得" + roleName(roleKey) + "初始积分"));
        if (!created) {
            return 0;
        }
        String now = DateUtil.getTimeAndToString();
        // 角色推进完成时一般已有实际结束时间；兜底用当前时间参与延期对比
        if (StrUtil.isEmpty(actualEndTime)) {
            actualEndTime = now;
        }
        // 延期扣分流水（幂等）；返回负分或 0
        String lateScore = applyLatePenalty(demand, roleKey, handleId, estimateEndTime, actualEndTime);
        String earnedScore = CalculationUtil.add(initScore, lateScore, 2);
        UpdateWrapper<AutoDemand> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, demand.getId());
        if ("front".equals(roleKey)) {
            updateWrapper.set(MybatisPlusUtil.toColumns(AutoDemand::getFrontEarnedScore), earnedScore);
            updateWrapper.set(MybatisPlusUtil.toColumns(AutoDemand::getFrontState), AutoDemandRoleStateEnum.FINISH.getKey());
            if (StrUtil.isEmpty(demand.getFrontActualEndTime())) {
                updateWrapper.set(MybatisPlusUtil.toColumns(AutoDemand::getFrontActualEndTime), actualEndTime);
            }
            if (StrUtil.isEmpty(demand.getFrontActualStartTime())) {
                updateWrapper.set(MybatisPlusUtil.toColumns(AutoDemand::getFrontActualStartTime), now);
            }
        } else if ("back".equals(roleKey)) {
            updateWrapper.set(MybatisPlusUtil.toColumns(AutoDemand::getBackEarnedScore), earnedScore);
            updateWrapper.set(MybatisPlusUtil.toColumns(AutoDemand::getBackState), AutoDemandRoleStateEnum.FINISH.getKey());
            if (StrUtil.isEmpty(demand.getBackActualEndTime())) {
                updateWrapper.set(MybatisPlusUtil.toColumns(AutoDemand::getBackActualEndTime), actualEndTime);
            }
            if (StrUtil.isEmpty(demand.getBackActualStartTime())) {
                updateWrapper.set(MybatisPlusUtil.toColumns(AutoDemand::getBackActualStartTime), now);
            }
        } else {
            updateWrapper.set(MybatisPlusUtil.toColumns(AutoDemand::getTestEarnedScore), earnedScore);
            updateWrapper.set(MybatisPlusUtil.toColumns(AutoDemand::getTestState), AutoDemandRoleStateEnum.FINISH.getKey());
            if (StrUtil.isEmpty(demand.getTestActualEndTime())) {
                updateWrapper.set(MybatisPlusUtil.toColumns(AutoDemand::getTestActualEndTime), actualEndTime);
            }
            if (StrUtil.isEmpty(demand.getTestActualStartTime())) {
                updateWrapper.set(MybatisPlusUtil.toColumns(AutoDemand::getTestActualStartTime), now);
            }
        }
        autoDemandDao.update(null, updateWrapper);
        return 1;
    }

    /**
     * 延期扣分：实际完成日与预计完成日按自然日比较。
     * 晚天数 = getDistanceDay(预计结束, 实际结束)；晚天数大于 0 时扣分 = 晚天数 × {@link #LATE_PENALTY_PER_DAY}。
     * 未填预计时间、日期解析失败、或未延期（含提前完成）均不扣分。
     *
     * @return 扣分流水分值（负数）；未扣则返回 "0"
     */
    private String applyLatePenalty(AutoDemand demand, String roleKey, String handleId,
                                    String estimateEndTime, String actualEndTime) {
        if (StrUtil.isBlank(estimateEndTime) || StrUtil.isBlank(actualEndTime) || StrUtil.isEmpty(handleId)) {
            return "0";
        }
        int lateDays;
        try {
            // 只比日期，同一天内晚几小时不算延期
            lateDays = DateUtil.getDistanceDay(estimateEndTime, actualEndTime);
        } catch (Exception e) {
            return "0";
        }
        if (lateDays <= 0) {
            return "0";
        }
        String deduct = CalculationUtil.multiply(String.valueOf(lateDays), LATE_PENALTY_PER_DAY, 2);
        String score = CalculationUtil.subtract("0", deduct, 2);
        AutoScoreTypeEnum lateType = lateScoreType(roleKey);
        String remark = String.format("晚于预计完成时间%d天，扣除%s分", lateDays, deduct);
        boolean created = createIfAbsent(buildDemandRecord(demand, handleId, lateType.getKey(), roleKey, score, remark));
        return created ? score : "0";
    }

    /** 角色 → 对应延期扣分流水类型 */
    private AutoScoreTypeEnum lateScoreType(String roleKey) {
        if ("front".equals(roleKey)) {
            return AutoScoreTypeEnum.FRONT_LATE_PENALTY;
        }
        if ("back".equals(roleKey)) {
            return AutoScoreTypeEnum.BACK_LATE_PENALTY;
        }
        return AutoScoreTypeEnum.TEST_LATE_PENALTY;
    }

    /** 是否计入「扣分合计」的流水类型（Bug / 非问题 / 延期 / 额外扣分） */
    private boolean isDeductScoreType(String scoreType) {
        return StrUtil.equals(scoreType, AutoScoreTypeEnum.BUG_PENALTY.getKey())
            || StrUtil.equals(scoreType, AutoScoreTypeEnum.BUG_NON_ISSUE.getKey())
            || StrUtil.equals(scoreType, AutoScoreTypeEnum.FRONT_LATE_PENALTY.getKey())
            || StrUtil.equals(scoreType, AutoScoreTypeEnum.BACK_LATE_PENALTY.getKey())
            || StrUtil.equals(scoreType, AutoScoreTypeEnum.TEST_LATE_PENALTY.getKey())
            || StrUtil.equals(scoreType, AutoScoreTypeEnum.EXTRA_DEDUCT.getKey());
    }

    /** 是否为需求延期扣分类型（明细里用需求编号/标题展示） */
    private boolean isLatePenaltyType(String scoreType) {
        return StrUtil.equals(scoreType, AutoScoreTypeEnum.FRONT_LATE_PENALTY.getKey())
            || StrUtil.equals(scoreType, AutoScoreTypeEnum.BACK_LATE_PENALTY.getKey())
            || StrUtil.equals(scoreType, AutoScoreTypeEnum.TEST_LATE_PENALTY.getKey());
    }

    private boolean isExtraDeductType(String scoreType) {
        return StrUtil.equals(scoreType, AutoScoreTypeEnum.EXTRA_DEDUCT.getKey());
    }

    @Override
    public void settleResolvedBug(AutoBug bug, String operatorId) {
        if (bug == null || !StrUtil.equals(bug.getState(), BugState.RESOLVED.getKey())) {
            return;
        }
        // 是否是非问题
        boolean nonIssue = Objects.equals(bug.getIsNonIssue(), IsDefaultEnum.IS_DEFAULT.getKey());
        String targetUserId = nonIssue ? bug.getCreateId() : bug.getHandleId();
        if (StrUtil.isEmpty(targetUserId)) {
            return;
        }
        String scoreType = nonIssue ? AutoScoreTypeEnum.BUG_NON_ISSUE.getKey() : AutoScoreTypeEnum.BUG_PENALTY.getKey();
        String remark = nonIssue ? "非问题，扣除提出人2分" : "Bug已解决，扣除负责人2分";
        createIfAbsent(buildBugRecord(bug, targetUserId, scoreType, CalculationUtil.subtract("0", BUG_SCORE, 2), remark));
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void settleAutoScore(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String objectId = String.valueOf(params.get("objectId"));
        String objectKey = NumberParseUtil.toStr(params.get("objectKey"));
        String remark = NumberParseUtil.toStr(params.get("remark"));
        checkObjectId(objectId);
        String operatorId = InputObject.getLogParamsStatic().get("id").toString();
        checkProjectManager(objectId, operatorId);
        List<Map<String, Object>> settleUsers = JSONUtil.toList(params.get("userList").toString(), null);
        if (CollectionUtil.isEmpty(settleUsers)) {
            throw new CustomException("请填写本次结算积分。");
        }
        Map<String, String> remainMap = buildRemainScoreMap(objectId, objectKey);
        int settleCount = 0;
        String totalSettleScore = "0";
        for (Map<String, Object> item : settleUsers) {
            String userId = NumberParseUtil.toStr(item.get("userId"));
            String score = nvlScore(item.get("score"));
            if (StrUtil.isEmpty(userId) || CalculationUtil.compareTo(score, "0", 2, RoundingMode.HALF_UP) <= 0) {
                continue;
            }
            String remainScore = nvlScore(remainMap.get(userId));
            if (CalculationUtil.compareTo(score, remainScore, 2, RoundingMode.HALF_UP) > 0) {
                throw new CustomException("结算积分不能大于待结算积分。");
            }
            AutoScoreSettle settle = new AutoScoreSettle();
            settle.setObjectId(objectId);
            settle.setObjectKey(objectKey);
            settle.setUserId(userId);
            settle.setScore(score);
            settle.setRemark(remark);
            autoScoreSettleService.createEntity(settle, operatorId);
            remainMap.put(userId, CalculationUtil.subtract(remainScore, score, 2));
            settleCount++;
            totalSettleScore = CalculationUtil.add(totalSettleScore, score, 2);
        }
        if (settleCount == 0) {
            throw new CustomException("请填写本次结算积分。");
        }
        Map<String, Object> bean = new HashMap<>();
        bean.put("settleCount", settleCount);
        bean.put("totalSettleScore", totalSettleScore);
        outputObject.setBean(bean);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void queryAutoScoreBoard(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String objectId = String.valueOf(params.get("objectId"));
        String objectKey = NumberParseUtil.toStr(params.get("objectKey"));
        checkObjectId(objectId);
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        List<Map<String, Object>> userList = buildUserScoreList(objectId, objectKey, null);
        String totalScore = "0";
        String remainScore = "0";
        for (Map<String, Object> row : userList) {
            totalScore = CalculationUtil.add(totalScore, nvlScore(row.get("totalScore")), 2);
            remainScore = CalculationUtil.add(remainScore, nvlScore(row.get("remainScore")), 2);
        }
        Map<String, Object> bean = new HashMap<>();
        bean.put("isCharge", isProjectManager(objectId, userId));
        bean.put("totalScore", totalScore);
        bean.put("remainScore", remainScore);
        bean.put("userList", userList);
        outputObject.setBean(bean);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void queryMyAutoScore(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String objectId = String.valueOf(params.get("objectId"));
        String objectKey = NumberParseUtil.toStr(params.get("objectKey"));
        String versionId = NumberParseUtil.toStr(params.get("versionId"));
        checkObjectId(objectId);
        String userId = InputObject.getLogParamsStatic().get("id").toString();

        List<AutoDemand> demandList = listDemands(objectId, objectKey, versionId);
        List<AutoScoreRecord> scoreRecords = listScoreRecords(objectId, versionId, null);
        Set<String> grantedTypes = scoreRecords.stream()
            .filter(item -> StrUtil.isNotEmpty(item.getDemandId()) && StrUtil.isNotEmpty(item.getScoreType()))
            .map(item -> item.getDemandId() + ":" + item.getScoreType())
            .collect(Collectors.toSet());
        List<AutoScoreRecord> myRecords = scoreRecords.stream()
            .filter(item -> StrUtil.equals(item.getUserId(), userId))
            .collect(Collectors.toList());

        List<Map<String, Object>> demandRows = new ArrayList<>();
        String expectedScore = "0";
        for (AutoDemand demand : demandList) {
            expectedScore = CalculationUtil.add(expectedScore, appendMergedDemandRow(demandRows, demand, userId, grantedTypes), 2);
        }

        String earnedScore = "0";
        // 字段名沿用 bugDeductScore，实际汇总 Bug / 延期 / 额外扣分
        String bugDeductScore = "0";
        List<String> bugIds = myRecords.stream()
            .map(AutoScoreRecord::getBugId)
            .filter(StrUtil::isNotEmpty)
            .distinct()
            .collect(Collectors.toList());
        Map<String, AutoBug> bugMap = new HashMap<>();
        if (!bugIds.isEmpty()) {
            List<AutoBug> bugs = autoBugDao.selectBatchIds(bugIds);
            bugMap = bugs.stream().collect(Collectors.toMap(AutoBug::getId, item -> item, (a, b) -> a));
        }
        // 延期扣分 / 额外扣分关联需求，用于明细展示编号/标题
        List<String> lateDemandIds = myRecords.stream()
            .filter(item -> isLatePenaltyType(item.getScoreType()) || isExtraDeductType(item.getScoreType()))
            .map(AutoScoreRecord::getDemandId)
            .filter(StrUtil::isNotEmpty)
            .distinct()
            .collect(Collectors.toList());
        Map<String, AutoDemand> lateDemandMap = new HashMap<>();
        if (!lateDemandIds.isEmpty()) {
            List<AutoDemand> demands = autoDemandDao.selectBatchIds(lateDemandIds);
            lateDemandMap = demands.stream().collect(Collectors.toMap(AutoDemand::getId, item -> item, (a, b) -> a));
        }
        List<Map<String, Object>> bugRows = new ArrayList<>();
        for (AutoScoreRecord record : myRecords) {
            String score = nvlScore(record.getScore());
            earnedScore = CalculationUtil.add(earnedScore, score, 2);
            if (!isDeductScoreType(record.getScoreType())) {
                continue;
            }
            bugDeductScore = CalculationUtil.add(bugDeductScore, score, 2);
            Map<String, Object> row = new HashMap<>();
            row.put("scoreType", record.getScoreType());
            row.put("scoreTypeName", scoreTypeName(record.getScoreType()));
            row.put("score", score);
            row.put("createTime", record.getCreateTime());
            row.put("remark", record.getRemark());
            if (isLatePenaltyType(record.getScoreType()) || isExtraDeductType(record.getScoreType())) {
                // 延期/额外扣分：明细「编号/标题」优先展示需求信息
                AutoDemand demand = lateDemandMap.get(record.getDemandId());
                row.put("demandId", record.getDemandId());
                row.put("bugId", "");
                row.put("bugNo", demand == null ? "" : demand.getNo());
                row.put("bugName", demand == null ? StrUtil.blankToDefault(record.getRemark(), "额外扣分") : demand.getName());
            } else {
                AutoBug bug = bugMap.get(record.getBugId());
                row.put("demandId", bug == null ? "" : bug.getDemandId());
                row.put("bugId", record.getBugId());
                row.put("bugNo", bug == null ? "" : bug.getNo());
                row.put("bugName", bug == null ? "" : bug.getName());
            }
            bugRows.add(row);
        }
        String settledScore = nvlScore(sumSettledByUser(objectId, objectKey).get(userId));
        String projectEarnedScore = sumUserEarnedScore(listScoreRecords(objectId, null, userId));
        String versionEarnedScore = earnedScore;
        Map<String, Object> bean = new HashMap<>();
        bean.put("projectTotalScore", projectEarnedScore);
        bean.put("versionTotalScore", versionEarnedScore);
        bean.put("totalScore", projectEarnedScore);
        bean.put("expectedScore", expectedScore);
        bean.put("earnedScore", earnedScore);
        bean.put("settledScore", settledScore);
        bean.put("remainScore", CalculationUtil.subtract(projectEarnedScore, settledScore, 2));
        bean.put("bugDeductScore", bugDeductScore);
        bean.put("demandList", demandRows);
        bean.put("bugList", bugRows);
        outputObject.setBean(bean);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void writeExtraAutoScore(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String objectId = String.valueOf(params.get("objectId"));
        String objectKey = NumberParseUtil.toStr(params.get("objectKey"));
        String userId = NumberParseUtil.toStr(params.get("userId"));
        String scoreType = NumberParseUtil.toStr(params.get("scoreType"));
        String score = nvlScore(params.get("score"));
        String versionId = NumberParseUtil.toStr(params.get("versionId"));
        String demandId = NumberParseUtil.toStr(params.get("demandId"));
        String remark = NumberParseUtil.toStr(params.get("remark"));
        checkObjectId(objectId);
        String operatorId = InputObject.getLogParamsStatic().get("id").toString();
        checkProjectManager(objectId, operatorId);
        if (StrUtil.isBlank(userId)) {
            throw new CustomException("请选择成员。");
        }
        boolean isGrant = StrUtil.equals(scoreType, AutoScoreTypeEnum.EXTRA_GRANT.getKey());
        boolean isDeduct = StrUtil.equals(scoreType, AutoScoreTypeEnum.EXTRA_DEDUCT.getKey());
        if (!isGrant && !isDeduct) {
            throw new CustomException("积分类型仅支持额外加分或额外扣分。");
        }
        if (CalculationUtil.compareTo(score, "0", 2, RoundingMode.HALF_UP) <= 0) {
            throw new CustomException("积分必须大于0。");
        }
        if (StrUtil.isNotBlank(demandId)) {
            AutoDemand demand = autoDemandDao.selectById(demandId);
            if (demand == null || !StrUtil.equals(demand.getObjectId(), objectId)) {
                throw new CustomException("需求不存在或不属于当前项目。");
            }
            if (StrUtil.isBlank(versionId)) {
                versionId = demand.getVersionId();
            }
        }
        String finalScore = isDeduct ? CalculationUtil.subtract("0", score, 2) : score;
        AutoScoreRecord record = new AutoScoreRecord();
        record.setObjectId(objectId);
        record.setObjectKey(objectKey);
        record.setUserId(userId);
        record.setScoreType(scoreType);
        record.setScore(finalScore);
        record.setVersionId(StrUtil.blankToDefault(versionId, null));
        record.setDemandId(StrUtil.blankToDefault(demandId, null));
        record.setRemark(StrUtil.blankToDefault(remark, isGrant ? "额外加分" : "额外扣分"));
        createEntity(record, operatorId);
        Map<String, Object> bean = new HashMap<>();
        bean.put("id", record.getId());
        bean.put("score", finalScore);
        bean.put("scoreType", scoreType);
        outputObject.setBean(bean);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    private String sumUserEarnedScore(List<AutoScoreRecord> records) {
        String earnedScore = "0";
        for (AutoScoreRecord record : records) {
            earnedScore = CalculationUtil.add(earnedScore, nvlScore(record.getScore()), 2);
        }
        return earnedScore;
    }

    private Map<String, String> buildRemainScoreMap(String objectId, String objectKey) {
        List<Map<String, Object>> userList = buildUserScoreList(objectId, objectKey, null);
        Map<String, String> remainMap = new HashMap<>();
        for (Map<String, Object> row : userList) {
            remainMap.put(String.valueOf(row.get("userId")), nvlScore(row.get("remainScore")));
        }
        return remainMap;
    }

    private List<Map<String, Object>> buildUserScoreList(String objectId, String objectKey, String versionId) {
        List<AutoDemand> demandList = listDemands(objectId, objectKey, versionId);
        List<AutoScoreRecord> scoreRecords = listScoreRecords(objectId, versionId, null);
        Set<String> grantedTypes = scoreRecords.stream()
            .filter(item -> StrUtil.isNotEmpty(item.getDemandId()) && StrUtil.isNotEmpty(item.getScoreType()))
            .map(item -> item.getDemandId() + ":" + item.getScoreType())
            .collect(Collectors.toSet());
        return buildUserScoreList(demandList, scoreRecords, grantedTypes, objectId, objectKey);
    }

    private List<Map<String, Object>> buildUserScoreList(List<AutoDemand> demandList, List<AutoScoreRecord> scoreRecords,
                                                         Set<String> grantedTypes, String objectId, String objectKey) {
        Map<String, Map<String, Object>> userMap = new LinkedHashMap<>();
        addTeamUsers(userMap, objectId);
        for (AutoDemand demand : demandList) {
            addUserIfPresent(userMap, demand.getFrontHandleId());
            addUserIfPresent(userMap, demand.getBackHandleId());
            addUserIfPresent(userMap, demand.getTestHandleId());
        }
        for (AutoScoreRecord record : scoreRecords) {
            addUserIfPresent(userMap, record.getUserId());
        }
        Map<String, List<AutoScoreRecord>> recordGroup = scoreRecords.stream()
            .filter(item -> StrUtil.isNotEmpty(item.getUserId()))
            .collect(Collectors.groupingBy(AutoScoreRecord::getUserId));
        Map<String, String> settledMap = sumSettledByUser(objectId, objectKey);
        for (Map.Entry<String, Map<String, Object>> entry : userMap.entrySet()) {
            String userId = entry.getKey();
            String expectedScore = "0";
            for (AutoDemand demand : demandList) {
                expectedScore = CalculationUtil.add(expectedScore, calcUngrantedInit(demand, userId, grantedTypes), 2);
            }
            String earnedScore = "0";
            String bugDeductScore = "0";
            for (AutoScoreRecord record : recordGroup.getOrDefault(userId, new ArrayList<>())) {
                String score = nvlScore(record.getScore());
                earnedScore = CalculationUtil.add(earnedScore, score, 2);
                if (isDeductScoreType(record.getScoreType())) {
                    bugDeductScore = CalculationUtil.add(bugDeductScore, score, 2);
                }
            }
            String settledScore = nvlScore(settledMap.get(userId));
            Map<String, Object> row = entry.getValue();
            row.put("expectedScore", expectedScore);
            row.put("totalScore", earnedScore);
            row.put("earnedScore", earnedScore);
            row.put("settledScore", settledScore);
            row.put("remainScore", CalculationUtil.subtract(earnedScore, settledScore, 2));
            row.put("bugDeductScore", bugDeductScore);
        }
        List<Map<String, Object>> userList = new ArrayList<>(userMap.values());
        iAuthUserService.setNameForMap(userList, "userId", "userName");
        return userList;
    }

    private void addUserIfPresent(Map<String, Map<String, Object>> userMap, String userId) {
        if (StrUtil.isEmpty(userId) || userMap.containsKey(userId)) {
            return;
        }
        Map<String, Object> row = new HashMap<>();
        row.put("userId", userId);
        userMap.put(userId, row);
    }

    @SuppressWarnings("unchecked")
    private void addTeamUsers(Map<String, Map<String, Object>> userMap, String objectId) {
        try {
            Map<String, Object> team = ExecuteFeignClient.get(() -> iTeamBusinessRest.queryTeamBusiness(objectId)).getBean();
            if (CollectionUtil.isEmpty(team)) {
                return;
            }
            addUserIfPresent(userMap, NumberParseUtil.toStr(team.get("chargeUser")));
            if (team.get("chargeUserMation") instanceof Map) {
                addUserIfPresent(userMap, NumberParseUtil.toStr(((Map<String, Object>) team.get("chargeUserMation")).get("id")));
            }
            Object roleList = team.get("teamRoleList");
            if (!(roleList instanceof List)) {
                return;
            }
            for (Object roleObj : (List<?>) roleList) {
                if (!(roleObj instanceof Map)) {
                    continue;
                }
                Object users = ((Map<String, Object>) roleObj).get("teamRoleUserList");
                if (!(users instanceof List)) {
                    continue;
                }
                for (Object userObj : (List<?>) users) {
                    if (!(userObj instanceof Map)) {
                        continue;
                    }
                    Map<String, Object> user = (Map<String, Object>) userObj;
                    String userId = NumberParseUtil.toStr(user.get("userId"));
                    if (StrUtil.isEmpty(userId) && user.get("userMation") instanceof Map) {
                        userId = NumberParseUtil.toStr(((Map<String, Object>) user.get("userMation")).get("id"));
                    }
                    addUserIfPresent(userMap, userId);
                }
            }
        } catch (Exception ignored) {
            // 团队查询失败时，仍展示需求和流水中的人员
        }
    }

    private String appendMergedDemandRow(List<Map<String, Object>> rows, AutoDemand demand, String userId, Set<String> grantedTypes) {
        String initScore = "0";
        int roleCount = 0;
        int grantedCount = 0;
        if (StrUtil.equals(demand.getFrontHandleId(), userId)) {
            roleCount++;
            initScore = CalculationUtil.add(initScore, nvlScore(demand.getFrontInitScore()), 2);
            if (grantedTypes.contains(demand.getId() + ":" + AutoScoreTypeEnum.FRONT_GRANT.getKey())) {
                grantedCount++;
            }
        }
        if (StrUtil.equals(demand.getBackHandleId(), userId)) {
            roleCount++;
            initScore = CalculationUtil.add(initScore, nvlScore(demand.getBackInitScore()), 2);
            if (grantedTypes.contains(demand.getId() + ":" + AutoScoreTypeEnum.BACK_GRANT.getKey())) {
                grantedCount++;
            }
        }
        if (StrUtil.equals(demand.getTestHandleId(), userId)) {
            roleCount++;
            initScore = CalculationUtil.add(initScore, nvlScore(demand.getTestInitScore()), 2);
            if (grantedTypes.contains(demand.getId() + ":" + AutoScoreTypeEnum.TEST_GRANT.getKey())) {
                grantedCount++;
            }
        }
        if (roleCount == 0) {
            return "0";
        }
        String remaining = calcUngrantedInit(demand, userId, grantedTypes);
        String statusName = grantedCount == 0 ? "预计获得" : (grantedCount == roleCount ? "已获得" : "部分已获得");
        Map<String, Object> row = new HashMap<>();
        row.put("demandId", demand.getId());
        row.put("demandNo", demand.getNo());
        row.put("demandName", demand.getName());
        row.put("initScore", initScore);
        row.put("granted", grantedCount == roleCount);
        row.put("statusName", statusName);
        rows.add(row);
        return remaining;
    }

    private String calcUngrantedInit(AutoDemand demand, String userId, Set<String> grantedTypes) {
        String remaining = "0";
        remaining = addUngranted(remaining, userId, demand.getFrontHandleId(), demand.getFrontInitScore(),
            demand.getId(), AutoScoreTypeEnum.FRONT_GRANT.getKey(), grantedTypes);
        remaining = addUngranted(remaining, userId, demand.getBackHandleId(), demand.getBackInitScore(),
            demand.getId(), AutoScoreTypeEnum.BACK_GRANT.getKey(), grantedTypes);
        remaining = addUngranted(remaining, userId, demand.getTestHandleId(), demand.getTestInitScore(),
            demand.getId(), AutoScoreTypeEnum.TEST_GRANT.getKey(), grantedTypes);
        return remaining;
    }

    private String addUngranted(String current, String userId, String handleId, String initScore,
                                String demandId, String scoreType, Set<String> grantedTypes) {
        if (!StrUtil.equals(handleId, userId) || grantedTypes.contains(demandId + ":" + scoreType)) {
            return current;
        }
        return CalculationUtil.add(current, nvlScore(initScore), 2);
    }

    private List<AutoDemand> listDemands(String objectId, String objectKey, String versionId) {
        QueryWrapper<AutoDemand> demandQuery = new QueryWrapper<>();
        demandQuery.eq(MybatisPlusUtil.toColumns(AutoDemand::getObjectId), objectId);
        if (StrUtil.isNotEmpty(objectKey) && !"null".equals(objectKey)) {
            demandQuery.eq(MybatisPlusUtil.toColumns(AutoDemand::getObjectKey), objectKey);
        }
        if (StrUtil.isNotEmpty(versionId) && !"null".equals(versionId)) {
            demandQuery.eq(MybatisPlusUtil.toColumns(AutoDemand::getVersionId), versionId);
        }
        demandQuery.ne(MybatisPlusUtil.toColumns(AutoDemand::getState), AutoDemandStateEnum.INVALID.getKey());
        return autoDemandDao.selectList(demandQuery);
    }

    private List<AutoScoreRecord> listScoreRecords(String objectId, String versionId, String userId) {
        QueryWrapper<AutoScoreRecord> recordQuery = new QueryWrapper<>();
        recordQuery.eq(MybatisPlusUtil.toColumns(AutoScoreRecord::getObjectId), objectId);
        if (StrUtil.isNotEmpty(versionId) && !"null".equals(versionId)) {
            recordQuery.eq(MybatisPlusUtil.toColumns(AutoScoreRecord::getVersionId), versionId);
        }
        if (StrUtil.isNotEmpty(userId)) {
            recordQuery.eq(MybatisPlusUtil.toColumns(AutoScoreRecord::getUserId), userId);
        }
        return list(recordQuery);
    }

    private Map<String, String> sumSettledByUser(String objectId, String objectKey) {
        QueryWrapper<AutoScoreSettle> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoScoreSettle::getObjectId), objectId);
        if (StrUtil.isNotEmpty(objectKey) && !"null".equals(objectKey)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(AutoScoreSettle::getObjectKey), objectKey);
        }
        List<AutoScoreSettle> settleList = autoScoreSettleService.list(queryWrapper);
        Map<String, String> settledMap = new HashMap<>();
        for (AutoScoreSettle settle : settleList) {
            if (StrUtil.isEmpty(settle.getUserId())) {
                continue;
            }
            String current = nvlScore(settledMap.get(settle.getUserId()));
            settledMap.put(settle.getUserId(), CalculationUtil.add(current, nvlScore(settle.getScore()), 2));
        }
        return settledMap;
    }

    private void checkObjectId(String objectId) {
        if (StrUtil.isEmpty(objectId) || "null".equals(objectId)) {
            throw new CustomException("项目不能为空。");
        }
    }

    private boolean isProjectManager(String objectId, String userId) {
        try {
            return StrUtil.equals(queryChargeUserId(objectId), userId);
        } catch (Exception e) {
            return false;
        }
    }

    private void checkProjectManager(String objectId, String userId) {
        if (!StrUtil.equals(queryChargeUserId(objectId), userId)) {
            throw new CustomException("仅项目经理可结算积分。");
        }
    }

    @SuppressWarnings("unchecked")
    private String queryChargeUserId(String objectId) {
        Map<String, Object> team = ExecuteFeignClient.get(() -> iTeamBusinessRest.queryTeamBusiness(objectId)).getBean();
        if (CollectionUtil.isEmpty(team)) {
            throw new CustomException("未查询到项目团队，无法结算积分。");
        }
        String chargeUser = NumberParseUtil.toStr(team.get("chargeUser"));
        if (StrUtil.isEmpty(chargeUser) && team.get("chargeUserMation") instanceof Map) {
            chargeUser = NumberParseUtil.toStr(((Map<String, Object>) team.get("chargeUserMation")).get("id"));
        }
        if (StrUtil.isEmpty(chargeUser)) {
            throw new CustomException("未查询到项目经理，无法结算积分。");
        }
        return chargeUser;
    }

    private AutoScoreRecord buildDemandRecord(AutoDemand demand, String userId, String scoreType, String roleKey,
                                              String score, String remark) {
        AutoScoreRecord record = new AutoScoreRecord();
        record.setObjectId(demand.getObjectId());
        record.setObjectKey(demand.getObjectKey());
        record.setVersionId(demand.getVersionId());
        record.setUserId(userId);
        record.setDemandId(demand.getId());
        record.setScoreType(scoreType);
        record.setRoleKey(roleKey);
        record.setScore(score);
        record.setRemark(remark);
        return record;
    }

    private AutoScoreRecord buildBugRecord(AutoBug bug, String userId, String scoreType, String score, String remark) {
        AutoScoreRecord record = new AutoScoreRecord();
        record.setObjectId(bug.getObjectId());
        record.setObjectKey(bug.getObjectKey());
        record.setVersionId(bug.getVersionId());
        record.setUserId(userId);
        record.setDemandId(bug.getDemandId());
        record.setBugId(bug.getId());
        record.setScoreType(scoreType);
        record.setScore(score);
        record.setRemark(remark);
        return record;
    }

    /**
     * 按业务唯一键幂等写入流水：存在则跳过。
     * Bug：scoreType + bugId；需求：scoreType + demandId + userId + roleKey（同一人兼多角色时可分别发/扣分）。
     */
    private boolean createIfAbsent(AutoScoreRecord record) {
        QueryWrapper<AutoScoreRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoScoreRecord::getScoreType), record.getScoreType());
        if (StrUtil.isNotEmpty(record.getBugId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(AutoScoreRecord::getBugId), record.getBugId());
        } else {
            queryWrapper.eq(MybatisPlusUtil.toColumns(AutoScoreRecord::getDemandId), record.getDemandId());
            queryWrapper.eq(MybatisPlusUtil.toColumns(AutoScoreRecord::getUserId), record.getUserId());
            // 同一用户兼前端/后端时，按 roleKey 区分流水，避免互相挡住
            if (StrUtil.isNotEmpty(record.getRoleKey())) {
                queryWrapper.eq(MybatisPlusUtil.toColumns(AutoScoreRecord::getRoleKey), record.getRoleKey());
            }
        }
        if (count(queryWrapper) > 0) {
            return false;
        }
        String operatorId = InputObject.getLogParamsStatic() == null ? record.getUserId()
            : InputObject.getLogParamsStatic().get("id").toString();
        createEntity(record, operatorId);
        return true;
    }

    private String roleName(String roleKey) {
        if ("front".equals(roleKey)) {
            return "前端";
        }
        if ("back".equals(roleKey)) {
            return "后端";
        }
        return "测试";
    }

    private String scoreTypeName(String scoreType) {
        for (AutoScoreTypeEnum item : AutoScoreTypeEnum.values()) {
            if (StrUtil.equals(item.getKey(), scoreType)) {
                return item.getValue();
            }
        }
        return scoreType;
    }

    private String nvlScore(Object value) {
        String str = NumberParseUtil.toStr(value);
        if (StrUtil.isBlank(str) || "null".equalsIgnoreCase(str)) {
            return "0";
        }
        return str;
    }

}
