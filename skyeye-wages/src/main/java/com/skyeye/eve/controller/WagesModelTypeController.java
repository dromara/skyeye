/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.WagesModelTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class WagesModelTypeController {

    @Autowired
    private WagesModelTypeService wagesModelTypeService;

    /**
     *
     * @Title: queryWagesModelTypeList
     * @Description: 获取薪资模板类型列表
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/WagesModelTypeController/queryWagesModelTypeList")
    @ResponseBody
    public void queryWagesModelTypeList(InputObject inputObject, OutputObject outputObject) throws Exception{
        wagesModelTypeService.queryWagesModelTypeList(inputObject, outputObject);
    }

    /**
     *
     * @Title: insertWagesModelTypeMation
     * @Description: 新增薪资模板类型信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/WagesModelTypeController/insertWagesModelTypeMation")
    @ResponseBody
    public void insertWagesModelTypeMation(InputObject inputObject, OutputObject outputObject) throws Exception{
        wagesModelTypeService.insertWagesModelTypeMation(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryWagesModelTypeMationToEditById
     * @Description: 编辑薪资模板类型信息时进行回显
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/WagesModelTypeController/queryWagesModelTypeMationToEditById")
    @ResponseBody
    public void queryWagesModelTypeMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
        wagesModelTypeService.queryWagesModelTypeMationToEditById(inputObject, outputObject);
    }

    /**
     *
     * @Title: editWagesModelTypeMationById
     * @Description: 编辑薪资模板类型信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/WagesModelTypeController/editWagesModelTypeMationById")
    @ResponseBody
    public void editWagesModelTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
        wagesModelTypeService.editWagesModelTypeMationById(inputObject, outputObject);
    }

    /**
     *
     * @Title: deleteWagesModelTypeMationById
     * @Description: 删除薪资模板类型信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/WagesModelTypeController/deleteWagesModelTypeMationById")
    @ResponseBody
    public void deleteWagesModelTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
        wagesModelTypeService.deleteWagesModelTypeMationById(inputObject, outputObject);
    }

    /**
     *
     * @Title: enableWagesModelTypeMationById
     * @Description: 启用薪资模板类型信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/WagesModelTypeController/enableWagesModelTypeMationById")
    @ResponseBody
    public void enableWagesModelTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
        wagesModelTypeService.enableWagesModelTypeMationById(inputObject, outputObject);
    }

    /**
     *
     * @Title: disableWagesModelTypeMationById
     * @Description: 禁用薪资模板类型信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/WagesModelTypeController/disableWagesModelTypeMationById")
    @ResponseBody
    public void disableWagesModelTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
        wagesModelTypeService.disableWagesModelTypeMationById(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryEnableWagesModelTypeList
     * @Description: 获取已经启用的薪资模板类型列表
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/WagesModelTypeController/queryEnableWagesModelTypeList")
    @ResponseBody
    public void queryEnableWagesModelTypeList(InputObject inputObject, OutputObject outputObject) throws Exception{
        wagesModelTypeService.queryEnableWagesModelTypeList(inputObject, outputObject);
    }

}
