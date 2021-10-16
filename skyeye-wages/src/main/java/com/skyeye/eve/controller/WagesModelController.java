/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.WagesModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class WagesModelController {

    @Autowired
    private WagesModelService wagesModelService;

    /**
     *
     * @Title: queryWagesModelList
     * @Description: 获取薪资模板列表
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/WagesModelController/queryWagesModelList")
    @ResponseBody
    public void queryWagesModelList(InputObject inputObject, OutputObject outputObject) throws Exception{
        wagesModelService.queryWagesModelList(inputObject, outputObject);
    }

    /**
     *
     * @Title: insertWagesModelMation
     * @Description: 新增薪资模板信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/WagesModelController/insertWagesModelMation")
    @ResponseBody
    public void insertWagesModelMation(InputObject inputObject, OutputObject outputObject) throws Exception{
        wagesModelService.insertWagesModelMation(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryWagesModelMationToEditById
     * @Description: 编辑薪资模板信息时进行回显
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/WagesModelController/queryWagesModelMationToEditById")
    @ResponseBody
    public void queryWagesModelMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
        wagesModelService.queryWagesModelMationToEditById(inputObject, outputObject);
    }

    /**
     *
     * @Title: editWagesModelMationById
     * @Description: 编辑薪资模板信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/WagesModelController/editWagesModelMationById")
    @ResponseBody
    public void editWagesModelMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
        wagesModelService.editWagesModelMationById(inputObject, outputObject);
    }

    /**
     *
     * @Title: deleteWagesModelMationById
     * @Description: 删除薪资模板信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/WagesModelController/deleteWagesModelMationById")
    @ResponseBody
    public void deleteWagesModelMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
        wagesModelService.deleteWagesModelMationById(inputObject, outputObject);
    }

    /**
     *
     * @Title: enableWagesModelMationById
     * @Description: 启用薪资模板信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/WagesModelController/enableWagesModelMationById")
    @ResponseBody
    public void enableWagesModelMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
        wagesModelService.enableWagesModelMationById(inputObject, outputObject);
    }

    /**
     *
     * @Title: disableWagesModelMationById
     * @Description: 禁用薪资模板信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/WagesModelController/disableWagesModelMationById")
    @ResponseBody
    public void disableWagesModelMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
        wagesModelService.disableWagesModelMationById(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryWagesModelDetailMationById
     * @Description: 薪资模板详细信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/WagesModelController/queryWagesModelDetailMationById")
    @ResponseBody
    public void queryWagesModelDetailMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
        wagesModelService.queryWagesModelDetailMationById(inputObject, outputObject);
    }

}
