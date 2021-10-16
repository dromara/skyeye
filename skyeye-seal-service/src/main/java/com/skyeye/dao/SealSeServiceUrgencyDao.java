/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dao;

import java.util.List;
import java.util.Map;

public interface SealSeServiceUrgencyDao {

    public int insertSealSeServiceUrgency(Map<String, Object> map) throws Exception;

    public List<Map<String, Object>> querySealSeServiceUrgencyList(Map<String, Object> map) throws Exception;

    public Map<String, Object> querySealSeServiceUrgencyMationById(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryStateById(Map<String, Object> map) throws  Exception;

    public List<Map<String, Object>> queryStateUpList(Map<String, Object> map) throws Exception;

    public Map<String, Object> querySealSeServiceUrgencyByName(Map<String, Object> map) throws Exception;

    public Map<String, Object> querySealSeServiceUrgencyByIdAndName(Map<String, Object> map) throws Exception;

    public int editSealSeServiceUrgencyById(Map<String, Object> map) throws Exception;

    public int editStateUpById(Map<String, Object> map) throws Exception;

    public int editStateDownById(Map<String, Object> map) throws Exception;

    public int deleteSealSeServiceUrgencyById(Map<String, Object> map) throws Exception;

}
