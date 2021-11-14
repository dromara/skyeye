/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

import java.util.List;
import java.util.Map;

public interface DsFormPageService {

	void queryDsFormPageList(InputObject inputObject, OutputObject outputObject) throws Exception;

	void insertDsFormPageMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	void insertDsFormPageContent(InputObject inputObject, OutputObject outputObject) throws Exception;

	void selectFormPageContentByPageId(InputObject inputObject, OutputObject outputObject) throws Exception;

	void deleteDsFormPageById(InputObject inputObject, OutputObject outputObject) throws Exception;

	void selectDsFormPageById(InputObject inputObject, OutputObject outputObject) throws Exception;

	void editDsFormPageMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	void queryAllDsFormPageContent(InputObject inputObject, OutputObject outputObject) throws Exception;

	void editDsFormPageContentByPageId(InputObject inputObject, OutputObject outputObject) throws Exception;

	void queryInterfaceIsTrueOrNot(InputObject inputObject, OutputObject outputObject) throws Exception;

	void queryInterfaceValue(InputObject inputObject, OutputObject outputObject) throws Exception;

	void queryDsFormContentListByPageId(InputObject inputObject, OutputObject outputObject) throws Exception;

	List<Map<String, Object>> getDsFormPageContentByFormId(String dsFormPageId) throws Exception;

	void queryDsFormContentListByCode(InputObject inputObject, OutputObject outputObject) throws Exception;
}
