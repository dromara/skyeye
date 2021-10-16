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
import com.skyeye.service.ErpWayProcedureService;

@Controller
public class ErpWayProcedureController {
	
	@Autowired
	private ErpWayProcedureService erpWayProcedureService;
	
	/**
     * 查询工艺列表
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpWayProcedureController/queryErpWayProcedureList")
    @ResponseBody
    public void queryErpWayProcedureList(InputObject inputObject, OutputObject outputObject) throws Exception{
    	erpWayProcedureService.queryErpWayProcedureList(inputObject, outputObject);
    }
    
    /**
     * 新增工艺
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpWayProcedureController/insertErpWayProcedureMation")
    @ResponseBody
    public void insertErpWayProcedureMation(InputObject inputObject, OutputObject outputObject) throws Exception {
        erpWayProcedureService.insertErpWayProcedureMation(inputObject, outputObject);
    }

    /**
     * 修改工艺时数据回显
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpWayProcedureController/queryErpWayProcedureMationToEditById")
    @ResponseBody
    public void queryErpWayProcedureMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
        erpWayProcedureService.queryErpWayProcedureMationToEditById(inputObject, outputObject);
    }

    /**
     * 修改工艺
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpWayProcedureController/editErpWayProcedureMationById")
    @ResponseBody
    public void editErpWayProcedureMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        erpWayProcedureService.editErpWayProcedureMationById(inputObject, outputObject);
    }

    /**
     * 禁用工艺
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpWayProcedureController/downErpWayProcedureMationById")
    @ResponseBody
    public void downErpWayProcedureMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        erpWayProcedureService.downErpWayProcedureMationById(inputObject, outputObject);
    }

    /**
     * 启用工艺
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpWayProcedureController/upErpWayProcedureMationById")
    @ResponseBody
    public void upErpWayProcedureMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        erpWayProcedureService.upErpWayProcedureMationById(inputObject, outputObject);
    }

    /**
     * 删除工艺
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpWayProcedureController/deleteErpWayProcedureMationById")
    @ResponseBody
    public void deleteErpWayProcedureMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        erpWayProcedureService.deleteErpWayProcedureMationById(inputObject, outputObject);
    }
    
    /**
     * 
     * @Title: queryErpWayProcedureDetailsMationById
     * @Description: 获取工艺详细信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     * @return: void
     * @throws
     */
    @RequestMapping("/post/ErpWayProcedureController/queryErpWayProcedureDetailsMationById")
    @ResponseBody
    public void queryErpWayProcedureDetailsMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        erpWayProcedureService.queryErpWayProcedureDetailsMationById(inputObject, outputObject);
    }

    /**
     * 查询启用的工艺列表
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpWayProcedureController/queryErpWayRuningProcedureList")
    @ResponseBody
    public void queryErpWayRuningProcedureList(InputObject inputObject, OutputObject outputObject) throws Exception{
        erpWayProcedureService.queryErpWayRuningProcedureList(inputObject, outputObject);
    }

}
