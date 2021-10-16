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
import com.skyeye.service.ErpBomService;

@Controller
public class ErpBomController {
	
	@Autowired
	private ErpBomService erpBomService;
	
	/**
     * 查询bom表列表
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpBomController/queryErpBomList")
    @ResponseBody
    public void queryErpBomList(InputObject inputObject, OutputObject outputObject) throws Exception{
    	erpBomService.queryErpBomList(inputObject, outputObject);
    }
    
    /**
     * 新增bom表
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpBomController/insertErpBomMation")
    @ResponseBody
    public void insertErpBomMation(InputObject inputObject, OutputObject outputObject) throws Exception{
    	erpBomService.insertErpBomMation(inputObject, outputObject);
    }
    
    /**
     * 查询bom表详情
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpBomController/queryErpBomDetailById")
    @ResponseBody
    public void queryErpBomDetailById(InputObject inputObject, OutputObject outputObject) throws Exception{
    	erpBomService.queryErpBomDetailById(inputObject, outputObject);
    }
    
    /**
     * 删除bom表信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpBomController/deleteErpBomMationById")
    @ResponseBody
    public void deleteErpBomMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
    	erpBomService.deleteErpBomMationById(inputObject, outputObject);
    }
    
    /**
     * 查询bom表信息用作编辑
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpBomController/queryErpBomMationToEditById")
    @ResponseBody
    public void queryErpBomMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
    	erpBomService.queryErpBomMationToEditById(inputObject, outputObject);
    }
    
    /**
     * 编辑bom表信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpBomController/editErpBomMation")
    @ResponseBody
    public void editErpBomMation(InputObject inputObject, OutputObject outputObject) throws Exception{
    	erpBomService.editErpBomMation(inputObject, outputObject);
    }
    
    /**
     * 根据规格id获取方案列表
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpBomController/queryErpBomListByNormsId")
    @ResponseBody
    public void queryErpBomListByNormsId(InputObject inputObject, OutputObject outputObject) throws Exception{
    	erpBomService.queryErpBomListByNormsId(inputObject, outputObject);
    }
    
    /**
     * 根据方案id获取bom表子件列表
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpBomController/queryErpBomChildProListByBomId")
    @ResponseBody
    public void queryErpBomChildProListByBomId(InputObject inputObject, OutputObject outputObject) throws Exception{
    	erpBomService.queryErpBomChildProListByBomId(inputObject, outputObject);
    }
	
}
