/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.school.dao.SchoolTransportationDao;
import com.skyeye.school.service.SchoolTransportationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class SchoolTransportationServiceImpl implements SchoolTransportationService {

	@Autowired
	private SchoolTransportationDao schoolTransportationDao;
	
	/**
	 * 
	     * @Title: querySchoolTransportationList
	     * @Description: 查出所有交通方式列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySchoolTransportationList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		// 获取当前用户拥有的学校的数据权限
		map.put("schoolPowerId", inputObject.getLogParams().get("schoolPowerId"));
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = schoolTransportationDao.querySchoolTransportationList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 
	     * @Title: insertSchoolTransportationMation
	     * @Description: 新增交通方式
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertSchoolTransportationMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = schoolTransportationDao.querySchoolTransportationByName(map);
		if(bean != null && !bean.isEmpty()){
			outputObject.setreturnMessage("该名称已存在，不能重复添加！");
		}else{
			map.put("id", ToolUtil.getSurFaceId());
			map.put("createId", inputObject.getLogParams().get("id"));
			map.put("createTime", DateUtil.getTimeAndToString());
			schoolTransportationDao.insertSchoolTransportationMation(map);
		}
	}

	/**
	 * 
	     * @Title: querySchoolTransportationToEditById
	     * @Description: 通过id查询一条交通方式信息回显编辑
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySchoolTransportationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object>	bean = schoolTransportationDao.querySchoolTransportationToEditById(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editSchoolTransportationById
	     * @Description: 编辑交通方式信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editSchoolTransportationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = schoolTransportationDao.querySchoolTransportationByNameAndId(map);
		if(bean != null && !bean.isEmpty()){
			outputObject.setreturnMessage("该名称已存在，不能重复添加！");
		}else{
			schoolTransportationDao.editSchoolTransportationById(map);
		}
	}

	/**
	 * 
	     * @Title: deleteSchoolTransportationById
	     * @Description: 删除交通方式信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteSchoolTransportationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		schoolTransportationDao.deleteSchoolTransportationById(map);
	}

	/**
	 * 
	     * @Title: querySchoolTransportationListToShow
	     * @Description: 获取交通方式信息列表展示为select/chockbox
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySchoolTransportationListToShow(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		//获取当前用户拥有的学校的数据权限
		map.put("schoolPowerId", inputObject.getLogParams().get("schoolPowerId"));
		List<Map<String, Object>> beans = schoolTransportationDao.querySchoolTransportationListToShow(map);
		if(!beans.isEmpty()){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}

}
