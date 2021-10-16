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
import com.skyeye.service.ErpPickService;

@Controller
public class ErpPickController {
	
	@Autowired
	private ErpPickService erpPickService;
	
	/**
     * 获取领料单列表信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpPickController/queryRequisitionMaterialOrderList")
    @ResponseBody
    public void queryRequisitionMaterialOrderList(InputObject inputObject, OutputObject outputObject) throws Exception{
    	erpPickService.queryRequisitionMaterialOrderList(inputObject, outputObject);
    }
    
    /**
     * 获取退料单列表信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpPickController/queryReturnMaterialOrderList")
    @ResponseBody
    public void queryReturnMaterialOrderList(InputObject inputObject, OutputObject outputObject) throws Exception{
    	erpPickService.queryReturnMaterialOrderList(inputObject, outputObject);
    }
    
    /**
     * 获取补料单列表信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpPickController/queryPatchMaterialOrderList")
    @ResponseBody
    public void queryPatchMaterialOrderList(InputObject inputObject, OutputObject outputObject) throws Exception{
    	erpPickService.queryPatchMaterialOrderList(inputObject, outputObject);
    }
    
    /**
     * 新增领料单信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpPickController/insertRequisitionMaterialOrder")
    @ResponseBody
    public void insertRequisitionMaterialOrder(InputObject inputObject, OutputObject outputObject) throws Exception{
    	erpPickService.insertRequisitionMaterialOrder(inputObject, outputObject);
    }
    
    /**
     * 编辑领料单时获取信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpPickController/queryRequisitionMaterialOrderToEditById")
    @ResponseBody
    public void queryRequisitionMaterialOrderToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
    	erpPickService.queryRequisitionMaterialOrderToEditById(inputObject, outputObject);
    }
    
    /**
     * 编辑领料单信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpPickController/editRequisitionMaterialOrderById")
    @ResponseBody
    public void editRequisitionMaterialOrderById(InputObject inputObject, OutputObject outputObject) throws Exception{
    	erpPickService.editRequisitionMaterialOrderById(inputObject, outputObject);
    }
    
    /**
     * 获取领料/补料/退料单信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpPickController/queryPickOrderMationById")
    @ResponseBody
    public void queryPickOrderMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
    	erpPickService.queryPickOrderMationById(inputObject, outputObject);
    }
    
    /**
     * 删除领料/补料/退料单信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpPickController/deletePickOrderMationById")
    @ResponseBody
    public void deletePickOrderMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
    	erpPickService.deletePickOrderMationById(inputObject, outputObject);
    }
    
    /**
     * 新增补料单信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpPickController/insertPatchMaterialOrder")
    @ResponseBody
    public void insertPatchMaterialOrder(InputObject inputObject, OutputObject outputObject) throws Exception{
    	erpPickService.insertPatchMaterialOrder(inputObject, outputObject);
    }
    
    /**
     * 编辑补料单时获取信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpPickController/queryPatchMaterialOrderToEditById")
    @ResponseBody
    public void queryPatchMaterialOrderToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
    	erpPickService.queryPatchMaterialOrderToEditById(inputObject, outputObject);
    }
    
    /**
     * 编辑补料单信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpPickController/editPatchMaterialOrderById")
    @ResponseBody
    public void editPatchMaterialOrderById(InputObject inputObject, OutputObject outputObject) throws Exception{
    	erpPickService.editPatchMaterialOrderById(inputObject, outputObject);
    }
    
    /**
     * 新增退料单信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpPickController/insertReturnMaterialOrder")
    @ResponseBody
    public void insertReturnMaterialOrder(InputObject inputObject, OutputObject outputObject) throws Exception{
    	erpPickService.insertReturnMaterialOrder(inputObject, outputObject);
    }
    
    /**
     * 编辑退料单时获取信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpPickController/queryReturnMaterialOrderToEditById")
    @ResponseBody
    public void queryReturnMaterialOrderToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
    	erpPickService.queryReturnMaterialOrderToEditById(inputObject, outputObject);
    }
    
    /**
     * 编辑退料单信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpPickController/editReturnMaterialOrderById")
    @ResponseBody
    public void editReturnMaterialOrderById(InputObject inputObject, OutputObject outputObject) throws Exception{
    	erpPickService.editReturnMaterialOrderById(inputObject, outputObject);
    }
    
    /**
     * 领料/补料/退料单信息提交审核
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpPickController/editPickOrderMationToSubById")
    @ResponseBody
    public void editPickOrderMationToSubById(InputObject inputObject, OutputObject outputObject) throws Exception{
    	erpPickService.editPickOrderMationToSubById(inputObject, outputObject);
    }
    
    /**
     * 领料/补料/退料单审核
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpPickController/editPickOrderMationToExamineById")
    @ResponseBody
    public void editPickOrderMationToExamineById(InputObject inputObject, OutputObject outputObject) throws Exception{
    	erpPickService.editPickOrderMationToExamineById(inputObject, outputObject);
    }
	
}
