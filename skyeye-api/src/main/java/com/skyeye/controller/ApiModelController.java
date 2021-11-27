/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.service.ApiModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ApiModelController {

    @Autowired
    private ApiModelService apiModelService;

    /**
     * @param inputObject
     * @param outputObject
     * @return void    返回类型
     * @throws Exception 参数
     * @throws
     * @Title: queryApiModelList
     * @Description: 获取api接口模块列表
     */
    @RequestMapping("/post/ApiModelController/queryApiModelList")
    @ResponseBody
    public void queryApiModelList(InputObject inputObject, OutputObject outputObject) throws Exception {
        apiModelService.queryApiModelList(inputObject, outputObject);
    }

    /**
     * @param inputObject
     * @param outputObject
     * @return void    返回类型
     * @throws Exception 参数
     * @throws
     * @Title: insertApiModelMation
     * @Description: 新增api接口模块信息
     */
    @RequestMapping("/post/ApiModelController/insertApiModelMation")
    @ResponseBody
    public void insertApiModelMation(InputObject inputObject, OutputObject outputObject) throws Exception {
        apiModelService.insertApiModelMation(inputObject, outputObject);
    }

    /**
     * @param inputObject
     * @param outputObject
     * @return void    返回类型
     * @throws Exception 参数
     * @throws
     * @Title: deleteApiModelById
     * @Description: 删除api接口模块信息
     */
    @RequestMapping("/post/ApiModelController/deleteApiModelById")
    @ResponseBody
    public void deleteApiModelById(InputObject inputObject, OutputObject outputObject) throws Exception {
        apiModelService.deleteApiModelById(inputObject, outputObject);
    }

    /**
     * @param inputObject
     * @param outputObject
     * @return void    返回类型
     * @throws Exception 参数
     * @throws
     * @Title: selectApiModelById
     * @Description: 通过id查找对应的api接口模块信息
     */
    @RequestMapping("/post/ApiModelController/selectApiModelById")
    @ResponseBody
    public void selectApiModelById(InputObject inputObject, OutputObject outputObject) throws Exception {
        apiModelService.selectApiModelById(inputObject, outputObject);
    }

    /**
     * @param inputObject
     * @param outputObject
     * @return void    返回类型
     * @throws Exception 参数
     * @throws
     * @Title: editApiModelMationById
     * @Description: 通过id编辑对应的api接口模块信息
     */
    @RequestMapping("/post/ApiModelController/editApiModelMationById")
    @ResponseBody
    public void editApiModelMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        apiModelService.editApiModelMationById(inputObject, outputObject);
    }

}
