/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.SysEnclosureService;

@Controller
public class SysEnclosureController {
	
	@Autowired
	private SysEnclosureService sysEnclosureService;
	
	/**
	 * 
	     * @Title: querySysEnclosureListByUserId
	     * @Description: 获取我的附件分类
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEnclosureController/querySysEnclosureListByUserId")
	@ResponseBody
	public void querySysEnclosureListByUserId(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEnclosureService.querySysEnclosureListByUserId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertSysEnclosureMationByUserId
	     * @Description: 新增我的附件分类
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEnclosureController/insertSysEnclosureMationByUserId")
	@ResponseBody
	public void insertSysEnclosureMationByUserId(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEnclosureService.insertSysEnclosureMationByUserId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySysEnclosureFirstTypeListByUserId
	     * @Description: 获取我的附件一级分类
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEnclosureController/querySysEnclosureFirstTypeListByUserId")
	@ResponseBody
	public void querySysEnclosureFirstTypeListByUserId(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEnclosureService.querySysEnclosureFirstTypeListByUserId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryThisFolderChilsByFolderId
	     * @Description: 获取指定文件夹下的文件夹和文件
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEnclosureController/queryThisFolderChilsByFolderId")
	@ResponseBody
	public void queryThisFolderChilsByFolderId(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEnclosureService.queryThisFolderChilsByFolderId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySysEnclosureMationByUserIdToEdit
	     * @Description: 编辑我的附件分类时进行回显
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEnclosureController/querySysEnclosureMationByUserIdToEdit")
	@ResponseBody
	public void querySysEnclosureMationByUserIdToEdit(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEnclosureService.querySysEnclosureMationByUserIdToEdit(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editSysEnclosureMationByUserId
	     * @Description: 编辑我的附件分类/附件
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEnclosureController/editSysEnclosureMationByUserId")
	@ResponseBody
	public void editSysEnclosureMationByUserId(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEnclosureService.editSysEnclosureMationByUserId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertUploadFileByUserId
	     * @Description: 上传文件
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEnclosureController/insertUploadFileByUserId")
	@ResponseBody
	public void insertUploadFileByUserId(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEnclosureService.insertUploadFileByUserId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertUploadFileChunksByUserId
	     * @Description: 上传文件合并块
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEnclosureController/insertUploadFileChunksByUserId")
	@ResponseBody
	public void insertUploadFileChunksByUserId(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEnclosureService.insertUploadFileChunksByUserId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryUploadFileChunksByChunkMd5
	     * @Description: 文件分块上传检测是否上传
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEnclosureController/queryUploadFileChunksByChunkMd5")
	@ResponseBody
	public void queryUploadFileChunksByChunkMd5(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEnclosureService.queryUploadFileChunksByChunkMd5(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySysEnclosureListToTreeByUserId
	     * @Description: 获取我的附件库
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEnclosureController/querySysEnclosureListToTreeByUserId")
	@ResponseBody
	public void querySysEnclosureListToTreeByUserId(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEnclosureService.querySysEnclosureListToTreeByUserId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryAllPeopleToTree
	     * @Description: 人员选择获取所有公司和人
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEnclosureController/queryAllPeopleToTree")
	@ResponseBody
	public void queryAllPeopleToTree(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEnclosureService.queryAllPeopleToTree(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryCompanyPeopleToTreeByUserBelongCompany
	     * @Description: 人员选择根据当前用户所属公司获取这个公司的人
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEnclosureController/queryCompanyPeopleToTreeByUserBelongCompany")
	@ResponseBody
	public void queryCompanyPeopleToTreeByUserBelongCompany(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEnclosureService.queryCompanyPeopleToTreeByUserBelongCompany(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryDepartmentPeopleToTreeByUserBelongDepartment
	     * @Description: 人员选择根据当前用户所属公司获取这个公司部门展示的人
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEnclosureController/queryDepartmentPeopleToTreeByUserBelongDepartment")
	@ResponseBody
	public void queryDepartmentPeopleToTreeByUserBelongDepartment(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEnclosureService.queryDepartmentPeopleToTreeByUserBelongDepartment(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryJobPeopleToTreeByUserBelongJob
	     * @Description: 人员选择根据当前用户所属公司获取这个公司岗位展示的人
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEnclosureController/queryJobPeopleToTreeByUserBelongJob")
	@ResponseBody
	public void queryJobPeopleToTreeByUserBelongJob(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEnclosureService.queryJobPeopleToTreeByUserBelongJob(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySimpleDepPeopleToTreeByUserBelongSimpleDep
	     * @Description: 人员选择根据当前用户所属公司获取这个公司同级部门展示的人
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEnclosureController/querySimpleDepPeopleToTreeByUserBelongSimpleDep")
	@ResponseBody
	public void querySimpleDepPeopleToTreeByUserBelongSimpleDep(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEnclosureService.querySimpleDepPeopleToTreeByUserBelongSimpleDep(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryTalkGroupUserListByUserId
	     * @Description: 根据聊天组展示用户
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEnclosureController/queryTalkGroupUserListByUserId")
	@ResponseBody
	public void queryTalkGroupUserListByUserId(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEnclosureService.queryTalkGroupUserListByUserId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertUploadFileToDataByUserId
	     * @Description: 一次性上传附件
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEnclosureController/insertUploadFileToDataByUserId")
	@ResponseBody
	public void insertUploadFileToDataByUserId(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEnclosureService.insertUploadFileToDataByUserId(inputObject, outputObject);
	}
	
}
