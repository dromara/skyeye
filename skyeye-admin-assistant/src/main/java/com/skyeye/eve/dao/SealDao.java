/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: SealDao
 * @Description: 印章管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/24 16:02
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface SealDao {

	public List<Map<String, Object>> selectAllSealMation(Map<String, Object> map) throws Exception;

	public int insertSealMation(Map<String, Object> map) throws Exception;

	public int deleteSealById(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySealMationById(@Param("id") String id) throws Exception;

	public int editSealBorrowIdById(@Param("id") String id, @Param("borrowId") String borrowId) throws Exception;

	public int editSealMationById(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> selectSealDetailsById(Map<String, Object> map) throws Exception;
	
	public List<Map<String, Object>> selectSealListToChoose(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> querySealEntityById(Map<String, Object> map) throws Exception;
	
	public List<Map<String, Object>> queryMySealManageList(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> selectRevertListToChoose(Map<String, Object> map) throws Exception;
}
