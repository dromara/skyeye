/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dao;

import java.util.List;
import java.util.Map;

public interface CrmDocumentaryDao {

    public List<Map<String, Object>> queryMyDocumentaryList(Map<String, Object> map) throws Exception;
    
    public List<Map<String, Object>> queryAllDocumentaryList(Map<String, Object> map) throws Exception;

    public int insertDocumentary(Map<String, Object> map) throws Exception;
    
    public Map<String, Object> queryDocumentaryMationById(Map<String, Object> map) throws Exception;
    
    public int editDocumentaryMationById(Map<String, Object> map) throws Exception;
    
    public int deleteDocumentaryMationById(Map<String, Object> map) throws Exception;
    
    public Map<String, Object> queryDocumentaryMationToDetail(Map<String, Object> map) throws Exception;
    
}
