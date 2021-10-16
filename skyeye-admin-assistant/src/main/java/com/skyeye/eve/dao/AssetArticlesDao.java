/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: AssetArticlesDao
 * @Description: 用品管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/24 9:09
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface AssetArticlesDao {

	public int insertAssetArticles(Map<String, Object> map) throws Exception;
	
    public List<Map<String, Object>> queryAssetArticlesList(Map<String, Object> map) throws Exception;
    
    public Map<String, Object> queryUserCompanyById(Map<String, Object> user) throws Exception;
    
    public Map<String, Object> queryAssetArticlesMationToDetails(Map<String, Object> map) throws Exception;
    
    public int deleteAssetArticles(Map<String, Object> map) throws Exception;
    
    public Map<String, Object> queryAssetArticlesMationById(Map<String, Object> map) throws Exception;
    
    public int editAssetArticlesMationById(Map<String, Object> map) throws Exception;
    
	public List<Map<String, Object>> queryAssetArticlesListByTypeId(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryAssetArticleById(Map<String, Object> bean) throws Exception;

	public int editAssetsArticlesById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryMyAssetArticlesList(Map<String, Object> map) throws Exception;
}
