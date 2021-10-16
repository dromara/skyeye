/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface MyNoteDao {
	
	public int insertFileFolderByUserId(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryFileFolderByUserIdAndParentId(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> queryFolderParentByFolderId(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> quertFolderParentById(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> queryFolderMationById(@Param("folderId") String folderId) throws Exception;
	
	public int deleteFileFolderById(Map<String, Object> map) throws Exception;
	
	public int deleteFilesByFolderId(Map<String, Object> map) throws Exception;
	
	public int deleteFolderChildByFolderId(Map<String, Object> map) throws Exception;
	
	public int deleteNoteContentById(Map<String, Object> map) throws Exception;
	
	public int editFileFolderById(Map<String, Object> map) throws Exception;
	
	public int editNoteContentNameById(Map<String, Object> map) throws Exception;
	
	public List<Map<String, Object>> queryMyNoteListNewByUserId(Map<String, Object> map) throws Exception;
	
	public int insertMyNoteContentByUserId(Map<String, Object> map) throws Exception;
	
	public List<Map<String, Object>> queryFileAndContentListByFolderId(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> queryMyNoteContentMationById(Map<String, Object> map) throws Exception;
	
	public int editMyNoteContentById(Map<String, Object> map) throws Exception;
	
	public List<Map<String, Object>> queryFileFolderListByList(List<Map<String, Object>> folderBeans) throws Exception;
	
	public int deleteFileFolderListByList(List<Map<String, Object>> folderBeans) throws Exception;
	
	public List<Map<String, Object>> queryFileListByList(List<Map<String, Object>> folderNew) throws Exception;
	
	public int deleteFileListByList(List<Map<String, Object>> folderNew) throws Exception;
	
	public int insertFileFolderListByList(List<Map<String, Object>> folderNew) throws Exception;

	public int insertFileListByList(List<Map<String, Object>> fileNew) throws Exception;
	
	public List<Map<String, Object>> queryTreeToMoveByUserId(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> queryShareNoteById(@Param("fileId") String fileId) throws Exception;

	public List<Map<String, Object>> queryAllFolderListByFolderId(@Param("folderId") String folderId) throws Exception;

	public List<Map<String, Object>> queryAllFileListByFolderList(List<Map<String, Object>> beans) throws Exception;

	public int editNoteToMoveById(@Param("fileId") String fileId, @Param("parentId") String parentId, @Param("userId") String userId) throws Exception;

}
