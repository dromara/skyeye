/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 */

package com.skyeye.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ErpWorkProcedureTypeDao {

	public List<Map<String, Object>> queryErpWorkProcedureTypeList(Map<String, Object> params) throws Exception;

    public Map<String, Object> queryErpWorkProcedureTypeByName(Map<String, Object> params) throws Exception;

    public int insertErpWorkProcedureTypeMation(Map<String, Object> params) throws Exception;

    public Map<String, Object> queryErpWorkProcedureTypeById(@Param("id") String id) throws Exception;

    public Map<String, Object> queryErpWorkProcedureTypeByNameAndId(Map<String, Object> params) throws Exception;

    public int editErpWorkProcedureTypeMationById(Map<String, Object> params) throws Exception;

    public Map<String, Object> queryErpWorkProcedureTypeStateById(String id) throws Exception;

    public void editErpWorkProcedureTypeStateById(@Param("id") String id, @Param("state") int state) throws Exception;

    public List<Map<String, Object>> queryErpWorkProcedureTypeUpMation() throws Exception;
}
