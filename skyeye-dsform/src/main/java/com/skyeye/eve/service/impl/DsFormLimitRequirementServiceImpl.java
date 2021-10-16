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
import com.skyeye.eve.dao.DsFormLimitRequirementDao;
import com.skyeye.eve.service.DsFormLimitRequirementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: DsFormLimitRequirementServiceImpl
 * @Description: 动态表单条件限制类型管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:35
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class DsFormLimitRequirementServiceImpl implements DsFormLimitRequirementService{
	
	@Autowired
	private DsFormLimitRequirementDao dsFormLimitRequirementDao;

	/**
	 * 
	     * @Title: queryDsFormLimitRequirementList
	     * @Description: 获取动态表单条件限制类型列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryDsFormLimitRequirementList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = dsFormLimitRequirementDao.queryDsFormLimitRequirementList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 
	     * @Title: insertDsFormLimitRequirementMation
	     * @Description: 添加动态表单条件限制类型信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertDsFormLimitRequirementMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = dsFormLimitRequirementDao.queryDsFormLimitRequirementMationByName(map);
		if(bean == null){
			Map<String, Object> user = inputObject.getLogParams();
			map.put("id", ToolUtil.getSurFaceId());
			map.put("createId", user.get("id"));
			map.put("createTime", DateUtil.getTimeAndToString());
			dsFormLimitRequirementDao.insertDsFormLimitRequirementMation(map);
		}else{
			outputObject.setreturnMessage("该动态表单条件限制类型名称已存在，不可进行二次保存");
		}
	}

	/**
	 * 
	     * @Title: deleteDsFormLimitRequirementMationById
	     * @Description: 删除动态表单条件限制类型信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteDsFormLimitRequirementMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		dsFormLimitRequirementDao.deleteDsFormLimitRequirementMationById(map);
	}

	/**
	 * 
	     * @Title: queryDsFormLimitRequirementMationToEditById
	     * @Description: 编辑动态表单条件限制类型信息时进行回显
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryDsFormLimitRequirementMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = dsFormLimitRequirementDao.queryDsFormLimitRequirementMationToEditById(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editDsFormLimitRequirementMationById
	     * @Description: 编辑动态表单条件限制类型信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editDsFormLimitRequirementMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = dsFormLimitRequirementDao.queryDsFormLimitRequirementMationByNameAndId(map);
		if(bean == null){
			dsFormLimitRequirementDao.editDsFormLimitRequirementMationById(map);
		}else{
			outputObject.setreturnMessage("该动态表单条件限制类型名称已存在，不可进行二次保存");
		}
	}

	/**
	 * 
	     * @Title: queryDsFormLimitRequirementMationToShow
	     * @Description: 获取动态表单内容供展示
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryDsFormLimitRequirementMationToShow(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = dsFormLimitRequirementDao.queryDsFormLimitRequirementMationToShow(map);
		if(beans != null){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}
	
}
