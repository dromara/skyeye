/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.score.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
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
import com.skyeye.score.service.AutoScoreRecordService;
import com.skyeye.team.rest.ITeamBusinessRest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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

    @Autowired
    private AutoDemandDao autoDemandDao;

    @Autowired
    private AutoBugDao autoBugDao;

    @Autowired
    private ITeamBusinessRest iTeamBusinessRest;

    @Override
    public void grantDemandScoreByState(AutoDemand demand, String userId) {
        grantDemandScoreByStateInternal(demand, userId);
    }

    private int grantDemandScoreByStateInternal(AutoDemand demand, String userId) {
        if (demand == null || StrUtil.isEmpty(demand.getState())) {
            return 0;
        }
        if (StrUtil.equals(demand.getState(), AutoDemandStateEnum.INVALID.getKey())) {
            return 0;
        }
        boolean grantDev = StrUtil.equals(demand.getState(), AutoDemandStateEnum.WAIT_TEST.getKey())
            || StrUtil.equals(demand.getState(), AutoDemandStateEnum.FINISH.getKey());
        boolean grantTest = StrUtil.equals(demand.getState(), AutoDemandStateEnum.FINISH.getKey());
        int count = 0;
        if (grantDev) {
            count += grantRoleScore(demand, "front", AutoScoreTypeEnum.FRONT_GRANT, userId);
            count += grantRoleScore(demand, "back", AutoScoreTypeEnum.BACK_GRANT, userId);
        }
        if (grantTest) {
            count += grantRoleScore(demand, "test", AutoScoreTypeEnum.TEST_GRANT, userId);
        }
        return count;
    }

    private int grantRoleScore(AutoDemand demand, String roleKey, AutoScoreTypeEnum scoreType, String operatorId) {
        String handleId;
        String initScore;
        if ("front".equals(roleKey)) {
            handleId = demand.getFrontHandleId();
            initScore = nvlScore(demand.getFrontInitScore());
        } else if ("back".equals(roleKey)) {
            handleId = demand.getBackHandleId();
            initScore = nvlScore(demand.getBackInitScore());
        } else {
            handleId = demand.getTestHandleId();
            initScore = nvlScore(demand.getTestInitScore());
        }
        if (StrUtil.isEmpty(handleId) || CalculationUtil.compareTo(initScore, "0", 2, RoundingMode.HALF_UP) <= 0) {
            return 0;
        }
        boolean created = createIfAbsent(buildDemandRecord(demand, handleId, scoreType.getKey(), roleKey, initScore,
            "需求开发完成，获得" + roleName(roleKey) + "初始积分"));
        if (!created) {
            return 0;
        }
        UpdateWrapper<AutoDemand> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, demand.getId());
        if ("front".equals(roleKey)) {
            updateWrapper.set(MybatisPlusUtil.toColumns(AutoDemand::getFrontEarnedScore), initScore);
            updateWrapper.set(MybatisPlusUtil.toColumns(AutoDemand::getFrontState), AutoDemandRoleStateEnum.FINISH.getKey());
        } else if ("back".equals(roleKey)) {
            updateWrapper.set(MybatisPlusUtil.toColumns(AutoDemand::getBackEarnedScore), initScore);
            updateWrapper.set(MybatisPlusUtil.toColumns(AutoDemand::getBackState), AutoDemandRoleStateEnum.FINISH.getKey());
        } else {
            updateWrapper.set(MybatisPlusUtil.toColumns(AutoDemand::getTestEarnedScore), initScore);
            updateWrapper.set(MybatisPlusUtil.toColumns(AutoDemand::getTestState), AutoDemandRoleStateEnum.FINISH.getKey());
        }
        autoDemandDao.update(null, updateWrapper);
        return 1;
    }

    @Override
    public void settleResolvedBug(AutoBug bug, String operatorId) {
        if (bug == null || !StrUtil.equals(bug.getState(), BugState.RESOLVED.getKey())) {
            return;
        }
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
    public void settleAutoScoreByVersion(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String objectId = String.valueOf(params.get("objectId"));
        String objectKey = NumberParseUtil.toStr(params.get("objectKey"));
        String versionId = String.valueOf(params.get("versionId"));
        checkVersionParams(objectId, versionId);
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        checkProjectManager(objectId, userId);
        List<AutoDemand> demandList = listVersionDemands(objectId, objectKey, versionId);
        int grantCount = 0;
        for (AutoDemand demand : demandList) {
            grantCount += grantDemandScoreByStateInternal(demand, userId);
        }
        Map<String, Object> bean = new HashMap<>();
        bean.put("grantCount", grantCount);
        outputObject.setBean(bean);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void queryMyAutoScore(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String objectId = String.valueOf(params.get("objectId"));
        String objectKey = NumberParseUtil.toStr(params.get("objectKey"));
        String versionId = String.valueOf(params.get("versionId"));
        checkVersionParams(objectId, versionId);
        String userId = InputObject.getLogParamsStatic().get("id").toString();

        List<AutoDemand> demandList = listVersionDemands(objectId, objectKey, versionId);
        List<AutoScoreRecord> versionRecords = listVersionRecords(objectId, versionId, null);
        Set<String> grantedTypes = versionRecords.stream()
            .filter(item -> StrUtil.isNotEmpty(item.getDemandId()) && StrUtil.isNotEmpty(item.getScoreType()))
            .map(item -> item.getDemandId() + ":" + item.getScoreType())
            .collect(Collectors.toSet());
        List<AutoScoreRecord> myRecords = versionRecords.stream()
            .filter(item -> StrUtil.equals(item.getUserId(), userId))
            .collect(Collectors.toList());

        List<Map<String, Object>> demandRows = new ArrayList<>();
        String expectedScore = "0";
        for (AutoDemand demand : demandList) {
            expectedScore = CalculationUtil.add(expectedScore, appendMergedDemandRow(demandRows, demand, userId, grantedTypes), 2);
        }

        String earnedScore = "0";
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
        List<Map<String, Object>> bugRows = new ArrayList<>();
        for (AutoScoreRecord record : myRecords) {
            String score = nvlScore(record.getScore());
            earnedScore = CalculationUtil.add(earnedScore, score, 2);
            if (StrUtil.equals(record.getScoreType(), AutoScoreTypeEnum.BUG_PENALTY.getKey())
                || StrUtil.equals(record.getScoreType(), AutoScoreTypeEnum.BUG_NON_ISSUE.getKey())) {
                bugDeductScore = CalculationUtil.add(bugDeductScore, score, 2);
                AutoBug bug = bugMap.get(record.getBugId());
                Map<String, Object> row = new HashMap<>();
                row.put("bugId", record.getBugId());
                row.put("bugNo", bug == null ? "" : bug.getNo());
                row.put("bugName", bug == null ? "" : bug.getName());
                row.put("scoreType", record.getScoreType());
                row.put("scoreTypeName", scoreTypeName(record.getScoreType()));
                row.put("score", score);
                row.put("createTime", record.getCreateTime());
                row.put("remark", record.getRemark());
                bugRows.add(row);
            }
        }

        boolean isCharge = isProjectManager(objectId, userId);
        Map<String, Object> bean = new HashMap<>();
        bean.put("expectedScore", expectedScore);
        bean.put("earnedScore", earnedScore);
        bean.put("bugDeductScore", bugDeductScore);
        bean.put("versionTotalScore", sumDemandTotalScore(demandList));
        bean.put("isCharge", isCharge);
        bean.put("demandList", demandRows);
        bean.put("bugList", bugRows);
        bean.put("userList", isCharge ? buildUserScoreList(demandList, versionRecords, grantedTypes) : new ArrayList<>());
        outputObject.setBean(bean);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    private List<Map<String, Object>> buildUserScoreList(List<AutoDemand> demandList, List<AutoScoreRecord> versionRecords,
                                                         Set<String> grantedTypes) {
        Map<String, Map<String, Object>> userMap = new LinkedHashMap<>();
        for (AutoDemand demand : demandList) {
            addUserIfPresent(userMap, demand.getFrontHandleId());
            addUserIfPresent(userMap, demand.getBackHandleId());
            addUserIfPresent(userMap, demand.getTestHandleId());
        }
        for (AutoScoreRecord record : versionRecords) {
            addUserIfPresent(userMap, record.getUserId());
        }
        Map<String, List<AutoScoreRecord>> recordGroup = versionRecords.stream()
            .filter(item -> StrUtil.isNotEmpty(item.getUserId()))
            .collect(Collectors.groupingBy(AutoScoreRecord::getUserId));
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
                if (StrUtil.equals(record.getScoreType(), AutoScoreTypeEnum.BUG_PENALTY.getKey())
                    || StrUtil.equals(record.getScoreType(), AutoScoreTypeEnum.BUG_NON_ISSUE.getKey())) {
                    bugDeductScore = CalculationUtil.add(bugDeductScore, score, 2);
                }
            }
            Map<String, Object> row = entry.getValue();
            row.put("expectedScore", expectedScore);
            row.put("earnedScore", earnedScore);
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

    private String sumDemandTotalScore(List<AutoDemand> demandList) {
        String total = "0";
        for (AutoDemand demand : demandList) {
            total = CalculationUtil.add(total, nvlScore(demand.getTotalScore()), 2);
        }
        return total;
    }

    private List<AutoDemand> listVersionDemands(String objectId, String objectKey, String versionId) {
        QueryWrapper<AutoDemand> demandQuery = new QueryWrapper<>();
        demandQuery.eq(MybatisPlusUtil.toColumns(AutoDemand::getObjectId), objectId);
        if (StrUtil.isNotEmpty(objectKey) && !"null".equals(objectKey)) {
            demandQuery.eq(MybatisPlusUtil.toColumns(AutoDemand::getObjectKey), objectKey);
        }
        demandQuery.eq(MybatisPlusUtil.toColumns(AutoDemand::getVersionId), versionId);
        demandQuery.ne(MybatisPlusUtil.toColumns(AutoDemand::getState), AutoDemandStateEnum.INVALID.getKey());
        return autoDemandDao.selectList(demandQuery);
    }

    private List<AutoScoreRecord> listVersionRecords(String objectId, String versionId, String userId) {
        QueryWrapper<AutoScoreRecord> recordQuery = new QueryWrapper<>();
        recordQuery.eq(MybatisPlusUtil.toColumns(AutoScoreRecord::getObjectId), objectId);
        recordQuery.eq(MybatisPlusUtil.toColumns(AutoScoreRecord::getVersionId), versionId);
        if (StrUtil.isNotEmpty(userId)) {
            recordQuery.eq(MybatisPlusUtil.toColumns(AutoScoreRecord::getUserId), userId);
        }
        return list(recordQuery);
    }

    private void checkVersionParams(String objectId, String versionId) {
        if (StrUtil.isEmpty(objectId) || StrUtil.isEmpty(versionId) || "null".equals(objectId) || "null".equals(versionId)) {
            throw new CustomException("项目和版本不能为空。");
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

    private boolean createIfAbsent(AutoScoreRecord record) {
        QueryWrapper<AutoScoreRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoScoreRecord::getScoreType), record.getScoreType());
        if (StrUtil.isNotEmpty(record.getBugId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(AutoScoreRecord::getBugId), record.getBugId());
        } else {
            queryWrapper.eq(MybatisPlusUtil.toColumns(AutoScoreRecord::getDemandId), record.getDemandId());
            queryWrapper.eq(MybatisPlusUtil.toColumns(AutoScoreRecord::getUserId), record.getUserId());
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
