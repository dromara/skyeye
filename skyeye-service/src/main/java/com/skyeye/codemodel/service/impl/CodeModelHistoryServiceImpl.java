package com.skyeye.codemodel.service.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.skyeye.codemodel.dao.CodeModelHistoryDao;
import com.skyeye.codemodel.service.CodeModelHistoryService;
import com.skyeye.common.constans.Constants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

@Service
public class CodeModelHistoryServiceImpl implements CodeModelHistoryService{
	
	@Autowired
	private CodeModelHistoryDao codeModelHistoryDao;

	/**
	 * 
	     * @Title: queryCodeModelHistoryList
	     * @Description: 获取模板生成历史列表
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@SuppressWarnings("static-access")
	@Override
	public void queryCodeModelHistoryList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = codeModelHistoryDao.queryCodeModelHistoryList(map, 
				new PageBounds(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString())));
		PageList<Map<String, Object>> beansPageList = (PageList<Map<String, Object>>)beans;
		int total = beansPageList.getPaginator().getTotalCount();
		File file = null;
		String tPath = inputObject.getRequest().getSession().getServletContext().getRealPath("/");
		String basePath = tPath.substring(0, inputObject.getRequest().getSession().getServletContext().getRealPath("/").indexOf(Constants.PROJECT_WEB)); 
		for(Map<String, Object> bean : beans){
			file = new File(basePath + "/" + bean.get("filePath").toString());
			if(!file.exists()){
				bean.put("isExist", "否");
			}else{
				bean.put("isExist", "是");
			}
		}
		outputObject.setBeans(beans);
		outputObject.settotal(total);
	}

	/**
	 * 
	     * @Title: insertCodeModelHistoryCreate
	     * @Description: 重新生成文件
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@SuppressWarnings("static-access")
	@Override
	public void insertCodeModelHistoryCreate(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String tPath = inputObject.getRequest().getSession().getServletContext().getRealPath("/");
		String basePath = tPath.substring(0, inputObject.getRequest().getSession().getServletContext().getRealPath("/").indexOf(Constants.PROJECT_WEB)); 
		String strZipPath = basePath + "/" + map.get("filePath").toString();
		File zipFile = new File(strZipPath);
		if(zipFile.exists()){
			outputObject.setreturnMessage("该文件已存在，生成失败。");
		}else{
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(strZipPath));
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
			out.close();
		}
	}

	/**
	 * 
	     * @Title: downloadCodeModelHistory
	     * @Description: 下载文件
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@SuppressWarnings({ "static-access", "resource" })
	@Override
	public void downloadCodeModelHistory(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String tPath = inputObject.getRequest().getSession().getServletContext().getRealPath("/");
		String basePath = tPath.substring(0, inputObject.getRequest().getSession().getServletContext().getRealPath("/").indexOf(Constants.PROJECT_WEB)); 
		String strZipPath = basePath + "/" + map.get("filePath").toString();
		//获取输入流  
		InputStream bis = new BufferedInputStream(new FileInputStream(new File(strZipPath)));
		inputObject.getResponse().setHeader("REQUESTMATION", "DOWNLOAD");
		// 转码，免得文件名中文乱码
		String filename = URLEncoder.encode(map.get("filePath").toString(), "UTF-8");
		// 设置文件下载头
		inputObject.getResponse().addHeader("Content-Disposition", "attachment;filename=" + filename);
		// 1.设置文件ContentType类型，这样设置，会自动判断下载文件类型
		inputObject.getResponse().setContentType("multipart/form-data");
		BufferedOutputStream out1 = new BufferedOutputStream(inputObject.getResponse().getOutputStream());
		int len = 0;
		while ((len = bis.read()) != -1) {
			out1.write(len);
			out1.flush();
		}
	}
	
	
}
