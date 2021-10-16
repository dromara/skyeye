/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface MaterialCategoryService {

	public void queryMaterialCategoryList(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void insertMaterialCategoryMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteMaterialCategoryById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void selectMaterialCategoryToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editMaterialCategoryMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editMaterialCategoryMationOrderNumUpById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editMaterialCategoryMationOrderNumDownById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryMaterialCategoryListToTree(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryMaterialCategoryListToTreeZtr(InputObject inputObject, OutputObject outputObject) throws Exception;

}
