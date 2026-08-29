/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.mq.job.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.skyeye.common.constans.FileConstants;
import com.skyeye.common.constans.MqConstants;
import com.skyeye.common.enumeration.DeleteFlagEnum;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.FileUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.folder.service.FolderService;
import com.skyeye.eve.note.classenum.FileFolderType;
import com.skyeye.eve.note.dao.NoteDao;
import com.skyeye.eve.note.entity.Note;
import com.skyeye.eve.note.service.NoteService;
import com.skyeye.eve.rest.mq.JobMateUpdateMation;
import com.skyeye.eve.service.IJobMateMationService;
import com.skyeye.exception.CustomException;
import com.skyeye.html.util.HtmlToPdfUtil;
import com.skyeye.luckysheet.util.LuckySheetToPdfUtil;
import com.youbenzi.md2.export.FileFactory;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

/**
 * @author 卫志强
 * @ClassName: OutputNotesIsZipServiceImpl
 * @Description: 笔记输出为压缩包
 * @date 2020年8月23日
 * <p>
 * RocketMQMessageListener注解参数解释
 * 1. topic:表示需要监听哪个topic的消息
 * 2. consumerGroup:表示消费者组
 * 3. selectorExpression:表示需要监听的tag
 */
@Component
@RocketMQMessageListener(
    topic = "${topic.output-notes-is-zip-service}",
    consumerGroup = "${topic.output-notes-is-zip-service}",
    selectorExpression = "${spring.profiles.active}")
public class OutputNotesIsZipConsume implements RocketMQListener<String> {

    private static Logger LOGGER = LoggerFactory.getLogger(OutputNotesIsZipConsume.class);

    @Autowired
    private IJobMateMationService iJobMateMationService;

    @Value("${IMAGES_PATH}")
    private String tPath;

    /**
     * 系统水印
     */
    @Value("${system.sysWaterMark}")
    private String sysWaterMark;

    /**
     * 图片访问基础路径
     */
    @Value("${webroot.fileBath}")
    private String webRootfileBath;

    @Autowired
    private NoteDao noteDao;

    @Autowired
    private FolderService folderService;

    @Autowired
    private NoteService noteService;

    @Value("${skyeye.tenant.enable}")
    private boolean tenantEnable;

    @Override
    public void onMessage(String data) {
        Map<String, Object> map = JSONUtil.toBean(data, null);
        String jobId = map.get("jobMateId").toString();
        Map<String, Object> mation = new HashMap<>();
        try {
            LOGGER.info("start output job, jobId is {}", jobId);
            String tenantId = map.getOrDefault("tenantId", StrUtil.EMPTY).toString();
            if (tenantEnable) {
                TenantContext.setTenantId(tenantId);
            }
            // 任务开始
            this.updateJobMation(jobId, MqConstants.JOB_TYPE_IS_PROCESSING, StrUtil.EMPTY);
            String rowId = map.get("rowId").toString();
            // 类型
            String type = map.get("noteType").toString();
            if (FileFolderType.FOLDER.getKey().equals(type)) {
                String zipFile = outputFolder(rowId, map.get("userId").toString(), tenantId);
                mation.put("filePath", zipFile);
            } else {
                String zipFile = outPutFileContent(rowId, map.get("userId").toString());
                mation.put("filePath", zipFile);
            }

            // 任务完成
            this.updateJobMation(jobId, MqConstants.JOB_TYPE_IS_SUCCESS, JSONUtil.toJsonStr(mation));
        } catch (Exception e) {
            LOGGER.info("job is fail, this message is {}", e);
            // 任务失败
            mation.put("message", e);
            this.updateJobMation(jobId, MqConstants.JOB_TYPE_IS_FAIL, JSONUtil.toJsonStr(mation));
        }
    }

    private void updateJobMation(String jobId, String status, String responseBody) {
        JobMateUpdateMation jobMateUpdateMation = new JobMateUpdateMation();
        jobMateUpdateMation.setJobId(jobId);
        jobMateUpdateMation.setStatus(status);
        jobMateUpdateMation.setResponseBody(responseBody);
        iJobMateMationService.comMQJobMation(jobMateUpdateMation);
    }

    /**
     * 文件夹(包含文件)输出为压缩包
     *
     * @param parentId
     * @param userId
     * @return
     */
    private String outputFolder(String parentId, String userId, String tenantId) {
        // 1.获取该目录下的所有目录
        List<Map<String, Object>> folderList = folderService.queryFolderAndChildList(Arrays.asList(parentId));
        // 2.获取所有目录下的所有文件
        List<Map<String, Object>> files = noteDao.queryFileList(folderList, DeleteFlagEnum.NOT_DELETE.getKey(), tenantId);
        String uuid = ToolUtil.getSurFaceId();
        // 文件存储基础路径
        String basePath = String.format("%s%s/%s/%s/", tPath, FileConstants.FileUploadPath.getSavePath(FileConstants.FileUploadPath.NOTE.getType()[0]), userId, uuid);
        FileUtil.createDirs(basePath);
        for (Map<String, Object> bean : files) {
            String content = bean.containsKey("content") ? bean.get("content").toString() : "";
            bean.put("fileAddress", outPutFileContent(basePath, content, Integer.parseInt(bean.get("type").toString()), userId, uuid));
            bean.put("content", "");
            bean.put("fileName", bean.get("name").toString() + ".pdf");
            String[] str = bean.get("parentId").toString().split(",");
            bean.put("directParentId", str[str.length - 1]);
            bean.put("fileType", bean.get("type"));
        }
        // 重置父id
        for (Map<String, Object> folder : folderList) {
            String[] str = folder.get("parentId").toString().split(",");
            folder.put("directParentId", str[str.length - 1]);
            folder.put("fileName", folder.get("name").toString());
            folder.put("fileType", FileFolderType.FOLDER.getKey());
        }
        folderList.addAll(files);
        // 将数据转化为树的形式，方便进行父id重新赋值
        folderList = ToolUtil.listToTree(folderList, "id", "directParentId", "children");
        // 打包--压缩包文件名
        String fileName = String.valueOf(System.currentTimeMillis());
        String strZipPath = basePath + fileName + ".zip";
        ZipOutputStream out = null;
        try {
            out = new ZipOutputStream(new FileOutputStream(strZipPath));
            ToolUtil.recursionZip(out, folderList, "", tPath.replace(FileConstants.FILE_BASE_FIRST_PATH, StrUtil.EMPTY), 2);
        } catch (Exception ee) {
            throw new CustomException(ee);
        } finally {
            // 删除临时文件
            for (Map<String, Object> bean : files) {
                FileUtil.deleteFile(tPath.replace(FileConstants.FILE_BASE_FIRST_PATH, StrUtil.EMPTY) + bean.get("fileAddress").toString());
            }
            FileUtil.close(out);
        }
        return String.format("%s/%s/%s/%s", FileConstants.FileUploadPath.getVisitPath(FileConstants.FileUploadPath.NOTE.getType()[0]), userId, uuid, fileName + ".zip");
    }

    /**
     * 单个文件输出
     *
     * @param basePath 基础路径
     * @param content  内容
     * @param type     类型
     * @param userId   用户id
     * @param uuid     唯一标识
     * @return
     */
    private String outPutFileContent(String basePath, String content, int type, String userId, String uuid) {
        String fileName = String.valueOf(System.currentTimeMillis());
        if (ToolUtil.isBlank(content)) {
            content = "暂无内容";
        }
        String outputPath = basePath + "/" + fileName + ".pdf";
        switch (type) {
            case 1:
                // 富文本编辑器
                HtmlToPdfUtil.convertHtmlToPdfWithWatermark(content, sysWaterMark, outputPath, webRootfileBath, StrUtil.EMPTY, StrUtil.EMPTY);
                break;
            case 2:
                // markdown笔记
                FileFactory.produce(content, outputPath, webRootfileBath, sysWaterMark);
                break;
            case 3:
                // word笔记-- TODO 暂时没有word的功能
                break;
            case 4:
                // ecxel笔记
                LuckySheetToPdfUtil.convertToPdf(content, outputPath);
                break;
            default:
                break;
        }
        return String.format("%s/%s/%s/%s", FileConstants.FileUploadPath.getVisitPath(FileConstants.FileUploadPath.NOTE.getType()[0]), userId, uuid, fileName + ".pdf");
    }

    /**
     * 单个文件输出
     *
     * @param fileId 文件id
     * @param userId 用户id
     * @return
     */
    private String outPutFileContent(String fileId, String userId) {
        Note note = noteService.selectById(fileId);
        String uuid = ToolUtil.getSurFaceId();
        // 文件存储基础路径
        String basePath = String.format("%s%s/%s/%s/", tPath, FileConstants.FileUploadPath.getSavePath(FileConstants.FileUploadPath.NOTE.getType()[0]), userId, uuid);
        FileUtil.createDirs(basePath);
        return outPutFileContent(basePath, note.getContent(), note.getType(), userId, uuid);
    }

}
