/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service.impl;

import cn.hutool.json.JSONUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.activiti.factory.ActivitiRunFactory;
import com.skyeye.common.constans.ActivitiConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.dao.ProCostExpenseDao;
import com.skyeye.eve.dao.SysEnclosureDao;
import com.skyeye.service.ProCostExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: ProCostExpenseServiceImpl
 * @Description: 项目费用报销管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/5 12:59
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
public class ProCostExpenseServiceImpl implements ProCostExpenseService {

    @Autowired
    private ProCostExpenseDao proCostExpenseDao;

    @Autowired
	private SysEnclosureDao sysEnclosureDao;

	/**
	 * 项目费用报销提交到工作流中的key
	 */
    private static final String PRO_COST_EXPENSE_PAGE_KEY = ActivitiConstants.ActivitiObjectType.PRO_COST_EXPENSE_PAGE.getKey();

    /**
    *
    * @Title: queryProCostExpenseList
    * @Description: 费用报销列表
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@Override
	public void queryProCostExpenseList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String userId = inputObject.getLogParams().get("id").toString();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = proCostExpenseDao.queryProCostExpenseList(map);
		for(Map<String, Object> bean : beans){
			Integer state = Integer.parseInt(bean.get("state").toString());
			ActivitiRunFactory.run(inputObject, outputObject, PRO_COST_EXPENSE_PAGE_KEY).setDataStateEditRowWhenInExamine(bean, state, userId);
		}
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

    /**
    *
    * @Title: insertProCostExpenseMation
    * @Description: 新增费用报销
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@Override
	@Transactional(value="transactionManager")
	public void insertProCostExpenseMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String expensePurposeStr = map.get("expensePurposeStr").toString();
		if(ToolUtil.isJson(expensePurposeStr)){
			String expenseId = ToolUtil.getSurFaceId();//报销主表id
			// 处理数据
			List<Map<String, Object>> jArray = JSONUtil.toList(expensePurposeStr, null);
			Map<String, Object> bean;
			List<Map<String, Object>> entitys = new ArrayList<>();//费用用途详细集合信息
			BigDecimal allPrice = new BigDecimal("0");//主单总价
			BigDecimal itemAllPrice = null;//子单对象
			for(int i = 0; i < jArray.size(); i++){
				bean = jArray.get(i);
				bean.put("id", ToolUtil.getSurFaceId());
				bean.put("expenseId", expenseId);//报销主表id
				entitys.add(bean);
				//计算主单总价
				itemAllPrice = new BigDecimal(bean.get("price").toString());
				allPrice = allPrice.add(itemAllPrice);
			}
			if(entitys.size() == 0){
				outputObject.setreturnMessage("请填写项目成本费用用途详细");
				return;
			}
			map.put("id", expenseId);
			map.put("state", 0);//状态  默认草稿
			map.put("allPrice", allPrice);//合计金额
			Map<String, Object> user = inputObject.getLogParams();//用户信息
			map.put("createId", user.get("id").toString());
			map.put("createTime", DateUtil.getTimeAndToString());
			proCostExpenseDao.insertProCostExpenseMation(map);
			proCostExpenseDao.insertProCostExpensePurposeMation(entitys);
			// 判断是否提交审批
			if("2".equals(map.get("subType").toString())){
				// 提交审批
				ActivitiRunFactory.run(inputObject, outputObject, PRO_COST_EXPENSE_PAGE_KEY).submitToActivi(expenseId);
			}
		}else{
			outputObject.setreturnMessage("数据格式错误");
		}
	}
	
	/**
	*
	* @Title: queryProCostExpenseMationToDetails
	* @Description: 费用报销详情
	* @param inputObject
	* @param outputObject
	* @throws Exception    参数
	* @return void    返回类型
	* @throws
	*/
	@Override
	public void queryProCostExpenseMationToDetails(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String id = map.get("id").toString();
		// 获取主表数据
		Map<String, Object> jsonBean = proCostExpenseDao.queryProCostExpenseMationById(id);
		// 获取费用用途详细
		jsonBean.put("purposes", proCostExpenseDao.queryProCostExpensePurpose(id));
		// 获取附件信息
        jsonBean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(jsonBean.get("enclosureInfo").toString()));
		outputObject.setBean(jsonBean);
		outputObject.settotal(1);
	}

	/**
	*
	* @Title: queryProCostExpenseMationToEdit
	* @Description: 获取费用报销信息用以编辑
	* @param inputObject
	* @param outputObject
	* @throws Exception    参数
	* @return void    返回类型
	* @throws
	*/
	@Override
	public void queryProCostExpenseMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		// 获取主表数据
		Map<String, Object> bean = proCostExpenseDao.queryProCostExpenseMationToEdit(map);
		// 获取费用用途详细
		List<Map<String, Object>> purposes = proCostExpenseDao.queryProCostExpensePurposeToEdit(map);
		bean.put("purposes", purposes);
		// 获取附件信息
		bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 *
	 * @Title: editProCostExpenseMation
	 * @Description: 编辑费用报销信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    参数
	 * @return void    返回类型
	 * @throws
	 */
	@Override
	public void editProCostExpenseMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String expensePurposeStr = map.get("expensePurposeStr").toString();
		if(ToolUtil.isJson(expensePurposeStr)){
			String expenseId = map.get("id").toString();//报销主表id
			// 处理数据
			List<Map<String, Object>> jArray = JSONUtil.toList(expensePurposeStr, null);
			Map<String, Object> bean;
			List<Map<String, Object>> entitys = new ArrayList<>();////费用用途详细集合信息
			BigDecimal allPrice = new BigDecimal("0");//主单总价
			BigDecimal itemAllPrice = null;//子单对象
			for(int i = 0; i < jArray.size(); i++){
				bean = jArray.get(i);
				bean.put("id", ToolUtil.getSurFaceId());
				bean.put("expenseId", expenseId);//报销主表id
				entitys.add(bean);
				//计算主单总价
				itemAllPrice = new BigDecimal(bean.get("price").toString());
				allPrice = allPrice.add(itemAllPrice);
			}
			if(entitys.size() == 0){
				outputObject.setreturnMessage("请填写项目成本费用用途详细");
				return;
			}
			map.put("allPrice", allPrice);//合计金额
			proCostExpenseDao.editProCostExpenseMation(map);
			proCostExpenseDao.deleteProCostExpensePurposeById(map);
			proCostExpenseDao.insertProCostExpensePurposeMation(entitys);
			// 判断是否提交审批
			if("2".equals(map.get("subType").toString())){
				// 提交审批
				ActivitiRunFactory.run(inputObject, outputObject, PRO_COST_EXPENSE_PAGE_KEY).submitToActivi(expenseId);
			}
		}else{
			outputObject.setreturnMessage("数据格式错误");
		}
	}

	/**
	 *
	 * @Title: editProCostExpenseToApprovalById
	 * @Description: 提交审批
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    参数
	 * @return void    返回类型
	 * @throws
	 */
	@Override
	public void editProCostExpenseToApprovalById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String id = map.get("id").toString();
		Map<String, Object> bean = proCostExpenseDao.queryProCostExpenseMationById(id);
		Integer state = Integer.parseInt(bean.get("state").toString());
		if(0 == state || 12 == state || 3 == state){
			// 草稿、审核不通过、撤销状态下可以提交审批
			ActivitiRunFactory.run(inputObject, outputObject, PRO_COST_EXPENSE_PAGE_KEY).submitToActivi(id);
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}

	/**
	 *
	 * @Title: editProCostExpenseProcessToRevoke
	 * @Description: 撤销费用报销审批申请
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    参数
	 * @return void    返回类型
	 * @throws
	 */
	@Override
	public void editProCostExpenseProcessToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception {
		ActivitiRunFactory.run(inputObject, outputObject, PRO_COST_EXPENSE_PAGE_KEY).revokeActivi();
	}

	/**
	 *
	 * @Title: editProCostExpenseMationInProcess
	 * @Description: 在工作流中编辑费用报销信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    参数
	 * @return void    返回类型
	 * @throws
	 */
	@Override
	public void editProCostExpenseMationInProcess(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String expensePurposeStr = map.get("expensePurposeStr").toString();
		if(ToolUtil.isJson(expensePurposeStr)){
			String expenseId = map.get("id").toString();//报销主表id
			// 处理数据
			List<Map<String, Object>> jArray = JSONUtil.toList(expensePurposeStr, null);
			Map<String, Object> bean;
			List<Map<String, Object>> entitys = new ArrayList<>();////费用用途详细集合信息
			BigDecimal allPrice = new BigDecimal("0");//主单总价
			BigDecimal itemAllPrice = null;//子单对象
			for(int i = 0; i < jArray.size(); i++){
				bean = jArray.get(i);
				bean.put("id", ToolUtil.getSurFaceId());
				bean.put("expenseId", expenseId);//报销主表id
				entitys.add(bean);
				//计算主单总价
				itemAllPrice = new BigDecimal(bean.get("price").toString());
				allPrice = allPrice.add(itemAllPrice);
			}
			if(entitys.size() == 0){
				outputObject.setreturnMessage("请填写项目成本费用用途详细");
				return;
			}
			map.put("allPrice", allPrice);//合计金额
			proCostExpenseDao.editProCostExpenseMation(map);
			proCostExpenseDao.deleteProCostExpensePurposeById(map);
			proCostExpenseDao.insertProCostExpensePurposeMation(entitys);
			
			// 编辑流程表参数
			ActivitiRunFactory.run(inputObject, outputObject, PRO_COST_EXPENSE_PAGE_KEY).editApplyMationInActiviti(expenseId);
		}else{
			outputObject.setreturnMessage("数据格式错误");
		}
	}

	/**
	 *
	 * @Title: deleteProCostExpenseMationById
	 * @Description: 删除费用报销
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    参数
	 * @return void    返回类型
	 * @throws
	 */
	@Override
	public void deleteProCostExpenseMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String id = map.get("id").toString();
		Map<String, Object> bean = proCostExpenseDao.queryProCostExpenseMationById(id);
		Integer state = Integer.parseInt(bean.get("state").toString());
		if(0 == state || 12 == state || 3 == state){
			// 草稿、审核不通过、撤销状态下可以删除
			proCostExpenseDao.deleteProCostExpenseMationById(map);//删除该费用报销相关信息
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}

	/**
	 *
	 * @Title: updateProCostExpenseToCancellation
	 * @Description: 作废费用报销
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    参数
	 * @return void    返回类型
	 * @throws
	 */
	@Override
	public void updateProCostExpenseToCancellation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String id = map.get("id").toString();
		Map<String, Object> bean = proCostExpenseDao.queryProCostExpenseMationById(id);
		Integer state = Integer.parseInt(bean.get("state").toString());
		if(0 == state || 12 == state || 3 == state){
			// 草稿、审核不通过、撤销状态下可以作废
			proCostExpenseDao.updateProCostExpenseToCancellation(map);
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}

}
