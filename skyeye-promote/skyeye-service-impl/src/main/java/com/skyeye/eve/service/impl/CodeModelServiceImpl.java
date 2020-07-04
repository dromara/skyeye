/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved.
 */
package com.skyeye.eve.service.impl;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.CodeModelDao;
import com.skyeye.eve.service.CodeModelService;

@Service
public class CodeModelServiceImpl implements CodeModelService{
	
	@Autowired
	private CodeModelDao codeModelDao;

	/**
	 * 
	     * @Title: queryCodeModelGroupList
	     * @Description: 获取模板列表
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryCodeModelList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = codeModelDao.queryCodeModelList(map, 
				new PageBounds(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString())));
		PageList<Map<String, Object>> beansPageList = (PageList<Map<String, Object>>)beans;
		int total = beansPageList.getPaginator().getTotalCount();
		outputObject.setBeans(beans);
		outputObject.settotal(total);
	}
	
	/**
	 * 
	     * @Title: insertCodeModelMationMation
	     * @Description: 新增模板列表
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void insertCodeModelMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = codeModelDao.queryCodeModelMationByName(map);
		if(bean == null){
			Map<String, Object> user = inputObject.getLogParams();
			map.put("id", ToolUtil.getSurFaceId());
			map.put("createId", user.get("id"));
			map.put("createTime", ToolUtil.getTimeAndToString());
			codeModelDao.insertCodeModelMation(map);
		}else{
			outputObject.setreturnMessage("该模板已存在，请更换。");
		}
	}

	/**
	 * 
	     * @Title: deleteCodeModelMationMationById
	     * @Description: 删除模板信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void deleteCodeModelById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		codeModelDao.deleteCodeModelById(map);
	}

	/**
	 * 
	     * @Title: queryCodeModelMationToEditById
	     * @Description: 编辑模板信息时进行回显
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryCodeModelMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = codeModelDao.queryCodeModelMationToEditById(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editCodeModelMationMationById
	     * @Description: 编辑模板信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void editCodeModelMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = codeModelDao.queryCodeModelMationByIdAndName(map);
		if(bean == null){
			codeModelDao.editCodeModelMationById(map);
		}else{
			outputObject.setreturnMessage("该模板已存在，请更换。");
		}
	}
	
}
