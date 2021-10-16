/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: ProFileDao
 * @Description: 项目文档管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 18:54
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ProFileDao {

	public List<Map<String, Object>> queryProFileList(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryProFileByTitle(Map<String, Object> map) throws Exception;

	public int insertProFileMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryProFileMationById(@Param("id") String id) throws Exception;

	public Map<String, Object> queryProFileMationToEdit(Map<String, Object> map) throws Exception;

	public int editProFileMation(Map<String, Object> map) throws Exception;

	public int updateProFileStateISInAudit(@Param("id") String id) throws Exception;

	public int deleteProFileProcessById(@Param("id") String id) throws Exception;

	public int insertProFileProcess(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryFileIdByProcessInstanceId(@Param("processInstanceId") String processInstanceId) throws Exception;

	public int editProFileStateById(Map<String, Object> map) throws Exception;

	public int editProFileProcessStateById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryProFileId(Map<String, Object> map) throws Exception;

	public int editProFileProcessToRevoke(Map<String, Object> map) throws Exception;

	public int deleteProFileProcessToRevoke(Map<String, Object> map) throws Exception;

	public int deleteProFileMationById(Map<String, Object> map) throws Exception;

	public int updateProFileToCancellation(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryAllProFileList(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryAllStateupProFileByProId(Map<String, Object> map) throws Exception;

}
