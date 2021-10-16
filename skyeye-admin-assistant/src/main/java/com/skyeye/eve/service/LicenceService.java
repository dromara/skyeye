/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 *
 * @ClassName: LicenceService
 * @Description: 证照管理服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/24 23:06
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface LicenceService {

	public void selectAllLicenceMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertLicenceMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteLicenceById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryLicenceMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editLicenceMationById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void selectLicenceDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void selectLicenceListToChoose(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void selectRevertListToChoose(InputObject inputObject, OutputObject outputObject) throws Exception;
	
}
