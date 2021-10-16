/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.RmPropertyValueDao;
import com.skyeye.eve.service.RmPropertyValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class RmPropertyValueServiceImpl implements RmPropertyValueService{
	
	@Autowired
	private RmPropertyValueDao rmPropertyValueDao;

	/**
	 * 
	     * @Title: queryRmPropertyValueList
	     * @Description: 获取小程序样式属性值列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryRmPropertyValueList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = rmPropertyValueDao.queryRmPropertyValueList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 
	     * @Title: insertRmPropertyValueMation
	     * @Description: 添加小程序样式属性值信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertRmPropertyValueMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = rmPropertyValueDao.queryRmPropertyValueMationByName(map);
		if(bean == null){
			Map<String, Object> user = inputObject.getLogParams();
			map.put("id", ToolUtil.getSurFaceId());
			map.put("createId", user.get("id"));
			map.put("createTime", DateUtil.getTimeAndToString());
			rmPropertyValueDao.insertRmPropertyValueMation(map);
		}else{
			outputObject.setreturnMessage("该标签属性值名称已存在，不可进行二次保存");
		}
	}

	/**
	 * 
	     * @Title: deleteRmPropertyValueMationById
	     * @Description: 删除小程序样式属性值信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteRmPropertyValueMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		rmPropertyValueDao.deleteRmPropertyValueMationById(map);
	}

	/**
	 * 
	     * @Title: queryRmPropertyValueMationToEditById
	     * @Description: 编辑小程序样式属性值信息时进行回显
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryRmPropertyValueMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = rmPropertyValueDao.queryRmPropertyValueMationToEditById(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editRmPropertyValueMationById
	     * @Description: 编辑小程序样式属性值信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editRmPropertyValueMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = rmPropertyValueDao.queryRmPropertyValueMationByNameAndId(map);
		if(bean == null){
			rmPropertyValueDao.editRmPropertyValueMationById(map);
		}else{
			outputObject.setreturnMessage("该标签属性值名称已存在，不可进行二次保存");
		}
	}
	
}
