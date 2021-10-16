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
import com.skyeye.service.MaterialService;

@Controller
public class MaterialController {
	
	@Autowired
	private MaterialService materialService;
	
	/**
     * 获取产品信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/MaterialController/queryMaterialList")
    @ResponseBody
    public void queryMaterialList(InputObject inputObject, OutputObject outputObject) throws Exception{
    	materialService.queryMaterialList(inputObject, outputObject);
    }
    
    /**
     * 新增产品信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/MaterialController/insertMaterialMation")
    @ResponseBody
    public void insertMaterialMation(InputObject inputObject, OutputObject outputObject) throws Exception{
    	materialService.insertMaterialMation(inputObject, outputObject);
    }
    
    /**
     * 禁用产品信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/MaterialController/editMaterialEnabledToDisablesById")
    @ResponseBody
    public void editMaterialEnabledToDisablesById(InputObject inputObject, OutputObject outputObject) throws Exception{
    	materialService.editMaterialEnabledToDisablesById(inputObject, outputObject);
    }
    
    /**
     * 启用产品信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/MaterialController/editMaterialEnabledToEnablesById")
    @ResponseBody
    public void editMaterialEnabledToEnablesById(InputObject inputObject, OutputObject outputObject) throws Exception{
    	materialService.editMaterialEnabledToEnablesById(inputObject, outputObject);
    }
    
    /**
     * 删除产品信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/MaterialController/deleteMaterialMationById")
    @ResponseBody
    public void deleteMaterialMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
    	materialService.deleteMaterialMationById(inputObject, outputObject);
    }
    
    /**
     * 产品信息详情
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/MaterialController/queryMaterialMationDetailsById")
    @ResponseBody
    public void queryMaterialMationDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception{
    	materialService.queryMaterialMationDetailsById(inputObject, outputObject);
    }
    
    /**
     * 编辑产品信息进行回显
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/MaterialController/queryMaterialMationToEditById")
    @ResponseBody
    public void queryMaterialMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
    	materialService.queryMaterialMationToEditById(inputObject, outputObject);
    }
    
    /**
     * 编辑产品信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/MaterialController/editMaterialMationById")
    @ResponseBody
    public void editMaterialMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
    	materialService.editMaterialMationById(inputObject, outputObject);
    }
    
    /**
     * 获取产品列表信息展示为表格方便选择
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/MaterialController/queryMaterialListToTable")
    @ResponseBody
    public void queryMaterialListToTable(InputObject inputObject, OutputObject outputObject) throws Exception{
    	materialService.queryMaterialListToTable(inputObject, outputObject);
    }
    
    /**
     * 根据商品规格id以及仓库id获取库存
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/MaterialController/queryMaterialTockByNormsId")
    @ResponseBody
    public void queryMaterialTockByNormsId(InputObject inputObject, OutputObject outputObject) throws Exception{
    	materialService.queryMaterialTockByNormsId(inputObject, outputObject);
    }
    
    /**
     * 根据产品规格id获取库存订单-用于库存订单明细
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/MaterialController/queryMaterialDepotItemByNormsId")
    @ResponseBody
    public void queryMaterialDepotItemByNormsId(InputObject inputObject, OutputObject outputObject) throws Exception{
    	materialService.queryMaterialDepotItemByNormsId(inputObject, outputObject);
    }
    
    /**
     * 根据商品id串获取商品列表
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/MaterialController/queryMaterialListByIds")
    @ResponseBody
    public void queryMaterialListByIds(InputObject inputObject, OutputObject outputObject) throws Exception{
    	materialService.queryMaterialListByIds(inputObject, outputObject);
    }
    
    /**
     * 根据商品id串获取商品列表---用于生产模块
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/MaterialController/queryMaterialListByIdsToProduce")
    @ResponseBody
    public void queryMaterialListByIdsToProduce(InputObject inputObject, OutputObject outputObject) throws Exception{
    	materialService.queryMaterialListByIdsToProduce(inputObject, outputObject);
    }
    
    /**
     * 根据商品信息以及bom方案信息获取商品树---用于生产模块
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/MaterialController/queryMaterialBomChildsToProduceByJson")
    @ResponseBody
    public void queryMaterialBomChildsToProduceByJson(InputObject inputObject, OutputObject outputObject) throws Exception{
    	materialService.queryMaterialBomChildsToProduceByJson(inputObject, outputObject);
    }
    
    /**
     * 获取商品库存信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/MaterialController/queryMaterialReserveList")
    @ResponseBody
    public void queryMaterialReserveList(InputObject inputObject, OutputObject outputObject) throws Exception{
    	materialService.queryMaterialReserveList(inputObject, outputObject);
    }
    
    /**
     * 获取预警商品库存信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/MaterialController/queryMaterialInventoryWarningList")
    @ResponseBody
    public void queryMaterialInventoryWarningList(InputObject inputObject, OutputObject outputObject) throws Exception{
    	materialService.queryMaterialInventoryWarningList(inputObject, outputObject);
    }
	
}
