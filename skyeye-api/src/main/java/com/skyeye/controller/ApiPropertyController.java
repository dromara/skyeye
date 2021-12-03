/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.service.ApiPropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ApiPropertyController {

    @Autowired
    private ApiPropertyService apiPropertyService;

    /**
     * @param inputObject
     * @param outputObject
     * @return void    返回类型
     * @throws Exception 参数
     * @throws
     * @Title: queryApiPropertyList
     * @Description: 获取api接口参数列表
     */
    @RequestMapping("/post/ApiPropertyController/queryApiPropertyList")
    @ResponseBody
    public void queryApiPropertyList(InputObject inputObject, OutputObject outputObject) throws Exception {
        apiPropertyService.queryApiPropertyMationList(inputObject, outputObject);
    }

    /**
     * @param inputObject
     * @param outputObject
     * @return void    返回类型
     * @throws Exception 参数
     * @throws
     * @Title: insertApiPropertyMation
     * @Description: 新增api接口参数信息
     */
    @RequestMapping("/post/ApiPropertyController/insertApiPropertyMation")
    @ResponseBody
    public void insertApiPropertyMation(InputObject inputObject, OutputObject outputObject) throws Exception {
        apiPropertyService.insertApiPropertyMation(inputObject, outputObject);
    }

    /**
     * @param inputObject
     * @param outputObject
     * @return void    返回类型
     * @throws Exception 参数
     * @throws
     * @Title: deleteApiPropertyById
     * @Description: 删除api接口参数信息
     */
    @RequestMapping("/post/ApiPropertyController/deleteApiPropertyById")
    @ResponseBody
    public void deleteApiPropertyById(InputObject inputObject, OutputObject outputObject) throws Exception {
        apiPropertyService.deleteApiPropertyMationById(inputObject, outputObject);
    }

    /**
     * @param inputObject
     * @param outputObject
     * @return void    返回类型
     * @throws Exception 参数
     * @throws
     * @Title: selectApiPropertyById
     * @Description: 通过id查找对应的api接口参数信息
     */
    @RequestMapping("/post/ApiPropertyController/selectApiPropertyById")
    @ResponseBody
    public void selectApiPropertyById(InputObject inputObject, OutputObject outputObject) throws Exception {
        apiPropertyService.selectApiPropertyMationById(inputObject, outputObject);
    }

    /**
     * @param inputObject
     * @param outputObject
     * @return void    返回类型
     * @throws Exception 参数
     * @throws
     * @Title: editApiPropertyMationById
     * @Description: 通过id编辑对应的api接口参数信息
     */
    @RequestMapping("/post/ApiPropertyController/editApiPropertyMationById")
    @ResponseBody
    public void editApiPropertyMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        apiPropertyService.editApiPropertyMationById(inputObject, outputObject);
    }

}
