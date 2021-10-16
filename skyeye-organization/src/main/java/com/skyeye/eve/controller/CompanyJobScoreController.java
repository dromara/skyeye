/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.CompanyJobScoreService;

@Controller
public class CompanyJobScoreController {

    @Autowired
    private CompanyJobScoreService companyJobScoreService;

    /**
     *
     * @Title: queryCompanyJobScoreList
     * @Description: 获取职位定级信息列表
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CompanyJobScoreController/queryCompanyJobScoreList")
    @ResponseBody
    public void queryCompanyJobScoreList(InputObject inputObject, OutputObject outputObject) throws Exception{
        companyJobScoreService.queryCompanyJobScoreList(inputObject, outputObject);
    }

    /**
     *
     * @Title: insertCompanyJobScoreMation
     * @Description: 新增职位定级信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CompanyJobScoreController/insertCompanyJobScoreMation")
    @ResponseBody
    public void insertCompanyJobScoreMation(InputObject inputObject, OutputObject outputObject) throws Exception{
        companyJobScoreService.insertCompanyJobScoreMation(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryCompanyJobScoreMationToEditById
     * @Description: 编辑职位定级信息时进行回显
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CompanyJobScoreController/queryCompanyJobScoreMationToEditById")
    @ResponseBody
    public void queryCompanyJobScoreMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
        companyJobScoreService.queryCompanyJobScoreMationToEditById(inputObject, outputObject);
    }

    /**
     *
     * @Title: editCompanyJobScoreMationById
     * @Description: 编辑职位定级信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CompanyJobScoreController/editCompanyJobScoreMationById")
    @ResponseBody
    public void editCompanyJobScoreMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
        companyJobScoreService.editCompanyJobScoreMationById(inputObject, outputObject);
    }

    /**
     *
     * @Title: deleteCompanyJobScoreMationById
     * @Description: 删除职位定级信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CompanyJobScoreController/deleteCompanyJobScoreMationById")
    @ResponseBody
    public void deleteCompanyJobScoreMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
        companyJobScoreService.deleteCompanyJobScoreMationById(inputObject, outputObject);
    }

    /**
     *
     * @Title: enableCompanyJobScoreMationById
     * @Description: 启用职位定级信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CompanyJobScoreController/enableCompanyJobScoreMationById")
    @ResponseBody
    public void enableCompanyJobScoreMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
        companyJobScoreService.enableCompanyJobScoreMationById(inputObject, outputObject);
    }

    /**
     *
     * @Title: disableCompanyJobScoreMationById
     * @Description: 禁用职位定级信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CompanyJobScoreController/disableCompanyJobScoreMationById")
    @ResponseBody
    public void disableCompanyJobScoreMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
        companyJobScoreService.disableCompanyJobScoreMationById(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryEnableCompanyJobScoreList
     * @Description: 获取已经启用的职位定级信息列表
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CompanyJobScoreController/queryEnableCompanyJobScoreList")
    @ResponseBody
    public void queryEnableCompanyJobScoreList(InputObject inputObject, OutputObject outputObject) throws Exception{
        companyJobScoreService.queryEnableCompanyJobScoreList(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryCompanyJobScoreDetailMationById
     * @Description: 获取职位定级信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CompanyJobScoreController/queryCompanyJobScoreDetailMationById")
    @ResponseBody
    public void queryCompanyJobScoreDetailMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
        companyJobScoreService.queryCompanyJobScoreDetailMationById(inputObject, outputObject);
    }

}
