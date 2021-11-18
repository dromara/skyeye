/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ActModleTypeDao {

	public int insertActModleTypeMation(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> selectAllActModleTypeMation(Map<String, Object> map) throws Exception;

	public int editActModleTypeNameById(Map<String, Object> map) throws Exception;

	public int editActModleTypeStateById(@Param("id") String id, @Param("state") int state) throws Exception;

	public Map<String, Object> queryCountOrderby(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryActModleTypeByTitle(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryActModleTypeMationById(@Param("id") String id) throws Exception;

	public Map<String, Object> queryActModleTypeUpMationById(Map<String, Object> map) throws Exception;

	public int editActModleTypeMationOrderNumUpById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryActModleTypeDownMationById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryActModleUpStateByUpStateType(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryAllDsForm(Map<String, Object> map) throws Exception;

	public int editDsFormMationBySequenceId(Map<String, Object> m) throws Exception;

	public void editDsFormMationBySequenceIdAndProcessInstanceId(Map<String, Object> job) throws Exception;

}
