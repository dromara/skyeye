/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.LicenceApplyRevertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ClassName: LicenceApplyRevertController
 * @Description: 证照归还申请控制类
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/1 10:48
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Controller
public class LicenceApplyRevertController {
    
    @Autowired
    private LicenceApplyRevertService licenceApplyRevertService;

    /**
     *
     * @Title: queryMyRevertLicenceList
     * @Description: 获取我归还的证照列表
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/LicenceApplyRevertController/queryMyRevertLicenceList")
    @ResponseBody
    public void queryMyRevertLicenceList(InputObject inputObject, OutputObject outputObject) throws Exception{
        licenceApplyRevertService.queryMyRevertLicenceList(inputObject, outputObject);
    }

    /**
     *
     * @Title: insertRevertLicenceMation
     * @Description: 证照归还申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/LicenceApplyRevertController/insertRevertLicenceMation")
    @ResponseBody
    public void insertRevertLicenceMation(InputObject inputObject, OutputObject outputObject) throws Exception{
        licenceApplyRevertService.insertRevertLicenceMation(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryRevertLicenceMationToDetails
     * @Description: 证照归还申请详情
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/LicenceApplyRevertController/queryRevertLicenceMationToDetails")
    @ResponseBody
    public void queryRevertLicenceMationToDetails(InputObject inputObject, OutputObject outputObject) throws Exception{
        licenceApplyRevertService.queryRevertLicenceMationToDetails(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryRevertLicenceMationToEdit
     * @Description: 证照归还申请编辑时进行回显
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/LicenceApplyRevertController/queryRevertLicenceMationToEdit")
    @ResponseBody
    public void queryRevertLicenceMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception{
        licenceApplyRevertService.queryRevertLicenceMationToEdit(inputObject, outputObject);
    }

    /**
     *
     * @Title: updateRevertLicenceMationById
     * @Description: 编辑证照归还申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/LicenceApplyRevertController/updateRevertLicenceMationById")
    @ResponseBody
    public void updateRevertLicenceMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
        licenceApplyRevertService.updateRevertLicenceMationById(inputObject, outputObject);
    }

    /**
     *
     * @Title: editRevertLicenceToSubApproval
     * @Description: 证照归还申请提交审批
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/LicenceApplyRevertController/editRevertLicenceToSubApproval")
    @ResponseBody
    public void editRevertLicenceToSubApproval(InputObject inputObject, OutputObject outputObject) throws Exception{
        licenceApplyRevertService.editRevertLicenceToSubApproval(inputObject, outputObject);
    }

    /**
     *
     * @Title: updateRevertLicenceToCancellation
     * @Description: 作废证照归还申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/LicenceApplyRevertController/updateRevertLicenceToCancellation")
    @ResponseBody
    public void updateRevertLicenceToCancellation(InputObject inputObject, OutputObject outputObject) throws Exception{
        licenceApplyRevertService.updateRevertLicenceToCancellation(inputObject, outputObject);
    }

    /**
     *
     * @Title: updateRevertLicenceMationToRevoke
     * @Description: 撤销证照归还申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/LicenceApplyRevertController/updateRevertLicenceMationToRevoke")
    @ResponseBody
    public void updateRevertLicenceMationToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception{
        licenceApplyRevertService.updateRevertLicenceMationToRevoke(inputObject, outputObject);
    }
    
}
