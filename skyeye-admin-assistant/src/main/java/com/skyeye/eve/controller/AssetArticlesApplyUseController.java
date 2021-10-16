/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.AssetArticlesApplyUseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ClassName: AssetArticlesApplyUseController
 * @Description: 用品领用申请控制类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/24 9:20
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Controller
public class AssetArticlesApplyUseController {

    @Autowired
    private AssetArticlesApplyUseService assetArticlesApplyUseService;

    /**
     *
     * @Title: queryMyUseAssetArticlesMation
     * @Description: 获取我领用的用品信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetArticlesApplyUseController/queryMyUseAssetArticlesMation")
    @ResponseBody
    public void queryMyUseAssetArticlesMation(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetArticlesApplyUseService.queryMyUseAssetArticlesMation(inputObject, outputObject);
    }

    /**
     *
     * @Title: insertAssetArticlesListToUse
     * @Description: 用品领用申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetArticlesApplyUseController/insertAssetArticlesListToUse")
    @ResponseBody
    public void insertAssetArticlesListToUse(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetArticlesApplyUseService.insertAssetArticlesListToUse(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryAssetArticlesListUseDetailsById
     * @Description: 用品领用申请详情
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetArticlesApplyUseController/queryAssetArticlesListUseDetailsById")
    @ResponseBody
    public void queryAssetArticlesListUseDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetArticlesApplyUseService.queryAssetArticlesListUseDetailsById(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryAssetArticlesListUseToEditById
     * @Description: 用品领用申请编辑时进行回显
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetArticlesApplyUseController/queryAssetArticlesListUseToEditById")
    @ResponseBody
    public void queryAssetArticlesListUseToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetArticlesApplyUseService.queryAssetArticlesListUseToEditById(inputObject, outputObject);
    }

    /**
     *
     * @Title: updateAssetArticlesListToUseById
     * @Description: 编辑用品领用申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetArticlesApplyUseController/updateAssetArticlesListToUseById")
    @ResponseBody
    public void updateAssetArticlesListToUseById(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetArticlesApplyUseService.updateAssetArticlesListToUseById(inputObject, outputObject);
    }

    /**
     *
     * @Title: editAssetArticlesUseToSubApproval
     * @Description: 用品领用申请提交审批
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetArticlesApplyUseController/editAssetArticlesUseToSubApproval")
    @ResponseBody
    public void editAssetArticlesUseToSubApproval(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetArticlesApplyUseService.editAssetArticlesUseToSubApproval(inputObject, outputObject);
    }

    /**
     *
     * @Title: updateAssetArticlesToCancellation
     * @Description: 作废用品领用申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetArticlesApplyUseController/updateAssetArticlesToCancellation")
    @ResponseBody
    public void updateAssetArticlesToCancellation(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetArticlesApplyUseService.updateAssetArticlesToCancellation(inputObject, outputObject);
    }

    /**
     *
     * @Title: updateAssetArticlesListToUseByIdInProcess
     * @Description: 在工作流中编辑用品领用申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetArticlesApplyUseController/updateAssetArticlesListToUseByIdInProcess")
    @ResponseBody
    public void updateAssetArticlesListToUseByIdInProcess(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetArticlesApplyUseService.updateAssetArticlesListToUseByIdInProcess(inputObject, outputObject);
    }

    /**
     *
     * @Title: editAssetArticlesUseToRevoke
     * @Description: 撤销用品领用申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetArticlesApplyUseController/editAssetArticlesUseToRevoke")
    @ResponseBody
    public void editAssetArticlesUseToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetArticlesApplyUseService.editAssetArticlesUseToRevoke(inputObject, outputObject);
    }

}
