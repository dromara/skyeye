/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.usercase.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.jayway.jsonpath.JsonPath;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.attr.classenum.AttrSymbols;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.MqConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.eve.rest.mq.JobMateMation;
import com.skyeye.eve.service.IJobMateMationService;
import com.skyeye.exception.CustomException;
import com.skyeye.history.classenum.AutoHistoryCaseExecuteResult;
import com.skyeye.history.entity.AutoHistoryCase;
import com.skyeye.history.entity.AutoHistoryStep;
import com.skyeye.history.entity.AutoHistoryStepAssert;
import com.skyeye.history.entity.AutoHistoryStepCase;
import com.skyeye.history.service.AutoHistoryCaseService;
import com.skyeye.module.service.AutoModuleService;
import com.skyeye.usercase.classenum.AutoStepTypeEnum;
import com.skyeye.usercase.classenum.AutoValueFromTypeEnum;
import com.skyeye.usercase.dao.AutoCaseDao;
import com.skyeye.usercase.entity.*;
import com.skyeye.usercase.service.AutoCaseService;
import com.skyeye.usercase.service.AutoStepApiService;
import com.skyeye.usercase.service.AutoStepDatabaseService;
import com.skyeye.usercase.service.AutoStepService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.*;

/**
 * @ClassName: AutoCaseServiceImpl
 * @Description: 用例管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/5/16 23:20
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye-report Inc. All rights reserved.
 * 注意：本内容具体规则请参照readme执行，地址：https://gitee.com/doc_wei01/skyeye-report/blob/master/README.md
 */
@Service
@SkyeyeService(name = "用例管理", groupName = "用例管理")
public class AutoCaseServiceImpl extends SkyeyeBusinessServiceImpl<AutoCaseDao, AutoCase> implements AutoCaseService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AutoStepService autoStepService;

    @Autowired
    private AutoStepApiService autoStepApiService;

    @Autowired
    private AutoStepDatabaseService autoStepDatabaseService;

    @Autowired
    private AutoHistoryCaseService autoHistoryCaseService;

    @Autowired
    private AutoModuleService autoModuleService;

    @Autowired
    private IJobMateMationService iJobMateMationService;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        AutoUserCaseQueryDo pageInfo = inputObject.getParams(AutoUserCaseQueryDo.class);
        if (tenantEnable) {
            pageInfo.setTenantId(TenantContext.getTenantId());
        }
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryAutoCaseList(pageInfo);
        autoModuleService.setMationForMap(beans, "moduleId", "moduleMation");
        return beans;
    }

    @Override
    public void writePostpose(AutoCase autoCase, String userId) {
        List<AutoStep> autoStepList = autoCase.getStepList();
        autoStepList.forEach(autoStep -> {
            autoStep.setCaseId(autoCase.getId());
        });
        autoStepService.deleteByObjectId(autoCase.getId());
        autoStepService.createEntity(autoCase.getStepList(), userId);
        // 步骤落库后覆盖 Redis，避免 queryAutoCaseById 仍命中旧缓存（含 stepList）
        putCache(getDataFromDb(autoCase.getId()));
    }

    @Override
    public AutoCase getDataFromDb(String id) {
        AutoCase autoCase = super.getDataFromDb(id);
        autoCase.setStepList(autoStepService.queryAutoStepListByCaseId(id));
        return autoCase;
    }

    @Override
    public void deletePostpose(String objectId) {
        autoStepService.deleteByObjectId(objectId);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void executeCase(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        executeCase(id, true);
    }

    @Override
    public void executeCase(String id, Boolean recordData) {
        AutoCase autoCase = selectById(id);
        executeCase(autoCase, recordData);
    }

    @Override
    public Object executeCase(AutoCase autoCase, Boolean recordData) {
        if (autoHistoryCaseService.checkUserCaseRuning(autoCase.getId())) {
            throw new CustomException("存在执行中的用例，请稍后执行");
        }
        AutoHistoryCase autoHistoryCase = new AutoHistoryCase();
        if (recordData) {
            // 需要记录执行历史信息
            autoHistoryCase.setName(autoCase.getName());
            autoHistoryCase.setModuleId(autoCase.getModuleId());
            autoHistoryCase.setResultKey(autoCase.getResultKey());
            autoHistoryCase.setCaseId(autoCase.getId());
            autoHistoryCaseService.createEntity(autoHistoryCase, StrUtil.EMPTY);
        }
        if (!recordData) {
            // 执行子用例是需要返回
            return updateHistoryCase(autoCase, recordData, autoHistoryCase);
        } else {
            // 发送消息
            Map<String, Object> userCaseMsg = new HashMap<>();
            userCaseMsg.put("autoHistoryCaseStr", JSONUtil.toJsonStr(autoHistoryCase));
            userCaseMsg.put("autoCaseStr", JSONUtil.toJsonStr(autoCase));
            userCaseMsg.put("type", MqConstants.JobMateMationJobType.USERCASE_EXECUTE_SERVICE.getJobType());
            JobMateMation jobMateMation = new JobMateMation();
            jobMateMation.setJsonStr(JSONUtil.toJsonStr(userCaseMsg));
            jobMateMation.setUserId(autoCase.getCreateId());
            iJobMateMationService.sendMQProducer(jobMateMation);
        }
        return null;
    }

    @Override
    public Object updateHistoryCase(AutoCase autoCase, Boolean recordData, AutoHistoryCase autoHistoryCase) {
        Integer executeResult = AutoHistoryCaseExecuteResult.EXECUTION_SUCCESSFUL.getKey();
        // 用例执行步骤
        List<AutoHistoryStep> autoHistoryStepList = new ArrayList<>();
        Object object = null;
        try {
            // 结果集对象
            Map<String, Object> result = new HashMap<>();
            for (AutoStep autoStep : autoCase.getStepList()) {
                AutoHistoryStep autoHistoryStep = new AutoHistoryStep();
                autoHistoryStep.setName(autoStep.getName());
                autoHistoryStep.setOrderBy(autoStep.getOrderBy());
                autoHistoryStep.setResultKey(autoStep.getResultKey());
                autoHistoryStep.setType(autoStep.getType());
                autoHistoryStep.setExecuteResult(AutoHistoryCaseExecuteResult.EXECUTION_SUCCESSFUL.getKey());
                // 获取前置条件(入参)
                Map<String, Object> inputParams = getInputParams(result, autoStep.getStepInputList());
                try {
                    // 执行
                    if (autoStep.getType() == AutoStepTypeEnum.STEP.getKey()) {
                        autoStepApiService.executeStepApi(autoStep, result, inputParams, autoHistoryStep);
                    } else if (autoStep.getType() == AutoStepTypeEnum.CASE.getKey()) {
                        if (ObjectUtil.isEmpty(autoStep.getStepCase()) || StrUtil.isEmpty(autoStep.getStepCase().getCaseId())) {
                            throw new CustomException("未绑定用例");
                        }
                        AutoCase childAutoCase = selectById(autoStep.getStepCase().getCaseId());
                        Object aCase = executeCase(childAutoCase, false);
                        result.put(autoStep.getResultKey(), aCase);
                        controctCaseMation(autoHistoryStep, result, autoStep);
                    } else if (autoStep.getType() == AutoStepTypeEnum.SCRIPT.getKey()) {

                    } else if (autoStep.getType() == AutoStepTypeEnum.TIMER.getKey()) {

                    } else if (autoStep.getType() == AutoStepTypeEnum.DATABASE.getKey()) {
                        autoStepDatabaseService.executeAtepDatabase(autoStep, result, inputParams, autoHistoryStep);
                    }
                } catch (Exception ex) {
                    autoHistoryStep.setExecuteResult(AutoHistoryCaseExecuteResult.EXECUTION_FAILED.getKey());
                    logger.warn("execute step:{} is Failed，Msg is: ", autoStep.getId(), ex);
                }
                autoHistoryStepList.add(autoHistoryStep);
                if (autoHistoryStep.getExecuteResult() == AutoHistoryCaseExecuteResult.EXECUTION_SUCCESSFUL.getKey()) {
                    // 如果前面的执行成功，则执行断言
                    List<AutoHistoryStepAssert> autoHistoryStepAssertList = new ArrayList<>();
                    Boolean executeAssertResult = executeAssert(autoStep.getStepAssertList(), autoHistoryStepAssertList, result);
                    autoHistoryStep.setAutoHistoryStepAssertList(autoHistoryStepAssertList);
                    if (!executeAssertResult) {
                        autoHistoryStep.setExecuteResult(AutoHistoryCaseExecuteResult.EXECUTION_FAILED.getKey());
                        throw new CustomException("断言执行失败");
                    }
                } else {
                    throw new CustomException("执行失败");
                }
                if (!recordData) {
                    // 执行子用例是需要返回
                    object = result.get(autoStep.getResultKey());
                }
            }
        } catch (Exception ee) {
            executeResult = AutoHistoryCaseExecuteResult.EXECUTION_FAILED.getKey();
            logger.warn("execute userCase:{} is Failed，Msg is: ", autoCase.getId(), ee);
        } finally {
            if (recordData) {
                // 修改执行历史信息
                autoHistoryCase.setStepList(autoHistoryStepList);
                autoHistoryCaseService.updateEntity(autoHistoryCase, StrUtil.EMPTY);
                autoHistoryCaseService.finishAutoCaseHistoryById(autoHistoryCase.getId(), executeResult);
            }
        }
        return object;
    }

    private void controctCaseMation(AutoHistoryStep autoHistoryStep, Map<String, Object> result, AutoStep autoStep) {
        AutoHistoryStepCase autoHistoryStepCase = new AutoHistoryStepCase();
        autoHistoryStepCase.setExecuteCaseId(autoStep.getStepCase().getLinkCaseId());
        autoHistoryStepCase.setExecuteResult(AutoHistoryCaseExecuteResult.EXECUTION_SUCCESSFUL.getKey());
        autoHistoryStepCase.setInputValue("");
        autoHistoryStepCase.setOutputValue(String.valueOf(result.get(autoStep.getResultKey())));
        autoHistoryStep.setAutoHistoryStepCase(autoHistoryStepCase);
    }

    /**
     * 根据步骤入参配置组装 inputParams。
     * valueFrom=自定义：直接使用 value 字面量；
     * valueFrom=表达式：用 JsonPath 从结果集 result 读取 $.value。
     * 返回的 Map 供 API 请求入参，或数据库 SQL 中 #{key} 替换。
     */
    private Map<String, Object> getInputParams(Map<String, Object> result, List<AutoStepInput> stepInputList) {
        Map<String, Object> inputParams = new HashMap<>();
        if (CollectionUtil.isEmpty(stepInputList)) {
            return inputParams;
        }

        Map<String, Object> hasValParams = new HashMap<>();
        String resultStr = JSONUtil.toJsonStr(result);
        stepInputList.forEach(autoStepAssert -> {
            if (autoStepAssert.getValueFrom() == AutoValueFromTypeEnum.EXPRESSION.getKey()) {
                String value = JsonPath.read(resultStr, String.format(Locale.ROOT, "$.%s", autoStepAssert.getValue()));
                hasValParams.put(autoStepAssert.getValue(), value);
            }
        });

        stepInputList.forEach(stepInput -> {
            if (stepInput.getValueFrom() == AutoValueFromTypeEnum.CUSTOMIZE.getKey()) {
                inputParams.put(stepInput.getKey(), stepInput.getValue());
            } else if (stepInput.getValueFrom() == AutoValueFromTypeEnum.EXPRESSION.getKey()) {
                inputParams.put(stepInput.getKey(), String.valueOf(hasValParams.get(stepInput.getValue())));
            }
        });
        return inputParams;
    }

    /**
     * 执行步骤断言。key 为实际值 JsonPath（相对 result）；valueFrom 决定期望值取字面量还是另一条路径。
     * 比较表达式：'{实际值}' {AttrSymbols.symbols} '{期望值}'，经 JavaScript 引擎求布尔值；任一条失败即短路返回 false。
     */
    private Boolean executeAssert(List<AutoStepAssert> stepAssertList, List<AutoHistoryStepAssert> autoHistoryStepAssertList, Map<String, Object> result) throws ScriptException {
        if (CollectionUtil.isEmpty(stepAssertList)) {
            return true;
        }

        // 获取需要通过表达式获取的值对象
        Map<String, Object> hasValParams = new HashMap<>();
        String resultStr = JSONUtil.toJsonStr(result);
        stepAssertList.forEach(autoStepAssert -> {
            Object value = JsonPath.read(resultStr, String.format(Locale.ROOT, "$.%s", autoStepAssert.getKey()));
            hasValParams.put(autoStepAssert.getKey(), value);
            if (autoStepAssert.getValueFrom() == AutoValueFromTypeEnum.EXPRESSION.getKey()) {
                value = JsonPath.read(resultStr, String.format(Locale.ROOT, "$.%s", autoStepAssert.getValue()));
                hasValParams.put(autoStepAssert.getValue(), value);
            }
        });

        for (AutoStepAssert autoStepAssert : stepAssertList) {
            AutoHistoryStepAssert autoHistoryStepAssert = new AutoHistoryStepAssert();
            autoHistoryStepAssert.setKey(autoStepAssert.getKey());
            autoHistoryStepAssert.setOperator(autoStepAssert.getOperator());
            autoHistoryStepAssert.setValueFrom(autoStepAssert.getValueFrom());
            if (autoStepAssert.getValueFrom() == AutoValueFromTypeEnum.CUSTOMIZE.getKey()) {
                autoHistoryStepAssert.setValue(autoStepAssert.getValue());
            } else if (autoStepAssert.getValueFrom() == AutoValueFromTypeEnum.EXPRESSION.getKey()) {
                autoHistoryStepAssert.setValue(String.valueOf(hasValParams.get(autoStepAssert.getValue())));
            }

            autoHistoryStepAssert.setRealValue(String.valueOf(hasValParams.get(autoStepAssert.getKey())));
            autoHistoryStepAssert.setOrderBy(autoStepAssert.getOrderBy());

            String regx = String.format(Locale.ROOT, "'%s' %s '%s'", autoHistoryStepAssert.getRealValue(), AttrSymbols.getSymbols(autoStepAssert.getOperator()), autoHistoryStepAssert.getValue());

            // 创建一个ScriptEngineManager实例
            ScriptEngineManager manager = new ScriptEngineManager();
            // 获取JavaScript引擎
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            Boolean approvalResult = (Boolean) engine.eval(regx);
            if (approvalResult) {
                autoHistoryStepAssert.setExecuteResult(AutoHistoryCaseExecuteResult.EXECUTION_SUCCESSFUL.getKey());
            } else {
                autoHistoryStepAssert.setExecuteResult(AutoHistoryCaseExecuteResult.EXECUTION_FAILED.getKey());
            }
            autoHistoryStepAssertList.add(autoHistoryStepAssert);
            if (autoHistoryStepAssert.getExecuteResult() == AutoHistoryCaseExecuteResult.EXECUTION_FAILED.getKey()) {
                return false;
            }
        }
        return true;
    }

}
