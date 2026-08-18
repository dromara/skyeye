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
import com.skyeye.common.util.NumberParseUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.demand.classenum.AutoDemandAuthEnum;
import com.skyeye.demand.classenum.AutoDemandStateEnum;
import com.skyeye.demand.dao.AutoDemandDao;
import com.skyeye.demand.entity.AutoDemand;
import com.skyeye.demand.service.AutoDemandService;
import com.skyeye.exception.CustomException;
import com.skyeye.module.service.AutoModuleService;
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
    }

    @Override
    protected void writePostpose(AutoDemand entity, String userId) {
        super.writePostpose(entity, userId);
        autoScoreRecordService.grantDemandScoreByState(entity, userId);
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
    }

    @Override
    public void validatorEntity(AutoDemand entity) {
        super.validatorEntity(entity);
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
        // 设置初始积分
        fillInitScoreByRatio(entity);
        // 校验预计开始时间和结束时间
        validateEstimateTime(entity.getFrontEstimateStartTime(), entity.getFrontEstimateEndTime(), "前端");
        validateEstimateTime(entity.getBackEstimateStartTime(), entity.getBackEstimateEndTime(), "后端");
        validateEstimateTime(entity.getTestEstimateStartTime(), entity.getTestEstimateEndTime(), "测试");
    }

    private void fillInitScoreByRatio(AutoDemand entity) {
        String total = nvlScore(entity.getTotalScore());
        entity.setFrontInitScore(calcRoleInit(total, entity.getFrontRatio()));
        entity.setBackInitScore(calcRoleInit(total, entity.getBackRatio()));
        entity.setTestInitScore(calcRoleInit(total, entity.getTestRatio()));
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
                .eq(MybatisPlusUtil.toColumns(AutoDemand::getFrontHandleId), userId)
                .or()
                .eq(MybatisPlusUtil.toColumns(AutoDemand::getBackHandleId), userId));
            queryWrapper.in(MybatisPlusUtil.toColumns(AutoDemand::getState),
                Arrays.asList(AutoDemandStateEnum.WAIT_RESEARCH.getKey(), AutoDemandStateEnum.RESEARCH.getKey()));
        } else if (StrUtil.equals(type, "myWaitTest")) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(AutoDemand::getTestHandleId), userId);
            queryWrapper.eq(MybatisPlusUtil.toColumns(AutoDemand::getState), AutoDemandStateEnum.WAIT_TEST.getKey());
        } else if (StrUtil.equals(type, "myFinish")) {
            queryWrapper.and(wrapper -> wrapper
                .and(item -> item.eq(MybatisPlusUtil.toColumns(AutoDemand::getFrontHandleId), userId)
                    .in(MybatisPlusUtil.toColumns(AutoDemand::getState),
                        Arrays.asList(AutoDemandStateEnum.WAIT_TEST.getKey(), AutoDemandStateEnum.FINISH.getKey())))
                .or(item -> item.eq(MybatisPlusUtil.toColumns(AutoDemand::getBackHandleId), userId)
                    .in(MybatisPlusUtil.toColumns(AutoDemand::getState),
                        Arrays.asList(AutoDemandStateEnum.WAIT_TEST.getKey(), AutoDemandStateEnum.FINISH.getKey())))
                .or(item -> item.eq(MybatisPlusUtil.toColumns(AutoDemand::getTestHandleId), userId)
                    .eq(MybatisPlusUtil.toColumns(AutoDemand::getState), AutoDemandStateEnum.FINISH.getKey())));
        } else if (StrUtil.equals(type, "unFinish")) {
            queryWrapper.in(MybatisPlusUtil.toColumns(AutoDemand::getState),
                Arrays.asList(AutoDemandStateEnum.WAIT_RESEARCH.getKey(), AutoDemandStateEnum.RESEARCH.getKey(),
                    AutoDemandStateEnum.WAIT_TEST.getKey()));
        } else if (StrUtil.equals(type, "finish")) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(AutoDemand::getState), AutoDemandStateEnum.FINISH.getKey());
        } else if (StrUtil.equals(type, "all")) {
            // 全部有效需求
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
        String id = inputObject.getParams().get("id").toString();
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        AutoDemand autoDemand = this.selectById(id);
        String state = autoDemand.getState();
        if (state.equals(AutoDemandStateEnum.INVALID.getKey()) || state.equals(AutoDemandStateEnum.FINISH.getKey())) {
            throw new CustomException("已完成或已作废，不可修改");
        } else if (state.equals(AutoDemandStateEnum.WAIT_RESEARCH.getKey())) {
            autoDemand.setState(AutoDemandStateEnum.RESEARCH.getKey());
        } else if (state.equals(AutoDemandStateEnum.RESEARCH.getKey())) {
            autoDemand.setState(AutoDemandStateEnum.WAIT_TEST.getKey());
        } else if (state.equals(AutoDemandStateEnum.WAIT_TEST.getKey())) {
            autoDemand.setState(AutoDemandStateEnum.FINISH.getKey());
        } else {
            throw new CustomException("false");
        }
        autoDemandService.updateEntity(autoDemand, userId);
        this.refreshCache(id);
        outputObject.setBean(autoDemand);
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
        if (StrUtil.equals(autoDemand.getState(), AutoDemandStateEnum.INVALID.getKey())) {
            throw new CustomException("已作废，不可修改预计时间。");
        }
        String roleName;
        if ("front".equals(roleKey)) {
            roleName = "前端";
            if (StrUtil.isEmpty(autoDemand.getFrontHandleId())) {
                throw new CustomException("未指定前端负责人，不能设置预计时间。");
            }
        } else if ("back".equals(roleKey)) {
            roleName = "后端";
            if (StrUtil.isEmpty(autoDemand.getBackHandleId())) {
                throw new CustomException("未指定后端负责人，不能设置预计时间。");
            }
        } else if ("test".equals(roleKey)) {
            roleName = "测试";
            if (StrUtil.isEmpty(autoDemand.getTestHandleId())) {
                throw new CustomException("未指定测试负责人，不能设置预计时间。");
            }
        } else {
            throw new CustomException("角色不正确。");
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

}
