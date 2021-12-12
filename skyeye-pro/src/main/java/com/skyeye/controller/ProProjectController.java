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
import com.skyeye.service.ProProjectService;

@Controller
public class ProProjectController {

	@Autowired
	private ProProjectService proProjectService;

	/**
	 *
	 * @Title: queryAllProProjectList
	 * @Description: 获取全部项目管理列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/ProProjectController/queryAllProProjectList")
	@ResponseBody
	public void queryAllProProjectList(InputObject inputObject, OutputObject outputObject) throws Exception {
		proProjectService.queryAllProProjectList(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: queryMyProProjectList
	 * @Description: 获取我的项目管理列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/ProProjectController/queryMyProProjectList")
	@ResponseBody
	public void queryMyProProjectList(InputObject inputObject, OutputObject outputObject) throws Exception {
		proProjectService.queryMyProProjectList(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: insertProProjectMation
	 * @Description: 新增项目信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/ProProjectController/insertProProjectMation")
	@ResponseBody
	public void insertProProjectMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		proProjectService.insertProProjectMation(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: queryAllProProjectToChoose
	 * @Description: 获取所有指定项目用于下拉框选择
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/ProProjectController/queryAllProProjectToChoose")
	@ResponseBody
	public void queryAllProProjectToChoose(InputObject inputObject, OutputObject outputObject) throws Exception {
		proProjectService.queryAllProProjectToChoose(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: queryProProjectMationToDetail
	 * @Description: 获取项目详细信息展示详情
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/ProProjectController/queryProProjectMationToDetail")
	@ResponseBody
	public void queryProProjectMationToDetail(InputObject inputObject, OutputObject outputObject) throws Exception {
		proProjectService.queryProProjectMationToDetail(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: queryProProjectMationToEdit
	 * @Description: 获取项目详细信息用于编辑回显
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/ProProjectController/queryProProjectMationToEdit")
	@ResponseBody
	public void queryProProjectMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception {
		proProjectService.queryProProjectMationToEdit(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: editProProjectMationById
	 * @Description: 编辑项目信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/ProProjectController/editProProjectMationById")
	@ResponseBody
	public void editProProjectMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		proProjectService.editProProjectMationById(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: editProProjectMationToSubApproval
	 * @Description: 项目提交审批
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/ProProjectController/editProProjectMationToSubApproval")
	@ResponseBody
	public void editProProjectMationToSubApproval(InputObject inputObject, OutputObject outputObject) throws Exception {
		proProjectService.editProProjectMationToSubApproval(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: deleteProProjectMationById
	 * @Description: 删除项目信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/ProProjectController/deleteProProjectMationById")
	@ResponseBody
	public void deleteProProjectMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		proProjectService.deleteProProjectMationById(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: editProjectProcessToRevoke
	 * @Description: 撤销项目审批申请
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/ProProjectController/editProjectProcessToRevoke")
	@ResponseBody
	public void editProjectProcessToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception {
		proProjectService.editProjectProcessToRevoke(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: editProjectProcessToNullify
	 * @Description: 作废项目
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/ProProjectController/editProjectProcessToNullify")
	@ResponseBody
	public void editProjectProcessToNullify(InputObject inputObject, OutputObject outputObject) throws Exception {
		proProjectService.editProjectProcessToNullify(inputObject, outputObject);
	}

	
	/**
	 *
	 * @Title: editProjectProcessToExecute
	 * @Description: 开始执行项目
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/ProProjectController/editProjectProcessToExecute")
	@ResponseBody
	public void editProjectProcessToExecute(InputObject inputObject, OutputObject outputObject) throws Exception {
		proProjectService.editProjectProcessToExecute(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: queryProjectProcessToProAppointShowById
	 * @Description: 项目任命数据回显
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/ProProjectController/queryProjectProcessToProAppointShowById")
	@ResponseBody
	public void queryProjectProcessToProAppointShowById(InputObject inputObject, OutputObject outputObject) throws Exception {
		proProjectService.queryProjectProcessToProAppointShowById(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: editProjectProcessToProAppointById
	 * @Description: 项目任命
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/ProProjectController/editProjectProcessToProAppointById")
	@ResponseBody
	public void editProjectProcessToProAppointById(InputObject inputObject, OutputObject outputObject) throws Exception {
		proProjectService.editProjectProcessToProAppointById(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: queryProjectProcessToPerFectShowById
	 * @Description: 信息完善数据回显
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/ProProjectController/queryProjectProcessToPerFectShowById")
	@ResponseBody
	public void queryProjectProcessToPerFectShowById(InputObject inputObject, OutputObject outputObject) throws Exception {
		proProjectService.queryProjectProcessToPerFectShowById(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: editProjectProcessToPerFectById
	 * @Description: 信息完善
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/ProProjectController/editProjectProcessToPerFectById")
	@ResponseBody
	public void editProjectProcessToPerFectById(InputObject inputObject, OutputObject outputObject) throws Exception {
		proProjectService.editProjectProcessToPerFectById(inputObject, outputObject);
	}

}
