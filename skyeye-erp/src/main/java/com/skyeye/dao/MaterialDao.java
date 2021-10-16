/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: MaterialDao
 * @Description: 产品信息管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 12:17
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface MaterialDao {

	public List<Map<String, Object>> queryMaterialList(Map<String, Object> params) throws Exception;

	public Map<String, Object> queryMaterialUnitByUnitId(Map<String, Object> bean) throws Exception;

	public int insertMaterialNormsList(List<Map<String, Object>> materialNorms) throws Exception;

	public int insertMaterialMation(Map<String, Object> material) throws Exception;

	public int insertMaterialNorms(Map<String, Object> entity) throws Exception;

	public Map<String, Object> queryMaterialEnabledById(Map<String, Object> map) throws Exception;

	public int editMaterialEnabledToDisablesById(Map<String, Object> map) throws Exception;

	public int editMaterialEnabledToEnablesById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryMaterialDeleteFlagByIdAndUserId(Map<String, Object> map) throws Exception;

	public int deleteMaterialMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryMaterialMationDetailsById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryMaterialNormsMationDetailsById(@Param("materialId") String materialId, @Param("depotId") String depotId) throws Exception;

	public int deleteMaterialNormsMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryMaterialByNameAndModel(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryMaterialMationToEditById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryMaterialNormsMationToEditById(Map<String, Object> bean) throws Exception;

	public Map<String, Object> queryMaterialByNameAndModelAndId(Map<String, Object> map) throws Exception;

	public int editMaterialMationById(Map<String, Object> material) throws Exception;

	public Map<String, Object> queryMaterialById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryMaterialNormsById(Map<String, Object> map) throws Exception;

	public int editMaterialNormsById(Map<String, Object> entity) throws Exception;

	public List<Map<String, Object>> queryMaterialListToTable(Map<String, Object> params) throws Exception;

	public List<Map<String, Object>> queryMaterialUnitByIdToSelect(@Param("materialId") String materialId) throws Exception;

	public Map<String, Object> queryMaterialTockByNormsId(@Param("normsId") String normsId, @Param("depotId") String depotId) throws Exception;

	public List<Map<String, Object>> queryMaterialDepotItemByNormsId(Map<String, Object> params) throws Exception;

	public int insertNormsStockMation(List<Map<String, Object>> normStock) throws Exception;

	public List<Map<String, Object>> queryNormsStockMationByNormId(Map<String, Object> norm) throws Exception;

	public List<Map<String, Object>> queryNormsStockMationToEditByNormId(Map<String, Object> norm) throws Exception;

	public int deleteNormsStockMationByPartsId(@Param("materialId") String materialId) throws Exception;

	public int insertExtendsMation(List<Map<String, Object>> extendBeans) throws Exception;

	public int deleteExtendsMation(@Param("materialId") String materialId) throws Exception;

	public List<Map<String, Object>> queryMaterialExtendMationToEditById(Map<String, Object> bean) throws Exception;

	public List<Map<String, Object>> queryMaterialExtendMationDetailsById(Map<String, Object> bean) throws Exception;

	public List<Map<String, Object>> queryMaterialListByIds(@Param("idsList") List<String> idsList) throws Exception;

	public int insertMaterialProcedureMation(List<Map<String, Object>> procedureBeans) throws Exception;

	public int deleteMaterialProcedureMation(@Param("materialId") String materialId) throws Exception;

	public List<Map<String, Object>> queryMaterialProcedureMationToEditById(Map<String, Object> bean) throws Exception;

	public List<Map<String, Object>> queryMaterialProcedureMationDetailsById(@Param("materialId") String materialId) throws Exception;

	/**
	 * 获取该商品的该规格下有几种bom方案
	 * @param materialId 商品id
	 * @param normsId 规格id
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> queryBomListByIdToSelect(@Param("materialId") String materialId, @Param("normsId") String normsId) throws Exception;

	/**
	 * 根据bomId获取这个bom表下的所有子商品
	 * @param bomId bom表id
	 * @param materialId 
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> queryBomMaterialListByBomId(@Param("bomId") String bomId, @Param("materialId") String materialId) throws Exception;

	/**
	 * 根据规格id查询商品信息
	 * @param normsId 规格id
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> queryMaterialsByNormsId(@Param("normsId") String normsId) throws Exception;

	public List<Map<String, Object>> queryMaterialReserveList(Map<String, Object> params) throws Exception;

	/**
	 * 根据方案id获取bom表子件列表
	 * @param bomId 方案id
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> queryErpBomChildProListByBomId(@Param("bomId") String bomId) throws Exception;

	public List<Map<String, Object>> queryMaterialInventoryWarningList(Map<String, Object> params) throws Exception;

}
