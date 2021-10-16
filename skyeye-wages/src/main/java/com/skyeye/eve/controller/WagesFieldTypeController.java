/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.WagesFieldTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class WagesFieldTypeController {

    @Autowired
    private WagesFieldTypeService wagesFieldTypeService;

    /**
     *
     * @Title: queryWagesFieldTypeList
     * @Description: 获取薪资字段列表
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/WagesFieldTypeController/queryWagesFieldTypeList")
    @ResponseBody
    public void queryWagesFieldTypeList(InputObject inputObject, OutputObject outputObject) throws Exception{
        wagesFieldTypeService.queryWagesFieldTypeList(inputObject, outputObject);
    }

    /**
     *
     * @Title: insertWagesFieldTypeMation
     * @Description: 新增薪资字段信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/WagesFieldTypeController/insertWagesFieldTypeMation")
    @ResponseBody
    public void insertWagesFieldTypeMation(InputObject inputObject, OutputObject outputObject) throws Exception{
        wagesFieldTypeService.insertWagesFieldTypeMation(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryWagesFieldTypeMationToEditById
     * @Description: 编辑薪资字段信息时进行回显
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/WagesFieldTypeController/queryWagesFieldTypeMationToEditById")
    @ResponseBody
    public void queryWagesFieldTypeMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
        wagesFieldTypeService.queryWagesFieldTypeMationToEditById(inputObject, outputObject);
    }

    /**
     *
     * @Title: editWagesFieldTypeMationById
     * @Description: 编辑薪资字段信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/WagesFieldTypeController/editWagesFieldTypeMationById")
    @ResponseBody
    public void editWagesFieldTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
        wagesFieldTypeService.editWagesFieldTypeMationById(inputObject, outputObject);
    }

    /**
     *
     * @Title: deleteWagesFieldTypeMationById
     * @Description: 删除薪资字段信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/WagesFieldTypeController/deleteWagesFieldTypeMationById")
    @ResponseBody
    public void deleteWagesFieldTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
        wagesFieldTypeService.deleteWagesFieldTypeMationById(inputObject, outputObject);
    }

    /**
     *
     * @Title: enableWagesFieldTypeMationById
     * @Description: 启用薪资字段信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/WagesFieldTypeController/enableWagesFieldTypeMationById")
    @ResponseBody
    public void enableWagesFieldTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
        wagesFieldTypeService.enableWagesFieldTypeMationById(inputObject, outputObject);
    }

    /**
     *
     * @Title: disableWagesFieldTypeMationById
     * @Description: 禁用薪资字段信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/WagesFieldTypeController/disableWagesFieldTypeMationById")
    @ResponseBody
    public void disableWagesFieldTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
        wagesFieldTypeService.disableWagesFieldTypeMationById(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryEnableWagesFieldTypeList
     * @Description: 获取已经启用的薪资字段列表
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/WagesFieldTypeController/queryEnableWagesFieldTypeList")
    @ResponseBody
    public void queryEnableWagesFieldTypeList(InputObject inputObject, OutputObject outputObject) throws Exception{
        wagesFieldTypeService.queryEnableWagesFieldTypeList(inputObject, outputObject);
    }

    /**
     *
     * @Title: querySysWagesFieldTypeList
     * @Description: 获取系统薪资字段列表
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/WagesFieldTypeController/querySysWagesFieldTypeList")
    @ResponseBody
    public void querySysWagesFieldTypeList(InputObject inputObject, OutputObject outputObject) throws Exception{
        wagesFieldTypeService.querySysWagesFieldTypeList(inputObject, outputObject);
    }

}
