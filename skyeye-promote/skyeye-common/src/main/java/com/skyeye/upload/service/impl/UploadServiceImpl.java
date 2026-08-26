/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.upload.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.json.JSONUtil;
import com.skyeye.cache.redis.RedisCache;
import com.skyeye.common.constans.*;
import com.skyeye.common.object.GetUserToken;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.object.PutObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.FileUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.framework.file.core.client.FileClient;
import com.skyeye.framework.file.core.client.s3.FilePresignedUrlRespDTO;
import com.skyeye.jedis.JedisClientService;
import com.skyeye.upload.entity.FileConfig;
import com.skyeye.upload.entity.Upload;
import com.skyeye.upload.entity.UploadChunks;
import com.skyeye.upload.enums.FileStorageEnum;
import com.skyeye.upload.service.FileConfigService;
import com.skyeye.upload.service.FileService;
import com.skyeye.upload.service.UploadService;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

/**
 * @ClassName: UploadServiceImpl
 * @Description: 文件上传、下载服务服务层
 * @author: skyeye云系列--卫志强
 * @date: 2022/11/28 21:39
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class UploadServiceImpl implements UploadService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UploadServiceImpl.class);

    @Autowired
    public JedisClientService jedisClient;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private FileConfigService fileConfigService;

    @Value("${IMAGES_PATH}")
    private String tPath;

    @Autowired
    private FileService fileService;

    @Value("${skyeye.tenant.enable}")
    protected boolean tenantEnable;

    /**
     * 断点续传上传文件
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void uploadFileResume(InputObject inputObject, OutputObject outputObject) {
        Upload upload = inputObject.getParams(Upload.class);
        // 将当前上下文初始化给 CommonsMutipartResolver （多部分解析器）
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(PutObject.getRequest().getSession().getServletContext());
        // 检查form中是否有enctype="multipart/form-data"
        if (!multipartResolver.isMultipart(PutObject.getRequest())) {
            return;
        }
        String userId = inputObject.getLogParams().get("id").toString();
        // 将request变成多部分request
        MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) PutObject.getRequest();
        // 获取multiRequest 中所有的文件名
        Iterator iter = multiRequest.getFileNames();
        while (iter.hasNext()) {
            String fileAddress = uploadFile(multiRequest, iter, upload.getType(), userId);
            upload.setFileType(fileAddress.substring(fileAddress.lastIndexOf(".") + 1).toLowerCase());
            upload.setFileSizeType("bytes");
            upload.setFileAddress(fileAddress);
            upload.setFileThumbnail("-1");

            String cacheKey = getCacheKey(upload.getMd5());
            List<Upload> beans = redisCache.getList(cacheKey, key -> new ArrayList<>(), RedisConstants.ONE_DAY_SECONDS, Upload.class);
            beans.add(upload);
            jedisClient.set(cacheKey, JSONUtil.toJsonStr(beans));
        }
    }

    private String uploadFile(MultipartHttpServletRequest multiRequest, Iterator iter, Integer type, String userId) {
        String basePath = tPath + FileConstants.FileUploadPath.getSavePath(type, userId);
        MultipartFile file = multiRequest.getFile(iter.next().toString());
        if (file == null) {
            throw new CustomException("file is not null.");
        }
        String fileName = file.getOriginalFilename();
        // 得到文件扩展名
        String fileExtName = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        FileUtil.createDirs(basePath);
        // 自定义的文件名称
        String newFileName = String.format(Locale.ROOT, "%s.%s", System.currentTimeMillis(), fileExtName);
        String path = basePath + "/" + newFileName;
        // 上传
        try {
            file.transferTo(new File(path));
        } catch (IOException e) {
            throw new CustomException(e);
        }
        return FileConstants.FileUploadPath.getVisitPath(type, userId) + newFileName;
    }

    /**
     * 上传文件合并块
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void uploadFileChunks(InputObject inputObject, OutputObject outputObject) {
        UploadChunks uploadChunks = inputObject.getParams(UploadChunks.class);
        String cacheKey = getCacheKey(uploadChunks.getMd5());
        List<Upload> beans = redisCache.getList(cacheKey, key -> new ArrayList<>(), RedisConstants.ONE_DAY_SECONDS, Upload.class);
        List<File> fileList = new ArrayList<>();
        for (Upload bean : beans) {
            File f = new File(tPath.replace("images", "") + bean.getFileAddress());
            fileList.add(f);
        }
        String userId = inputObject.getLogParams().get("id").toString();
        String fileName = uploadChunks.getName();
        String fileExtName = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        String newFileName = String.format(Locale.ROOT, "%s.%s", System.currentTimeMillis(), fileExtName);
        String path = tPath + FileConstants.FileUploadPath.getSavePath(uploadChunks.getType(), userId)
            + CommonCharConstants.SLASH_MARK + newFileName;
        FileChannel outChnnel = null;
        try {
            File outputFile = new File(path);
            // 创建文件
            outputFile.createNewFile();
            // 输出流
            outChnnel = new FileOutputStream(outputFile).getChannel();
            FileChannel inChannel;
            for (File file : fileList) {
                inChannel = new FileInputStream(file).getChannel();
                inChannel.transferTo(0, inChannel.size(), outChnnel);
                inChannel.close();
                // 删除分片
                file.delete();
            }
        } catch (Exception e) {
            throw new CustomException(e);
        } finally {
            FileUtil.close(outChnnel);
        }
        jedisClient.del(cacheKey);
        uploadChunks.setFileType(fileExtName);
        uploadChunks.setFileSizeType("bytes");
        newFileName = FileConstants.FileUploadPath.getVisitPath(uploadChunks.getType(), userId) + newFileName;
        uploadChunks.setFileAddress(newFileName);
        outputObject.setBean(uploadChunks);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    private String getCacheKey(String md5) {
        return String.format(Locale.ROOT, "upload:file:chunks:%s", md5);
    }

    /**
     * 文件分块上传检测是否上传
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void checkUploadFileChunks(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String md5 = map.get("md5").toString();
        String chunk = map.get("chunk").toString();
        String cacheKey = getCacheKey(md5);
        List<Upload> beans = redisCache.getList(cacheKey, key -> new ArrayList<>(), RedisConstants.ONE_DAY_SECONDS, Upload.class);
        Upload bean = null;
        int index = -1;
        for (int i = 0; i < beans.size(); i++) {
            Upload upload = beans.get(i);
            if (chunk.equals(upload.getChunk())) {
                bean = upload;
                index = i;
                break;
            }
        }
        if (bean != null) {
            String fileAddress = tPath.replace("images", "") + bean.getFileAddress();
            File checkFile = new File(fileAddress);
            String chunkSize = map.get("chunkSize").toString();
            if (checkFile.exists() && checkFile.length() == Integer.parseInt(chunkSize)) {
            } else {
                beans.remove(index);
                jedisClient.set(cacheKey, JSONUtil.toJsonStr(beans));
                outputObject.setreturnMessage("文件上传失败");
            }
        } else {
            outputObject.setreturnMessage("文件上传失败");
        }
    }

    /**
     * 上传文件
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void uploadFile(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        int type = Integer.parseInt(map.get("type").toString());
        String basePath = tPath + FileConstants.FileUploadPath.getSavePath(type);
        Map<String, Object> bean = new HashMap<>();
// TODO 上传到文件存储器待测试
//        // 上传到文件存储器
//        FileClient client = fileConfigService.getMasterFileClient();
//        if (client == null) {
//            throw new CustomException("客户端(master) 不能为空");
//        }
//        byte[] content = null;
        // 检查上传文件
        MultipartFile file = checkUploadFile();
        // 文件名称
        String fileName = file.getOriginalFilename();
        // 得到文件扩展名
        String fileExtName = fileName.substring(fileName.lastIndexOf(".") + 1);
        // 自定义的文件名称
        String newFileName = String.format(Locale.ROOT, "%s.%s", System.currentTimeMillis(), fileExtName);
        bean.put("fileExtName", fileExtName);
        bean.put("size", file.getSize());
        bean.put("fileSizeType", "bytes");
        String path = basePath + "/" + newFileName;
//            try {
//                content = IoUtil.readBytes(file.getInputStream());
//                String url = client.upload(content, path, fileExtName);
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
        FileUtil.createDirs(basePath);
        LOGGER.info("upload file type is: {}, path is: {}", type, path);
        // 上传
        try {
            file.transferTo(new File(path));
        } catch (IOException ex) {
            throw new CustomException(ex);
        }
        newFileName = FileConstants.FileUploadPath.getVisitPath(type) + newFileName;
        saveFile(file, newFileName, type);
        bean.put("picUrl", newFileName);
        bean.put("type", type);
        bean.put("fileName", fileName);

        outputObject.setBean(bean);
    }

    private void saveFile(MultipartFile multipartFile, String path, Integer fileType) {
        com.skyeye.upload.entity.File file = new com.skyeye.upload.entity.File();
//        file.setConfigId(client.getId());
        file.setName(multipartFile.getOriginalFilename());
        file.setPath(path);
        file.setUrl(path);
        file.setType(FileUtil.getMineType(multipartFile.getOriginalFilename()));
        file.setFileType(fileType);
        file.setSize(multipartFile.getSize());

        String userId = StrUtil.EMPTY;
        String userToken = GetUserToken.getUserToken(InputObject.getRequest());
        if (StrUtil.isNotEmpty(userToken)) {
            String userTokenUserId = GetUserToken.getUserTokenUserId(InputObject.getRequest());
            Boolean aBoolean = SysUserAuthConstants.exitUserLoginRedisCache(userTokenUserId);
            if (aBoolean) {
                userId = InputObject.getLogParamsStatic().get("id").toString();
            }
        }
        if (tenantEnable) {
            String tenantId = TenantContext.getTenantId();
            if (StrUtil.isNotEmpty(tenantId)) {
                fileService.createEntity(file, userId);
            }
        } else {
            fileService.createEntity(file, userId);
        }
    }

    /**
     * 上传文件Base64
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void uploadFileBase64(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        int type = Integer.parseInt(map.get("type").toString());
        String imgStr = map.get("images").toString();
        imgStr = imgStr.replaceAll("\\+", "%2B").replaceAll(" ", "+");
        String[] d = imgStr.split("base64,");
        // 上传数据是否合法
        if (d != null && d.length == 2) {
            String dataPrix = d[0];
            String data = d[1];
            if (FileUtil.checkBase64IsImage(dataPrix)) {
                try {
                    byte[] bytes = Base64.decodeBase64(data.getBytes());
                    // 决定存储路径
                    String basePath = tPath + FileConstants.FileUploadPath.getSavePath(type);
                    FileUtil.createDirs(basePath);
                    // 自定义的文件名称
                    String trueFileName = System.currentTimeMillis() + "." + FileUtil.getBase64FileTypeByPrix(dataPrix);
                    // 写入文件
                    FileUtil.writeByteToPointPath(bytes, basePath + "/" + trueFileName);
                    Map<String, Object> bean = new HashMap<>();
                    bean.put("picUrl", FileConstants.FileUploadPath.getVisitPath(type) + trueFileName);
                    bean.put("type", type);
                    outputObject.setBean(bean);
                } catch (Exception ee) {
                    LOGGER.warn("uploadFileBase64 failed. {}", ee);
                    outputObject.setreturnMessage("上传失败，数据不合法");
                }
            } else {
                outputObject.setreturnMessage("文件类型不正确，只允许上传jpg,png,jpeg格式的图片");
            }
        } else {
            outputObject.setreturnMessage("上传失败，数据不合法");
        }
    }

    @Override
    public void getFileContent(HttpServletRequest request, HttpServletResponse response, String configId) {
        // 获取请求的路径
        String path = StrUtil.subAfter(request.getRequestURI(), "/get/", false);
        if (StrUtil.isEmpty(path)) {
            throw new IllegalArgumentException("结尾的 path 路径必须传递");
        }
        // 解码，解决中文路径的问题 https://gitee.com/zhijiantianya/ruoyi-vue-pro/pulls/807/
        path = URLUtil.decode(path);

        // 读取内容
        FileClient client = fileConfigService.getFileClient(configId);
        Assert.notNull(client, "客户端({}) 不能为空", configId);
        try {
            byte[] content = client.getContent(path);
            if (content == null) {
                LOGGER.warn("[getFileContent][configId({}) path({}) 文件不存在]", configId, path);
                response.setStatus(HttpStatus.NOT_FOUND.value());
                return;
            }
            FileUtil.writeAttachment(response, path, content);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteFileByPath(InputObject inputObject, OutputObject outputObject) {
        String path = inputObject.getParams().get("path").toString();
        com.skyeye.upload.entity.File file = fileService.queryByPath(path);
        if (file == null) {
            throw new CustomException("文件不存在");
        }
        // 从文件存储器中删除
        FileClient client = fileConfigService.getFileClient(file.getConfigId());
        Assert.notNull(client, "客户端({}) 不能为空", file.getConfigId());
        try {
            client.delete(file.getPath());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 删除记录
        fileService.deleteById(file.getId());
    }

    @Override
    public void getFilePresignedUrl(InputObject inputObject, OutputObject outputObject) {
        String path = inputObject.getParams().get("path").toString();
        FileClient fileClient = fileConfigService.getMasterFileClient();
        try {
            FilePresignedUrlRespDTO presignedObjectUrl = fileClient.getPresignedObjectUrl(path);
            presignedObjectUrl.setConfigId(fileClient.getId());
            outputObject.setBean(presignedObjectUrl);
            outputObject.settotal(CommonNumConstants.NUM_ONE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 按指定文件配置/存储器类型上传。优先 configId，其次 storage，都空用默认；不存在则 uploaded=false。
     */
    @Override
    public void skyeyeUploadToFileStorage(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String configId = params.get("configId") == null ? StrUtil.EMPTY : params.get("configId").toString().trim();
        Integer storage = null;
        Object storageObj = params.get("storage");
        if (storageObj != null && StrUtil.isNotBlank(storageObj.toString())) {
            storage = Integer.parseInt(storageObj.toString());
            if (FileStorageEnum.getByStorage(storage) == null) {
                throw new CustomException("非法的文件存储器类型: " + storage);
            }
        }
        Object typeObj = params.get("type");
        if (typeObj == null || StrUtil.isBlank(typeObj.toString())) {
            throw new CustomException("文件目录类型 type 不能为空");
        }
        int type = Integer.parseInt(typeObj.toString());
        String fileName = params.get("fileName") == null ? StrUtil.EMPTY : params.get("fileName").toString();
        if (StrUtil.isBlank(fileName)) {
            throw new CustomException("fileName 不能为空");
        }
        String contentBase64 = params.get("contentBase64") == null ? StrUtil.EMPTY : params.get("contentBase64").toString();
        String localPath = params.get("localPath") == null ? StrUtil.EMPTY : params.get("localPath").toString();

        Map<String, Object> result = new HashMap<>();
        result.put("uploaded", false);

        FileConfig fileConfig;
        FileClient fileClient;
        try {
            if (StrUtil.isNotBlank(configId)) {
                fileConfig = fileConfigService.selectById(configId);
                if (fileConfig == null || StrUtil.isBlank(fileConfig.getId())) {
                    LOGGER.warn("uploadToFileStorage skip, configId not found: {}", configId);
                    outputObject.setBean(result);
                    return;
                }
            } else {
                fileConfig = fileConfigService.getFileConfigByStorage(storage);
                if (fileConfig == null) {
                    LOGGER.warn("uploadToFileStorage skip, storage not found: {}", storage);
                    outputObject.setBean(result);
                    return;
                }
            }
            fileClient = fileConfigService.getFileClient(fileConfig.getId());
        } catch (Exception e) {
            LOGGER.warn("uploadToFileStorage skip, storage unavailable: {}", e.getMessage());
            outputObject.setBean(result);
            return;
        }
        if (fileClient == null || fileConfig == null) {
            outputObject.setBean(result);
            return;
        }

        byte[] content = resolveUploadBytes(contentBase64, localPath);
        if (content == null || content.length == 0) {
            throw new CustomException("上传内容为空，请传 contentBase64 或有效的 localPath");
        }

        String objectKey = buildStorageObjectKey(type, fileName);
        String mimeType = FileUtil.getMineType(fileName);
        try {
            String url = fileClient.upload(content, objectKey, mimeType);
            saveStorageFile(fileClient.getId(), fileName, objectKey, url, mimeType, type, content.length);

            result.put("uploaded", true);
            result.put("configId", fileClient.getId());
            result.put("storage", fileConfig.getStorage());
            result.put("path", objectKey);
            result.put("url", url);
            result.put("fileName", fileName);
            result.put("size", content.length);
            // 火山 TOS：供知识库 doc/add add_type=tos 使用（bucket/objectKey）
            if (FileStorageEnum.S3.getKey().equals(fileConfig.getStorage())
                && fileConfig.getConfigMation() instanceof com.skyeye.framework.file.core.client.s3.S3FileClientConfig) {
                com.skyeye.framework.file.core.client.s3.S3FileClientConfig s3Config =
                    (com.skyeye.framework.file.core.client.s3.S3FileClientConfig) fileConfig.getConfigMation();
                if (StrUtil.containsIgnoreCase(s3Config.getEndpoint(),
                    com.skyeye.framework.file.core.client.s3.S3FileClientConfig.ENDPOINT_VOLCES)) {
                    result.put("tosPath", s3Config.getBucket() + "/" + objectKey);
                    result.put("bucket", s3Config.getBucket());
                }
            }
            outputObject.setBean(result);
            outputObject.settotal(CommonNumConstants.NUM_ONE);
        } catch (Exception e) {
            throw new CustomException("上传到文件存储器失败: " + e.getMessage());
        }
    }

    private byte[] resolveUploadBytes(String contentBase64, String localPath) {
        if (StrUtil.isNotBlank(contentBase64)) {
            return Base64.decodeBase64(contentBase64.getBytes(StandardCharsets.UTF_8));
        }
        if (StrUtil.isBlank(localPath)) {
            return null;
        }
        File file = new File(localPath);
        String canonical;
        String imagesCanonical;
        try {
            canonical = file.getCanonicalPath();
            imagesCanonical = new File(tPath).getCanonicalPath();
        } catch (IOException e) {
            throw new CustomException("localPath 无效: " + e.getMessage());
        }
        if (!canonical.startsWith(imagesCanonical)) {
            throw new CustomException("localPath 必须位于 IMAGES_PATH 目录下");
        }
        if (!file.exists() || !file.isFile()) {
            throw new CustomException("本地文件不存在: " + localPath);
        }
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new CustomException("读取本地文件失败: " + e.getMessage());
        }
    }

    private String buildStorageObjectKey(int type, String fileName) {
        String visit = FileConstants.FileUploadPath.getVisitPath(type);
        // /images/upload/wordfolder/ -> wordfolder/
        String prefix = visit;
        int idx = prefix.indexOf("/upload/");
        if (idx >= 0) {
            prefix = prefix.substring(idx + "/upload/".length());
        }
        prefix = prefix.replaceAll("^/+", "");
        if (StrUtil.isNotBlank(prefix) && !prefix.endsWith("/")) {
            prefix = prefix + "/";
        }
        String name = fileName.replaceAll("^/+", "");
        return prefix + name;
    }

    private void saveStorageFile(String configId, String fileName, String path, String url,
                                 String mimeType, Integer fileType, long size) {
        com.skyeye.upload.entity.File file = new com.skyeye.upload.entity.File();
        file.setConfigId(configId);
        file.setName(fileName);
        file.setPath(path);
        file.setUrl(url);
        file.setType(mimeType);
        file.setFileType(fileType);
        file.setSize(size);

        String userId = StrUtil.EMPTY;
        String userToken = GetUserToken.getUserToken(InputObject.getRequest());
        if (StrUtil.isNotEmpty(userToken)) {
            String userTokenUserId = GetUserToken.getUserTokenUserId(InputObject.getRequest());
            Boolean aBoolean = SysUserAuthConstants.exitUserLoginRedisCache(userTokenUserId);
            if (aBoolean) {
                userId = InputObject.getLogParamsStatic().get("id").toString();
            }
        }
        if (tenantEnable) {
            String tenantId = TenantContext.getTenantId();
            if (StrUtil.isNotEmpty(tenantId)) {
                fileService.createEntity(file, userId);
            }
        } else {
            fileService.createEntity(file, userId);
        }
    }

    private MultipartFile checkUploadFile() {
        // 初始化多部分解析器
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(PutObject.getRequest().getSession().getServletContext());
        if (!multipartResolver.isMultipart(PutObject.getRequest())) {
            throw new CustomException("请求必须为multipart/form-data");
        }
        MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) PutObject.getRequest();
        Iterator iter = multiRequest.getFileNames();
        if (!iter.hasNext()) {
            throw new CustomException("未选择文件");
        }
        MultipartFile zipFilePart = multiRequest.getFile(iter.next().toString());
        if (zipFilePart == null || zipFilePart.isEmpty()) {
            throw new CustomException("上传文件为空");
        }
        return zipFilePart;
    }


    /**
     * 上传Markdown压缩包并解析图片
     */
    @Override
    public void markdownZipUploadAndParse(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        // 检查上传文件
        MultipartFile zipFilePart = checkUploadFile();
        // 获取上传文件
        String originalZipName = zipFilePart.getOriginalFilename();
        if (originalZipName == null || !originalZipName.toLowerCase(Locale.ROOT).endsWith(".zip")) {
            throw new CustomException("仅支持zip格式");
        }
        int type = Integer.parseInt(map.get("type").toString());

        String tempZipPath = null;
        String unzipDir = null;
        try {
            // 使用系统临时目录
            String basePath = tPath + FileConstants.FileUploadPath.getSavePath(type);
            FileUtil.createDirs(basePath);
            // 保存临时文件
            tempZipPath = basePath + CommonCharConstants.SLASH_MARK + UUID.randomUUID() + ".zip";
            zipFilePart.transferTo(new File(tempZipPath));

            // 解压文件
            unzipDir = basePath + CommonCharConstants.SLASH_MARK + UUID.randomUUID();
            FileUtil.createDirs(unzipDir);
            unzip(tempZipPath, unzipDir);

            List<Map<String, Object>> resultList = processMarkdownDirectory(unzipDir, type);
            outputObject.setBeans(resultList);
            outputObject.settotal(resultList.size());
        } catch (IOException e) {
            throw new CustomException(e);
        } finally {
            // 删除临时文件
            if (StrUtil.isNotEmpty(tempZipPath)) {
                FileUtil.deleteFile(tempZipPath);
            }
            if (StrUtil.isNotEmpty(unzipDir)) {
                try {
                    FileUtils.forceDelete(new File(unzipDir));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void unzip(String zipPath, String destDir) throws IOException {
        Path destPath = new File(destDir).toPath();

        // 验证ZIP文件
        File zipFile = new File(zipPath);
        if (!zipFile.exists() || !zipFile.canRead()) {
            throw new IOException("ZIP文件不存在或无法读取: " + zipPath);
        }

        // 验证ZIP文件格式
        try (FileInputStream fis = new FileInputStream(zipFile)) {
            byte[] header = new byte[4];
            if (fis.read(header) != 4 ||
                header[0] != 0x50 || header[1] != 0x4B ||
                header[2] != 0x03 || header[3] != 0x04) {
                throw new IOException("不是有效的ZIP文件格式");
            }
        }

        // 尝试多种编码方式解压ZIP文件
        Exception lastException = null;
        Charset[] charsets = {
            StandardCharsets.UTF_8,
            StandardCharsets.ISO_8859_1,
            Charset.forName("GBK"),
            Charset.forName("GB2312"),
            Charset.forName("GB18030")
        };

        for (Charset charset : charsets) {
            try {
                unzipWithCharset(zipFile, destPath, charset);
                LOGGER.info("成功使用编码 {} 解压ZIP文件: {}", charset.name(), zipPath);
                return; // 成功解压，退出方法
            } catch (Exception e) {
                lastException = e;
                LOGGER.warn("使用编码 {} 解压ZIP文件失败: {}, 错误: {}", charset.name(), zipPath, e.getMessage());
                // 继续尝试下一个编码
            }
        }

        // 所有编码都失败，抛出最后一个异常
        throw new IOException("无法解压ZIP文件，已尝试多种编码方式: " + zipPath, lastException);
    }

    private void unzipWithCharset(File zipFile, Path destPath, Charset charset) throws IOException {
        try (InputStream fis = Files.newInputStream(zipFile.toPath());
             ZipInputStream zis = new ZipInputStream(fis, charset)) {
            ZipEntry entry;
            int entryCount = 0;
            while ((entry = zis.getNextEntry()) != null) {
                try {
                    entryCount++;
                    if (entryCount > 1000) { // 防止ZIP炸弹
                        throw new IOException("ZIP文件包含过多条目，可能存在安全风险");
                    }

                    // 安全处理文件名，避免编码问题
                    String entryName = getSafeEntryName(entry, charset);
                    Path newPath = resolveZipEntry(destPath, entryName);

                    if (entry.isDirectory()) {
                        Files.createDirectories(newPath);
                    } else {
                        Files.createDirectories(newPath.getParent());
                        Files.copy(zis, newPath, StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (Exception e) {
                    LOGGER.error("解压条目失败: " + entry.getName(), e);
                    // 不立即抛出异常，继续处理其他条目
                    continue;
                } finally {
                    zis.closeEntry();
                }
            }

            if (entryCount == 0) {
                throw new IOException("ZIP文件为空或损坏");
            }
        } catch (ZipException e) {
            throw new IOException("ZIP文件格式错误: " + e.getMessage(), e);
        }
    }

    private String getSafeEntryName(ZipEntry entry, Charset charset) {
        try {
            // 尝试获取原始字节数据
            byte[] nameBytes = entry.getName().getBytes(charset);
            // 重新解码，确保编码一致性
            return new String(nameBytes, charset);
        } catch (Exception e) {
            LOGGER.warn("处理ZIP条目名称时出现编码问题: {}, 使用原始名称", entry.getName());
            // 如果编码处理失败，返回原始名称
            return entry.getName();
        }
    }

    private Path resolveZipEntry(Path destDir, String entryName) throws IOException {
        // 清理文件名，移除或替换不安全的字符
        String safeEntryName = sanitizeFileName(entryName);
        Path normalizedPath = destDir.resolve(safeEntryName).normalize();
        if (!normalizedPath.startsWith(destDir)) {
            throw new IOException("不安全的zip条目: " + safeEntryName);
        }
        return normalizedPath;
    }

    private String sanitizeFileName(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return "unnamed_file";
        }

        // 移除或替换不安全的字符
        return fileName
            .replaceAll("[<>:\"/\\\\|?*]", CommonCharConstants.SLASH_MARK)  // 替换Windows不允许的字符
            .replaceAll("\\s+", CommonCharConstants.SLASH_MARK)             // 替换多个空格为下划线
            .trim();
    }

    /**
     * 解析图片路径，支持多种相对路径格式
     */
    private Path resolveImagePath(Path baseDir, String imagePath) {
        if (imagePath == null || imagePath.trim().isEmpty()) {
            return null;
        }

        String normalizedPath = imagePath.trim();

        // 处理Windows风格的路径分隔符
        normalizedPath = normalizedPath.replace('\\', '/');

        // 处理相对路径
        if (normalizedPath.startsWith("./")) {
            normalizedPath = normalizedPath.substring(2);
        } else if (normalizedPath.startsWith("../")) {
            // 处理上级目录，但限制层级防止路径遍历攻击
            int upLevels = 0;
            while (normalizedPath.startsWith("../")) {
                upLevels++;
                normalizedPath = normalizedPath.substring(3);
                if (upLevels > 5) { // 限制最多向上5级目录
                    LOGGER.warn("路径层级过深，可能存在安全风险: {}", imagePath);
                    return null;
                }
            }
            // 向上级目录查找
            Path currentDir = baseDir;
            for (int i = 0; i < upLevels; i++) {
                currentDir = currentDir.getParent();
                if (currentDir == null) {
                    LOGGER.warn("无法解析上级目录路径: {}", imagePath);
                    return null;
                }
            }
            return currentDir.resolve(normalizedPath).normalize();
        }

        // 直接相对路径
        Path resolvedPath = baseDir.resolve(normalizedPath).normalize();

        // 安全检查：确保解析后的路径仍在安全范围内
        if (!resolvedPath.startsWith(baseDir.getParent() == null ? baseDir : baseDir.getParent())) {
            LOGGER.warn("图片路径解析后超出安全范围: {} -> {}", imagePath, resolvedPath);
            return null;
        }

        return resolvedPath;
    }

    /**
     * 测试图片路径解析（可选，用于调试）
     */
    private void logImagePathResolution(Path baseDir, String imagePath, Path resolvedPath) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("图片路径解析: baseDir={}, imagePath={}, resolvedPath={}",
                baseDir, imagePath, resolvedPath);
        }
    }

    private List<Map<String, Object>> processMarkdownDirectory(String rootDir, int type) throws IOException {
        Path root = new File(rootDir).toPath();
        List<Map<String, Object>> results = new ArrayList<>();
        String uuid = UUID.randomUUID().toString();
        Files.walk(root)
            .filter(p -> Files.isRegularFile(p) && isMarkdownFile(p.getFileName().toString()))
            .forEach(mdPath -> {
                try {
                    String content = new String(Files.readAllBytes(mdPath), StandardCharsets.UTF_8);
                    String updated = replaceImageRefs(mdPath.getParent(), content, type, uuid);
                    Map<String, Object> one = new HashMap<>();
                    one.put("fileName", root.relativize(mdPath).toString());
                    one.put("content", updated);
                    results.add(one);
                } catch (IOException e) {
                    throw new CustomException(e);
                }
            });
        return results;
    }

    private boolean isMarkdownFile(String name) {
        String lower = name.toLowerCase(Locale.ROOT);
        return lower.endsWith(".md") || lower.endsWith(".markdown");
    }

    private String replaceImageRefs(Path baseDir, String content, int type, String uuid) throws IOException {
        // 处理 Markdown 图片: ![alt](url) - 支持相对路径
        Pattern mdImg = Pattern.compile("!\\[[^\\]]*]\\(([^)]+)\\)");
        // 处理 HTML 图片: <img src="url">
        Pattern htmlImg = Pattern.compile("<img[^>]+src=\\\"([^\\\"]+)\\\"[\\s\\S]*?>", Pattern.CASE_INSENSITIVE);

        String afterMd = replaceWithMatcher(content, mdImg, baseDir, type, uuid);
        String afterHtml = replaceWithMatcher(afterMd, htmlImg, baseDir, type, uuid);
        return afterHtml;
    }

    private String replaceWithMatcher(String text, Pattern pattern, Path baseDir, int type, String uuid) throws IOException {
        Matcher matcher = pattern.matcher(text);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String src = matcher.group(1).trim();
            String newUrl = src;

            // 处理相对路径的图片
            if (!isHttpLike(src) && !src.startsWith("data:")) {
                Path imgPath = resolveImagePath(baseDir, src);
                logImagePathResolution(baseDir, src, imgPath);

                if (imgPath != null && Files.exists(imgPath) && Files.isRegularFile(imgPath)) {
                    try {
                        newUrl = saveImageAndGetVisitUrl(imgPath.toFile(), type, uuid);
                        LOGGER.info("成功处理图片: {} -> {}", src, newUrl);
                    } catch (Exception e) {
                        LOGGER.warn("处理图片失败: {}, 错误: {}", src, e.getMessage());
                        // 图片处理失败时保持原链接
                    }
                } else {
                    LOGGER.warn("图片文件不存在: {}", imgPath);
                }
            }

            String replacement = matcher.group(0).replace(src, Matcher.quoteReplacement(newUrl));
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private boolean isHttpLike(String s) {
        if (s == null || s.trim().isEmpty()) {
            return false;
        }
        String lower = s.toLowerCase(Locale.ROOT).trim();
        return lower.startsWith("http://") ||
            lower.startsWith("https://") ||
            lower.startsWith("ftp://") ||
            lower.startsWith("//") ||  // 协议相对URL
            lower.startsWith("mailto:") ||
            lower.startsWith("tel:");
    }

    private String saveImageAndGetVisitUrl(File source, int type, String uuid) throws IOException {
        String originalName = source.getName();
        String ext = originalName.contains(".") ? originalName.substring(originalName.lastIndexOf('.') + 1) : "";
        String basePath = tPath + FileConstants.FileUploadPath.getSavePath(type) + CommonCharConstants.SLASH_MARK + uuid;
        FileUtil.createDirs(basePath);
        String newFileName = String.format(Locale.ROOT, "%s.%s", System.currentTimeMillis(), ext);
        String targetPath = basePath + "/" + newFileName;
        Files.copy(source.toPath(), new java.io.File(targetPath).toPath(), StandardCopyOption.REPLACE_EXISTING);

        String visitPath = FileConstants.FileUploadPath.getVisitPath(type) + uuid + CommonCharConstants.SLASH_MARK + newFileName;
        // 记录文件
        savePhysicalFileRecord(originalName, visitPath, type, source.length());
        return visitPath;
    }

    private void savePhysicalFileRecord(String originalName, String visitPath, Integer fileType, long size) {
        com.skyeye.upload.entity.File file = new com.skyeye.upload.entity.File();
        file.setName(originalName);
        file.setPath(visitPath);
        file.setUrl(visitPath);
        file.setType(FileUtil.getMineType(originalName));
        file.setFileType(fileType);
        file.setSize(size);

        String userId = StrUtil.EMPTY;
        String userToken = GetUserToken.getUserToken(InputObject.getRequest());
        if (StrUtil.isNotEmpty(userToken)) {
            String userTokenUserId = GetUserToken.getUserTokenUserId(InputObject.getRequest());
            Boolean aBoolean = SysUserAuthConstants.exitUserLoginRedisCache(userTokenUserId);
            if (aBoolean) {
                userId = InputObject.getLogParamsStatic().get("id").toString();
            }
        }
        fileService.createEntity(file, userId);
    }

}
