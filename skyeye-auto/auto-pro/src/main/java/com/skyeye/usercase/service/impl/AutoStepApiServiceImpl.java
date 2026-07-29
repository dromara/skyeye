/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.usercase.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.api.entity.AutoApi;
import com.skyeye.api.service.AutoApiService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.history.entity.AutoHistoryStep;
import com.skyeye.history.entity.AutoHistoryStepApi;
import com.skyeye.usercase.dao.AutoStepApiDao;
import com.skyeye.usercase.entity.AutoStep;
import com.skyeye.usercase.entity.AutoStepApi;
import com.skyeye.usercase.service.AutoStepApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @ClassName: AutoStepApiServiceImpl
 * @Description: 用例步骤关联的api管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/5/16 23:20
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye-report Inc. All rights reserved.
 * 注意：本内容具体规则请参照readme执行，地址：https://gitee.com/doc_wei01/skyeye-report/blob/master/README.md
 */
@Service
@SkyeyeService(name = "用例步骤关联的api管理", groupName = "用例步骤关联的api管理", manageShow = false)
public class AutoStepApiServiceImpl extends SkyeyeBusinessServiceImpl<AutoStepApiDao, AutoStepApi> implements AutoStepApiService {

    @Autowired
    private AutoApiService autoApiService;

    @Override
    public void saveList(String objectId, List<AutoStepApi> autoStepApiList) {
        deleteByObjectId(objectId);
        if (CollectionUtil.isNotEmpty(autoStepApiList)) {
            for (AutoStepApi apis : autoStepApiList) {
                apis.setCaseId(objectId);
            }
            createEntity(autoStepApiList, StrUtil.EMPTY);
        }
    }

    @Override
    public void deleteByObjectId(String objectId) {
        QueryWrapper<AutoStepApi> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoStepApi::getCaseId), objectId);
        remove(queryWrapper);
    }

    @Override
    public Map<String, AutoStepApi> selectByObjectId(String objectId) {
        QueryWrapper<AutoStepApi> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoStepApi::getCaseId), objectId);
        List<AutoStepApi> list = list(queryWrapper);
        return list.stream().collect(Collectors.toMap(AutoStepApi::getStepId, Function.identity(), (v1, v2) -> v1));
    }

    @Override
    public void executeStepApi(AutoStep autoStep, Map<String, Object> result, Map<String, Object> inputParams, AutoHistoryStep autoHistoryStep) {
        // 试跑未保存步骤时可能没有 stepApi.id，只需绑定了 apiId 即可
        if (ObjectUtil.isEmpty(autoStep.getStepApi()) || StrUtil.isEmpty(autoStep.getStepApi().getApiId())) {
            throw new CustomException("不存在该API或者未绑定API");
        }
        AutoApi autoApi = autoApiService.selectById(autoStep.getStepApi().getApiId());
        autoApi.setInputExample(JSONUtil.toJsonStr(inputParams));
        AutoHistoryStepApi autoHistoryStepApi = new AutoHistoryStepApi();
        Map<String, Object> apiResult = autoApiService.apiTest(autoApi, autoHistoryStepApi);
        autoHistoryStep.setAutoHistoryStepApi(autoHistoryStepApi);
        result.put(autoStep.getResultKey(), apiResult);
    }

}
