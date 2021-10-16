package com.skyeye.eve.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface CompanyJobScoreFieldDao {

    public int insertCompanyJobScoreField(@Param("list") List<Map<String, Object>> beans) throws Exception;

    public int deleteCompanyJobScoreFieldByJobScoreId(@Param("jobScoreId") String jobScoreId) throws Exception;

    public List<Map<String, Object>> queryCompanyJobScoreFieldByJobScoreId(@Param("jobScoreId") String jobScoreId) throws Exception;
    
}
