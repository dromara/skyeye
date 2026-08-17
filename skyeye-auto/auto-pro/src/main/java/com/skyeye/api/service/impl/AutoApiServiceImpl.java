/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.api.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.api.classenum.AutoApiAuthEnum;
import com.skyeye.api.dao.AutoApiDao;
import com.skyeye.api.entity.AutoApi;
import com.skyeye.api.service.AutoApiService;
import com.skyeye.base.business.service.impl.SkyeyeTeamAuthServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.HttpRequestUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.environment.service.AutoEnvironmentService;
import com.skyeye.exception.CustomException;
import com.skyeye.history.entity.AutoHistoryStepApi;
import com.skyeye.microservice.entity.AutoMicroservice;
import com.skyeye.microservice.service.AutoMicroserviceService;
import com.skyeye.module.service.AutoModuleService;
import com.skyeye.server.entity.AutoServer;
import com.skyeye.server.service.AutoServerService;
import com.skyeye.variable.classenum.AutoVariableType;
import com.skyeye.variable.service.AutoVariableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @ClassName: AutoApiServiceImpl
 * @Description: 接口管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/26 8:31
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "接口管理", groupName = "接口管理", teamAuth = true)
public class AutoApiServiceImpl extends SkyeyeTeamAuthServiceImpl<AutoApiDao, AutoApi> implements AutoApiService {

    @Autowired
    private AutoMicroserviceService autoMicroserviceService;

    @Autowired
    private AutoModuleService autoModuleService;

    @Autowired
    private AutoEnvironmentService autoEnvironmentService;

    @Autowired
    private AutoServerService autoServerService;

    @Autowired
    private AutoVariableService autoVariableService;

    @Override
    public Class getAuthEnumClass() {
        return AutoApiAuthEnum.class;
    }

    @Override
    public List<String> getAuthPermissionKeyList() {
        return Arrays.asList(AutoApiAuthEnum.ADD.getKey(), AutoApiAuthEnum.EDIT.getKey(), AutoApiAuthEnum.DELETE.getKey());
    }

    @Override
    protected QueryWrapper<AutoApi> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<AutoApi> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoApi::getObjectId), commonPageInfo.getObjectId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoApi::getObjectKey), commonPageInfo.getObjectKey());
        if (StrUtil.isNotEmpty(commonPageInfo.getCustomParamsMapStr("moduleId"))) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(AutoApi::getModuleId), commonPageInfo.getCustomParamsMapStr("moduleId"));
        }
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        autoModuleService.setMationForMap(beans, "moduleId", "moduleMation");
        autoEnvironmentService.setMationForMap(beans, "environmentId", "environmentMation");
        autoServerService.setMationForMap(beans, "serverId", "serverMation");
        autoMicroserviceService.setMationForMap(beans, "microserviceId", "microserviceMation");
        return beans;
    }

    @Override
    public void apiTest(InputObject inputObject, OutputObject outputObject) {
        AutoApi autoApi = inputObject.getParams(AutoApi.class);
        Map<String, Object> result = apiTest(autoApi, null);
        outputObject.setBean(result);
    }

    @Override
    public void apiTestById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        Map<String, Object> result = apiTest(id);
        outputObject.setBean(result);
    }

    @Override
    public Map<String, Object> apiTest(String id) {
        AutoApi autoApi = selectById(id);
        return apiTest(autoApi, null);
    }

    @Override
    public Map<String, Object> apiTest(AutoApi autoApi, AutoHistoryStepApi autoHistoryStepApi) {
        AutoServer autoServer = autoServerService.selectById(autoApi.getServerId());
        if (ObjectUtil.isEmpty(autoServer) || StrUtil.isEmpty(autoServer.getId())) {
            throw new CustomException("服务信息不存在。");
        }
        AutoMicroservice autoMicroservice = autoMicroserviceService.selectById(autoApi.getMicroserviceId());
        if (ObjectUtil.isEmpty(autoMicroservice) || StrUtil.isEmpty(autoMicroservice.getId())) {
            throw new CustomException("微服务信息不存在。");
        }
        // 获取请求地址
        String httpUrl = String.format(Locale.ROOT, "http://%s:%s/%s%s", autoServer.getIp(), autoMicroservice.getPort(),
            autoMicroservice.getPath(), autoApi.getAddress());
        // 构造参数
        Map<String, String> requestHeaderKey2Value = autoVariableService.getAutoVariable(AutoVariableType.GLOBAL_HEADER.getKey(), autoApi.getEnvironmentId());
        // 发送请求
        String startTime = DateUtil.getPointTime(DateUtil.YYYY_MM_DD_HH_MM_SS_SSS);
        String responseData = HttpRequestUtil.getDataByRequest(httpUrl, autoApi.getRequestWay(), requestHeaderKey2Value, autoApi.getInputExample());
        String endTime = DateUtil.getPointTime(DateUtil.YYYY_MM_DD_HH_MM_SS_SSS);
        if (ObjectUtil.isNotEmpty(autoHistoryStepApi)) {
            // 设置请求历史信息
            autoHistoryStepApi.setUrl(httpUrl);
            autoHistoryStepApi.setMethod(autoApi.getRequestWay());
            autoHistoryStepApi.setHeader(JSONUtil.toJsonStr(requestHeaderKey2Value));
            autoHistoryStepApi.setInputValue(autoApi.getInputExample());
            autoHistoryStepApi.setOutputValue(responseData);
            autoHistoryStepApi.setExecuteStartTime(startTime);
            autoHistoryStepApi.setExecuteEndTime(endTime);
            autoHistoryStepApi.setExecuteTime(String.valueOf(DateUtil.getDistanceMillisecondHMS(startTime, endTime, DateUtil.YYYY_MM_DD_HH_MM_SS_SSS)));
        }
        Map<String, Object> reqObj = JSONUtil.toBean(responseData, null);
        return reqObj;
    }

}
