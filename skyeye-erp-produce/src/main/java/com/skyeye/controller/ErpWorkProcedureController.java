/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 */

package com.skyeye.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.service.ErpWorkProcedureService;

@Controller
public class ErpWorkProcedureController {
	
	@Autowired
	private ErpWorkProcedureService erpWorkProcedureService;
	
	/**
     * 查询工序列表
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpWorkProcedureController/queryErpWorkProcedureList")
    @ResponseBody
    public void queryErpWorkProcedureList(InputObject inputObject, OutputObject outputObject) throws Exception{
        erpWorkProcedureService.queryErpWorkProcedureList(inputObject, outputObject);
    }

    /**
     * 添加工序信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpWorkProcedureController/insertErpWorkProcedureMation")
    @ResponseBody
    public void insertErpWorkProcedureMation(InputObject inputObject, OutputObject outputObject) throws Exception{
        erpWorkProcedureService.insertErpWorkProcedureMation(inputObject, outputObject);
    }

    /**
     * 查询单个工序信息，用于信息回显
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpWorkProcedureController/queryErpWorkProcedureToEditById")
    @ResponseBody
    public void queryErpWorkProcedureToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
        erpWorkProcedureService.queryErpWorkProcedureToEditById(inputObject, outputObject);
    }

    /**
     * 删除工序信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpWorkProcedureController/deletErpWorkProcedureById")
    @ResponseBody
    public void deletErpWorkProcedureById(InputObject inputObject, OutputObject outputObject) throws Exception{
        erpWorkProcedureService.deletErpWorkProcedureById(inputObject, outputObject);
    }

    /**
     * 编辑工序信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpWorkProcedureController/editErpWorkProcedureById")
    @ResponseBody
    public void editErpWorkProcedureById(InputObject inputObject, OutputObject outputObject) throws Exception{
        erpWorkProcedureService.editErpWorkProcedureById(inputObject, outputObject);
    }
    
    /**
     * 查询工序列表展示为表格供其他选择
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpWorkProcedureController/queryErpWorkProcedureListToTable")
    @ResponseBody
    public void queryErpWorkProcedureListToTable(InputObject inputObject, OutputObject outputObject) throws Exception{
        erpWorkProcedureService.queryErpWorkProcedureListToTable(inputObject, outputObject);
    }
    
    /**
     * 根据工序id串获取工序列表
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpWorkProcedureController/queryErpWorkProcedureListByIds")
    @ResponseBody
    public void queryErpWorkProcedureListByIds(InputObject inputObject, OutputObject outputObject) throws Exception{
        erpWorkProcedureService.queryErpWorkProcedureListByIds(inputObject, outputObject);
    }

    /**
     * 获取工序详情信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpWorkProcedureController/queryErpWorkProcedureMationById")
    @ResponseBody
    public void queryErpWorkProcedureMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
        erpWorkProcedureService.queryErpWorkProcedureMationById(inputObject, outputObject);
    }
    
    /**
     * 查询工序列表展示为下拉框供其他选择
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpWorkProcedureController/queryErpWorkProcedureListToSelect")
    @ResponseBody
    public void queryErpWorkProcedureListToSelect(InputObject inputObject, OutputObject outputObject) throws Exception{
        erpWorkProcedureService.queryErpWorkProcedureListToSelect(inputObject, outputObject);
    }
	
}
