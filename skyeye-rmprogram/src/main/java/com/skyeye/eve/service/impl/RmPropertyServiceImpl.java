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
import com.skyeye.eve.dao.RmPropertyDao;
import com.skyeye.eve.service.RmPropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class RmPropertyServiceImpl implements RmPropertyService{
	
	@Autowired
	private RmPropertyDao rmPropertyDao;

	/**
	 * 
	     * @Title: queryRmPropertyList
	     * @Description: 获取小程序样式属性列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryRmPropertyList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = rmPropertyDao.queryRmPropertyList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 
	     * @Title: insertRmPropertyMation
	     * @Description: 添加小程序样式属性信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertRmPropertyMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = rmPropertyDao.queryRmPropertyMationByName(map);
		if(bean == null){
			Map<String, Object> user = inputObject.getLogParams();
			map.put("id", ToolUtil.getSurFaceId());
			map.put("createId", user.get("id"));
			map.put("createTime", DateUtil.getTimeAndToString());
			rmPropertyDao.insertRmPropertyMation(map);
		}else{
			outputObject.setreturnMessage("该样式属性名称已存在，不可进行二次保存");
		}
	}

	/**
	 * 
	     * @Title: deleteRmPropertyMationById
	     * @Description: 删除小程序样式属性信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteRmPropertyMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = rmPropertyDao.queryRmPropertyValueNumById(map);
		if(bean == null){
			Map<String, Object> useThisBean = rmPropertyDao.queryUseRmPropertyNumById(map);
			if(useThisBean == null){
				rmPropertyDao.deleteRmPropertyMationById(map);
			}else{
				if(Integer.parseInt(useThisBean.get("usePropertyNum").toString()) == 0){//该样式属性没有被使用
					rmPropertyDao.deleteRmPropertyMationById(map);
				}else{
					outputObject.setreturnMessage("该样式属性正在使用中，无法删除。");
				}
			}
		}else{
			if(Integer.parseInt(bean.get("propertyValueNum").toString()) == 0){//该样式属性下没有值
				Map<String, Object> useThisBean = rmPropertyDao.queryUseRmPropertyNumById(map);
				if(useThisBean == null){
					rmPropertyDao.deleteRmPropertyMationById(map);
				}else{
					if(Integer.parseInt(useThisBean.get("usePropertyNum").toString()) == 0){//该样式属性没有被使用
						rmPropertyDao.deleteRmPropertyMationById(map);
					}else{
						outputObject.setreturnMessage("该样式属性正在使用中，无法删除。");
					}
				}
			}else{
				outputObject.setreturnMessage("该样式属性下存在值，无法删除。");
			}
		}
		
	}

	/**
	 * 
	     * @Title: queryRmPropertyMationToEditById
	     * @Description: 编辑小程序样式属性信息时进行回显
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryRmPropertyMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = rmPropertyDao.queryRmPropertyMationToEditById(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editRmPropertyMationById
	     * @Description: 编辑小程序样式属性信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editRmPropertyMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = rmPropertyDao.queryRmPropertyMationByNameAndId(map);
		if(bean == null){
			rmPropertyDao.editRmPropertyMationById(map);
		}else{
			outputObject.setreturnMessage("该样式属性名称已存在，不可进行二次保存");
		}
	}

	/**
	 * 
	     * @Title: queryRmPropertyListToShow
	     * @Description: 获取小程序样式属性供展示
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryRmPropertyListToShow(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = rmPropertyDao.queryRmPropertyListToShow(map);
		if(beans != null && !beans.isEmpty()){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}
	
}
