/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: AssetFromDao
 * @Description: 资产来源数据接口层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/18 16:49
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface AssetFromDao {

    public List<Map<String, Object>> selectAllAssetFromMation(Map<String, Object> map) throws Exception;

    public int insertAssetFromMation(Map<String, Object> map) throws Exception;

    public int deleteAssetFromById(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryAssetFromMationById(Map<String, Object> map) throws Exception;

    public int editAssetFromMationById(Map<String, Object> map) throws Exception;

    public List<Map<String, Object>> selectAllAssetFromToChoose(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryAssetFromISTopByThisId(Map<String, Object> map) throws Exception;

    public int editAssetFromSortTopById(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryAssetFromISLowerByThisId(Map<String, Object> map) throws Exception;

    public int editAssetFromSortLowerById(Map<String, Object> map) throws Exception;

    public int editAssetFromUpTypeById(Map<String, Object> map) throws Exception;

    public int editAssetFromDownTypeById(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryAssetFromMationStateById(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryAssetFromAfterOrderBum(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryAssetFromMationByName(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryAssetFromMationByNameAndId(Map<String, Object> map) throws Exception;
    
}
