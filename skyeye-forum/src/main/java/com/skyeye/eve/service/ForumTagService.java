/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface ForumTagService {

	public void queryForumTagList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertForumTagMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteForumTagById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void updateUpForumTagById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void updateDownForumTagById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void selectForumTagById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editForumTagMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editForumTagMationOrderNumUpById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editForumTagMationOrderNumDownById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryForumTagUpStateList(InputObject inputObject, OutputObject outputObject) throws Exception;

}
