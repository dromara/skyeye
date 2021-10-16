/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface LightAppTypeService {

public void queryLightAppTypeList(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void insertLightAppTypeMation(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void queryLightAppTypeMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editLightAppTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void editLightAppTypeSortTopById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editLightAppTypeSortLowerById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void deleteLightAppTypeById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void editLightAppTypeUpTypeById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void editLightAppTypeDownTypeById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void queryLightAppTypeUpList(InputObject inputObject, OutputObject outputObject) throws Exception;

}
