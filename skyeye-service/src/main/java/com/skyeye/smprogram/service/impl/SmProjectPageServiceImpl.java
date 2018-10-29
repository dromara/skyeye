package com.skyeye.smprogram.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.smprogram.dao.SmProjectPageDao;
import com.skyeye.smprogram.service.SmProjectPageService;

@Service
public class SmProjectPageServiceImpl implements SmProjectPageService{
	
	@Autowired
	private SmProjectPageDao smProjectPageDao;
	
	/**
	 * 
	     * @Title: queryProPageMationByProIdList
	     * @Description: 根据项目获取项目内部的页面
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryProPageMationByProIdList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = smProjectPageDao.queryProPageMationByProIdList(map);
		if(beans != null && !beans.isEmpty()){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}
	
	
	
}
