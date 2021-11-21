/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.service.IfsSetOfBooksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IfsSetOfBooksController {

    @Autowired
    private IfsSetOfBooksService ifsSetOfBooksService;

    /**
     * @param inputObject
     * @param outputObject
     * @return void    返回类型
     * @throws Exception 参数
     * @throws
     * @Title: queryIfsSetOfBooksList
     * @Description: 获取账套管理列表
     */
    @RequestMapping("/post/IfsSetOfBooksController/queryIfsSetOfBooksList")
    @ResponseBody
    public void queryIfsSetOfBooksList(InputObject inputObject, OutputObject outputObject) throws Exception {
        ifsSetOfBooksService.queryIfsSetOfBooksList(inputObject, outputObject);
    }

    /**
     * @param inputObject
     * @param outputObject
     * @return void    返回类型
     * @throws Exception 参数
     * @throws
     * @Title: insertIfsSetOfBooksMation
     * @Description: 新增账套管理信息
     */
    @RequestMapping("/post/IfsSetOfBooksController/insertIfsSetOfBooksMation")
    @ResponseBody
    public void insertIfsSetOfBooksMation(InputObject inputObject, OutputObject outputObject) throws Exception {
        ifsSetOfBooksService.insertIfsSetOfBooksMation(inputObject, outputObject);
    }

    /**
     * @param inputObject
     * @param outputObject
     * @return void    返回类型
     * @throws Exception 参数
     * @throws
     * @Title: deleteIfsSetOfBooksById
     * @Description: 删除账套管理信息
     */
    @RequestMapping("/post/IfsSetOfBooksController/deleteIfsSetOfBooksById")
    @ResponseBody
    public void deleteIfsSetOfBooksById(InputObject inputObject, OutputObject outputObject) throws Exception {
        ifsSetOfBooksService.deleteIfsSetOfBooksById(inputObject, outputObject);
    }

    /**
     * @param inputObject
     * @param outputObject
     * @return void    返回类型
     * @throws Exception 参数
     * @throws
     * @Title: selectIfsSetOfBooksById
     * @Description: 通过id查找对应的账套管理信息
     */
    @RequestMapping("/post/IfsSetOfBooksController/selectIfsSetOfBooksById")
    @ResponseBody
    public void selectIfsSetOfBooksById(InputObject inputObject, OutputObject outputObject) throws Exception {
        ifsSetOfBooksService.selectIfsSetOfBooksById(inputObject, outputObject);
    }

    /**
     * @param inputObject
     * @param outputObject
     * @return void    返回类型
     * @throws Exception 参数
     * @throws
     * @Title: editIfsSetOfBooksMationById
     * @Description: 通过id编辑对应的账套管理信息
     */
    @RequestMapping("/post/IfsSetOfBooksController/editIfsSetOfBooksMationById")
    @ResponseBody
    public void editIfsSetOfBooksMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        ifsSetOfBooksService.editIfsSetOfBooksMationById(inputObject, outputObject);
    }

    /**
     * @param inputObject
     * @param outputObject
     * @return void    返回类型
     * @throws Exception 参数
     * @throws
     * @Title: selectIfsSetOfBooksMationById
     * @Description: 通过id查找对应的账套管理信息详情
     */
    @RequestMapping("/post/IfsSetOfBooksController/selectIfsSetOfBooksMationById")
    @ResponseBody
    public void selectIfsSetOfBooksMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        ifsSetOfBooksService.selectIfsSetOfBooksMationById(inputObject, outputObject);
    }

}
