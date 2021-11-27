/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface IfsSetOfBooksService {

	public void queryIfsSetOfBooksList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertIfsSetOfBooksMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteIfsSetOfBooksById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void selectIfsSetOfBooksById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editIfsSetOfBooksMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

}
