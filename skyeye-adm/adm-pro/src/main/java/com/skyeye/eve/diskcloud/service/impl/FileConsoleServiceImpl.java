/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.diskcloud.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.constans.FileConstants;
import com.skyeye.common.enumeration.DeleteFlagEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.object.PutObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.BytesUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.FileUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.diskcloud.classenum.DefaultFolder;
import com.skyeye.eve.diskcloud.classenum.DickCloudType;
import com.skyeye.eve.diskcloud.classenum.FileType;
import com.skyeye.eve.diskcloud.classenum.FolderType;
import com.skyeye.eve.diskcloud.dao.FileConsoleDao;
import com.skyeye.eve.diskcloud.entity.FileCatalog;
import com.skyeye.eve.diskcloud.entity.FileConsole;
import com.skyeye.eve.diskcloud.service.FileCatalogService;
import com.skyeye.eve.diskcloud.service.FileConsoleService;
import com.skyeye.exception.CustomException;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.imageio.stream.FileImageOutputStream;
import java.io.*;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * @ClassName: FileConsoleServiceImpl
 * @Description: 文件管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:21
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "文件管理", groupName = "文件管理")
public class FileConsoleServiceImpl extends SkyeyeBusinessServiceImpl<FileConsoleDao, FileConsole> implements FileConsoleService {

    @Autowired
    private FileCatalogService fileCatalogService;

    @Autowired
    private FileConsoleDao fileConsoleDao;

    /**
     * 文件上传时保存文件的路径
     */
    private static final Integer FILE_PATH_TYPE = FileConstants.FileUploadPath.FILE_CONSOLE.getType()[0];

    @Value("${IMAGES_PATH}")
    private String tPath;

    @Value("${server.port}")
    private String sysPort;

    private static final Logger LOGGER = LoggerFactory.getLogger(FileConsoleServiceImpl.class);

    /**
     * 删除文件
     *
     * @param fileAddress   文件地址
     * @param fileThumbnail 文件缩略图地址
     * @param fileType      文件类型
     */
    public void deleteFileByMation(String fileAddress, String fileThumbnail, String fileType) {
        FileUtil.deleteFile(fileAddress);
        FileUtil.deleteFile(fileThumbnail);//删除缩略图
        if (FileType.judgeIsAllowedFileType(fileType, 5)) {//ace文件
            FileUtil.deleteFile(fileAddress.substring(0, fileAddress.lastIndexOf(".")) + ".pdf");//删除ace转换文件
        }
    }

    /**
     * 根据当前用户获取目录
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryFileFolderByUserId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        // 父目录id
        String parentId = map.get("parentId").toString();
        if (ToolUtil.isBlank(parentId) || "0".equals(parentId)) {
            // 加载一级目录
            List<Map<String, Object>> beans = DefaultFolder.getFileConsoleISDefaultFolder();
            outputObject.setBeans(beans);
        } else {
            // 加载子目录
            String userId = inputObject.getLogParams().get("id").toString();
            map.put("folderType", this.getFolderType(parentId));
            map.put("userId", userId);
            map.put("deleteFlag", DeleteFlagEnum.NOT_DELETE.getKey());
            String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
            map.put("tenantId", tenantId);
            List<Map<String, Object>> beans = fileConsoleDao.queryFileFolderByUserIdAndParentId(map);
            outputObject.setBeans(beans);
        }
    }

    private Integer getFolderType(String folderId) {
        if (DefaultFolder.SKYDRIVE.getKey().equals(folderId)) {
            // 企业网盘
            return FolderType.COMPANY.getKey();
        } else {
            FileCatalog fileCatalog = fileCatalogService.selectById(folderId);
            if (ObjectUtil.isNotEmpty(fileCatalog) && StrUtil.isNotEmpty(fileCatalog.getId())) {
                if (fileCatalog.getParentId().indexOf("3,") == 0) {
                    // 企业网盘
                    return FolderType.COMPANY.getKey();
                } else {
                    // 私人
                    return FolderType.PERSONER.getKey();
                }
            } else {
                // 私人
                return FolderType.PERSONER.getKey();
            }
        }
    }

    /**
     * 获取这个目录下的所有文件+目录
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @IgnoreTenant
    public void queryFilesListByFolderId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> user = inputObject.getLogParams();
        map.put("userId", user.get("id"));
        String folderId = map.get("folderId").toString();
        map.put("folderType", this.getFolderType(folderId));
        map.put("deleteFlag", DeleteFlagEnum.NOT_DELETE.getKey());
        this.setOrderByParams(map);
        if (tenantEnable) {
            // 多租户模式
            map.put("tenantId", TenantContext.getTenantId());
        }
        List<Map<String, Object>> beans = fileConsoleDao.queryFilesListByFolderId(map);
        iAuthUserService.setNameForMap(beans, "createId", "createName");
        for (Map<String, Object> bean : beans) {
            if (!DickCloudType.FOLDER.getKey().equals(bean.get("type").toString())) {
                // 不是文件夹
                String size = BytesUtil.sizeFormatNum2String(Long.parseLong(bean.get("size").toString()));
                bean.put("size", size);
            }
        }
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

    /**
     * 设置排序字段
     *
     * @param map
     */
    private void setOrderByParams(Map<String, Object> map) {
        String orderBy = map.get("orderBy").toString();
        if (ToolUtil.isBlank(orderBy)) {
            map.put("orderByStr", " k.orderBy ASC, k.`name` ASC");
        } else {
            if ("1".equals(orderBy)) {
                map.put("orderByStr", " k.`name` ASC");
            } else if ("2".equals(orderBy)) {
                map.put("orderByStr", " k.createTime DESC");
            } else if ("3".equals(orderBy)) {
                map.put("orderByStr", " k.orderBy ASC, k.`name` ASC");
            } else if ("4".equals(orderBy)) {
                map.put("orderByStr", " k.size DESC");
            }
        }
        map.remove("orderBy");
    }

    /**
     * 删除目录以及目录下的所有文件
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void deleteFileFolderById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        // 获取要删除的文件
        String fileList = map.get("fileList").toString();
        List<Map<String, Object>> array = JSONUtil.toList(fileList, null);
        // 文件访问基础路径
        String basePath = tPath.replace(FileConstants.FILE_BASE_FIRST_PATH, StrUtil.EMPTY);
        for (int i = 0; i < array.size(); i++) {
            // 获取id和fileType
            String id = array.get(i).get("id").toString();
            String fileType = array.get(i).get("fileType").toString();
            if (DickCloudType.FOLDER.getKey().equals(fileType)) {
                // 删除自身目录
                fileCatalogService.deleteById(id);
                // 目录下的子文件
                List<FileConsole> files = queryByParentId(id);
                for (FileConsole file : files) {
                    // 删除文件
                    deleteFileByMation(basePath + file.getAddress(), basePath + file.getThumbnail(), file.getType());
                }
                // 删除子文件
                deleteByParentId(id);
                List<String> childIds = files.stream().map(FileConsole::getId).collect(Collectors.toList());
                refreshCache(childIds);
                // 删除子文件夹
                fileCatalogService.deleteByParentId(id);
            } else {
                // 操作文件表
                FileConsole fileConsole = selectById(id);
                if (ObjectUtil.isNotEmpty(fileConsole) && StrUtil.isNotEmpty(fileConsole.getId())) {
                    // 删除文件
                    deleteFileByMation(basePath + fileConsole.getAddress(), basePath + fileConsole.getThumbnail(), fileConsole.getType());
                    deleteById(id);
                }
            }
        }
    }

    private List<FileConsole> queryByParentId(String parentId) {
        QueryWrapper<FileConsole> queryWrapper = new QueryWrapper<>();
        queryWrapper.apply("INSTR(CONCAT(',', " + MybatisPlusUtil.toColumns(FileConsole::getParentId) + ", ','), CONCAT(',', {0}, ','))", parentId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(FileConsole::getDeleteFlag), DeleteFlagEnum.NOT_DELETE.getKey());
        return list(queryWrapper);
    }

    private void deleteByParentId(String parentId) {
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        UpdateWrapper<FileConsole> updateWrapper = new UpdateWrapper<>();
        updateWrapper.apply("INSTR(CONCAT(',', " + MybatisPlusUtil.toColumns(FileConsole::getParentId) + ", ','), CONCAT(',', {0}, ','))", parentId);
        updateWrapper.set(MybatisPlusUtil.toColumns(FileConsole::getDeleteFlag), DeleteFlagEnum.DELETED.getKey());
        updateWrapper.set(MybatisPlusUtil.toColumns(FileConsole::getLastUpdateId), userId);
        updateWrapper.set(MybatisPlusUtil.toColumns(FileConsole::getLastUpdateTime), DateUtil.getTimeAndToString());
        update(updateWrapper);
    }

    /**
     * 编辑目录名称
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void editFileFolderById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        String name = map.get("name").toString();
        String fileType = map.get("fileType").toString();
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        if (DickCloudType.FOLDER.getKey().equals(fileType)) {
            // 操作目录表
            fileCatalogService.editNameById(id, name, userId);
        } else {
            //操作文件表
            editNameById(id, name, userId);
        }
    }

    @Override
    public void editNameById(String id, String name, String userId) {
        UpdateWrapper<FileConsole> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(FileConsole::getName), name);
        updateWrapper.set(MybatisPlusUtil.toColumns(FileConsole::getLastUpdateId), userId);
        updateWrapper.set(MybatisPlusUtil.toColumns(FileConsole::getLastUpdateTime), DateUtil.getTimeAndToString());
        update(updateWrapper);
        refreshCache(id);
    }

    /**
     * 上传文件
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void insertUploadFile(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        // 将当前上下文初始化给 CommonsMutipartResolver （多部分解析器）
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(PutObject.getRequest().getSession().getServletContext());
        // 检查form中是否有enctype="multipart/form-data"
        if (multipartResolver.isMultipart(PutObject.getRequest())) {
            Map<String, Object> user = inputObject.getLogParams();
            String userId = user.get("id").toString();
            // 将request变成多部分request
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) PutObject.getRequest();
            // 获取multiRequest 中所有的文件名
            Iterator iter = multiRequest.getFileNames();
            String basePath = tPath + FileConstants.FileUploadPath.getSavePath(FILE_PATH_TYPE) + "/" + userId;
            while (iter.hasNext()) {
                // 一次遍历所有文件
                MultipartFile file = multiRequest.getFile(iter.next().toString());
                String fileName = file.getOriginalFilename();// 文件名称
                //得到文件扩展名
                String fileExtName = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
                if (file != null) {
                    FileUtil.createDirs(basePath);
                    // 自定义的文件名称
                    String newFileName = System.currentTimeMillis() + "." + fileExtName;
                    String path = basePath + "/" + newFileName;
                    // 上传
                    try {
                        file.transferTo(new File(path));
                    } catch (IOException e) {
                        throw new CustomException(e);
                    }
                    FileConsole fileConsole = new FileConsole();
                    fileConsole.setName(map.get("name").toString());
                    fileConsole.setSize(Integer.parseInt(map.get("size").toString()));
                    fileConsole.setParentId(fileCatalogService.setParentId(map.get("folderId").toString()));
                    fileConsole.setFileMd5(map.get("md5").toString());
                    fileConsole.setChunk(Integer.parseInt(map.get("chunk").toString()));
                    fileConsole.setChunkSize(map.get("chunkSize").toString());
                    fileConsole.setType(fileExtName);
                    fileConsole.setSizeType("bytes");
                    String trueFileName = FileConstants.FileUploadPath.getVisitPath(FILE_PATH_TYPE) + userId + "/" + newFileName;
                    fileConsole.setAddress(trueFileName);
                    fileConsole.setThumbnail("-1");
                    createEntity(fileConsole, userId);
                }
            }
        }
    }

    /**
     * 上传文件合并块
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void insertUploadFileChunks(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String md5 = map.get("md5").toString();
        String userId = inputObject.getLogParams().get("id").toString();
        List<FileConsole> fileConsoleList = queryFileByMd5(md5);
        List<File> fileList = getFileList(fileConsoleList);
        String name = map.get("name").toString();
        // 文件后缀
        String fileExtName = name.substring(name.lastIndexOf(".") + 1).toLowerCase();
        // 新文件名
        String newFileName = System.currentTimeMillis() + "." + fileExtName;
        String basePath = tPath + FileConstants.FileUploadPath.getSavePath(FILE_PATH_TYPE);
        // 文件路径
        String path = basePath + "/" + userId + "/" + newFileName;
        FileChannel outChnnel = null;
        try {
            File outputFile = new File(path);
            // 创建文件
            outputFile.createNewFile();
            // 输出流
            outChnnel = new FileOutputStream(outputFile).getChannel();
            // 合并
            FileChannel inChannel;
            for (File file : fileList) {
                inChannel = new FileInputStream(file).getChannel();
                inChannel.transferTo(0, inChannel.size(), outChnnel);
                inChannel.close();
                // 删除分片
                file.delete();
            }
        } catch (Exception ee) {
            throw new CustomException(ee);
        } finally {
            FileUtil.close(outChnnel);
        }
        deleteByMd5(md5);
        refreshCache(fileConsoleList.stream().map(FileConsole::getId).collect(Collectors.toList()));

        FileConsole fileConsole = new FileConsole();
        fileConsole.setName(name);
        fileConsole.setSize(Integer.parseInt(map.get("size").toString()));
        fileConsole.setParentId(fileCatalogService.setParentId(map.get("folderId").toString()));
        fileConsole.setChunkSize(map.get("size").toString());
        fileConsole.setType(fileExtName);
        fileConsole.setSizeType("bytes");
        String trueFileName = FileConstants.FileUploadPath.getVisitPath(FILE_PATH_TYPE) + userId + "/" + newFileName;
        fileConsole.setAddress(trueFileName);
        fileConsole.setThumbnail(getThumbnail(userId, fileExtName, basePath, path, trueFileName));
        createEntity(fileConsole, userId);
    }

    private String getThumbnail(String userId, String fileExtName, String basePath, String path, String trueFileName) {
        if (FileType.judgeIsAllowedFileType(fileExtName, CommonNumConstants.NUM_ONE)) {
            // 图片
            return trueFileName;
        } else if (FileType.judgeIsAllowedFileType(fileExtName, CommonNumConstants.NUM_SIX)) {
            // 电子书
            String picName = System.currentTimeMillis() + ".jpg";
            String newFilename = basePath + "/" + userId + "/" + picName;
            writeAndReadQpubFileThumbnail(path, newFilename);
            return FileConstants.FileUploadPath.getVisitPath(FILE_PATH_TYPE) + userId + "/" + picName;
        } else if (FileType.judgeIsAllowedFileType(fileExtName, CommonNumConstants.NUM_TWO)) {
            // office文件缩略图地址
            return FileType.getIconByFileExt(fileExtName);
        } else if (FileType.judgeIsAllowedFileType(fileExtName, CommonNumConstants.NUM_FIVE)) {
            // ace文件缩略图地址
            return FileType.getIconByFileExt(fileExtName);
        } else if (FileType.judgeIsAllowedFileType(fileExtName, CommonNumConstants.NUM_THREE)) {
            // 视频
            String ffmpegGPath = tPath + "/util/ffmpeg.exe";
            String fileThumbnail = System.currentTimeMillis() + ".jpg";
            FileUtil.createDirs(basePath + "/" + userId + "/ffmpeg/");
            if (ToolUtil.take(path, basePath + "/" + userId + "/ffmpeg/" + fileThumbnail, ffmpegGPath)) {
                return FileConstants.FileUploadPath.getVisitPath(FILE_PATH_TYPE) + userId + "/ffmpeg/" + fileThumbnail;
            } else {
                FileUtil.deleteFile(path);
                throw new CustomException("上传失败。");
            }
        } else if (FileType.judgeIsAllowedFileType(fileExtName, CommonNumConstants.NUM_FOUR)) {
            // 压缩包
            return "../../assets/images/rar.png";
        } else {
            // 其他
            return "../../assets/images/cloud/other-icon.png";
        }
    }

    private List<FileConsole> queryFileByMd5(String md5) {
        QueryWrapper<FileConsole> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(FileConsole::getFileMd5), md5);
        queryWrapper.eq(MybatisPlusUtil.toColumns(FileConsole::getDeleteFlag), DeleteFlagEnum.NOT_DELETE.getKey());
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(FileConsole::getChunk));
        return list(queryWrapper);
    }

    private List<File> getFileList(List<FileConsole> fileConsoles) {
        List<File> fileList = new ArrayList<>();
        for (FileConsole bean : fileConsoles) {
            File f = new File(tPath.replace(FileConstants.FILE_BASE_FIRST_PATH, StrUtil.EMPTY) + bean.getAddress());
            fileList.add(f);
        }
        return fileList;
    }

    private void deleteByMd5(String md5) {
        QueryWrapper<FileConsole> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(FileConsole::getFileMd5), md5);
        remove(queryWrapper);
    }

    /**
     * 文件分块上传检测是否上传
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void queryUploadFileChunksByChunkMd5(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String md5 = map.get("md5").toString();
        String chunk = map.get("chunk").toString();
        // 根据块进行查询
        QueryWrapper<FileConsole> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(FileConsole::getFileMd5), md5);
        queryWrapper.eq(MybatisPlusUtil.toColumns(FileConsole::getChunk), chunk);
        FileConsole fileConsole = getOne(queryWrapper, false);
        if (ObjectUtil.isNotEmpty(fileConsole) && StrUtil.isNotEmpty(fileConsole.getId())) {
            String fileAddress = tPath.replace(FileConstants.FILE_BASE_FIRST_PATH, StrUtil.EMPTY) + fileConsole.getAddress();
            File checkFile = new File(fileAddress);
            String chunkSize = map.get("chunkSize").toString();
            if (checkFile.exists() && checkFile.length() == Integer.parseInt(chunkSize)) {
            } else {
                // 删除块
                QueryWrapper<FileConsole> queryWrapper1 = new QueryWrapper<>();
                queryWrapper1.eq(MybatisPlusUtil.toColumns(FileConsole::getFileMd5), md5);
                queryWrapper1.eq(MybatisPlusUtil.toColumns(FileConsole::getChunk), chunk);
                remove(queryWrapper1);
                outputObject.setreturnMessage(String.format(Locale.ROOT, "MD5:%s,分块:%s不存在", md5, chunk));
            }
        } else {
            outputObject.setreturnMessage(String.format(Locale.ROOT, "MD5:%s,分块:%s不存在", md5, chunk));
        }
    }

    /**
     * office文件编辑
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void editUploadOfficeFileById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        int status = Integer.parseInt(map.get("status").toString());
        // 当我们关闭编辑窗口后，十秒钟左右onlyoffice会将它存储的我们的编辑后的文件，，此时status = 2
        if (status == 2 || status == 3) {//MustSave, Corrupted
            String id = map.get("key").toString().split("-")[0];
            try {
                // 新文件地址
                URL url = new URL(map.get("url").toString());
                java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
                InputStream stream = connection.getInputStream();
                if (stream == null) {
                    outputObject.setreturnMessage("Stream is null");
                    return;
                }
                // 从请求中获取要覆盖的文件参数定义"path"
                FileConsole fileConsole = selectById(id);
                String fileAddress = tPath.replace(FileConstants.FILE_BASE_FIRST_PATH, StrUtil.EMPTY) + fileConsole.getAddress();
                File savedFile = new File(fileAddress);
                try (FileOutputStream out = new FileOutputStream(savedFile)) {
                    int read;
                    final byte[] bytes = new byte[1024];
                    while ((read = stream.read(bytes)) != -1) {
                        out.write(bytes, 0, read);
                    }
                    out.flush();
                }

                UpdateWrapper<FileConsole> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq(CommonConstants.ID, id);
                updateWrapper.set(MybatisPlusUtil.toColumns(FileConsole::getLastUpdateTime), DateUtil.getTimeAndToString());
                update(updateWrapper);
                refreshCache(id);

                connection.disconnect();
                outputObject.setErroCode(1);
            } catch (Exception e) {
                throw new CustomException(e);
            }
        }
    }

    /**
     * 根据当前用户获取总文件大小
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryAllFileSizeByUserId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String userId = inputObject.getLogParams().get("id").toString();

        // 查询当前登陆人的【我的文档】下的所有未删除的文件
        QueryWrapper<FileConsole> queryWrapper = new QueryWrapper<>();
        queryWrapper.apply("INSTR(CONCAT(',', " + MybatisPlusUtil.toColumns(FileConsole::getParentId) + ", ','), CONCAT(',', {0}, ','))", DefaultFolder.FOLDER.getKey());
        queryWrapper.eq(MybatisPlusUtil.toColumns(FileConsole::getDeleteFlag), DeleteFlagEnum.NOT_DELETE.getKey());
        queryWrapper.eq(MybatisPlusUtil.toColumns(FileConsole::getCreateId), userId);
        List<FileConsole> fileConsoles = list(queryWrapper);

        if (CollectionUtil.isNotEmpty(fileConsoles)) {
            long sum = fileConsoles.stream().mapToLong(FileConsole::getSize).sum();
            String size = BytesUtil.sizeFormatNum2String(sum);
            map.put("size", size);
        } else {
            map.clear();
            map.put("size", "0KB");
        }
        outputObject.setBean(map);
    }

    /**
     * 分享文件保存
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void insertShareFileListToSave(InputObject inputObject, OutputObject outputObject) {
        setCopyFileMation(inputObject);
    }

    private void setCopyFileMation(InputObject inputObject) {
        Map<String, Object> map = inputObject.getParams();
        List<Map<String, Object>> array = JSONUtil.toList(map.get("jsonStr").toString(), null);//获取数据信息
        String userId = inputObject.getLogParams().get("id").toString();
        String folderId = fileCatalogService.setParentId(map.get("folderId").toString());
        String basePath = tPath + FileConstants.FileUploadPath.getSavePath(FILE_PATH_TYPE) + "/" + userId;
        String visitPath = FileConstants.FileUploadPath.getVisitPath(FILE_PATH_TYPE) + userId;
        List<String> folderBeans = new ArrayList<>();
        List<String> fileBeans = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            Map<String, Object> object = array.get(i);
            if (DickCloudType.FOLDER.getKey().equals(object.get("rowType").toString())) {//文件夹
                folderBeans.add(object.get("rowId").toString());
            } else {
                fileBeans.add(object.get("rowId").toString());
            }
        }
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        if (!folderBeans.isEmpty()) {//选择保存的文件夹不为空
            List<Map<String, Object>> folderNew = fileCatalogService.queryFolderAndChildList(folderBeans);
            List<Map<String, Object>> fileNew = skyeyeBaseMapper.queryChildFileListByFolder(folderNew, DeleteFlagEnum.NOT_DELETE.getKey(), tenantId);
            for (Map<String, Object> folder : folderNew) {//重置父id
                String[] str = folder.get("parentId").toString().split(",");
                folder.put("directParentId", str[str.length - 1]);
                folder.put("newId", ToolUtil.getSurFaceId());
            }
            //将数据转化为树的形式，方便进行父id重新赋值
            folderNew = ToolUtil.listToTree(folderNew, "id", "directParentId", "children");
            ToolUtil.FileListParentISEdit(folderNew, folderId);//替换父id
            folderNew = ToolUtil.FileTreeTransList(folderNew);//将树转为list
            for (Map<String, Object> folder : folderNew) {
                folder.put("createId", userId);
                folder.put("createTime", DateUtil.getTimeAndToString());
            }
            //为文件重置新parentId参数
            for (Map<String, Object> folder : folderNew) {
                String parentId = folder.get("parentId").toString() + folder.get("id").toString() + ",";
                String newParentId = folder.get("newParentId").toString() + folder.get("newId").toString() + ",";
                for (Map<String, Object> file : fileNew) {
                    if (parentId.equals(file.get("parentId").toString())) {
                        file.put("newParentId", newParentId);
                    }
                }
            }
            //为文件重置新参数
            for (Map<String, Object> file : fileNew) {
                setCopyFileMation(userId, basePath, visitPath, file);
            }
            if (!folderNew.isEmpty()) {
                fileConsoleDao.insertFolderList(folderNew, tenantId);
            }
            if (!fileNew.isEmpty()) {
                fileConsoleDao.insertShareFileListByList(fileNew, tenantId);
            }
        }
        if (!fileBeans.isEmpty()) {//选择保存的文件不为空
            List<Map<String, Object>> fileNew = fileConsoleDao.queryShareFileListByFileList(fileBeans, DeleteFlagEnum.NOT_DELETE.getKey(), tenantId);
            //为文件重置新参数
            for (Map<String, Object> file : fileNew) {
                file.put("newParentId", folderId);
                setCopyFileMation(userId, basePath, visitPath, file);
            }
            fileConsoleDao.insertShareFileListByList(fileNew, tenantId);
        }
    }

    private void setCopyFileMation(String userId, String basePath, String visitPath, Map<String, Object> file) {
        file.put("newId", ToolUtil.getSurFaceId());
        file.put("createId", userId);
        file.put("createTime", DateUtil.getTimeAndToString());
        String fileExtName = file.get("type").toString().toLowerCase();
        String newFileName = System.currentTimeMillis() + "." + fileExtName;//新文件名
        String path = basePath + "/" + newFileName;//文件新路径
        String oldPath = tPath.replace(FileConstants.FILE_BASE_FIRST_PATH, StrUtil.EMPTY) + file.get("address").toString();//原始路径
        String trueFileName = visitPath + "/" + newFileName;//数据库存储路径
        file.put("address", trueFileName);
        if (FileType.judgeIsAllowedFileType(fileExtName, 1)) {//图片
            file.put("thumbnail", trueFileName);//缩略图
        } else if (FileType.judgeIsAllowedFileType(fileExtName, 6)) {//电子书
            String oldFileThumbnail = tPath.replace(FileConstants.FILE_BASE_FIRST_PATH, StrUtil.EMPTY) + file.get("thumbnail").toString();
            String fileThunbnailName = String.valueOf(System.currentTimeMillis());
            String fileThumbnailpath = basePath + "/" + fileThunbnailName + ".png";
            file.put("thumbnail", visitPath + "/" + fileThunbnailName + ".png");//缩略图
            ToolUtil.NIOCopyFile(oldFileThumbnail, fileThumbnailpath);
        } else if (FileType.judgeIsAllowedFileType(fileExtName, 3)) {//视频
            String oldFileThumbnail = tPath.replace(FileConstants.FILE_BASE_FIRST_PATH, StrUtil.EMPTY) + file.get("thumbnail").toString();
            String fileThunbnailName = String.valueOf(System.currentTimeMillis());
            String fileThumbnailpath = basePath + "/ffmpeg/" + fileThunbnailName + ".png";//缩略图新路径
            file.put("thumbnail", visitPath + "/ffmpeg/" + fileThunbnailName + ".png");//缩略图
            ToolUtil.NIOCopyFile(oldFileThumbnail, fileThumbnailpath);
        }
        ToolUtil.NIOCopyFile(oldPath, path);
    }

    /**
     * 文档在线预览
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryFileToShowById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        FileConsole fileConsole = selectById(id);
        String fileType = fileConsole.getType();
        String filePath = tPath.replace(FileConstants.FILE_BASE_FIRST_PATH, StrUtil.EMPTY) + fileConsole.getAddress();//文件路径

        if ("txt".equals(fileType)) {//ace文件
            File docFile = new File(filePath);
            File pdfFile;
            if (filePath.contains(".")) {
                pdfFile = new File(filePath.substring(0, filePath.lastIndexOf(".")) + ".pdf");
            } else {
                pdfFile = new File(filePath + ".pdf");
            }
            /*判断即将要转换的文件是否真实存在*/
            if (docFile.exists()) {
                /*判断改文件是否已经被转换过,若已经转换则直接预览*/
                if (!pdfFile.exists()) {
                    /*打开OpenOffice连接,*/
                    OpenOfficeConnection connection = new SocketOpenOfficeConnection(Integer.parseInt(sysPort));
                    try {
                        connection.connect();
                        DocumentConverter converter = new OpenOfficeDocumentConverter(connection);
                        converter.convert(docFile, pdfFile);
                        connection.disconnect();
                        filePath = pdfFile.getPath(); //文件转换之后的路径
                        PutObject.getResponse().setContentType("application/pdf");
                    } catch (Exception e) {
                        LOGGER.warn("connection failed, message is {}", e);
                    } finally {
                        // 发生exception时, connection不会自动切断, 程序会一直挂着
                        try {
                            if (connection != null) {
                                connection.disconnect();
                            }
                        } catch (Exception e) {
                            LOGGER.warn("close connection failed, message is {}", e);
                        }
                    }
                } else {
                    filePath = pdfFile.getPath(); //文件已经转换过
                    PutObject.getResponse().setContentType("application/pdf");
                }
            } else {
                outputObject.setreturnMessage("需要预览的文档在服务器中不存在!");
                return;
            }
        }

        /*将文件写入输出流,显示在界面上,实现预览效果*/
        FileInputStream fis = null;
        OutputStream os = null;
        try {
            fis = new FileInputStream(filePath);
            os = PutObject.getResponse().getOutputStream();
            int count;
            byte[] buffer = new byte[1024 * 1024];
            while ((count = fis.read(buffer)) != -1) {
                os.write(buffer, 0, count);
            }
            if ("java".equals(fileType) || "sql".equals(fileType) || "css".equals(fileType) || "tpl".equals(fileType)
                || "json".equals(fileType) || "js".equals(fileType)) {
                os.write(("<script type='text/javascript' id='javaTextTemplate'>document.getElementById('javaTextTemplate').remove();"
                    + "var content = '<textarea style=\"width: 100%;height: 100%;border: 0px;padding: 0px;margin: 0px; resize: none;\" readonly>' "
                    + "+ document.body.innerHTML + '</textarea>'; "
                    + "document.body.innerHTML = content;</script>").getBytes());
            }
            os.flush();
        } catch (IOException e) {
            LOGGER.warn("write failed, message is {}", e);
        } finally {
            FileUtil.close(os);
            FileUtil.close(fis);
        }
    }

    /**
     * 创建空文件
     *
     * @param fileExtName 文件后缀
     * @param userId      用户id
     * @param folderId    所属文件夹id
     */
    public void createNewFileOrFolder(String fileExtName, String userId, String folderId) {
        String newFileName = System.currentTimeMillis() + "." + fileExtName;//新文件名
        String basePath = tPath + FileConstants.FileUploadPath.getSavePath(FILE_PATH_TYPE) + "/" + userId;
        FileUtil.createDirs(basePath);
        String visitPath = FileConstants.FileUploadPath.getVisitPath(FILE_PATH_TYPE) + userId;
        String path = basePath + "/" + newFileName;
        createFile(fileExtName, path);

        FileConsole fileConsole = new FileConsole();
        fileConsole.setName("新建文件" + "." + fileExtName);
        fileConsole.setSize(CommonNumConstants.NUM_ZERO);
        fileConsole.setParentId(fileCatalogService.setParentId(folderId));
        fileConsole.setChunk(CommonNumConstants.NUM_ZERO);
        fileConsole.setChunkSize(CommonNumConstants.NUM_ZERO.toString());
        fileConsole.setType(fileExtName);
        fileConsole.setSizeType("bytes");
        String trueFileName = visitPath + "/" + newFileName;
        fileConsole.setAddress(trueFileName);
        fileConsole.setThumbnail(FileType.getIconByFileExt(fileExtName));
        createEntity(fileConsole, userId);
    }

    /**
     * 创建文件
     *
     * @param fileExtName 文件后缀
     * @param path        文件地址
     */
    private void createFile(String fileExtName, String path) {
        if (FileType.OFFICE_IS_DOCX.getKey().equalsIgnoreCase(fileExtName)) {
            FileUtil.createNewDocxFile(path);
        } else if (FileType.OFFICE_IS_XLSX.getKey().equalsIgnoreCase(fileExtName)) {
            FileUtil.createNewExcelFile(path);
        } else if (FileType.OFFICE_IS_PPT.getKey().equalsIgnoreCase(fileExtName)) {
            FileUtil.createNewPPtFile(path);
        } else {
            FileUtil.createNewSimpleFile(path);
        }
    }

    /**
     * 新建word文件
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void createFileToService(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        createNewFileOrFolder(map.get("type").toString(), inputObject.getLogParams().get("id").toString(), map.get("folderId").toString());
    }

    /**
     * 创建副本
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void insertDuplicateCopyToService(InputObject inputObject, OutputObject outputObject) {
        setCopyFileMation(inputObject);
    }

    /**
     * 获取文件属性
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryFileMationById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        FileCatalog fileCatalog = fileCatalogService.selectById(id);
        if (ObjectUtil.isNotEmpty(fileCatalog) && StrUtil.isNotEmpty(fileCatalog.getId())) {
            iAuthUserService.setName(fileCatalog, "createId", "createName");
            fileCatalog.setType(DickCloudType.FOLDER.getValue());
            fileCatalog.setTurnSize("0KB");
            outputObject.setBean(fileCatalog);
        }

        FileConsole fileConsole = selectById(id);
        if (ObjectUtil.isNotEmpty(fileConsole) && StrUtil.isNotEmpty(fileConsole.getId())) {
            iAuthUserService.setName(fileConsole, "createId", "createName");
            fileConsole.setType(DickCloudType.FILE.getValue());
            fileConsole.setTurnSize(BytesUtil.sizeFormatNum2String(fileConsole.getSize()));
            outputObject.setBean(fileConsole);
        }
    }

    /**
     * 文件打包
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void insertFileMationToPackageToFolder(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        List<Map<String, Object>> array = JSONUtil.toList(map.get("jsonStr").toString(), null);
        String userId = inputObject.getLogParams().get("id").toString();
        String folderId = fileCatalogService.setParentId(map.get("folderId").toString());
        List<String> folderBeans = new ArrayList<>();
        List<String> fileBeans = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            Map<String, Object> object = array.get(i);
            if (DickCloudType.FOLDER.getKey().equals(object.get("rowType").toString())) {//文件夹
                folderBeans.add(object.get("rowId").toString());
            } else {
                fileBeans.add(object.get("rowId").toString());
            }
        }
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        // 加载数据
        List<Map<String, Object>> dowlLoadFile = new ArrayList<>();
        if (!folderBeans.isEmpty()) {
            // 选择保存的文件夹不为空
            List<Map<String, Object>> folderNew = fileCatalogService.queryFolderAndChildList(folderBeans);
            List<Map<String, Object>> fileNew = skyeyeBaseMapper.queryChildFileListByFolder(folderNew, DeleteFlagEnum.NOT_DELETE.getKey(), tenantId);
            fileNew.forEach(file -> {
                file.put("fileAddress", file.get("address").toString());
            });
            dowlLoadFile.addAll(folderNew);
            dowlLoadFile.addAll(fileNew);
        }
        if (!fileBeans.isEmpty()) {
            // 选择保存的文件不为空
            List<Map<String, Object>> fileNew = skyeyeBaseMapper.queryShareFileListByFileList(fileBeans, DeleteFlagEnum.NOT_DELETE.getKey(), tenantId);
            fileNew.forEach(file -> {
                file.put("fileAddress", file.get("address").toString());
            });
            dowlLoadFile.addAll(fileNew);
        }

        if (!dowlLoadFile.isEmpty()) {
            for (Map<String, Object> folder : dowlLoadFile) {
                // 重置父id
                String[] str = folder.get("parentId").toString().split(CommonCharConstants.COMMA_MARK);
                folder.put("directParentId", str[str.length - 1]);
                folder.put("fileName", folder.get("name").toString());
                folder.put("fileType", folder.get("type").toString());
            }
            //将数据转化为树的形式，方便进行父id重新赋值
            dowlLoadFile = ToolUtil.listToTree(dowlLoadFile, "id", "directParentId", "children");
            //打包
            String fileName = String.valueOf(System.currentTimeMillis());//压缩包文件名
            String basePath = tPath + FileConstants.FileUploadPath.getSavePath(FILE_PATH_TYPE) + "/" + userId;
            String visitPath = FileConstants.FileUploadPath.getVisitPath(FILE_PATH_TYPE) + userId;
            String strZipPath = basePath + CommonCharConstants.SLASH_MARK + fileName + ".zip";
            File zipFile = new File(strZipPath);
            if (zipFile.exists()) {
                outputObject.setreturnMessage("该文件已存在，生成失败。");
                return;
            } else {
                ZipOutputStream out = null;
                try {
                    out = new ZipOutputStream(new FileOutputStream(strZipPath));
                    ToolUtil.recursionZip(out, dowlLoadFile, StrUtil.EMPTY, tPath.replace(FileConstants.FILE_BASE_FIRST_PATH, StrUtil.EMPTY), 2);
                } catch (Exception ee) {
                    throw new CustomException(ee);
                } finally {
                    FileUtil.close(out);
                }
            }

            FileConsole fileConsole = new FileConsole();
            fileConsole.setName("压缩文件" + "." + FileType.PACKAGE_IS_ZIP.getKey());
            fileConsole.setSize((int) zipFile.length());
            fileConsole.setParentId(folderId);
            fileConsole.setChunkSize(String.valueOf(zipFile.length()));
            fileConsole.setType(FileType.PACKAGE_IS_ZIP.getKey());
            fileConsole.setSizeType("bytes");
            String trueFileName = visitPath + CommonCharConstants.SLASH_MARK + fileName + "." + FileType.PACKAGE_IS_ZIP.getKey();
            fileConsole.setAddress(trueFileName);
            fileConsole.setThumbnail("../../assets/images/rar.png");
            createEntity(fileConsole, userId);
        } else {
            outputObject.setreturnMessage("未找到要打包的文件.");
        }
    }

    /**
     * 压缩包解压
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void insertFileMationPackageToFolder(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        FileConsole fileConsole = selectById(id);
        if (FileType.judgeIsAllowedFileType(fileConsole.getType(), 4)) {//压缩包
            String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
            String userId = inputObject.getLogParams().get("id").toString();
            String basePath = tPath + FileConstants.FileUploadPath.getSavePath(FILE_PATH_TYPE) + "/" + userId + "/";
            String visitPath = FileConstants.FileUploadPath.getVisitPath(FILE_PATH_TYPE) + userId;
            String zipfile = tPath.replace(FileConstants.FILE_BASE_FIRST_PATH, StrUtil.EMPTY) + fileConsole.getAddress();//压缩包文件
            if (new File(zipfile).exists()) {
                ZipEntry entry;
                ZipFile zip = null;
                // 封装解压后的路径
                BufferedOutputStream bos = null;
                // 封装待解压文件路径
                BufferedInputStream bis = null;
                List<Map<String, Object>> beans = new ArrayList<>();
                try {
                    // 设置,默认是UTF-8
                    Charset charset = Charset.forName("GBK");
                    zip = new ZipFile(zipfile, charset);
                    Map<String, Object> bean;
                    Enumeration<ZipEntry> enums = (Enumeration<ZipEntry>) zip.entries();
                    String fileName;//文件名称
                    String fileZipPath;//文件路径--作为文件父id
                    String newSaveFileName;//新文件保存名称
                    while (enums.hasMoreElements()) {
                        entry = enums.nextElement();
                        bean = new HashMap<>();
                        if (entry.isDirectory()) {
                            fileName = ToolUtil.getSubStr("/" + entry.getName(), 2);//文件名
                            fileZipPath = entry.getName().replace(fileName, StrUtil.EMPTY);//文件路径--作为文件父id
                            if (ToolUtil.isBlank(fileZipPath)) {
                                bean.put("parentId", "0");
                            } else {
                                bean.put("parentId", fileZipPath);
                            }
                            bean.put("originalName", entry.getName());
                            bean.put("id", entry.getName());
                            bean.put("newId", ToolUtil.getSurFaceId());
                            bean.put("name", fileName.replace("/", StrUtil.EMPTY));
                            bean.put("address", StrUtil.EMPTY);
                            bean.put("fileExtName", DickCloudType.FOLDER.getKey());
                            beans.add(bean);
                        } else {
                            fileName = entry.getName().substring(("/" + entry.getName()).lastIndexOf("/"));//文件名
                            fileZipPath = entry.getName().replace(fileName, "");//文件路径--作为文件父id
                            newSaveFileName = String.valueOf(System.currentTimeMillis());
                            if (ToolUtil.isBlank(fileZipPath)) {
                                bean.put("parentId", "0");
                            } else {
                                bean.put("parentId", fileZipPath);
                            }
                            bean.put("originalName", entry.getName());
                            bean.put("id", entry.getName());
                            bean.put("newId", ToolUtil.getSurFaceId());
                            bean.put("name", fileName);
                            bean.put("address", visitPath + "/" + newSaveFileName + "." + fileName.substring(fileName.lastIndexOf(".") + 1));
                            bean.put("fileExtName", fileName.substring(fileName.lastIndexOf(".") + 1));
                            beans.add(bean);
                            bos = new BufferedOutputStream(new FileOutputStream(basePath + newSaveFileName + "." + fileName.substring(fileName.lastIndexOf(".") + 1)));
                            // 获取条目流
                            bis = new BufferedInputStream(zip.getInputStream(entry));
                            byte[] buf = new byte[1024];
                            int len;
                            while ((len = bis.read(buf)) != -1) {
                                bos.write(buf, 0, len);
                            }
                            bos.close();
                        }
                    }
                } catch (Exception ee) {
                    throw new CustomException(ee);
                } finally {
                    FileUtil.close(bis);
                    FileUtil.close(bos);
                    FileUtil.close(zip);
                }
                beans = ToolUtil.listToTree(beans, "id", "parentId", "children");
                ToolUtil.FileListParentISEdit(beans, fileConsole.getParentId());//替换父id
                beans = ToolUtil.FileTreeTransList(beans);//将树转为list
                List<Map<String, Object>> folderList = ToolUtil.getFolderByList(beans);//获取集合中的文件夹
                List<Map<String, Object>> fileList = ToolUtil.getFileByList(beans);//获取集合中的文件
                for (Map<String, Object> item : folderList) {//文件夹
                    item.put("deleteFlag", DeleteFlagEnum.NOT_DELETE.getKey());
                    item.put("createId", userId);
                    item.put("createTime", DateUtil.getTimeAndToString());
                }
                if (!folderList.isEmpty()) {
                    fileConsoleDao.insertFolderList(folderList, tenantId);
                }
                String baseShowPath = tPath.replace(FileConstants.FILE_BASE_FIRST_PATH, StrUtil.EMPTY);
                for (Map<String, Object> item : fileList) {//文件
                    File f = new File(baseShowPath + item.get("address").toString());
                    item.put("sizeType", "bytes");//文件大小单位
                    item.put("size", f.length());//文件大小
                    item.put("chunk", 0);//文件整合完之后的序号 默认0
                    item.put("chunkSize", f.length());//文件整合之后的大小
                    String fileExtName = item.get("fileExtName").toString();
                    item.put("type", fileExtName);
                    if (FileType.judgeIsAllowedFileType(fileExtName, 1)) {//图片
                        item.put("thumbnail", item.get("address").toString());//文件缩略图地址
                    } else if (FileType.judgeIsAllowedFileType(fileExtName, 6)) {//电子书
                        String picName = System.currentTimeMillis() + ".jpg";
                        String newFilename = basePath + picName;
                        writeAndReadQpubFileThumbnail(baseShowPath + item.get("address").toString(), newFilename);
                        // 文件缩略图地址
                        item.put("thumbnail", visitPath + "/" + picName);
                    } else if (FileType.judgeIsAllowedFileType(fileExtName, 2)) {
                        // office文件缩略图地址
                        item.put("thumbnail", FileType.getIconByFileExt(fileExtName));
                    } else if (FileType.judgeIsAllowedFileType(fileExtName, 5)) {
                        // ace文件缩略图地址
                        item.put("thumbnail", FileType.getIconByFileExt(fileExtName));
                    } else if (FileType.judgeIsAllowedFileType(fileExtName, 3)) {//视频
                        String ffmpegGPath = tPath + "/util/ffmpeg.exe";//工具路径
                        String fileThumbnail = System.currentTimeMillis() + ".jpg";
                        FileUtil.createDirs(basePath + "ffmpeg/");
                        if (ToolUtil.take(baseShowPath + item.get("address").toString(), basePath + "ffmpeg/" + fileThumbnail, ffmpegGPath)) {
                            item.put("thumbnail", visitPath + "/ffmpeg/" + fileThumbnail);
                        } else {
                            FileUtil.deleteFile(baseShowPath + item.get("address").toString());
                            outputObject.setreturnMessage("上传失败。");
                            return;
                        }
                    } else if (FileType.judgeIsAllowedFileType(fileExtName, 4)) {//压缩包
                        item.put("thumbnail", "../../assets/images/rar.png");//文件缩略图地址
                    } else {//其他
                        item.put("thumbnail", "../../assets/images/cloud/other-icon.png");//文件缩略图地址
                    }
                    item.put("deleteFlag", DeleteFlagEnum.NOT_DELETE.getKey());
                    item.put("createId", userId);
                    item.put("createTime", DateUtil.getTimeAndToString());
                }
                if (!fileList.isEmpty()) {
                    fileConsoleDao.insertShareFileListByList(fileList, tenantId);
                }
            } else {
                outputObject.setreturnMessage("该文件已不存在。");
            }
        } else {
            outputObject.setreturnMessage("文件类型不正确，无法进行解压。");
        }
    }

    /**
     * 文件或者文件夹复制
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void insertPasteCopyToService(InputObject inputObject, OutputObject outputObject) {
        setCopyFileMation(inputObject);
    }

    /**
     * 文件或者文件夹剪切
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void insertPasteCutToService(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        List<Map<String, Object>> array = JSONUtil.toList(map.get("jsonStr").toString(), null);//获取数据信息
        String userId = inputObject.getLogParams().get("id").toString();
        String folderId = fileCatalogService.setParentId(map.get("folderId").toString());
        List<String> folderBeans = new ArrayList<>();
        List<String> fileBeans = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            Map<String, Object> object = array.get(i);
            if (DickCloudType.FOLDER.getKey().equals(object.get("rowType").toString())) {//文件夹
                folderBeans.add(object.get("rowId").toString());
            } else {
                fileBeans.add(object.get("rowId").toString());
            }
        }
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        if (!folderBeans.isEmpty()) {//选择保存的文件夹不为空
            List<Map<String, Object>> folderNew = fileCatalogService.queryFolderAndChildList(folderBeans);
            if (!folderNew.isEmpty()) {//删除之前的信息
                fileCatalogService.deleteById(folderNew.stream().map(bean -> bean.get("id").toString()).collect(Collectors.toList()));
            }
            List<Map<String, Object>> fileNew = fileConsoleDao.queryChildFileListByFolder(folderNew, DeleteFlagEnum.NOT_DELETE.getKey(), tenantId);
            if (!fileNew.isEmpty()) {//删除之前的信息
                deleteById(fileNew.stream().map(bean -> bean.get("id").toString()).collect(Collectors.toList()));
            }
            for (Map<String, Object> folder : folderNew) {//重置父id
                String[] str = folder.get("parentId").toString().split(",");
                folder.put("directParentId", str[str.length - 1]);
                folder.put("newId", ToolUtil.getSurFaceId());
            }
            // 将数据转化为树的形式，方便进行父id重新赋值
            folderNew = ToolUtil.listToTree(folderNew, "id", "directParentId", "children");
            ToolUtil.FileListParentISEdit(folderNew, folderId);//替换父id
            folderNew = ToolUtil.FileTreeTransList(folderNew);//将树转为list
            for (Map<String, Object> folder : folderNew) {
                folder.put("createId", userId);
                folder.put("createTime", DateUtil.getTimeAndToString());
            }
            // 为文件重置新parentId参数
            for (Map<String, Object> folder : folderNew) {
                String parentId = folder.get("parentId").toString() + folder.get("id").toString() + ",";
                String newParentId = folder.get("newParentId").toString() + folder.get("newId").toString() + ",";
                for (Map<String, Object> file : fileNew) {
                    if (parentId.equals(file.get("parentId").toString())) {
                        file.put("newParentId", newParentId);
                    }
                }
            }
            //为文件重置新参数
            for (Map<String, Object> file : fileNew) {
                file.put("newId", ToolUtil.getSurFaceId());
                file.put("createId", userId);
                file.put("createTime", DateUtil.getTimeAndToString());
            }
            if (!folderNew.isEmpty()) {
                fileConsoleDao.insertFolderList(folderNew, tenantId);
            }
            if (!fileNew.isEmpty()) {
                fileConsoleDao.insertShareFileListByList(fileNew, tenantId);
            }
        }
        if (!fileBeans.isEmpty()) {//选择保存的文件不为空
            List<Map<String, Object>> fileNew = fileConsoleDao.queryShareFileListByFileList(fileBeans, DeleteFlagEnum.NOT_DELETE.getKey(), tenantId);
            if (!fileNew.isEmpty()) {//删除之前的信息
                deleteById(fileNew.stream().map(bean -> bean.get("id").toString()).collect(Collectors.toList()));
            }
            //为文件重置新参数
            for (Map<String, Object> file : fileNew) {
                file.put("newParentId", folderId);
                file.put("newId", ToolUtil.getSurFaceId());
                file.put("createId", userId);
                file.put("createTime", DateUtil.getTimeAndToString());
            }
            fileConsoleDao.insertShareFileListByList(fileNew, tenantId);
        }
    }

    /**
     * office文件编辑获取修改时间作为最新的key
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryOfficeUpdateTimeToKey(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        FileConsole fileConsole = selectById(id);
        outputObject.setBean(fileConsole);
    }

    /**
     * 文件统计报表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @IgnoreTenant
    public void queryFileNumStatistics(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        //文件总数量和总存储
        Map<String, Object> allNum = skyeyeBaseMapper.queryAllNumFile(DeleteFlagEnum.NOT_DELETE.getKey(), tenantId);
        allNum.put("fileSizeZh", BytesUtil.sizeFormatNum2String(Long.parseLong(allNum.get("fileSize").toString())));
        //今日新增的文件总数量和总存储
        Map<String, Object> allNumToday = skyeyeBaseMapper.queryAllNumFileToday(DeleteFlagEnum.NOT_DELETE.getKey(), tenantId);
        allNumToday.put("fileSizeZh", BytesUtil.sizeFormatNum2String(Long.parseLong(allNumToday.get("fileSize").toString())));
        //本周新增的文件总数量和总存储
        Map<String, Object> allNumThisWeek = skyeyeBaseMapper.queryAllNumFileThisWeek(DeleteFlagEnum.NOT_DELETE.getKey(), tenantId);
        allNumThisWeek.put("fileSizeZh", BytesUtil.sizeFormatNum2String(Long.parseLong(allNumThisWeek.get("fileSize").toString())));
        //文件类型占比
        List<Map<String, Object>> fileTypeNum = skyeyeBaseMapper.queryFileTypeNum(DeleteFlagEnum.NOT_DELETE.getKey(), tenantId);
        Map<String, Object> fileTypeNumEntity = new HashMap<>();
        String fileTypeNumStr = "";
        for (Map<String, Object> en : fileTypeNum) {
            fileTypeNumStr += en.get("name").toString() + ",";
        }
        fileTypeNumEntity.put("fileTypeNum", fileTypeNum);
        fileTypeNumEntity.put("fileTypeNumStr", fileTypeNumStr);
        //文件存储占比（前三）
        List<Map<String, Object>> fileStorageNum = skyeyeBaseMapper.queryFileStorageNum(DeleteFlagEnum.NOT_DELETE.getKey(), tenantId);
        //本年度新增文件数
        List<Map<String, Object>> newFileNum = skyeyeBaseMapper.queryNewFileNum(DeleteFlagEnum.NOT_DELETE.getKey(), tenantId);
        //近七天新增文件类型数
        List<Map<String, Object>> fileTypeNumSevenDay = skyeyeBaseMapper.queryFileTypeNumSevenDay(DeleteFlagEnum.NOT_DELETE.getKey(), tenantId);

        map.clear();
        map.put("allNum", allNum);
        map.put("allNumToday", allNumToday);
        map.put("allNumThisWeek", allNumThisWeek);
        map.put("fileTypeNumEntity", fileTypeNumEntity);
        map.put("fileStorageNum", fileStorageNum);
        map.put("newFileNum", newFileNum);
        map.put("fileTypeNumSevenDay", fileTypeNumSevenDay);
        outputObject.setBean(map);
    }

    /**
     * 文件打包下载
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void insertFileMationToPackageDownload(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        List<Map<String, Object>> array = JSONUtil.toList(map.get("jsonStr").toString(), null);//获取数据信息
        String trueFileName;//文件存储路径
        Map<String, Object> user = inputObject.getLogParams();
        String userId = user.get("id").toString();
        // 创建前端传来的数据对象
        List<String> folderBeans = new ArrayList<>();
        List<String> fileBeans = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            Map<String, Object> object = array.get(i);
            if (DickCloudType.FOLDER.getKey().equals(object.get("rowType").toString())) {//文件夹
                folderBeans.add(object.get("rowId").toString());
            } else {
                fileBeans.add(object.get("rowId").toString());
            }
        }
        String basePath = tPath + FileConstants.FileUploadPath.getSavePath(FILE_PATH_TYPE) + "/temporaryfile/" + userId + "/";
        FileUtil.createDirs(basePath);
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        //加载数据
        List<Map<String, Object>> dowlLoadFile = new ArrayList<>();
        if (!folderBeans.isEmpty()) {//选择保存的文件夹不为空
            List<Map<String, Object>> folderNew = fileCatalogService.queryFolderAndChildList(folderBeans);
            List<Map<String, Object>> fileNew = skyeyeBaseMapper.queryChildFileListByFolder(folderNew, DeleteFlagEnum.NOT_DELETE.getKey(), tenantId);
            dowlLoadFile.addAll(folderNew);
            dowlLoadFile.addAll(fileNew);
        }
        if (!fileBeans.isEmpty()) {//选择保存的文件不为空
            List<Map<String, Object>> fileNew = skyeyeBaseMapper.queryShareFileListByFileList(fileBeans, DeleteFlagEnum.NOT_DELETE.getKey(), tenantId);
            dowlLoadFile.addAll(fileNew);
        }

        //创建压缩包
        if (!dowlLoadFile.isEmpty()) {
            for (Map<String, Object> bean : dowlLoadFile) {//重置父id
                String[] str = bean.get("parentId").toString().split(",");
                bean.put("directParentId", str[str.length - 1]);
                bean.put("fileName", bean.get("name"));
                bean.put("fileType", bean.get("type"));
                bean.put("fileAddress", bean.get("address"));
            }
            //将数据转化为树的形式，方便进行父id重新赋值
            dowlLoadFile = ToolUtil.listToTree(dowlLoadFile, "id", "directParentId", "children");
            //打包
            String fileName = String.valueOf(System.currentTimeMillis());//压缩包文件名
            String strZipPath = basePath + fileName + ".zip";
            File zipFile = new File(strZipPath);
            if (zipFile.exists()) {
                outputObject.setreturnMessage("该文件已存在，生成失败。");
                return;
            } else {
                ZipOutputStream out = null;
                try {
                    out = new ZipOutputStream(new FileOutputStream(strZipPath));
                    ToolUtil.recursionZip(out, dowlLoadFile, "", tPath.replace(FileConstants.FILE_BASE_FIRST_PATH, StrUtil.EMPTY), 2);
                } catch (Exception ee) {
                    throw new CustomException(ee);
                } finally {
                    FileUtil.close(out);
                }
            }
            trueFileName = FileConstants.FileUploadPath.getVisitPath(FILE_PATH_TYPE) + "temporaryfile/" + userId + "/" + fileName + ".zip";
        } else {
            outputObject.setreturnMessage("未找到要打包的文件.");
            return;
        }
        map.clear();
        map.put("fileAddress", trueFileName);//文件地址
        outputObject.setBean(map);
    }

    /**
     * 获取并写入epub电子书的缩略图
     *
     * @param epubFilePath     epub电子书文件地址
     * @param thumbnailPicPath 缩略图图片地址
     */
    private void writeAndReadQpubFileThumbnail(String epubFilePath, String thumbnailPicPath) {
        FileImageOutputStream imgout = null;
        try {
            EpubReader epubReader = new EpubReader();
            Book book = epubReader.readEpub(new FileInputStream(epubFilePath));
            Resource resource = book.getResources().getByHref("Images/cover.jpg");
            byte[] p = resource.getData();
            imgout = new FileImageOutputStream(new File(thumbnailPicPath));
            imgout.write(p, 0, p.length);
        } catch (IOException e) {
            throw new CustomException(e);
        } finally {
            FileUtil.close(imgout);
        }
    }

}
