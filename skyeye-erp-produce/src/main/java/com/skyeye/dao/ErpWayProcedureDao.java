/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 
 * @ClassName: ErpWayProcedureDao
 * @Description: 工艺路线管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 11:40
 *   
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ErpWayProcedureDao {

	public List<Map<String, Object>> queryErpWayProcedureList(Map<String, Object> params) throws Exception;

	public Map<String, Object> queryErpWayProcedureByNameOrNumber(Map<String, Object> params) throws Exception;

	public int insertErpWayProcedureMation(Map<String, Object> params) throws Exception;

	public int deleteProcedureByWayProcedureId(@Param("wayProcedureId") String wayProcedureId) throws Exception;

	public int insertWayProcedureList(List<Map<String, Object>> beans) throws Exception;

	public Map<String, Object> queryErpWayProcedureToEditById(@Param("wayProcedureId") String wayProcedureId) throws Exception;

	public List<Map<String, Object>> queryProcedureListByWayProcedureId(@Param("wayProcedureId") String wayProcedureId) throws Exception;

	public Map<String, Object> queryErpWayProcedureByNameAndId(Map<String, Object> params) throws Exception;

	public int editErpWayProcedureMationById(Map<String, Object> params) throws Exception;

	public Map<String, Object> queryErpFarmMationStateById(@Param("wayProcedureId") String wayProcedureId) throws Exception;

	public int editErpFarmMationStateById(@Param("wayProcedureId") String wayProcedureId, @Param("state") int state) throws Exception;

	public Map<String, Object> queryErpWayProcedureDetailsById(@Param("wayProcedureId") String wayProcedureId) throws Exception;

}
