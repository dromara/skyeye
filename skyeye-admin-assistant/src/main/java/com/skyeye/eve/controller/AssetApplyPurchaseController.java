/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.AssetApplyPurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ClassName: AssetPurchaseController
 * @Description: 资产采购单控制层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/18 23:28
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Controller
public class AssetApplyPurchaseController {

    @Autowired
    private AssetApplyPurchaseService assetApplyPurchaseService;

    /**
     *
     * @Title: queryMyPurchaseAssetMation
     * @Description: 获取我采购的资产信息列表
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetApplyPurchaseController/queryMyPurchaseAssetMation")
    @ResponseBody
    public void queryMyPurchaseAssetMation(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetApplyPurchaseService.queryMyPurchaseAssetMation(inputObject, outputObject);
    }

    /**
     *
     * @Title: insertAssetListToPurchase
     * @Description: 资产采购申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetApplyPurchaseController/insertAssetListToPurchase")
    @ResponseBody
    public void insertAssetListToPurchase(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetApplyPurchaseService.insertAssetListToPurchase(inputObject, outputObject);
    }

    /**
     *
     * @Title: editAssetPurchaseToSubApproval
     * @Description: 资产采购申请提交审批
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetApplyPurchaseController/editAssetPurchaseToSubApproval")
    @ResponseBody
    public void editAssetPurchaseToSubApproval(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetApplyPurchaseService.editAssetPurchaseToSubApproval(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryAssetListPurchaseDetailsById
     * @Description: 资产采购申请详情
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetApplyPurchaseController/queryAssetListPurchaseDetailsById")
    @ResponseBody
    public void queryAssetListPurchaseDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetApplyPurchaseService.queryAssetListPurchaseDetailsById(inputObject, outputObject);
    }

    /**
     *
     * @Title: updateAssetPurchaseToCancellation
     * @Description: 作废资产采购申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetApplyPurchaseController/updateAssetPurchaseToCancellation")
    @ResponseBody
    public void updateAssetPurchaseToCancellation(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetApplyPurchaseService.updateAssetPurchaseToCancellation(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryAssetListPurchaseToEditById
     * @Description: 资产采购申请编辑时进行回显
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetApplyPurchaseController/queryAssetListPurchaseToEditById")
    @ResponseBody
    public void queryAssetListPurchaseToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetApplyPurchaseService.queryAssetListPurchaseToEditById(inputObject, outputObject);
    }

    /**
     *
     * @Title: updateAssetListToPurchaseById
     * @Description: 编辑资产采购申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetApplyPurchaseController/updateAssetListToPurchaseById")
    @ResponseBody
    public void updateAssetListToPurchaseById(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetApplyPurchaseService.updateAssetListToPurchaseById(inputObject, outputObject);
    }

    /**
     *
     * @Title: updateAssetListToPurchaseByIdInProcess
     * @Description: 在工作流中编辑资产采购申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetApplyPurchaseController/updateAssetListToPurchaseByIdInProcess")
    @ResponseBody
    public void updateAssetListToPurchaseByIdInProcess(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetApplyPurchaseService.updateAssetListToPurchaseByIdInProcess(inputObject, outputObject);
    }

    /**
     *
     * @Title: editAssetPurchaseToRevoke
     * @Description: 撤销资产采购申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetApplyPurchaseController/editAssetPurchaseToRevoke")
    @ResponseBody
    public void editAssetPurchaseToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetApplyPurchaseService.editAssetPurchaseToRevoke(inputObject, outputObject);
    }

}
