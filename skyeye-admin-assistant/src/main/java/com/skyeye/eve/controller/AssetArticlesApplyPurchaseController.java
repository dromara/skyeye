/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.AssetArticlesApplyPurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ClassName: AssetArticlesPurchaseController
 * @Description: 用品采购申请控制类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/24 11:39
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Controller
public class AssetArticlesApplyPurchaseController {

    @Autowired
    private AssetArticlesApplyPurchaseService assetArticlesApplyPurchaseService;

    /**
     *
     * @Title: insertAssetArticlesListToPurchase
     * @Description: 用品采购申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetArticlesApplyPurchaseController/insertAssetArticlesListToPurchase")
    @ResponseBody
    public void insertAssetArticlesListToPurchase(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetArticlesApplyPurchaseService.insertAssetArticlesListToPurchase(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryMyPurchaseAssetArticlesMation
     * @Description: 获取我采购的用品信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetArticlesApplyPurchaseController/queryMyPurchaseAssetArticlesMation")
    @ResponseBody
    public void queryMyPurchaseAssetArticlesMation(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetArticlesApplyPurchaseService.queryMyPurchaseAssetArticlesMation(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryAssetArticlesListPurchaseDetailsById
     * @Description: 用品采购申请详情
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetArticlesApplyPurchaseController/queryAssetArticlesListPurchaseDetailsById")
    @ResponseBody
    public void queryAssetArticlesListPurchaseDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetArticlesApplyPurchaseService.queryAssetArticlesListPurchaseDetailsById(inputObject, outputObject);
    }

    /**
     *
     * @Title: editAssetArticlesUseToPurchaseSubApproval
     * @Description: 用品采购申请提交审批
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetArticlesApplyPurchaseController/editAssetArticlesUseToPurchaseSubApproval")
    @ResponseBody
    public void editAssetArticlesUseToPurchaseSubApproval(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetArticlesApplyPurchaseService.editAssetArticlesUseToPurchaseSubApproval(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryAssetArticlesListPurchaseToEditById
     * @Description: 用品采购申请编辑时进行回显
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetArticlesApplyPurchaseController/queryAssetArticlesListPurchaseToEditById")
    @ResponseBody
    public void queryAssetArticlesListPurchaseToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetArticlesApplyPurchaseService.queryAssetArticlesListPurchaseToEditById(inputObject, outputObject);
    }

    /**
     *
     * @Title: updateAssetArticlesListToPurchaseById
     * @Description: 编辑用品采购申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetArticlesApplyPurchaseController/updateAssetArticlesListToPurchaseById")
    @ResponseBody
    public void updateAssetArticlesListToPurchaseById(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetArticlesApplyPurchaseService.updateAssetArticlesListToPurchaseById(inputObject, outputObject);
    }

    /**
     *
     * @Title: updateAssetArticlesPurchaseToCancellation
     * @Description: 作废用品采购申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetArticlesApplyPurchaseController/updateAssetArticlesPurchaseToCancellation")
    @ResponseBody
    public void updateAssetArticlesPurchaseToCancellation(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetArticlesApplyPurchaseService.updateAssetArticlesPurchaseToCancellation(inputObject, outputObject);
    }

    /**
     *
     * @Title: updateAssetArticlesListToPurchaseByIdInProcess
     * @Description: 在工作流中编辑用品采购申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetArticlesApplyPurchaseController/updateAssetArticlesListToPurchaseByIdInProcess")
    @ResponseBody
    public void updateAssetArticlesListToPurchaseByIdInProcess(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetArticlesApplyPurchaseService.updateAssetArticlesListToPurchaseByIdInProcess(inputObject, outputObject);
    }

    /**
     *
     * @Title: editAssetArticlesPurchaseToRevoke
     * @Description: 撤销用品采购申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetArticlesApplyPurchaseController/editAssetArticlesPurchaseToRevoke")
    @ResponseBody
    public void editAssetArticlesPurchaseToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetArticlesApplyPurchaseService.editAssetArticlesPurchaseToRevoke(inputObject, outputObject);
    }

}
