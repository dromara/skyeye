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
import com.skyeye.service.ProCostExpenseService;

/**
 *
 * @ClassName: ProCostExpenseController
 * @Description: 项目费用报销管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/11 22:37
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Controller
public class ProCostExpenseController {

    @Autowired
    private ProCostExpenseService proCostExpenseService;
    
    /**
     *
     * @Title: queryProCostExpenseList
     * @Description: 获取项目的费用报销的列表
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ProCostExpenseController/queryProCostExpenseList")
    @ResponseBody
    public void queryProCostExpenseList(InputObject inputObject, OutputObject outputObject) throws Exception{
    	proCostExpenseService.queryProCostExpenseList(inputObject, outputObject);
    }

    /**
     *
     * @Title: insertProCostExpenseMation
     * @Description: 新增费用报销
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ProCostExpenseController/insertProCostExpenseMation")
    @ResponseBody
    public void insertProCostExpenseMation(InputObject inputObject, OutputObject outputObject) throws Exception {
    	proCostExpenseService.insertProCostExpenseMation(inputObject, outputObject);
    }
   
	/**
	*
	* @Title: queryProCostExpenseMationToDetails
	* @Description: 费用报销详情
	* @param inputObject
	* @param outputObject
	* @throws Exception    参数
	* @return void    返回类型
	* @throws
	*/
	@RequestMapping("/post/ProCostExpenseController/queryProCostExpenseMationToDetails")
	@ResponseBody
	public void queryProCostExpenseMationToDetails(InputObject inputObject, OutputObject outputObject) throws Exception {
		proCostExpenseService.queryProCostExpenseMationToDetails(inputObject, outputObject);
	}
	
	/**
	*
	* @Title: queryProCostExpenseMationToEdit
	* @Description: 获取费用报销信息用以编辑
	* @param inputObject
	* @param outputObject
	* @throws Exception    参数
	* @return void    返回类型
	* @throws
	*/
	@RequestMapping("/post/ProCostExpenseController/queryProCostExpenseMationToEdit")
	@ResponseBody
	public void queryProCostExpenseMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception {
		proCostExpenseService.queryProCostExpenseMationToEdit(inputObject, outputObject);
	}

	/**
	 *
	 * @Title: editProCostExpenseMation
	 * @Description: 编辑费用报销信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    参数
	 * @return void    返回类型
	 * @throws
	 */
	@RequestMapping("/post/ProCostExpenseController/editProCostExpenseMation")
	@ResponseBody
	public void editProCostExpenseMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		proCostExpenseService.editProCostExpenseMation(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: editProCostExpenseToApprovalById
	 * @Description: 提交审批
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    参数
	 * @return void    返回类型
	 * @throws
	 */
	@RequestMapping("/post/ProCostExpenseController/editProCostExpenseToApprovalById")
	@ResponseBody
	public void editProCostExpenseToApprovalById(InputObject inputObject, OutputObject outputObject) throws Exception {
		proCostExpenseService.editProCostExpenseToApprovalById(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: editProCostExpenseProcessToRevoke
	 * @Description: 撤销费用报销审批申请
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    参数
	 * @return void    返回类型
	 * @throws
	 */
	@RequestMapping("/post/ProCostExpenseController/editProCostExpenseProcessToRevoke")
	@ResponseBody
	public void editProCostExpenseProcessToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception {
		proCostExpenseService.editProCostExpenseProcessToRevoke(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: deleteProCostExpenseMationById
	 * @Description: 删除费用报销
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    参数
	 * @return void    返回类型
	 * @throws
	 */
	@RequestMapping("/post/ProCostExpenseController/deleteProCostExpenseMationById")
	@ResponseBody
	public void deleteProCostExpenseMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		proCostExpenseService.deleteProCostExpenseMationById(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: updateProCostExpenseToCancellation
	 * @Description: 作废费用报销
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    参数
	 * @return void    返回类型
	 * @throws
	 */
	@RequestMapping("/post/ProCostExpenseController/updateProCostExpenseToCancellation")
	@ResponseBody
	public void updateProCostExpenseToCancellation(InputObject inputObject, OutputObject outputObject) throws Exception {
		proCostExpenseService.updateProCostExpenseToCancellation(inputObject, outputObject);
	}

}
