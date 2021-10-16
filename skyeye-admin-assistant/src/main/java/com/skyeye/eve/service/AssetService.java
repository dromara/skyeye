/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 *
 * @ClassName: AssetService
 * @Description: 资产管理服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/18 16:47
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface AssetService {

	public void selectAllAssetMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertAssetMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteAssetById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryAssetMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editAssetMationById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void selectAssetDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void updateAssetNormalById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void updateAssetRepairById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void updateAssetScrapById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void queryUnUseAssetListByTypeId(InputObject inputObject, OutputObject outputObject) throws Exception;

}
