/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.dao.ErpProducePageDao;
import com.skyeye.eve.service.ErpProducePageService;

@Service
public class ErpProducePageServiceImpl implements ErpProducePageService{
	
	@Autowired
	private ErpProducePageDao erpProducePageDao;
	
	/**
	 * 
	    * @Title: queryDepartmentPickMaterial
	    * @Description: 统计当前部门月度领料图
	    * @param inputObject
	    * @param outputObject
	    * @throws Exception    参数
	    * @return void    返回类型
	    * @throws
	 */
	@Override
	public void queryDepartmentPickMaterial(InputObject inputObject, OutputObject outputObject) throws Exception {
		String year = inputObject.getParams().get("year").toString();
		Map<String, Object> user = inputObject.getLogParams();
		String departmentId = user.get("departmentId").toString();
		// 获取当前部门指定月度所领取的物料列表
		List<Map<String, Object>> pickMaterial = erpProducePageDao.queryPickMaterialYearByDepartmentId(departmentId, year);
		// 获取所领取的物料月度数量
		for(Map<String, Object> bean : pickMaterial){
			bean.put("yearPickMaterialNum", erpProducePageDao.queryPickMaterialNumYearByDepartmentId(departmentId, year, bean.get("materialId").toString()));
		}
		outputObject.setBeans(pickMaterial);
	}

	/**
	 * 
	    * @Title: queryDepartmentPatchMaterial
	    * @Description: 统计当前部门月度补料图
	    * @param inputObject
	    * @param outputObject
	    * @throws Exception    参数
	    * @return void    返回类型
	    * @throws
	 */
	@Override
	public void queryDepartmentPatchMaterial(InputObject inputObject, OutputObject outputObject) throws Exception {
		String year = inputObject.getParams().get("year").toString();
		Map<String, Object> user = inputObject.getLogParams();
		String departmentId = user.get("departmentId").toString();
		// 获取当前部门指定月度所有补的物料列表
		List<Map<String, Object>> pickMaterial = erpProducePageDao.queryPatchMaterialYearByDepartmentId(departmentId, year);
		// 获取所有补的物料月度数量
		for(Map<String, Object> bean : pickMaterial){
			bean.put("yearPatchMaterialNum", erpProducePageDao.queryPatchMaterialNumYearByDepartmentId(departmentId, year, bean.get("materialId").toString()));
		}
		outputObject.setBeans(pickMaterial);
	}

	/**
	 * 
	    * @Title: queryDepartmentReturnMaterial
	    * @Description: 统计当前部门月度退料图
	    * @param inputObject
	    * @param outputObject
	    * @throws Exception    参数
	    * @return void    返回类型
	    * @throws
	 */
	@Override
	public void queryDepartmentReturnMaterial(InputObject inputObject, OutputObject outputObject) throws Exception {
		String year = inputObject.getParams().get("year").toString();
		Map<String, Object> user = inputObject.getLogParams();
		String departmentId = user.get("departmentId").toString();
		// 获取当前部门指定月度所有退的物料列表
		List<Map<String, Object>> pickMaterial = erpProducePageDao.queryReturnMaterialYearByDepartmentId(departmentId, year);
		// 获取所有退的物料月度数量
		for(Map<String, Object> bean : pickMaterial){
			bean.put("yearReturnMaterialNum", erpProducePageDao.queryReturnMaterialNumYearByDepartmentId(departmentId, year, bean.get("materialId").toString()));
		}
		outputObject.setBeans(pickMaterial);
	}

	/**
	 * 
	    * @Title: queryDepartmentMachin
	    * @Description: 统计当前部门月度新建加工单图
	    * @param inputObject
	    * @param outputObject
	    * @throws Exception    参数
	    * @return void    返回类型
	    * @throws
	 */
	@Override
	public void queryDepartmentMachin(InputObject inputObject, OutputObject outputObject) throws Exception {
		String year = inputObject.getParams().get("year").toString();
		Map<String, Object> user = inputObject.getLogParams();
		String departmentId = user.get("departmentId").toString();
		List<Map<String, Object>> beans = erpProducePageDao.queryDepartmentMachin(departmentId, year);
		outputObject.setBeans(beans);
	}
	
}
