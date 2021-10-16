/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface FileConsoleService {

	public void queryFileFolderByUserId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertFileFolderByUserId(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void queryFilesListByFolderId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteFileFolderById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editFileFolderById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void insertUploadFileByUserId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertUploadFileChunksByUserId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryUploadFileChunksByChunkMd5(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryUploadFilePathById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void editUploadOfficeFileById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void queryAllFileSizeByUserId(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void insertFileCatalogToRecycleById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryFileRecycleBinByUserId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteFileRecycleBinById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void insertFileToShareById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void queryShareFileListByUserId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteShareFileById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryShareFileMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryShareFileMationCheckById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryShareFileBaseMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryShareFileListByParentId(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void insertShareFileListToSave(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void queryFileToShowById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void insertWordFileToService(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertExcelFileToService(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertPPTFileToService(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertTXTFileToService(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertHtmlFileToService(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void insertDuplicateCopyToService(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryFileMationById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void insertFileMationToPackageToFolder(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void insertFileMationPackageToFolder(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void insertPasteCopyToService(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertPasteCutToService(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void queryOfficeUpdateTimeToKey(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryFileNumStatistics(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertFileMationToPackageDownload(InputObject inputObject, OutputObject outputObject) throws Exception;
	
}
