/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service.impl;

import cn.hutool.json.JSONUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.constans.ErpConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.dao.ErpMachinDao;
import com.skyeye.dao.ErpPickDao;
import com.skyeye.dao.MaterialDao;
import com.skyeye.erp.entity.TransmitObject;
import com.skyeye.service.ErpCommonService;
import com.skyeye.service.ErpPickService;
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
 * @ClassName: ErpPickServiceImpl
 * @Description: 领料单、补料单、退料单管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:48
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class ErpPickServiceImpl implements ErpPickService{
	
	@Autowired
	private ErpPickDao erpPickDao;
	
	@Autowired
	private MaterialDao materialDao;
	
	@Autowired
	private ErpMachinDao erpMachinDao;
	
	@Autowired
	private ErpCommonService erpCommonService;

	/**
	 * 领料单类型
	 */
	private static final String ORDER_REQUISITION_TYPE = ErpConstants.DepoTheadSubType.PICK_PICKING.getType();

	/**
	 * 补料单类型
	 */
	private static final String ORDER_PATCH_TYPE = ErpConstants.DepoTheadSubType.PICK_REPLENISHMENT.getType();

	/**
	 * 退料单类型
	 */
	private static final String ORDER_RETURN_TYPE = ErpConstants.DepoTheadSubType.PICK_RETURN.getType();
	
	/**
     * 获取领料单列表信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void queryRequisitionMaterialOrderList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		params.put("type", ORDER_REQUISITION_TYPE);
		getPickList(outputObject, params, user);
	}

	/**
     * 获取退料单列表信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void queryReturnMaterialOrderList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		params.put("type", ORDER_RETURN_TYPE);
		getPickList(outputObject, params, user);
	}

	/**
     * 获取补料单列表信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void queryPatchMaterialOrderList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		params.put("type", ORDER_PATCH_TYPE);
		getPickList(outputObject, params, user);
	}

	private void getPickList(OutputObject outputObject, Map<String, Object> params, Map<String, Object> user) throws Exception {
		String departMentId = user.get("departmentId").toString();
		params.put("departMentId", departMentId);
		Page pages = PageHelper.startPage(Integer.parseInt(params.get("page").toString()), Integer.parseInt(params.get("limit").toString()));
		List<Map<String, Object>> beans = erpPickDao.queryErpPickOrderList(params);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 创建订单时，订单数据整理
	 *
	 * @param depothead 整理后的数据
	 * @param from 入参-源数据
	 * @param user 用户信息
	 * @param type 单据类型
	 * @throws Exception 
	 */
	private void addOrderCreateHaderMation(Map<String, Object> depothead, Map<String, Object> from, Map<String, Object> user, String type) throws Exception {
		String orderNum = ErpConstants.DepoTheadSubType.getOrderNumBySubType(type);
		depothead.put("defaultNumber", orderNum); // 初始票据号
		depothead.put("status", 0); // 状态默认0：新建
		depothead.put("departmentId", user.get("departmentId")); // 部门
		depothead.put("createId", user.get("id")); // 创建人
		depothead.put("createTime", DateUtil.getTimeAndToString()); // 创建时间
		editOrderCreateHaderMation(depothead, from, user);
	}
	
	/**
	 * 编辑订单时，订单数据整理
	 * @param header 整理后的数据
	 * @param from 入参-源数据
	 * @param user 用户信息
	 */
	public void editOrderCreateHaderMation(Map<String, Object> header, Map<String, Object> from, Map<String, Object> user) throws Exception {
		// 出入库时间即单据日期--必有
		header.put("operTime", from.get("operTime"));
		// 备注--必有
		header.put("remark", from.get("remark"));
		// 仓库--必有
		header.put("depotId", from.get("depotId"));
		// 加工单id--非必有
		header.put("machinId", from.containsKey("machinId") ? from.get("machinId") : "");
		// 计划单id--非必有
		header.put("productionId", from.containsKey("productionId") ? from.get("productionId") : "");
	}

	/**
	 * 单据计算子单据信息
	 *
	 * @param materialStr 子单据json字符串
	 * @param headerId 父单据id
	 * @param object 父单据总价对象
	 * @param entitys 要返回的单据子表实体集合信息
	 * @param user 用户信息
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private void resetMaterialChildMation(String materialStr, String headerId, TransmitObject object, List<Map<String, Object>> entitys, Map<String, Object> user) throws Exception {
		//处理数据
		List<Map<String, Object>> jArray = JSONUtil.toList(materialStr, null);
		//产品中间转换对象，单据子表存储对象
		Map<String, Object> bean, entity;
		//子单对象
		BigDecimal itemAllPrice = null;
		for(int i = 0; i < jArray.size(); i++){
			bean = jArray.get(i);
			entity = materialDao.queryMaterialsByNormsId(bean.get("mUnitId").toString());
			if(entity != null && !entity.isEmpty()){
				//获取单价
				itemAllPrice = new BigDecimal(bean.get("unitPrice").toString());
				entity.put("id", ToolUtil.getSurFaceId());
				entity.put("headerId", headerId);//单据主表id
				entity.put("departmentId", user.get("departmentId"));//部门
				entity.put("operNumber", bean.get("rkNum"));//数量
				entity.put("unitPrice", bean.get("unitPrice"));//单价
				//计算子单总价：单价*数量
				itemAllPrice = itemAllPrice.multiply(new BigDecimal(bean.get("rkNum").toString()));
				entity.put("allPrice", itemAllPrice);//单据子表总价
				entitys.add(entity);
				//计算主单总价
				object.setAllPrice(object.getAllPrice().add(itemAllPrice));
			}
		}
	}

	/**
     * 新增领料单信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	@Transactional(value="transactionManager")
	public void insertRequisitionMaterialOrder(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String materialStr = map.get("materialStr").toString();
		if(ToolUtil.isJson(materialStr)){
			// 单据主表id
			String useId = ToolUtil.getSurFaceId();
			// 值传递对象，获取对应的值即可
			TransmitObject object = new TransmitObject();
			// 单据子表实体集合信息
			List<Map<String, Object>> entitys = new ArrayList<>();
			resetMaterialChildMation(materialStr, useId, object, entitys, inputObject.getLogParams());
			if(entitys == null || entitys.isEmpty()){
				outputObject.setreturnMessage("请选择商品.");
				return;
			}
			//单据主表对象
			Map<String, Object> depothead = new HashMap<>();
			addOrderCreateHaderMation(depothead, map, inputObject.getLogParams(), ORDER_REQUISITION_TYPE);
			depothead.put("id", useId);
			depothead.put("type", ORDER_REQUISITION_TYPE);//领料单
			//合计金额
			depothead.put("totalPrice", object.getAllPrice());
			erpPickDao.insertOrderParentMation(depothead);
			erpPickDao.insertOrderChildMation(entitys);
		}else{
			outputObject.setreturnMessage("数据格式错误.");
		}
	}

	/**
     * 编辑领料单时获取信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void queryRequisitionMaterialOrderToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String orderId = map.get("id").toString();
		Map<String, Object> mation = erpPickDao.queryHeaderMationById(orderId, ORDER_REQUISITION_TYPE);
		if(mation != null && !mation.isEmpty()){
			// 获取子件信息
			List<Map<String, Object>> materiel = erpPickDao.queryChildMationById(orderId);
			materiel.forEach(bean -> {
				// 规格信息
				List<Map<String, Object>> maUnitList;
				try {
					maUnitList = materialDao.queryMaterialUnitByIdToSelect(bean.get("productId").toString());
					if ("1".equals(bean.get("unit").toString())) {// 不是多单位
						maUnitList.get(0).put("name", bean.get("unitName").toString());
					}
					bean.put("unitList", maUnitList);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			mation.put("items", materiel);
			
			//判断是否绑定加工单id
			if(mation.containsKey("machinId") && !ToolUtil.isBlank(mation.get("machinId").toString())){
				//获取绑定的加工单信息
				mation.put("machinToMation", erpMachinDao.queryMachinStateIsPassOrderMationById(mation.get("machinId").toString()));
			}else{
				mation.put("machinToMation", new ArrayList<>());
			}
			
			outputObject.setBean(mation);
		}else{
			outputObject.setreturnMessage("该单据信息不存在.");
		}
	}

	/**
     * 编辑领料单信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	@Transactional(value="transactionManager")
	public void editRequisitionMaterialOrderById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		// 单据主表id
		String useId = map.get("id").toString();
		String materialStr = map.get("materialStr").toString();
		Map<String, Object> mation = erpPickDao.queryHeaderMationById(useId, ORDER_REQUISITION_TYPE);
		if(mation == null || mation.isEmpty()){
			outputObject.setreturnMessage("该单据不存在.");
			return;
		}
		if("1".equals(mation.get("status").toString())
				|| "2".equals(mation.get("status").toString())){
			// 审核中/审核通过无法编辑
			outputObject.setreturnMessage("该单据状态已更改.");
			return;
		}
		if(ToolUtil.isJson(materialStr)){
			// 值传递对象，获取对应的值即可
			TransmitObject object = new TransmitObject();
			// 单据子表实体集合信息
			List<Map<String, Object>> entitys = new ArrayList<>();
			resetMaterialChildMation(materialStr, useId, object, entitys, inputObject.getLogParams());
			if(entitys == null || entitys.isEmpty()){
				outputObject.setreturnMessage("请选择商品.");
				return;
			}
			// 单据主表对象
			Map<String, Object> depothead = new HashMap<>();
			editOrderCreateHaderMation(depothead, map, inputObject.getLogParams());
			depothead.put("id", useId);
			// 合计金额
			depothead.put("totalPrice", object.getAllPrice());
			// 编辑单据信息
			depothead.put("type", ORDER_REQUISITION_TYPE);
			int size = erpPickDao.editRequisitionMaterialOrderById(depothead);
			if(size > 0){
				erpPickDao.deleteOrderChildMationByHeaderId(useId);
				erpPickDao.insertOrderChildMation(entitys);
			}
		}else{
			outputObject.setreturnMessage("数据格式错误.");
		}
	}

	/**
     * 获取领料/补料/退料单信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void queryPickOrderMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String orderId = map.get("id").toString();
		Map<String, Object> mation = erpPickDao.queryPickOrderMationById(orderId);
		if(mation != null && !mation.isEmpty()){
			// 获取子件信息
			List<Map<String, Object>> materiel = erpPickDao.queryChildMationById(orderId);
			mation.put("items", materiel);
			outputObject.setBean(mation);
			outputObject.settotal(1);
		}else{
			outputObject.setreturnMessage("该单据信息不存在.");
		}
	}

	/**
     * 删除领料/补料/退料单信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	@Transactional(value="transactionManager")
	public void deletePickOrderMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String orderId = map.get("id").toString();
		Map<String, Object> mation = erpPickDao.queryPickOrderMationById(orderId);
		if(mation == null || mation.isEmpty()){
			outputObject.setreturnMessage("该单据不存在.");
			return;
		}
		if("1".equals(mation.get("status").toString())
				|| "2".equals(mation.get("status").toString())){
			// 审核中/审核通过无法编辑
			outputObject.setreturnMessage("该单据状态已更改.");
			return;
		}
		erpPickDao.deletePickOrderMationById(orderId);
		erpPickDao.deleteOrderChildMationByHeaderId(orderId);
	}

	/**
     * 新增补料单信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	@Transactional(value="transactionManager")
	public void insertPatchMaterialOrder(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String materialStr = map.get("materialStr").toString();
		if(ToolUtil.isJson(materialStr)){
			// 单据主表id
			String useId = ToolUtil.getSurFaceId();
			// 值传递对象，获取对应的值即可
			TransmitObject object = new TransmitObject();
			// 单据子表实体集合信息
			List<Map<String, Object>> entitys = new ArrayList<>();
			resetMaterialChildMation(materialStr, useId, object, entitys, inputObject.getLogParams());
			if(entitys == null || entitys.isEmpty()){
				outputObject.setreturnMessage("请选择商品.");
				return;
			}
			//单据主表对象
			Map<String, Object> depothead = new HashMap<>();
			addOrderCreateHaderMation(depothead, map, inputObject.getLogParams(), ORDER_PATCH_TYPE);
			depothead.put("id", useId);
			depothead.put("type", ORDER_PATCH_TYPE);//补料单
			//合计金额
			depothead.put("totalPrice", object.getAllPrice());
			erpPickDao.insertOrderParentMation(depothead);
			erpPickDao.insertOrderChildMation(entitys);
		}else{
			outputObject.setreturnMessage("数据格式错误.");
		}
	}

	/**
     * 编辑补料单时获取信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	@Transactional(value="transactionManager")
	public void queryPatchMaterialOrderToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String orderId = map.get("id").toString();
		Map<String, Object> mation = erpPickDao.queryHeaderMationById(orderId, ORDER_PATCH_TYPE);
		if(mation != null && !mation.isEmpty()){
			// 获取子件信息
			List<Map<String, Object>> materiel = erpPickDao.queryChildMationById(orderId);
			materiel.forEach(bean -> {
				// 规格信息
				List<Map<String, Object>> maUnitList;
				try {
					maUnitList = materialDao.queryMaterialUnitByIdToSelect(bean.get("productId").toString());
					if ("1".equals(bean.get("unit").toString())) {// 不是多单位
						maUnitList.get(0).put("name", bean.get("unitName").toString());
					}
					bean.put("unitList", maUnitList);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			mation.put("items", materiel);
			outputObject.setBean(mation);
		}else{
			outputObject.setreturnMessage("该单据信息不存在.");
		}
	}

	/**
     * 编辑补料单信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	@Transactional(value="transactionManager")
	public void editPatchMaterialOrderById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		// 单据主表id
		String useId = map.get("id").toString();
		String materialStr = map.get("materialStr").toString();
		Map<String, Object> mation = erpPickDao.queryHeaderMationById(useId, ORDER_PATCH_TYPE);
		if(mation == null || mation.isEmpty()){
			outputObject.setreturnMessage("该单据不存在.");
			return;
		}
		if("1".equals(mation.get("status").toString())
				|| "2".equals(mation.get("status").toString())){
			// 审核中/审核通过无法编辑
			outputObject.setreturnMessage("该单据状态已更改.");
			return;
		}
		if(ToolUtil.isJson(materialStr)){
			// 值传递对象，获取对应的值即可
			TransmitObject object = new TransmitObject();
			// 单据子表实体集合信息
			List<Map<String, Object>> entitys = new ArrayList<>();
			resetMaterialChildMation(materialStr, useId, object, entitys, inputObject.getLogParams());
			if(entitys == null || entitys.isEmpty()){
				outputObject.setreturnMessage("请选择商品.");
				return;
			}
			// 单据主表对象
			Map<String, Object> depothead = new HashMap<>();
			editOrderCreateHaderMation(depothead, map, inputObject.getLogParams());
			depothead.put("id", useId);
			// 合计金额
			depothead.put("totalPrice", object.getAllPrice());
			// 编辑单据信息
			depothead.put("type", ORDER_PATCH_TYPE);
			int size = erpPickDao.editRequisitionMaterialOrderById(depothead);
			if(size > 0){
				erpPickDao.deleteOrderChildMationByHeaderId(useId);
				erpPickDao.insertOrderChildMation(entitys);
			}
		}else{
			outputObject.setreturnMessage("数据格式错误.");
		}
	}

	/**
     * 新增退料单信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	@Transactional(value="transactionManager")
	public void insertReturnMaterialOrder(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String materialStr = map.get("materialStr").toString();
		if(ToolUtil.isJson(materialStr)){
			// 单据主表id
			String useId = ToolUtil.getSurFaceId();
			// 值传递对象，获取对应的值即可
			TransmitObject object = new TransmitObject();
			// 单据子表实体集合信息
			List<Map<String, Object>> entitys = new ArrayList<>();
			resetMaterialChildMation(materialStr, useId, object, entitys, inputObject.getLogParams());
			if(entitys == null || entitys.isEmpty()){
				outputObject.setreturnMessage("请选择商品.");
				return;
			}
			//单据主表对象
			Map<String, Object> depothead = new HashMap<>();
			addOrderCreateHaderMation(depothead, map, inputObject.getLogParams(), ORDER_RETURN_TYPE);
			depothead.put("id", useId);
			depothead.put("type", ORDER_RETURN_TYPE);//退料单
			//合计金额
			depothead.put("totalPrice", object.getAllPrice());
			erpPickDao.insertOrderParentMation(depothead);
			erpPickDao.insertOrderChildMation(entitys);
		}else{
			outputObject.setreturnMessage("数据格式错误.");
		}
	}

	/**
     * 编辑退料单时获取信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	@Transactional(value="transactionManager")
	public void queryReturnMaterialOrderToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String orderId = map.get("id").toString();
		Map<String, Object> mation = erpPickDao.queryHeaderMationById(orderId, ORDER_RETURN_TYPE);
		if(mation != null && !mation.isEmpty()){
			// 获取子件信息
			List<Map<String, Object>> materiel = erpPickDao.queryChildMationById(orderId);
			materiel.forEach(bean -> {
				// 规格信息
				List<Map<String, Object>> maUnitList;
				try {
					maUnitList = materialDao.queryMaterialUnitByIdToSelect(bean.get("productId").toString());
					if ("1".equals(bean.get("unit").toString())) {// 不是多单位
						maUnitList.get(0).put("name", bean.get("unitName").toString());
					}
					bean.put("unitList", maUnitList);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			mation.put("items", materiel);
			outputObject.setBean(mation);
		}else{
			outputObject.setreturnMessage("该单据信息不存在.");
		}
	}

	/**
     * 编辑退料单信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	@Transactional(value="transactionManager")
	public void editReturnMaterialOrderById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		// 单据主表id
		String useId = map.get("id").toString();
		String materialStr = map.get("materialStr").toString();
		Map<String, Object> mation = erpPickDao.queryHeaderMationById(useId, ORDER_RETURN_TYPE);
		if(mation == null || mation.isEmpty()){
			outputObject.setreturnMessage("该单据不存在.");
			return;
		}
		if("1".equals(mation.get("status").toString())
				|| "2".equals(mation.get("status").toString())){
			// 审核中/审核通过无法编辑
			outputObject.setreturnMessage("该单据状态已更改.");
			return;
		}
		if(ToolUtil.isJson(materialStr)){
			// 值传递对象，获取对应的值即可
			TransmitObject object = new TransmitObject();
			// 单据子表实体集合信息
			List<Map<String, Object>> entitys = new ArrayList<>();
			resetMaterialChildMation(materialStr, useId, object, entitys, inputObject.getLogParams());
			if(entitys == null || entitys.isEmpty()){
				outputObject.setreturnMessage("请选择商品.");
				return;
			}
			// 单据主表对象
			Map<String, Object> depothead = new HashMap<>();
			editOrderCreateHaderMation(depothead, map, inputObject.getLogParams());
			depothead.put("id", useId);
			// 合计金额
			depothead.put("totalPrice", object.getAllPrice());
			// 编辑单据信息
			depothead.put("type", ORDER_RETURN_TYPE);
			int size = erpPickDao.editRequisitionMaterialOrderById(depothead);
			if(size > 0){
				erpPickDao.deleteOrderChildMationByHeaderId(useId);
				erpPickDao.insertOrderChildMation(entitys);
			}
		}else{
			outputObject.setreturnMessage("数据格式错误.");
		}
	}

	/**
     * 领料/补料/退料单信息提交审核
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void editPickOrderMationToSubById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String orderId = map.get("id").toString();
		Map<String, Object> mation = erpPickDao.queryPickOrderMationById(orderId);
		if(mation == null || mation.isEmpty()){
			outputObject.setreturnMessage("该单据不存在.");
			return;
		}
		if("0".equals(mation.get("status").toString())
				|| "3".equals(mation.get("status").toString())){
			// 未审核/审核拒绝可以提交审核
			erpPickDao.editPickOrderMationToSubById(orderId);
		}else{
			outputObject.setreturnMessage("该单据状态已更改.");
			return;
		}
	}

	/**
     * 领料/补料/退料单审核
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void editPickOrderMationToExamineById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String orderId = map.get("id").toString();
		// 获取单据信息
		Map<String, Object> orderMation = erpPickDao.queryPickOrderMationById(orderId);
		if(orderMation != null && !orderMation.isEmpty()){
			// 单据状态
			String status = orderMation.get("status").toString();
			if(ErpConstants.ERP_HEADER_STATUS_IS_IN_APPROVED.equals(status)){
				// 订单当前状态为审核中的可以进行审核操作
				String upStatus = map.get("status").toString();
				if("1".equals(upStatus)){
					orderMation.put("status", ErpConstants.ERP_HEADER_STATUS_IS_APPROVED_PASS);
					synchronized(orderId){
						// 单据类型，领料单/补料单/退料单
						String orderType = orderMation.get("type").toString();
						String departMentId = orderMation.get("departmentId").toString();
						// 获取当前单据所有规格的数量,库存数量,部门存量
						List<Map<String, Object>> thisOrderNum = erpPickDao.queryPickOrderMationToExamineById(orderId, departMentId);
						// 根据单据类型做不同的操作
						if(ORDER_REQUISITION_TYPE.equals(orderType)){
							// 领料单
							// 1.判断库存是否充足
							if(!checkStockWhetherOutstrip(thisOrderNum)){
								outputObject.setreturnMessage("指定仓库库存不足，领料失败.");
								return;
							}
							// 2.修改加工单状态为已领料
							if(orderMation.containsKey("machinId") && !ToolUtil.isBlank(orderMation.get("machinId").toString())){
								erpPickDao.editMachinStateISPickedByMachinId(orderMation.get("machinId").toString());
							}
						}else if(ORDER_PATCH_TYPE.equals(orderType)){
							// 补料单
							// 1.判断库存是否充足
							if(!checkStockWhetherOutstrip(thisOrderNum)){
								outputObject.setreturnMessage("指定仓库库存不足，补料失败.");
								return;
							}
						}else if(ORDER_RETURN_TYPE.equals(orderType)){
							// 退料单
							// 1.判断部门存量是否充足
							if(!checkDepartStockWhetherOutstrip(thisOrderNum)){
								outputObject.setreturnMessage("部门库存存量不足，退料失败.");
								return;
							}
						}else{
							outputObject.setreturnMessage("单据类型错误.");
							return;
						}
						//修改库存
						updateStock(thisOrderNum, orderType);
					}
				}else if("0".equals(upStatus)){
					//审核不通过
					orderMation.put("status", ErpConstants.ERP_HEADER_STATUS_IS_APPROVED_NOT_PASS);
				}else{
					outputObject.setreturnMessage("参数错误");
					return;
				}
				// 修改单据状态
				editPickOrderMationToExamineById(orderId, map, inputObject.getLogParams(), orderMation);
			}
		}else{
			outputObject.setreturnMessage("该单据状态已经改变或数据不存在.");
		}
	}

	/**
	 * 修改领料/补料/退料单审核结果信息
	 *
	 * @param orderId 单据id
	 * @param map 前台传递的参数集合
	 * @param user 用户对象
	 * @param orderMation 订单信息
	 */
	private void editPickOrderMationToExamineById(String orderId, Map<String, Object> map, Map<String, Object> user, Map<String, Object> orderMation) throws Exception {
		Map<String, Object> mation = new HashMap<>();
		mation.put("orderId", orderId);
		mation.put("examineContent", map.get("content"));
		mation.put("examineId", user.get("id"));
		mation.put("examineTime", DateUtil.getTimeAndToString());
		mation.put("realComplateTime", DateUtil.getTimeAndToString());
		mation.put("status", orderMation.get("status"));
		erpPickDao.editPickOrderMationToExamineById(mation);
	}
	
	/**
	 * @throws Exception 
	 * 
	    * @Title: updateStock
	    * @Description: 修改库存信息
	    * @param thisOrderNum 单据子表
	    * @param orderType 单据类型
	    * @return void    返回类型
	    * @throws
	 */
	private void updateStock(List<Map<String, Object>> thisOrderNum, String orderType) throws Exception {
		for(Map<String, Object> bean : thisOrderNum){
			String depotId = bean.get("depotId").toString();
			String departMentId = bean.get("departMentId").toString();
			String materialId = bean.get("materialId").toString();
			String normsId = bean.get("normsId").toString();
			String needNumber = bean.get("needNumber").toString();
			if(ORDER_REQUISITION_TYPE.equals(orderType)){
				// 领料单
				// 1.出库
				erpCommonService.editMaterialNormsDepotStock(depotId, materialId, normsId, needNumber, 2);
				// 2.修改部门库存存量-入库
				updateDepartMentStock(departMentId, materialId, normsId, needNumber, 1);
			}else if(ORDER_PATCH_TYPE.equals(orderType)){
				// 补料单
				// 1.出库
				erpCommonService.editMaterialNormsDepotStock(depotId, materialId, normsId, needNumber, 2);
				// 2.修改部门库存存量-入库
				updateDepartMentStock(departMentId, materialId, normsId, needNumber, 1);
			}else if(ORDER_RETURN_TYPE.equals(orderType)){
				// 退料单
				// 1.入库
				erpCommonService.editMaterialNormsDepotStock(depotId, materialId, normsId, needNumber, 1);
				// 2.修改部门库存存量-出库
				updateDepartMentStock(departMentId, materialId, normsId, needNumber, 2);
			}
		}
	}
	
	/**
	 * @throws Exception 
	 * 
	    * @Title: updateDepartMentStock
	    * @Description: 修改部门库存存量信息
	    * @param departMentId 部门id
	    * @param materialId 商品id
	    * @param normsId 规格id
	    * @param operNumber 变化数量
	    * @param type 1.入库  2.出库
	    * @throws Exception    参数
	    * @return void    返回类型
	    * @throws
	 */
	@Override
	public synchronized void updateDepartMentStock(String departMentId, String materialId, String normsId, String operNumber, int type) throws Exception{
		// 变化的数量
		int changeNumber = Integer.parseInt(operNumber);
		Map<String, Object> stock = erpPickDao.queryDepartMentStockByDepartIdAndNormsId(departMentId, normsId);
		// 如果该规格在指定部门中已经有存储数据，则直接做修改
		if(stock != null && !stock.isEmpty()){
			int stockNum = Integer.parseInt(stock.get("stockNum").toString());
			if(type == 1){
				// 入库
				stockNum = stockNum + changeNumber;
			}else if(type == 2){
				// 出库
				stockNum = stockNum - changeNumber;
			}else{
				throw new Exception("状态错误");
			}
			erpPickDao.editDepartMentStockByDepotIdAndNormsId(departMentId, normsId, stockNum);
		}else{
			int stockNum = 0;
			if(type == 1){
				// 入库
				stockNum = stockNum + changeNumber;
			}else if(type == 2){
				// 出库
				stockNum = stockNum - changeNumber;
			}else{
				throw new Exception("状态错误");
			}
			erpPickDao.insertDepartMentStockByDepotIdAndNormsId(materialId, departMentId, normsId, stockNum);
		}
	}

	/**
	 * 
	    * @Title: checkStockWhetherOutstrip
	    * @Description: 判断库存是否充足
	    * @param thisOrderNum 库存的数量列表
	    * @param @return    参数
	    * @return boolean true 满足，false 不满足
	    * @throws
	 */
	private boolean checkStockWhetherOutstrip(List<Map<String, Object>> thisOrderNum){
		// 遍历单据的规格
		for(Map<String, Object> bean : thisOrderNum) {
			// 库存剩余数量
			int currentTock = Integer.parseInt(bean.get("currentTock").toString());
			// 单据数量
			int needNumber = Integer.parseInt(bean.get("needNumber").toString());
			if(currentTock - needNumber < 0){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 
	    * @Title: checkDepartStockWhetherOutstrip
	    * @Description: 判断部门存量是否充足
	    * @param thisOrderNum 库存的数量列表
	    * @param @return    参数
	    * @return boolean true 满足，false 不满足
	    * @throws
	 */
	private boolean checkDepartStockWhetherOutstrip(List<Map<String, Object>> thisOrderNum){
		// 遍历单据的规格
		for(Map<String, Object> bean : thisOrderNum) {
			// 部门库存存量
			int departMentTock = Integer.parseInt(bean.get("departMentTock").toString());
			// 单据数量
			int needNumber = Integer.parseInt(bean.get("needNumber").toString());
			if(departMentTock - needNumber < 0){
				return false;
			}
		}
		return true;
	}
	
}
