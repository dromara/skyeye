/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.school.dao.SchoolTeacherSubjectDao;
import com.skyeye.school.service.SchoolTeacherSubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SchoolTeacherSubjectServiceImpl implements SchoolTeacherSubjectService{
	
	@Autowired
	private SchoolTeacherSubjectDao schoolTeacherSubjectDao;

	/**
	 * 
	     * @Title: querySchoolTeacherSubjectList
	     * @Description: 获取教师技能列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySchoolTeacherSubjectList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		// 获取当前用户拥有的学校的数据权限
		map.put("schoolPowerId", inputObject.getLogParams().get("schoolPowerId"));
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = schoolTeacherSubjectDao.querySchoolTeacherSubjectList(map);
		// 获取技能列表
		for(Map<String, Object> bean: beans){
			bean.put("skill", schoolTeacherSubjectDao.querySchoolTeacherSkillById(bean.get("id").toString()));
		}
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 
	     * @Title: querySchoolTeacherSubjectMationById
	     * @Description: 获取教师部分信息以及当前拥有的技能列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySchoolTeacherSubjectMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		//获取教师信息
		Map<String, Object> teacher = schoolTeacherSubjectDao.querySchoolTeacherMationById(map);
		if(teacher != null && !teacher.isEmpty()){
			//获取技能列表
			teacher.put("skill", schoolTeacherSubjectDao.querySchoolTeacherSkillById(teacher.get("id").toString()));
			outputObject.setBean(teacher);
		}else{
			outputObject.setreturnMessage("该教师信息不存在。");
		}
	}

	/**
	 * 
	     * @Title: editSchoolTeacherSubjectMation
	     * @Description: 教师科目技能信息绑定
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void editSchoolTeacherSubjectMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		//获取员工id
		String staffId = map.get("id").toString();
		//处理教师的科目技能信息
		String[] propertyIds = map.get("propertyIds").toString().split(",");
		List<Map<String, Object>> beans = new ArrayList<>();
		Map<String, Object> bean = null;
		for(String str : propertyIds){
			if(!ToolUtil.isBlank(str)){
				bean = new HashMap<>();
				bean.put("id", ToolUtil.getSurFaceId());
				bean.put("staffId", staffId);
				bean.put("subjectId", str);
				beans.add(bean);
			}
		}
		//删除之前的绑定信息
		schoolTeacherSubjectDao.deleteSchoolTeacherSubjectMation(staffId);
		if(!beans.isEmpty()){
			//新增绑定信息
			schoolTeacherSubjectDao.insertSchoolTeacherSubjectMation(beans);
		}
	}
	
}
