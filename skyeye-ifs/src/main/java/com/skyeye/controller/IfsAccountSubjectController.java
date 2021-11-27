/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.service.IfsAccountSubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IfsAccountSubjectController {

    @Autowired
    private IfsAccountSubjectService ifsAccountSubjectService;

    /**
     * @param inputObject
     * @param outputObject
     * @return void    返回类型
     * @throws Exception 参数
     * @throws
     * @Title: queryIfsAccountSubjectList
     * @Description: 获取会计科目管理列表
     */
    @RequestMapping("/post/IfsAccountSubjectController/queryIfsAccountSubjectList")
    @ResponseBody
    public void queryIfsAccountSubjectList(InputObject inputObject, OutputObject outputObject) throws Exception {
        ifsAccountSubjectService.queryIfsAccountSubjectList(inputObject, outputObject);
    }

    /**
     * @param inputObject
     * @param outputObject
     * @return void    返回类型
     * @throws Exception 参数
     * @throws
     * @Title: insertIfsAccountSubjectMation
     * @Description: 新增会计科目管理信息
     */
    @RequestMapping("/post/IfsAccountSubjectController/insertIfsAccountSubjectMation")
    @ResponseBody
    public void insertIfsAccountSubjectMation(InputObject inputObject, OutputObject outputObject) throws Exception {
        ifsAccountSubjectService.insertIfsAccountSubjectMation(inputObject, outputObject);
    }

    /**
     * @param inputObject
     * @param outputObject
     * @return void    返回类型
     * @throws Exception 参数
     * @throws
     * @Title: deleteIfsAccountSubjectById
     * @Description: 删除会计科目管理信息
     */
    @RequestMapping("/post/IfsAccountSubjectController/deleteIfsAccountSubjectById")
    @ResponseBody
    public void deleteIfsAccountSubjectById(InputObject inputObject, OutputObject outputObject) throws Exception {
        ifsAccountSubjectService.deleteIfsAccountSubjectById(inputObject, outputObject);
    }

    /**
     * @param inputObject
     * @param outputObject
     * @return void    返回类型
     * @throws Exception 参数
     * @throws
     * @Title: selectIfsAccountSubjectById
     * @Description: 通过id查找对应的会计科目管理信息
     */
    @RequestMapping("/post/IfsAccountSubjectController/selectIfsAccountSubjectById")
    @ResponseBody
    public void selectIfsAccountSubjectById(InputObject inputObject, OutputObject outputObject) throws Exception {
        ifsAccountSubjectService.selectIfsAccountSubjectById(inputObject, outputObject);
    }

    /**
     * @param inputObject
     * @param outputObject
     * @return void    返回类型
     * @throws Exception 参数
     * @throws
     * @Title: editIfsAccountSubjectMationById
     * @Description: 通过id编辑对应的会计科目管理信息
     */
    @RequestMapping("/post/IfsAccountSubjectController/editIfsAccountSubjectMationById")
    @ResponseBody
    public void editIfsAccountSubjectMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        ifsAccountSubjectService.editIfsAccountSubjectMationById(inputObject, outputObject);
    }

}
