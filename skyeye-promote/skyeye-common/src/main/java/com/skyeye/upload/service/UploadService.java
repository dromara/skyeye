/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.upload.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName: UploadService
 * @Description: 文件上传、下载服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2022/11/28 21:39
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface UploadService {

    void uploadFileResume(InputObject inputObject, OutputObject outputObject);

    void uploadFileChunks(InputObject inputObject, OutputObject outputObject);

    void checkUploadFileChunks(InputObject inputObject, OutputObject outputObject);

    void uploadFile(InputObject inputObject, OutputObject outputObject);

    void uploadFileBase64(InputObject inputObject, OutputObject outputObject);

    void getFileContent(HttpServletRequest request, HttpServletResponse response, String configId);

    void deleteFileByPath(InputObject inputObject, OutputObject outputObject);

    void getFilePresignedUrl(InputObject inputObject, OutputObject outputObject);

    void markdownZipUploadAndParse(InputObject inputObject, OutputObject outputObject);

    /**
     * 按指定文件存储器类型上传（storage 为空用默认；对应类型配置不存在则不上传）
     */
    void skyeyeUploadToFileStorage(InputObject inputObject, OutputObject outputObject);

    /**
     * 从指定文件配置的对象存储删除对象（供业务侧临时文件清理）
     */
    void skyeyeDeleteFromFileStorage(InputObject inputObject, OutputObject outputObject);
}
