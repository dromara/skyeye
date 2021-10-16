/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 */

package com.skyeye.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: ErpWorkProcedureDao
 * @Description: 工序管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 11:50
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ErpWorkProcedureDao {

	public List<Map<String, Object>> queryErpWorkProcedureList(Map<String, Object> params) throws Exception;

	public Map<String, Object> queryErpWorkProcedureByNameOrNumber(Map<String, Object> params) throws Exception;

	public int insertErpWorkProcedureMation(Map<String, Object> params) throws Exception;

	public Map<String, Object> queryErpWorkProcedureToEditById(Map<String, Object> params) throws Exception;

	public int deletErpWorkProcedureById(Map<String, Object> params) throws Exception;

	public Map<String, Object> queryErpWorkProcedureByNameOrNumberAndId(Map<String, Object> params) throws Exception;

	public int editErpWorkProcedureById(Map<String, Object> params) throws Exception;

	public List<Map<String, Object>> queryErpWorkProcedureListToTable(Map<String, Object> params) throws Exception;

	public List<Map<String, Object>> queryErpWorkProcedureListByIds(@Param("idsList") List<String> idsList) throws Exception;

	public Map<String, Object> queryErpWorkProcedureMationById(@Param("procedureId") String procedureId) throws Exception;

	public List<Map<String, Object>> queryErpWorkProcedureListToSelect() throws Exception;
}