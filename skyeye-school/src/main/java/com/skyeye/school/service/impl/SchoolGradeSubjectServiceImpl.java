/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.school.dao.SchoolGradeSubjectDao;
import com.skyeye.school.service.SchoolGradeSubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: SchoolGradeSubjectServiceImpl
 * @Description: 年级与技能的绑定关系管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 20:46
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class SchoolGradeSubjectServiceImpl implements SchoolGradeSubjectService{
	
	@Autowired
	private SchoolGradeSubjectDao schoolGradeSubjectDao;

	/**
	 * 
	     * @Title: querySchoolGradeSubjectList
	     * @Description: 获取年级技能列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySchoolGradeSubjectList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		// 获取当前用户拥有的学校的数据权限
		map.put("schoolPowerId", inputObject.getLogParams().get("schoolPowerId"));
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = schoolGradeSubjectDao.querySchoolGradeSubjectList(map);
		// 获取技能列表
		for(Map<String, Object> bean: beans){
			bean.put("skill", schoolGradeSubjectDao.querySchoolGradeSkillById(bean.get("id").toString()));
		}
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 
	     * @Title: querySchoolGradeSubjectMationById
	     * @Description: 获取年级部分信息以及当前拥有的技能列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySchoolGradeSubjectMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		//获取年级信息
		Map<String, Object> grade = schoolGradeSubjectDao.querySchoolGradeMationById(map);
		if(grade != null && !grade.isEmpty()){
			//获取技能列表
			grade.put("skill", schoolGradeSubjectDao.querySchoolGradeSkillById(grade.get("id").toString()));
			outputObject.setBean(grade);
		}else{
			outputObject.setreturnMessage("该年级信息不存在。");
		}
	}

	/**
	 * 
	     * @Title: editSchoolGradeSubjectMation
	     * @Description: 年级科目技能信息绑定
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void editSchoolGradeSubjectMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		//获取年级id
		String gradeId = map.get("id").toString();
		//处理年级的科目技能信息
		String[] propertyIds = map.get("propertyIds").toString().split(",");
		List<Map<String, Object>> beans = new ArrayList<>();
		Map<String, Object> bean = null;
		for(String str : propertyIds){
			if(!ToolUtil.isBlank(str)){
				bean = new HashMap<>();
				bean.put("id", ToolUtil.getSurFaceId());
				bean.put("gradeId", gradeId);
				bean.put("subjectId", str);
				beans.add(bean);
			}
		}
		//删除之前的绑定信息
		schoolGradeSubjectDao.deleteSchoolGradeSubjectMation(gradeId);
		if(!beans.isEmpty()){
			//新增绑定信息
			schoolGradeSubjectDao.insertSchoolGradeSubjectMation(beans);
		}
	}
	
}
