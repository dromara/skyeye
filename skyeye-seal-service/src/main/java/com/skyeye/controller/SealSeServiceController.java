/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.service.SealSeServiceService;

@Controller
public class SealSeServiceController {

	@Autowired
	private SealSeServiceService sealSeServiceService;

	/**
	 *
	 * @Title: querySealSeServiceList
	 * @Description: 获取全部工单列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/SealSeServiceController/querySealSeServiceList")
	@ResponseBody
	public void querySealSeServiceList(InputObject inputObject, OutputObject outputObject) throws Exception {
		sealSeServiceService.querySealSeServiceList(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: querySealSeServiceWaitToWorkList
	 * @Description: 获取全部待派工的工单列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/SealSeServiceController/querySealSeServiceWaitToWorkList")
	@ResponseBody
	public void querySealSeServiceWaitToWorkList(InputObject inputObject, OutputObject outputObject) throws Exception {
		sealSeServiceService.querySealSeServiceWaitToWorkList(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: querySealSeServiceWaitToReceiveList
	 * @Description: 获取当前登录人待接单的列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/SealSeServiceController/querySealSeServiceWaitToReceiveList")
	@ResponseBody
	public void querySealSeServiceWaitToReceiveList(InputObject inputObject, OutputObject outputObject) throws Exception {
		sealSeServiceService.querySealSeServiceWaitToReceiveList(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: querySealSeServiceWaitToSignonList
	 * @Description: 获取当前登录人待签到的列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/SealSeServiceController/querySealSeServiceWaitToSignonList")
	@ResponseBody
	public void querySealSeServiceWaitToSignonList(InputObject inputObject, OutputObject outputObject) throws Exception {
		sealSeServiceService.querySealSeServiceWaitToSignonList(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: querySealSeServiceWaitToFinishList
	 * @Description: 获取当前登录人待完工的列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/SealSeServiceController/querySealSeServiceWaitToFinishList")
	@ResponseBody
	public void querySealSeServiceWaitToFinishList(InputObject inputObject, OutputObject outputObject) throws Exception {
		sealSeServiceService.querySealSeServiceWaitToFinishList(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: querySealSeServiceWaitToAssessmentList
	 * @Description: 获取当前登录人待评价的列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/SealSeServiceController/querySealSeServiceWaitToAssessmentList")
	@ResponseBody
	public void querySealSeServiceWaitToAssessmentList(InputObject inputObject, OutputObject outputObject) throws Exception {
		sealSeServiceService.querySealSeServiceWaitToAssessmentList(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: queryAllSealSeServiceWaitToAssessmentList
	 * @Description: 获取全部待评价的工单列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/SealSeServiceController/queryAllSealSeServiceWaitToAssessmentList")
	@ResponseBody
	public void queryAllSealSeServiceWaitToAssessmentList(InputObject inputObject, OutputObject outputObject) throws Exception {
		sealSeServiceService.queryAllSealSeServiceWaitToAssessmentList(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: queryAllSealSeServiceWaitToCheckList
	 * @Description: 获取全部待审核的工单列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/SealSeServiceController/queryAllSealSeServiceWaitToCheckList")
	@ResponseBody
	public void queryAllSealSeServiceWaitToCheckList(InputObject inputObject, OutputObject outputObject) throws Exception {
		sealSeServiceService.queryAllSealSeServiceWaitToCheckList(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: queryAllSealSeServiceFinishedList
	 * @Description: 获取已完成的工单列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/SealSeServiceController/queryAllSealSeServiceFinishedList")
	@ResponseBody
	public void queryAllSealSeServiceFinishedList(InputObject inputObject, OutputObject outputObject) throws Exception {
		sealSeServiceService.queryAllSealSeServiceFinishedList(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: querySealSeServiceTodetails
	 * @Description: 查询工单详情
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/SealSeServiceController/querySealSeServiceTodetails")
	@ResponseBody
	public void querySealSeServiceTodetails(InputObject inputObject, OutputObject outputObject) throws Exception {
		sealSeServiceService.querySealSeServiceTodetails(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: insertSealSeServiceMation
	 * @Description: 新增售后服务信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/SealSeServiceController/insertSealSeServiceMation")
	@ResponseBody
	public void insertSealSeServiceMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		sealSeServiceService.insertSealSeServiceMation(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: querySealSeServiceMationToEdit
	 * @Description: 根据id获取售后服务信息用于编辑回显
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/SealSeServiceController/querySealSeServiceMationToEdit")
	@ResponseBody
	public void querySealSeServiceMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception {
		sealSeServiceService.querySealSeServiceMationToEdit(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: editSealSeServiceMationById
	 * @Description: 编辑售后服务信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/SealSeServiceController/editSealSeServiceMationById")
	@ResponseBody
	public void editSealSeServiceMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		sealSeServiceService.editSealSeServiceMationById(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: querySealSeServiceWaitToWorkMation
	 * @Description: 派工时获取派工信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/SealSeServiceController/querySealSeServiceWaitToWorkMation")
	@ResponseBody
	public void querySealSeServiceWaitToWorkMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		sealSeServiceService.querySealSeServiceWaitToWorkMation(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: editSealSeServiceWaitToWorkMation
	 * @Description: 派工
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/SealSeServiceController/editSealSeServiceWaitToWorkMation")
	@ResponseBody
	public void editSealSeServiceWaitToWorkMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		sealSeServiceService.editSealSeServiceWaitToWorkMation(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: querySealSeServiceWaitToReceiveMation
	 * @Description: 获取接单信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/SealSeServiceController/querySealSeServiceWaitToReceiveMation")
	@ResponseBody
	public void querySealSeServiceWaitToReceiveMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		sealSeServiceService.querySealSeServiceWaitToReceiveMation(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: insertSealSeServiceWaitToReceiveMation
	 * @Description: 接单
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/SealSeServiceController/insertSealSeServiceWaitToReceiveMation")
	@ResponseBody
	public void insertSealSeServiceWaitToReceiveMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		sealSeServiceService.insertSealSeServiceWaitToReceiveMation(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: querySealSeServiceWaitToSignonMation
	 * @Description: 获取签到信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/SealSeServiceController/querySealSeServiceWaitToSignonMation")
	@ResponseBody
	public void querySealSeServiceWaitToSignonMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		sealSeServiceService.querySealSeServiceWaitToSignonMation(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: insertSealSeServiceWaitToSignonMation
	 * @Description: 签到
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/SealSeServiceController/insertSealSeServiceWaitToSignonMation")
	@ResponseBody
	public void insertSealSeServiceWaitToSignonMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		sealSeServiceService.insertSealSeServiceWaitToSignonMation(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: deleteSealSeServiceMationById
	 * @Description: 删除售后服务信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/SealSeServiceController/deleteSealSeServiceMationById")
	@ResponseBody
	public void deleteSealSeServiceMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		sealSeServiceService.deleteSealSeServiceMationById(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: insertSealSeServiceApplyMation
	 * @Description: 新增配件申领单
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/SealSeServiceController/insertSealSeServiceApplyMation")
	@ResponseBody
	public void insertSealSeServiceApplyMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		sealSeServiceService.insertSealSeServiceApplyMation(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: querySealSeServiceSignon
	 * @Description: 查询待完工状态的工单用于新增配件下拉选择
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/SealSeServiceController/querySealSeServiceSignon")
	@ResponseBody
	public void querySealSeServiceSignon(InputObject inputObject, OutputObject outputObject) throws Exception {
		sealSeServiceService.querySealSeServiceSignon(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: querySealSeServiceApplyList
	 * @Description: 查询当前登录人的申领单
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/SealSeServiceController/querySealSeServiceApplyList")
	@ResponseBody
	public void querySealSeServiceApplyList(InputObject inputObject, OutputObject outputObject) throws Exception {
		sealSeServiceService.querySealSeServiceApplyList(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: queryAllSealSeServiceApplyList
	 * @Description: 查询所有审批通过的申领单
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/SealSeServiceController/queryAllSealSeServiceApplyList")
	@ResponseBody
	public void queryAllSealSeServiceApplyList(InputObject inputObject, OutputObject outputObject) throws Exception {
		sealSeServiceService.queryAllSealSeServiceApplyList(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: deleteSealSeServiceApplyById
	 * @Description: 删除配件申领单
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/SealSeServiceController/deleteSealSeServiceApplyById")
	@ResponseBody
	public void deleteSealSeServiceApplyById(InputObject inputObject, OutputObject outputObject) throws Exception {
		sealSeServiceService.deleteSealSeServiceApplyById(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: querySealSeServiceApplyToEdit
	 * @Description: 获取配件申领单信息用于编辑
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/SealSeServiceController/querySealSeServiceApplyToEdit")
	@ResponseBody
	public void querySealSeServiceApplyToEdit(InputObject inputObject, OutputObject outputObject) throws Exception {
		sealSeServiceService.querySealSeServiceApplyToEdit(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: editSealSeServiceApplyMation
	 * @Description: 编辑配件申领单
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/SealSeServiceController/editSealSeServiceApplyMation")
	@ResponseBody
	public void editSealSeServiceApplyMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		sealSeServiceService.editSealSeServiceApplyMation(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: querySealSeServiceApplyToDetail
	 * @Description: 获取配件申领单详情
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/SealSeServiceController/querySealSeServiceApplyToDetail")
	@ResponseBody
	public void querySealSeServiceApplyToDetail(InputObject inputObject, OutputObject outputObject) throws Exception {
		sealSeServiceService.querySealSeServiceApplyToDetail(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: queryAllSealSeServiceApplyWaitToCheckList
	 * @Description: 查询所有待审核的申领单
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/SealSeServiceController/queryAllSealSeServiceApplyWaitToCheckList")
	@ResponseBody
	public void queryAllSealSeServiceApplyWaitToCheckList(InputObject inputObject, OutputObject outputObject) throws Exception {
		sealSeServiceService.queryAllSealSeServiceApplyWaitToCheckList(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: editSealSeServiceApplyToCheckList
	 * @Description: 审核申领单
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/SealSeServiceController/editSealSeServiceApplyToCheckList")
	@ResponseBody
	public void editSealSeServiceApplyToCheckList(InputObject inputObject, OutputObject outputObject) throws Exception {
		sealSeServiceService.editSealSeServiceApplyToCheckList(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: querySealSeServiceWaitToFinishedMation
	 * @Description: 工单完成操作时信息回显
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/SealSeServiceController/querySealSeServiceWaitToFinishedMation")
	@ResponseBody
	public void querySealSeServiceWaitToFinishedMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		sealSeServiceService.querySealSeServiceWaitToFinishedMation(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: queryMyPartsNumByMUnitId
	 * @Description: 根据配件规格id获取库存
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/SealSeServiceController/queryMyPartsNumByMUnitId")
	@ResponseBody
	public void queryMyPartsNumByMUnitId(InputObject inputObject, OutputObject outputObject) throws Exception {
		sealSeServiceService.queryMyPartsNumByMUnitId(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: editServiceToComplateByServiceId
	 * @Description: 工单完工操作
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/SealSeServiceController/editServiceToComplateByServiceId")
	@ResponseBody
	public void editServiceToComplateByServiceId(InputObject inputObject, OutputObject outputObject) throws Exception {
		sealSeServiceService.editServiceToComplateByServiceId(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: querySealSeServiceWaitToEvaluateMation
	 * @Description: 评价时获取展示信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/SealSeServiceController/querySealSeServiceWaitToEvaluateMation")
	@ResponseBody
	public void querySealSeServiceWaitToEvaluateMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		sealSeServiceService.querySealSeServiceWaitToEvaluateMation(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: editSealSeServiceToEvaluateMationByServiceId
	 * @Description: 人工评价（后台用户评价）
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/SealSeServiceController/editSealSeServiceToEvaluateMationByServiceId")
	@ResponseBody
	public void editSealSeServiceToEvaluateMationByServiceId(InputObject inputObject, OutputObject outputObject) throws Exception {
		sealSeServiceService.editSealSeServiceToEvaluateMationByServiceId(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: editSealSeServiceToFinishedMationByServiceId
	 * @Description: 完工审核操作
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/SealSeServiceController/editSealSeServiceToFinishedMationByServiceId")
	@ResponseBody
	public void editSealSeServiceToFinishedMationByServiceId(InputObject inputObject, OutputObject outputObject) throws Exception {
		sealSeServiceService.editSealSeServiceToFinishedMationByServiceId(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: querySealSeServiceList
	 * @Description: 情况反馈信息展示
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/SealSeServiceController/querySealSeServiceFeedBackMationByServiceId")
	@ResponseBody
	public void querySealSeServiceFeedBackMationByServiceId(InputObject inputObject, OutputObject outputObject) throws Exception {
		sealSeServiceService.querySealSeServiceFeedBackMationByServiceId(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: querySealSeServiceMyWriteList
	 * @Description: 获取当前登录人填报的工单列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/SealSeServiceController/querySealSeServiceMyWriteList")
	@ResponseBody
	public void querySealSeServiceMyWriteList(InputObject inputObject, OutputObject outputObject) throws Exception {
		sealSeServiceService.querySealSeServiceMyWriteList(inputObject, outputObject);
	}

}
