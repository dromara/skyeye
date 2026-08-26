/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.upload.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.upload.entity.Upload;
import com.skyeye.upload.entity.UploadChunks;
import com.skyeye.upload.enums.FileStorageEnum;
import com.skyeye.upload.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName: UploadController
 * @Description: 文件上传、下载控制类
 * @author: skyeye云系列--卫志强
 * @date: 2022/11/28 21:39
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "文件上传、下载", tags = "文件上传、下载", modelName = "基础模块")
public class UploadController {

    @Autowired
    private UploadService uploadService;

    @ApiOperation(id = "skyeyeUploadFile", value = "断点续传上传文件", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = Upload.class)
    @RequestMapping("/post/UploadController/uploadFileResume")
    public void uploadFileResume(InputObject inputObject, OutputObject outputObject) {
        uploadService.uploadFileResume(inputObject, outputObject);
    }

    @ApiOperation(id = "skyeyeUploadFileChunks", value = "上传文件合并块", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = UploadChunks.class)
    @RequestMapping("/post/UploadController/uploadFileChunks")
    public void uploadFileChunks(InputObject inputObject, OutputObject outputObject) {
        uploadService.uploadFileChunks(inputObject, outputObject);
    }

    @ApiOperation(id = "checkUploadFileChunks", value = "文件分块上传检测是否上传", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "md5", name = "md5", value = "文件唯一标示", required = "required"),
        @ApiImplicitParam(id = "chunk", name = "chunk", value = "分块上传的块下标", required = "required"),
        @ApiImplicitParam(id = "chunkSize", name = "chunkSize", value = "分块上传时，块的大小，用于最后合并", required = "required")})
    @RequestMapping("/post/UploadController/checkUploadFileChunks")
    public void checkUploadFileChunks(InputObject inputObject, OutputObject outputObject) {
        uploadService.checkUploadFileChunks(inputObject, outputObject);
    }

    @ApiOperation(id = "common003", value = "上传文件", method = "POST", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "type", name = "type", value = "文件类型", required = "required,num")})
    @RequestMapping("/post/UploadController/uploadFile")
    public void uploadFile(InputObject inputObject, OutputObject outputObject) {
        uploadService.uploadFile(inputObject, outputObject);
    }

    @ApiOperation(id = "markdownZipUploadAndParse", value = "上传Markdown压缩包并解析图片", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "type", name = "type", value = "文件类型", required = "required,num")})
    @RequestMapping("/post/UploadController/markdownZipUploadAndParse")
    public void markdownZipUploadAndParse(InputObject inputObject, OutputObject outputObject) {
        uploadService.markdownZipUploadAndParse(inputObject, outputObject);
    }

    @ApiOperation(id = "common004", value = "上传文件Base64", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "type", name = "type", value = "文件类型", required = "required,num"),
        @ApiImplicitParam(id = "images", name = "images", value = "图片Base64", required = "required")})
    @RequestMapping("/post/UploadController/uploadFileBase64")
    public void uploadFileBase64(InputObject inputObject, OutputObject outputObject) {
        uploadService.uploadFileBase64(inputObject, outputObject);
    }

    /**
     * 文件下载
     *
     * @param request
     * @param response
     * @param configId
     */
    @GetMapping("/upload/{configId}/get/**")
    public void getFileContent(HttpServletRequest request,
                               HttpServletResponse response,
                               @PathVariable("configId") String configId) {
        uploadService.getFileContent(request, response, configId);
    }

    @ApiOperation(id = "deleteFileByPath", value = "删除文件", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "path", name = "path", value = "文件路径", required = "required")})
    @RequestMapping("/post/UploadController/deleteFileByPath")
    public void deleteFileByPath(InputObject inputObject, OutputObject outputObject) {
        uploadService.deleteFileByPath(inputObject, outputObject);
    }

    @ApiOperation(id = "getFilePresignedUrl", value = "获取文件预签名地址，模式二：前端上传文件：用于前端直接上传七牛、阿里云 OSS 等文件存储器", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "path", name = "path", value = "文件路径", required = "required")})
    @RequestMapping("/post/UploadController/getFilePresignedUrl")
    public void getFilePresignedUrl(InputObject inputObject, OutputObject outputObject) {
        uploadService.getFilePresignedUrl(inputObject, outputObject);
    }

    @ApiOperation(id = "skyeyeUploadToFileStorage", value = "按指定文件配置/存储器上传（供业务侧如 AI 知识库同步调用；不存在则跳过）", method = "POST", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "configId", name = "configId", value = "文件配置ID（优先）；指定后忽略 storage"),
        @ApiImplicitParam(id = "storage", name = "storage", value = "存储器类型，见 FileStorageEnum（如 20=S3）；configId 为空时生效；都空则用默认", enumClass = FileStorageEnum.class),
        @ApiImplicitParam(id = "type", name = "type", value = "文件目录类型", required = "required,num"),
        @ApiImplicitParam(id = "fileName", name = "fileName", value = "文件名（含后缀）", required = "required"),
        @ApiImplicitParam(id = "contentBase64", name = "contentBase64", value = "文件内容 Base64，与 localPath 二选一"),
        @ApiImplicitParam(id = "localPath", name = "localPath", value = "本机绝对路径（需在 IMAGES_PATH 下），与 contentBase64 二选一")})
    @RequestMapping("/post/UploadController/skyeyeUploadToFileStorage")
    public void skyeyeUploadToFileStorage(InputObject inputObject, OutputObject outputObject) {
        uploadService.skyeyeUploadToFileStorage(inputObject, outputObject);
    }

}
