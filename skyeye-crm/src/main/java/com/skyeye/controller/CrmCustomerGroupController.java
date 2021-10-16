/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.service.CrmCustomerGroupService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CrmCustomerGroupController {

    @Autowired
    private CrmCustomerGroupService crmCustomerGroupService;

    /**
     *
     * @Title: insertCustomerGroup
     * @Description: 添加客户分组信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回分组
     * @throws
     */
    @RequestMapping("/post/CrmCustomerGroupController/insertCustomerGroup")
    @ResponseBody
    public void insertCustomerGroup(InputObject inputObject, OutputObject outputObject) throws Exception {
        crmCustomerGroupService.insertCustomerGroup(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryCustomerGroupList
     * @Description: 获取所有客户分组状态为未被删除的记录
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回分组
     * @throws
     */
    @RequestMapping("/post/CrmCustomerGroupController/queryCustomerGroupList")
    @ResponseBody
    public void queryCustomerGroupList(InputObject inputObject, OutputObject outputObject) throws Exception{
        crmCustomerGroupService.queryCustomerGroupList(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryStateUpList
     * @Description: 获取客户分组状态为上线的所有记录
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回分组
     * @throws
     */
    @RequestMapping("/post/CrmCustomerGroupController/queryStateUpList")
    @ResponseBody
    public void queryStateUpList(InputObject inputObject, OutputObject outputObject) throws Exception{
        crmCustomerGroupService.queryStateUpList(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryCustomerGroupMationById
     * @Description: 编辑时回显数据
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回分组
     * @throws
     */
    @RequestMapping("/post/CrmCustomerGroupController/queryCustomerGroupMationById")
    @ResponseBody
    public void queryCustomerGroupMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        crmCustomerGroupService.queryCustomerGroupMationById(inputObject, outputObject);
    }

    /**
     *
     * @Title: editCustomerGroupById
     * @Description: 编辑客户分组名称
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回分组
     * @throws
     */
    @RequestMapping("/post/CrmCustomerGroupController/editCustomerGroupById")
    @ResponseBody
    public void editCustomerGroupById(InputObject inputObject, OutputObject outputObject) throws Exception{
        crmCustomerGroupService.editCustomerGroupById(inputObject, outputObject);
    }

    /**
     *
     * @Title: editStateUpById
     * @Description: 上线
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回分组
     * @throws
     */
    @RequestMapping("/post/CrmCustomerGroupController/editStateUpById")
    @ResponseBody
    public void editStateUpById(InputObject inputObject, OutputObject outputObject) throws Exception {
        crmCustomerGroupService.editStateUpById(inputObject, outputObject);
    }

    /**
     *
     * @Title: editStateDownById
     * @Description: 下线
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回分组
     * @throws
     */
    @RequestMapping("/post/CrmCustomerGroupController/editStateDownById")
    @ResponseBody
    public void editStateDownById(InputObject inputObject, OutputObject outputObject) throws Exception {
        crmCustomerGroupService.editStateDownById(inputObject, outputObject);
    }

    /**
     *
     * @Title: deleteCustomerGroupById
     * @Description: 删除
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回分组
     * @throws
     */
    @RequestMapping("/post/CrmCustomerGroupController/deleteCustomerGroupById")
    @ResponseBody
    public void deleteCustomerGroupById(InputObject inputObject, OutputObject outputObject) throws Exception{
        crmCustomerGroupService.deleteCustomerGroupById(inputObject, outputObject);
    }

}
