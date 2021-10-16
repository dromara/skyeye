/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface MyNoteService {

	public void queryFileMyNoteByUserId(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void insertFileMyNoteByUserId(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void deleteFileFolderById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void editFileFolderById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void queryMyNoteListNewByUserId(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void insertMyNoteContentByUserId(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void queryFileAndContentListByFolderId(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void queryMyNoteContentMationById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void editMyNoteContentById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void editFileToDragById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void editNoteToMoveById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void queryTreeToMoveByUserId(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void queryShareNoteById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void outputNoteIsZipJob(InputObject inputObject, OutputObject outputObject) throws Exception;
	
}
