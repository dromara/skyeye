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
import com.skyeye.eve.dao.SysWorkLogDao;
import com.skyeye.eve.service.SysWorkLogService;

@Service
public class SysWorkLogServiceImpl implements SysWorkLogService{
	
	@Autowired
	private SysWorkLogDao sysWorkLogDao;

	/**
	 * 
	     * @Title: querySysMenuList
	     * @Description: 获取日志列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySysWorkLogList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("total", (Integer.parseInt(map.get("page").toString()) - 1) * Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = sysWorkLogDao.querySysWorkLogList(map);
		int total = sysWorkLogDao.querySysWorkLogListCount(map);
		outputObject.setBeans(beans);
		outputObject.settotal(total);
	}
	
}
