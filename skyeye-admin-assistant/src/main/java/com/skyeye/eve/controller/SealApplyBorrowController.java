/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.SealApplyBorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ClassName: SealApplyBorrowController
 * @Description: 印章借用申请控制类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/24 15:56
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Controller
public class SealApplyBorrowController {

    @Autowired
    private SealApplyBorrowService sealApplyBorrowService;

    /**
     *
     * @Title: queryMyBorrowSealList
     * @Description: 获取我借用的印章列表
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealApplyBorrowController/queryMyBorrowSealList")
    @ResponseBody
    public void queryMyBorrowSealList(InputObject inputObject, OutputObject outputObject) throws Exception{
        sealApplyBorrowService.queryMyBorrowSealList(inputObject, outputObject);
    }

    /**
     *
     * @Title: insertBorrowSealMation
     * @Description: 印章借用申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealApplyBorrowController/insertBorrowSealMation")
    @ResponseBody
    public void insertBorrowSealMation(InputObject inputObject, OutputObject outputObject) throws Exception{
        sealApplyBorrowService.insertBorrowSealMation(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryBorrowSealMationToDetails
     * @Description: 印章借用申请详情
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealApplyBorrowController/queryBorrowSealMationToDetails")
    @ResponseBody
    public void queryBorrowSealMationToDetails(InputObject inputObject, OutputObject outputObject) throws Exception{
        sealApplyBorrowService.queryBorrowSealMationToDetails(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryBorrowSealMationToEdit
     * @Description: 印章借用申请编辑时进行回显
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealApplyBorrowController/queryBorrowSealMationToEdit")
    @ResponseBody
    public void queryBorrowSealMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception{
        sealApplyBorrowService.queryBorrowSealMationToEdit(inputObject, outputObject);
    }

    /**
     *
     * @Title: updateBorrowSealMationById
     * @Description: 编辑印章借用申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealApplyBorrowController/updateBorrowSealMationById")
    @ResponseBody
    public void updateBorrowSealMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
        sealApplyBorrowService.updateBorrowSealMationById(inputObject, outputObject);
    }

    /**
     *
     * @Title: editBorrowSealToSubApproval
     * @Description: 印章借用申请提交审批
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealApplyBorrowController/editBorrowSealToSubApproval")
    @ResponseBody
    public void editBorrowSealToSubApproval(InputObject inputObject, OutputObject outputObject) throws Exception{
        sealApplyBorrowService.editBorrowSealToSubApproval(inputObject, outputObject);
    }

    /**
     *
     * @Title: updateBorrowSealToCancellation
     * @Description: 作废印章借用申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealApplyBorrowController/updateBorrowSealToCancellation")
    @ResponseBody
    public void updateBorrowSealToCancellation(InputObject inputObject, OutputObject outputObject) throws Exception{
        sealApplyBorrowService.updateBorrowSealToCancellation(inputObject, outputObject);
    }

    /**
     *
     * @Title: updateBorrowSealToRevoke
     * @Description: 撤销印章借用申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealApplyBorrowController/updateBorrowSealToRevoke")
    @ResponseBody
    public void updateBorrowSealToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception{
        sealApplyBorrowService.updateBorrowSealToRevoke(inputObject, outputObject);
    }

}
