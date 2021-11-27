/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface IfsAccountSubjectService {

	public void queryIfsAccountSubjectList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertIfsAccountSubjectMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteIfsAccountSubjectById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void selectIfsAccountSubjectById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editIfsAccountSubjectMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	}
