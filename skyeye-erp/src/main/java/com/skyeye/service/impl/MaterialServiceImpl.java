/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service.impl;

import cn.hutool.json.JSONUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.dao.MaterialDao;
import com.skyeye.eve.dao.SysEnclosureDao;
import com.skyeye.service.MaterialService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 *
 * @ClassName: MaterialServiceImpl
 * @Description: 产品信息管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:44
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class MaterialServiceImpl implements MaterialService{

	private static final Logger LOGGER = LoggerFactory.getLogger(MaterialServiceImpl.class);
	
	@Autowired
	private MaterialDao materialDao;
	
	@Autowired
	private SysEnclosureDao sysEnclosureDao;

	/**
     * 获取产品信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void queryMaterialList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(params.get("page").toString()), Integer.parseInt(params.get("limit").toString()));
        List<Map<String, Object>> beans = materialDao.queryMaterialList(params);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 构建产品规格信息
	 * @param bean
	 * @return
	 */
	private Map<String, Object> getMaterialNormMation(Map<String, Object> bean){
		Map<String, Object> entity = new HashMap<>();
		//设置价格的一些参数
		setCommonPrice(entity, bean);
		entity.put("deleteFlag", 0);//默认状态  删除标记，0未删除，1删除
		entity.put("createTime", DateUtil.getTimeAndToString());//创建时间
		return entity;
	}
	
	/**
	 * 设置价格的一些参数
	 * @param entity 即将赋值的对象
	 * @param bean 参考对象(源对象)
	 */
	private void setCommonPrice(Map<String, Object> entity, Map<String, Object> bean){
		entity.put("safetyTock", bean.get("safetyTock"));//安全存量
		entity.put("retailPrice", bean.get("retailPrice"));//零售价
		entity.put("lowPrice", bean.get("lowPrice"));//最低售价
		entity.put("estimatePurchasePrice", bean.get("estimatePurchasePrice"));//预计采购价
		entity.put("salePrice", bean.get("salePrice"));//销售价
	}
	
	/**
	 * 解析规格初始化库存信息
	 * @param bean
	 * @param normsId
	 * @return
	 */
	private List<Map<String, Object>> getNormsStock(Map<String, Object> bean, String materialId, String normsId){
		List<Map<String, Object>> beans = new ArrayList<>();
		String normsStockStr = bean.get("normsStock").toString();
		if(!ToolUtil.isBlank(normsStockStr) && ToolUtil.isJson(normsStockStr)){
			List<Map<String, Object>> jArray = JSONUtil.toList(normsStockStr, null);
			for(int i = 0; i < jArray.size(); i++){
				Map<String, Object> jObject = jArray.get(i);
				Map<String, Object> item = new HashMap<>();
				item.put("depotId", jObject.get("depotId"));
				item.put("materialId", materialId);
				item.put("normsId", normsId);
				item.put("initialTock", jObject.get("initialTock"));
				beans.add(item);
			}
		}
		return beans;
	}
	
	/**
	 * 处理扩展信息
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> getExtendData(String extendData, String materialId){
		List<Map<String, Object>> jArray = JSONUtil.toList(extendData, null);
		//扩展信息集合对象
		List<Map<String, Object>> beans = new ArrayList<>();
		//扩展信息实体对象
		for(int i = 0; i < jArray.size(); i++){
			Map<String, Object> bean = jArray.get(i);
			bean.put("id", ToolUtil.getSurFaceId());
			bean.put("materialId", materialId);
			beans.add(bean);
		}
		return beans;
	}
	
	/**
	 * 处理工序信息
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> getProcedureExtendData(String extendData, String materialId){
		List<Map<String, Object>> jArray = JSONUtil.toList(extendData, null);
		//工序信息集合对象
		List<Map<String, Object>> beans = new ArrayList<>();
		//工序信息实体对象
		for(int i = 0; i < jArray.size(); i++){
			Map<String, Object> bean = jArray.get(i);
			bean.put("materialId", materialId);
			beans.add(bean);
		}
		return beans;
	}

	/**
     * 新增产品信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(value="transactionManager")
	public void insertMaterialMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String materialNormsStr = map.get("materialNormsStr").toString();
		if(ToolUtil.isJson(materialNormsStr)){
			Map<String, Object> inSql = materialDao.queryMaterialByNameAndModel(map);
			if(inSql != null && !inSql.isEmpty()){
				outputObject.setreturnMessage("同种型号的产品已经存在.");
				return;
			}
			//产品对象
			Map<String, Object> material = new HashMap<>();
			//产品id
			String materialId = ToolUtil.getSurFaceId();
			//处理数据
			List<Map<String, Object>> jArray = JSONUtil.toList(materialNormsStr, null);
			//产品单位，是多个还是单个,  1.单个  2.多个
			int unit = Integer.parseInt(map.get("unit").toString());
			//计量单位名称  当unit=1时，必填，当不是多单位时，必填，定义在这方便下面产品信息使用
			String unitName = "";
			//计量单位组id  当unit=2时，必填
			String unitGroupId = "";
			//首选出库单位，首选入库单位
			String firstOutUnit = "", firstInUnit = "";
			//配件规格仓库初始库存
			List<Map<String, Object>> normStock = new ArrayList<>();
			if(unit == 1){//非多单位
				if(jArray.isEmpty()){
					outputObject.setreturnMessage("产品规格信息不能为空.");
					return;
				}
				Map<String, Object> bean = jArray.get(0);
				unitName = bean.get("unitName").toString();
				if(ToolUtil.isBlank(unitName)){
					outputObject.setreturnMessage("计量单位不能为空.");
					return;
				}
				Map<String, Object> entity = getMaterialNormMation(bean);
				//规格id
				String normsId = ToolUtil.getSurFaceId();
				entity.put("id", normsId);
				entity.put("materialId", materialId);//产品id
				materialDao.insertMaterialNorms(entity);
				//解析初始库存信息
				normStock.addAll(getNormsStock(bean, materialId, normsId));
			}else if(unit == 2){//多单位
				unitGroupId = map.get("unitGroupId").toString();
				firstOutUnit = map.get("firstOutUnit").toString();
				firstInUnit = map.get("firstInUnit").toString();
				if(ToolUtil.isBlank(unitGroupId)){
					outputObject.setreturnMessage("请选择单位.");
					return;
				}
				if(ToolUtil.isBlank(firstOutUnit)){
					outputObject.setreturnMessage("请选择首选出库单位.");
					return;
				}
				if(ToolUtil.isBlank(firstInUnit)){
					outputObject.setreturnMessage("请选择首选入库单位.");
					return;
				}
				//产品规格集合对象
				List<Map<String, Object>> materialNorms = new ArrayList<>();
				//产品规格实体对象，jsonArray实体对象
				for(int i = 0; i < jArray.size(); i++){
					Map<String, Object> bean = jArray.get(i);
					Map<String, Object> entity = materialDao.queryMaterialUnitByUnitId(bean);
					if(entity != null && !entity.isEmpty()){
						entity = getMaterialNormMation(bean);
						String normsId = ToolUtil.getSurFaceId();
						entity.put("id", normsId);
						entity.put("unitId", bean.get("unitId"));//计量单位id
						entity.put("materialId", materialId);//产品id
						materialNorms.add(entity);
						//解析初始库存信息
						normStock.addAll(getNormsStock(bean, materialId, normsId));
					}
				}
				if(materialNorms.isEmpty()){
					outputObject.setreturnMessage("产品规格信息不能为空.");
					return;
				}
				materialDao.insertMaterialNormsList(materialNorms);
			}else{
				outputObject.setreturnMessage("参数错误.");
				return;
			}
			
			material.put("id", materialId);
			material.put("materialName", map.get("materialName"));//产品名称
			material.put("categoryId", map.get("categoryId"));//分类id
			material.put("model", map.get("model"));//型号
			material.put("remark", map.get("remark"));//备注
			material.put("unit", unit);//产品单位，是多个还是单个,  1.单个  2.多个
			material.put("unitName", unitName);//计量单位  当unit=1时，必填
			material.put("unitGroupId", unitGroupId);//计量单位组id  当unit=2时，必填
			material.put("firstInUnit", firstInUnit);//首选入库单位
			material.put("firstOutUnit", firstOutUnit);//首选出库单位
			material.put("enabled", 1);//默认状态  启用 0-禁用  1-启用
			material.put("type", map.get("type"));//商品来源类型  1.自产  2.外购
			material.put("deleteFlag", 0);//默认状态  删除标记，0未删除，1删除
			material.put("createTime", DateUtil.getTimeAndToString());//创建时间
			material.put("enclosureInfo", map.get("enclosureInfo"));//附件
			materialDao.insertMaterialMation(material);
			//存储初始化库存数量
			if(!normStock.isEmpty())
				materialDao.insertNormsStockMation(normStock);
			
			//处理扩展信息
			String extendData = map.get("extendData").toString();
			if(ToolUtil.isJson(extendData)){
				//处理数据
				List<Map<String, Object>> extendBeans = getExtendData(extendData, materialId);
				if(!extendBeans.isEmpty()){
					materialDao.insertExtendsMation(extendBeans);
				}
			}
			
			//处理商品的工序信息
			String procedureJsonStr = map.get("procedureJsonStr").toString();
			if(ToolUtil.isJson(procedureJsonStr)){
				//处理数据
				List<Map<String, Object>> procedureBeans = getProcedureExtendData(procedureJsonStr, materialId);
				if(!procedureBeans.isEmpty()){
					materialDao.insertMaterialProcedureMation(procedureBeans);
				}
			}
		} else {
			outputObject.setreturnMessage("数据格式错误");
		}
	}
	
	/**
     * 禁用产品信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	@Transactional(value="transactionManager")
	public void editMaterialEnabledToDisablesById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = materialDao.queryMaterialEnabledById(map);
		if(bean != null && !bean.isEmpty()){
			if("1".equals(bean.get("enabled").toString())){//启用状态下可以禁用
				//禁用
				materialDao.editMaterialEnabledToDisablesById(map);
			}
		}else{
			outputObject.setreturnMessage("this data is non-exits.");
		}
	}

	/**
     * 启用产品信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	@Transactional(value="transactionManager")
	public void editMaterialEnabledToEnablesById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = materialDao.queryMaterialEnabledById(map);
		if(bean != null && !bean.isEmpty()){
			if("0".equals(bean.get("enabled").toString())){//禁用状态下可以启用
				//启用
				materialDao.editMaterialEnabledToEnablesById(map);
			}
		}else{
			outputObject.setreturnMessage("this data is non-exits.");
		}
	}

	/**
     * 删除产品信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	@Transactional(value="transactionManager")
	public void deleteMaterialMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = materialDao.queryMaterialDeleteFlagByIdAndUserId(map);
		if(bean != null && !bean.isEmpty()){//未删除状态下可以删除
			//删除产品
			materialDao.deleteMaterialMationById(map);
			//删除产品规格
			materialDao.deleteMaterialNormsMationById(map);
		}else{
			outputObject.setreturnMessage("this data is non-exits.");
		}
	}

	/**
     * 产品信息详情
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void queryMaterialMationDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		//获取产品信息
		Map<String, Object> bean = materialDao.queryMaterialMationDetailsById(map);
		if(bean != null && !bean.isEmpty() && bean.containsKey("id")){
			//获取产品规格参数信息
			List<Map<String, Object>> norms = materialDao.queryMaterialNormsMationDetailsById(bean.get("id").toString(), null);
			//规格
			for(Map<String, Object> norm : norms){
				norm.put("normStock", materialDao.queryNormsStockMationByNormId(norm));
			}
			bean.put("norms", norms);
			//扩展信息
			bean.put("extends", materialDao.queryMaterialExtendMationDetailsById(bean));
			
			//工序信息
			bean.put("procedureMationList", materialDao.queryMaterialProcedureMationDetailsById(bean.get("id").toString()));
			
			//附件
			if(!ToolUtil.isBlank(bean.get("enclosureInfo").toString())){
				List<Map<String,Object>> beans = sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString());
				bean.put("enclosureInfo", beans);
			}
			
			outputObject.setBean(bean);
			outputObject.settotal(1);
		}else{
			outputObject.setreturnMessage("this data is non-exits.");
		}
	}

	/**
     * 编辑产品信息进行回显
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void queryMaterialMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		//获取产品信息
		Map<String, Object> bean = materialDao.queryMaterialMationToEditById(map);
		if(bean != null && !bean.isEmpty()){
			//获取产品规格参数信息
			List<Map<String, Object>> norms = materialDao.queryMaterialNormsMationToEditById(bean);
			//规格
			for(Map<String, Object> norm : norms){
				norm.put("normStock", materialDao.queryNormsStockMationToEditByNormId(norm));
			}
			bean.put("norms", norms);
			//扩展信息
			bean.put("extends", materialDao.queryMaterialExtendMationToEditById(bean));
			
			//工序信息
			bean.put("procedureMationList", materialDao.queryMaterialProcedureMationToEditById(bean));
			
			//附件
			if(!ToolUtil.isBlank(bean.get("enclosureInfo").toString())){
				List<Map<String,Object>> beans = sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString());
				bean.put("enclosureInfo", beans);
			}
			
			outputObject.setBean(bean);
			outputObject.settotal(1);
		}else{
			outputObject.setreturnMessage("this data is non-exits.");
		}
	}

	/**
     * 编辑产品信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(value="transactionManager")
	public void editMaterialMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String materialNormsStr = map.get("materialNormsStr").toString();
		if(ToolUtil.isJson(materialNormsStr)){
			Map<String, Object> inSql = materialDao.queryMaterialByNameAndModelAndId(map);
			if(inSql != null && !inSql.isEmpty()){
				outputObject.setreturnMessage("同种型号的产品已经存在.");
				return;
			}
			//产品对象
			Map<String, Object> material = new HashMap<>();
			//产品id
			String materialId = map.get("id").toString();
			//处理数据
			List<Map<String, Object>> jArray = JSONUtil.toList(materialNormsStr, null);
			//产品单位，是多个还是单个,  1.单个  2.多个
			int unit = Integer.parseInt(map.get("unit").toString());
			//计量单位名称  当unit=1时，必填，当不是多单位时，必填，定义在这方便下面产品信息使用
			String unitName = "";
			//计量单位组id  当unit=2时，必填
			String unitGroupId = "";
			//首选出库单位，首选入库单位
			String firstOutUnit = "", firstInUnit = "";
			//配件规格仓库初始库存
			List<Map<String, Object>> normStock = new ArrayList<>();
			
			//获取原始的产品数据
			Map<String, Object> baseMation = materialDao.queryMaterialById(map);
			List<Map<String, Object>> baseNormsMation = materialDao.queryMaterialNormsById(map);
			if(unit == 1){//非多单位
				if(jArray.isEmpty()){
					outputObject.setreturnMessage("产品规格信息不能为空.");
					return;
				}
				Map<String, Object> bean = jArray.get(0);
				unitName = bean.get("unitName").toString();
				if(ToolUtil.isBlank(unitName)){
					outputObject.setreturnMessage("计量单位不能为空.");
					return;
				}
				Map<String, Object> entity = new HashMap<>();
				//设置价格的一些参数
				setCommonPrice(entity, bean);
				
				//规格id
				String normsId;
				//即将修改的单位类型和原始的单位类型不一样
				if(!String.valueOf(unit).equals(baseMation.get("unit").toString())){
					//删除产品规格
					materialDao.deleteMaterialNormsMationById(map);
					normsId = ToolUtil.getSurFaceId();
					entity.put("id", normsId);
					entity.put("deleteFlag", 0);//默认状态
					entity.put("createTime", DateUtil.getTimeAndToString());//创建时间
					entity.put("materialId", materialId);//产品id
					materialDao.insertMaterialNorms(entity);
				}else{
					normsId = baseNormsMation.get(0).get("id").toString();
					entity.put("id", normsId);
					materialDao.editMaterialNormsById(entity);
				}
				//解析初始库存信息
				normStock.addAll(getNormsStock(bean, materialId, normsId));
			}else if(unit == 2){//多单位
				unitGroupId = map.get("unitGroupId").toString();
				firstOutUnit = map.get("firstOutUnit").toString();
				firstInUnit = map.get("firstInUnit").toString();
				if(ToolUtil.isBlank(unitGroupId)){
					outputObject.setreturnMessage("请选择单位.");
					return;
				}
				if(ToolUtil.isBlank(firstOutUnit)){
					outputObject.setreturnMessage("请选择首选出库单位.");
					return;
				}
				if(ToolUtil.isBlank(firstInUnit)){
					outputObject.setreturnMessage("请选择首选入库单位.");
					return;
				}
				//即将修改的单位类型和原始的单位类型不一样
				if(!String.valueOf(unit).equals(baseMation.get("unit").toString())){
					//删除产品规格
					materialDao.deleteMaterialNormsMationById(map);
				}
				//即将修改的单位组和原始的不一样
				if(!unitGroupId.equals(baseMation.get("unitGroupId").toString())){
					//删除产品规格
					materialDao.deleteMaterialNormsMationById(map);
					//产品规格集合对象
					List<Map<String, Object>> materialNorms = new ArrayList<>();
					//产品规格实体对象，jsonArray实体对象
					Map<String, Object> entity, bean;
					for(int i = 0; i < jArray.size(); i++){
						bean = jArray.get(i);
						entity = materialDao.queryMaterialUnitByUnitId(bean);
						if(entity != null && !entity.isEmpty()){
							entity = getMaterialNormMation(bean);
							String normsId = ToolUtil.getSurFaceId();
							entity.put("id", normsId);
							entity.put("unitId", bean.get("unitId"));//计量单位id
							entity.put("materialId", materialId);//产品id
							materialNorms.add(entity);
							//解析初始库存信息
							normStock.addAll(getNormsStock(bean, materialId, normsId));
						}
					}
					if(!materialNorms.isEmpty()){
						materialDao.insertMaterialNormsList(materialNorms);
					}
				}else{
					//一样的话，则修改
					//产品规格实体对象，jsonArray实体对象
					Map<String, Object> entity, bean;
					for(int i = 0; i < jArray.size(); i++){
						bean = jArray.get(i);
						entity = materialDao.queryMaterialUnitByUnitId(bean);
						if(entity != null && !entity.isEmpty()){
							entity.put("unitId", bean.get("unitId"));//计量单位id
							//设置价格的一些参数
							setCommonPrice(entity, bean);
							String id = "";
							for(Map<String, Object> item : baseNormsMation){
								if(bean.get("unitId").toString().equals(item.get("unitId").toString())){
									id = item.get("id").toString();
									break;
								}
							}
							entity.put("id", id);
							materialDao.editMaterialNormsById(entity);
							//解析初始库存信息
							normStock.addAll(getNormsStock(bean, materialId, id));
						}
					}
				}
			}else{
				outputObject.setreturnMessage("参数错误.");
				return;
			}
			
			material.put("id", materialId);
			material.put("materialName", map.get("materialName"));//产品名称
			material.put("categoryId", map.get("categoryId"));//分类id
			material.put("model", map.get("model"));//型号
			material.put("remark", map.get("remark"));//备注
			material.put("unit", unit);//产品单位，是多个还是单个,  1.单个  2.多个
			material.put("unitName", unitName);//计量单位  当unit=1时，必填
			material.put("unitGroupId", unitGroupId);//计量单位组id  当unit=2时，必填
			material.put("firstInUnit", firstInUnit);//首选入库单位
			material.put("firstOutUnit", firstOutUnit);//首选出库单位
			material.put("type", map.get("type"));//商品来源类型  1.自产  2.外购
			material.put("enclosureInfo", map.get("enclosureInfo"));//附件
			materialDao.editMaterialMationById(material);
			//删除之前保存的初始化库存
			materialDao.deleteNormsStockMationByPartsId(materialId);
			//存储初始化库存数量
			if(!normStock.isEmpty())
				materialDao.insertNormsStockMation(normStock);
			
			//处理扩展信息-首先删除之前保留的信息
			materialDao.deleteExtendsMation(materialId);
			String extendData = map.get("extendData").toString();
			if(ToolUtil.isJson(extendData)){
				//处理数据
				List<Map<String, Object>> extendBeans = getExtendData(extendData, materialId);
				if(!extendBeans.isEmpty()){
					materialDao.insertExtendsMation(extendBeans);
				}
			}
			
			//处理商品的工序信息-首先删除之前保留的信息
			materialDao.deleteMaterialProcedureMation(materialId);
			String procedureJsonStr = map.get("procedureJsonStr").toString();
			if(ToolUtil.isJson(procedureJsonStr)){
				//处理数据
				List<Map<String, Object>> procedureBeans = getProcedureExtendData(procedureJsonStr, materialId);
				if(!procedureBeans.isEmpty()){
					materialDao.insertMaterialProcedureMation(procedureBeans);
				}
			}
		} else {
			outputObject.setreturnMessage("数据格式错误");
		}
	}

	/**
     * 获取产品列表信息展示为表格方便选择
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void queryMaterialListToTable(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(params.get("page").toString()), Integer.parseInt(params.get("limit").toString()));
		List<Map<String, Object>> beans = materialDao.queryMaterialListToTable(params);
		//获取规格单位信息
		List<Map<String, Object>> unitList;
		for (Map<String, Object> bean : beans) {
			unitList = materialDao.queryMaterialUnitByIdToSelect(bean.get("productId").toString());
			if ("1".equals(bean.get("unit").toString())) {// 不是多单位
				unitList.get(0).put("name", bean.get("unitName").toString());
			}
			bean.put("unitList", unitList);
			
			//工序信息
			bean.put("procedureMationList", materialDao.queryMaterialProcedureMationDetailsById(bean.get("productId").toString()));
		}
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
     * 根据商品规格id以及仓库id获取库存
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void queryMaterialTockByNormsId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
		// 获取多个商品规格id
		String[] normsIds = params.get("normsIds").toString().split(",");
		// 仓库id
		String depotId = params.get("depotId").toString();
		if(normsIds.length == 1){
			Map<String, Object> bean = materialDao.queryMaterialTockByNormsId(normsIds[0], depotId);
			outputObject.setBean(bean);
		} else {
			List<Map<String, Object>> normsStocks = new ArrayList<>();
			Arrays.asList(normsIds).forEach(normsId -> {
				try {
					Map<String, Object> bean = new HashMap<>();
					bean.putAll(materialDao.queryMaterialTockByNormsId(normsId, depotId));
					bean.put("normsId", normsId);
					normsStocks.add(bean);
				} catch (Exception e) {
					LOGGER.warn("get stock by normsId and depotId failed. normsId is {}, message is: {}", normsId, e);
				}
			});
			outputObject.setBeans(normsStocks);
		}
	}

	/**
     * 根据产品规格id获取库存订单-用于库存订单明细
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void queryMaterialDepotItemByNormsId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(params.get("page").toString()), Integer.parseInt(params.get("limit").toString()));
        List<Map<String, Object>> beans = materialDao.queryMaterialDepotItemByNormsId(params);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
     * 根据商品id串获取商品列表
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void queryMaterialListByIds(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<String> idsList = Arrays.asList(map.get("ids").toString().split(","));
		List<Map<String, Object>> beans = new ArrayList<>();
		if(!idsList.isEmpty()){
			beans = materialDao.queryMaterialListByIds(idsList);
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}else{
			outputObject.setBeans(beans);
		}
	}

	/**
     * 根据商品id串获取商品列表---用于生产模块
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void queryMaterialListByIdsToProduce(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<String> idsList = Arrays.asList(map.get("ids").toString().split(","));
		List<Map<String, Object>> beans = new ArrayList<>();
		if(!idsList.isEmpty()){
			beans = materialDao.queryMaterialListByIds(idsList);
			//获取规格单位信息
			List<Map<String, Object>> unitList;
			for (Map<String, Object> bean : beans) {
				//获取规格列表
				unitList = materialDao.queryMaterialUnitByIdToSelect(bean.get("productId").toString());
				if ("1".equals(bean.get("unit").toString())) {// 不是多单位
					unitList.get(0).put("name", bean.get("unitName").toString());
				}
				bean.put("unitList", unitList);
				
				//获取bom多方案列表信息
				bean.put("bomList", materialDao.queryBomListByIdToSelect(bean.get("productId").toString(), unitList.get(0).get("id").toString()));
				
				//工序信息
				bean.put("procedureMationList", materialDao.queryMaterialProcedureMationDetailsById(bean.get("productId").toString()));
			}
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}else{
			outputObject.setBeans(beans);
		}
	}

	/**
     * 根据商品信息以及bom方案信息获取商品树---用于生产模块
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@SuppressWarnings({ "unchecked" })
	@Override
	public void queryMaterialBomChildsToProduceByJson(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String proList = map.get("proList").toString();
		if(ToolUtil.isJson(proList)){
			//处理数据
			List<Map<String, Object>> jArray = JSONUtil.toList(proList, null);
			//返回前台的数据
			List<Map<String, Object>> beans = new ArrayList<>();
			Map<String, Object> bean;
			//遍历商品
			for(int i = 0; i < jArray.size(); i++){
				bean = jArray.get(i);
				//获取bom方案下的商品
				if(!ToolUtil.isBlank(bean.get("bomId").toString())){
					List<Map<String, Object>> bomMaterialList = materialDao.queryBomMaterialListByBomId(bean.get("bomId").toString(), bean.get("productId").toString());
					for(Map<String, Object> bemPro: bomMaterialList){
						//工序信息
						bemPro.put("procedureMationList", materialDao.queryMaterialProcedureMationDetailsById(bemPro.get("productId").toString()));
					}
					beans.addAll(bomMaterialList);
				}
				beans.add(bean);
			}
			outputObject.setBeans(beans);
		}else{
			outputObject.setreturnMessage("数据格式化错误。");
		}
	}
	
	/**
     * 获取商品库存信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void queryMaterialReserveList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(params.get("page").toString()), Integer.parseInt(params.get("limit").toString()));
        List<Map<String, Object>> beans = materialDao.queryMaterialReserveList(params);
        // 获取规格单位信息
  		List<Map<String, Object>> norms;
  		for (Map<String, Object> bean : beans) {
  			// 获取商品规格参数信息-这里主要用到规格的总库存
			norms = materialDao.queryMaterialNormsMationDetailsById(bean.get("id").toString(), params.get("depotId").toString());
  			if ("1".equals(bean.get("unit").toString())) {// 不是多单位
  				norms.get(0).put("unitName", bean.get("unitName").toString());
  			}
  			bean.put("norms", norms);
  		}
        outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
     * 获取预警商品库存信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void queryMaterialInventoryWarningList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(params.get("page").toString()), Integer.parseInt(params.get("limit").toString()));
        List<Map<String, Object>> beans = materialDao.queryMaterialInventoryWarningList(params);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}
	
}
