/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface FileConsoleDao {

	public int insertFileFolderByUserId(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryFileFolderByUserIdAndParentId(Map<String, Object> map) throws Exception;
	
	public List<Map<String, Object>> queryFilesListByFolderId(Map<String, Object> map) throws Exception;

	public int deleteFileFolderById(String id) throws Exception;

	public int editFileFolderById(Map<String, Object> map) throws Exception;
	
	public int insertUploadFileByUserId(Map<String, Object> map) throws Exception;

	public int editFilePaperNameById(Map<String, Object> map) throws Exception;

	public int deleteFilePaperById(String id) throws Exception;

	public Map<String, Object> queryFilePaperPathById(String id) throws Exception;

	public Map<String, Object> queryUploadFileChunksByChunkMd5(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryUploadFileChunksByMd5(Map<String, Object> map) throws Exception;

	public int deleteUploadFileChunksByMd5(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> queryUploadFilePathById(Map<String, Object> map) throws Exception;

	/**
	 * 根据文件夹id获取文件夹信息
	 *
	 * @param folderId 文件夹id
	 * @return 文件夹信息
	 * @throws Exception
	 */
	public Map<String, Object> queryFolderParentByFolderId(@Param("folderId") String folderId) throws Exception;

	/**
	 * 根据文件夹id获取该文件夹下的所有文件（包含子文件夹中的文件）
	 *
	 * @param id 文件夹id
	 * @return 该文件夹下的所有文件
	 * @throws Exception
	 */
	public List<Map<String, Object>> queryFilesByFolderId(@Param("id") String id) throws Exception;

	public int deleteFilesByFolderId(String id) throws Exception;

	public int deleteFolderChildByFolderId(String id) throws Exception;

	public int deleteUploadFileChunksByChunkMd5(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> queryUploadFilePathByKey(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> queryAllFileSizeByUserId(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> queryFileCatalogByUserIdAndId(Map<String, Object> map) throws Exception;

	public int insertFileCatalogToRecycleById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryFileRecycleBinByUserId(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryFileRecycleBinById(Map<String, Object> map) throws Exception;

	public int deleteFileRecycleBinById(Map<String, Object> map) throws Exception;

	public int updateFileFolderRecycleBinById(Map<String, Object> map) throws Exception;

	public int updateFileRecycleBinById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryThisFileCreaterByFileId(String id) throws Exception;

	public int updateFileStateIsDeleteById(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> queryFileMationByIdAndUserId(Map<String, Object> map) throws Exception;

	public int insertFileToShareById(Map<String, Object> map) throws Exception;
	
	public List<Map<String, Object>> queryShareFileListByUserId(Map<String, Object> map) throws Exception;

	public int deleteShareFileById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryShareFileMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryShareFileMationCheckById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryShareFileBaseMationById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryShareFileFirstListByParentId(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryShareFileListByParentId(Map<String, Object> map) throws Exception;
	
	public List<Map<String, Object>> queryShareFileFolderListByList(List<Map<String, Object>> folderBeans) throws Exception;

	public List<Map<String, Object>> queryShareFileListByList(List<Map<String, Object>> folderNew) throws Exception;

	public int insertShareFileFolderListByList(List<Map<String, Object>> folderNew) throws Exception;

	public int insertShareFileListByList(List<Map<String, Object>> fileNew) throws Exception;
	
	public List<Map<String, Object>> queryShareFileListByFileList(List<Map<String, Object>> fileBeans) throws Exception;
	
	public Map<String, Object> quertWinFileOrFolderParentById(String id) throws Exception;
	
	public Map<String, Object> queryFileToShowById(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> queryFileMationById(Map<String, Object> map) throws Exception;
	
	public List<Map<String, Object>> queryToPackageFileFolderListByList(List<Map<String, Object>> folderBeans) throws Exception;

	public List<Map<String, Object>> queryToPackageFileListByList(List<Map<String, Object>> folderNew) throws Exception;

	public List<Map<String, Object>> queryToPackageFileListByFileList(List<Map<String, Object>> fileBeans) throws Exception;
	
	public Map<String, Object> queryFilePackageMationById(Map<String, Object> map) throws Exception;

	public int insertFolderByPackageAndUserId(List<Map<String, Object>> folderList) throws Exception;

	public int insertFileByPackageAndUserId(List<Map<String, Object>> fileList) throws Exception;
	
	public int deleteShareFileFolderListByList(List<Map<String, Object>> folderBeans) throws Exception;

	public int deleteShareFileListByList(List<Map<String, Object>> folderNew) throws Exception;

	public int deleteShareFileListByFileList(List<Map<String, Object>> fileBeans) throws Exception;
	
	public int editFileUpdateTimeByKey(Map<String, Object> map) throws Exception;
	
    public Map<String, Object> queryOfficeUpdateTimeToKey(Map<String, Object> map) throws Exception;
    
    public Map<String, Object> queryThisFolderParentIdByRowId(Map<String, Object> bean) throws Exception;

	public Map<String, Object> queryAllNumFile(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryAllNumFileToday(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryAllNumFileThisWeek(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryFileTypeNum(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryFileStorageNum(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryNewFileNum(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryFileTypeNumSevenDay(Map<String, Object> map) throws Exception;
	
}
