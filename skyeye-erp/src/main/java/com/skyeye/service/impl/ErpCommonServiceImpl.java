/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service.impl;

import cn.hutool.json.JSONUtil;
import com.skyeye.common.constans.ErpConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.dao.ErpCommonDao;
import com.skyeye.dao.MaterialDao;
import com.skyeye.erp.entity.EditMaterialNormsObject;
import com.skyeye.erp.entity.TransmitObject;
import com.skyeye.exception.CustomException;
import com.skyeye.factory.ErpRunFactory;
import com.skyeye.service.ErpCommonService;
import com.skyeye.annotation.transaction.ActivitiAndBaseTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @ClassName: ErpCommonServiceImpl
 * @Description: ERP公共服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:42
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class ErpCommonServiceImpl implements ErpCommonService {

	private static Logger LOGGER = LoggerFactory.getLogger(ErpCommonServiceImpl.class);
	
	@Autowired
	private ErpCommonDao erpCommonDao;
	
	@Autowired
	private MaterialDao materialDao;
	
	/**
     * 获取单据详情信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void queryDepotHeadDetailsMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String orderId = map.get("id").toString();
		Map<String, Object> bean = this.queryDepotHeadMationById(orderId);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 根据单据id获取单据信息
	 *
	 * @param orderId 单据id
	 * @throws Exception
	 */
	public Map<String, Object> queryDepotHeadMationById(String orderId) throws Exception {
		// 获取主表信息
		LOGGER.info("get order mation, orderId is {}.", orderId);
		Map<String, Object> bean = erpCommonDao.queryDepotHeadDetailsMationById(orderId);
		if(bean != null && !bean.isEmpty()){
			// 获取子表信息
			List<Map<String, Object>> norms = erpCommonDao.queryDepotItemDetailsMationById(bean);
			bean.put("items", norms);
			// 获取其他费用项目列表
			if(bean.containsKey("otherMoneyList")
					&& !ToolUtil.isBlank(bean.get("otherMoneyList").toString())
					&& ToolUtil.isJson(bean.get("otherMoneyList").toString())){
				List<Map<String, Object>> jArray = JSONUtil.toList(bean.get("otherMoneyList").toString(), null);
				List<Map<String, Object>> otherMoneyList = new ArrayList<>();
				Map<String, Object> item;
				for(int i = 0; i < jArray.size(); i++){
					item = jArray.get(i);
					item = erpCommonDao.queryInoutitemMationById(item);
					otherMoneyList.add(item);
				}
				bean.put("otherMoneyList", otherMoneyList);
			}else {
				bean.put("otherMoneyList", new ArrayList<>());
			}
			return bean;
		}else{
			throw new CustomException("this erp order data is non-exits.");
		}
	}

	/**
     * 删除单据信息
	 *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void deleteErpOrderById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String orderType = map.get("orderType").toString();
		ErpRunFactory.run(inputObject, outputObject, orderType).deleteOrderMationById();
	}
	
	/**
	 * 编辑单据时，需要回显商品数据以及规格列表，该方法则为整理这些数据
	 *
	 * @param norm 规格信息
	 * @param unitList 单位列表信息
	 */
	@Override
	public void resetProductAndUnitToEditShow(Map<String, Object> norm, List<Map<String, Object>> unitList) throws Exception {
		// 构造商品数据重新放回规格对象中
		Map<String, Object> product = new HashMap<>();
		product.put("productId", norm.get("materialId"));
		product.put("productName", norm.get("materialName"));
		product.put("productModel", norm.get("materialModel"));
		// 单位列表信息
		if ("1".equals(norm.get("materialUnit").toString())) {
			// 不是多单位
			unitList.get(0).put("name", norm.get("materialUnitName").toString());
		}
		product.put("unitList", unitList);
		// 放入规格中
		norm.put("product", product);
	}
	
	/**
	 * 单据计算子单据信息
	 *
	 * @param jsonStr 子单据json字符串
	 * @param headerId 父单据id
	 * @param object 主单据信息
	 * @param entitys 要返回的单据子表实体集合信息
	 * @return 空
	 * @throws Exception
	 */
	@Override
	public void resetChildBillTypeOneMation(String jsonStr, String headerId, TransmitObject object, List<Map<String, Object>> entitys) throws Exception{
		//处理数据
		List<Map<String, Object>> jArray = JSONUtil.toList(jsonStr, null);
		for(int i = 0; i < jArray.size(); i++){
			//产 品中间转换对象，单据子表存储对象
			Map<String, Object> bean = jArray.get(i);
			Map<String, Object> entity = materialDao.queryMaterialsByNormsId(bean.get("mUnitId").toString());
			if(entity != null && !entity.isEmpty()){
				// 获取单价
				BigDecimal itemAllPrice = new BigDecimal(bean.get("unitPrice").toString());
				entity.put("id", ToolUtil.getSurFaceId());
				entity.put("headerId", headerId);//单据主表id
				entity.put("operNumber", bean.get("rkNum"));//数量
				//计算子单总价：单价*数量
				itemAllPrice = itemAllPrice.multiply(new BigDecimal(bean.get("rkNum").toString()));
				entity.put("allPrice", itemAllPrice);//单据子表总价
				
				entity.put("estimatePurchasePrice", bean.get("unitPrice"));//单价
				entity.put("taxRate", bean.containsKey("taxRate") ? bean.get("taxRate") : "0");//税率
				entity.put("taxMoney", bean.containsKey("taxMoney") ? bean.get("taxMoney") : "0");//税额
				entity.put("taxUnitPrice", bean.containsKey("taxUnitPrice") ? bean.get("taxUnitPrice") : "0");//含税单价
				entity.put("taxLastMoney", bean.containsKey("taxLastMoney") ? bean.get("taxLastMoney") : "0");//价税合计
				
				//拆分单才有
				if(bean.containsKey("materialType")){
					if("1".equals(bean.get("materialType").toString())){
						entity.put("mType", 1);//商品类型  0.普通  1.组合件  2.普通子件
					}else{
						entity.put("mType", 2);//商品类型  0.普通  1.组合件  2.普通子件
					}
				}else{
					entity.put("mType", 0);//商品类型  0.普通  1.组合件  2.普通子件
				}
				
				entity.put("anotherDepotId", bean.containsKey("anotherDepotId") ? bean.get("anotherDepotId") : "");//调入仓库id
				
				entity.put("remark", bean.get("remark"));//备注
				entity.put("depotId", bean.get("depotId"));//仓库
				entity.put("deleteFlag", 0);//删除标记，0未删除，1删除
				entitys.add(entity);
				// 计算主单总价
				object.setAllPrice(object.getAllPrice().add(itemAllPrice));
				if(bean.containsKey("taxLastMoney")){
					// 计算价税合计金额
					object.setTaxLastMoneyPrice(object.getTaxLastMoneyPrice().add(new BigDecimal(bean.get("taxLastMoney").toString())));
				}
			}
		}
	}
	
	/**
	 * 创建订单时，订单数据整理
	 * @param header 整理后的数据
	 * @param from 入参-源数据
	 * @param user 用户信息
	 */
	@Override
	public void addOrderCreateHaderMation(Map<String, Object> header, Map<String, Object> from, Map<String, Object> user) throws Exception {
		// 操作人信息--必有
		header.put("operPersonId", user.get("id"));
		header.put("operPersonName", user.get("userName"));
		// 创建时间--必有
		header.put("createTime", DateUtil.getTimeAndToString());
		// 删除标记，0未删除，1删除--必有
		header.put("deleteFlag", 0);
		// 状态：0未审核、1.审核中、2.审核通过、3.审核拒绝、4.已转采购|销售-已完成；默认未审核--必有
		header.put("status", ErpConstants.ERP_HEADER_STATUS_IS_NOT_APPROVED);
		
		// 新增单据时，包含单据编辑可编辑的内容
		editOrderCreateHaderMation(header, from, user);
	}
	
	/**
	 * 编辑订单时，订单数据整理
	 *
	 * @param header 整理后的数据
	 * @param from 入参-源数据
	 * @param user 用户信息
	 */
	@Override
    public void editOrderCreateHaderMation(Map<String, Object> header, Map<String, Object> from,
        Map<String, Object> user) throws Exception {
        // 出入库时间即单据日期--必有
        header.put("operTime", from.get("operTime"));
        // 备注--必有
        header.put("remark", from.get("remark"));
        // 找零金额---零售出库,零售退货才有
		if (from.containsKey("giveChange") && !ToolUtil.isBlank(from.get("giveChange").toString())) {
			header.put("giveChange", from.get("giveChange"));
		}

        // 供应商Id/客户id/会员id--可有可无：采购单(供应商)，销售订单(客户)
        header.put("organId", from.containsKey("supplierId") ? from.get("supplierId") : "");
        // 账户Id--可有可无
        header.put("accountId", from.containsKey("accountId") ? from.get("accountId") : "");
        // 付款类型--可有可无
        header.put("payType", from.containsKey("payType") ? from.get("payType") : "3");
        // 销售人员--可有可无
        header.put("salesMan", from.containsKey("salesMan") ? from.get("salesMan") : "");
        // 优惠率--可有可无
        header.put("discount", from.containsKey("discount") ? from.get("discount") : "0");
        // 优惠金额--可有可无
        header.put("discountMoney", from.containsKey("discountMoney") ? from.get("discountMoney") : "0");
        // 本次付款金额--可有可无
        header.put("changeAmount", from.containsKey("changeAmount") ? from.get("changeAmount") : "0");
        // 销售费用合计--可有可无
        header.put("otherMoney", from.containsKey("otherMoney") ? from.get("otherMoney") : "0");
        // 采购费用涉及项目Id（包括快递、招待等）json串--可有可无
        header.put("otherMoneyList", from.containsKey("otherMoneyList") ? from.get("otherMoneyList") : "");
        // 订单是否需要统筹 1.需要 2.不需要
        header.put("needOverPlan", from.containsKey("needOverPlan") ? from.get("needOverPlan") : "2");
        // 计划完成日期--非必需
        if (from.containsKey("planComplateTime") && !ToolUtil.isBlank(from.get("planComplateTime").toString())) {
            header.put("planComplateTime", from.get("planComplateTime"));
        }
        // 表单提交类型，默认设置为草稿类型的表单提交
		header.put("subType", from.containsKey("subType") ? from.get("subType") : "1");
        // 单据提交类型  1.走工作流提交  2.直接提交  默认设置为直接提交
		header.put("submitType", from.containsKey("submitType") ? from.get("submitType") : "2");
    }

	/**
	 * 修改商品规格库存
	 *
	 * @param depotId 仓库id
	 * @param materialId 商品id
	 * @param normsId 规格id
	 * @param operNumber 变化数量
	 * @param type 1.入库  2.出库
	 * @throws Exception    参数
	 * @return void    返回类型
	 */
	@Override
	public void editMaterialNormsDepotStock(String depotId, String materialId, String normsId, String operNumber, int type) throws Exception {
		EditMaterialNormsObject materialNormsObject = new EditMaterialNormsObject();
		materialNormsObject.setDepotId(depotId);
		materialNormsObject.setMaterialId(materialId);
		materialNormsObject.setNormsId(normsId);
		materialNormsObject.setOperNumber(operNumber);
		materialNormsObject.setType(type);
		synchronized(materialNormsObject){
			//变化的数量
			int changeNumber = Integer.parseInt(materialNormsObject.getOperNumber());
			Map<String, Object> stock = erpCommonDao.queryDepotStockByDepotIdAndNormsId(materialNormsObject.getDepotId(), materialNormsObject.getNormsId());
			//如果该规格在指定仓库中已经有存储数据，则直接做修改
			if(stock != null && !stock.isEmpty()){
				int stockNum = Integer.parseInt(stock.get("stockNum").toString());
				if(materialNormsObject.getType() == 1){
					//入库
					stockNum = stockNum + changeNumber;
				}else if(materialNormsObject.getType() == 2){
					//出库
					stockNum = stockNum - changeNumber;
				}else{
					throw new Exception("状态错误");
				}
				erpCommonDao.editDepotStockByDepotIdAndNormsId(materialNormsObject.getDepotId(), materialNormsObject.getNormsId(), stockNum);
			}else{
				int stockNum = 0;
				if(materialNormsObject.getType() == 1){
					//入库
					stockNum = stockNum + changeNumber;
				}else if(materialNormsObject.getType() == 2){
					//出库
					stockNum = stockNum - changeNumber;
				}else{
					throw new Exception("状态错误");
				}
				erpCommonDao.insertDepotStockByDepotIdAndNormsId(materialNormsObject.getMaterialId(), materialNormsObject.getDepotId(), materialNormsObject.getNormsId(), stockNum);
			}
		}
	}

	/**
	 * 获取订单流水线信息-方便实时监控进度
	 *
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	public void queryDepotFlowLineDetailsMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String orderId = map.get("id").toString();
		int type = Integer.parseInt(map.get("type").toString());
		Map<String, Object> mation = getDepotMationByIdAndType(orderId, type);
		if(mation != null && !mation.isEmpty()){
			Map<String, Object> result = new HashMap<>();
			result.put("title", ErpConstants.DepoTheadSubType.getTitle(mation.get("subType").toString()));
			result.put("name", mation.get("defaultNumber"));
			result.put("subType", mation.get("subType"));
			result.put("planComplateDate", mation.containsKey("planComplateDate") ? mation.get("planComplateDate") : "");
			result.put("realComplateDate", mation.containsKey("realComplateDate") ? mation.get("realComplateDate") : "");
			result.put("state", mation.get("state"));
			result.put("children", queryChildOrderMation(orderId, type));
			outputObject.setBean(result);
		}else{
			outputObject.setreturnMessage("this order is non-exits.");
		}
	}

	/**
	 * （erp+生产）根据单据id获取订单信息
	 *
	 * @param orderId 订单id
	 * @param type    单据类型  1.销售订单；2.生产计划单；3.加工单；4.采购订单
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> getDepotMationByIdAndType(String orderId, int type) throws Exception {
		Map<String, Object> mation = null;
		if(type == 1 || type == 4){
			mation = erpCommonDao.queryDepotHeadDetailsMationById(orderId);
			mation.put("planComplateDate", mation.containsKey("planComplateTime") ? mation.get("planComplateTime") : "");
			mation.put("realComplateDate", mation.containsKey("realComplateTime") ? mation.get("realComplateTime") : "");
		}else if(type == 2){
			mation = erpCommonDao.queryProductionHeadDetailsMationById(orderId,
					Integer.parseInt(ErpConstants.DepoTheadSubType.PRODUCTION_HEAD.getType()));
		}else if(type == 3){
			mation = erpCommonDao.queryMachinDetailsMationById(orderId,
					Integer.parseInt(ErpConstants.DepoTheadSubType.MACHIN_HEADER.getType()));
		}
		return mation;
	}

	/**
	 * （erp+生产）根据单据id以及类型获取所有子单据的信息
	 *
	 * @param orderId 订单id
	 * @param type    单据类型  1.销售订单；2.生产计划单；3.加工单；4.采购订单
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<Map<String, Object>> queryChildOrderMation(String orderId, int type) throws Exception {
		List<Map<String, Object>> order = new ArrayList<>();
		if(type == 1){
			queryErpProductionHead(order, orderId);
		}else if(type == 2){
			queryErpMachinHead(order, Arrays.asList(new String[]{orderId}));
		}else if(type == 3){
			queryErpMachinChild(order, Arrays.asList(new String[]{orderId}));
		}else if(type == 4){
			queryErpPurchasePutHead(order, Arrays.asList(new String[]{orderId}));
		}
		order.forEach(bean -> {
			bean.put("title", ErpConstants.DepoTheadSubType.getTitle(bean.get("subType").toString()));
		});
		order = ToolUtil.listToTree(order, "id", "parentId", "children");
		return order;
	}

	/**
	 * 根据销售单id获取生产计划单
	 *
	 * @param order 单据列表
	 * @param orderId 订单id
	 */
	private void queryErpProductionHead(List<Map<String, Object>> order, String orderId) throws Exception {
		// 生产计划单
		List<Map<String, Object>> productionHead = erpCommonDao.queryErpProductionHeadByPId(orderId,
				Integer.parseInt(ErpConstants.DepoTheadSubType.PRODUCTION_HEAD.getType()));
		if(!productionHead.isEmpty()){
			order.addAll(productionHead);
			List<String> productionHeadIds = productionHead.stream().map(p -> p.get("id").toString()).collect(Collectors.toList());
			// 获取采购单
			queryErpPurchaseHead(order, productionHeadIds);
			// 获取加工单
			queryErpMachinHead(order, productionHeadIds);
		}
		// 获取销售入库单
		queryErpSalesOutHead(order, orderId);
	}

	/**
	 * 根据生产计划单id获取加工单
	 *
	 * @param order 单据列表
	 * @param orderId 订单id
	 */
	private void queryErpMachinHead(List<Map<String, Object>> order, List<String> orderId) throws Exception {
		List<Map<String, Object>> machinHead = erpCommonDao.queryErpMachinHead(orderId,
				Integer.parseInt(ErpConstants.DepoTheadSubType.MACHIN_HEADER.getType()));
		if(!machinHead.isEmpty()){
			order.addAll(machinHead);
			List<String> machinHeadIds = machinHead.stream().map(p -> p.get("id").toString()).collect(Collectors.toList());
			// 获取子单据(工序验收单)
			queryErpMachinChild(order, machinHeadIds);
		}
	}

	/**
	 * 根据生产计划单id获取采购单
	 *
	 * @param order 单据列表
	 * @param orderId 订单id
	 */
	private void queryErpPurchaseHead(List<Map<String, Object>> order, List<String> orderId) throws Exception {
		List<Map<String, Object>> purchaseOrderHead = erpCommonDao.queryErpPurchaseHead(orderId);
		if(!purchaseOrderHead.isEmpty()){
			order.addAll(purchaseOrderHead);
			List<String> purchaseOrderHeadIds = purchaseOrderHead.stream().map(p -> p.get("id").toString()).collect(Collectors.toList());
			// 获取采购入库单
			queryErpPurchasePutHead(order, purchaseOrderHeadIds);
		}
	}

	/**
	 * 根据加工单id获取子单据(工序验收单)
	 *
	 * @param order 单据列表
	 * @param orderId 订单id
	 */
	private void queryErpMachinChild(List<Map<String, Object>> order, List<String> orderId) throws Exception {
		List<Map<String, Object>> machinHead = erpCommonDao.queryErpMachinChild(orderId,
				Integer.parseInt(ErpConstants.DepoTheadSubType.MACHIN_CHILD.getType()));
		if(!machinHead.isEmpty()){
			order.addAll(machinHead);
			// 获取验收入库单
			queryErpMachinPut(order, orderId);
			// 获取领料单/退料单/补料单
			queryErpPickOrder(order, orderId);
		}
	}

	/**
	 * 根据加工单id获取验收入库单
	 *
	 * @param order 单据列表
	 * @param orderId 订单id
	 */
	private void queryErpMachinPut(List<Map<String, Object>> order, List<String> orderId) throws Exception {
		List<Map<String, Object>> machinPutHead = erpCommonDao.queryErpMachinPut(orderId);
		order.addAll(machinPutHead);
	}

	/**
	 * 根据采购单id获取采购入库单
	 *
	 * @param order 单据列表
	 * @param orderId 订单id
	 */
	private void queryErpPurchasePutHead(List<Map<String, Object>> order, List<String> orderId) throws Exception {
		List<Map<String, Object>> machinHead = erpCommonDao.queryErpPurchasePutHead(orderId);
		order.addAll(machinHead);
	}

	/**
	 * 根据销售单id获取销售出库单
	 *
	 * @param order 单据列表
	 * @param orderId 订单id
	 */
	private void queryErpSalesOutHead(List<Map<String, Object>> order, String orderId) throws Exception {
		List<Map<String, Object>> machinHead = erpCommonDao.queryErpSalesOutHead(orderId);
		order.addAll(machinHead);
	}

	/**
	 * 获取领料单/退料单/补料单
	 *
	 * @param order 单据列表
	 * @param orderId 订单id
	 * @throws Exception
	 */
	private void queryErpPickOrder(List<Map<String, Object>> order, List<String> orderId) throws Exception {
		// 单据类型  19.领料单  20.补料单  21.退料单
		List<String> orderTypes = Arrays.asList(new String[]{ErpConstants.DepoTheadSubType.PICK_PICKING.getType(),
				ErpConstants.DepoTheadSubType.PICK_REPLENISHMENT.getType(), ErpConstants.DepoTheadSubType.PICK_RETURN.getType()});
		orderTypes.forEach(orderType -> {
			try {
				List<Map<String, Object>> pick = erpCommonDao.queryErpPickHead(orderId, Integer.parseInt(orderType));
				order.addAll(pick);
			} catch (Exception e) {
				LOGGER.info("queryErpPickOrder -> queryErpPickHead failed.", e);
			}
		});
	}

	/**
	 * erp相关单据撤销审批
	 *
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	@ActivitiAndBaseTransaction(value = {"transactionManager"})
	public void editDepotHeadToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception {
		String orderType = inputObject.getParams().get("orderType").toString();
		ErpRunFactory.run(inputObject, outputObject, orderType).revokeOrder();
	}

	/**
	 * erp相关单据根据订单类型获取数据提交类型
	 *
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	public void queryDepotHeadSubmitTypeByOrderType(InputObject inputObject, OutputObject outputObject) throws Exception{
		String orderType = inputObject.getParams().get("orderType").toString();
		Integer needExamine = ErpRunFactory.run(inputObject, outputObject, orderType).getSubmitTypeByOrderType();
		Map<String, Object> result = new HashMap<>();
		result.put("needExamine", needExamine);
		outputObject.setBean(result);
		outputObject.settotal(1);
	}

	/**
	 * 订单信息提交审核
	 *
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	@Transactional(value="transactionManager")
	public void orderSubmitToApproval(InputObject inputObject, OutputObject outputObject) throws Exception {
		String orderType = inputObject.getParams().get("orderType").toString();
		ErpRunFactory.run(inputObject, outputObject, orderType).subOrderToExamineById();
	}

}
