/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dao;

import java.util.List;
import java.util.Map;

public interface ProProjectDiscussTypeDao {

    public int insertProProjectDiscussType(Map<String, Object> map) throws Exception;

    public List<Map<String, Object>> queryProProjectDiscussTypeList(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryProProjectDiscussTypeMationById(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryStateById(Map<String, Object> map) throws  Exception;

    public List<Map<String, Object>> queryStateUpList(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryProProjectDiscussTypeByName(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryProProjectDiscussTypeByIdAndName(Map<String, Object> map) throws Exception;

    public int editProProjectDiscussTypeById(Map<String, Object> map) throws Exception;

    public int editStateUpById(Map<String, Object> map) throws Exception;

    public int editStateDownById(Map<String, Object> map) throws Exception;

    public int deleteProProjectDiscussTypeById(Map<String, Object> map) throws Exception;

}
