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
import com.skyeye.dao.ErpBomDao;
import com.skyeye.dao.ErpProductionDao;
import com.skyeye.dao.MaterialDao;
import com.skyeye.jedis.JedisClientService;
import com.skyeye.service.ErpCommonService;
import com.skyeye.service.ErpProductionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: ErpProductionServiceImpl
 * @Description: 生产计划单管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:48
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class ErpProductionServiceImpl implements ErpProductionService{
	
	@Autowired
	private ErpProductionDao erpProductionDao;
	
	@Autowired
	private ErpBomDao erpBomDao;
	
	@Autowired
	private MaterialDao materialDao;
	
	@Autowired
	private ErpCommonService erpCommonService;
	
	@Autowired
	private JedisClientService jedisClientService;

	/**
	 * 生产计划单类型
	 */
	private static final String ORDER_TYPE = ErpConstants.DepoTheadSubType.PRODUCTION_HEAD.getType();
	
	/**
     * 查询生产计划单列表
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void queryErpProductionList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(params.get("page").toString()), Integer.parseInt(params.get("limit").toString()));
        List<Map<String, Object>> beans = erpProductionDao.queryErpProductionList(params);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
     * 新增生产计划单
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	@Transactional(value="transactionManager")
	public void insertErpProductionMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String childProStr = map.get("childProStr").toString();//子件清单列表
		String procedureJsonStr = map.get("procedureJsonStr").toString();//工序列表
		//计划单id
		String productionId = ToolUtil.getSurFaceId();
		//子件清单信息
		List<Map<String, Object>> childProList = JSONUtil.toList(childProStr, null);
		if(childProList.isEmpty()){
			outputObject.setreturnMessage("子件清单不能为空");
			return;
		}
		childProList.stream().forEach(bean -> {
			bean.put("orderId", productionId);
			bean.put("id", ToolUtil.getSurFaceId());
		});
		//工序信息
		List<Map<String, Object>> procedureList = JSONUtil.toList(procedureJsonStr, null);
		if(procedureList.isEmpty()){
			outputObject.setreturnMessage("工序信息不能为空");
			return;
		}
		procedureList.stream().forEach(bean -> {
			bean.put("orderId", productionId);
		});
		//计划单信息
		Map<String, Object> production = new HashMap<>();
		production.put("id", productionId);
		//单据所属父id，目前只允许添加一级，不允许树状单据
		production.put("parentId", "0");
		String orderNum = ErpConstants.DepoTheadSubType.getOrderNumBySubType(ORDER_TYPE);
		production.put("defaultNumber", orderNum);
		production.put("orderId", map.get("orderId"));
		production.put("materialId", map.get("materialId"));
		production.put("normsId", map.get("normsId"));
		production.put("number", map.get("number"));
		production.put("planStartDate", map.get("planStartDate"));
		production.put("planComplateDate", map.get("planComplateDate"));
		production.put("operTime", map.get("operTime"));
		production.put("remark", map.get("remark"));
		production.put("wayProcedureId", map.get("wayProcedureId"));
		production.put("bomId", map.get("bomId"));
		production.put("createId", inputObject.getLogParams().get("id"));
		production.put("createTime", DateUtil.getTimeAndToString());
		//1.新建  2.审核中  3.审核通过  4.审核不通过  5.生产完成
		production.put("state", 1);
		//插入计划单信息
		erpProductionDao.insertErpProductionMation(production);
		//插入子件清单信息
		erpProductionDao.insertErpProductionChildMation(childProList);
		//插入工序信息
		erpProductionDao.insertErpProductionProcedureMation(procedureList);
	}

	/**
     * 查询生产计划单信息用于编辑
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void queryErpProductionMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String orderId = map.get("id").toString();
		Map<String, Object> productionMation = erpProductionDao.queryErpProductionMationToEditById(orderId);
		if(productionMation != null && !productionMation.isEmpty()){
			String productId = productionMation.get("productId").toString();
			String normsId = productionMation.get("normsId").toString();
			//1.bom方案列表
			List<Map<String, Object>> beans = erpBomDao.queryErpBomListByNormsId(normsId);
			productionMation.put("bomList", beans);
			//2.规格信息
			List<Map<String, Object>> unitList = materialDao.queryMaterialUnitByIdToSelect(productId);
			if ("1".equals(productionMation.get("unit").toString())) {// 不是多单位
				unitList.get(0).put("name", productionMation.get("unitName").toString());
			}
			if(!ToolUtil.isBlank(productionMation.get("orderId").toString())){
				//如果销售单id不为空,只取销售单中指定的规格id
				Map<String, Object> unit = unitList.stream().filter(bean -> bean.get("id").toString().equals(normsId)).findFirst().get();
				unitList.clear();
				unitList.add(unit);
			}
			productionMation.put("unitList", unitList);
			//3.子件清单列表
			List<Map<String, Object>> childList = erpProductionDao.queryErpProductionChildMationToEditByOrderId(orderId);
			//设置指定规格默认的待入库数量
			for(Map<String, Object> bean: childList){
				bean.put("beWarehousedNum", 0);
				//工序信息
				bean.put("procedureMationList", materialDao.queryMaterialProcedureMationDetailsById(bean.get("productId").toString()));
			}
			productionMation.put("childList", childList);
			//4.工序列表信息
			productionMation.put("procedureMationList", erpProductionDao.queryErpProductionProcedureMationDetailsById(orderId));
			
			outputObject.setBean(productionMation);
		}else{
			outputObject.setreturnMessage("该单据信息不存在.");
		}
	}

	/**
     * 编辑生产计划单信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(value="transactionManager")
	public void editErpProductionMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String childProStr = map.get("childProStr").toString();//子件清单列表
		String procedureJsonStr = map.get("procedureJsonStr").toString();//工序列表
		//计划单id
		String productionId = map.get("id").toString();
		//子件清单信息
		List<Map<String, Object>> childProList = JSONUtil.toList(childProStr, null);
		if(childProList.isEmpty()){
			outputObject.setreturnMessage("子件清单不能为空");
			return;
		}
		childProList.stream().forEach(bean -> {
			bean.put("orderId", productionId);
			bean.put("id", ToolUtil.getSurFaceId());
		});
		//工序信息
		List<Map<String, Object>> procedureList = JSONUtil.toList(procedureJsonStr, null);
		if(procedureList.isEmpty()){
			outputObject.setreturnMessage("工序信息不能为空");
			return;
		}
		procedureList.stream().forEach(bean -> {
			bean.put("orderId", productionId);
		});
		//计划单信息
		Map<String, Object> production = new HashMap<>();
		production.put("id", productionId);
		production.put("orderId", map.get("orderId"));
		production.put("materialId", map.get("materialId"));
		production.put("normsId", map.get("normsId"));
		production.put("number", map.get("number"));
		production.put("planStartDate", map.get("planStartDate"));
		production.put("planComplateDate", map.get("planComplateDate"));
		production.put("operTime", map.get("operTime"));
		production.put("remark", map.get("remark"));
		production.put("wayProcedureId", map.get("wayProcedureId"));
		production.put("bomId", map.get("bomId"));
		//编辑计划单信息
		int num = erpProductionDao.editErpProductionMationById(production);
		if(num > 0){
			//删除子件清单信息
			erpProductionDao.deleteErpProductionChildMation(productionId);
			//插入子件清单信息
			erpProductionDao.insertErpProductionChildMation(childProList);
			//删除工序信息
			erpProductionDao.deleteErpProductionProcedureMation(productionId);
			//插入工序信息
			erpProductionDao.insertErpProductionProcedureMation(procedureList);
		}
	}

	/**
     * 删除生产计划单信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	@Transactional(value="transactionManager")
	public void deleteErpProductionMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String orderId = map.get("id").toString();
		Map<String, Object> bean = erpProductionDao.queryErpPruductionStateById(orderId);
		if(bean != null && !bean.isEmpty()){
			if("1".equals(bean.get("state").toString()) || "4".equals(bean.get("state").toString())){
				//新建或审核不通过可以删除
				erpProductionDao.deleteErpProductionMationById(orderId);
			}
		}else{
			outputObject.setreturnMessage("该数据不存在.");
		}
	}

	/**
     * 查询生产计划单信息详情
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@SuppressWarnings("unchecked")
	@Override
	public void queryErpProductionMationToDetailById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String orderId = map.get("id").toString();
		String productionDetailsKey = ErpConstants.getSysOrderDetailsCacheKey(orderId);
		if(!jedisClientService.exists(productionDetailsKey)){
			Map<String, Object> productionMation = erpProductionDao.queryErpProductionMationToDetailById(orderId);
			if(productionMation != null && !productionMation.isEmpty()){
				// 1.子件清单列表
				List<Map<String, Object>> childList = erpProductionDao.queryErpProductionChildMationToDetailByOrderId(orderId);
				// 设置指定规格默认的待入库数量
				for(Map<String, Object> bean: childList){
					bean.put("beWarehousedNum", 0);
					// 工序信息
					bean.put("procedureMationList", materialDao.queryMaterialProcedureMationDetailsById(bean.get("productId").toString()));
				}
				productionMation.put("childList", childList);
				// 2.工序列表信息
				productionMation.put("procedureMationList", erpProductionDao.queryErpProductionProcedureMationDetailsById(orderId));
				// 3.如果是已完成状态，则放入缓存
				if("5".equals(productionMation.get("state").toString())){
					jedisClientService.set(productionDetailsKey, JSONUtil.toJsonStr(productionMation));
				}
				outputObject.setBean(productionMation);
			}else{
				outputObject.setreturnMessage("该单据信息不存在.");
			}
		}else{
			Map<String, Object> productionMation = JSONUtil.toBean(jedisClientService.get(productionDetailsKey), null);
			outputObject.setBean(productionMation);
		}
	}

	/**
     * 提交审批申请
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	@Transactional(value="transactionManager")
	public void submitApplicationForApproval(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String orderId = map.get("id").toString();
		Map<String, Object> bean = erpProductionDao.queryErpPruductionStateById(orderId);
		if(bean != null && !bean.isEmpty()){
			if("1".equals(bean.get("state").toString()) || "4".equals(bean.get("state").toString())){
				//新建或审核不通过可以提交申请
				String state = "2";//修改为审核中
				erpProductionDao.editErpPruductionStateById(orderId, state);
			}
		}else{
			outputObject.setreturnMessage("该数据不存在.");
		}
	}

	/**
     * 生产计划单信息审核
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void editErpProductionStateToExamineById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String orderId = map.get("id").toString();
		//获取销售单状态
		Map<String, Object> bean = erpProductionDao.queryErpPruductionStateById(orderId);
		if(bean != null && !bean.isEmpty()){
			if("2".equals(bean.get("state").toString())){
				//审核中的可以进行审核
				String state = map.get("state").toString();
				if("3".equals(state) || "4".equals(state)){
					//审批信息
					map.put("approvalId", inputObject.getLogParams().get("id"));
					map.put("approvalTime", DateUtil.getTimeAndToString());
					erpProductionDao.editErpProductionStateToExamineById(map);
				}else{
					outputObject.setreturnMessage("参数错误.");
					return;
				}
			}
		}else{
			outputObject.setreturnMessage("该数据不存在.");
		}
	}
	
	/**
     * 查询生产计划单列表展示为表格供采购单,加工单选择
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void queryErpProductionListToTable(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(params.get("page").toString()), Integer.parseInt(params.get("limit").toString()));
        List<Map<String, Object>> beans = erpProductionDao.queryErpProductionListToTable(params);
        beans.stream().forEach(bean -> {
			try {
				//1.规格信息
				List<Map<String, Object>> unitList = materialDao.queryMaterialUnitByIdToSelect(bean.get("materialId").toString());
				if ("1".equals(bean.get("unit").toString())) {// 不是多单位
					unitList.get(0).put("name", bean.get("unitName").toString());
				}
				bean.put("unitList", unitList);
				//2.工序列表信息
				bean.put("procedureMationList", erpProductionDao.queryErpProductionProcedureMationDetailsById(bean.get("id").toString()));
			} catch (Exception e) {
				outputObject.setreturnMessage("系统异常.");
			}
        });
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
     * 根据生产计划单id获取该单据下的所有外购商品以及剩余数量
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void queryErpProductionOutsideProListByOrderId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
		String orderId = params.get("id").toString();
		// 回显单据商品的类型：1.外购；2.自产+外购
		// 1.采购订单需要回显外购一种类型的商品
		// 2.加工单需要回显自产和外购两种类型的商品
		String chooseType = params.get("chooseType").toString();
		// 商品来源类型  1.自产  2.外购
		String type = "";
		if("1".equals(chooseType)){
			type = "2";
		}else if("2".equals(chooseType)){
			type = "1,2";
		}
		// 根据订单id获取该订单下的所有商品规格信息,包含外购和自产
		List<Map<String, Object>> norms = erpProductionDao.queryErpProductionOutsideProListByOrderId(orderId, type);
		for(Map<String, Object> norm : norms){
			erpCommonService.resetProductAndUnitToEditShow(norm, materialDao.queryMaterialUnitByIdToSelect(norm.get("materialId").toString()));
		}
		outputObject.setBeans(norms);
	}
	
}
