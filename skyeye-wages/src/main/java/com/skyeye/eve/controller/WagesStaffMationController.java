/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.WagesStaffMationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class WagesStaffMationController {

    @Autowired
    private WagesStaffMationService wagesStaffMationService;

    /**
     *
     * @Title: queryWagesStaffWaitAllocatedMationList
     * @Description: 获取待设定薪资员工列表
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/WagesStaffMationController/queryWagesStaffWaitAllocatedMationList")
    @ResponseBody
    public void queryWagesStaffWaitAllocatedMationList(InputObject inputObject, OutputObject outputObject) throws Exception{
        wagesStaffMationService.queryWagesStaffWaitAllocatedMationList(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryStaffWagesModelFieldMationListByStaffId
     * @Description: 根据员工id获取该员工拥有的薪资字段
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/WagesStaffMationController/queryStaffWagesModelFieldMationListByStaffId")
    @ResponseBody
    public void queryStaffWagesModelFieldMationListByStaffId(InputObject inputObject, OutputObject outputObject) throws Exception{
        wagesStaffMationService.queryStaffWagesModelFieldMationListByStaffId(inputObject, outputObject);
    }

    /**
     *
     * @Title: saveStaffWagesModelFieldMation
     * @Description: 保存员工薪资设定
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/WagesStaffMationController/saveStaffWagesModelFieldMation")
    @ResponseBody
    public void saveStaffWagesModelFieldMation(InputObject inputObject, OutputObject outputObject) throws Exception{
        wagesStaffMationService.saveStaffWagesModelFieldMation(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryWagesStaffDesignMationList
     * @Description: 获取已设定薪资员工列表
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/WagesStaffMationController/queryWagesStaffDesignMationList")
    @ResponseBody
    public void queryWagesStaffDesignMationList(InputObject inputObject, OutputObject outputObject) throws Exception{
        wagesStaffMationService.queryWagesStaffDesignMationList(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryWagesStaffPaymentDetail
     * @Description: 获取员工薪资条薪资
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/WagesStaffMationController/queryWagesStaffPaymentDetail")
    @ResponseBody
    public void queryWagesStaffPaymentDetail(InputObject inputObject, OutputObject outputObject) throws Exception{
        wagesStaffMationService.queryWagesStaffPaymentDetail(inputObject, outputObject);
    }

}
