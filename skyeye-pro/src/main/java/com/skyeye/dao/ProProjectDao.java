/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: ProProjectDao
 * @Description: 项目管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/5 20:22
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ProProjectDao {

    public List<Map<String, Object>> queryAllProProjectList(Map<String, Object> map) throws Exception;
    
    public List<Map<String, Object>> queryMyProProjectList(Map<String, Object> map) throws Exception;

	/**
	 * 新增项目信息
	 *
	 * @param map 项目信息
	 * @return
	 * @throws Exception
	 */
	public int insertProProjectMation(Map<String, Object> map) throws Exception;
    
    public List<Map<String, Object>> queryAllProProjectToChoose(Map<String, Object> map) throws Exception;
    
    public List<Map<String, Object>> queryProProjectMationByNameAndNum(Map<String, Object> map) throws Exception;

	/**
	 * 根据项目id获取项目详情信息
	 *
	 * @param proId 项目id
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> queryProProjectMationToDetail(@Param("id") String proId) throws Exception;
    
    public Map<String, Object> queryProProjectMationToEdit(Map<String, Object> map) throws Exception;

	/**
	 * 编辑项目信息
	 *
	 * @param map 项目信息
	 * @return
	 * @throws Exception
	 */
	public int editProProjectMationById(Map<String, Object> map) throws Exception;
    
    public List<Map<String, Object>> queryProProjectMationByNameAndId(Map<String, Object> map) throws Exception;
    
    public Map<String, Object> queryProProjectStateById(Map<String, Object> map) throws Exception;
    
    public int updateProProjectStateISInAudit(@Param("id") String id) throws Exception;
    
    public int deleteProProjectRelationByProId(@Param("proId") String proId) throws Exception;
    
    public int insertProProjectRelationMation(Map<String, Object> map) throws Exception;

	public int deleteProProjectMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryProjectId(Map<String, Object> map) throws Exception;

	public int editProjectProcessToRevoke(Map<String, Object> map) throws Exception;

	public int deleteProjectProcessToRevoke(Map<String, Object> map) throws Exception;

	public int editProjectProcessToNullify(Map<String, Object> map) throws Exception;

	/**
	 * 根据流程实例id获取项目的id
	 *
	 * @param processInstanceId 流程实例id
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> queryProProjectByProcessInstanceId(@Param("processInstanceId") String processInstanceId) throws Exception;

	public int editProProjectToPassByUseId(Map<String, Object> map) throws Exception;

	public int editProProjectToNoPassByUseId(Map<String, Object> map) throws Exception;

	public int editProProjectStateResultById(Map<String, Object> map) throws Exception;

	public int editProjectProcessToExecute(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryProjectProcessToProAppointShowById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryProjectManagerInfo(Map<String, Object> bean) throws Exception;

	public List<Map<String, Object>> queryProjectSponsorInfo(Map<String, Object> bean) throws Exception;

	public List<Map<String, Object>> queryProjectMembersInfo(Map<String, Object> bean) throws Exception;

	public int editProjectProcessToProAppointById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryProjectProcessToPerFectShowById(Map<String, Object> map) throws Exception;

	public int editProjectProcessToPerFectById(Map<String, Object> map) throws Exception;
    
}
