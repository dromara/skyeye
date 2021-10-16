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
import com.skyeye.school.dao.SchoolFamilySituationDao;
import com.skyeye.school.service.SchoolFamilySituationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class SchoolFamilySituationServiceImpl implements SchoolFamilySituationService {

	@Autowired
	private SchoolFamilySituationDao schoolFamilySituationDao;
	
	/**
	 * 
	     * @Title: querySchoolFamilySituationList
	     * @Description: 查出所有家庭情况列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySchoolFamilySituationList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		// 获取当前用户拥有的学校的数据权限
		map.put("schoolPowerId", inputObject.getLogParams().get("schoolPowerId"));
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = schoolFamilySituationDao.querySchoolFamilySituationList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 
	     * @Title: insertSchoolFamilySituationMation
	     * @Description: 新增家庭情况
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertSchoolFamilySituationMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = schoolFamilySituationDao.querySchoolFamilySituationByName(map);
		if(bean != null && !bean.isEmpty()){
			outputObject.setreturnMessage("该名称已存在，不能重复添加！");
		}else{
			map.put("id", ToolUtil.getSurFaceId());
			map.put("createId", inputObject.getLogParams().get("id"));
			map.put("createTime", DateUtil.getTimeAndToString());
			schoolFamilySituationDao.insertSchoolFamilySituationMation(map);
		}
	}

	/**
	 * 
	     * @Title: querySchoolFamilySituationToEditById
	     * @Description: 通过id查询一条家庭情况信息回显编辑
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySchoolFamilySituationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object>	bean = schoolFamilySituationDao.querySchoolFamilySituationToEditById(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editSchoolFamilySituationById
	     * @Description: 编辑家庭情况信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editSchoolFamilySituationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = schoolFamilySituationDao.querySchoolFamilySituationByNameAndId(map);
		if(bean != null && !bean.isEmpty()){
			outputObject.setreturnMessage("该名称已存在，不能重复添加！");
		}else{
			schoolFamilySituationDao.editSchoolFamilySituationById(map);
		}
	}

	/**
	 * 
	     * @Title: deleteSchoolFamilySituationById
	     * @Description: 删除家庭情况信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteSchoolFamilySituationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		schoolFamilySituationDao.deleteSchoolFamilySituationById(map);
	}
	
	/**
	 * 
	     * @Title: querySchoolFamilySituationListToShow
	     * @Description: 获取家庭情况信息列表展示为select/chockbox
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySchoolFamilySituationListToShow(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		//获取当前用户拥有的学校的数据权限
		map.put("schoolPowerId", inputObject.getLogParams().get("schoolPowerId"));
		List<Map<String, Object>> beans = schoolFamilySituationDao.querySchoolFamilySituationListToShow(map);
		if(!beans.isEmpty()){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}

}
