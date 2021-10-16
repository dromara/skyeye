/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.dao.ErpDepartStockDao;
import com.skyeye.service.ErpDepartStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ErpDepartStockServiceImpl implements ErpDepartStockService {
	
	@Autowired
	private ErpDepartStockDao erpDepartStockDao;

	/**
     * 获取部门物料库存信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void queryDepartStockReserveList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		String departMentId = user.get("departmentId").toString();
		Page pages = PageHelper.startPage(Integer.parseInt(params.get("page").toString()), Integer.parseInt(params.get("limit").toString()));
        List<Map<String, Object>> beans = erpDepartStockDao.queryMaterialReserveList(params);
        // 获取规格单位信息
  		for (Map<String, Object> bean : beans) {
  			// 获取商品规格参数信息-这里主要用到规格的总库存
			List<Map<String, Object>> norms = erpDepartStockDao.queryMaterialNormsMationDetailsById(bean.get("id").toString(), departMentId);
  			if ("1".equals(bean.get("unit").toString())) {// 不是多单位
  				norms.get(0).put("unitName", bean.get("unitName").toString());
  			}
  			bean.put("norms", norms);
  		}
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}
	
}
