/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: AssetArticlesTypeDao
 * @Description: 用品分类数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/24 9:06
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface AssetArticlesTypeDao {

    public List<Map<String, Object>> queryAssetArticlesTypeList(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryAssetArticlesTypeMationByName(Map<String, Object> map) throws Exception;

    public int insertAssetArticlesType(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryAssetArticlesTypeBySimpleLevel(Map<String, Object> map) throws Exception;

    public int deleteAssetArticlesTypeById(Map<String, Object> map) throws Exception;

    public int updateUpAssetArticlesTypeById(Map<String, Object> map) throws Exception;

    public int updateDownAssetArticlesTypeById(Map<String, Object> map) throws Exception;

    public Map<String, Object> selectAssetArticlesTypeById(Map<String, Object> map) throws Exception;

    public int editAssetArticlesTypeById(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryAssetArticlesTypeUpMationById(Map<String, Object> map) throws Exception;

    public int editAssetArticlesTypeOrderUpById(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryAssetArticlesTypeDownMationById(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryAssetArticlesTypeStateById(Map<String, Object> map) throws Exception;

    public List<Map<String, Object>> queryAssetArticlesTypeUpStateList(Map<String, Object> map) throws Exception;

}
