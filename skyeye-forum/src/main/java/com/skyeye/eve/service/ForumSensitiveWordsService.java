/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface ForumSensitiveWordsService {

	public void queryForumSensitiveWordsList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertForumSensitiveWordsMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteForumSensitiveWordsById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void selectForumSensitiveWordsById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editForumSensitiveWordsMationById(InputObject inputObject, OutputObject outputObject) throws Exception;
}
