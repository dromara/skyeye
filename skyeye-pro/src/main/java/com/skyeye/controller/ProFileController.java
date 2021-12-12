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
import com.skyeye.service.ProFileService;

/**
 *
 * @ClassName: ProFileController
 * @Description: 项目文档管理控制类
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/10 21:12
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Controller
public class ProFileController {

    @Autowired
    private ProFileService proFileService;
    
    /**
     *
     * @Title: queryProFileList
     * @Description: 获取项目的文档的列表
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ProFileController/queryProFileList")
    @ResponseBody
    public void queryProFileList(InputObject inputObject, OutputObject outputObject) throws Exception{
    	proFileService.queryProFileList(inputObject, outputObject);
    }

    /**
     *
     * @Title: insertProFileMation
     * @Description: 新增文档
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ProFileController/insertProFileMation")
    @ResponseBody
    public void insertProFileMation(InputObject inputObject, OutputObject outputObject) throws Exception {
    	proFileService.insertProFileMation(inputObject, outputObject);
    }
   
	/**
	*
	* @Title: queryProFileMationToDetails
	* @Description: 文档详情
	* @param inputObject
	* @param outputObject
	* @throws Exception    参数
	* @return void    返回类型
	* @throws
	*/
	@RequestMapping("/post/ProFileController/queryProFileMationToDetails")
	@ResponseBody
	public void queryProFileMationToDetails(InputObject inputObject, OutputObject outputObject) throws Exception {
		proFileService.queryProFileMationToDetails(inputObject, outputObject);
	}
	
	/**
	*
	* @Title: queryProFileMationToEdit
	* @Description: 获取文档信息用以编辑
	* @param inputObject
	* @param outputObject
	* @throws Exception    参数
	* @return void    返回类型
	* @throws
	*/
	@RequestMapping("/post/ProFileController/queryProFileMationToEdit")
	@ResponseBody
	public void queryProFileMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception {
		proFileService.queryProFileMationToEdit(inputObject, outputObject);
	}

	/**
	 *
	 * @Title: editProFileMation
	 * @Description: 编辑文档信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    参数
	 * @return void    返回类型
	 * @throws
	 */
	@RequestMapping("/post/ProFileController/editProFileMation")
	@ResponseBody
	public void editProFileMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		proFileService.editProFileMation(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: editProFileToApprovalById
	 * @Description: 提交审批
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    参数
	 * @return void    返回类型
	 * @throws
	 */
	@RequestMapping("/post/ProFileController/editProFileToApprovalById")
	@ResponseBody
	public void editProFileToApprovalById(InputObject inputObject, OutputObject outputObject) throws Exception {
		proFileService.editProFileToApprovalById(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: editProFileProcessToRevoke
	 * @Description: 撤销文档审批申请
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    参数
	 * @return void    返回类型
	 * @throws
	 */
	@RequestMapping("/post/ProFileController/editProFileProcessToRevoke")
	@ResponseBody
	public void editProFileProcessToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception {
		proFileService.editProFileProcessToRevoke(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: deleteProFileMationById
	 * @Description: 删除文档
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    参数
	 * @return void    返回类型
	 * @throws
	 */
	@RequestMapping("/post/ProFileController/deleteProFileMationById")
	@ResponseBody
	public void deleteProFileMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		proFileService.deleteProFileMationById(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: updateProFileToCancellation
	 * @Description: 作废文档
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    参数
	 * @return void    返回类型
	 * @throws
	 */
	@RequestMapping("/post/ProFileController/updateProFileToCancellation")
	@ResponseBody
	public void updateProFileToCancellation(InputObject inputObject, OutputObject outputObject) throws Exception {
		proFileService.updateProFileToCancellation(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: queryAllProFileList
	 * @Description: 获取全部的 项目文档的列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    参数
	 * @return void    返回类型
	 * @throws
	 */
	@RequestMapping("/post/ProFileController/queryAllProFileList")
	@ResponseBody
	public void queryAllProFileList(InputObject inputObject, OutputObject outputObject) throws Exception {
		proFileService.queryAllProFileList(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: queryAllStateupProFileByProId
	 * @Description: 获取某个项目下的所有已经审核通过的文档
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    参数
	 * @return void    返回类型
	 * @throws
	 */
	@RequestMapping("/post/ProFileController/queryAllStateupProFileByProId")
	@ResponseBody
	public void queryAllStateupProFileByProId(InputObject inputObject, OutputObject outputObject) throws Exception {
		proFileService.queryAllStateupProFileByProId(inputObject, outputObject);
	}
}
