/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service.impl;

import cn.hutool.json.JSONUtil;
import com.skyeye.common.constans.RmProGramConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.FileUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.SmProjectPageModeDao;
import com.skyeye.eve.service.SmProjectPageModeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;


@Service
public class SmProjectPageModeServiceImpl implements SmProjectPageModeService{
	
	@Autowired
	private SmProjectPageModeDao smProjectPageModeDao;
	
	@Value("${IMAGES_PATH}")
	private String tPath;

	/**
	 * 
	     * @Title: queryProPageModeMationByPageIdList
	     * @Description: 根据项目页面获取该页面拥有的组件列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryProPageModeMationByPageIdList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = smProjectPageModeDao.queryProPageModeMationByPageIdList(map);
		if(beans != null && !beans.isEmpty()){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}

	/**
	 * 
	     * @Title: editProPageModeMationByPageIdList
	     * @Description: 插入项目页面对应的模块内容
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editProPageModeMationByPageIdList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		smProjectPageModeDao.deletePageModelMationListByPageId(map);//根据页面id删除之前的页面和模块的绑定信息
		List<Map<String, Object>> array = JSONUtil.toList(map.get("jsonData").toString(), null);//获取模板绑定信息
		if(array.size() > 0){
			Map<String, Object> user = inputObject.getLogParams();
			List<Map<String, Object>> beans = new ArrayList<>();
			Map<String, Object> bean;
			for(int i = 0; i < array.size(); i++){
				Map<String, Object> object = array.get(i);
				bean = new HashMap<>();
				bean.put("pageId", object.get("pageId"));
				bean.put("modelId", object.get("modelId"));
				bean.put("id", ToolUtil.getSurFaceId());
				bean.put("createId", user.get("id"));
				bean.put("createTime", DateUtil.getTimeAndToString());
				bean.put("sort", i);
				beans.add(bean);
			}
			smProjectPageModeDao.editProPageModeMationByPageIdList(beans);
		}
	}

	/**
	 * 
	     * @Title: queryPropertyListByMemberId
	     * @Description: 根据组件id获取标签属性
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryPropertyListByMemberId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = smProjectPageModeDao.queryPropertyListByMemberId(map);
		if(beans != null && !beans.isEmpty()){
			for(Map<String, Object> bean : beans){
				if("1".equals(bean.get("selChildData").toString())){//子查询设置为是
					List<Map<String, Object>> propertyValues = smProjectPageModeDao.queryPropertyValuesListByPropertyId(bean);
					bean.put("propertyValue", propertyValues);
				}
			}
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}

	/**
	 * 
	     * @Title: queryPageToExportH5ByPageId
	     * @Description: 导出当前页面为h5
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(value="transactionManager")
	public void queryPageToExportH5ByPageId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		///获取页面信息
		Map<String, Object> page = smProjectPageModeDao.queryProPageMationByPageId(map);
		if(page == null){
			outputObject.setreturnMessage("该页面信息不存在");
			return;
		}
		//获取页面拥有的组件
		List<Map<String, Object>> models = smProjectPageModeDao.queryProPageModeMationByPageIdList(map);
		StringBuffer content = new StringBuffer();
		content.append(FileUtil.readFileContent(tPath.replace("images", "") + "/images/smpromodel/h5/htmltop.tpl"));
		for(Map<String, Object> model : models){
			content.append(model.containsKey("htmlContent") ? model.get("htmlContent").toString() : "");
			content.append("<script type='text/javascript'>" + (model.containsKey("htmlJsContent") ? model.get("htmlJsContent").toString() : "") + "</script>");
		}
		content.append(FileUtil.readFileContent(tPath.replace("images", "") + "/images/smpromodel/h5/htmlbuttom.tpl"));
		
		//构造导出目录
		List<Map<String, Object>> folderNew = RmProGramConstants.transform();
		page.put("content", content.toString());
		page.put("fileType", "html");
		folderNew.add(page);
		//构建文件所属目录
		Map<String, Object> fileFolder = new HashMap<>();
		fileFolder.put("id", page.get("filePath"));
		fileFolder.put("fileName", page.get("filePath"));
		fileFolder.put("fileType", "folder");
		fileFolder.put("parentId", "2");
		folderNew.add(fileFolder);
		
		//将数据转化为树的形式，方便进行父id重新赋值
		folderNew = ToolUtil.listToTree(folderNew, "id", "parentId", "children");

		String folderPath = tPath + "/smprodown/" + inputObject.getLogParams().get("id").toString() + "/";
		File pack = new File(folderPath);
		if(!pack.isDirectory())//目录不存在 
			pack.mkdirs();//创建目录
		
		//开始打包
		String strZipPath = folderPath + map.get("pageId").toString() + ".zip";
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(strZipPath));
		try{
			ToolUtil.recursionZip(out, folderNew, "", tPath.replace("images", ""), 1);
			map.put("url", "/images/smprodown/" + inputObject.getLogParams().get("id").toString() + "/" + map.get("pageId").toString() + ".zip");
			map.put("fileName", page.get("name").toString() + ".zip");
			outputObject.setBean(map);
		}finally{
			if(out != null) {
				out.close();
			}
		}
	}

}
