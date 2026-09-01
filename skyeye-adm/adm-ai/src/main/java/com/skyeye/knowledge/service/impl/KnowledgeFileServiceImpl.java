/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.knowledge.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.ai.core.enums.AiPlatformEnum;
import com.skyeye.ai.core.factory.AiFactory;
import com.skyeye.ai.core.knowledge.AiKnowledgeClient;
import com.skyeye.ai.core.knowledge.AiKnowledgeUploadHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.FileConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.FileUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.service.IUploadService;
import com.skyeye.exception.CustomException;
import com.skyeye.key.entity.AiApiKey;
import com.skyeye.key.service.AiApiKeyService;
import com.skyeye.knowledge.classenum.KnowledgeFileSyncStatusEnum;
import com.skyeye.knowledge.classenum.KnowledgeSyncItemTypeEnum;
import com.skyeye.knowledge.classenum.KnowledgeSyncResultEnum;
import com.skyeye.knowledge.classenum.KnowledgeSyncTriggerEnum;
import com.skyeye.knowledge.dao.KnowledgeFileDao;
import com.skyeye.knowledge.entity.Knowledge;
import com.skyeye.knowledge.entity.KnowledgeFile;
import com.skyeye.knowledge.entity.KnowledgeSyncHistoryItem;
import com.skyeye.knowledge.service.KnowledgeFileService;
import com.skyeye.knowledge.service.KnowledgeService;
import com.skyeye.knowledge.service.KnowledgeSyncHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

@Slf4j
@Service
@SkyeyeService(name = "AI知识库文件", groupName = "AI知识库", allowDynamicAttrKey = false)
public class KnowledgeFileServiceImpl extends SkyeyeBusinessServiceImpl<KnowledgeFileDao, KnowledgeFile> implements KnowledgeFileService {

    private static final int FILE_STORAGE_S3 = 20;

    private static final int ERROR_MSG_LIMIT = 1000;

    @Value("${IMAGES_PATH}")
    private String tPath;

    @Autowired
    @Lazy
    private KnowledgeService knowledgeService;

    @Autowired
    private IUploadService iUploadService;

    @Autowired
    private AiFactory aiFactory;

    @Autowired
    private AiApiKeyService aiApiKeyService;

    @Autowired
    private KnowledgeSyncHistoryService knowledgeSyncHistoryService;

    @Autowired
    @Qualifier("knowledgeSyncExecutor")
    private Executor knowledgeSyncExecutor;

    @Override
    public void validatorEntity(KnowledgeFile entity) {
        super.validatorEntity(entity);
        Knowledge knowledge = knowledgeService.selectById(entity.getKnowledgeId());
        if (knowledge == null || StrUtil.isBlank(knowledge.getId())) {
            throw new CustomException("知识库不存在");
        }
        if (entity.getFileSize() == null || entity.getFileSize() < 0) {
            entity.setFileSize(0L);
        }
        if (StrUtil.isBlank(entity.getFileExt()) && entity.getName().contains(".")) {
            entity.setFileExt(StrUtil.subAfter(entity.getName(), ".", true));
        }
        if (entity.getSyncStatus() == null) {
            entity.setSyncStatus(KnowledgeFileSyncStatusEnum.WAIT.getKey());
        }
    }

    @Override
    protected QueryWrapper<KnowledgeFile> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<KnowledgeFile> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isEmpty(commonPageInfo.getObjectId())) {
            throw new CustomException("请传入知识库id");
        }
        queryWrapper.eq(MybatisPlusUtil.toColumns(KnowledgeFile::getKnowledgeId), commonPageInfo.getObjectId());
        return queryWrapper;
    }

    @Override
    public List<KnowledgeFile> selectByKnowledgeId(String knowledgeId) {
        QueryWrapper<KnowledgeFile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(KnowledgeFile::getKnowledgeId), knowledgeId);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(KnowledgeFile::getCreateTime));
        return list(queryWrapper);
    }

    @Override
    public List<KnowledgeFile> selectNeedSync(String knowledgeId) {
        return selectByKnowledgeId(knowledgeId);
    }

    @Override
    public int syncPendingFiles(Knowledge knowledge, AiApiKey apiKey) {
        List<KnowledgeSyncHistoryItem> items = syncPendingFileItems(knowledge, apiKey);
        int success = 0;
        for (KnowledgeSyncHistoryItem item : items) {
            if (item.getSyncCount() != null) {
                success += item.getSyncCount();
            }
        }
        return success;
    }

    @Override
    public List<KnowledgeSyncHistoryItem> syncPendingFileItems(Knowledge knowledge, AiApiKey apiKey) {
        List<KnowledgeFile> files = selectNeedSync(knowledge.getId());
        List<KnowledgeSyncHistoryItem> items = new ArrayList<>();
        if (CollectionUtil.isEmpty(files)) {
            return items;
        }
        for (KnowledgeFile file : files) {
            try {
                syncOneFile(knowledge, apiKey, file);
                items.add(KnowledgeSyncHistoryItem.of(KnowledgeSyncItemTypeEnum.FILE.getKey(),
                    file.getId(), file.getName(), 1, KnowledgeSyncResultEnum.SUCCESS.getKey(), null));
            } catch (Exception e) {
                String msg = StrUtil.blankToDefault(e.getMessage(), "同步失败");
                markFail(file, msg);
                log.warn("知识库[{}]文件[{}]同步失败: {}", knowledge.getId(), file.getName(), msg);
                items.add(KnowledgeSyncHistoryItem.of(KnowledgeSyncItemTypeEnum.FILE.getKey(),
                    file.getId(), file.getName(), 0, KnowledgeSyncResultEnum.FAIL.getKey(), msg));
            }
        }
        return items;
    }

    @Override
    public void syncFileById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        KnowledgeFile file = super.selectById(id);
        if (file == null || StrUtil.isBlank(file.getId())) {
            throw new CustomException("文件不存在");
        }
        if (knowledgeSyncHistoryService.hasRunning(file.getKnowledgeId())) {
            throw new CustomException("该知识库正在同步中，请稍后再试");
        }
        Knowledge knowledge = knowledgeService.selectById(file.getKnowledgeId());
        if (knowledge == null || StrUtil.isBlank(knowledge.getId())) {
            throw new CustomException("知识库不存在");
        }
        if (!EnableEnum.ENABLE_USING.getKey().equals(knowledge.getEnabled())) {
            throw new CustomException("知识库已禁用，无法同步");
        }
        AiApiKey apiKey = aiApiKeyService.selectEnabledKeyByKnowledgeId(knowledge.getId());
        if (StrUtil.isBlank(apiKey.getPlatformKnowledgeId())) {
            throw new CustomException("请先在 AI 配置中填写平台知识库 ID");
        }

        String startTime = DateUtil.getTimeAndToString();
        String historyId = knowledgeSyncHistoryService.createRunningHistory(
            knowledge.getId(), KnowledgeSyncTriggerEnum.FILE.getKey(), startTime);
        String tenantId = TenantContext.getTenantId();
        TenantEnum isolationType = TenantContext.getIsolationType();
        knowledgeSyncExecutor.execute(() -> {
            Integer status = KnowledgeSyncResultEnum.SUCCESS.getKey();
            String errorMsg = StrUtil.EMPTY;
            int syncCount = 0;
            List<KnowledgeSyncHistoryItem> items = new ArrayList<>();
            try {
                if (StrUtil.isNotBlank(tenantId)) {
                    TenantContext.setTenantId(tenantId);
                }
                if (isolationType != null) {
                    TenantContext.setIsolationType(isolationType);
                }
                syncOneFile(knowledge, apiKey, file);
                syncCount = 1;
                items.add(KnowledgeSyncHistoryItem.of(KnowledgeSyncItemTypeEnum.FILE.getKey(),
                    file.getId(), file.getName(), 1, KnowledgeSyncResultEnum.SUCCESS.getKey(), null));
                try {
                    UpdateWrapper<Knowledge> updateWrapper = new UpdateWrapper<>();
                    updateWrapper.eq(CommonConstants.ID, knowledge.getId());
                    updateWrapper.set(MybatisPlusUtil.toColumns(Knowledge::getLastSyncTime), DateUtil.getTimeAndToString());
                    knowledgeService.update(updateWrapper);
                } catch (Exception ex) {
                    log.warn("更新知识库最近同步时间失败 knowledgeId={}: {}", knowledge.getId(), ex.getMessage());
                }
            } catch (Exception e) {
                status = KnowledgeSyncResultEnum.FAIL.getKey();
                errorMsg = StrUtil.blankToDefault(e.getMessage(), "同步失败");
                markFail(file, errorMsg);
                items.add(KnowledgeSyncHistoryItem.of(KnowledgeSyncItemTypeEnum.FILE.getKey(),
                    file.getId(), file.getName(), 0, KnowledgeSyncResultEnum.FAIL.getKey(), errorMsg));
                log.warn("知识库[{}]单文件[{}]异步同步失败: {}", knowledge.getId(), file.getName(), errorMsg);
            } finally {
                try {
                    knowledgeSyncHistoryService.finishHistory(historyId, status, syncCount,
                        DateUtil.getTimeAndToString(), errorMsg, items);
                } catch (Exception ex) {
                    log.warn("单文件同步写历史失败 historyId={}: {}", historyId, ex.getMessage());
                }
                TenantContext.clear();
            }
        });

        Map<String, Object> bean = new HashMap<>();
        bean.put("id", file.getId());
        bean.put("knowledgeId", knowledge.getId());
        bean.put("historyId", historyId);
        bean.put("async", true);
        outputObject.setBean(bean);
        outputObject.settotal(1);
    }

    @Override
    public void deletePreExecution(String id) {
        KnowledgeFile file = super.selectById(id);
        if (file == null || StrUtil.isBlank(file.getId())) {
            return;
        }
        // ① 同步进行中禁止删除，避免半截上传后对象对不上
        if (knowledgeSyncHistoryService.hasRunning(file.getKnowledgeId())) {
            throw new CustomException("该知识库正在同步中，请稍后再试");
        }
        AiApiKey apiKey = null;
        try {
            apiKey = aiApiKeyService.selectEnabledKeyByKnowledgeId(file.getKnowledgeId());
        } catch (Exception e) {
            log.warn("删除知识库文件时未找到可用 AI 配置 knowledgeId={}: {}", file.getKnowledgeId(), e.getMessage());
        }
        // ② 先清 S3 + 平台文档，再删本地，最后由框架删库记录
        deleteRemote(file, apiKey);
        deleteLocalFile(file.getPath());
    }

    @Override
    public void deleteByKnowledgeId(String knowledgeId) {
        // ① 查出该知识库下全部上传文件
        List<KnowledgeFile> files = selectByKnowledgeId(knowledgeId);
        if (CollectionUtil.isEmpty(files)) {
            return;
        }
        AiApiKey apiKey = null;
        try {
            apiKey = aiApiKeyService.selectEnabledKeyByKnowledgeId(knowledgeId);
        } catch (Exception e) {
            log.warn("删除知识库文件时未找到可用 AI 配置 knowledgeId={}: {}", knowledgeId, e.getMessage());
        }
        // ② 逐个清远端 + 本地
        for (KnowledgeFile file : files) {
            deleteRemote(file, apiKey);
            deleteLocalFile(file.getPath());
        }
        // ③ 再删库记录
        QueryWrapper<KnowledgeFile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(KnowledgeFile::getKnowledgeId), knowledgeId);
        remove(queryWrapper);
    }

    private void syncOneFile(Knowledge knowledge, AiApiKey apiKey, KnowledgeFile file) {
        // ① 库表 path 已是相对地址，直接传给上传接口
        if (StrUtil.isBlank(file.getPath())) {
            throw new CustomException("文件路径为空: " + file.getName());
        }
        // ② 失败重同步：先删旧 S3 对象，避免残留
        if (StrUtil.isNotBlank(file.getS3ObjectId()) && StrUtil.isNotBlank(file.getStorageConfigId())) {
            try {
                iUploadService.deleteFromFileStorage(file.getStorageConfigId(), file.getS3ObjectId());
            } catch (Exception e) {
                log.warn("覆盖同步前删除旧 S3 对象失败 path={}: {}", file.getS3ObjectId(), e.getMessage());
            }
        }
        // ③ 上传到文件存储器（S3/TOS），得到 url / tosPath / 对象路径
        String objectDir = AiKnowledgeUploadHelper.buildObjectDir(knowledge.getId());
        String storageFileName = StrUtil.blankToDefault(file.getName(), "file.bin");
        AiPlatformEnum platform = AiPlatformEnum.getName(apiKey.getPlatform());
        String configId = StrUtil.blankToDefault(knowledge.getFileConfigId(), StrUtil.EMPTY);
        Integer storage = null;
        if (StrUtil.isBlank(configId) && AiPlatformEnum.DOU_BAO == platform) {
            storage = FILE_STORAGE_S3;
        }
        Map<String, Object> storageResult = iUploadService.uploadToFileStorage(
            configId, storage, FileConstants.FileUploadPath.KNOWLG_CONTENT.getType()[0],
            storageFileName, StrUtil.EMPTY, file.getPath(), objectDir);
        boolean uploaded = isUploaded(storageResult);
        String fileUrl = uploaded ? str(storageResult.get("url")) : StrUtil.EMPTY;
        String tosPath = uploaded ? str(storageResult.get("tosPath")) : StrUtil.EMPTY;
        String storageConfigId = uploaded ? str(storageResult.get("configId")) : StrUtil.EMPTY;
        String s3ObjectId = uploaded ? str(storageResult.get("path")) : StrUtil.EMPTY;
        // ④ 豆包必须有公网 URL 或 TOS 路径，否则无法 doc/add
        if (AiPlatformEnum.DOU_BAO == platform) {
            if (!uploaded || (StrUtil.isBlank(fileUrl) && StrUtil.isBlank(tosPath))) {
                throw new CustomException("豆包知识库同步需要可用的文件存储器（请在知识库配置中指定文件配置，或配置 S3/TOS）");
            }
        }
        // ⑤ 覆盖同步：稳定 doc_id（f_{文件记录id}）重复导入会覆盖；
        //    若库里还是旧的平台 ID（与稳定 ID 不同），先删旧文档再导入
        AiKnowledgeClient client = aiFactory.getKnowledgeClient(platform);
        String stableDocId = AiKnowledgeUploadHelper.buildFileDocId(knowledge.getId(), file.getId());
        if (StrUtil.isNotBlank(file.getPlatformDocId()) && !StrUtil.equals(file.getPlatformDocId(), stableDocId)) {
            try {
                client.deleteDoc(apiKey.toAiKnowledgeConfig(), file.getPlatformDocId());
            } catch (Exception e) {
                log.warn("覆盖同步前删除平台文档失败 docId={}: {}", file.getPlatformDocId(), e.getMessage());
            }
        }
        String platformDocId = client.uploadFile(apiKey.toAiKnowledgeConfig(), storageFileName, fileUrl, tosPath,
            stableDocId);
        // ⑥ 回写同步成功：状态、S3 对象 ID、存储配置、平台文档 ID
        UpdateWrapper<KnowledgeFile> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, file.getId());
        updateWrapper.set(MybatisPlusUtil.toColumns(KnowledgeFile::getSyncStatus),
            KnowledgeFileSyncStatusEnum.SUCCESS.getKey());
        updateWrapper.set(MybatisPlusUtil.toColumns(KnowledgeFile::getS3ObjectId), s3ObjectId);
        updateWrapper.set(MybatisPlusUtil.toColumns(KnowledgeFile::getStorageConfigId), storageConfigId);
        updateWrapper.set(MybatisPlusUtil.toColumns(KnowledgeFile::getPlatformDocId),
            StrUtil.blankToDefault(platformDocId, StrUtil.EMPTY));
        updateWrapper.set(MybatisPlusUtil.toColumns(KnowledgeFile::getSyncTime), DateUtil.getTimeAndToString());
        updateWrapper.set(MybatisPlusUtil.toColumns(KnowledgeFile::getErrorMsg), StrUtil.EMPTY);
        update(updateWrapper);
        file.setSyncStatus(KnowledgeFileSyncStatusEnum.SUCCESS.getKey());
        file.setS3ObjectId(s3ObjectId);
        file.setStorageConfigId(storageConfigId);
        file.setPlatformDocId(platformDocId);
    }

    private void markFail(KnowledgeFile file, String errorMsg) {
        UpdateWrapper<KnowledgeFile> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, file.getId());
        updateWrapper.set(MybatisPlusUtil.toColumns(KnowledgeFile::getSyncStatus),
            KnowledgeFileSyncStatusEnum.FAIL.getKey());
        if (StrUtil.isNotBlank(errorMsg)) {
            String msg = errorMsg.length() > ERROR_MSG_LIMIT ? errorMsg.substring(0, ERROR_MSG_LIMIT) : errorMsg;
            updateWrapper.set(MybatisPlusUtil.toColumns(KnowledgeFile::getErrorMsg), msg);
        }
        update(updateWrapper);
    }

    private void deleteRemote(KnowledgeFile file, AiApiKey apiKey) {
        // ① 按存储配置 + 对象路径删 S3/TOS
        if (StrUtil.isNotBlank(file.getStorageConfigId()) && StrUtil.isNotBlank(file.getS3ObjectId())) {
            try {
                iUploadService.deleteFromFileStorage(file.getStorageConfigId(), file.getS3ObjectId());
            } catch (Exception e) {
                log.warn("删除知识库 S3 文件失败 configId={} path={}: {}",
                    file.getStorageConfigId(), file.getS3ObjectId(), e.getMessage());
            }
        }
        // ② 按平台文档 ID 删知识库侧文档
        if (apiKey != null && StrUtil.isNotBlank(file.getPlatformDocId())) {
            try {
                AiPlatformEnum platform = AiPlatformEnum.getName(apiKey.getPlatform());
                aiFactory.getKnowledgeClient(platform).deleteDoc(apiKey.toAiKnowledgeConfig(), file.getPlatformDocId());
            } catch (Exception e) {
                log.warn("删除平台知识库文档失败 docId={}: {}", file.getPlatformDocId(), e.getMessage());
            }
        }
    }

    private void deleteLocalFile(String visitPath) {
        String abs = resolveLocalAbsPath(visitPath);
        if (StrUtil.isBlank(abs)) {
            return;
        }
        try {
            FileUtil.deleteFile(abs);
        } catch (Exception e) {
            log.warn("删除知识库本地文件失败 path={}: {}", abs, e.getMessage());
        }
    }

    private String resolveLocalAbsPath(String visitPath) {
        if (StrUtil.isBlank(visitPath)) {
            return StrUtil.EMPTY;
        }
        String path = visitPath.replace('\\', '/');
        if (path.startsWith(tPath)) {
            return path;
        }
        int type = FileConstants.FileUploadPath.KNOWLG_CONTENT.getType()[0];
        String visitPrefix = FileConstants.FileUploadPath.getVisitPath(type);
        String savePrefix = FileConstants.FileUploadPath.getSavePath(type);
        if (path.contains(visitPrefix)) {
            String name = path.substring(path.indexOf(visitPrefix) + visitPrefix.length());
            return tPath + savePrefix + "/" + name;
        }
        if (path.startsWith("/")) {
            return tPath + path;
        }
        return tPath + "/" + path;
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

    private String str(Object value) {
        return value == null ? StrUtil.EMPTY : value.toString();
    }

}
