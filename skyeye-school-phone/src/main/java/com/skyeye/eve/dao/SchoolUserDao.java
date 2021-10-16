/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface SchoolUserDao {

	public Map<String, Object> queryMationByIdCardOrNo(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryStuUserMationDetailById(@Param("stuId") String stuId) throws Exception;

	public int editUserPassword(@Param("stuId") String stuId, @Param("password") String password) throws Exception;

}
