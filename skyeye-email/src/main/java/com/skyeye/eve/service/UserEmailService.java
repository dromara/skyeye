/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface UserEmailService {

	public void queryEmailListByUserId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertEmailListByUserId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertEmailListFromServiceByUserId(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void queryInboxEmailListByEmailId(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void queryEmailMationByEmailId(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void insertSendedEmailListFromServiceByUserId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySendedEmailListByEmailId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertDelsteEmailListFromServiceByUserId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryDeleteEmailListByEmailId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertDraftsEmailListFromServiceByUserId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryDraftsEmailListByEmailId(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void insertToSendEmailMationByUserId(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void insertToDraftsEmailMationByUserId(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void queryDraftsEmailMationToEditByUserId(InputObject inputObject, OutputObject outputObject) throws Exception;
	
    public void editToDraftsEmailMationByUserId(InputObject inputObject, OutputObject outputObject) throws Exception;
    
    public void insertToSendEmailMationByEmailId(InputObject inputObject, OutputObject outputObject) throws Exception;
    
    public void queryForwardEmailMationToEditByUserId(InputObject inputObject, OutputObject outputObject) throws Exception;
    
    public void insertForwardToSendEmailMationByUserId(InputObject inputObject, OutputObject outputObject) throws Exception;
    
}
