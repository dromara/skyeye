/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.dao.SysDataBaseDao;
import com.skyeye.eve.service.SysDataBaseService;

@Service
public class SysDataBaseServiceImpl implements SysDataBaseService{
	
	@Autowired
	private SysDataBaseDao sysDataBaseDao;
	
	@Value("${jdbc.database.name}")  
    private String dbName;

	/**
	 * 
	     * @Title: querySysDataBaseList
	     * @Description: 获取数据库表名信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySysDataBaseSelectList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("dbName", dbName);
		List<Map<String, Object>> beans = sysDataBaseDao.querySysDataBaseSelectList(map);
		outputObject.setBeans(beans);
		if(!beans.isEmpty()){
			outputObject.settotal(beans.size());
		}
	}

	/**
	 * 
	     * @Title: querySysDataBaseList
	     * @Description: 获取数据库表备注信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySysDataBaseDescSelectList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("dbName", dbName);
		List<Map<String, Object>> beans = sysDataBaseDao.querySysDataBaseDescSelectList(map);
		outputObject.setBeans(beans);
		if(!beans.isEmpty()){
			outputObject.settotal(beans.size());
		}
	}

}
