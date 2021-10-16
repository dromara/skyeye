/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: SealSeServiceDao
 * @Description: 售后服务工单管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 22:43
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface SealSeServiceDao {

	public List<Map<String, Object>> querySealSeServiceList(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> querySealSeServiceWaitToWorkList(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> querySealSeServiceWaitToReceiveList(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> querySealSeServiceWaitToSignonList(Map<String, Object> map) throws Exception;
	
	public List<Map<String, Object>> querySealSeServiceWaitToFinishList(Map<String, Object> map) throws Exception;
	
	public List<Map<String, Object>> querySealSeServiceWaitToAssessmentList(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryAllSealSeServiceWaitToAssessmentList(Map<String, Object> map) throws Exception;
	
	public List<Map<String, Object>> queryAllSealSeServiceWaitToCheckList(Map<String, Object> map) throws Exception;
	
	public List<Map<String, Object>> queryAllSealSeServiceFinishedList(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySealSeServiceToDetails(Map<String, Object> map) throws Exception;

	public int insertSealSeServiceMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySealSeServiceMationToEdit(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySealSeServiceWaitToWorkMation(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryMaterialMationById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryFeedbackMationById(Map<String, Object> map) throws Exception;

	public int editSealSeServiceWaitToWorkMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySealSeServiceState(Map<String, Object> map) throws Exception;

	public int editSealSeServiceMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySealSeServiceWaitToReceiveMation(Map<String, Object> map) throws Exception;

	public int insertSealSeServiceWaitToReceiveMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySealSeServiceWaitToSignonMation(Map<String, Object> map) throws Exception;

	public int insertSealSeServiceWaitToSignonMation(Map<String, Object> map) throws Exception;

	public int editSealSeServiceWaitToReceiveMation(Map<String, Object> map) throws Exception;

	public int editSealSeServiceWaitToSignonMation(Map<String, Object> map) throws Exception;

	public int deleteSealSeServiceMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryMaterialsById(Map<String, Object> bean) throws Exception;

	public int insertSealSeServiceApplyMation(Map<String, Object> depothead) throws Exception;

	public int insertSealSeServiceApplyMaterial(List<Map<String, Object>> entitys) throws Exception;

	public List<Map<String, Object>> querySealSeServiceSignon(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> querySealSeServiceApplyList(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryAllSealSeServiceApplyList(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryMaterialsCurrentTockById(Map<String, Object> bean) throws Exception;

	public Map<String, Object> querySealSeServiceApplyState(Map<String, Object> map) throws Exception;

	public int deleteSealSeServiceApplyById(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySealSeServiceApplyToEdit(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryApplyMaterialMationById(Map<String, Object> map) throws Exception;

	public int editSealSeServiceApplyMation(Map<String, Object> depothead) throws Exception;

	public int deleteSealSeServiceApplyMaterial(Map<String, Object> depothead) throws Exception;

	public Map<String, Object> querySealSeServiceApplyToDetail(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryApplyMaterialMationToDetail(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryAllSealSeServiceApplyWaitToCheckList(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryApplyMaterialOtherMationById(Map<String, Object> map) throws Exception;

	public int editSealSeServiceApplyCheckMation(Map<String, Object> depothead) throws Exception;

	public int editSealSeServiceCheckMation(List<Map<String, Object>> entitys) throws Exception;

	public int insertErpDepotHeadByApplyId(@Param("id") String id, 
											@Param("headId") String headId, 
											@Param("orderNum") String orderNum) throws Exception;

	public List<Map<String, Object>> queryErpDepotItemMaterialMationByApplyId(@Param("id") String applyId) throws Exception;

	public int insertErpDepotItems(List<Map<String, Object>> entitys) throws Exception;

	public Map<String, Object> querySealSeServiceWaitToFinishedMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryMyPartsNumByMUnitId(Map<String, Object> map) throws Exception;

	public int insertSealSeServiceUseMaterial(List<Map<String, Object>> entitys) throws Exception;

	public int insertSealSeServiceFaultMation(Map<String, Object> fault) throws Exception;

	public int editSealSeServiceFaultMationById(Map<String, Object> fault) throws Exception;

	public int deleteSealSeServiceUseMaterialByServiceId(@Param("serviceId") String serviceId) throws Exception;

	public int editSealSeServiceToComplateMation(@Param("serviceId") String serviceId) throws Exception;

	public List<Map<String, Object>> querySealSeServiceUseMaterialByServiceId(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySealSeServiceWaitToEvaluateMation(Map<String, Object> map) throws Exception;

	public int editSealSeServiceToEvaluateMationByServiceId(Map<String, Object> map) throws Exception;

	public int insertEvaluateMation(Map<String, Object> map) throws Exception;

	public int editSealSeServiceToFinishedMationByServiceId(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySealSeServiceFeedBackMationByServiceId(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> querySealSeServiceMyWriteList(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryFaultMationByOrderId(@Param("serviceId") String serviceId);

}
