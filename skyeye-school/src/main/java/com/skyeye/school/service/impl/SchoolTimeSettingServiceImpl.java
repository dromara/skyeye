/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.school.service.impl;

import cn.hutool.json.JSONUtil;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.school.dao.SchoolTimeSettingDao;
import com.skyeye.school.service.SchoolTimeSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class SchoolTimeSettingServiceImpl implements SchoolTimeSettingService{
	
	@Autowired
	private SchoolTimeSettingDao schoolTimeSettingDao;

	/**
	 * 
	     * @Title: querySchoolTimeSettingMation
	     * @Description: 获取年级时间设置相关参数
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySchoolTimeSettingMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		//获取当前用户拥有的学校的数据权限
		map.put("schoolPowerId", inputObject.getLogParams().get("schoolPowerId"));
		//获取年级做行
		List<Map<String, Object>> gradeList = schoolTimeSettingDao.queryGradeListToRow(map);
		//获取学期做列
		List<Map<String, Object>> semesterList = schoolTimeSettingDao.querySemesterListToCol(map);
		//获取已有的时间
		List<Map<String, Object>> timeList = schoolTimeSettingDao.queryTimeSettingList(map);
		map.put("gradeList", gradeList);
		map.put("semesterList", semesterList);
		map.put("timeList", timeList);
		outputObject.setBean(map);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editSchoolTimeSettingMation
	     * @Description: 修改年级时间设置
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(value="transactionManager")
	public void editSchoolTimeSettingMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String arrayStr = map.get("arrayStr").toString();
		if(ToolUtil.isJson(arrayStr)){
			String schoolId = map.get("schoolId").toString();
			String userId = inputObject.getLogParams().get("id").toString();
			//处理数据
			List<Map<String, Object>> entitys = JSONUtil.toList(arrayStr, null);
			for(Map<String, Object> entity: entitys){
				entity.put("id", ToolUtil.getSurFaceId());
				entity.put("createId", userId);
				entity.put("createTime", DateUtil.getTimeAndToString());
			}
			schoolTimeSettingDao.deleteSchoolTimeSettingMation(schoolId);
			if(!entitys.isEmpty()){
				schoolTimeSettingDao.insertSchoolTimeSettingMation(entitys);
			}
		}else{
			outputObject.setreturnMessage("数据格式错误");
		}
	}
	
}
