package com.skyeye.smprogram.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.smprogram.service.RmTypeService;

@Controller
public class RmTypeController {
	
	@Autowired
	private RmTypeService rmTypeService;
	
	/**
	 * 
	     * @Title: queryRmTypeList
	     * @Description: 获取小程序分类列表
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/RmTypeController/queryRmTypeList")
	@ResponseBody
	public void queryRmTypeList(InputObject inputObject, OutputObject outputObject) throws Exception{
		rmTypeService.queryRmTypeList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertRmTypeMation
	     * @Description: 新增小程序分类列表
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/RmTypeController/insertRmTypeMation")
	@ResponseBody
	public void insertRmTypeMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		rmTypeService.insertRmTypeMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteRmTypeById
	     * @Description: 删除小程序分类信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/RmTypeController/deleteRmTypeById")
	@ResponseBody
	public void deleteRmTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
		rmTypeService.deleteRmTypeById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryRmTypeMationToEditById
	     * @Description: 编辑小程序分类信息时进行回显
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/RmTypeController/queryRmTypeMationToEditById")
	@ResponseBody
	public void queryRmTypeMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
		rmTypeService.queryRmTypeMationToEditById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editRmTypeMationById
	     * @Description: 编辑小程序分类信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/RmTypeController/editRmTypeMationById")
	@ResponseBody
	public void editRmTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		rmTypeService.editRmTypeMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editRmTypeSortTopById
	     * @Description: 小程序分类展示顺序上移
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/RmTypeController/editRmTypeSortTopById")
	@ResponseBody
	public void editRmTypeSortTopById(InputObject inputObject, OutputObject outputObject) throws Exception{
		rmTypeService.editRmTypeSortTopById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editRmTypeSortLowerById
	     * @Description: 小程序分类展示顺序下移
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/RmTypeController/editRmTypeSortLowerById")
	@ResponseBody
	public void editRmTypeSortLowerById(InputObject inputObject, OutputObject outputObject) throws Exception{
		rmTypeService.editRmTypeSortLowerById(inputObject, outputObject);
	}
	
}
