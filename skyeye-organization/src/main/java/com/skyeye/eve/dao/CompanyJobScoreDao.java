/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface CompanyJobScoreDao {

    public List<Map<String, Object>> queryCompanyJobScoreList(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryCompanyJobScoreByNameAndNotId(@Param("nameCn") String nameCn, @Param("jobId") String jobId, @Param("notId") String id) throws Exception;

    public int insertCompanyJobScoreMation(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryCompanyJobScoreMationById(@Param("id") String id) throws Exception;

    public int editCompanyJobScoreMationById(Map<String, Object> map) throws Exception;

    public int editCompanyJobScoreStateMationById(@Param("id") String id, @Param("state") int state) throws Exception;

    public int editCompanyJobScoreStateMationByJobId(@Param("jobId") String jobId, @Param("state") int state) throws Exception;

    public List<Map<String, Object>> queryEnableCompanyJobScoreList(Map<String, Object> map) throws Exception;
}
