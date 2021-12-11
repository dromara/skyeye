/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.SealApplyRevertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ClassName: SealApplyRevertController
 * @Description: 印章归还申请控制类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/24 17:39
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Controller
public class SealApplyRevertController {

    @Autowired
    private SealApplyRevertService sealApplyRevertService;

    /**
     *
     * @Title: queryMyRevertSealList
     * @Description: 获取我归还的印章列表
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealApplyRevertController/queryMyRevertSealList")
    @ResponseBody
    public void queryMyRevertSealList(InputObject inputObject, OutputObject outputObject) throws Exception{
        sealApplyRevertService.queryMyRevertSealList(inputObject, outputObject);
    }

    /**
     *
     * @Title: insertRevertSealMation
     * @Description: 印章归还申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealApplyRevertController/insertRevertSealMation")
    @ResponseBody
    public void insertRevertSealMation(InputObject inputObject, OutputObject outputObject) throws Exception{
        sealApplyRevertService.insertRevertSealMation(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryRevertSealMationToDetails
     * @Description: 印章归还申请详情
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealApplyRevertController/queryRevertSealMationToDetails")
    @ResponseBody
    public void queryRevertSealMationToDetails(InputObject inputObject, OutputObject outputObject) throws Exception{
        sealApplyRevertService.queryRevertSealMationToDetails(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryRevertSealMationToEdit
     * @Description: 印章归还申请编辑时进行回显
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealApplyRevertController/queryRevertSealMationToEdit")
    @ResponseBody
    public void queryRevertSealMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception{
        sealApplyRevertService.queryRevertSealMationToEdit(inputObject, outputObject);
    }

    /**
     *
     * @Title: updateRevertSealMationById
     * @Description: 编辑印章归还申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealApplyRevertController/updateRevertSealMationById")
    @ResponseBody
    public void updateRevertSealMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
        sealApplyRevertService.updateRevertSealMationById(inputObject, outputObject);
    }

    /**
     *
     * @Title: editRevertSealToSubApproval
     * @Description: 印章归还申请提交审批
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealApplyRevertController/editRevertSealToSubApproval")
    @ResponseBody
    public void editRevertSealToSubApproval(InputObject inputObject, OutputObject outputObject) throws Exception{
        sealApplyRevertService.editRevertSealToSubApproval(inputObject, outputObject);
    }

    /**
     *
     * @Title: updateRevertSealToCancellation
     * @Description: 作废印章归还申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealApplyRevertController/updateRevertSealToCancellation")
    @ResponseBody
    public void updateRevertSealToCancellation(InputObject inputObject, OutputObject outputObject) throws Exception{
        sealApplyRevertService.updateRevertSealToCancellation(inputObject, outputObject);
    }

    /**
     *
     * @Title: updateRevertSealToRevoke
     * @Description: 撤销印章归还申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealApplyRevertController/updateRevertSealToRevoke")
    @ResponseBody
    public void updateRevertSealToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception{
        sealApplyRevertService.updateRevertSealToRevoke(inputObject, outputObject);
    }

}
