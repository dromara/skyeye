/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface SysEnclosureService {

	public void querySysEnclosureListByUserId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertSysEnclosureMationByUserId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySysEnclosureFirstTypeListByUserId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryThisFolderChilsByFolderId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySysEnclosureMationByUserIdToEdit(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSysEnclosureMationByUserId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertUploadFileByUserId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertUploadFileChunksByUserId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryUploadFileChunksByChunkMd5(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySysEnclosureListToTreeByUserId(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void queryAllPeopleToTree(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryCompanyPeopleToTreeByUserBelongCompany(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryDepartmentPeopleToTreeByUserBelongDepartment(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryJobPeopleToTreeByUserBelongJob(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySimpleDepPeopleToTreeByUserBelongSimpleDep(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryTalkGroupUserListByUserId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertUploadFileToDataByUserId(InputObject inputObject, OutputObject outputObject) throws Exception;
	
}
