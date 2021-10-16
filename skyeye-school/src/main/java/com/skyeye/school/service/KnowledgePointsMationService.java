/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.school.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface KnowledgePointsMationService {

	public void queryKnowledgePointsMationList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertKnowledgePointsMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteKnowledgePointsMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryKnowledgePointsMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editKnowledgePointsMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryKnowledgePointsMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryKnowledgePointsMationListToTable(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryKnowledgePointsMationListByIds(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryKnowledgePointsMationBankList(InputObject inputObject, OutputObject outputObject) throws Exception;

}
