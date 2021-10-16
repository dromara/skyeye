/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface DsFormDisplayTemplateService {

	public void queryDsFormDisplayTemplateList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertDsFormDisplayTemplateMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteDsFormDisplayTemplateMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryDsFormDisplayTemplateMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editDsFormDisplayTemplateMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryDisplayTemplateListToShow(InputObject inputObject, OutputObject outputObject) throws Exception;

}
