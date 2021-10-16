/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 */

package com.skyeye.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.dao.ErpWorkProcedureDao;
import com.skyeye.dao.ErpWorkProcedureOperatorDao;
import com.skyeye.service.ErpWorkProcedureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 *
 * @ClassName: ErpWorkProcedureServiceImpl
 * @Description: 工序信息管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:49
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class ErpWorkProcedureServiceImpl implements ErpWorkProcedureService{
	
	@Autowired
	private ErpWorkProcedureDao erpWorkProcedureDao;

	@Autowired
	private ErpWorkProcedureOperatorDao erpWorkProcedureOperatorDao;

	/**
     * 查询工序列表
     * @param inputObject 入参
     * @param outputObject 出参
     */
	@Override
	public void queryErpWorkProcedureList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(params.get("page").toString()), Integer.parseInt(params.get("limit").toString()));
        List<Map<String, Object>> beans = erpWorkProcedureDao.queryErpWorkProcedureList(params);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
     * 添加工序信息
	 * @param inputObject 入参
	 * @param outputObject 出参
     */
	@Override
	@Transactional(value="transactionManager")
	public void insertErpWorkProcedureMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
		// 判断工序名称或者编号是否存在
		Map<String, Object> bean = erpWorkProcedureDao.queryErpWorkProcedureByNameOrNumber(params);
		if(bean != null && !bean.isEmpty()){
			outputObject.setreturnMessage("该工序名称或编号已存在。");
		}else{
			String procedureId = ToolUtil.getSurFaceId();
			params.put("id", procedureId);
			params.put("createId", inputObject.getLogParams().get("id"));
			params.put("createTime", DateUtil.getTimeAndToString());
			erpWorkProcedureDao.insertErpWorkProcedureMation(params);
			// 操作员信息处理
			handleProcedureUser(params.get("procedureUserId").toString(), procedureId);
		}
	}

	/**
	 * 处理工序操作员信息
	 * @param procedureUserId 工序操作员id，逗号隔开
	 * @param procedureId 工序id
	 */
	private void handleProcedureUser(String procedureUserId, String procedureId) throws Exception {
		erpWorkProcedureOperatorDao.deleteProcedureUserByProcedureId(procedureId);
		if(!ToolUtil.isBlank(procedureUserId)){
			List<Map<String, Object>> beans = new ArrayList<>();
			Arrays.asList(procedureUserId.split(",")).forEach(userId ->{
				Map<String, Object> bean = new HashMap<>();
				bean.put("procedureId", procedureId);
				bean.put("userId", userId);
				beans.add(bean);
			});
			if(!beans.isEmpty()){
				erpWorkProcedureOperatorDao.insertProcedureUserList(beans);
			}
		}
	}

	/**
     * 查询单个工序信息，用于信息回显
	 * @param inputObject 入参
	 * @param outputObject 出参
     */
	@Override
	public void queryErpWorkProcedureToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
		Map<String, Object> bean = erpWorkProcedureDao.queryErpWorkProcedureToEditById(params);
		if(bean != null && !bean.isEmpty()){
			String procedureId = params.get("id").toString();
			List<Map<String, Object>> operators = erpWorkProcedureOperatorDao.queryProcedureUserListByProcedureId(procedureId);
			bean.put("operators", operators);
			outputObject.setBean(bean);
			outputObject.settotal(1);
		}else{
			outputObject.setreturnMessage("该数据不存在。");
		}
	}

	/**
     * 删除工序信息
	 * @param inputObject 入参
	 * @param outputObject 出参
     */
	@Override
	@Transactional(value="transactionManager")
	public void deletErpWorkProcedureById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
		erpWorkProcedureDao.deletErpWorkProcedureById(params);
		String procedureId = params.get("id").toString();
		// 删除工序操作员信息
		erpWorkProcedureOperatorDao.deleteProcedureUserByProcedureId(procedureId);
	}

	/**
     * 编辑工序信息
	 * @param inputObject 入参
	 * @param outputObject 出参
     */
	@Override
	@Transactional(value="transactionManager")
	public void editErpWorkProcedureById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
		// 判断工序名称或者编号是否存在
		Map<String, Object> bean = erpWorkProcedureDao.queryErpWorkProcedureByNameOrNumberAndId(params);
		if(bean != null && !bean.isEmpty()){
			outputObject.setreturnMessage("该工序名称或编号已存在。");
		}else{
			String procedureId = params.get("id").toString();
			erpWorkProcedureDao.editErpWorkProcedureById(params);
			// 操作员信息处理
			handleProcedureUser(params.get("procedureUserId").toString(), procedureId);
		}
	}
	
	/**
     * 查询工序列表展示为表格供其他选择
	 * @param inputObject 入参
	 * @param outputObject 出参
     */
	@Override
	public void queryErpWorkProcedureListToTable(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(params.get("page").toString()), Integer.parseInt(params.get("limit").toString()));
        List<Map<String, Object>> beans = erpWorkProcedureDao.queryErpWorkProcedureListToTable(params);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
     * 根据工序id串获取工序列表
	 * @param inputObject 入参
	 * @param outputObject 出参
     */
	@Override
	public void queryErpWorkProcedureListByIds(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<String> idsList = Arrays.asList(map.get("ids").toString().split(","));
		List<Map<String, Object>> beans = new ArrayList<>();
		if(!idsList.isEmpty()){
			beans = erpWorkProcedureDao.queryErpWorkProcedureListByIds(idsList);
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}else{
			outputObject.setBeans(beans);
		}
	}

	/**
	 * 获取工序详情信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	public void queryErpWorkProcedureMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
		String procedureId = params.get("id").toString();
		Map<String, Object> bean = erpWorkProcedureDao.queryErpWorkProcedureMationById(procedureId);
		if(bean != null && !bean.isEmpty()){
			List<Map<String, Object>> operators = erpWorkProcedureOperatorDao.queryProcedureUserListByProcedureId(procedureId);
			bean.put("operators", operators);
			outputObject.setBean(bean);
			outputObject.settotal(1);
		}else{
			outputObject.setreturnMessage("该数据不存在。");
		}
	}

	/**
     * 查询工序列表展示为下拉框供其他选择
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void queryErpWorkProcedureListToSelect(InputObject inputObject, OutputObject outputObject) throws Exception {
		List<Map<String, Object>> beans = erpWorkProcedureDao.queryErpWorkProcedureListToSelect();
		outputObject.setBeans(beans);
		outputObject.settotal(beans.size());
	}

}
