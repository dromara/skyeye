/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dao;

import java.util.List;
import java.util.Map;

public interface CrmOpportunityFromDao {

    public int insertCrmOpportunityFrom(Map<String, Object> map) throws Exception;

    public List<Map<String, Object>> queryCrmOpportunityFromList(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryCrmOpportunityFromMationById(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryStateById(Map<String, Object> map) throws  Exception;

    public List<Map<String, Object>> queryStateUpList(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryCrmOpportunityFromByName(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryCrmOpportunityFromByIdAndName(Map<String, Object> map) throws Exception;

    public int editCrmOpportunityFromById(Map<String, Object> map) throws Exception;

    public int editStateUpById(Map<String, Object> map) throws Exception;

    public int editStateDownById(Map<String, Object> map) throws Exception;

    public int deleteCrmOpportunityFromById(Map<String, Object> map) throws Exception;

}
