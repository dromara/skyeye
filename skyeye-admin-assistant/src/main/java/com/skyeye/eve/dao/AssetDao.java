/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: AssetDao
 * @Description: 资产管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 18:45
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface AssetDao {

	public List<Map<String, Object>> selectAllAssetMation(Map<String, Object> map) throws Exception;

	public int insertAssetMation(Map<String, Object> map) throws Exception;

	public int deleteAssetById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryAssetMationById(@Param("id") String id) throws Exception;

	public int editAssetMationById(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> selectAssetDetailsById(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> queryAssetState(Map<String, Object> map) throws Exception;

	public int updateAssetRepairById(Map<String, Object> map) throws Exception;

	public int updateAssetScrapById(Map<String, Object> map) throws Exception;

	public int updateAssetNormalById(Map<String, Object> map) throws Exception;
	
	public List<Map<String, Object>> queryUnUseAssetListByTypeId(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryAssetById(Map<String, Object> bean) throws Exception;

	public int updateAssetToCancellation(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryEmployeeNameFromAssetUse(Map<String, Object> bean) throws Exception;

	public int editAssetManagementById(Map<String, Object> bean) throws Exception;

    public Map<String, Object> queryAssetMationByAssetNum(Map<String, Object> map) throws Exception;
	
    public Map<String, Object> queryAssetMationByIdAndAssetNum(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryUserCompanyById(Map<String, Object> user) throws Exception;

	public int insertAssetMationList(List<Map<String, Object>> beans) throws Exception;

	public int editAssetManagementMation(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryMyAssetManagementList(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryAnythingFromAsset(Map<String, Object> bean) throws Exception;
}
