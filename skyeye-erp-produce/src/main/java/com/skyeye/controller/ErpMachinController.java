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
import com.skyeye.service.ErpMachinService;

@Controller
public class ErpMachinController {
	
	@Autowired
	private ErpMachinService erpMachinService;
	
	/**
     * 获取加工单列表信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpMachinController/queryMachinOrderToList")
    @ResponseBody
    public void queryMachinOrderToList(InputObject inputObject, OutputObject outputObject) throws Exception{
    	erpMachinService.queryMachinOrderToList(inputObject, outputObject);
    }
    
    /**
     * 新增加工单信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpMachinController/insertMachinOrderMation")
    @ResponseBody
    public void insertMachinOrderMation(InputObject inputObject, OutputObject outputObject) throws Exception{
    	erpMachinService.insertMachinOrderMation(inputObject, outputObject);
    }
    
    /**
     * 编辑加工单信息时回显
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpMachinController/queryMachinOrderToEditById")
    @ResponseBody
    public void queryMachinOrderToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
    	erpMachinService.queryMachinOrderToEditById(inputObject, outputObject);
    }
    
    /**
     * 编辑加工单信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpMachinController/editMachinOrderMationById")
    @ResponseBody
    public void editMachinOrderMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
    	erpMachinService.editMachinOrderMationById(inputObject, outputObject);
    }
    
    /**
     * 删除加工单信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpMachinController/deleteMachinOrderMationById")
    @ResponseBody
    public void deleteMachinOrderMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
    	erpMachinService.deleteMachinOrderMationById(inputObject, outputObject);
    }
    
    /**
     * 加工单详情信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpMachinController/queryMachinOrderDetailMationById")
    @ResponseBody
    public void queryMachinOrderDetailMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
    	erpMachinService.queryMachinOrderDetailMationById(inputObject, outputObject);
    }
    
    /**
     * 加工单提交审核
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpMachinController/editMachinOrderMationToSubmitById")
    @ResponseBody
    public void editMachinOrderMationToSubmitById(InputObject inputObject, OutputObject outputObject) throws Exception{
    	erpMachinService.editMachinOrderMationToSubmitById(inputObject, outputObject);
    }
    
    /**
     * 加工单审核
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpMachinController/editMachinOrderMationToExamineById")
    @ResponseBody
    public void editMachinOrderMationToExamineById(InputObject inputObject, OutputObject outputObject) throws Exception{
    	erpMachinService.editMachinOrderMationToExamineById(inputObject, outputObject);
    }
    
    /**
     * 根据部门获取已经审核通过的加工单列表信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpMachinController/queryMachinStateIsPassOrderListByDepartmentId")
    @ResponseBody
    public void queryMachinStateIsPassOrderListByDepartmentId(InputObject inputObject, OutputObject outputObject) throws Exception{
    	erpMachinService.queryMachinStateIsPassOrderListByDepartmentId(inputObject, outputObject);
    }
    
    /**
     * 根据部门获取审核通过的加工单列表信息展示为表格供领料/补料单使用（不包含已完成的加工单）
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpMachinController/queryMachinStateIsPassNoComplateOrderListByDepartmentId")
    @ResponseBody
    public void queryMachinStateIsPassNoComplateOrderListByDepartmentId(InputObject inputObject, OutputObject outputObject) throws Exception{
    	erpMachinService.queryMachinStateIsPassNoComplateOrderListByDepartmentId(inputObject, outputObject);
    }
    
    /**
     * 根据加工单id获取该单据下的所有单据中商品以及剩余领料数量
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpMachinController/queryMachinStateIsPassOrderMationById")
    @ResponseBody
    public void queryMachinStateIsPassOrderMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
    	erpMachinService.queryMachinStateIsPassOrderMationById(inputObject, outputObject);
    }
    
    /**
     * 加工单工序验收
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpMachinController/editMachinStateMationByChildId")
    @ResponseBody
    public void editMachinStateMationByChildId(InputObject inputObject, OutputObject outputObject) throws Exception{
    	erpMachinService.editMachinStateMationByChildId(inputObject, outputObject);
    }
	
}
