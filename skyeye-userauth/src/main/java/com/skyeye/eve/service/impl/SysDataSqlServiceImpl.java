/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.BytesUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.IdWorker;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.SysDataSqlDao;
import com.skyeye.eve.service.SysDataSqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.util.List;
import java.util.Map;

@Service
public class SysDataSqlServiceImpl implements SysDataSqlService{
	
	@Autowired
	private SysDataSqlDao sysDataSqlDao;
	
	@Value("${jdbc.database.name}")  
    private String dbName;
	
	@Value("${IMAGES_PATH}")
	private String tPath;
	
	@Value("${MYSQL_DUMP}")
	private String mysqlDump;
	
	@Value("${jdbc.database.username}")
	private String userName;
	
	@Value("${jdbc.database.password}")
	private String password;
	
	@Value("${jdbc.database.name}")
	private String databaseName;
	
	@Value("${jdbc.database.address}")
	private String address;

	/**
	 * 
	     * @Title: querySysDataSqlBackupsList
	     * @Description: 获取历史备份列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySysDataSqlBackupsList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = sysDataSqlDao.querySysDataSqlBackupsList(map);
		for(Map<String, Object> bean: beans){
			bean.put("fileSize", BytesUtil.sizeFormatNum2String(Long.parseLong(bean.get("fileSize").toString())));//数据大小
		}
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 
	     * @Title: queryAllTableMationList
	     * @Description: 获取所有表的列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryAllTableMationList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("dbName", dbName);
		List<Map<String, Object>> beans = sysDataSqlDao.queryAllTableMationList(map);
		for(Map<String, Object> bean: beans){
			bean.put("tableSize", BytesUtil.sizeFormatNum2String(Long.parseLong(bean.get("tableSize").toString())));//数据大小
			bean.put("indexSize", BytesUtil.sizeFormatNum2String(Long.parseLong(bean.get("indexSize").toString())));//索引大小
		}
		outputObject.setBeans(beans);
		outputObject.settotal(beans.size());
	}

	/**
	 * 
	     * @Title: insertTableBackUps
	     * @Description: 开始备份
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertTableBackUps(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		Map<String, Object> version = sysDataSqlDao.queryDataSqlVersion(map);
		String basePath = tPath + "\\upload\\datasql";
		File pack = new File(basePath);
		if(!pack.isDirectory())//目录不存在 
			pack.mkdirs();//创建目录
		String newFileName = String.valueOf(System.currentTimeMillis()) + ".sql";
		String path = basePath + "\\" + newFileName;//文件存储地址
		//拼接命令行的命令
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(mysqlDump + "/mysqldump").append(" --opt").append(" -h").append(address);
		stringBuilder.append(" --user=").append(userName).append(" --password=").append(password).append(" --lock-all-tables=true");
		stringBuilder.append(" --result-file=").append(path).append(" --default-character-set=utf8 ").append(databaseName);
		//调用外部执行exe文件的javaAPI
		Process process = Runtime.getRuntime().exec(stringBuilder.toString());
		if (process.waitFor() == 0) {// 0 表示线程正常终止。
			System.out.println("执行完毕");
			//保存到数据库
			map.put("id", ToolUtil.getSurFaceId());
			map.put("createId", user.get("id"));
			map.put("createTime", DateUtil.getTimeAndToString());
			map.put("mysqlVersion", version.get("version"));//数据库版本
			String filePath = "/images/upload/datasql/" + newFileName;
			map.put("filePath", filePath);//文件存储地址
			IdWorker id = new IdWorker();
			String sqlVersion = String.valueOf(id.nextId());
			map.put("sqlVersion", sqlVersion);//备份版本
			map.put("sqlTitle", "数据库备份_" + sqlVersion);//备份标题
			File sqlFile = new File(path);
			map.put("fileSize", sqlFile.length());
			sysDataSqlDao.insertTableBackUps(map);
		}
	}

	/**
	 * 
	     * @Title: insertTableReduction
	     * @Description: 开始还原
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void insertTableReduction(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sysDataSqlDao.queryDataSqlVersionById(map);
		if(bean != null && !bean.isEmpty()){
			String basePath = tPath.replace("images", "");
			String filePath = bean.get("filePath").toString();
			File file = new File(basePath + filePath);
			if(file.exists()){
				//数据库备份文件存在
				Runtime runtime = Runtime.getRuntime();
				Process process = runtime.exec(mysqlDump + "mysql.exe -h" + address + " -u" + userName + " -p" + password + " --default-character-set=utf8 " + databaseName);
				OutputStream outputStream = process.getOutputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(basePath + filePath), "utf-8"));
				String str = null;
				StringBuffer sb = new StringBuffer();
				while ((str = br.readLine()) != null) {
					sb.append(str + "\r\n");
				}
				str = sb.toString();
				OutputStreamWriter writer = new OutputStreamWriter(outputStream, "utf-8");
				writer.write(str);
				writer.flush();
				outputStream.close();
				br.close();
				writer.close();
			}else{
				outputObject.setreturnMessage("该备份文件已不存在。");
			}
		}else{
			outputObject.setreturnMessage("该备份信息已不存在。");
		}
	}
	
}
