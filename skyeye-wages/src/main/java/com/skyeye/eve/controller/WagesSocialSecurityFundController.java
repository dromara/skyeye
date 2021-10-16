/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.WagesSocialSecurityFundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class WagesSocialSecurityFundController {

    @Autowired
    private WagesSocialSecurityFundService wagesSocialSecurityFundService;

    /**
     *
     * @Title: queryWagesSocialSecurityFundList
     * @Description: 获取社保公积金模板列表
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/WagesSocialSecurityFundController/queryWagesSocialSecurityFundList")
    @ResponseBody
    public void queryWagesSocialSecurityFundList(InputObject inputObject, OutputObject outputObject) throws Exception{
        wagesSocialSecurityFundService.queryWagesSocialSecurityFundList(inputObject, outputObject);
    }

    /**
     *
     * @Title: insertWagesSocialSecurityFundMation
     * @Description: 新增社保公积金模板信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/WagesSocialSecurityFundController/insertWagesSocialSecurityFundMation")
    @ResponseBody
    public void insertWagesSocialSecurityFundMation(InputObject inputObject, OutputObject outputObject) throws Exception{
        wagesSocialSecurityFundService.insertWagesSocialSecurityFundMation(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryWagesSocialSecurityFundMationToEditById
     * @Description: 编辑社保公积金模板信息时进行回显
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/WagesSocialSecurityFundController/queryWagesSocialSecurityFundMationToEditById")
    @ResponseBody
    public void queryWagesSocialSecurityFundMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
        wagesSocialSecurityFundService.queryWagesSocialSecurityFundMationToEditById(inputObject, outputObject);
    }

    /**
     *
     * @Title: editWagesSocialSecurityFundMationById
     * @Description: 编辑社保公积金模板信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/WagesSocialSecurityFundController/editWagesSocialSecurityFundMationById")
    @ResponseBody
    public void editWagesSocialSecurityFundMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
        wagesSocialSecurityFundService.editWagesSocialSecurityFundMationById(inputObject, outputObject);
    }

    /**
     *
     * @Title: deleteWagesSocialSecurityFundMationById
     * @Description: 删除社保公积金模板信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/WagesSocialSecurityFundController/deleteWagesSocialSecurityFundMationById")
    @ResponseBody
    public void deleteWagesSocialSecurityFundMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
        wagesSocialSecurityFundService.deleteWagesSocialSecurityFundMationById(inputObject, outputObject);
    }

    /**
     *
     * @Title: enableWagesSocialSecurityFundMationById
     * @Description: 启用社保公积金模板信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/WagesSocialSecurityFundController/enableWagesSocialSecurityFundMationById")
    @ResponseBody
    public void enableWagesSocialSecurityFundMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
        wagesSocialSecurityFundService.enableWagesSocialSecurityFundMationById(inputObject, outputObject);
    }

    /**
     *
     * @Title: disableWagesSocialSecurityFundMationById
     * @Description: 禁用社保公积金模板信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/WagesSocialSecurityFundController/disableWagesSocialSecurityFundMationById")
    @ResponseBody
    public void disableWagesSocialSecurityFundMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
        wagesSocialSecurityFundService.disableWagesSocialSecurityFundMationById(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryWagesSocialSecurityFundMationById
     * @Description: 社保公积金模板详情信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/WagesSocialSecurityFundController/queryWagesSocialSecurityFundMationById")
    @ResponseBody
    public void queryWagesSocialSecurityFundMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
        wagesSocialSecurityFundService.queryWagesSocialSecurityFundMationById(inputObject, outputObject);
    }

}
