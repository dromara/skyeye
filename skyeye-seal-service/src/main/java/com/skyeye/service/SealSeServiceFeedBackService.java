/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface SealSeServiceFeedBackService {

	public void queryFeedBackList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySealServiceMationToFeedBack(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertFeedBackMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryFeedBackMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editFeedBackMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteFeedBackMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryFeedBackDetailsMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

}
