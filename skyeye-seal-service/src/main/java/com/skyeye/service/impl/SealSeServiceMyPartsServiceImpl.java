/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.dao.SealSeServiceMyPartsDao;
import com.skyeye.service.SealSeServiceMyPartsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SealSeServiceMyPartsServiceImpl implements SealSeServiceMyPartsService{
	
	@Autowired
	private SealSeServiceMyPartsDao sealSeServiceMyPartsDao;
	
	/**
	 *
	 * @Title: queryMyApplyPartsList
	 * @Description: 获取我申领的未使用的配件
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@Override
	public void queryMyApplyPartsList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("userId", inputObject.getLogParams().get("id"));
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = sealSeServiceMyPartsDao.queryMyApplyPartsList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 *
	 * @Title: queryMyApplyUsePartsList
	 * @Description: 获取我使用的配件
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@Override
	public void queryMyApplyUsePartsList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("userId", inputObject.getLogParams().get("id"));
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = sealSeServiceMyPartsDao.queryMyApplyUsePartsList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}
	
}
