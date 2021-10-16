/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface UserPhoneDao {
	
	public Map<String, Object> queryMationByUserCode(Map<String, Object> map) throws Exception;
	
	public List<Map<String, Object>> queryAppMenuByUserId(Map<String, Object> map) throws Exception;
	
	public List<Map<String, Object>> queryAppAuthPointsByUserId(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> queryUserMationByOpenId(String openId) throws Exception;

	public int insertWxUserMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryUserMationByUserCode(@Param("userCode") String userCode) throws Exception;

	public Map<String, Object> queryUserBindMationByUserId(@Param("userId") String userId) throws Exception;

	public int updateBindUserMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryUserMationByOPenId(String openId) throws Exception;

	public List<Map<String, Object>> queryAllPeopleToTree(Map<String, Object> map) throws Exception;
	
}
