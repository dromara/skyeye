/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 */

package com.skyeye.service.impl;

import cn.hutool.json.JSONUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.dao.ErpBomDao;
import com.skyeye.dao.MaterialDao;
import com.skyeye.service.ErpBomService;
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
 * @ClassName: ErpBomServiceImpl
 * @Description: bom表管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:47
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class ErpBomServiceImpl implements ErpBomService{
	
	@Autowired
	private ErpBomDao erpBomDao;
	
	@Autowired
	private MaterialDao materialDao;

	/**
     * 查询bom表列表
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void queryErpBomList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(params.get("page").toString()), Integer.parseInt(params.get("limit").toString()));
        List<Map<String, Object>> beans = erpBomDao.queryErpBomList(params);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
     * 新增bom表
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(value="transactionManager")
	public void insertErpBomMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> titleInSQL = erpBomDao.queryBomTitleInSQLByTitle(map.get("bomTitle").toString());
		if(titleInSQL != null && !titleInSQL.isEmpty()){
			outputObject.setreturnMessage("该方案名称已存在，请更换。");
			return;
		}
		String childStr = map.get("childStr").toString();
		if(ToolUtil.isJson(childStr)){
			String parentId = ToolUtil.getSurFaceId();//bom表id
			//处理数据
			List<Map<String, Object>> jArray = JSONUtil.toList(childStr, null);
			//子件清单
			Map<String, Object> bean, produce;
			List<Map<String, Object>> beans = new ArrayList<>();
			List<Map<String, Object>> childProcedure = new ArrayList<>();
			BigDecimal consumablesPrice = new BigDecimal("0");//耗材总费用
			BigDecimal procedurePrice = new BigDecimal("0");//工序总费用
			BigDecimal wastagePrice = new BigDecimal("0");//耗损总费用
			//子件对象
			BigDecimal childConsumablesPrice = null;//耗材总费用
			BigDecimal childProcedurePrice = null;//工序总费用
			BigDecimal childWastagePrice = null;//耗损总费用
			for(int i = 0; i < jArray.size(); i++){
				bean = jArray.get(i);
				//子件id
				String childId = ToolUtil.getSurFaceId();
				bean.put("id", childId);
				bean.put("bomId", parentId);
				
				//单个子件耗材总费用  单价*所需要的数量
				childConsumablesPrice = new BigDecimal(bean.get("unitPrice").toString())
						.multiply(new BigDecimal(bean.get("needNum").toString()));
				bean.put("consumablesPrice", childConsumablesPrice);
				
				//子件耗损总费用
				childWastagePrice = new BigDecimal(bean.get("wastagePrice").toString());
				bean.put("childWastagePrice", childWastagePrice);
				
				//---子件工序总费用
				childProcedurePrice = new BigDecimal("0");
				//处理工序信息
				String childProcedureStr = bean.get("procedureMationList").toString();
				List<Map<String, Object>> childProcedureList = JSONUtil.toList(childProcedureStr, null);
				for(int j = 0; j < childProcedureList.size(); j++){
					produce = childProcedureList.get(j);
					produce.put("childId", childId);
					childProcedure.add(produce);
					
					//单个子件耗材总费用  单价*所需要的数量
					childProcedurePrice = childProcedurePrice.add(new BigDecimal(produce.get("unitPrice").toString())
											.multiply(new BigDecimal(bean.get("needNum").toString())));
				}
				//---子件工序总费用
				bean.put("procedurePrice", childProcedurePrice);
				
				//子件总费用
				bean.put("allPrice", childProcedurePrice.add(childWastagePrice).add(childConsumablesPrice));
				beans.add(bean);
				
				//父件相关费用
				//耗材总费用
				consumablesPrice = consumablesPrice.add(childConsumablesPrice);
				//工序总费用
				procedurePrice = procedurePrice.add(childProcedurePrice);
				//耗损总费用
				wastagePrice = wastagePrice.add(childWastagePrice);
			}
			if(beans.size() == 0){
				outputObject.setreturnMessage("请选择子件清单");
				return;
			}
			//父件对象
			Map<String, Object> bomMation = new HashMap<>();
			bomMation.put("id", parentId);
			bomMation.put("title", map.get("bomTitle"));
			bomMation.put("materialId", map.get("materialId"));
			bomMation.put("normsId", map.get("normsId"));
			bomMation.put("remark", map.get("remark"));
			bomMation.put("consumablesPrice", consumablesPrice);
			bomMation.put("procedurePrice", procedurePrice);
			bomMation.put("wastagePrice", wastagePrice);
			bomMation.put("sealPrice", map.get("sealPrice"));
			bomMation.put("createId", inputObject.getLogParams().get("id"));
			bomMation.put("createTime", DateUtil.getTimeAndToString());
			//插入bom父件信息
			erpBomDao.insertErpBomParentMation(bomMation);
			//插入bom子件信息
			if(!beans.isEmpty()){
				erpBomDao.insertErpBomChildMation(beans);
			}
			//插入bom子件工序信息
			if(!childProcedure.isEmpty()){
				erpBomDao.insertErpBomChildProcedureMation(childProcedure);
			}
		}else{
			outputObject.setreturnMessage("数据格式错误");
		}
	}

	/**
     * 查询bom表详情
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void queryErpBomDetailById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String bomId = map.get("id").toString();
		Map<String, Object> bomParent = erpBomDao.queryBomParentById(bomId);
		if(bomParent != null && !bomParent.isEmpty()){
			// 子件清单
			List<Map<String, Object>> bomMaterialList = materialDao.queryBomMaterialListByBomId(bomId, bomParent.get("productId").toString());
			for(Map<String, Object> bemPro: bomMaterialList){
				// 工序信息
				bemPro.put("procedureMationList", materialDao.queryMaterialProcedureMationDetailsById(bemPro.get("productId").toString()));
			}
			bomParent.put("bomMaterialList", bomMaterialList);
			outputObject.setBean(bomParent);
			outputObject.settotal(1);
		}else{
			outputObject.setreturnMessage("该bom表信息不存在。");
		}
	}

	/**
     * 删除bom表信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	@Transactional(value="transactionManager")
	public void deleteErpBomMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String bomId = map.get("id").toString();
		Map<String, Object> bomParent = erpBomDao.queryBomParentById(bomId);
		if(bomParent != null && !bomParent.isEmpty()){
			// 删除父件表信息
			erpBomDao.deleteErpBomParentMationById(bomId);
			// 删除子件表信息
			erpBomDao.deleteErpBomChildMationById(bomId);
		}else{
			outputObject.setreturnMessage("该bom表信息不存在。");
		}
	}

	/**
     * 查询bom表信息用作编辑
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void queryErpBomMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String bomId = map.get("id").toString();
		Map<String, Object> bomParent = erpBomDao.queryErpBomMationToEditById(bomId);
		if(bomParent != null && !bomParent.isEmpty()){
			// 1.父件资料
			String productId = bomParent.get("productId").toString();
			// 1.1规格信息
			List<Map<String, Object>> unitList = materialDao.queryMaterialUnitByIdToSelect(productId);
			if ("1".equals(bomParent.get("unit").toString())) {// 不是多单位
				unitList.get(0).put("name", bomParent.get("unitName").toString());
			}
			bomParent.put("unitList", unitList);
			
			// 1.2工序信息
			bomParent.put("procedureMationList", materialDao.queryMaterialProcedureMationDetailsById(productId));
			
			// 2.子件清单
			List<Map<String, Object>> bomMaterialList = materialDao.queryBomMaterialListByBomId(bomId, productId);
			for(Map<String, Object> bemPro: bomMaterialList){
				String childId = bemPro.get("childId").toString();
				// 工序信息
				bemPro.put("procedureMationList", erpBomDao.queryBomChildProcedureListByChildId(childId));
			}
			bomParent.put("bomMaterialList", bomMaterialList);
			outputObject.setBean(bomParent);
			outputObject.settotal(1);
		}else{
			outputObject.setreturnMessage("该bom表信息不存在。");
		}
	}

	/**
     * 编辑bom表信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	@Transactional(value="transactionManager")
	public void editErpBomMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> titleInSQL = erpBomDao.queryBomTitleInSQLByIdAndTitle(map.get("id").toString(), map.get("bomTitle").toString());
		if(titleInSQL != null && !titleInSQL.isEmpty()){
			outputObject.setreturnMessage("该方案名称已存在，请更换。");
			return;
		}
		String childStr = map.get("childStr").toString();
		if(ToolUtil.isJson(childStr)){
			String parentId = map.get("id").toString();//bom表id
			// 处理数据
			List<Map<String, Object>> jArray = JSONUtil.toList(childStr, null);
			// 子件清单
			List<Map<String, Object>> beans = new ArrayList<>();
			List<Map<String, Object>> childProcedure = new ArrayList<>();
			BigDecimal consumablesPrice = new BigDecimal("0");//耗材总费用
			BigDecimal procedurePrice = new BigDecimal("0");//工序总费用
			BigDecimal wastagePrice = new BigDecimal("0");//耗损总费用
			//子件对象
			BigDecimal childConsumablesPrice = null;//耗材总费用
			BigDecimal childProcedurePrice = null;//工序总费用
			BigDecimal childWastagePrice = null;//耗损总费用
			for(int i = 0; i < jArray.size(); i++){
				Map<String, Object> bean = jArray.get(i);
				// 子件id
				String childId = ToolUtil.getSurFaceId();
				bean.put("id", childId);
				bean.put("bomId", parentId);
				
				// 单个子件耗材总费用  单价*所需要的数量
				childConsumablesPrice = new BigDecimal(bean.get("unitPrice").toString())
						.multiply(new BigDecimal(bean.get("needNum").toString()));
				bean.put("consumablesPrice", childConsumablesPrice);
				
				// 子件耗损总费用
				childWastagePrice = new BigDecimal(bean.get("wastagePrice").toString());
				bean.put("childWastagePrice", childWastagePrice);
				
				// ---子件工序总费用
				childProcedurePrice = new BigDecimal("0");
				// 处理工序信息
				String childProcedureStr = bean.get("procedureMationList").toString();
				List<Map<String, Object>> childProcedureList = JSONUtil.toList(childProcedureStr, null);
				for(int j = 0; j < childProcedureList.size(); j++){
					Map<String, Object> produce = childProcedureList.get(j);
					produce.put("childId", childId);
					childProcedure.add(produce);
					
					// 单个子件耗材总费用  单价*所需要的数量
					childProcedurePrice = childProcedurePrice.add(new BigDecimal(produce.get("unitPrice").toString())
											.multiply(new BigDecimal(bean.get("needNum").toString())));
				}
				// ---子件工序总费用
				bean.put("procedurePrice", childProcedurePrice);
				
				//子件总费用
				bean.put("allPrice", childProcedurePrice.add(childWastagePrice).add(childConsumablesPrice));
				beans.add(bean);
				
				// 父件相关费用
				// 耗材总费用
				consumablesPrice = consumablesPrice.add(childConsumablesPrice);
				// 工序总费用
				procedurePrice = procedurePrice.add(childProcedurePrice);
				// 耗损总费用
				wastagePrice = wastagePrice.add(childWastagePrice);
			}
			if(beans.size() == 0){
				outputObject.setreturnMessage("请选择子件清单");
				return;
			}
			//父件对象
			Map<String, Object> bomMation = new HashMap<>();
			bomMation.put("id", parentId);
			bomMation.put("title", map.get("bomTitle"));
			bomMation.put("materialId", map.get("materialId"));
			bomMation.put("normsId", map.get("normsId"));
			bomMation.put("remark", map.get("remark"));
			bomMation.put("consumablesPrice", consumablesPrice);
			bomMation.put("procedurePrice", procedurePrice);
			bomMation.put("wastagePrice", wastagePrice);
			bomMation.put("sealPrice", map.get("sealPrice"));
			// 编辑bom父件信息
			erpBomDao.editErpBomParentMation(bomMation);
			// 删除子件信息以及子件关联工序信息
			erpBomDao.deleteErpBomChildMationByBomId(parentId);
			// 插入bom子件信息
			if(!beans.isEmpty()){
				erpBomDao.insertErpBomChildMation(beans);
			}
			// 插入bom子件工序信息
			if(!childProcedure.isEmpty()){
				erpBomDao.insertErpBomChildProcedureMation(childProcedure);
			}
		}else{
			outputObject.setreturnMessage("数据格式错误");
		}
	}

	/**
     * 根据规格id获取方案列表
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void queryErpBomListByNormsId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String normsId = map.get("normsId").toString();
		List<Map<String, Object>> beans = erpBomDao.queryErpBomListByNormsId(normsId);
		outputObject.setBeans(beans);
	}

	/**
     * 根据方案id获取bom表子件列表
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void queryErpBomChildProListByBomId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String bomId = map.get("bomId").toString();
		//子件清单
		List<Map<String, Object>> bomMaterialList = materialDao.queryErpBomChildProListByBomId(bomId);
		//设置指定规格默认的待入库数量
		for(Map<String, Object> bean: bomMaterialList){
			bean.put("beWarehousedNum", 0);
			//工序信息
			bean.put("procedureMationList", materialDao.queryMaterialProcedureMationDetailsById(bean.get("productId").toString()));
		}
		outputObject.setBeans(bomMaterialList);
	}
	
}
