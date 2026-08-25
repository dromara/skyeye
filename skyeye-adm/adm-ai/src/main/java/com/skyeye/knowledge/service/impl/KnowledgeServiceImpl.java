/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.knowledge.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.enumeration.ScheduleFrequency;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.knowledge.classenum.KnowledgeSyncResultEnum;
import com.skyeye.knowledge.classenum.KnowledgeSyncTriggerEnum;
import com.skyeye.knowledge.classenum.KnowledgeSyncTypeEnum;
import com.skyeye.knowledge.dao.KnowledgeDao;
import com.skyeye.knowledge.entity.Knowledge;
import com.skyeye.knowledge.entity.KnowledgeSync;
import com.skyeye.knowledge.service.KnowledgeDocService;
import com.skyeye.knowledge.service.KnowledgeSegmentService;
import com.skyeye.knowledge.service.KnowledgeService;
import com.skyeye.knowledge.service.KnowledgeSyncHistoryService;
import com.skyeye.knowledge.service.KnowledgeSyncService;
import com.skyeye.knowledge.util.KnowledgeJdbcHelper;
import com.skyeye.knowledge.util.KnowledgeScheduleHelper;
import com.skyeye.role.entity.Role;
import com.skyeye.role.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@SkyeyeService(name = "AI知识库", groupName = "AI知识库", allowDynamicAttrKey = false)
public class KnowledgeServiceImpl extends SkyeyeBusinessServiceImpl<KnowledgeDao, Knowledge> implements KnowledgeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KnowledgeServiceImpl.class);

    @Autowired
    private KnowledgeSyncService knowledgeSyncService;

    @Autowired
    private KnowledgeDocService knowledgeDocService;

    @Autowired
    private KnowledgeSegmentService knowledgeSegmentService;

    @Autowired
    private KnowledgeSyncHistoryService knowledgeSyncHistoryService;

    @Autowired
    @Lazy
    private RoleService roleService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateEntity(InputObject inputObject, OutputObject outputObject) {
        Knowledge entity = inputObject.getParams(Knowledge.class);
        fillSyncList(entity, inputObject.getParams().get("syncList"));
        String id = saveOrUpdateEntity(entity, inputObject.getLogParams().get("id").toString());
        outputObject.setBean(selectById(id));
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    private void fillSyncList(Knowledge entity, Object syncListObj) {
        if (entity == null || CollectionUtil.isNotEmpty(entity.getSyncList()) || syncListObj == null) {
            return;
        }
        if (syncListObj instanceof String) {
            String text = syncListObj.toString().trim();
            if (StrUtil.isNotBlank(text)) {
                entity.setSyncList(JSONUtil.toList(text, KnowledgeSync.class));
            }
        } else if (syncListObj instanceof List) {
            entity.setSyncList(JSONUtil.toList(JSONUtil.toJsonStr(syncListObj), KnowledgeSync.class));
        }
    }

    @Override
    public void validatorEntity(Knowledge entity) {
        super.validatorEntity(entity);
        ScheduleFrequency frequency = ScheduleFrequency.getByKey(entity.getFrequency());
        if (frequency == null) {
            throw new CustomException("请选择同步频次");
        }
        if (ScheduleFrequency.WEEKLY == frequency && StrUtil.isBlank(entity.getWeekDays())) {
            throw new CustomException("请选择每周同步日期");
        }
        if (ScheduleFrequency.MONTHLY == frequency && StrUtil.isBlank(entity.getMonthDays())) {
            throw new CustomException("请选择每月同步日期");
        }
        if (ScheduleFrequency.CUSTOM == frequency && StrUtil.isBlank(entity.getCustomCron())) {
            throw new CustomException("请配置自定义同步规则");
        }
        if (StrUtil.isBlank(entity.getDriverClass())) {
            entity.setDriverClass(KnowledgeJdbcHelper.DEFAULT_DRIVER);
        }
        if (StrUtil.isNotBlank(entity.getId()) && StrUtil.isBlank(entity.getJdbcPassword())) {
            Knowledge old = super.selectById(entity.getId());
            if (old != null) {
                entity.setJdbcPassword(old.getJdbcPassword());
            }
        }
        List<KnowledgeSync> syncList = entity.getSyncList();
        if (CollectionUtil.isNotEmpty(syncList)) {
            for (KnowledgeSync sync : syncList) {
                if (StrUtil.isBlank(sync.getTableName()) || StrUtil.isBlank(sync.getIdField())
                    || StrUtil.isBlank(sync.getContentFields())) {
                    throw new CustomException("请完善同步表、主键字段和内容字段");
                }
                if (sync.getSyncType() == null) {
                    sync.setSyncType(KnowledgeSyncTypeEnum.FULL.getKey());
                }
                if (KnowledgeSyncTypeEnum.INCREMENTAL.getKey().equals(sync.getSyncType())
                    && StrUtil.isBlank(sync.getWatermarkField())) {
                    throw new CustomException("增量同步必须指定水位字段");
                }
            }
        }
    }

    @Override
    public void writePostpose(Knowledge entity, String userId) {
        super.writePostpose(entity, userId);
        knowledgeSyncService.saveList(entity.getId(), entity.getSyncList());
    }

    @Override
    public void deletePreExecution(String id) {
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Role::getKnowledgeId), id);
        if (roleService.count(queryWrapper) > 0) {
            throw new CustomException("该知识库已绑定 AI 角色，请先解除绑定再删除");
        }
    }

    @Override
    public void deletePostpose(String id) {
        knowledgeSyncService.deleteByKnowledgeId(id);
        knowledgeDocService.deleteByKnowledgeId(id);
        knowledgeSegmentService.deleteByKnowledgeId(id);
        knowledgeSyncHistoryService.deleteByKnowledgeId(id);
    }

    @Override
    public Knowledge selectById(String id) {
        Knowledge knowledge = super.selectById(id);
        if (knowledge != null) {
            knowledge.setSyncList(knowledgeSyncService.selectByKnowledgeId(id));
            knowledge.setJdbcPassword(StrUtil.EMPTY);
        }
        return knowledge;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        maskPassword(beans);
        return beans;
    }

    @Override
    public List<Map<String, Object>> queryDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryDataList(inputObject);
        maskPassword(beans);
        return beans;
    }

    private void maskPassword(List<Map<String, Object>> beans) {
        if (CollectionUtil.isEmpty(beans)) {
            return;
        }
        for (Map<String, Object> bean : beans) {
            bean.put("jdbcPassword", StrUtil.EMPTY);
        }
    }

    @Override
    public void testDbConnection(InputObject inputObject, OutputObject outputObject) {
        JdbcParam param = resolveJdbc(inputObject.getParams());
        KnowledgeJdbcHelper.testConnection(param.driverClass, param.jdbcUrl, param.jdbcUser, param.jdbcPassword);
    }

    @Override
    public void queryDbTables(InputObject inputObject, OutputObject outputObject) {
        JdbcParam param = resolveJdbc(inputObject.getParams());
        List<Map<String, Object>> tables = KnowledgeJdbcHelper.listTables(
            param.driverClass, param.jdbcUrl, param.jdbcUser, param.jdbcPassword);
        outputObject.setBeans(tables);
        outputObject.settotal(tables.size());
    }

    @Override
    public void queryTableColumns(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String tableName = params.get("tableName") == null ? StrUtil.EMPTY : params.get("tableName").toString();
        JdbcParam param = resolveJdbc(params);
        List<Map<String, Object>> columns = KnowledgeJdbcHelper.listColumns(
            param.driverClass, param.jdbcUrl, param.jdbcUser, param.jdbcPassword, tableName);
        outputObject.setBeans(columns);
        outputObject.settotal(columns.size());
    }

    @Override
    public void syncNow(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        Knowledge knowledge = selectByIdForSync(id);
        int count = syncKnowledge(knowledge, KnowledgeSyncTriggerEnum.MANUAL.getKey());
        Map<String, Object> bean = new java.util.HashMap<>();
        bean.put("id", knowledge.getId());
        bean.put("syncCount", count);
        bean.put("lastSyncTime", knowledge.getLastSyncTime());
        outputObject.setBean(bean);
    }

    @Override
    public int syncKnowledge(Knowledge knowledge) {
        return syncKnowledge(knowledge, KnowledgeSyncTriggerEnum.MANUAL.getKey());
    }

    @Override
    public int syncKnowledge(Knowledge knowledge, Integer triggerType) {
        if (knowledge == null || StrUtil.isBlank(knowledge.getId())) {
            throw new CustomException("知识库不存在");
        }
        if (!EnableEnum.ENABLE_USING.getKey().equals(knowledge.getEnabled())) {
            throw new CustomException("知识库已禁用，无法同步");
        }
        if (StrUtil.isBlank(knowledge.getJdbcUrl())) {
            throw new CustomException("请先配置同步数据库");
        }
        List<KnowledgeSync> syncList = knowledge.getSyncList();
        if (CollectionUtil.isEmpty(syncList)) {
            syncList = knowledgeSyncService.selectByKnowledgeId(knowledge.getId());
        }
        if (CollectionUtil.isEmpty(syncList)) {
            throw new CustomException("请先配置同步表");
        }
        Integer trigger = triggerType == null ? KnowledgeSyncTriggerEnum.MANUAL.getKey() : triggerType;
        String startTime = DateUtil.getTimeAndToString();
        int total = 0;
        Integer status = KnowledgeSyncResultEnum.SUCCESS.getKey();
        String errorMsg = StrUtil.EMPTY;
        try {
            String tenantId = StrUtil.blankToDefault(TenantContext.getTenantId(), knowledge.getTenantId());
            for (KnowledgeSync sync : syncList) {
                total += syncOneTable(knowledge, sync, tenantId);
            }
            UpdateWrapper<Knowledge> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, knowledge.getId());
            updateWrapper.set(MybatisPlusUtil.toColumns(Knowledge::getLastSyncTime), DateUtil.getTimeAndToString());
            update(updateWrapper);
            knowledge.setLastSyncTime(DateUtil.getTimeAndToString());
            return total;
        } catch (Exception e) {
            status = KnowledgeSyncResultEnum.FAIL.getKey();
            errorMsg = e.getMessage();
            if (e instanceof CustomException) {
                throw (CustomException) e;
            }
            throw new CustomException(StrUtil.blankToDefault(e.getMessage(), "同步失败"));
        } finally {
            try {
                knowledgeSyncHistoryService.saveHistory(knowledge.getId(), trigger, status, total,
                    startTime, DateUtil.getTimeAndToString(), errorMsg);
            } catch (Exception ex) {
                LOGGER.warn("知识库[{}]保存同步历史失败: {}", knowledge.getName(), ex.getMessage());
            }
        }
    }

    @Override
    public void syncDueKnowledges() {
        QueryWrapper<Knowledge> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Knowledge::getEnabled), EnableEnum.ENABLE_USING.getKey());
        List<Knowledge> list = list(queryWrapper);
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        Date now = new Date();
        for (Knowledge item : list) {
            if (!KnowledgeScheduleHelper.isDue(item, now)) {
                continue;
            }
            try {
                if (StrUtil.isNotBlank(item.getTenantId())) {
                    TenantContext.setTenantId(item.getTenantId());
                }
                Knowledge knowledge = selectByIdForSync(item.getId());
                syncKnowledge(knowledge, KnowledgeSyncTriggerEnum.SCHEDULE.getKey());
            } catch (Exception e) {
                LOGGER.warn("知识库[{}]定时同步失败: {}", item.getName(), e.getMessage());
            }
        }
    }

    private int syncOneTable(Knowledge knowledge, KnowledgeSync sync, String tenantId) {
        KnowledgeJdbcHelper.checkIdentifier(sync.getTableName(), "表名");
        KnowledgeJdbcHelper.checkIdentifier(sync.getIdField(), "主键字段");
        Set<String> columnSet = new LinkedHashSet<>();
        columnSet.add(sync.getIdField());
        if (StrUtil.isNotBlank(sync.getTitleField())) {
            KnowledgeJdbcHelper.checkIdentifier(sync.getTitleField(), "标题字段");
            columnSet.add(sync.getTitleField());
        }
        List<String> contentFields = splitFields(sync.getContentFields());
        if (CollectionUtil.isEmpty(contentFields)) {
            throw new CustomException("请选择内容字段");
        }
        columnSet.addAll(contentFields);
        boolean fullSync = !KnowledgeSyncTypeEnum.INCREMENTAL.getKey().equals(sync.getSyncType());
        String watermarkField = fullSync ? StrUtil.EMPTY : sync.getWatermarkField();
        String lastWatermark = fullSync ? StrUtil.EMPTY : sync.getLastWatermark();
        if (StrUtil.isNotBlank(watermarkField)) {
            columnSet.add(watermarkField);
        }
        String tenantField = StrUtil.blankToDefault(sync.getTenantField(), "tenant_id");
        List<Map<String, Object>> rows = KnowledgeJdbcHelper.queryRows(
            knowledge.getDriverClass(), knowledge.getJdbcUrl(), knowledge.getJdbcUser(), knowledge.getJdbcPassword(),
            sync.getTableName(), new ArrayList<>(columnSet), tenantField, tenantId, watermarkField, lastWatermark);
        int count = knowledgeDocService.saveSyncedRows(knowledge.getId(), sync.getTableName(), rows,
            sync.getIdField(), sync.getTitleField(), contentFields, fullSync);
        if (StrUtil.isNotBlank(watermarkField) && CollectionUtil.isNotEmpty(rows)) {
            Object max = rows.get(rows.size() - 1).get(watermarkField);
            if (max != null) {
                UpdateWrapper<KnowledgeSync> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq(CommonConstants.ID, sync.getId());
                updateWrapper.set(MybatisPlusUtil.toColumns(KnowledgeSync::getLastWatermark), String.valueOf(max));
                knowledgeSyncService.update(updateWrapper);
            }
        }
        return count;
    }

    private Knowledge selectByIdForSync(String id) {
        Knowledge knowledge = super.selectById(id);
        if (knowledge == null || StrUtil.isBlank(knowledge.getId())) {
            throw new CustomException("知识库不存在");
        }
        knowledge.setSyncList(knowledgeSyncService.selectByKnowledgeId(id));
        return knowledge;
    }

    private JdbcParam resolveJdbc(Map<String, Object> params) {
        JdbcParam param = new JdbcParam();
        param.jdbcUrl = str(params.get("jdbcUrl"));
        param.jdbcUser = str(params.get("jdbcUser"));
        param.jdbcPassword = str(params.get("jdbcPassword"));
        param.driverClass = StrUtil.blankToDefault(str(params.get("driverClass")), KnowledgeJdbcHelper.DEFAULT_DRIVER);
        String id = str(params.get("id"));
        if (StrUtil.isNotBlank(id) && (StrUtil.isBlank(param.jdbcPassword) || StrUtil.isBlank(param.jdbcUrl))) {
            Knowledge knowledge = super.selectById(id);
            if (knowledge != null) {
                if (StrUtil.isBlank(param.jdbcUrl)) {
                    param.jdbcUrl = knowledge.getJdbcUrl();
                }
                if (StrUtil.isBlank(param.jdbcUser)) {
                    param.jdbcUser = knowledge.getJdbcUser();
                }
                if (StrUtil.isBlank(param.jdbcPassword)) {
                    param.jdbcPassword = knowledge.getJdbcPassword();
                }
                if (StrUtil.isBlank(str(params.get("driverClass")))) {
                    param.driverClass = StrUtil.blankToDefault(knowledge.getDriverClass(), KnowledgeJdbcHelper.DEFAULT_DRIVER);
                }
            }
        }
        return param;
    }

    private List<String> splitFields(String contentFields) {
        List<String> result = new ArrayList<>();
        if (StrUtil.isBlank(contentFields)) {
            return result;
        }
        for (String item : contentFields.split(",")) {
            String field = item.trim();
            if (StrUtil.isBlank(field)) {
                continue;
            }
            KnowledgeJdbcHelper.checkIdentifier(field, "内容字段");
            result.add(field);
        }
        return result;
    }

    private String str(Object value) {
        return value == null ? StrUtil.EMPTY : value.toString();
    }

    private static class JdbcParam {
        private String jdbcUrl;
        private String jdbcUser;
        private String jdbcPassword;
        private String driverClass;
    }

}
