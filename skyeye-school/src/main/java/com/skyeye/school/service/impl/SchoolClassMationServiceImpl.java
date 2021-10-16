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
import com.skyeye.school.dao.SchoolClassMationDao;
import com.skyeye.school.service.SchoolClassMationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: SchoolClassMationServiceImpl
 * @Description: 班级信息管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 11:20
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class SchoolClassMationServiceImpl implements SchoolClassMationService{
	
	@Autowired
	private SchoolClassMationDao schoolClassMationDao;

	/**
	 * 
	     * @Title: querySchoolClassMationList
	     * @Description: 获取班级信息列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySchoolClassMationList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		// 获取当前用户拥有的学校的数据权限
		map.put("schoolPowerId", inputObject.getLogParams().get("schoolPowerId"));
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = schoolClassMationDao.querySchoolClassMationList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 
	     * @Title: insertSchoolClassMation
	     * @Description: 添加班级信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertSchoolClassMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = schoolClassMationDao.querySchoolClassMationByName(map);
		if(bean == null || bean.isEmpty()){
			Map<String, Object> user = inputObject.getLogParams();
			map.put("id", ToolUtil.getSurFaceId());
			map.put("createId", user.get("id"));
			map.put("createTime", DateUtil.getTimeAndToString());
			map.put("state", 1);//班级状态  1.当前班级  2.历史班级
			
			//获取年级当前最新的班级是哪一届的
			Map<String, Object> newClassGrade = schoolClassMationDao.queryNewClassGradeMationByGradeId(map);
			if(newClassGrade != null && !newClassGrade.isEmpty()){
				//当前年级存在班级信息，判断年份
				int nowGradeYear = Integer.parseInt(newClassGrade.get("sessionYear").toString());
				int paramsGradeYear = Integer.parseInt(map.get("year").toString());
				if(paramsGradeYear < nowGradeYear){
					//传递的年届小于当前最新的班级年届；说明是补充以前的班级信息，状态默认为历史班级
					map.put("state", 2);
				}else if(paramsGradeYear == nowGradeYear){
					//传递的年届等于当前最新的班级年届；说明是新增的当前的班级信息，状态默认为当前班级
				}else if(paramsGradeYear > nowGradeYear){
					//传递的年届大于当前最新的班级年届；说明是新增的最新的班级信息，状态默认为当前班级，同时修改之前的老年份的班级信息为历史班级
					schoolClassMationDao.editClassGradeMationToHistoryByGradeId(map.get("gradeId").toString());
				}
			}
			
			schoolClassMationDao.insertSchoolClassMation(map);
		}else{
			outputObject.setreturnMessage("该班级信息已注册，请确认。");
		}
	}

	/**
	 * 
	     * @Title: deleteSchoolClassMationById
	     * @Description: 删除班级信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteSchoolClassMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		schoolClassMationDao.deleteSchoolClassMationById(map);
	}

	/**
	 * 
	     * @Title: querySchoolClassMationToEditById
	     * @Description: 编辑班级信息信息时进行回显
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySchoolClassMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = schoolClassMationDao.querySchoolClassMationToEditById(map);
		if(bean != null && !bean.isEmpty()){
			outputObject.setBean(bean);
			outputObject.settotal(1);
		}else{
			outputObject.setreturnMessage("该数据不存在。");
		}
	}

	/**
	 * 
	     * @Title: editSchoolClassMationById
	     * @Description: 编辑班级信息信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editSchoolClassMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = schoolClassMationDao.querySchoolClassMationByName(map);
		if(bean == null){
			schoolClassMationDao.editSchoolClassMationById(map);
		}else{
			outputObject.setreturnMessage("该班级信息已注册，请确认。");
		}
	}
	
}
