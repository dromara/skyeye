/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: LicenceDao
 * @Description: 证照管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/24 23:08
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface LicenceDao {

	public List<Map<String, Object>> selectAllLicenceMation(Map<String, Object> map) throws Exception;

	public int insertLicenceMation(Map<String, Object> map) throws Exception;

	public int deleteLicenceById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryLicenceMationById(@Param("licenceId") String licenceId) throws Exception;

	public int editLicenceBorrowIdById(@Param("licenceId") String licenceId, @Param("borrowId") String borrowId) throws Exception;

	public int editLicenceMationById(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> selectLicenceDetailsById(Map<String, Object> map) throws Exception;
	
	public List<Map<String, Object>> selectLicenceListToChoose(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> queryLicenceEntityById(@Param("licenceId") String licenceId) throws Exception;

	public List<Map<String, Object>> selectRevertListToChoose(Map<String, Object> map) throws Exception;
	
	public List<Map<String, Object>> queryMyLicenceManageList(Map<String, Object> map) throws Exception;
}
