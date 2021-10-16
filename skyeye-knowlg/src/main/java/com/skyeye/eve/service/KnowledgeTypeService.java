/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface KnowledgeTypeService {
	
	public void queryKnowledgeTypeList(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void insertKnowledgeTypeMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteKnowledgeTypeById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void updateUpKnowledgeTypeById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void updateDownKnowledgeTypeById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void selectKnowledgeTypeById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editKnowledgeTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryUpKnowledgeTypeTreeMation(InputObject inputObject, OutputObject outputObject) throws Exception;
}
