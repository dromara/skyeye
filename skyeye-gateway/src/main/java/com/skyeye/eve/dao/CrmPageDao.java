/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface CrmPageDao {

	public List<Map<String, Object>> queryInsertNumByYear(@Param("year") String year) throws Exception;

	public List<Map<String, Object>> queryCustomNumByType() throws Exception;

	public List<Map<String, Object>> queryCustomNumByFrom() throws Exception;

	public List<Map<String, Object>> queryCustomNumByIndustry() throws Exception;

	public List<Map<String, Object>> queryCustomNumByGroup() throws Exception;

	public List<Map<String, Object>> queryCustomDocumentaryType(@Param("year") String year) throws Exception;

	public List<Map<String, Object>> queryNewContractNum(@Param("year") String year) throws Exception;

	public List<Map<String, Object>> queryNewDocumentaryNum(@Param("year") String year) throws Exception;

}
