/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dao;

import java.util.List;
import java.util.Map;

public interface SealSeServiceFeedbackTypeDao {

    public int insertCrmServiceFeedbackType(Map<String, Object> map) throws Exception;

    public List<Map<String, Object>> queryCrmServiceFeedbackTypeList(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryCrmServiceFeedbackTypeMationById(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryStateById(Map<String, Object> map) throws  Exception;

    public List<Map<String, Object>> queryStateUpList(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryCrmServiceFeedbackTypeByName(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryCrmServiceFeedbackTypeByIdAndName(Map<String, Object> map) throws Exception;

    public int editCrmServiceFeedbackTypeById(Map<String, Object> map) throws Exception;

    public int editStateUpById(Map<String, Object> map) throws Exception;

    public int editStateDownById(Map<String, Object> map) throws Exception;

    public int deleteCrmServiceFeedbackTypeById(Map<String, Object> map) throws Exception;

}
