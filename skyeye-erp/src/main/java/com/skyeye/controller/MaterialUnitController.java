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
import com.skyeye.service.MaterialUnitService;

@Controller
public class MaterialUnitController {
	
	@Autowired
	private MaterialUnitService materialUnitService;
	
	/**
     * 获取计量单位列表
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/MaterialUnitController/queryMaterialUnitList")
    @ResponseBody
    public void queryMaterialUnitList(InputObject inputObject, OutputObject outputObject) throws Exception{
    	materialUnitService.queryMaterialUnitList(inputObject, outputObject);
    }
    
    /**
     * 新增计量单位
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/MaterialUnitController/insertMaterialUnitMation")
    @ResponseBody
    public void insertMaterialUnitMation(InputObject inputObject, OutputObject outputObject) throws Exception{
    	materialUnitService.insertMaterialUnitMation(inputObject, outputObject);
    }
    
    /**
     * 删除计量单位
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/MaterialUnitController/deleteMaterialUnitMationById")
    @ResponseBody
    public void deleteMaterialUnitMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
    	materialUnitService.deleteMaterialUnitMationById(inputObject, outputObject);
    }
    
    /**
     * 编辑计量单位时进行回显
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/MaterialUnitController/queryMaterialUnitMationToEditById")
    @ResponseBody
    public void queryMaterialUnitMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
    	materialUnitService.queryMaterialUnitMationToEditById(inputObject, outputObject);
    }
    
    /**
     * 编辑计量单位
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/MaterialUnitController/editMaterialUnitMationById")
    @ResponseBody
    public void editMaterialUnitMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
    	materialUnitService.editMaterialUnitMationById(inputObject, outputObject);
    }
    
    /**
     * 获取计量单位展示为下拉框
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/MaterialUnitController/queryMaterialUnitListToSelect")
    @ResponseBody
    public void queryMaterialUnitListToSelect(InputObject inputObject, OutputObject outputObject) throws Exception{
    	materialUnitService.queryMaterialUnitListToSelect(inputObject, outputObject);
    }
	
}
