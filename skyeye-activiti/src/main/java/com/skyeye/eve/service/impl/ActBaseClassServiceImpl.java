/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.ActBaseClassDao;
import com.skyeye.eve.service.ActBaseClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: ActBaseClassServiceImpl
 * @Description: 工作流配置类管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 22:34
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class ActBaseClassServiceImpl implements ActBaseClassService {
	
	@Autowired
	private ActBaseClassDao actBaseClassDao;

	/**
	 * 
	     * @Title: queryActBaseClassList
	     * @Description: 获取配置类列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryActBaseClassList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = actBaseClassDao.queryActBaseClassList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 
	     * @Title: insertActBaseClassMation
	     * @Description: 添加配置类
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void insertActBaseClassMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("id", ToolUtil.getSurFaceId());
		map.put("createId", inputObject.getLogParams().get("id"));
		map.put("createTime", DateUtil.getTimeAndToString());
		actBaseClassDao.insertActBaseClassMation(map); 
	}

	/**
	 * 
	     * @Title: deleteActBaseClassById
	     * @Description: 删除配置类
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void deleteActBaseClassById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		actBaseClassDao.deleteActBaseClassById(map);
	}

	/**
	 * 
	     * @Title: selectActBaseClassById
	     * @Description: 通过id查找对应的配置类信息用以编辑
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void selectActBaseClassById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object>	bean = actBaseClassDao.selectActBaseClassById(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editActBaseClassMationById
	     * @Description: 编辑配置类
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void editActBaseClassMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("lastUpdateId", inputObject.getLogParams().get("id"));
		map.put("lastUpdateTime", DateUtil.getTimeAndToString());
		actBaseClassDao.editActBaseClassMationById(map);
	}

	/**
	 * 
	     * @Title: selectActBaseClassByIdToDedails
	     * @Description: 配置类详情
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void selectActBaseClassByIdToDedails(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object>	bean = actBaseClassDao.selectActBaseClassByIdToDedails(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

}
