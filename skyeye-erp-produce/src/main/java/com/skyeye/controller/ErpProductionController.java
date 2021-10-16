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
import com.skyeye.service.ErpProductionService;

@Controller
public class ErpProductionController {

	@Autowired
	private ErpProductionService erpProductionService;
	
	/**
     * 查询生产计划单列表
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpProductionController/queryErpProductionList")
    @ResponseBody
    public void queryErpProductionList(InputObject inputObject, OutputObject outputObject) throws Exception{
    	erpProductionService.queryErpProductionList(inputObject, outputObject);
    }
    
    /**
     * 新增生产计划单
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpProductionController/insertErpProductionMation")
    @ResponseBody
    public void insertErpProductionMation(InputObject inputObject, OutputObject outputObject) throws Exception{
    	erpProductionService.insertErpProductionMation(inputObject, outputObject);
    }
    
    /**
     * 查询生产计划单信息用于编辑
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpProductionController/queryErpProductionMationToEditById")
    @ResponseBody
    public void queryErpProductionMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
    	erpProductionService.queryErpProductionMationToEditById(inputObject, outputObject);
    }
    
    /**
     * 编辑生产计划单信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpProductionController/editErpProductionMationById")
    @ResponseBody
    public void editErpProductionMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
    	erpProductionService.editErpProductionMationById(inputObject, outputObject);
    }
    
    /**
     * 删除生产计划单信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpProductionController/deleteErpProductionMationById")
    @ResponseBody
    public void deleteErpProductionMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
    	erpProductionService.deleteErpProductionMationById(inputObject, outputObject);
    }
    
    /**
     * 查询生产计划单信息详情
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpProductionController/queryErpProductionMationToDetailById")
    @ResponseBody
    public void queryErpProductionMationToDetailById(InputObject inputObject, OutputObject outputObject) throws Exception{
    	erpProductionService.queryErpProductionMationToDetailById(inputObject, outputObject);
    }
    
    /**
     * 提交审批申请
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpProductionController/submitApplicationForApproval")
    @ResponseBody
    public void submitApplicationForApproval(InputObject inputObject, OutputObject outputObject) throws Exception{
    	erpProductionService.submitApplicationForApproval(inputObject, outputObject);
    }
    
    /**
     * 生产计划单信息审核
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpProductionController/editErpProductionStateToExamineById")
    @ResponseBody
    public void editErpProductionStateToExamineById(InputObject inputObject, OutputObject outputObject) throws Exception{
    	erpProductionService.editErpProductionStateToExamineById(inputObject, outputObject);
    }
    
    /**
     * 查询生产计划单列表展示为表格供采购单,加工单选择
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpProductionController/queryErpProductionListToTable")
    @ResponseBody
    public void queryErpProductionListToTable(InputObject inputObject, OutputObject outputObject) throws Exception{
    	erpProductionService.queryErpProductionListToTable(inputObject, outputObject);
    }
    
    /**
     * 根据生产计划单id获取该单据下的所有外购商品以及剩余数量
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpProductionController/queryErpProductionOutsideProListByOrderId")
    @ResponseBody
    public void queryErpProductionOutsideProListByOrderId(InputObject inputObject, OutputObject outputObject) throws Exception{
    	erpProductionService.queryErpProductionOutsideProListByOrderId(inputObject, outputObject);
    }
	
}
