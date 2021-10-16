/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 *
 * @ClassName: SealService
 * @Description: 印章管理服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/24 16:04
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface SealService {

	public void selectAllSealMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertSealMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteSealById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySealMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSealMationById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void selectSealDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void selectSealListToChoose(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void selectRevertListToChoose(InputObject inputObject, OutputObject outputObject) throws Exception;
	
}
