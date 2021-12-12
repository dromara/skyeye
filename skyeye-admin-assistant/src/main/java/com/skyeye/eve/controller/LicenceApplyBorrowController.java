/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.LicenceApplyBorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ClassName: LicenceApplyBorrowController
 * @Description: 证照借用控制层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/24 22:57
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Controller
public class LicenceApplyBorrowController {

    @Autowired
    private LicenceApplyBorrowService licenceApplyBorrowService;

    /**
     *
     * @Title: queryMyBorrowLicenceList
     * @Description: 获取我借用的证照列表
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/LicenceApplyBorrowController/queryMyBorrowLicenceList")
    @ResponseBody
    public void queryMyBorrowLicenceList(InputObject inputObject, OutputObject outputObject) throws Exception{
        licenceApplyBorrowService.queryMyBorrowLicenceList(inputObject, outputObject);
    }
    /**
     *
     * @Title: insertBorrowLicenceMation
     * @Description: 证照借用申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/LicenceApplyBorrowController/insertBorrowLicenceMation")
    @ResponseBody
    public void insertBorrowLicenceMation(InputObject inputObject, OutputObject outputObject) throws Exception{
        licenceApplyBorrowService.insertBorrowLicenceMation(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryBorrowLicenceMationToDetails
     * @Description: 证照借用申请详情
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/LicenceApplyBorrowController/queryBorrowLicenceMationToDetails")
    @ResponseBody
    public void queryBorrowLicenceMationToDetails(InputObject inputObject, OutputObject outputObject) throws Exception{
        licenceApplyBorrowService.queryBorrowLicenceMationToDetails(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryBorrowLicenceMationToEdit
     * @Description: 证照借用申请编辑时进行回显
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/LicenceApplyBorrowController/queryBorrowLicenceMationToEdit")
    @ResponseBody
    public void queryBorrowLicenceMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception{
        licenceApplyBorrowService.queryBorrowLicenceMationToEdit(inputObject, outputObject);
    }

    /**
     *
     * @Title: updateBorrowLicenceMationById
     * @Description: 编辑证照借用申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/LicenceApplyBorrowController/updateBorrowLicenceMationById")
    @ResponseBody
    public void updateBorrowLicenceMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
        licenceApplyBorrowService.updateBorrowLicenceMationById(inputObject, outputObject);
    }

    /**
     *
     * @Title: editBorrowLicenceToSubApproval
     * @Description: 证照借用申请提交审批
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/LicenceApplyBorrowController/editBorrowLicenceToSubApproval")
    @ResponseBody
    public void editBorrowLicenceToSubApproval(InputObject inputObject, OutputObject outputObject) throws Exception{
        licenceApplyBorrowService.editBorrowLicenceToSubApproval(inputObject, outputObject);
    }

    /**
     *
     * @Title: updateBorrowLicenceToCancellation
     * @Description: 作废证照借用申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/LicenceApplyBorrowController/updateBorrowLicenceToCancellation")
    @ResponseBody
    public void updateBorrowLicenceToCancellation(InputObject inputObject, OutputObject outputObject) throws Exception{
        licenceApplyBorrowService.updateBorrowLicenceToCancellation(inputObject, outputObject);
    }

    /**
     *
     * @Title: updateBorrowLicenceMationToRevoke
     * @Description: 撤销证照借用申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/LicenceApplyBorrowController/updateBorrowLicenceMationToRevoke")
    @ResponseBody
    public void updateBorrowLicenceMationToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception{
        licenceApplyBorrowService.updateBorrowLicenceMationToRevoke(inputObject, outputObject);
    }

}
