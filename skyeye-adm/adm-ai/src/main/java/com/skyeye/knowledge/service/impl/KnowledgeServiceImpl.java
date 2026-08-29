/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.knowledge.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.ai.core.enums.AiPlatformEnum;
import com.skyeye.ai.core.factory.AiFactory;
import com.skyeye.ai.core.knowledge.AiKnowledgeClient;
import com.skyeye.ai.core.knowledge.AiKnowledgeUploadHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.constans.FileConstants;
import com.skyeye.common.constans.QuartzConstants;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.enumeration.ScheduleFrequency;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.FileUtil;
import com.skyeye.common.util.QuartzCronUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.rest.quartz.SysQuartzMation;
import com.skyeye.eve.service.IQuartzService;
import com.skyeye.eve.service.IUploadService;
import com.skyeye.exception.CustomException;
import com.skyeye.key.entity.AiApiKey;
import com.skyeye.key.service.AiApiKeyService;
import com.skyeye.knowledge.classenum.KnowledgeSyncResultEnum;
import com.skyeye.knowledge.classenum.KnowledgeSyncTriggerEnum;
import com.skyeye.knowledge.classenum.KnowledgeSyncTypeEnum;
import com.skyeye.knowledge.dao.KnowledgeDao;
import com.skyeye.knowledge.entity.Knowledge;
import com.skyeye.knowledge.entity.KnowledgeSync;
import com.skyeye.knowledge.service.KnowledgeFileService;
import com.skyeye.knowledge.service.KnowledgeService;
import com.skyeye.knowledge.service.KnowledgeSyncHistoryService;
import com.skyeye.knowledge.service.KnowledgeSyncService;
import com.skyeye.knowledge.util.KnowledgeJdbcHelper;
import com.skyeye.knowledge.util.KnowledgeTenantFilterHelper;
import com.skyeye.role.entity.Role;
import com.skyeye.role.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Executor;

@Service
@SkyeyeService(name = "AI知识库", groupName = "AI知识库", allowDynamicAttrKey = false)
public class KnowledgeServiceImpl extends SkyeyeBusinessServiceImpl<KnowledgeDao, Knowledge> implements KnowledgeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KnowledgeServiceImpl.class);

    /**
     * 单文件最大约 5MB，与行数阈值取先到者
     */
    private static final int DEFAULT_FLUSH_MAX_BYTES = 5 * 1024 * 1024;

    /**
     * 累计多少行后刷一次上传（可通过 skyeye.ai.knowledge.sync.flush-rows 配置）
     */
    @Value("${skyeye.ai.knowledge.sync.flush-rows:5000}")
    private int flushRows;

    @Value("${skyeye.ai.knowledge.sync.flush-max-bytes:" + DEFAULT_FLUSH_MAX_BYTES + "}")
    private int flushMaxBytes;

    /**
     * 导入平台知识库成功后删除 S3/TOS 临时文件
     */
    @Value("${skyeye.ai.knowledge.sync.delete-storage-after-import:true}")
    private boolean deleteStorageAfterImport;

    /**
     * 与 skyeye-pro FileStorageEnum.S3 一致，豆包知识库走 S3/TOS
     */
    private static final int FILE_STORAGE_S3 = 20;

    @Autowired
    private KnowledgeSyncService knowledgeSyncService;

    @Autowired
    private KnowledgeSyncHistoryService knowledgeSyncHistoryService;

    @Autowired
    private AiFactory aiFactory;

    @Autowired
    private AiApiKeyService aiApiKeyService;

    @Autowired
    private IQuartzService iQuartzService;

    @Autowired
    private IUploadService iUploadService;

    @Autowired
    @Qualifier("knowledgeSyncExecutor")
    private Executor knowledgeSyncExecutor;

    @Autowired
    @Lazy
    private RoleService roleService;

    @Autowired
    @Lazy
    private KnowledgeFileService knowledgeFileService;

    @Value("${IMAGES_PATH}")
    private String tPath;

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
        if (EnableEnum.ENABLE_USING.getKey().equals(entity.getEnabled())
            && StrUtil.isEmpty(QuartzCronUtil.buildScheduleConf(
            entity.getFrequency(), entity.getExecuteTime(),
            entity.getWeekDays(), entity.getMonthDays(), entity.getCustomCron()))) {
            throw new CustomException("定时Cron生成失败，请检查执行时间与频次配置");
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
    }

    @Override
    public void writePostpose(Knowledge entity, String userId) {
        // 对齐巡检计划：先删旧 XXL 子任务，再按启用状态用业务 Cron 重新注册
        iQuartzService.stopAndDeleteTaskQuartz(entity.getId());
        super.writePostpose(entity, userId);
        if (EnableEnum.ENABLE_USING.getKey().equals(entity.getEnabled())) {
            String cron = QuartzCronUtil.buildScheduleConf(
                entity.getFrequency(), entity.getExecuteTime(),
                entity.getWeekDays(), entity.getMonthDays(), entity.getCustomCron());
            if (StrUtil.isEmpty(cron)) {
                throw new CustomException("定时Cron生成失败");
            }
            SysQuartzMation quartz = new SysQuartzMation();
            quartz.setName(entity.getId());
            quartz.setTitle(entity.getName());
            quartz.setScheduleConf(cron);
            quartz.setGroupId(QuartzConstants.QuartzMateMationJobType.AI_KNOWLEDGE_SYNC.getTaskType());
            iQuartzService.startUpTaskQuartz(quartz);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void writeSyncList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String knowledgeId = str(params.get("id"));
        if (StrUtil.isBlank(knowledgeId)) {
            throw new CustomException("请选择知识库");
        }
        Knowledge knowledge = super.selectById(knowledgeId);
        if (knowledge == null || StrUtil.isBlank(knowledge.getId())) {
            throw new CustomException("知识库不存在");
        }
        List<KnowledgeSync> syncList = parseSyncList(params.get("syncList"));
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
            if (StrUtil.isBlank(sync.getTenantIsolation())) {
                sync.setTenantIsolation(TenantEnum.STRONG_ISOLATION.getKey());
            } else {
                sync.setTenantIsolation(KnowledgeTenantFilterHelper.resolveIsolationKey(sync.getTenantIsolation()));
            }
            if (KnowledgeTenantFilterHelper.needTenantColumn(sync.getTenantIsolation())) {
                if (StrUtil.isBlank(sync.getTenantField())) {
                    sync.setTenantField("tenant_id");
                }
            } else {
                // 不做隔离 / 仅平台：不强制租户字段，避免校验无 tenant_id 的表
                sync.setTenantField(StrUtil.blankToDefault(sync.getTenantField(), StrUtil.EMPTY));
            }
        }
        knowledgeSyncService.saveList(knowledgeId, syncList);
        Map<String, Object> bean = new HashMap<>();
        bean.put("id", knowledgeId);
        bean.put("syncList", knowledgeSyncService.selectByKnowledgeId(knowledgeId));
        outputObject.setBean(bean);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    private List<KnowledgeSync> parseSyncList(Object syncListObj) {
        if (syncListObj == null) {
            return new ArrayList<>();
        }
        if (syncListObj instanceof String) {
            String text = syncListObj.toString().trim();
            if (StrUtil.isBlank(text)) {
                return new ArrayList<>();
            }
            return JSONUtil.toList(text, KnowledgeSync.class);
        }
        if (syncListObj instanceof List) {
            return JSONUtil.toList(JSONUtil.toJsonStr(syncListObj), KnowledgeSync.class);
        }
        return new ArrayList<>();
    }

    @Override
    public void deletePreExecution(String id) {
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Role::getKnowledgeId), id);
        if (roleService.count(queryWrapper) > 0) {
            throw new CustomException("该知识库已绑定 AI 角色，请先解除绑定再删除");
        }
        knowledgeFileService.deleteByKnowledgeId(id);
    }

    @Override
    public void deletePostpose(String id) {
        knowledgeSyncService.deleteByKnowledgeId(id);
        knowledgeSyncHistoryService.deleteByKnowledgeId(id);
        iQuartzService.stopAndDeleteTaskQuartz(id);
    }

    @Override
    public Knowledge selectById(String id) {
        Knowledge knowledge = super.selectById(id);
        if (knowledge != null) {
            knowledge.setSyncList(knowledgeSyncService.selectByKnowledgeId(id));
            knowledge.setHasJdbcPassword(StrUtil.isNotBlank(knowledge.getJdbcPassword()));
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
        if (knowledgeSyncHistoryService.hasRunning(knowledge.getId())) {
            throw new CustomException("该知识库正在同步中，请稍后再试");
        }
        // 预先校验，避免异步后才发现配置问题
        prepareSync(knowledge);
        Integer trigger = KnowledgeSyncTriggerEnum.MANUAL.getKey();
        String startTime = DateUtil.getTimeAndToString();
        String historyId = knowledgeSyncHistoryService.createRunningHistory(knowledge.getId(), trigger, startTime);
        String tenantId = TenantContext.getTenantId();
        TenantEnum isolationType = TenantContext.getIsolationType();
        knowledgeSyncExecutor.execute(() -> {
            try {
                if (StrUtil.isNotBlank(tenantId)) {
                    TenantContext.setTenantId(tenantId);
                }
                if (isolationType != null) {
                    TenantContext.setIsolationType(isolationType);
                }
                doSyncKnowledge(knowledge, historyId);
            } catch (Throwable t) {
                // doSyncKnowledge 的 finally 已写入历史；此处仅兜底未写入的异常
                LOGGER.warn("知识库[{}]异步同步失败: {}", knowledge.getId(), t.getMessage());
            } finally {
                TenantContext.clear();
            }
        });
        Map<String, Object> bean = new HashMap<>();
        bean.put("id", knowledge.getId());
        bean.put("historyId", historyId);
        bean.put("async", true);
        outputObject.setBean(bean);
        outputObject.setreturnMessage("已提交同步任务，请在同步历史查看进度");
    }

    @Override
    public int syncKnowledge(Knowledge knowledge) {
        return syncKnowledge(knowledge, KnowledgeSyncTriggerEnum.MANUAL.getKey());
    }

    @Override
    public int syncKnowledgeById(String knowledgeId) {
        Knowledge knowledge = selectByIdForSync(knowledgeId);
        return syncKnowledge(knowledge, KnowledgeSyncTriggerEnum.SCHEDULE.getKey());
    }

    @Override
    public int syncKnowledge(Knowledge knowledge, Integer triggerType) {
        if (knowledge == null || StrUtil.isBlank(knowledge.getId())) {
            throw new CustomException("知识库不存在");
        }
        if (knowledgeSyncHistoryService.hasRunning(knowledge.getId())) {
            throw new CustomException("该知识库正在同步中，请稍后再试");
        }
        prepareSync(knowledge);
        Integer trigger = triggerType == null ? KnowledgeSyncTriggerEnum.MANUAL.getKey() : triggerType;
        String startTime = DateUtil.getTimeAndToString();
        String historyId = knowledgeSyncHistoryService.createRunningHistory(knowledge.getId(), trigger, startTime);
        return doSyncKnowledge(knowledge, historyId);
    }

    private void prepareSync(Knowledge knowledge) {
        if (!EnableEnum.ENABLE_USING.getKey().equals(knowledge.getEnabled())) {
            throw new CustomException("知识库已禁用，无法同步");
        }
        AiApiKey apiKey = aiApiKeyService.selectEnabledKeyByKnowledgeId(knowledge.getId());
        if (StrUtil.isBlank(apiKey.getPlatformKnowledgeId())) {
            throw new CustomException("请先在 AI 配置中填写平台知识库 ID");
        }
        List<KnowledgeSync> syncList = knowledge.getSyncList();
        if (CollectionUtil.isEmpty(syncList)) {
            syncList = knowledgeSyncService.selectByKnowledgeId(knowledge.getId());
            knowledge.setSyncList(syncList);
        }
    }

    private int doSyncKnowledge(Knowledge knowledge, String historyId) {
        int total = 0;
        Integer status = KnowledgeSyncResultEnum.SUCCESS.getKey();
        String errorMsg = StrUtil.EMPTY;
        try {
            AiApiKey apiKey = aiApiKeyService.selectEnabledKeyByKnowledgeId(knowledge.getId());
            List<KnowledgeSync> syncList = knowledge.getSyncList();
            if (CollectionUtil.isEmpty(syncList)) {
                syncList = knowledgeSyncService.selectByKnowledgeId(knowledge.getId());
            }
            if (syncList == null) {
                syncList = Collections.emptyList();
            }
            SyncUploadContext uploadContext = new SyncUploadContext(knowledge.getId());
            // ① 有 JDBC + 同步表：抽表数据上传（没有表配置则跳过，不报错）；源表按全租户拉取，不按当前租户过滤
            if (CollectionUtil.isNotEmpty(syncList) && StrUtil.isNotBlank(knowledge.getJdbcUrl())) {
                for (KnowledgeSync sync : syncList) {
                    total += syncOneTable(knowledge, sync, apiKey, uploadContext);
                }
                flushUploadIfNeeded(knowledge, apiKey, uploadContext, true);
            }
            // ② 待同步/失败文件：本地 → S3 → 平台知识库
            total += knowledgeFileService.syncPendingFiles(knowledge, apiKey);
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
                knowledgeSyncHistoryService.finishHistory(historyId, status, total,
                    DateUtil.getTimeAndToString(), errorMsg);
            } catch (Exception ex) {
                LOGGER.warn("知识库[{}]保存同步历史失败: {}", knowledge.getName(), ex.getMessage());
            }
        }
    }

    private int syncOneTable(Knowledge knowledge, KnowledgeSync sync, AiApiKey apiKey,
                             SyncUploadContext uploadContext) {
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
        String lastWatermark = fullSync ? StrUtil.EMPTY : StrUtil.blankToDefault(sync.getLastWatermark(), StrUtil.EMPTY);
        if (StrUtil.isNotBlank(watermarkField)) {
            KnowledgeJdbcHelper.checkIdentifier(watermarkField, "水位字段");
            columnSet.add(watermarkField);
        }
        // 租户字段：仅强制隔离/弱隔离时加入查询与校验；不做隔离、仅平台的表通常无 tenant_id
        String tenantIsolation = KnowledgeTenantFilterHelper.resolveIsolationKey(sync.getTenantIsolation());
        String tenantField = StrUtil.EMPTY;
        if (KnowledgeTenantFilterHelper.needTenantColumn(tenantIsolation)) {
            tenantField = StrUtil.blankToDefault(sync.getTenantField(), "tenant_id");
            KnowledgeJdbcHelper.checkIdentifier(tenantField, "租户字段");
            columnSet.add(tenantField);
        }

        KnowledgeJdbcHelper.validateTableAndColumns(
            knowledge.getDriverClass(), knowledge.getJdbcUrl(), knowledge.getJdbcUser(), knowledge.getJdbcPassword(),
            sync.getTableName(), columnSet);

        List<String> columns = new ArrayList<>(columnSet);
        String tableRemark = KnowledgeJdbcHelper.loadTableComment(
            knowledge.getDriverClass(), knowledge.getJdbcUrl(), knowledge.getJdbcUser(), knowledge.getJdbcPassword(),
            sync.getTableName());
        Map<String, String> fieldRemarks = KnowledgeJdbcHelper.loadColumnCommentMap(
            knowledge.getDriverClass(), knowledge.getJdbcUrl(), knowledge.getJdbcUser(), knowledge.getJdbcPassword(),
            sync.getTableName());
        String lastId = StrUtil.EMPTY;
        String cursorWatermark = lastWatermark;
        int total = 0;
        while (true) {
            List<Map<String, Object>> rows = KnowledgeJdbcHelper.queryRowsBatch(
                knowledge.getDriverClass(), knowledge.getJdbcUrl(), knowledge.getJdbcUser(), knowledge.getJdbcPassword(),
                sync.getTableName(), columns,
                sync.getIdField(), lastId, watermarkField, cursorWatermark, KnowledgeJdbcHelper.BATCH_SIZE);
            if (CollectionUtil.isEmpty(rows)) {
                break;
            }
            for (Map<String, Object> row : rows) {
                uploadContext.ensureTableHeader(sync.getTableName());
                uploadContext.appendRow(AiKnowledgeUploadHelper.buildRowBlock(
                    sync.getTableName(), tableRemark, sync.getIdField(), sync.getTitleField(), row, contentFields,
                    fieldRemarks, tenantField, tenantIsolation));
                total++;
                flushUploadIfNeeded(knowledge, apiKey, uploadContext, false);
            }

            Map<String, Object> lastRow = rows.get(rows.size() - 1);
            lastId = valueOf(lastRow.get(sync.getIdField()));
            if (StrUtil.isNotBlank(watermarkField)) {
                String batchMaxWm = valueOf(lastRow.get(watermarkField));
                if (StrUtil.isNotBlank(batchMaxWm)) {
                    cursorWatermark = batchMaxWm;
                    UpdateWrapper<KnowledgeSync> updateWrapper = new UpdateWrapper<>();
                    updateWrapper.eq(CommonConstants.ID, sync.getId());
                    updateWrapper.set(MybatisPlusUtil.toColumns(KnowledgeSync::getLastWatermark), batchMaxWm);
                    knowledgeSyncService.update(updateWrapper);
                    sync.setLastWatermark(batchMaxWm);
                }
            }
            if (rows.size() < KnowledgeJdbcHelper.BATCH_SIZE) {
                break;
            }
        }
        return total;
    }

    /**
     * force=true 时刷掉剩余缓冲（整次同步结束）；否则按行数/体积阈值刷盘
     */
    private void flushUploadIfNeeded(Knowledge knowledge, AiApiKey apiKey, SyncUploadContext uploadContext,
                                     boolean force) {
        if (!uploadContext.hasPendingContent()) {
            return;
        }
        if (force || uploadContext.shouldFlush(flushRows, flushMaxBytes)) {
            flushUpload(knowledge, apiKey, uploadContext);
        }
    }

    private void flushUpload(Knowledge knowledge, AiApiKey apiKey, SyncUploadContext uploadContext) {
        String content = uploadContext.drainUploadBuffer();
        if (StrUtil.isBlank(content)) {
            return;
        }
        int partIndex = uploadContext.nextPart();
        String fileName = AiKnowledgeUploadHelper.buildFileName(partIndex);
        String platformDocName = AiKnowledgeUploadHelper.buildPlatformDocName(knowledge.getId(), partIndex);
        String objectDir = uploadContext.getObjectDir();
        // 落盘后拿到与库表一致的相对路径（/images/...），上传接口自行拼 IMAGES_PATH
        String relativePath = writeLocalTempFile(fileName, content);
        String storageConfigId;
        String storageObjectPath;
        try {
            String contentBase64 = Base64.getEncoder().encodeToString(content.getBytes(StandardCharsets.UTF_8));
            int type = FileConstants.FileUploadPath.KNOWLG_CONTENT.getType()[0];
            AiPlatformEnum platform = AiPlatformEnum.getName(apiKey.getPlatform());
            // 优先用知识库绑定的文件配置；未绑定时豆包按 S3 类型取，其它走默认
            String configId = knowledge == null ? StrUtil.EMPTY : StrUtil.blankToDefault(knowledge.getFileConfigId(), StrUtil.EMPTY);
            Integer storage = null;
            if (StrUtil.isBlank(configId) && AiPlatformEnum.DOU_BAO == platform) {
                storage = FILE_STORAGE_S3;
            }
            Map<String, Object> storageResult = iUploadService.uploadToFileStorage(
                configId, storage, type, fileName, contentBase64, relativePath, objectDir);
            boolean uploaded = isUploaded(storageResult);
            String fileUrl = uploaded ? str(storageResult.get("url")) : StrUtil.EMPTY;
            String tosPath = uploaded ? str(storageResult.get("tosPath")) : StrUtil.EMPTY;
            storageConfigId = uploaded ? str(storageResult.get("configId")) : StrUtil.EMPTY;
            storageObjectPath = uploaded ? str(storageResult.get("path")) : StrUtil.EMPTY;

            AiKnowledgeClient client = aiFactory.getKnowledgeClient(platform);
            if (AiPlatformEnum.DOU_BAO == platform) {
                if (!uploaded || (StrUtil.isBlank(fileUrl) && StrUtil.isBlank(tosPath))) {
                    throw new CustomException("豆包知识库同步需要可用的文件存储器（请在知识库配置中指定文件配置，或配置 S3/TOS）");
                }
            }
            client.uploadText(apiKey.toAiKnowledgeConfig(), fileName, content, fileUrl, tosPath, platformDocName);
            deleteStorageObjectIfNeeded(storageConfigId, storageObjectPath);
        } finally {
            if (StrUtil.isNotBlank(relativePath)) {
                FileUtil.deleteFile(tPath.replace("images", StrUtil.EMPTY) + relativePath);
            }
        }
    }

    /**
     * 平台导入成功后清理 S3/TOS 临时对象（失败仅打日志，不影响同步结果）
     */
    private void deleteStorageObjectIfNeeded(String configId, String objectPath) {
        if (!deleteStorageAfterImport || StrUtil.isBlank(configId) || StrUtil.isBlank(objectPath)) {
            return;
        }
        try {
            iUploadService.deleteFromFileStorage(configId, objectPath);
        } catch (Exception e) {
            LOGGER.warn("知识库同步后删除临时存储文件失败 configId={} path={}: {}",
                configId, objectPath, e.getMessage());
        }
    }

    /**
     * 写入临时文件，返回相对访问路径（/images/upload/...），与知识库文件表 path 格式一致
     */
    private String writeLocalTempFile(String fileName, String content) {
        try {
            int type = FileConstants.FileUploadPath.KNOWLG_CONTENT.getType()[0];
            String savePath = tPath + FileConstants.FileUploadPath.getSavePath(type);
            FileUtil.createDirs(savePath);
            FileUtil.writeByteToPointPath(content.getBytes(StandardCharsets.UTF_8), savePath + "/" + fileName);
            return FileConstants.FileUploadPath.getVisitPath(type) + fileName;
        } catch (Exception e) {
            LOGGER.warn("写入知识库临时文件失败: {}", e.getMessage());
            return StrUtil.EMPTY;
        }
    }

    /**
     * 单次同步的上传上下文：跨表共享缓冲，仅在达到阈值或整次同步结束时刷盘
     */
    private static final class SyncUploadContext {
        private final String objectDir;
        private final StringBuilder uploadBuffer = new StringBuilder();
        private int partSeq;
        private int bufferRows;
        private String currentTable;

        SyncUploadContext(String knowledgeId) {
            this.objectDir = AiKnowledgeUploadHelper.buildObjectDir(knowledgeId);
        }

        String getObjectDir() {
            return objectDir;
        }

        int nextPart() {
            return ++partSeq;
        }

        void ensureTableHeader(String tableName) {
            if (StrUtil.equals(currentTable, tableName)) {
                return;
            }
            currentTable = tableName;
            if (uploadBuffer.length() > 0) {
                uploadBuffer.append('\n');
            }
            uploadBuffer.append("知识库同步表: ").append(tableName).append('\n');
        }

        void appendRow(String rowBlock) {
            uploadBuffer.append(rowBlock);
            bufferRows++;
        }

        boolean hasPendingContent() {
            return uploadBuffer.length() > 0;
        }

        boolean shouldFlush(int flushRows, int flushMaxBytes) {
            return bufferRows >= flushRows || uploadBuffer.length() >= flushMaxBytes;
        }

        String drainUploadBuffer() {
            if (uploadBuffer.length() <= 0) {
                return StrUtil.EMPTY;
            }
            String content = uploadBuffer.toString();
            uploadBuffer.setLength(0);
            bufferRows = 0;
            currentTable = null;
            return content;
        }
    }

    private boolean isUploaded(Map<String, Object> storageResult) {
        if (storageResult == null) {
            return false;
        }
        Object uploaded = storageResult.get("uploaded");
        if (uploaded instanceof Boolean) {
            return (Boolean) uploaded;
        }
        return "true".equalsIgnoreCase(String.valueOf(uploaded));
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

    private String valueOf(Object value) {
        return value == null ? StrUtil.EMPTY : String.valueOf(value);
    }

    private static class JdbcParam {
        private String jdbcUrl;
        private String jdbcUser;
        private String jdbcPassword;
        private String driverClass;
    }

}
