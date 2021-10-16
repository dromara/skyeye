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
import com.skyeye.service.MaterialCategoryService;

/**
 *
 * @ClassName: MaterialCategoryController
 * @Description: 产品类型管理控制类
 * @author: skyeye云系列--卫志强
 * @date: 2021/10/6 9:22
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Controller
public class MaterialCategoryController {
	
	@Autowired
	private MaterialCategoryService materialCategoryService;
	
	/**
     * 获取产品类型信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/MaterialCategoryController/queryMaterialCategoryList")
    @ResponseBody
    public void queryMaterialCategoryList(InputObject inputObject, OutputObject outputObject) throws Exception{
    	materialCategoryService.queryMaterialCategoryList(inputObject, outputObject);
    }
    
    /**
	 * 
     * @Description: 添加产品类型
     * @param inputObject
     * @param outputObject
     * @throws Exception
     * @throws
	 */
	@RequestMapping("/post/MaterialCategoryController/insertMaterialCategoryMation")
	@ResponseBody
	public void insertMaterialCategoryMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		materialCategoryService.insertMaterialCategoryMation(inputObject, outputObject);
	}
	
	/**
	 * 
     * @Description: 删除产品类型
     * @param inputObject
     * @param outputObject
     * @throws Exception
	 */
	@RequestMapping("/post/MaterialCategoryController/deleteMaterialCategoryById")
	@ResponseBody
	public void deleteMaterialCategoryById(InputObject inputObject, OutputObject outputObject) throws Exception{
		materialCategoryService.deleteMaterialCategoryById(inputObject, outputObject);
	}
	
	/**
	 * 
     * @Description: 通过id查找对应的产品类型信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
	 */
	@RequestMapping("/post/MaterialCategoryController/selectMaterialCategoryToEditById")
	@ResponseBody
	public void selectMaterialCategoryToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
		materialCategoryService.selectMaterialCategoryToEditById(inputObject, outputObject);
	}
	
	/**
	 * 
     * @Description: 编辑产品类型
     * @param inputObject
     * @param outputObject
     * @throws Exception
	 */
	@RequestMapping("/post/MaterialCategoryController/editMaterialCategoryMationById")
	@ResponseBody
	public void editMaterialCategoryMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		materialCategoryService.editMaterialCategoryMationById(inputObject, outputObject);
	}
	
	/**
	 * 
     * @Description: 产品类型上移
     * @param inputObject
     * @param outputObject
     * @throws Exception
	 */
	@RequestMapping("/post/MaterialCategoryController/editMaterialCategoryMationOrderNumUpById")
	@ResponseBody
	public void editSysWinTypeMationOrderNumUpById(InputObject inputObject, OutputObject outputObject) throws Exception{
		materialCategoryService.editMaterialCategoryMationOrderNumUpById(inputObject, outputObject);
	}
	
	/**
	 * 
     * @Description: 产品类型下移
     * @param inputObject
     * @param outputObject
     * @throws Exception
	 */
	@RequestMapping("/post/MaterialCategoryController/editMaterialCategoryMationOrderNumDownById")
	@ResponseBody
	public void editSysWinTypeMationOrderNumDownById(InputObject inputObject, OutputObject outputObject) throws Exception{
		materialCategoryService.editMaterialCategoryMationOrderNumDownById(inputObject, outputObject);
	}
	
	/**
     * 获取所有类型展示为树
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/MaterialCategoryController/queryMaterialCategoryListToTree")
    @ResponseBody
    public void queryMaterialCategoryListToTree(InputObject inputObject, OutputObject outputObject) throws Exception{
    	materialCategoryService.queryMaterialCategoryListToTree(inputObject, outputObject);
    }
    
    /**
     * 获取所有类型展示为树-数据结果呈树状
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/MaterialCategoryController/queryMaterialCategoryListToTreeZtr")
    @ResponseBody
    public void queryMaterialCategoryListToTreeZtr(InputObject inputObject, OutputObject outputObject) throws Exception{
    	materialCategoryService.queryMaterialCategoryListToTreeZtr(inputObject, outputObject);
    }

}
