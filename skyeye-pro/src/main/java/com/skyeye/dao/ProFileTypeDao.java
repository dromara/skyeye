/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dao;

import java.util.List;
import java.util.Map;

public interface ProFileTypeDao {

    public int insertProFileType(Map<String, Object> map) throws Exception;

    public List<Map<String, Object>> queryProFileTypeList(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryProFileTypeMationById(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryStateById(Map<String, Object> map) throws  Exception;

    public List<Map<String, Object>> queryStateUpList(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryProFileTypeByName(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryProFileTypeByIdAndName(Map<String, Object> map) throws Exception;

    public int editProFileTypeById(Map<String, Object> map) throws Exception;

    public int editStateUpById(Map<String, Object> map) throws Exception;

    public int editStateDownById(Map<String, Object> map) throws Exception;

    public int deleteProFileTypeById(Map<String, Object> map) throws Exception;

}
