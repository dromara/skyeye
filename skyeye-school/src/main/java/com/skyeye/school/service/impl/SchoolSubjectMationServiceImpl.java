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
import com.skyeye.school.dao.SchoolSubjectMationDao;
import com.skyeye.school.service.SchoolSubjectMationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class SchoolSubjectMationServiceImpl implements SchoolSubjectMationService {

	@Autowired
	private SchoolSubjectMationDao schoolSubjectMationDao;
	
	/**
	 * 
	     * @Title: querySchoolSubjectMationList
	     * @Description: 查出所有科目列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySchoolSubjectMationList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		// 获取当前用户拥有的学校的数据权限
		map.put("schoolPowerId", inputObject.getLogParams().get("schoolPowerId"));
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = schoolSubjectMationDao.querySchoolSubjectMationList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 
	     * @Title: insertSchoolSubjectMationMation
	     * @Description: 新增科目
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertSchoolSubjectMationMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = schoolSubjectMationDao.querySchoolSubjectMationByName(map);
		if(bean != null && !bean.isEmpty()){
			outputObject.setreturnMessage("该名称已存在，不能重复添加！");
		}else{
			map.put("id", ToolUtil.getSurFaceId());
			map.put("createId", inputObject.getLogParams().get("id"));
			map.put("createTime", DateUtil.getTimeAndToString());
			schoolSubjectMationDao.insertSchoolSubjectMationMation(map);
		}
	}

	/**
	 * 
	     * @Title: querySchoolSubjectMationToEditById
	     * @Description: 通过id查询一条科目信息回显编辑
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySchoolSubjectMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object>	bean = schoolSubjectMationDao.querySchoolSubjectMationToEditById(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editSchoolSubjectMationById
	     * @Description: 编辑科目信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editSchoolSubjectMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = schoolSubjectMationDao.querySchoolSubjectMationByNameAndId(map);
		if(bean != null && !bean.isEmpty()){
			outputObject.setreturnMessage("该名称已存在，不能重复添加！");
		}else{
			schoolSubjectMationDao.editSchoolSubjectMationById(map);
		}
	}

	/**
	 * 
	     * @Title: deleteSchoolSubjectMationById
	     * @Description: 删除科目信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteSchoolSubjectMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		schoolSubjectMationDao.deleteSchoolSubjectMationById(map);
	}

	/**
	 * 
	     * @Title: querySchoolSubjectMationListToShow
	     * @Description: 获取科目信息列表展示为select/chockbox
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySchoolSubjectMationListToShow(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		//获取当前用户拥有的学校的数据权限
		map.put("schoolPowerId", inputObject.getLogParams().get("schoolPowerId"));
		List<Map<String, Object>> beans = schoolSubjectMationDao.querySchoolSubjectMationListToShow(map);
		if(!beans.isEmpty()){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}

	/**
	 * 
	     * @Title: querySchoolSubjectMationListToShowByGradeId
	     * @Description: 根据年级获取科目信息列表展示为select/chockbox
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySchoolSubjectMationListToShowByGradeId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = schoolSubjectMationDao.querySchoolSubjectMationListToShowByGradeId(map);
		if(!beans.isEmpty()){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}

}
