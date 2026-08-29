/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.common.constans.FileConstants;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.BytesUtil;
import com.skyeye.common.util.DataCommonUtil;
import com.skyeye.common.util.FileUtil;
import com.skyeye.common.util.IdWorker;
import com.skyeye.eve.dao.SysDataSqlDao;
import com.skyeye.eve.service.SysDataSqlService;
import com.skyeye.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: SysDataSqlServiceImpl
 * @Description: 数据库备份管理服务层--平台隔离
 * @author: skyeye云系列--卫志强
 * @date: 2025/6/24 8:56
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "数据库备份管理", groupName = "数据库备份管理", manageShow = false, tenant = TenantEnum.PLATE)
public class SysDataSqlServiceImpl implements SysDataSqlService {

    @Autowired
    private SysDataSqlDao sysDataSqlDao;

    @Value("${jdbc.database.name}")
    private String dbName;

    @Value("${IMAGES_PATH}")
    private String tPath;

    @Value("${jdbc.database.username}")
    private String userName;

    @Value("${jdbc.database.password}")
    private String password;

    @Value("${jdbc.database.name}")
    private String databaseName;

    @Value("${jdbc.database.address}")
    private String address;

    @Value("${skyeye.tenant.enable}")
    protected boolean tenantEnable;

    /**
     * 获取历史备份列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void querySysDataSqlBackupsList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        map.put("tenantId", tenantId);
        List<Map<String, Object>> beans = sysDataSqlDao.querySysDataSqlBackupsList(map);
        for (Map<String, Object> bean : beans) {
            bean.put("fileSize", BytesUtil.sizeFormatNum2String(Long.parseLong(bean.get("fileSize").toString())));
        }
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    /**
     * 获取所有表的列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryAllTableMationList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        map.put("dbName", dbName);
        List<Map<String, Object>> beans = sysDataSqlDao.queryAllTableMationList(map);
        for (Map<String, Object> bean : beans) {
            bean.put("tableSize", BytesUtil.sizeFormatNum2String(Long.parseLong(bean.get("tableSize").toString())));//数据大小
            bean.put("indexSize", BytesUtil.sizeFormatNum2String(Long.parseLong(bean.get("indexSize").toString())));//索引大小
        }
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

    /**
     * 开始备份
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void insertTableBackUps(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> version = sysDataSqlDao.queryDataSqlVersion(map);
        String basePath = tPath + "\\upload\\datasql";
        FileUtil.createDirs(basePath);
        String newFileName = System.currentTimeMillis() + ".sql";
        String path = basePath + "\\" + newFileName;

        try {
            // 使用which/where命令查找mysqldump
            String osName = System.getProperty("os.name").toLowerCase();
            String findCommand = osName.contains("windows") ? "where mysqldump" : "which mysqldump";
            Process findProcess = Runtime.getRuntime().exec(findCommand);
            BufferedReader reader = new BufferedReader(new InputStreamReader(findProcess.getInputStream()));
            String mysqldumpPath = reader.readLine();

            if (mysqldumpPath == null || mysqldumpPath.isEmpty()) {
                throw new CustomException("找不到mysqldump命令，请确保MySQL客户端工具在系统PATH中");
            }

            // 构建备份命令
            List<String> command = new ArrayList<>();
            command.add(mysqldumpPath);
            command.add("--opt");
            command.add("-h" + address);
            command.add("-u" + userName);
            command.add("-p" + password);
            command.add("--lock-all-tables=true");
            command.add("--result-file=" + path);
            command.add("--default-character-set=utf8");
            command.add(databaseName);

            ProcessBuilder processBuilder = new ProcessBuilder(command);
            Process process = processBuilder.start();

            // 读取错误流，避免进程阻塞
            StringBuilder errorMsg = new StringBuilder();
            try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = errorReader.readLine()) != null) {
                    errorMsg.append(line).append("\n");
                }
            }

            if (process.waitFor() == 0) {
                String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
                map.put("tenantId", tenantId);
                // 备份成功，保存到数据库
                DataCommonUtil.setCommonData(map, inputObject.getLogParams().get("id").toString());
                map.put("mysqlVersion", version.get("version")); // 数据库版本
                String filePath = "/images/upload/datasql/" + newFileName;
                map.put("filePath", filePath); // 文件存储地址
                IdWorker id = new IdWorker();
                String sqlVersion = String.valueOf(id.nextId());
                map.put("sqlVersion", sqlVersion); // 备份版本
                map.put("sqlTitle", "数据库备份_" + sqlVersion); // 备份标题
                File sqlFile = new File(path);
                map.put("fileSize", sqlFile.length());
                sysDataSqlDao.insertTableBackUps(map);
            } else {
                throw new CustomException("数据库备份失败: " + errorMsg.toString());
            }
        } catch (Exception ex) {
            throw new CustomException(ex);
        }
    }

    /**
     * 开始还原
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void insertTableReduction(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        map.put("tenantId", tenantId);
        Map<String, Object> bean = sysDataSqlDao.queryDataSqlVersionById(map);
        if (bean != null && !bean.isEmpty()) {
            String basePath = tPath.replace(FileConstants.FILE_BASE_FIRST_PATH, StrUtil.EMPTY);
            String filePath = bean.get("filePath").toString();
            File file = new File(basePath + filePath);
            // 数据库备份文件存在
            if (file.exists()) {
                try {
                    // 使用which/where命令查找mysql命令
                    String osName = System.getProperty("os.name").toLowerCase();
                    String findCommand = osName.contains("windows") ? "where mysql" : "which mysql";
                    Process findProcess = Runtime.getRuntime().exec(findCommand);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(findProcess.getInputStream()));
                    String mysqlPath = reader.readLine();

                    if (mysqlPath == null || mysqlPath.isEmpty()) {
                        throw new CustomException("找不到mysql命令，请确保MySQL客户端工具在系统PATH中");
                    }

                    // 构建还原命令
                    List<String> command = new ArrayList<>();
                    command.add(mysqlPath);
                    command.add("-h" + address);
                    command.add("-u" + userName);
                    command.add("-p" + password);
                    command.add("--default-character-set=utf8");
                    command.add(databaseName);

                    ProcessBuilder processBuilder = new ProcessBuilder(command);
                    Process process = processBuilder.start();

                    // 从备份文件读取SQL并写入mysql进程的输入流
                    try (OutputStream outputStream = process.getOutputStream();
                         BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
                         OutputStreamWriter writer = new OutputStreamWriter(outputStream, "utf-8")) {

                        String line;
                        while ((line = br.readLine()) != null) {
                            writer.write(line + "\n");
                        }
                        writer.flush();
                    }

                    // 关闭输入流，告诉mysql进程输入结束
                    process.getOutputStream().close();

                    // 读取错误流，避免进程阻塞
                    StringBuilder errorMsg = new StringBuilder();
                    try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                        String line;
                        while ((line = errorReader.readLine()) != null) {
                            errorMsg.append(line).append("\n");
                        }
                    }

                    int exitCode = process.waitFor();
                    if (exitCode != 0) {
                        throw new CustomException("数据库还原失败: " + errorMsg.toString());
                    }

                } catch (Exception ex) {
                    throw new CustomException("数据库还原失败: " + ex.getMessage());
                }
            } else {
                outputObject.setreturnMessage("该备份文件已不存在。");
            }
        } else {
            outputObject.setreturnMessage("该备份信息已不存在。");
        }
    }

}
