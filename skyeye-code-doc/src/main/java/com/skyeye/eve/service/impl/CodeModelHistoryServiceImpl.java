/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.dao.CodeModelHistoryDao;
import com.skyeye.eve.service.CodeModelHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class CodeModelHistoryServiceImpl implements CodeModelHistoryService{
	
	@Autowired
	private CodeModelHistoryDao codeModelHistoryDao;
	
	@Value("${IMAGES_PATH}")
	private String tPath;

	/**
	 * 
	     * @Title: queryCodeModelHistoryList
	     * @Description: 获取模板生成历史列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryCodeModelHistoryList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = codeModelHistoryDao.queryCodeModelHistoryList(map);
		File file = null;
		for(Map<String, Object> bean : beans){
			file = new File(tPath + "/" + bean.get("filePath").toString());
			if(!file.exists()){
				bean.put("isExist", "否");
			}else{
				bean.put("isExist", "是");
			}
		}
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 
	     * @Title: insertCodeModelHistoryCreate
	     * @Description: 重新生成文件
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertCodeModelHistoryCreate(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String strZipPath = tPath + "/" + map.get("filePath").toString();
		File zipFile = new File(strZipPath);
		if(zipFile.exists()){
			outputObject.setreturnMessage("该文件已存在，生成失败。");
		}else{
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(strZipPath));
			try{
				byte[] buffer = new byte[1024];
				List<Map<String, Object>> beans = codeModelHistoryDao.queryCodeModelHistoryListByFilePath(map);
				for(Map<String, Object> bean : beans){
					//加入压缩包
					ByteArrayInputStream stream = new ByteArrayInputStream(bean.get("content").toString().getBytes());
					out.putNextEntry(new ZipEntry(bean.get("fileName").toString() + "." + bean.get("fileType").toString().toLowerCase()));
					int len;
					// 读入需要下载的文件的内容，打包到zip文件
					while ((len = stream.read(buffer)) > 0) {
						out.write(buffer, 0, len);
					}
					out.closeEntry();
				}
			}finally{
				if(out != null)
					out.close();
			}
		}
	}

	/**
	 * 
	     * @Title: downloadCodeModelHistory
	     * @Description: 下载文件
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void downloadCodeModelHistory(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String strZipPath = tPath + "/" + map.get("filePath").toString();
		//获取输入流  
		InputStream bis = new BufferedInputStream(new FileInputStream(new File(strZipPath)));
		BufferedOutputStream out = null;
		try{
			inputObject.getResponse().setHeader("REQUESTMATION", "DOWNLOAD");
			// 转码，免得文件名中文乱码
			String filename = URLEncoder.encode(map.get("filePath").toString(), "UTF-8");
			// 设置文件下载头
			inputObject.getResponse().addHeader("Content-Disposition", "attachment;filename=" + filename);
			// 1.设置文件ContentType类型，这样设置，会自动判断下载文件类型
			inputObject.getResponse().setContentType("multipart/form-data");
			out = new BufferedOutputStream(inputObject.getResponse().getOutputStream());
			int len = 0;
			while ((len = bis.read()) != -1) {
				out.write(len);
				out.flush();
			}
		}finally{
			if(bis != null)
				bis.close();
			if(out != null)
				out.close();
		}
	}
	
	
}
