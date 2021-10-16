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
import com.skyeye.eve.dao.DsFormDisplayTemplateDao;
import com.skyeye.eve.service.DsFormDisplayTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: DsFormDisplayTemplateServiceImpl
 * @Description: 动态表单数据展示模板管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:22
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class DsFormDisplayTemplateServiceImpl implements DsFormDisplayTemplateService{
	
	@Autowired
	private DsFormDisplayTemplateDao dsFormDisplayTemplateDao;

	/**
	 * 
	     * @Title: queryDsFormDisplayTemplateList
	     * @Description: 获取动态表单数据展示模板列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryDsFormDisplayTemplateList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = dsFormDisplayTemplateDao.queryDsFormDisplayTemplateList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 
	     * @Title: insertDsFormDisplayTemplateMation
	     * @Description: 添加动态表单数据展示模板信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertDsFormDisplayTemplateMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = dsFormDisplayTemplateDao.queryDsFormDisplayTemplateMationByName(map);
		if(bean == null){
			Map<String, Object> user = inputObject.getLogParams();
			map.put("id", ToolUtil.getSurFaceId());
			map.put("createId", user.get("id"));
			map.put("createTime", DateUtil.getTimeAndToString());
			dsFormDisplayTemplateDao.insertDsFormDisplayTemplateMation(map);
		}else{
			outputObject.setreturnMessage("该动态表单数据展示模板名称已存在，不可进行二次保存");
		}
	}

	/**
	 * 
	     * @Title: deleteDsFormDisplayTemplateMationById
	     * @Description: 删除动态表单数据展示模板信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteDsFormDisplayTemplateMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		dsFormDisplayTemplateDao.deleteDsFormDisplayTemplateMationById(map);
	}

	/**
	 * 
	     * @Title: queryDsFormDisplayTemplateMationToEditById
	     * @Description: 编辑动态表单数据展示模板信息时进行回显
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryDsFormDisplayTemplateMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = dsFormDisplayTemplateDao.queryDsFormDisplayTemplateMationToEditById(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editDsFormDisplayTemplateMationById
	     * @Description: 编辑动态表单数据展示模板信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editDsFormDisplayTemplateMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = dsFormDisplayTemplateDao.queryDsFormDisplayTemplateMationByNameAndId(map);
		if(bean == null){
			dsFormDisplayTemplateDao.editDsFormDisplayTemplateMationById(map);
		}else{
			outputObject.setreturnMessage("该动态表单数据展示模板名称已存在，不可进行二次保存");
		}
	}

	/**
	 * 
	     * @Title: queryDisplayTemplateListToShow
	     * @Description: 获取动态表单数据展示模板
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryDisplayTemplateListToShow(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = dsFormDisplayTemplateDao.queryDisplayTemplateListToShow(map);
		if(beans != null){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}
	
}
