/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: SysStaffContractDao
 * @Description: 员工合同管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 20:55
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface SysStaffContractDao {

	public List<Map<String, Object>> queryAllSysStaffContractList(Map<String, Object> params) throws Exception;

	public int insertSysStaffContractMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysStaffContractMationToEdit(@Param("id") String id) throws Exception;

	public int editSysStaffContractMationById(Map<String, Object> map) throws Exception;

	public int deleteSysStaffContractMationById(@Param("id") String id) throws Exception;

	public List<Map<String, Object>> queryPointStaffSysStaffContractList(Map<String, Object> params) throws Exception;

	public int editSysStaffContractStateById(@Param("id") String id, @Param("state") int state) throws Exception;

}