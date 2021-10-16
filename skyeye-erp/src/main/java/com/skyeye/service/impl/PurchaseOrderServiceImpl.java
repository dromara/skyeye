/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.skyeye.common.constans.ErpConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ExcelUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.dao.ErpCommonDao;
import com.skyeye.dao.MaterialDao;
import com.skyeye.dao.PurchaseOrderDao;
import com.skyeye.erp.entity.TransmitObject;
import com.skyeye.factory.ErpRunFactory;
import com.skyeye.service.ErpCommonService;
import com.skyeye.service.PurchaseOrderService;
import com.skyeye.annotation.transaction.ActivitiAndBaseTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: PurchaseOrderServiceImpl
 * @Description: 采购订单管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:45
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService{

	private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseOrderServiceImpl.class);
	
	@Autowired
	private PurchaseOrderDao purchaseOrderDao;
	
	@Autowired
	private ErpCommonDao erpCommonDao;
	
	@Autowired
	private ErpCommonService erpCommonService;
	
	@Autowired
	private MaterialDao materialDao;

	/**
	 * 采购订单类型
	 */
	private static final String ORDER_TYPE = ErpConstants.DepoTheadSubType.PURCHASE_ORDER.getType();

	/**
	 * 采购入库单类型
	 */
	private static final String ORDER_PURCHASE_TYPE = ErpConstants.DepoTheadSubType.PUT_IS_PURCHASE.getType();

	/**
     * 获取采购单列表信息
	 *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void queryPurchaseOrderToList(InputObject inputObject, OutputObject outputObject) throws Exception {
        ErpRunFactory.run(inputObject, outputObject, ORDER_TYPE).queryOrderList();
	}

	/**
     * 新增采购单信息
	 *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	@ActivitiAndBaseTransaction(value = {"activitiTransactionManager", "transactionManager"})
	public void insertPurchaseOrderMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		ErpRunFactory.run(inputObject, outputObject, ORDER_TYPE).insertOrderMation();
	}

	/**
     * 采购单信息编辑回显
	 *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void queryPurchaseOrderToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		ErpRunFactory.run(inputObject, outputObject, ORDER_TYPE).queryOrderMationToEditById();
	}

	/**
     * 编辑采购单信息
	 *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	@ActivitiAndBaseTransaction(value = {"activitiTransactionManager", "transactionManager"})
	public void editPurchaseOrderMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		ErpRunFactory.run(inputObject, outputObject, ORDER_TYPE).editOrderMationById();
	}

	/**
     * 采购单信息转采购入库回显
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void queryPurchaseOrderToTurnPutById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		//获取采购单主单信息
		Map<String, Object> bean = purchaseOrderDao.queryPurchaseOrderToTurnPutById(map.get("id").toString(), ORDER_TYPE);
		if(bean != null && !bean.isEmpty()){
			//根据订单id获取该订单下的所有商品规格信息
			List<Map<String, Object>> norms = purchaseOrderDao.queryPurchaseOrderNormsToTurnPutById(bean);
			for(Map<String, Object> norm : norms){
				erpCommonService.resetProductAndUnitToEditShow(norm, materialDao.queryMaterialUnitByIdToSelect(norm.get("materialId").toString()));
			}
			bean.put("items", norms);
			//获取其他项目列表
			if(bean.containsKey("otherMoneyList") && !ToolUtil.isBlank(bean.get("otherMoneyList").toString()) && ToolUtil.isJson(bean.get("otherMoneyList").toString())){
				bean.put("otherMoneyList", JSONUtil.toList(bean.get("otherMoneyList").toString(), null));
			}else {
				bean.put("otherMoneyList", new JSONArray());
			}
			outputObject.setBean(bean);
			outputObject.settotal(1);
		}else{
			outputObject.setreturnMessage("该数据不存在");
		}
	}

	/**
     * 采购单信息转采购入库
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	@Transactional(value="transactionManager")
	public void insertPurchaseOrderToTurnPut(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String orderId = map.get("id").toString();
		//获取采购单状态
		Map<String, Object> cgBean = erpCommonDao.queryOrderParentStateById(orderId, ORDER_TYPE);
		if(cgBean != null && !cgBean.isEmpty()){
			//审核通过/已经入库的可以进行入库
			if(ErpConstants.ERP_HEADER_STATUS_IS_APPROVED_PASS.equals(cgBean.get("status").toString())){
				String depotheadStr = map.get("depotheadStr").toString();//采购用品列表
				String otherMoneyList = map.get("otherMoneyList").toString();//其他项目费用列表
				if(ToolUtil.isJson(depotheadStr) && ToolUtil.isJson(otherMoneyList)){
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
					depothead.put("type", ErpConstants.ERP_HEADER_TYPE_IS_IN_WAREHOUSE);//类型(1.出库/2.入库3.其他)
					depothead.put("subType", ORDER_PURCHASE_TYPE);//采购入库单
					String orderNum = ErpConstants.DepoTheadSubType.getOrderNumBySubType(ORDER_PURCHASE_TYPE);
					depothead.put("defaultNumber", orderNum);//初始票据号
					depothead.put("number", orderNum);//票据号
					depothead.put("linkNumber", map.get("cgddOrderNum"));
					BigDecimal discountMoney = new BigDecimal(map.get("discountMoney").toString());//优惠金额
					//优惠后金额
					depothead.put("discountLastMoney", object.getTaxLastMoneyPrice().subtract(discountMoney));
					
					//合计金额
					depothead.put("totalPrice", object.getAllPrice());
					erpCommonDao.insertOrderParentMation(depothead);
					erpCommonDao.insertOrderChildMation(entitys);
				}else{
					outputObject.setreturnMessage("数据格式错误");
				}
			}else{
				outputObject.setreturnMessage("状态错误，无法入库.");
			}
		}else{
			outputObject.setreturnMessage("该数据不存在.");
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
        List<Map<String, Object>> beans = purchaseOrderDao.queryMationToExcel(params);
        String[] key = new String[]{"defaultNumber", "supplierName", "materialNames", "status", "totalPrice", "operPersonName", "operTime"};
        String[] column = new String[]{"单据编号", "供应商", "关联产品", "状态", "合计金额", "操作人", "单据日期"};
        String[] dataType = new String[]{"", "data", "data", "data", "data", "data", "data"};
        //采购单信息导出
        ExcelUtil.createWorkBook("采购订单", "采购订单详细", beans, key, column, dataType, inputObject.getResponse());
	}
	
}
