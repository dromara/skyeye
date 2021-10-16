package com.skyeye.eve.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface WagesModelApplicableObjectsDao {

    public int insertWagesModelApplicableObjects(@Param("list") List<Map<String, Object>> beans) throws Exception;

    public int deleteWagesModelApplicableObjectsByModelId(@Param("modelId") String modelId) throws Exception;

    public List<Map<String, Object>> queryWagesModelApplicableObjectsByModelId(@Param("modelId") String modelId) throws Exception;

    public List<Map<String, Object>> queryAllEanbleWagesModelApplicableObjects(@Param("lastMonthDate") String lastMonthDate) throws Exception;
    
}
