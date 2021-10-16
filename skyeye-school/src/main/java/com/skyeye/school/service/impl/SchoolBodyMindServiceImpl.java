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
import com.skyeye.school.dao.SchoolBodyMindDao;
import com.skyeye.school.service.SchoolBodyMindService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class SchoolBodyMindServiceImpl implements SchoolBodyMindService {

	@Autowired
	private SchoolBodyMindDao schoolBodyMindDao;
	
	/**
	 * 
	     * @Title: querySchoolBodyMindList
	     * @Description: 查出所有身心障碍列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySchoolBodyMindList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		// 获取当前用户拥有的学校的数据权限
		map.put("schoolPowerId", inputObject.getLogParams().get("schoolPowerId"));
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = schoolBodyMindDao.querySchoolBodyMindList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 
	     * @Title: insertSchoolBodyMindMation
	     * @Description: 新增身心障碍
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertSchoolBodyMindMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = schoolBodyMindDao.querySchoolBodyMindByName(map);
		if(bean != null && !bean.isEmpty()){
			outputObject.setreturnMessage("该名称已存在，不能重复添加！");
		}else{
			map.put("id", ToolUtil.getSurFaceId());
			map.put("createId", inputObject.getLogParams().get("id"));
			map.put("createTime", DateUtil.getTimeAndToString());
			schoolBodyMindDao.insertSchoolBodyMindMation(map);
		}
	}

	/**
	 * 
	     * @Title: querySchoolBodyMindToEditById
	     * @Description: 通过id查询一条身心障碍信息回显编辑
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySchoolBodyMindToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object>	bean = schoolBodyMindDao.querySchoolBodyMindToEditById(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editSchoolBodyMindById
	     * @Description: 编辑身心障碍信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editSchoolBodyMindById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = schoolBodyMindDao.querySchoolBodyMindByNameAndId(map);
		if(bean != null && !bean.isEmpty()){
			outputObject.setreturnMessage("该名称已存在，不能重复添加！");
		}else{
			schoolBodyMindDao.editSchoolBodyMindById(map);
		}
	}

	/**
	 * 
	     * @Title: deleteSchoolBodyMindById
	     * @Description: 删除身心障碍信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteSchoolBodyMindById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		schoolBodyMindDao.deleteSchoolBodyMindById(map);
	}

	/**
	 * 
	     * @Title: querySchoolBodyMindListToShow
	     * @Description: 获取身心障碍信息列表展示为select/chockbox
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySchoolBodyMindListToShow(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		//获取当前用户拥有的学校的数据权限
		map.put("schoolPowerId", inputObject.getLogParams().get("schoolPowerId"));
		List<Map<String, Object>> beans = schoolBodyMindDao.querySchoolBodyMindListToShow(map);
		if(!beans.isEmpty()){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}

}
