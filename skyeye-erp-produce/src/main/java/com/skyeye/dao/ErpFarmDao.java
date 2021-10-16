/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 */

package com.skyeye.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author 卫志强
 * @title: ErpFarmDao
 * @projectName skyeye-promote
 * @description: TODO
 * @date 2020/8/30 14:18
 */
public interface ErpFarmDao {

    public List<Map<String, Object>> queryErpFarmList(Map<String, Object> params) throws Exception;

    public Map<String, Object> queryErpFarmMationByName(Map<String, Object> params) throws Exception;

    public int insertErpFarmMation(Map<String, Object> params) throws Exception;

    public Map<String, Object> queryErpFarmToEditById(@Param("farmId") String farmId) throws Exception;

    public Map<String, Object> queryErpFarmByNameAndId(Map<String, Object> params) throws Exception;

    public int editErpFarmMationById(Map<String, Object> params) throws Exception;

    public Map<String, Object> queryErpFarmMationStateById(@Param("farmId")String id) throws Exception;

    public int editErpFarmMationStateById(@Param("farmId")String id, @Param("state") int state) throws Exception;

    public Map<String, Object> queryErpFarmMationById(@Param("farmId") String farmId) throws Exception;

    public List<Map<String, Object>> queryErpFarmListByProcedureId(Map<String, Object> params) throws Exception;

    public int deleteFarmProcedureByFarmId(@Param("farmId") String farmId) throws Exception;

    public int insertFarmProcedureList(List<Map<String, Object>> beans) throws Exception;

    public List<Map<String, Object>> queryProcedureListByFarmId(@Param("farmId")String farmId) throws Exception;

	public List<Map<String, Object>> queryErpFarmListToTable(Map<String, Object> params) throws Exception;

	public List<Map<String, Object>> queryErpFarmProcedureListByIds(@Param("idsList") List<String> idsList) throws Exception;
}
