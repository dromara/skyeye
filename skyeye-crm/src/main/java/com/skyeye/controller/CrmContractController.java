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
import com.skyeye.service.CrmContractService;

@Controller
public class CrmContractController {

	@Autowired
	private CrmContractService crmContractService;

	/**
	 *
	 * @Title: queryCrmContractList
	 * @Description: 获取全部合同管理列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/CrmContractController/queryCrmContractList")
	@ResponseBody
	public void queryCrmContractList(InputObject inputObject, OutputObject outputObject) throws Exception {
		crmContractService.queryCrmContractList(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: queryMyCrmContractList
	 * @Description: 获取我的合同管理列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/CrmContractController/queryMyCrmContractList")
	@ResponseBody
	public void queryMyCrmContractList(InputObject inputObject, OutputObject outputObject) throws Exception {
		crmContractService.queryMyCrmContractList(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: queryDepartmentListToChoose
	 * @Description: 获取部门列表用于下拉框选择
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/CrmContractController/queryDepartmentListToChoose")
	@ResponseBody
	public void queryDepartmentListToChoose(InputObject inputObject, OutputObject outputObject) throws Exception {
		crmContractService.queryDepartmentListToChoose(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: insertCrmContractMation
	 * @Description: 新增合同信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/CrmContractController/insertCrmContractMation")
	@ResponseBody
	public void insertCrmContractMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		crmContractService.insertCrmContractMation(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: queryCrmContractMationToDetail
	 * @Description: 根据id获取合同信息详情
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/CrmContractController/queryCrmContractMationToDetail")
	@ResponseBody
	public void queryCrmContractMationToDetail(InputObject inputObject, OutputObject outputObject) throws Exception {
		crmContractService.queryCrmContractMationToDetail(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: queryCrmContractMationById
	 * @Description: 获取合同信息进行回显
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/CrmContractController/queryCrmContractMationById")
	@ResponseBody
	public void queryCrmContractMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		crmContractService.queryCrmContractMationById(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: editCrmContractMationById
	 * @Description: 编辑合同信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/CrmContractController/editCrmContractMationById")
	@ResponseBody
	public void editCrmContractMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		crmContractService.editCrmContractMationById(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: editCrmContractMationToSave
	 * @Description: 编辑合同信息(已提交审核)
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/CrmContractController/editCrmContractMationToSave")
	@ResponseBody
	public void editCrmContractMationToSave(InputObject inputObject, OutputObject outputObject) throws Exception {
		crmContractService.editCrmContractMationToSave(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: queryCrmContractListToChoose
	 * @Description: 根据客户id获取合同管理列表用于下拉框选择
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/CrmContractController/queryCrmContractListToChoose")
	@ResponseBody
	public void queryCrmContractListToChoose(InputObject inputObject, OutputObject outputObject) throws Exception {
		crmContractService.queryCrmContractListToChoose(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: editCrmContractToSubApproval
	 * @Description: 合同提交审批
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/CrmContractController/editCrmContractToSubApproval")
	@ResponseBody
	public void editCrmContractToSubApproval(InputObject inputObject, OutputObject outputObject) throws Exception {
		crmContractService.editCrmContractToSubApproval(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: editCrmContractToPerform
	 * @Description: 合同执行
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/CrmContractController/editCrmContractToPerform")
	@ResponseBody
	public void editCrmContractToPerform(InputObject inputObject, OutputObject outputObject) throws Exception {
		crmContractService.editCrmContractToPerform(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: editCrmContractToClose
	 * @Description: 合同关闭
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/CrmContractController/editCrmContractToClose")
	@ResponseBody
	public void editCrmContractToClose(InputObject inputObject, OutputObject outputObject) throws Exception {
		crmContractService.editCrmContractToClose(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: editCrmContractToShelve
	 * @Description: 合同搁置
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/CrmContractController/editCrmContractToShelve")
	@ResponseBody
	public void editCrmContractToShelve(InputObject inputObject, OutputObject outputObject) throws Exception {
		crmContractService.editCrmContractToShelve(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: editCrmContractToRecovery
	 * @Description: 合同恢复
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/CrmContractController/editCrmContractToRecovery")
	@ResponseBody
	public void editCrmContractToRecovery(InputObject inputObject, OutputObject outputObject) throws Exception {
		crmContractService.editCrmContractToRecovery(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: deleteCrmContractById
	 * @Description: 删除合同信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/CrmContractController/deleteCrmContractById")
	@ResponseBody
	public void deleteCrmContractById(InputObject inputObject, OutputObject outputObject) throws Exception {
		crmContractService.deleteCrmContractById(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: editCrmContractToRevokeByProcessInstanceId
	 * @Description: 合同审批撤销
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/CrmContractController/editCrmContractToRevokeByProcessInstanceId")
	@ResponseBody
	public void editCrmContractToRevokeByProcessInstanceId(InputObject inputObject, OutputObject outputObject) throws Exception {
		crmContractService.editCrmContractToRevokeByProcessInstanceId(inputObject, outputObject);
	}

}
