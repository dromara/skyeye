/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface DsFormPageService {

	public void queryDsFormPageList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertDsFormPageMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertDsFormPageContent(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void selectFormPageContentByPageId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteDsFormPageById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void selectDsFormPageById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editDsFormPageMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryAllDsFormPageContent(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editDsFormPageContentByPageId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryInterfaceIsTrueOrNot(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryInterfaceValue(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryDsFormContentListByPageId(InputObject inputObject, OutputObject outputObject) throws Exception;

}
