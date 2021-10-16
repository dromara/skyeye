/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.constans.ErpConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ExcelUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.dao.ErpCommonDao;
import com.skyeye.dao.MaterialDao;
import com.skyeye.dao.SplitListDao;
import com.skyeye.erp.entity.TransmitObject;
import com.skyeye.service.ErpCommonService;
import com.skyeye.service.SplitListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: SplitListServiceImpl
 * @Description: 拆分单管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/8 21:20
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class SplitListServiceImpl implements SplitListService{
	
	@Autowired
	private SplitListDao splitListDao;
	
	@Autowired
	private ErpCommonDao erpCommonDao;
	
	@Autowired
	private ErpCommonService erpCommonService;
	
	@Autowired
	private MaterialDao materialDao;

	/**
	 * 拆分单类型
	 */
	private static final String ORDER_TYPE = ErpConstants.DepoTheadSubType.SPLIT_LIST_ORDER.getType();

	/**
     * 获取拆分单列表信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void querySplitListToList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(params.get("page").toString()), Integer.parseInt(params.get("limit").toString()));
        List<Map<String, Object>> beans = splitListDao.querySplitListToList(params);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
     * 新增拆分单信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	@Transactional(value="transactionManager")
	public void insertSplitListMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String depotheadStr = map.get("depotheadStr").toString();//拆分单产品列表
		if(ToolUtil.isJson(depotheadStr)){
			String useId = ToolUtil.getSurFaceId();//单据主表id
			//值传递对象，获取对应的值即可
			TransmitObject object = new TransmitObject();
			//单据子表实体集合信息
			List<Map<String, Object>> entitys = new ArrayList<>();
			erpCommonService.resetChildBillTypeOneMation(depotheadStr, useId, object, entitys);
			if(entitys == null || entitys.isEmpty()){
				outputObject.setreturnMessage("请选择商品");
				return;
			}
			//单据主表对象
			Map<String, Object> depothead = new HashMap<>();
			erpCommonService.addOrderCreateHaderMation(depothead, map, inputObject.getLogParams());
			depothead.put("id", useId);
			depothead.put("type", ErpConstants.ERP_HEADER_TYPE_IS_OTHER);//类型(1.出库/2.入库3.其他)
			depothead.put("subType", ORDER_TYPE);//拆分单
			
			String orderNum = ErpConstants.DepoTheadSubType.getOrderNumBySubType(ORDER_TYPE);
			depothead.put("defaultNumber", orderNum);//初始票据号
			depothead.put("number", orderNum);//票据号
			
			depothead.put("payType", "1");//付款类型-拆分单默认为1
			//合计金额
			depothead.put("totalPrice", object.getAllPrice());
			
			erpCommonDao.insertOrderParentMation(depothead);
			erpCommonDao.insertOrderChildMation(entitys);
		}else{
			outputObject.setreturnMessage("数据格式错误");
		}
	}

	/**
     * 编辑拆分单信息时进行回显
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void querySplitListMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		//获取主表信息
		Map<String, Object> bean = erpCommonDao.queryOrderMationToEditById(map.get("id").toString(), ORDER_TYPE);
		if(bean != null && !bean.isEmpty()){
			//根据订单id获取该订单下的所有商品规格信息
			List<Map<String, Object>> norms = erpCommonDao.queryOrderNormsToEditByOrderId(bean.get("id").toString());
			for(Map<String, Object> norm : norms){
				erpCommonService.resetProductAndUnitToEditShow(norm, materialDao.queryMaterialUnitByIdToSelect(norm.get("materialId").toString()));
			}
			bean.put("items", norms);
			outputObject.setBean(bean);
			outputObject.settotal(1);
		}else{
			outputObject.setreturnMessage("该数据已不存在.");
		}
	}

	/**
     * 编辑拆分单信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	@Transactional(value="transactionManager")
	public void editSplitListMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String depotheadStr = map.get("depotheadStr").toString();//拆分单产品列表
		if(ToolUtil.isJson(depotheadStr)){
			String useId = map.get("id").toString();//单据主表id
			//值传递对象，获取对应的值即可
			TransmitObject object = new TransmitObject();
			//单据子表实体集合信息
			List<Map<String, Object>> entitys = new ArrayList<>();
			erpCommonService.resetChildBillTypeOneMation(depotheadStr, useId, object, entitys);
			if(entitys.size() == 0){
				outputObject.setreturnMessage("请选择商品");
				return;
			}
			//单据主表对象
			Map<String, Object> depothead = new HashMap<>();
			erpCommonService.editOrderCreateHaderMation(depothead, map, inputObject.getLogParams());
			depothead.put("id", useId);
			
			//合计金额
			depothead.put("totalPrice", object.getAllPrice());
			
			//编辑拆分单
			//单据类型，编辑时需要传递
			depothead.put("subType", ORDER_TYPE);
			int num = erpCommonDao.editOrderParentMationById(depothead);
			if(num > 0){
				//删除子单据信息
				erpCommonDao.deleteOrderChildMationById(useId);
				//插入子单据信息
				erpCommonDao.insertOrderChildMation(entitys);
			}
		}else{
			outputObject.setreturnMessage("数据格式错误");
		}
	}

	/**
     * 导出Excel
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void queryMationToExcel(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
        List<Map<String, Object>> beans = splitListDao.queryMationToExcel(params);
        String[] key = new String[]{"defaultNumber", "materialNames", "totalPrice", "operPersonName", "operTime"};
        String[] column = new String[]{"单据编号", "关联产品", "合计金额", "操作人", "单据日期"};
        String[] dataType = new String[]{"", "data", "data", "data", "data"};
        //拆分单信息导出
        ExcelUtil.createWorkBook("拆分单", "拆分单详细", beans, key, column, dataType, inputObject.getResponse());
	}
	
	/**
     * 拆分单信息提交审核
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	@Transactional(value="transactionManager")
	public void editSplitOrderStateToSubExamineById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String orderId = map.get("id").toString();
		//获取拆分单状态
		Map<String, Object> bean = erpCommonDao.queryOrderParentStateById(orderId, ORDER_TYPE);
		if(bean != null && !bean.isEmpty()){
			String status = bean.get("status").toString();
			if(ErpConstants.ERP_HEADER_STATUS_IS_NOT_APPROVED.equals(status) 
					|| ErpConstants.ERP_HEADER_STATUS_IS_APPROVED_NOT_PASS.equals(status)){
				//未提交审核或者审核拒绝的可以提交
				erpCommonDao.editOrderStateToSubExamineById(orderId, ORDER_TYPE);
			}
		}else{
			outputObject.setreturnMessage("该单据状态已经改变或数据不存在.");
		}
	}
	
}
