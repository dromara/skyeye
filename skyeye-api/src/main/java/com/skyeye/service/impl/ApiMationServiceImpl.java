/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.dao.ApiMationDao;
import com.skyeye.dao.ApiPropertyDao;
import com.skyeye.service.ApiMationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: ApiMationServiceImpl
 * @Description: api接口服务类
 * @author: skyeye云系列
 * @date: 2021/11/28 12:31
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class ApiMationServiceImpl implements ApiMationService {

	@Autowired
	private ApiMationDao apiMationDao;

	@Autowired
	private ApiPropertyDao apiPropertyDao;

	/**
	 * @param inputObject
	 * @param outputObject
	 * @return void    返回类型
	 * @throws Exception 参数
	 * @throws
	 * @Title: queryApiMationList
	 * @Description: 获取api接口表
	 */
	@Override
	public void queryApiMationList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("userId", inputObject.getLogParams().get("id"));
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = apiMationDao.queryApiMationList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * @param inputObject
	 * @param outputObject
	 * @return void    返回类型
	 * @throws Exception 参数
	 * @throws
	 * @Title: insertApiMationMation
	 * @Description: 新增api接口
	 */
	@Override
	@Transactional(value = "transactionManager")
	public void insertApiMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = apiMationDao.queryApiMationByName(map);
		if (bean != null && !bean.isEmpty()) {
			outputObject.setreturnMessage("该接口名称已存在，请更换");
		} else {
			this.insertApiMationList(Arrays.asList(map), inputObject.getLogParams().get("id").toString());
		}
	}

	@Override
	@Transactional(value = "transactionManager")
	public void insertApiMationList(List<Map<String, Object>> beans, String userId) throws Exception{
		beans.forEach(bean -> {
			bean.put("id", ToolUtil.getSurFaceId());
			bean.put("userId", userId);
			bean.put("createTime", DateUtil.getTimeAndToString());
		});
		apiMationDao.insertApiMation(beans);
	}

	/**
	 * @param inputObject
	 * @param outputObject
	 * @return void    返回类型
	 * @throws Exception 参数
	 * @throws
	 * @Title: deleteApiMationById
	 * @Description: 删除api接口信息
	 */
	@Override
	@Transactional(value = "transactionManager")
	public void deleteApiMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		apiPropertyDao.deleteApiPropertyByApiId(map);
		apiMationDao.deleteApiMationById(map);
	}

	/**
	 * @param inputObject
	 * @param outputObject
	 * @return void    返回类型
	 * @throws Exception 参数
	 * @throws
	 * @Title: selectApiMationById
	 * @Description: 通过id查找对应的api接口信息
	 */
	@Override
	public void selectApiMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = apiMationDao.queryApiMationToEditById(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * @param inputObject
	 * @param outputObject
	 * @return void    返回类型
	 * @throws Exception 参数
	 * @throws
	 * @Title: editApiMationMationById
	 * @Description: 通过id编辑对应的api接口信息
	 */
	@Override
	@Transactional(value = "transactionManager")
	public void editApiMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = apiMationDao.queryApiMationByName(map);
		if (bean != null && !bean.isEmpty()) {
			outputObject.setreturnMessage("该接口名称已存在，请更换");
		} else {
			map.put("userId", inputObject.getLogParams().get("id"));
			map.put("lastUpdateTime", DateUtil.getTimeAndToString());
			apiMationDao.editApiMationById(map);
		}
	}

}

