/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service.impl;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.dao.ApiMationDao;
import com.skyeye.service.ApiMationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 *
 * @ClassName: ApiMationServiceImpl
 * @Description: api接口信息服务类
 * @author: skyeye云系列
 * @date: 2021/11/20 13:12
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class ApiMationServiceImpl implements ApiMationService {

	@Autowired
	private ApiMationDao apiMationDao;

	/**
	 * 
	     * @Title: insertApiMation
	     * @Description: 新增api接口信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertApiMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("userId", inputObject.getLogParams().get("id"));
		Map<String, Object> bean = apiMationDao.queryApiMationByRequestUrl(map);
		if (bean != null && !bean.isEmpty()) {
			map.put("lastUpdateTime", DateUtil.getTimeAndToString());
			apiMationDao.editApiMationById(map);
		} else {
			map.put("id", ToolUtil.getSurFaceId());
			map.put("createTime", DateUtil.getTimeAndToString());
			apiMationDao.insertApiMationMation(map);
		}
	}
	
	/**
	 * 
	     * @Title: selectApiMationById
	     * @Description: 通过id查找对应的api接口信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void selectApiMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object>	bean = apiMationDao.selectApiMationById(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

}

