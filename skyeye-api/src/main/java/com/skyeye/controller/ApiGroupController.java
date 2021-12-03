/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.service.ApiGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ApiGroupController {

    @Autowired
    private ApiGroupService apiGroupService;

    /**
     * @param inputObject
     * @param outputObject
     * @return void    返回类型
     * @throws Exception 参数
     * @throws
     * @Title: queryApiGroupList
     * @Description: 获取api接口分组列表
     */
    @RequestMapping("/post/ApiGroupController/queryApiGroupList")
    @ResponseBody
    public void queryApiGroupList(InputObject inputObject, OutputObject outputObject) throws Exception {
        apiGroupService.queryApiGroupMationList(inputObject, outputObject);
    }

    /**
     * @param inputObject
     * @param outputObject
     * @return void    返回类型
     * @throws Exception 参数
     * @throws
     * @Title: insertApiGroupMation
     * @Description: 新增api接口分组信息
     */
    @RequestMapping("/post/ApiGroupController/insertApiGroupMation")
    @ResponseBody
    public void insertApiGroupMation(InputObject inputObject, OutputObject outputObject) throws Exception {
        apiGroupService.insertApiGroupMation(inputObject, outputObject);
    }

    /**
     * @param inputObject
     * @param outputObject
     * @return void    返回类型
     * @throws Exception 参数
     * @throws
     * @Title: deleteApiGroupById
     * @Description: 删除api接口分组信息
     */
    @RequestMapping("/post/ApiGroupController/deleteApiGroupById")
    @ResponseBody
    public void deleteApiGroupById(InputObject inputObject, OutputObject outputObject) throws Exception {
        apiGroupService.deleteApiGroupMationById(inputObject, outputObject);
    }

    /**
     * @param inputObject
     * @param outputObject
     * @return void    返回类型
     * @throws Exception 参数
     * @throws
     * @Title: selectApiGroupById
     * @Description: 通过id查找对应的api接口分组信息
     */
    @RequestMapping("/post/ApiGroupController/selectApiGroupById")
    @ResponseBody
    public void selectApiGroupById(InputObject inputObject, OutputObject outputObject) throws Exception {
        apiGroupService.selectApiGroupMationById(inputObject, outputObject);
    }

    /**
     * @param inputObject
     * @param outputObject
     * @return void    返回类型
     * @throws Exception 参数
     * @throws
     * @Title: editApiGroupMationById
     * @Description: 通过id编辑对应的api接口分组信息
     */
    @RequestMapping("/post/ApiGroupController/editApiGroupMationById")
    @ResponseBody
    public void editApiGroupMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        apiGroupService.editApiGroupMationById(inputObject, outputObject);
    }

}
