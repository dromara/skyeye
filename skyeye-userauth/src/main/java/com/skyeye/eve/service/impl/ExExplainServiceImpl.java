/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import cn.hutool.json.JSONUtil;
import com.skyeye.common.constans.Constants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.ExExplainDao;
import com.skyeye.eve.service.ExExplainService;
import com.skyeye.jedis.JedisClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class ExExplainServiceImpl implements ExExplainService {
	
	@Autowired
	private ExExplainDao exExplainDao;
	
	@Autowired
	public JedisClientService jedisClient;

	/**
	 * 
	     * @Title: insertExExplainMation
	     * @Description: 添加代码生成器说明信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertExExplainMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = exExplainDao.queryExExplainMation(map);
		if(bean == null){
			Integer type = Integer.parseInt(map.get("type").toString());
			Map<String, Object> user = inputObject.getLogParams();
			String id = ToolUtil.getSurFaceId();
			map.put("id", id);
			map.put("createId", user.get("id"));
			map.put("createTime", DateUtil.getTimeAndToString());
			exExplainDao.insertExExplainMation(map);
			jedisClient.del(Constants.getSysExplainExexplainRedisKey(type));
			bean = new HashMap<>();
			bean.put("id", id);
			outputObject.setBean(bean);
		}else{
			outputObject.setreturnMessage("该代码生成器说明已存在，不可进行二次保存");
		}
	}

	/**
	 * 
	     * @Title: queryExExplainMation
	     * @Description: 编辑代码生成器说明信息时进行回显
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryExExplainMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = exExplainDao.queryExExplainMation(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editExExplainMationById
	     * @Description: 编辑代码生成器说明信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editExExplainMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = exExplainDao.queryExExplainMation(map);
		if(bean == null){
			outputObject.setreturnMessage("该代码生成器说明不存在，不可进行编辑");
		}else{
			Integer type = Integer.parseInt(map.get("type").toString());
			jedisClient.del(Constants.getSysExplainExexplainRedisKey(type));
			exExplainDao.editExExplainMationById(map);
		}
	}

	/**
	 * 
	     * @Title: queryExExplainMationToShow
	     * @Description: 获取代码生成器说明信息供展示
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryExExplainMationToShow(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Integer type = Integer.parseInt(map.get("type").toString());
		String key = Constants.getSysExplainExexplainRedisKey(type);
		if(jedisClient.exists(key)){
			map = JSONUtil.toBean(jedisClient.get(key), null);
		}else{
			Map<String, Object> bean = exExplainDao.queryExExplainMation(map);
			if(bean == null){
				map.put("title", "标题");
				map.put("content", "等待发布说明。");
			}else{
				jedisClient.set(key, JSONUtil.toJsonStr(bean));
				map = bean;
			}
		}
		outputObject.setBean(map);
	}
	
}
