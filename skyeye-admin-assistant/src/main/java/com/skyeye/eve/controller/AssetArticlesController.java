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
import com.skyeye.eve.service.AssetArticlesService;

/**
 *
 * @ClassName: AssetArticlesController
 * @Description: 用品管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/24 11:48
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Controller
public class AssetArticlesController {

	@Autowired
	private AssetArticlesService assetArticlesService;
	
	/**
	 * 
	     * @Title: insertAssetArticles
	     * @Description: 新增用品
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/AssetArticlesController/insertAssetArticles")
	@ResponseBody
	public void insertAssetArticles(InputObject inputObject, OutputObject outputObject) throws Exception{
		assetArticlesService.insertAssetArticles(inputObject, outputObject);
	}
	
	/**
     * 
         * @Title: queryAssetArticlesList
         * @Description: 获取用品列表
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @RequestMapping("/post/AssetArticlesController/queryAssetArticlesList")
    @ResponseBody
    public void queryAssetArticlesList(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetArticlesService.queryAssetArticlesList(inputObject, outputObject);
    }
	
    /**
     * 
         * @Title: queryAssetArticlesMationToDetails
         * @Description: 用品详情
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @RequestMapping("/post/AssetArticlesController/queryAssetArticlesMationToDetails")
    @ResponseBody
    public void queryAssetArticlesMationToDetails(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetArticlesService.queryAssetArticlesMationToDetails(inputObject, outputObject);
    }
	
    /**
     * 
         * @Title: deleteAssetArticles
         * @Description: 删除用品
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @RequestMapping("/post/AssetArticlesController/deleteAssetArticles")
    @ResponseBody
    public void deleteAssetArticles(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetArticlesService.deleteAssetArticles(inputObject, outputObject);
    }
	
    /**
     * 
         * @Title: queryAssetArticlesMationById
         * @Description: 通过id查找对应的用品信息用以编辑
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @RequestMapping("/post/AssetArticlesController/queryAssetArticlesMationById")
    @ResponseBody
    public void queryAssetArticlesMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetArticlesService.queryAssetArticlesMationById(inputObject, outputObject);
    }
	
    /**
     * 
         * @Title: editAssetArticlesMationById
         * @Description: 通过id编辑对应的用品信息
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @RequestMapping("/post/AssetArticlesController/editAssetArticlesMationById")
    @ResponseBody
    public void editAssetArticlesMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetArticlesService.editAssetArticlesMationById(inputObject, outputObject);
    }
    
    /**
     * 
         * @Title: queryAssetArticlesListByTypeId
         * @Description: 根据用品类别获取用品列表信息
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @RequestMapping("/post/AssetArticlesController/queryAssetArticlesListByTypeId")
    @ResponseBody
    public void queryAssetArticlesListByTypeId(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetArticlesService.queryAssetArticlesListByTypeId(inputObject, outputObject);
    }
    
}
