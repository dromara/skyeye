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
import com.skyeye.eve.service.SealService;

/**
 *
 * @ClassName: SealController
 * @Description: 印章管理控制类
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/1 19:33
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Controller
public class SealController {
	
	@Autowired
	private SealService sealService;
	
	/**
	 * 
	     * @Title: selectAllSealMation
	     * @Description: 遍历所有的印章
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SealController/selectAllSealMation")
	@ResponseBody
	public void selectAllSealMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		sealService.selectAllSealMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertSealMation
	     * @Description: 新增印章
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SealController/insertSealMation")
	@ResponseBody
	public void insertSealMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		sealService.insertSealMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteSealById
	     * @Description: 删除印章
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SealController/deleteSealById")
	@ResponseBody
	public void deleteSealById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sealService.deleteSealById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySealMationById
	     * @Description: 查询印章信息用以编辑
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SealController/querySealMationById")
	@ResponseBody
	public void querySealMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sealService.querySealMationById(inputObject, outputObject);
	}

	/**
	 * 
	     * @Title: editSealMationById
	     * @Description: 编辑印章
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SealController/editSealMationById")
	@ResponseBody
	public void editSealMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sealService.editSealMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: selectSealDetailsById
	     * @Description: 印章详情
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SealController/selectSealDetailsById")
	@ResponseBody
	public void selectAccidentDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sealService.selectSealDetailsById(inputObject, outputObject);
	}
	
    /**
	 * 
	     * @Title: selectSealListToChoose
	     * @Description: 获取印章列表用于借用选择
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SealController/selectSealListToChoose")
	@ResponseBody
	public void selectSealListToChoose(InputObject inputObject, OutputObject outputObject) throws Exception{
		sealService.selectSealListToChoose(inputObject, outputObject);
	}
	
    /**
	 * 
	     * @Title: selectRevertListToChoose
	     * @Description: 获取印章列表用于归还选择
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SealController/selectRevertListToChoose")
	@ResponseBody
	public void selectRevertListToChoose(InputObject inputObject, OutputObject outputObject) throws Exception{
		sealService.selectRevertListToChoose(inputObject, outputObject);
	}
	
}
