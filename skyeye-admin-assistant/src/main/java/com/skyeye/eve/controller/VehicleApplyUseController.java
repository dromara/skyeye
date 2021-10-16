/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.VehicleApplyUseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ClassName: VehicleApplyUseController
 * @Description: 用车申请控制类
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/1 17:48
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Controller
public class VehicleApplyUseController {

    @Autowired
    private VehicleApplyUseService vehicleApplyUseService;

    /**
     *
     * @Title: queryMyUseVehicleMation
     * @Description: 获取我的用车申请信息列表
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/VehicleApplyUseController/queryMyUseVehicleMation")
    @ResponseBody
    public void queryMyUseVehicleMation(InputObject inputObject, OutputObject outputObject) throws Exception{
        vehicleApplyUseService.queryMyUseVehicleMation(inputObject, outputObject);
    }

    /**
     *
     * @Title: insertVehicleMationToUse
     * @Description: 用车申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/VehicleApplyUseController/insertVehicleMationToUse")
    @ResponseBody
    public void insertVehicleMationToUse(InputObject inputObject, OutputObject outputObject) throws Exception{
        vehicleApplyUseService.insertVehicleMationToUse(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryVehicleUseDetails
     * @Description: 用车申请详情
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/VehicleApplyUseController/queryVehicleUseDetails")
    @ResponseBody
    public void queryVehicleUseDetails(InputObject inputObject, OutputObject outputObject) throws Exception{
        vehicleApplyUseService.queryVehicleUseDetails(inputObject, outputObject);
    }

    /**
     *
     * @Title: editVehicleUseToSubApproval
     * @Description: 用车申请提交审批
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/VehicleApplyUseController/editVehicleUseToSubApproval")
    @ResponseBody
    public void editVehicleUseToSubApproval(InputObject inputObject, OutputObject outputObject) throws Exception{
        vehicleApplyUseService.editVehicleUseToSubApproval(inputObject, outputObject);
    }

    /**
     *
     * @Title: updateVehicleUseToCancellation
     * @Description: 作废用车申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/VehicleApplyUseController/updateVehicleUseToCancellation")
    @ResponseBody
    public void updateVehicleUseToCancellation(InputObject inputObject, OutputObject outputObject) throws Exception{
        vehicleApplyUseService.updateVehicleUseToCancellation(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryVehicleUseMationToEdit
     * @Description: 获取用车申请信息用于编辑回显
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/VehicleApplyUseController/queryVehicleUseMationToEdit")
    @ResponseBody
    public void queryVehicleUseMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception{
        vehicleApplyUseService.queryVehicleUseMationToEdit(inputObject, outputObject);
    }

    /**
     *
     * @Title: updateVehicleUseMationToEdit
     * @Description: 用车申请编辑
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/VehicleApplyUseController/updateVehicleUseMationToEdit")
    @ResponseBody
    public void updateVehicleUseMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception{
        vehicleApplyUseService.updateVehicleUseMationToEdit(inputObject, outputObject);
    }

    /**
     *
     * @Title: updateVehicleUseMationByIdInProcess
     * @Description: 在工作流中编辑用车申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/VehicleApplyUseController/updateVehicleUseMationByIdInProcess")
    @ResponseBody
    public void updateVehicleUseMationByIdInProcess(InputObject inputObject, OutputObject outputObject) throws Exception{
        vehicleApplyUseService.updateVehicleUseMationByIdInProcess(inputObject, outputObject);
    }

    /**
     *
     * @Title: updateVehicleUseToRevoke
     * @Description: 撤销用车申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/VehicleApplyUseController/updateVehicleUseToRevoke")
    @ResponseBody
    public void updateVehicleUseToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception{
        vehicleApplyUseService.updateVehicleUseToRevoke(inputObject, outputObject);
    }

}
