/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 *
 * @ClassName: AssetArticlesService
 * @Description: 用品管理服务接口类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/24 11:47
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface AssetArticlesService {

	public void insertAssetArticles(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void queryAssetArticlesList(InputObject inputObject, OutputObject outputObject) throws Exception;
	
    public void queryAssetArticlesMationToDetails(InputObject inputObject, OutputObject outputObject) throws Exception;
    
    public void deleteAssetArticles(InputObject inputObject, OutputObject outputObject) throws Exception;
    
    public void queryAssetArticlesMationById(InputObject inputObject, OutputObject outputObject) throws Exception;
    
    public void editAssetArticlesMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryAssetArticlesListByTypeId(InputObject inputObject, OutputObject outputObject) throws Exception;

}
