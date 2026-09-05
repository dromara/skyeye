/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye-report
 ******************************************************************************/

package com.skyeye.demand.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeTeamAuthServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.NumberParseUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.demand.classenum.AutoDemandAuthEnum;
import com.skyeye.demand.classenum.AutoDemandRoleStateEnum;
import com.skyeye.demand.classenum.AutoDemandStateEnum;
import com.skyeye.demand.dao.AutoDemandDao;
import com.skyeye.demand.entity.AutoDemand;
import com.skyeye.demand.service.AutoDemandService;
import com.skyeye.exception.CustomException;
import com.skyeye.module.service.AutoModuleService;
import com.skyeye.projectconfig.service.AutoProjectConfigService;
import com.skyeye.score.service.AutoScoreRecordService;
import com.skyeye.version.service.AutoVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: AutoDemandServiceImpl
 * @Description: 需求表服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/5/16 23:20
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye-report Inc. All rights reserved.
 * 注意：本内容具体规则请参照readme执行，地址：https://gitee.com/doc_wei01/skyeye-report/blob/master/README.md
 */
@Service
@SkyeyeService(name = "需求管理", groupName = "需求管理", teamAuth = true, allowDynamicAttrKey = false)
public class AutoDemandServiceImpl extends SkyeyeTeamAuthServiceImpl<AutoDemandDao, AutoDemand> implements AutoDemandService {

    @Autowired
    private AutoVersionService autoVersionService;

    @Autowired
    private AutoModuleService autoModuleService;

    @Autowired
    private AutoDemandService autoDemandService;

    @Autowired
    private AutoScoreRecordService autoScoreRecordService;

    @Autowired
    private AutoDemandAiDraftService autoDemandAiDraftService;

    @Autowired
    private AutoDemandCaseAiDraftService autoDemandCaseAiDraftService;

    @Autowired
    private AutoProjectConfigService autoProjectConfigService;

    @Override
    public Class getAuthEnumClass() {
        return AutoDemandAuthEnum.class;
    }

    @Override
    public List<String> getAuthPermissionKeyList() {
        return Arrays.asList(AutoDemandAuthEnum.ADD.getKey(), AutoDemandAuthEnum.EDIT.getKey(), AutoDemandAuthEnum.DELETE.getKey());
    }

    @Override
    public void createPrepose(AutoDemand autoDemand) {
        Map<String, Object> business = BeanUtil.beanToMap(autoDemand);
        String no = iCodeRuleService.getNextCodeByClassName(getClass().getName(), business);
        autoDemand.setNo(no);
        autoDemand.setFrontEarnedScore(nvlScore(autoDemand.getFrontEarnedScore()));
        autoDemand.setBackEarnedScore(nvlScore(autoDemand.getBackEarnedScore()));
        autoDemand.setTestEarnedScore(nvlScore(autoDemand.getTestEarnedScore()));
        if (StrUtil.isEmpty(autoDemand.getFrontState())) {
            autoDemand.setFrontState(AutoDemandRoleStateEnum.WAIT.getKey());
        }
        if (StrUtil.isEmpty(autoDemand.getBackState())) {
            autoDemand.setBackState(AutoDemandRoleStateEnum.WAIT.getKey());
        }
        if (StrUtil.isEmpty(autoDemand.getTestState())) {
            autoDemand.setTestState(AutoDemandRoleStateEnum.WAIT.getKey());
        }
        autoDemand.setState(AutoDemandStateEnum.WAIT_RESEARCH.getKey());
    }

    @Override
    protected void writePostpose(AutoDemand entity, String userId) {
        super.writePostpose(entity, userId);
        autoScoreRecordService.grantDemandScoreByState(entity);
    }

    @Override
    public void updatePrepose(AutoDemand entity) {
        AutoDemand oldDemand = getById(entity.getId());
        if (oldDemand == null) {
            return;
        }
        entity.setFrontEarnedScore(nvlScore(oldDemand.getFrontEarnedScore()));
        entity.setBackEarnedScore(nvlScore(oldDemand.getBackEarnedScore()));
        entity.setTestEarnedScore(nvlScore(oldDemand.getTestEarnedScore()));
        protectFinishedRole(oldDemand, entity, "front");
        protectFinishedRole(oldDemand, entity, "back");
        protectFinishedRole(oldDemand, entity, "test");
        keepActualTimeIfBlank(oldDemand, entity);
    }

    private void protectFinishedRole(AutoDemand oldDemand, AutoDemand entity, String roleKey) {
        if (!isRoleFinish(getRoleState(oldDemand, roleKey))) {
            return;
        }
        if ("front".equals(roleKey)) {
            entity.setFrontHandleId(oldDemand.getFrontHandleId());
            entity.setFrontEstimateStartTime(oldDemand.getFrontEstimateStartTime());
            entity.setFrontEstimateEndTime(oldDemand.getFrontEstimateEndTime());
            entity.setFrontActualStartTime(oldDemand.getFrontActualStartTime());
            entity.setFrontActualEndTime(oldDemand.getFrontActualEndTime());
            entity.setFrontState(oldDemand.getFrontState());
            entity.setFrontRatio(oldDemand.getFrontRatio());
            entity.setFrontInitScore(oldDemand.getFrontInitScore());
        } else if ("back".equals(roleKey)) {
            entity.setBackHandleId(oldDemand.getBackHandleId());
            entity.setBackEstimateStartTime(oldDemand.getBackEstimateStartTime());
            entity.setBackEstimateEndTime(oldDemand.getBackEstimateEndTime());
            entity.setBackActualStartTime(oldDemand.getBackActualStartTime());
            entity.setBackActualEndTime(oldDemand.getBackActualEndTime());
            entity.setBackState(oldDemand.getBackState());
            entity.setBackRatio(oldDemand.getBackRatio());
            entity.setBackInitScore(oldDemand.getBackInitScore());
        } else {
            entity.setTestHandleId(oldDemand.getTestHandleId());
            entity.setTestEstimateStartTime(oldDemand.getTestEstimateStartTime());
            entity.setTestEstimateEndTime(oldDemand.getTestEstimateEndTime());
            entity.setTestActualStartTime(oldDemand.getTestActualStartTime());
            entity.setTestActualEndTime(oldDemand.getTestActualEndTime());
            entity.setTestState(oldDemand.getTestState());
            entity.setTestRatio(oldDemand.getTestRatio());
            entity.setTestInitScore(oldDemand.getTestInitScore());
        }
    }

    private void keepActualTimeIfBlank(AutoDemand oldDemand, AutoDemand entity) {
        if (StrUtil.isEmpty(entity.getFrontActualStartTime())) {
            entity.setFrontActualStartTime(oldDemand.getFrontActualStartTime());
        }
        if (StrUtil.isEmpty(entity.getFrontActualEndTime())) {
            entity.setFrontActualEndTime(oldDemand.getFrontActualEndTime());
        }
        if (StrUtil.isEmpty(entity.getBackActualStartTime())) {
            entity.setBackActualStartTime(oldDemand.getBackActualStartTime());
        }
        if (StrUtil.isEmpty(entity.getBackActualEndTime())) {
            entity.setBackActualEndTime(oldDemand.getBackActualEndTime());
        }
        if (StrUtil.isEmpty(entity.getTestActualStartTime())) {
            entity.setTestActualStartTime(oldDemand.getTestActualStartTime());
        }
        if (StrUtil.isEmpty(entity.getTestActualEndTime())) {
            entity.setTestActualEndTime(oldDemand.getTestActualEndTime());
        }
    }

    @Override
    public void validatorEntity(AutoDemand entity) {
        super.validatorEntity(entity);
        AutoDemand oldDemand = StrUtil.isEmpty(entity.getId()) ? null : getById(entity.getId());
        applyProjectConfigConstraints(entity, oldDemand);
        int frontRatio = NumberParseUtil.parseInt(entity.getFrontRatio(), 0, 0, 100);
        int backRatio = NumberParseUtil.parseInt(entity.getBackRatio(), 0, 0, 100);
        int testRatio = NumberParseUtil.parseInt(entity.getTestRatio(), 0, 0, 100);
        entity.setFrontRatio(frontRatio);
        entity.setBackRatio(backRatio);
        entity.setTestRatio(testRatio);
        if (frontRatio + backRatio + testRatio > 100) {
            throw new CustomException("前端、后端、测试积分比例之和不能大于100。");
        }
        String totalScore = nvlScore(entity.getTotalScore());
        if (CalculationUtil.compareTo(totalScore, "0", 2, RoundingMode.HALF_UP) < 0) {
            throw new CustomException("总积分不能小于0。");
        }
        // 设置初始积分，已完成角色保持原积分
        fillInitScoreByRatio(entity, oldDemand);
        // 校验预计开始时间和结束时间
        validateEstimateTime(entity.getFrontEstimateStartTime(), entity.getFrontEstimateEndTime(), "前端");
        validateEstimateTime(entity.getBackEstimateStartTime(), entity.getBackEstimateEndTime(), "后端");
        validateEstimateTime(entity.getTestEstimateStartTime(), entity.getTestEstimateEndTime(), "测试");
    }

    /**
     * 按项目功能配置约束积分分配与预计时间字段。
     */
    private void applyProjectConfigConstraints(AutoDemand entity, AutoDemand oldDemand) {
        String objectId = StrUtil.blankToDefault(entity.getObjectId(),
            oldDemand == null ? "" : oldDemand.getObjectId());
        boolean scoreEnabled = autoProjectConfigService.isScoreAllocateEnabled(objectId);
        boolean estimateEnabled = autoProjectConfigService.isEstimateTimeEnabled(objectId);
        if (!scoreEnabled) {
            if (oldDemand != null) {
                entity.setTotalScore(nvlScore(oldDemand.getTotalScore()));
                entity.setFrontRatio(oldDemand.getFrontRatio());
                entity.setBackRatio(oldDemand.getBackRatio());
                entity.setTestRatio(oldDemand.getTestRatio());
                entity.setFrontInitScore(nvlScore(oldDemand.getFrontInitScore()));
                entity.setBackInitScore(nvlScore(oldDemand.getBackInitScore()));
                entity.setTestInitScore(nvlScore(oldDemand.getTestInitScore()));
                entity.setTestJoinAnalysis(oldDemand.getTestJoinAnalysis());
            } else {
                entity.setTotalScore("0");
                entity.setFrontRatio(0);
                entity.setBackRatio(0);
                entity.setTestRatio(0);
                entity.setFrontInitScore("0");
                entity.setBackInitScore("0");
                entity.setTestInitScore("0");
            }
        }
        if (!estimateEnabled) {
            if (oldDemand != null) {
                entity.setFrontEstimateStartTime(oldDemand.getFrontEstimateStartTime());
                entity.setFrontEstimateEndTime(oldDemand.getFrontEstimateEndTime());
                entity.setBackEstimateStartTime(oldDemand.getBackEstimateStartTime());
                entity.setBackEstimateEndTime(oldDemand.getBackEstimateEndTime());
                entity.setTestEstimateStartTime(oldDemand.getTestEstimateStartTime());
                entity.setTestEstimateEndTime(oldDemand.getTestEstimateEndTime());
            } else {
                entity.setFrontEstimateStartTime(null);
                entity.setFrontEstimateEndTime(null);
                entity.setBackEstimateStartTime(null);
                entity.setBackEstimateEndTime(null);
                entity.setTestEstimateStartTime(null);
                entity.setTestEstimateEndTime(null);
            }
        }
    }

    private void fillInitScoreByRatio(AutoDemand entity, AutoDemand oldDemand) {
        String total = nvlScore(entity.getTotalScore());
        if (oldDemand != null && isRoleFinish(oldDemand.getFrontState())) {
            entity.setFrontInitScore(nvlScore(oldDemand.getFrontInitScore()));
        } else {
            entity.setFrontInitScore(calcRoleInit(total, entity.getFrontRatio()));
        }
        if (oldDemand != null && isRoleFinish(oldDemand.getBackState())) {
            entity.setBackInitScore(nvlScore(oldDemand.getBackInitScore()));
        } else {
            entity.setBackInitScore(calcRoleInit(total, entity.getBackRatio()));
        }
        if (oldDemand != null && isRoleFinish(oldDemand.getTestState())) {
            entity.setTestInitScore(nvlScore(oldDemand.getTestInitScore()));
        } else {
            entity.setTestInitScore(calcRoleInit(total, entity.getTestRatio()));
        }
    }

    private String calcRoleInit(String total, Integer ratio) {
        String ratioStr = String.valueOf(NumberParseUtil.parseInt(ratio, 0, 0, 100));
        return CalculationUtil.divide(CalculationUtil.multiply(total, ratioStr, 4), "100", 2, RoundingMode.DOWN);
    }

    private String nvlScore(Object value) {
        String str = NumberParseUtil.toStr(value);
        if (StrUtil.isBlank(str) || "null".equalsIgnoreCase(str)) {
            return "0";
        }
        try {
            CalculationUtil.compareTo(str, "0", 2, RoundingMode.HALF_UP);
            return str;
        } catch (Exception e) {
            throw new CustomException("积分格式不正确。");
        }
    }

    private void validateEstimateTime(String startTime, String endTime, String roleName) {
        if (StrUtil.isNotEmpty(startTime) && StrUtil.isNotEmpty(endTime) && startTime.compareTo(endTime) > 0) {
            throw new CustomException(roleName + "预计开始时间不能晚于预计结束时间。");
        }
    }

    private void fillUnallocatedScore(AutoDemand entity) {
        if (entity == null) {
            return;
        }
        String allocated = CalculationUtil.add(2, nvlScore(entity.getFrontInitScore()),
            nvlScore(entity.getBackInitScore()), nvlScore(entity.getTestInitScore()));
        entity.setUnallocatedScore(CalculationUtil.subtract(nvlScore(entity.getTotalScore()), allocated, 2));
    }


    @Override
    public void deletePreExecution(AutoDemand autoDemand) {
        String state = autoDemand.getState();
        if (state.equals(AutoDemandStateEnum.INVALID.getKey()) || state.equals(AutoDemandStateEnum.FINISH.getKey())) {
            throw new CustomException("已完成或已作废，不可删除");
        }
    }

    @Override
    protected QueryWrapper<AutoDemand> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<AutoDemand> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoDemand::getObjectId), commonPageInfo.getObjectId());
        if (StrUtil.isNotEmpty(commonPageInfo.getCustomParamsMapStr("moduleId"))) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(AutoDemand::getModuleId), commonPageInfo.getCustomParamsMapStr("moduleId"));
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getCustomParamsMapStr("versionId"))) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(AutoDemand::getVersionId), commonPageInfo.getCustomParamsMapStr("versionId"));
        }
        // 按团队成员筛选：前端/后端/测试任一负责人为该成员
        String memberId = commonPageInfo.getCustomParamsMapStr("memberId");
        if (StrUtil.isNotEmpty(memberId)) {
            queryWrapper.and(wrapper -> wrapper
                .eq(MybatisPlusUtil.toColumns(AutoDemand::getFrontHandleId), memberId)
                .or().eq(MybatisPlusUtil.toColumns(AutoDemand::getBackHandleId), memberId)
                .or().eq(MybatisPlusUtil.toColumns(AutoDemand::getTestHandleId), memberId));
        }
        applyListTypeFilter(queryWrapper, commonPageInfo.getType());
        return queryWrapper;
    }

    private void applyListTypeFilter(QueryWrapper<AutoDemand> queryWrapper, String type) {
        if (StrUtil.isEmpty(type)) {
            type = "unFinish";
        }
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        queryWrapper.ne(MybatisPlusUtil.toColumns(AutoDemand::getState), AutoDemandStateEnum.INVALID.getKey());
        if (StrUtil.equals(type, "myWaitDev")) {
            queryWrapper.and(wrapper -> wrapper
                .and(item -> item.eq(MybatisPlusUtil.toColumns(AutoDemand::getFrontHandleId), userId)
                    .and(st -> st.isNull(MybatisPlusUtil.toColumns(AutoDemand::getFrontState))
                        .or().ne(MybatisPlusUtil.toColumns(AutoDemand::getFrontState), AutoDemandRoleStateEnum.FINISH.getKey()))
                    .in(MybatisPlusUtil.toColumns(AutoDemand::getState),
                        Arrays.asList(AutoDemandStateEnum.WAIT_RESEARCH.getKey(), AutoDemandStateEnum.RESEARCH.getKey())))
                .or(item -> item.eq(MybatisPlusUtil.toColumns(AutoDemand::getBackHandleId), userId)
                    .and(st -> st.isNull(MybatisPlusUtil.toColumns(AutoDemand::getBackState))
                        .or().ne(MybatisPlusUtil.toColumns(AutoDemand::getBackState), AutoDemandRoleStateEnum.FINISH.getKey()))
                    .in(MybatisPlusUtil.toColumns(AutoDemand::getState),
                        Arrays.asList(AutoDemandStateEnum.WAIT_RESEARCH.getKey(), AutoDemandStateEnum.RESEARCH.getKey()))));
        } else if (StrUtil.equals(type, "myWaitTest")) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(AutoDemand::getTestHandleId), userId);
            queryWrapper.and(st -> st.isNull(MybatisPlusUtil.toColumns(AutoDemand::getTestState))
                .or().ne(MybatisPlusUtil.toColumns(AutoDemand::getTestState), AutoDemandRoleStateEnum.FINISH.getKey()));
            queryWrapper.eq(MybatisPlusUtil.toColumns(AutoDemand::getState), AutoDemandStateEnum.WAIT_TEST.getKey());
        } else if (StrUtil.equals(type, "myFinish")) {
            queryWrapper.and(wrapper -> wrapper
                .and(item -> item.eq(MybatisPlusUtil.toColumns(AutoDemand::getFrontHandleId), userId)
                    .eq(MybatisPlusUtil.toColumns(AutoDemand::getFrontState), AutoDemandRoleStateEnum.FINISH.getKey()))
                .or(item -> item.eq(MybatisPlusUtil.toColumns(AutoDemand::getBackHandleId), userId)
                    .eq(MybatisPlusUtil.toColumns(AutoDemand::getBackState), AutoDemandRoleStateEnum.FINISH.getKey()))
                .or(item -> item.eq(MybatisPlusUtil.toColumns(AutoDemand::getTestHandleId), userId)
                    .eq(MybatisPlusUtil.toColumns(AutoDemand::getTestState), AutoDemandRoleStateEnum.FINISH.getKey())));
        } else if (StrUtil.equals(type, "unFinish")) {
            queryWrapper.in(MybatisPlusUtil.toColumns(AutoDemand::getState),
                Arrays.asList(AutoDemandStateEnum.WAIT_RESEARCH.getKey(), AutoDemandStateEnum.RESEARCH.getKey(),
                    AutoDemandStateEnum.WAIT_TEST.getKey()));
        } else if (StrUtil.equals(type, "finish")) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(AutoDemand::getState), AutoDemandStateEnum.FINISH.getKey());
        } else if (StrUtil.equals(type, "all")) {
            // 全部有效需求
        } else if (StrUtil.equals(type, "mine")) {
            queryWrapper.and(wrapper -> wrapper
                .eq(MybatisPlusUtil.toColumns(AutoDemand::getFrontHandleId), userId)
                .or().eq(MybatisPlusUtil.toColumns(AutoDemand::getBackHandleId), userId)
                .or().eq(MybatisPlusUtil.toColumns(AutoDemand::getTestHandleId), userId));
        }
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        autoVersionService.setMationForMap(beans, "versionId", "versionMation");
        autoModuleService.setMationForMap(beans, "moduleId", "moduleMation");
        iAuthUserService.setMationForMap(beans, "frontHandleId", "frontHandleMation");
        iAuthUserService.setMationForMap(beans, "backHandleId", "backHandleMation");
        iAuthUserService.setMationForMap(beans, "testHandleId", "testHandleMation");
        beans.forEach(bean -> {
            String allocated = CalculationUtil.add(2, nvlScore(bean.get("frontInitScore")),
                nvlScore(bean.get("backInitScore")), nvlScore(bean.get("testInitScore")));
            bean.put("unallocatedScore", CalculationUtil.subtract(nvlScore(bean.get("totalScore")), allocated, 2));
        });
        return beans;
    }

    @Override
    public AutoDemand selectById(String id) {
        AutoDemand autoDemand = super.selectById(id);
        autoVersionService.setDataMation(autoDemand, AutoDemand::getVersionId);
        autoModuleService.setDataMation(autoDemand, AutoDemand::getModuleId);
        iAuthUserService.setDataMation(autoDemand, AutoDemand::getFrontHandleId);
        iAuthUserService.setDataMation(autoDemand, AutoDemand::getBackHandleId);
        iAuthUserService.setDataMation(autoDemand, AutoDemand::getTestHandleId);
        fillUnallocatedScore(autoDemand);
        return autoDemand;
    }


    @Override
    public void updateStateAutoDemandById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = String.valueOf(params.get("id"));
        String roleKey = NumberParseUtil.toStr(params.get("roleKey"));
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        AutoDemand autoDemand = this.selectById(id);
        if (autoDemand == null) {
            throw new CustomException("需求不存在。");
        }
        String demandState = autoDemand.getState();
        if (StrUtil.equals(demandState, AutoDemandStateEnum.INVALID.getKey())
            || StrUtil.equals(demandState, AutoDemandStateEnum.FINISH.getKey())) {
            throw new CustomException("已完成或已作废，不可修改");
        }
        String roleName = roleName(roleKey);
        String handleId = getRoleHandleId(autoDemand, roleKey);
        String roleState = getRoleState(autoDemand, roleKey);
        if (StrUtil.isEmpty(handleId)) {
            throw new CustomException("未指定" + roleName + "负责人，不能推进状态。");
        }
        if (!StrUtil.equals(handleId, userId)) {
            throw new CustomException("仅" + roleName + "负责人可推进自己的状态。");
        }
        if (isRoleFinish(roleState)) {
            throw new CustomException(roleName + "已完成，不可再修改。");
        }
        if ("test".equals(roleKey)) {
            if (!StrUtil.equals(demandState, AutoDemandStateEnum.WAIT_TEST.getKey())) {
                throw new CustomException("请等待前端、后端完成后再开始测试。");
            }
        } else if (!StrUtil.equals(demandState, AutoDemandStateEnum.WAIT_RESEARCH.getKey())
            && !StrUtil.equals(demandState, AutoDemandStateEnum.RESEARCH.getKey())) {
            throw new CustomException("研发已结束，不能再推进" + roleName + "状态。");
        }
        String now = DateUtil.getTimeAndToString();
        if (isRoleWait(roleState)) {
            setRoleState(autoDemand, roleKey, AutoDemandRoleStateEnum.PROGRESS.getKey());
            setRoleActualStartTime(autoDemand, roleKey, now);
        } else if (isRoleProgress(roleState)) {
            setRoleState(autoDemand, roleKey, AutoDemandRoleStateEnum.FINISH.getKey());
            setRoleActualEndTime(autoDemand, roleKey, now);
        } else {
            throw new CustomException(roleName + "状态不正确。");
        }
        syncDemandStateFromRoles(autoDemand);
        autoDemandService.updateEntity(autoDemand, userId);
        this.refreshCache(id);
        outputObject.setBean(selectById(id));
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void invalidAutoDemandById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        AutoDemand autoDemand = this.selectById(id);
        String state = autoDemand.getState();
        if (state.equals(AutoDemandStateEnum.INVALID.getKey()) || state.equals(AutoDemandStateEnum.FINISH.getKey())) {
            throw new CustomException("已完成或已作废，不可修改");
        } else if (state.equals(AutoDemandStateEnum.WAIT_RESEARCH.getKey()) || state.equals(AutoDemandStateEnum.RESEARCH.getKey()) || state.equals(AutoDemandStateEnum.WAIT_TEST.getKey())) {
            autoDemand.setState(AutoDemandStateEnum.INVALID.getKey());
        } else {
            throw new CustomException("false");
        }
        autoDemandService.updateEntity(autoDemand, userId);
        this.refreshCache(id);
        outputObject.setBean(autoDemand);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void updateAutoDemandEstimateTime(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = String.valueOf(params.get("id"));
        String roleKey = NumberParseUtil.toStr(params.get("roleKey"));
        String startTime = NumberParseUtil.toStr(params.get("startTime"));
        String endTime = NumberParseUtil.toStr(params.get("endTime"));
        AutoDemand autoDemand = getById(id);
        if (autoDemand == null) {
            throw new CustomException("需求不存在。");
        }
        if (!autoProjectConfigService.isEstimateTimeEnabled(autoDemand.getObjectId())) {
            throw new CustomException("当前项目未开启需求预计时间设置。");
        }
        if (StrUtil.equals(autoDemand.getState(), AutoDemandStateEnum.INVALID.getKey())
            || StrUtil.equals(autoDemand.getState(), AutoDemandStateEnum.FINISH.getKey())) {
            throw new CustomException("已完成或已作废，不可修改预计时间。");
        }
        String roleName = roleName(roleKey);
        if (StrUtil.isEmpty(getRoleHandleId(autoDemand, roleKey))) {
            throw new CustomException("未指定" + roleName + "负责人，不能设置预计时间。");
        }
        if (isRoleFinish(getRoleState(autoDemand, roleKey))) {
            throw new CustomException(roleName + "已完成，不可再修改预计时间。");
        }
        if (StrUtil.isBlank(startTime) || StrUtil.isBlank(endTime) || "null".equalsIgnoreCase(startTime) || "null".equalsIgnoreCase(endTime)) {
            throw new CustomException("请设置预计开始时间和结束时间。");
        }
        validateEstimateTime(startTime, endTime, roleName);
        UpdateWrapper<AutoDemand> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        if ("front".equals(roleKey)) {
            updateWrapper.set(MybatisPlusUtil.toColumns(AutoDemand::getFrontEstimateStartTime), startTime);
            updateWrapper.set(MybatisPlusUtil.toColumns(AutoDemand::getFrontEstimateEndTime), endTime);
        } else if ("back".equals(roleKey)) {
            updateWrapper.set(MybatisPlusUtil.toColumns(AutoDemand::getBackEstimateStartTime), startTime);
            updateWrapper.set(MybatisPlusUtil.toColumns(AutoDemand::getBackEstimateEndTime), endTime);
        } else {
            updateWrapper.set(MybatisPlusUtil.toColumns(AutoDemand::getTestEstimateStartTime), startTime);
            updateWrapper.set(MybatisPlusUtil.toColumns(AutoDemand::getTestEstimateEndTime), endTime);
        }
        update(updateWrapper);
        this.refreshCache(id);
        outputObject.setBean(selectById(id));
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void aiGenerateDemandDraft(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        Map<String, Object> bean = autoDemandAiDraftService.generate(params);
        outputObject.setBean(bean);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void aiParseDemandDraft(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> bean = autoDemandAiDraftService.parseAnswer(inputObject.getParams());
        outputObject.setBean(bean);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void aiGenerateCaseDraft(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> bean = autoDemandCaseAiDraftService.generate(inputObject.getParams());
        outputObject.setBean(bean);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void aiParseCaseDraft(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> bean = autoDemandCaseAiDraftService.parseAnswer(inputObject.getParams());
        outputObject.setBean(bean);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    private void syncDemandStateFromRoles(AutoDemand demand) {
        boolean frontDone = isRoleDone(demand.getFrontHandleId(), demand.getFrontState());
        boolean backDone = isRoleDone(demand.getBackHandleId(), demand.getBackState());
        boolean testDone = isRoleDone(demand.getTestHandleId(), demand.getTestState());
        boolean frontStarted = isRoleStarted(demand.getFrontHandleId(), demand.getFrontState());
        boolean backStarted = isRoleStarted(demand.getBackHandleId(), demand.getBackState());
        if (frontDone && backDone && testDone) {
            demand.setState(AutoDemandStateEnum.FINISH.getKey());
        } else if (frontDone && backDone) {
            demand.setState(AutoDemandStateEnum.WAIT_TEST.getKey());
        } else if (frontStarted || backStarted) {
            demand.setState(AutoDemandStateEnum.RESEARCH.getKey());
        } else {
            demand.setState(AutoDemandStateEnum.WAIT_RESEARCH.getKey());
        }
    }

    private boolean isRoleDone(String handleId, String roleState) {
        return StrUtil.isEmpty(handleId) || isRoleFinish(roleState);
    }

    private boolean isRoleStarted(String handleId, String roleState) {
        return StrUtil.isNotEmpty(handleId) && (isRoleProgress(roleState) || isRoleFinish(roleState));
    }

    private boolean isRoleWait(String roleState) {
        return StrUtil.isEmpty(roleState) || StrUtil.equals(roleState, AutoDemandRoleStateEnum.WAIT.getKey());
    }

    private boolean isRoleProgress(String roleState) {
        return StrUtil.equals(roleState, AutoDemandRoleStateEnum.PROGRESS.getKey());
    }

    private boolean isRoleFinish(String roleState) {
        return StrUtil.equals(roleState, AutoDemandRoleStateEnum.FINISH.getKey());
    }

    private String roleName(String roleKey) {
        if ("front".equals(roleKey)) {
            return "前端";
        }
        if ("back".equals(roleKey)) {
            return "后端";
        }
        if ("test".equals(roleKey)) {
            return "测试";
        }
        throw new CustomException("角色不正确。");
    }

    private String getRoleHandleId(AutoDemand demand, String roleKey) {
        roleName(roleKey);
        if ("front".equals(roleKey)) {
            return demand.getFrontHandleId();
        }
        if ("back".equals(roleKey)) {
            return demand.getBackHandleId();
        }
        return demand.getTestHandleId();
    }

    private String getRoleState(AutoDemand demand, String roleKey) {
        roleName(roleKey);
        if ("front".equals(roleKey)) {
            return demand.getFrontState();
        }
        if ("back".equals(roleKey)) {
            return demand.getBackState();
        }
        return demand.getTestState();
    }

    private void setRoleState(AutoDemand demand, String roleKey, String state) {
        if ("front".equals(roleKey)) {
            demand.setFrontState(state);
        } else if ("back".equals(roleKey)) {
            demand.setBackState(state);
        } else {
            demand.setTestState(state);
        }
    }

    private void setRoleActualStartTime(AutoDemand demand, String roleKey, String time) {
        if ("front".equals(roleKey)) {
            demand.setFrontActualStartTime(time);
        } else if ("back".equals(roleKey)) {
            demand.setBackActualStartTime(time);
        } else {
            demand.setTestActualStartTime(time);
        }
    }

    private void setRoleActualEndTime(AutoDemand demand, String roleKey, String time) {
        if ("front".equals(roleKey)) {
            demand.setFrontActualEndTime(time);
        } else if ("back".equals(roleKey)) {
            demand.setBackActualEndTime(time);
        } else {
            demand.setTestActualEndTime(time);
        }
    }

}
