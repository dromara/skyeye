/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.service.CrmCustomerTypeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CrmCustomerTypeController {

    @Autowired
    private CrmCustomerTypeService crmCustomerTypeService;

    /**
     *
     * @Title: insertCustomerType
     * @Description: 添加客户类型信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CrmCustomerTypeController/insertCustomerType")
    @ResponseBody
    public void insertCustomerType(InputObject inputObject, OutputObject outputObject) throws Exception {
        crmCustomerTypeService.insertCustomerType(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryCustomerTypeList
     * @Description: 获取所有客户类型状态为未被删除的记录
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CrmCustomerTypeController/queryCustomerTypeList")
    @ResponseBody
    public void queryCustomerTypeList(InputObject inputObject, OutputObject outputObject) throws Exception{
        crmCustomerTypeService.queryCustomerTypeList(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryStateUpList
     * @Description: 获取客户类型状态为上线的所有记录
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CrmCustomerTypeController/queryStateUpList")
    @ResponseBody
    public void queryStateUpList(InputObject inputObject, OutputObject outputObject) throws Exception{
        crmCustomerTypeService.queryStateUpList(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryCustomerTypeMationById
     * @Description: 通过客户类型id查询id和name
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CrmCustomerTypeController/queryCustomerTypeMationById")
    @ResponseBody
    public void queryCustomerTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        crmCustomerTypeService.queryCustomerTypeMationById(inputObject, outputObject);
    }

    /**
     *
     * @Title: editCustomerTypeById
     * @Description: 编辑客户类型名称
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CrmCustomerTypeController/editCustomerTypeById")
    @ResponseBody
    public void editCustomerTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
        crmCustomerTypeService.editCustomerTypeById(inputObject, outputObject);
    }

    /**
     *
     * @Title: editStateUpById
     * @Description: 编辑客户类型状态为上线
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CrmCustomerTypeController/editStateUpById")
    @ResponseBody
    public void editStateUpById(InputObject inputObject, OutputObject outputObject) throws Exception {
        crmCustomerTypeService.editStateUpById(inputObject, outputObject);
    }

    /**
     *
     * @Title: editStateDownById
     * @Description: 编辑客户类型状态为下线
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CrmCustomerTypeController/editStateDownById")
    @ResponseBody
    public void editStateDownById(InputObject inputObject, OutputObject outputObject) throws Exception {
        crmCustomerTypeService.editStateDownById(inputObject, outputObject);
    }

    /**
     *
     * @Title: deleteCustomerTypeById
     * @Description: 编辑客户类型状态为删除
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CrmCustomerTypeController/deleteCustomerTypeById")
    @ResponseBody
    public void deleteCustomerTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
        crmCustomerTypeService.deleteCustomerTypeById(inputObject, outputObject);
    }

}
