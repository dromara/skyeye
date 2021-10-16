/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.school.service.impl;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.school.dao.SchoolGradeMationDao;
import com.skyeye.school.service.SchoolGradeMationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class SchoolGradeMationServiceImpl implements SchoolGradeMationService{
	
	@Autowired
	private SchoolGradeMationDao schoolGradeMationDao;

	/**
	 * 
	     * @Title: querySchoolGradeMationList
	     * @Description: 获取年级信息列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySchoolGradeMationList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		//获取当前用户拥有的学校的数据权限
		map.put("schoolPowerId", inputObject.getLogParams().get("schoolPowerId"));
		List<Map<String, Object>> beans = schoolGradeMationDao.querySchoolGradeMationList(map);
		if(!beans.isEmpty()){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}

	/**
	 * 
	     * @Title: insertSchoolGradeMation
	     * @Description: 添加年级信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertSchoolGradeMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = schoolGradeMationDao.querySchoolGradeMationByName(map);
		if(bean == null){
			//获取序号做排序
			Map<String, Object> itemCount = schoolGradeMationDao.querySchoolGradeOrderByNumber(map);
			if(itemCount ==  null){
				map.put("gradeOrder", 1);
			}else{
				map.put("gradeOrder", Integer.parseInt(itemCount.get("gradeOrder").toString()) + 1);
			}
			map.put("id", ToolUtil.getSurFaceId());
			map.put("createId", inputObject.getLogParams().get("id"));
			map.put("createTime", DateUtil.getTimeAndToString());
			schoolGradeMationDao.insertSchoolGradeMation(map);
		}else{
			outputObject.setreturnMessage("该年级信息已注册，请确认。");
		}
	}

	/**
	 * 
	     * @Title: deleteSchoolGradeMationById
	     * @Description: 删除年级信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteSchoolGradeMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		schoolGradeMationDao.deleteSchoolGradeMationById(map);
	}

	/**
	 * 
	     * @Title: querySchoolGradeMationToEditById
	     * @Description: 编辑年级信息时进行回显
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySchoolGradeMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = schoolGradeMationDao.querySchoolGradeMationToEditById(map);
		if(bean != null && !bean.isEmpty()){
			outputObject.setBean(bean);
			outputObject.settotal(1);
		}else{
			outputObject.setreturnMessage("该数据不存在。");
		}
	}

	/**
	 * 
	     * @Title: editSchoolGradeMationById
	     * @Description: 编辑年级信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editSchoolGradeMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = schoolGradeMationDao.querySchoolGradeMationByName(map);
		if(bean == null){
			schoolGradeMationDao.editSchoolGradeMationById(map);
		}else{
			outputObject.setreturnMessage("该年级信息已注册，请确认。");
		}
	}

	/**
	 * 
     * @Title: queryAllGradeMationBySchoolId
     * @Description: 查询学校中所有的正常年级
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
	 */
	@Override
	public void queryAllGradeMationBySchoolId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		//获取当前用户拥有的学校的数据权限
		map.put("schoolPowerId", inputObject.getLogParams().get("schoolPowerId"));
		List<Map<String, Object>> beans = schoolGradeMationDao.queryAllGradeMationBySchoolId(map);
		if(!beans.isEmpty()){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}

	/**
	 * 
	     * @Title: editGradeMationOrderNumToUp
	     * @Description: 年级上移
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void editGradeMationOrderNumToUp(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = schoolGradeMationDao.queryGradeUpMationById(map);//获取当前数据的同级分类下的上一条数据
		if(bean == null){
			outputObject.setreturnMessage("当前年级已经是首位，无须进行上移。");
		}else{
			//进行位置交换
			map.put("upOrderBy", bean.get("prevOrderBy"));
			bean.put("upOrderBy", bean.get("thisOrderBy"));
			schoolGradeMationDao.editGradeMationOrderNumToUp(map);
			schoolGradeMationDao.editGradeMationOrderNumToUp(bean);
		}
	}

	/**
	 * 
	     * @Title: editGradeMationOrderNumToUp
	     * @Description: 年级下移
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void editGradeMationOrderNumToDown(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = schoolGradeMationDao.queryGradeDownMationById(map);//获取当前数据的同级分类下的下一条数据
		if(bean == null){
			outputObject.setreturnMessage("当前年级已经是末位，无须进行下移。");
		}else{
			//进行位置交换
			map.put("upOrderBy", bean.get("prevOrderBy"));
			bean.put("upOrderBy", bean.get("thisOrderBy"));
			schoolGradeMationDao.editGradeMationOrderNumToUp(map);
			schoolGradeMationDao.editGradeMationOrderNumToUp(bean);
		}
	}

	/**
	 * 
	     * @Title: querySchoolGradeNowYearMationById
	     * @Description: 获取选中的年级是哪一届的以及这一届的班级信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySchoolGradeNowYearMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		//获取当前年级是哪一届的
		Map<String, Object> gradeNowYear = schoolGradeMationDao.queryGradeNowYearMationById(map);
		if(gradeNowYear != null && !gradeNowYear.isEmpty()){
			String year = gradeNowYear.get("year").toString();
			String gradeId = map.get("gradeId").toString();
			//获取班级
			List<Map<String, Object>> gradeClass = schoolGradeMationDao.queryClassMationList(gradeId, year);
			outputObject.setBean(gradeNowYear);
			outputObject.setBeans(gradeClass);
			outputObject.settotal(gradeClass.size());
		}else{
			outputObject.setreturnMessage("该年级还未曾分班。");
		}
	}
	
}
