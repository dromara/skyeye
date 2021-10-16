/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.dao.CrmPageDao;
import com.skyeye.eve.service.CrmPageService;

@Service
public class CrmPageServiceImpl implements CrmPageService {
	
	@Autowired
	private CrmPageDao crmPageDao;

	/**
	 * 
	    * @Title: queryInsertNumByYear
	    * @Description: 获取指定年度的客户新增量，联系人新增量
	    * @param inputObject
	    * @param outputObject
	    * @throws Exception    参数
	    * @return void    返回类型
	    * @throws
	 */
	@Override
	public void queryInsertNumByYear(InputObject inputObject, OutputObject outputObject) throws Exception {
		String year = inputObject.getParams().get("year").toString();
		List<Map<String, Object>> beans = crmPageDao.queryInsertNumByYear(year);
		outputObject.setBeans(beans);
	}

	/**
	 * 
	    * @Title: queryCustomNumByOtherType
	    * @Description: 根据客户分类，客户来源，所属行业，客户分组统计客户数量
	    * @param inputObject
	    * @param outputObject
	    * @throws Exception    参数
	    * @return void    返回类型
	    * @throws
	 */
	@Override
	public void queryCustomNumByOtherType(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = new HashMap<>();
		// 1.根据客户分类统计客户数量
		List<Map<String, Object>> numType = crmPageDao.queryCustomNumByType();
		// 2.根据客户来源统计客户数量
		List<Map<String, Object>> numFrom = crmPageDao.queryCustomNumByFrom();
		// 3.根据所属行业统计客户数量
		List<Map<String, Object>> numIndustry = crmPageDao.queryCustomNumByIndustry();
		// 4.根据客户分组统计客户数量
		List<Map<String, Object>> numGroup = crmPageDao.queryCustomNumByGroup();
		map.put("numType", numType);
		map.put("numFrom", numFrom);
		map.put("numIndustry", numIndustry);
		map.put("numGroup", numGroup);
		outputObject.setBean(map);
	}

	/**
	 * 
	    * @Title: queryCustomDocumentaryType
	    * @Description: 客户跟单方式分析
	    * @param inputObject
	    * @param outputObject
	    * @throws Exception    参数
	    * @return void    返回类型
	    * @throws
	 */
	@Override
	public void queryCustomDocumentaryType(InputObject inputObject, OutputObject outputObject) throws Exception {
		String year = inputObject.getParams().get("year").toString();
		List<Map<String, Object>> beans = crmPageDao.queryCustomDocumentaryType(year);
		outputObject.setBeans(beans);
	}

	/**
	 * 
	    * @Title: queryNewContractNum
	    * @Description: 获取合同在指定年度的月新增量
	    * @param inputObject
	    * @param outputObject
	    * @throws Exception    参数
	    * @return void    返回类型
	    * @throws
	 */
	@Override
	public void queryNewContractNum(InputObject inputObject, OutputObject outputObject) throws Exception {
		String year = inputObject.getParams().get("year").toString();
		List<Map<String, Object>> beans = crmPageDao.queryNewContractNum(year);
		outputObject.setBeans(beans);
	}

	/**
	 * 
	    * @Title: queryNewDocumentaryNum
	    * @Description: 获取员工跟单在指定年度的月新增量
	    * @param inputObject
	    * @param outputObject
	    * @throws Exception    参数
	    * @return void    返回类型
	    * @throws
	 */
	@Override
	public void queryNewDocumentaryNum(InputObject inputObject, OutputObject outputObject) throws Exception {
		String year = inputObject.getParams().get("year").toString();
		List<Map<String, Object>> beans = crmPageDao.queryNewDocumentaryNum(year);
		outputObject.setBeans(beans);
	}
	
	
	
}
