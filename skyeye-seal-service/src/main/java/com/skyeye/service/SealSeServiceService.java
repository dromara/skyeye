/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface SealSeServiceService {

	public void querySealSeServiceList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySealSeServiceWaitToWorkList(InputObject inputObject,OutputObject outputObject) throws Exception;

	public void querySealSeServiceWaitToReceiveList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySealSeServiceWaitToSignonList(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void querySealSeServiceWaitToFinishList(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void querySealSeServiceWaitToAssessmentList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryAllSealSeServiceWaitToAssessmentList(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void queryAllSealSeServiceWaitToCheckList(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void queryAllSealSeServiceFinishedList(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void querySealSeServiceTodetails(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertSealSeServiceMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySealSeServiceMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySealSeServiceWaitToWorkMation(InputObject inputObject,	OutputObject outputObject) throws Exception;

	public void editSealSeServiceWaitToWorkMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSealSeServiceMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySealSeServiceWaitToReceiveMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertSealSeServiceWaitToReceiveMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySealSeServiceWaitToSignonMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertSealSeServiceWaitToSignonMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteSealSeServiceMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertSealSeServiceApplyMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySealSeServiceSignon(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySealSeServiceApplyList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryAllSealSeServiceApplyList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteSealSeServiceApplyById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySealSeServiceApplyToEdit(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSealSeServiceApplyMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySealSeServiceApplyToDetail(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryAllSealSeServiceApplyWaitToCheckList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSealSeServiceApplyToCheckList(InputObject inputObject,OutputObject outputObject) throws Exception;

	public void querySealSeServiceWaitToFinishedMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryMyPartsNumByMUnitId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editServiceToComplateByServiceId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySealSeServiceWaitToEvaluateMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSealSeServiceToEvaluateMationByServiceId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSealSeServiceToFinishedMationByServiceId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySealSeServiceFeedBackMationByServiceId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySealSeServiceMyWriteList(InputObject inputObject, OutputObject outputObject) throws Exception;
}
