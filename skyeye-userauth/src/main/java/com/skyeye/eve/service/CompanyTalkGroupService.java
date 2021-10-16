/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface CompanyTalkGroupService {

	public void insertGroupMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryGroupInvitationMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editAgreeInGroupInvitationMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editRefuseInGroupInvitationMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryGroupMationList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertGroupMationToTalk(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryGroupMemberByGroupId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryChatLogByType(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editUserToExitGroup(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editCreateToExitGroup(InputObject inputObject, OutputObject outputObject) throws Exception;

}
