/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.MailGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ClassName: MailGroupController
 * @Description: 通讯录分组管理控制类
 * @author: skyeye云系列--卫志强
 * @date: 2021/10/23 12:55
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Controller
public class MailGroupController {

    @Autowired
    private MailGroupService mailGroupService;

    /**
     *
     * @Title: queryMailMationTypeList
     * @Description: 获取我的通讯录类别列表
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/MailGroupController/queryMailMationTypeList")
    @ResponseBody
    public void queryMailMationTypeList(InputObject inputObject, OutputObject outputObject) throws Exception{
        mailGroupService.queryMailMationTypeList(inputObject, outputObject);
    }

    /**
     *
     * @Title: insertMailMationType
     * @Description: 新增通讯录类型
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/MailGroupController/insertMailMationType")
    @ResponseBody
    public void insertMailMationType(InputObject inputObject, OutputObject outputObject) throws Exception{
        mailGroupService.insertMailMationType(inputObject, outputObject);
    }

    /**
     *
     * @Title: deleteMailMationTypeById
     * @Description: 删除通讯录类型
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/MailGroupController/deleteMailMationTypeById")
    @ResponseBody
    public void deleteMailMationTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
        mailGroupService.deleteMailMationTypeById(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryMailMationTypeToEditById
     * @Description: 编辑通讯录类型进行回显
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/MailGroupController/queryMailMationTypeToEditById")
    @ResponseBody
    public void queryMailMationTypeToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
        mailGroupService.queryMailMationTypeToEditById(inputObject, outputObject);
    }

    /**
     *
     * @Title: editMailMationTypeById
     * @Description: 编辑通讯录类型
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/MailGroupController/editMailMationTypeById")
    @ResponseBody
    public void editMailMationTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
        mailGroupService.editMailMationTypeById(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryMailMationTypeListToSelect
     * @Description: 获取我的通讯录类型用作下拉框展示
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/MailGroupController/queryMailMationTypeListToSelect")
    @ResponseBody
    public void queryMailMationTypeListToSelect(InputObject inputObject, OutputObject outputObject) throws Exception{
        mailGroupService.queryMailMationTypeListToSelect(inputObject, outputObject);
    }


}
