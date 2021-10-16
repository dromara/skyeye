/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.AssetApplyUseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ClassName: AssetApplyUseController
 * @Description: 资产领用申请控制类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/18 17:10
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Controller
public class AssetApplyUseController {
    
    @Autowired
    private AssetApplyUseService assetApplyUseService;

    /**
     *
     * @Title: queryMyUseAssetMation
     * @Description: 获取我领用的资产信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetApplyUseController/queryMyUseAssetMation")
    @ResponseBody
    public void queryMyUseAssetMation(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetApplyUseService.queryMyUseAssetMation(inputObject, outputObject);
    }

    /**
     *
     * @Title: insertAssetListToUse
     * @Description: 资产领用申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetApplyUseController/insertAssetListToUse")
    @ResponseBody
    public void insertAssetListToUse(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetApplyUseService.insertAssetListToUse(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryAssetListUseDetailsById
     * @Description: 资产领用申请详情
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetApplyUseController/queryAssetListUseDetailsById")
    @ResponseBody
    public void queryAssetListUseDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetApplyUseService.queryAssetListUseDetailsById(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryAssetListUseToEditById
     * @Description: 资产领用申请编辑时进行回显
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetApplyUseController/queryAssetListUseToEditById")
    @ResponseBody
    public void queryAssetListUseToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetApplyUseService.queryAssetListUseToEditById(inputObject, outputObject);
    }

    /**
     *
     * @Title: updateAssetListToUseById
     * @Description: 编辑资产领用申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetApplyUseController/updateAssetListToUseById")
    @ResponseBody
    public void updateAssetListToUseById(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetApplyUseService.updateAssetListToUseById(inputObject, outputObject);
    }

    /**
     *
     * @Title: updateAssetToCancellation
     * @Description: 作废资产领用申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetApplyUseController/updateAssetToCancellation")
    @ResponseBody
    public void updateAssetToCancellation(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetApplyUseService.updateAssetToCancellation(inputObject, outputObject);
    }

    /**
     *
     * @Title: editAssetUseToSubApproval
     * @Description: 资产领用申请提交审批
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetApplyUseController/editAssetUseToSubApproval")
    @ResponseBody
    public void editAssetUseToSubApproval(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetApplyUseService.editAssetUseToSubApproval(inputObject, outputObject);
    }

    /**
     *
     * @Title: updateAssetListToUseByIdInProcess
     * @Description: 在工作流中编辑资产领用申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetApplyUseController/updateAssetListToUseByIdInProcess")
    @ResponseBody
    public void updateAssetListToUseByIdInProcess(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetApplyUseService.updateAssetListToUseByIdInProcess(inputObject, outputObject);
    }

    /**
     *
     * @Title: editAssetUseToRevoke
     * @Description: 撤销资产领用申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetApplyUseController/editAssetUseToRevoke")
    @ResponseBody
    public void editAssetUseToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetApplyUseService.editAssetUseToRevoke(inputObject, outputObject);
    }
    
}
