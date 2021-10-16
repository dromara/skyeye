/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Author: 卫志强
 * @Description: TODO
 * @Date: 2019/9/14 10:45
 */
public interface StoreHouseDao {

    public List<Map<String, Object>> queryStoreHouseByList(Map<String, Object> params) throws Exception;

    public Map<String, Object> queryStoreHouseByName(Map<String, Object> params) throws Exception;

    public int insertStoreHouse(Map<String, Object> params) throws Exception;

    public Map<String, Object> queryStoreHouseById(Map<String, Object> params) throws Exception;

    public int editStoreHouseById(Map<String, Object> params) throws Exception;

    public int editStoreHouseByDeleteFlag(Map<String, Object> params) throws Exception;

    public int editStoreHouseByDefault(Map<String, Object> params) throws Exception;

    public int editStoreHouseByDefaultAll(Map<String, Object> params) throws Exception;

    public Map<String, Object> queryStoreHouseByIdAndName(Map<String, Object> params) throws Exception;

    public Map<String, Object> queryStoreHouseByIsDefault(Map<String, Object> params) throws Exception;

	public List<Map<String, Object>> queyrStoreHouseListToSelect(Map<String, Object> map) throws Exception;
	
    public Map<String, Object> queryStoreHouseByIdAndInfo(Map<String, Object> params) throws Exception;

	public List<Map<String, Object>> queryChargeUserNameById(Map<String, Object> bean) throws Exception;

	public List<Map<String, Object>> queryStoreHouseListByCurrentUserId(@Param("currentUserId") String currentUserId) throws Exception;
    
}
