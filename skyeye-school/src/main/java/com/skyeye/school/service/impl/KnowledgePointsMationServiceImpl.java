/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.school.dao.KnowledgePointsMationDao;
import com.skyeye.school.service.KnowledgePointsMationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class KnowledgePointsMationServiceImpl implements KnowledgePointsMationService{
	
	@Autowired
	private KnowledgePointsMationDao knowledgePointsMationDao;

	/**
	 * 
	     * @Title: queryKnowledgePointsMationList
	     * @Description: 获取知识点列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryKnowledgePointsMationList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		// 获取当前用户拥有的学校的数据权限
		map.put("schoolPowerId", inputObject.getLogParams().get("schoolPowerId"));
		map.put("createId", inputObject.getLogParams().get("id"));
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = knowledgePointsMationDao.queryKnowledgePointsMationList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 
	     * @Title: insertKnowledgePointsMation
	     * @Description: 添加知识点
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertKnowledgePointsMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = knowledgePointsMationDao.queryKnowledgePointsMationByName(map);
		if(bean == null){
			Map<String, Object> user = inputObject.getLogParams();
			map.put("id", ToolUtil.getSurFaceId());
			map.put("createId", user.get("id"));
			map.put("createTime", DateUtil.getTimeAndToString());
			knowledgePointsMationDao.insertKnowledgePointsMation(map);
		}else{
			outputObject.setreturnMessage("该年级下的知识点标题已存在.");
		}
	}

	/**
	 * 
	     * @Title: deleteKnowledgePointsMationById
	     * @Description: 删除知识点
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteKnowledgePointsMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		knowledgePointsMationDao.deleteKnowledgePointsMationById(map);
	}

	/**
	 * 
	     * @Title: queryKnowledgePointsMationToEditById
	     * @Description: 编辑知识点信息时进行回显
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryKnowledgePointsMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = knowledgePointsMationDao.queryKnowledgePointsMationToEditById(map);
		if(bean != null && !bean.isEmpty()){
			outputObject.setBean(bean);
			outputObject.settotal(1);
		}else{
			outputObject.setreturnMessage("该数据不存在。");
		}
	}

	/**
	 * 
	     * @Title: editKnowledgePointsMationById
	     * @Description: 编辑知识点信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editKnowledgePointsMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = knowledgePointsMationDao.queryKnowledgePointsMationByNameAndId(map);
		if(bean == null){
			knowledgePointsMationDao.editKnowledgePointsMationById(map);
		}else{
			outputObject.setreturnMessage("该知识点已注册，请确认。");
		}
	}

	/**
	 * 
	     * @Title: queryKnowledgePointsMationById
	     * @Description: 知识点详情
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryKnowledgePointsMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = knowledgePointsMationDao.queryKnowledgePointsMationById(map);
		if(bean != null && !bean.isEmpty()){
			outputObject.setBean(bean);
			outputObject.settotal(1);
		}else{
			outputObject.setreturnMessage("该数据不存在。");
		}
	}
	
	/**
	 * 
	     * @Title: queryKnowledgePointsMationListToTable
	     * @Description: 获取知识点列表展示为表格供其他选择
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryKnowledgePointsMationListToTable(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		// 获取当前用户拥有的学校的数据权限
		map.put("schoolPowerId", inputObject.getLogParams().get("schoolPowerId"));
		map.put("createId", inputObject.getLogParams().get("id"));
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = knowledgePointsMationDao.queryKnowledgePointsMationListToTable(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 
	     * @Title: queryKnowledgePointsMationListByIds
	     * @Description: 根据知识点id串获取知识点列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryKnowledgePointsMationListByIds(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<String> idsList = Arrays.asList(map.get("ids").toString().split(","));
		List<Map<String, Object>> beans = new ArrayList<>();
		if(!idsList.isEmpty()){
			beans = knowledgePointsMationDao.queryKnowledgePointsMationListByIds(idsList);
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}else{
			outputObject.setBeans(beans);
		}
	}

	/**
	 * 
	     * @Title: queryKnowledgePointsMationBankList
	     * @Description: 获取知识点库列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryKnowledgePointsMationBankList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		// 获取当前用户拥有的学校的数据权限
		map.put("schoolPowerId", inputObject.getLogParams().get("schoolPowerId"));
		map.put("createId", inputObject.getLogParams().get("id"));
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = knowledgePointsMationDao.queryKnowledgePointsMationBankList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

}
