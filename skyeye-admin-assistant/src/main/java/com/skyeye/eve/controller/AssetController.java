/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.AssetService;

@Controller
public class AssetController {
	
	@Autowired
	private AssetService assetService;
	
	/**
	 * 
	     * @Title: selectAllAssetMation
	     * @Description: 遍历所有的资产
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/AssetController/selectAllAssetMation")
	@ResponseBody
	public void selectAllAssetMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		assetService.selectAllAssetMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertAssetMation
	     * @Description: 新增资产
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/AssetController/insertAssetMation")
	@ResponseBody
	public void insertAssetMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		assetService.insertAssetMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteAssetById
	     * @Description: 删除资产
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/AssetController/deleteAssetById")
	@ResponseBody
	public void deleteAssetById(InputObject inputObject, OutputObject outputObject) throws Exception{
		assetService.deleteAssetById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryAssetMationById
	     * @Description: 查询资产信息用以编辑
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/AssetController/queryAssetMationById")
	@ResponseBody
	public void queryAssetMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		assetService.queryAssetMationById(inputObject, outputObject);
	}

	/**
	 * 
	     * @Title: editAssetMationById
	     * @Description: 编辑资产
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/AssetController/editAssetMationById")
	@ResponseBody
	public void editAssetMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		assetService.editAssetMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: selectAssetDetailsById
	     * @Description: 资产详情
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/AssetController/selectAssetDetailsById")
	@ResponseBody
	public void selectAccidentDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception{
		assetService.selectAssetDetailsById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: updateAssetNormalById
	     * @Description: 车辆恢复正常
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/AssetController/updateAssetNormalById")
	@ResponseBody
	public void updateAssetNormalById(InputObject inputObject, OutputObject outputObject) throws Exception{
		assetService.updateAssetNormalById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: updateAssetRepairById
	     * @Description: 车辆维修
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/AssetController/updateAssetRepairById")
	@ResponseBody
	public void updateAssetRepairById(InputObject inputObject, OutputObject outputObject) throws Exception{
		assetService.updateAssetRepairById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: updateAssetScrapById
	     * @Description: 车辆报废
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/AssetController/updateAssetScrapById")
	@ResponseBody
	public void updateAssetScrapById(InputObject inputObject, OutputObject outputObject) throws Exception{
		assetService.updateAssetScrapById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryUnUseAssetListByTypeId
	     * @Description: 根据资产类别获取未被使用的资产列表信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/AssetController/queryUnUseAssetListByTypeId")
	@ResponseBody
	public void queryUnUseAssetListByTypeId(InputObject inputObject, OutputObject outputObject) throws Exception{
		assetService.queryUnUseAssetListByTypeId(inputObject, outputObject);
	}
	
}
