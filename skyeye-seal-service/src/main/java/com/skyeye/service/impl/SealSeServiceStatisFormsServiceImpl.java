/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.dao.SealSeServiceStatisFormsDao;
import com.skyeye.service.SealSeServiceStatisFormsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: SealSeServiceStatisFormsServiceImpl
 * @Description: 售后服务模块统计服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 23:19
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class SealSeServiceStatisFormsServiceImpl implements SealSeServiceStatisFormsService{
	
	@Autowired
	private SealSeServiceStatisFormsDao sealSeServiceStatisFormsDao;

	/**
    *
    * @Title: queryCustomOrderTable
    * @Description: 获取客户工单数量统计表格
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@Override
	public void queryCustomOrderTable(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
        List<Map<String, Object>> beans = sealSeServiceStatisFormsDao.queryCustomOrderTable(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
    *
    * @Title: queryUserWorkerOrderTable
    * @Description: 获取员工工单数量统计表格
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@Override
	public void queryUserWorkerOrderTable(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
        List<Map<String, Object>> beans = sealSeServiceStatisFormsDao.queryUserWorkerOrderTable(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
    *
    * @Title: queryWarrantyOrderTable
    * @Description: 获取质保类型工单数量统计表格
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@Override
	public void queryWarrantyOrderTable(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
        List<Map<String, Object>> beans = sealSeServiceStatisFormsDao.queryWarrantyOrderTable(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
    *
    * @Title: queryProductTypeOrderTable
    * @Description: 获取设备产品类型工单数量统计表格
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@Override
	public void queryProductTypeOrderTable(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
        List<Map<String, Object>> beans = sealSeServiceStatisFormsDao.queryProductTypeOrderTable(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

}
