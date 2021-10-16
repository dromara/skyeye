/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.AssetApplyReturnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ClassName: AssetApplyReturnController
 * @Description: 资产归还单控制层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/20 22:36
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Controller
public class AssetApplyReturnController {

    @Autowired
    private AssetApplyReturnService assetApplyReturnService;

    /**
     *
     * @Title: queryMyReturnAssetMation
     * @Description: 获取我归还的资产信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetApplyReturnController/queryMyReturnAssetMation")
    @ResponseBody
    public void queryMyReturnAssetMation(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetApplyReturnService.queryMyReturnAssetMation(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryMyUnReturnAssetListByTypeId
     * @Description: 根据资产类别获取我领用后未归还的资产列表信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetApplyReturnController/queryMyUnReturnAssetListByTypeId")
    @ResponseBody
    public void queryMyUnReturnAssetListByTypeId(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetApplyReturnService.queryMyUnReturnAssetListByTypeId(inputObject, outputObject);
    }

    /**
     *
     * @Title: insertAssetListToReturn
     * @Description: 资产归还申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetApplyReturnController/insertAssetListToReturn")
    @ResponseBody
    public void insertAssetListToReturn(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetApplyReturnService.insertAssetListToReturn(inputObject, outputObject);
    }

    /**
     *
     * @Title: editAssetReturnToSubApproval
     * @Description: 资产归还申请提交审批
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetApplyReturnController/editAssetReturnToSubApproval")
    @ResponseBody
    public void editAssetReturnToSubApproval(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetApplyReturnService.editAssetReturnToSubApproval(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryAssetListReturnDetailsById
     * @Description: 资产归还申请详情
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetApplyReturnController/queryAssetListReturnDetailsById")
    @ResponseBody
    public void queryAssetListReturnDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetApplyReturnService.queryAssetListReturnDetailsById(inputObject, outputObject);
    }

    /**
     *
     * @Title: updateAssetReturnToCancellation
     * @Description: 作废资产归还申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetApplyReturnController/updateAssetReturnToCancellation")
    @ResponseBody
    public void updateAssetReturnToCancellation(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetApplyReturnService.updateAssetReturnToCancellation(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryAssetListReturnToEditById
     * @Description: 资产归还申请编辑时进行回显
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetApplyReturnController/queryAssetListReturnToEditById")
    @ResponseBody
    public void queryAssetListReturnToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetApplyReturnService.queryAssetListReturnToEditById(inputObject, outputObject);
    }

    /**
     *
     * @Title: updateAssetListToReturnById
     * @Description: 编辑资产归还申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetApplyReturnController/updateAssetListToReturnById")
    @ResponseBody
    public void updateAssetListToReturnById(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetApplyReturnService.updateAssetListToReturnById(inputObject, outputObject);
    }

    /**
     *
     * @Title: updateAssetListToReturnByIdInProcess
     * @Description: 在工作流中编辑资产归还申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetApplyReturnController/updateAssetListToReturnByIdInProcess")
    @ResponseBody
    public void updateAssetListToReturnByIdInProcess(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetApplyReturnService.updateAssetListToReturnByIdInProcess(inputObject, outputObject);
    }

    /**
     *
     * @Title: editAssetReturnToRevoke
     * @Description: 撤销资产归还申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetApplyReturnController/editAssetReturnToRevoke")
    @ResponseBody
    public void editAssetReturnToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetApplyReturnService.editAssetReturnToRevoke(inputObject, outputObject);
    }


}
