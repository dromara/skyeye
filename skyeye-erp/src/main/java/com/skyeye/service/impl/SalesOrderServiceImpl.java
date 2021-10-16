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
import com.skyeye.dao.SalesOrderDao;
import com.skyeye.erp.entity.TransmitObject;
import com.skyeye.eve.dao.SysEveUserStaffDao;
import com.skyeye.factory.ErpRunFactory;
import com.skyeye.service.ErpCommonService;
import com.skyeye.service.SalesOrderService;
import com.skyeye.annotation.transaction.ActivitiAndBaseTransaction;
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
 * @ClassName: SalesOrderServiceImpl
 * @Description: 销售订单管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:45
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class SalesOrderServiceImpl implements SalesOrderService{
	
	@Autowired
	private SalesOrderDao salesOrderDao;
	
	@Autowired
	private ErpCommonDao erpCommonDao;
	
	@Autowired
	private ErpCommonService erpCommonService;
	
	@Autowired
	private MaterialDao materialDao;
	
	@Autowired
    private SysEveUserStaffDao sysEveUserStaffDao;

	/**
	 * 销售订单类型
	 */
	private static final String ORDER_TYPE = ErpConstants.DepoTheadSubType.OUTCHASE_ORDER.getType();

	/**
	 * 销售出库单类型
	 */
	private static final String ORDER_OUTLET_TYPE = ErpConstants.DepoTheadSubType.OUT_IS_SALES_OUTLET.getType();

	/**
     * 获取销售单列表信息
	 *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void querySalesOrderToList(InputObject inputObject, OutputObject outputObject) throws Exception {
		ErpRunFactory.run(inputObject, outputObject, ORDER_TYPE).queryOrderList();
	}

	/**
     * 新增销售单信息
	 *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	@ActivitiAndBaseTransaction(value = {"activitiTransactionManager", "transactionManager"})
	public void insertSalesOrderMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		ErpRunFactory.run(inputObject, outputObject, ORDER_TYPE).insertOrderMation();
	}

	/**
     * 销售单信息编辑回显
	 *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void querySalesOrderToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		ErpRunFactory.run(inputObject, outputObject, ORDER_TYPE).queryOrderMationToEditById();
	}

	/**
     * 编辑销售单信息
	 *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	@ActivitiAndBaseTransaction(value = {"activitiTransactionManager", "transactionManager"})
	public void editSalesOrderMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		ErpRunFactory.run(inputObject, outputObject, ORDER_TYPE).editOrderMationById();
	}

	/**
     * 销售单信息转销售出库回显
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void querySalesOrderToTurnPutById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		//获取销售单主单信息
		Map<String, Object> bean = salesOrderDao.querySalesOrderToTurnPutById(map.get("id").toString(), ORDER_TYPE);
		if(bean != null && !bean.isEmpty()){
			List<Map<String, Object>> norms = salesOrderDao.querySalesOrderNormsToTurnPutById(bean);
			for(Map<String, Object> norm : norms){
				erpCommonService.resetProductAndUnitToEditShow(norm, materialDao.queryMaterialUnitByIdToSelect(norm.get("materialId").toString()));
			}
			bean.put("items", norms);
			//获取销售项目列表
			if(bean.containsKey("otherMoneyList") && !ToolUtil.isBlank(bean.get("otherMoneyList").toString()) && ToolUtil.isJson(bean.get("otherMoneyList").toString())){
				bean.put("otherMoneyList", JSONUtil.toList(bean.get("otherMoneyList").toString(), null));
			}else {
				bean.put("otherMoneyList", new JSONArray());
			}
			//获取销售人员
			if(bean.containsKey("salesMan")){
				bean.put("userInfo", sysEveUserStaffDao.queryUserNameList(bean.get("salesMan").toString()));
			}
			outputObject.setBean(bean);
			outputObject.settotal(1);
		}else{
			outputObject.setreturnMessage("该数据不存在");
		}
	}

	/**
     * 销售单信息转销售出库
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	@Transactional(value="transactionManager")
	public void insertSalesOrderToTurnPut(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String orderId = map.get("id").toString();
		//获取销售单状态
		Map<String, Object> cgBean = erpCommonDao.queryOrderParentStateById(orderId, ORDER_TYPE);
		if(cgBean != null && !cgBean.isEmpty()){
			//审核通过/已经出库的可以进行出库
			if(ErpConstants.ERP_HEADER_STATUS_IS_APPROVED_PASS.equals(cgBean.get("status").toString())){
				String depotheadStr = map.get("depotheadStr").toString();//销售产品列表
				String otherMoneyList = map.get("otherMoneyList").toString();//销售项目费用列表
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
					depothead.put("type", ErpConstants.ERP_HEADER_TYPE_IS_EX_WAREHOUSE);//类型(1.出库/2.入库3.其他)
					depothead.put("subType", ORDER_OUTLET_TYPE);//销售出库
					String orderNum = ErpConstants.DepoTheadSubType.getOrderNumBySubType(ORDER_OUTLET_TYPE);
					depothead.put("defaultNumber", orderNum);//初始票据号
					depothead.put("number", orderNum);//票据号
					depothead.put("linkNumber", map.get("xsddOrderNum"));
					
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
        List<Map<String, Object>> beans = salesOrderDao.queryMationToExcel(params);
        String[] key = new String[]{"defaultNumber", "supplierName", "materialNames", "status", "totalPrice", "operPersonName", "operTime"};
        String[] column = new String[]{"单据编号", "客户", "关联产品", "状态", "合计金额", "操作人", "单据日期"};
        String[] dataType = new String[]{"", "data", "data", "data", "data", "data", "data"};
        //销售单信息导出
        ExcelUtil.createWorkBook("销售订单", "销售订单详细", beans, key, column, dataType, inputObject.getResponse());
	}

	/**
     * 获取审核通过的销售单列表展示为树
	 * -- 前提：审核通过
	 * -- 规则1：不需要统筹的订单数据
	 * -- 规则2：需要统筹并且已经进行统筹的订单数据
	 *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void querySalesOrderListToTree(InputObject inputObject, OutputObject outputObject) throws Exception {
        List<Map<String, Object>> beans = salesOrderDao.querySalesOrderListToTree();
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
	}

	/**
     * 根据销售单id获取销售的子单据列表
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void querySalesOrderMaterialListByOrderId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
		//根据订单id获取该订单下的所有商品规格信息
		List<Map<String, Object>> norms = erpCommonDao.queryOrderNormsToEditByOrderId(params.get("id").toString());
        outputObject.setBeans(norms);
        outputObject.settotal(norms.size());
	}
	
}
