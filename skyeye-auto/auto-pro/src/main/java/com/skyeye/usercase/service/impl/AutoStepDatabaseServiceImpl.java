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
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.database.service.AutoDataBaseService;
import com.skyeye.exception.CustomException;
import com.skyeye.history.entity.AutoHistoryStep;
import com.skyeye.history.entity.AutoHistoryStepDatabase;
import com.skyeye.sql.entity.AutoDataSource;
import com.skyeye.sql.entity.ReportMetaDataRow;
import com.skyeye.sql.query.factory.QueryerFactory;
import com.skyeye.usercase.dao.AutoStepDatabaseDao;
import com.skyeye.usercase.entity.AutoStep;
import com.skyeye.usercase.entity.AutoStepDatabase;
import com.skyeye.usercase.entity.AutoStepDatabaseValue;
import com.skyeye.usercase.service.AutoStepDatabaseService;
import com.skyeye.usercase.service.AutoStepDatabaseValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @ClassName: AutoStepDatabaseServiceImpl
 * @Description: 用例步骤关联的数据库管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/5/16 23:20
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye-report Inc. All rights reserved.
 * 注意：本内容具体规则请参照readme执行，地址：https://gitee.com/doc_wei01/skyeye-report/blob/master/README.md
 */
@Service
@SkyeyeService(name = "用例步骤关联的数据库管理", groupName = "用例步骤关联的数据库管理", manageShow = false)
public class AutoStepDatabaseServiceImpl extends SkyeyeBusinessServiceImpl<AutoStepDatabaseDao, AutoStepDatabase> implements AutoStepDatabaseService {

    @Autowired
    private AutoStepDatabaseValueService autoStepDatabaseValueService;

    @Autowired
    private AutoDataBaseService autoDataBaseService;

    @Override
    public void writePostpose(List<AutoStepDatabase> autoStepDatabaseList, String userId) {
        List<AutoStepDatabaseValue> autoStepDatabaseValueList = new ArrayList<>();
        autoStepDatabaseList.forEach(autoStepDatabase -> {
            autoStepDatabase.getStepDatabaseValueList().forEach(autoStepDatabaseValue -> {
                autoStepDatabaseValue.setStepDatabaseId(autoStepDatabase.getId());
            });
            autoStepDatabaseValueList.addAll(autoStepDatabase.getStepDatabaseValueList());
        });
        autoStepDatabaseValueService.saveList(autoStepDatabaseList.stream().findFirst().orElse(new AutoStepDatabase()).getCaseId(), autoStepDatabaseValueList);
    }

    @Override
    public void saveList(String objectId, List<AutoStepDatabase> autoStepDatabaseList) {
        deleteByObjectId(objectId);
        if (CollectionUtil.isNotEmpty(autoStepDatabaseList)) {
            for (AutoStepDatabase databases : autoStepDatabaseList) {
                databases.setCaseId(objectId);
            }
            createEntity(autoStepDatabaseList, StrUtil.EMPTY);
        }
    }

    @Override
    public void deleteByObjectId(String objectId) {
        autoStepDatabaseValueService.deleteByObjectId(objectId);
        QueryWrapper<AutoStepDatabase> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoStepDatabase::getCaseId), objectId);
        remove(queryWrapper);
    }

    @Override
    public Map<String, AutoStepDatabase> selectByObjectId(String objectId) {
        QueryWrapper<AutoStepDatabase> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoStepDatabase::getCaseId), objectId);
        List<AutoStepDatabase> list = list(queryWrapper);
        Map<String, List<AutoStepDatabaseValue>> databaseValue = autoStepDatabaseValueService.selectByObjectId(objectId);
        list.forEach(autoStepDatabase -> {
            autoStepDatabase.setStepDatabaseValueList(databaseValue.get(autoStepDatabase.getId()));
        });
        return list.stream().collect(Collectors.toMap(AutoStepDatabase::getStepId, Function.identity(), (v1, v2) -> v1));
    }

    @Override
    public void executeAtepDatabase(AutoStep autoStep, Map<String, Object> result, Map<String, Object> inputParams, AutoHistoryStep autoHistoryStep) {
        // 试跑未保存步骤时可能没有 stepDatabase.id，只需绑定了 databaseId 即可
        if (ObjectUtil.isEmpty(autoStep.getStepDatabase())
            || StrUtil.isEmpty(autoStep.getStepDatabase().getDatabaseId())) {
            throw new CustomException("不存在该数据库或者未绑定数据库");
        }
        if (StrUtil.isEmpty(autoStep.getStepDatabase().getSqlContent())) {
            throw new CustomException("脚本不能为空");
        }
        // 替换占位符的字段
        String sqlContent = autoStep.getStepDatabase().getSqlContent();
        for (Map.Entry<String, Object> entry : inputParams.entrySet()) {
            sqlContent = sqlContent.replaceAll(String.format("#\\{%s\\}", entry.getKey()), String.format("'%s'", entry.getValue()));
        }

        AutoDataSource autoDataSource = autoDataBaseService.getReportDataSource(autoStep.getStepDatabase().getDatabaseId());
        // 数据库执行信息
        AutoHistoryStepDatabase autoHistoryStepDatabase = new AutoHistoryStepDatabase();
        String historyDbId = StrUtil.blankToDefault(autoStep.getStepDatabase().getId(), autoStep.getStepDatabase().getDatabaseId());
        autoHistoryStepDatabase.setDatabaseId(historyDbId);
        autoHistoryStepDatabase.setSqlContent(sqlContent);
        try {
            List<ReportMetaDataRow> metaDataRows = QueryerFactory.create(autoDataSource).getMetaDataRows(sqlContent);
            // 获取数据库查询出来的接口值，并进行数据组装
            List<Map<String, Object>> selResult = new ArrayList<>();
            if (CollectionUtil.isNotEmpty(autoStep.getStepDatabase().getStepDatabaseValueList())) {
                for (ReportMetaDataRow metaDataRow : metaDataRows) {
                    Map<String, Object> sel = new HashMap<>();
                    autoStep.getStepDatabase().getStepDatabaseValueList().forEach(autoStepDatabaseValue -> {
                        sel.put(autoStepDatabaseValue.getKey(), metaDataRow.getCell(autoStepDatabaseValue.getValue()).getValue());
                    });
                    selResult.add(sel);
                }
            }
            result.put(autoStep.getResultKey(), selResult);

            autoHistoryStepDatabase.setOutputValue(JSONUtil.toJsonStr(selResult));
        } catch (Exception ex) {
            autoHistoryStepDatabase.setOutputValue(ex.toString());
            throw new CustomException("executeAtepDatabase ", ex);
        } finally {
            autoHistoryStep.setAutoHistoryStepDatabase(autoHistoryStepDatabase);
        }
    }

}
