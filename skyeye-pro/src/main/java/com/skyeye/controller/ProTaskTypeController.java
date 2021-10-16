/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.service.ProTaskTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ProTaskTypeController {

    @Autowired
    private ProTaskTypeService proTaskTypeService;

    /**
     *
     * @Title: insertProTaskType
     * @Description: 添加任务分类信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ProTaskTypeController/insertProTaskType")
    @ResponseBody
    public void insertProTaskType(InputObject inputObject, OutputObject outputObject) throws Exception {
        proTaskTypeService.insertProTaskType(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryProTaskTypeList
     * @Description: 获取所有任务分类状态为未被删除的记录
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ProTaskTypeController/queryProTaskTypeList")
    @ResponseBody
    public void queryProTaskTypeList(InputObject inputObject, OutputObject outputObject) throws Exception{
        proTaskTypeService.queryProTaskTypeList(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryStateUpList
     * @Description: 获取任务分类状态为上线的所有记录
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ProTaskTypeController/queryStateUpList")
    @ResponseBody
    public void queryStateUpList(InputObject inputObject, OutputObject outputObject) throws Exception{
        proTaskTypeService.queryStateUpList(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryProTaskTypeMationById
     * @Description: 通过任务分类id查询id和name
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ProTaskTypeController/queryProTaskTypeMationById")
    @ResponseBody
    public void queryProTaskTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        proTaskTypeService.queryProTaskTypeMationById(inputObject, outputObject);
    }

    /**
     *
     * @Title: ediProTaskTypeById
     * @Description: 编辑任务分类名称
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ProTaskTypeController/editProTaskTypeById")
    @ResponseBody
    public void editProTaskTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
        proTaskTypeService.editProTaskTypeById(inputObject, outputObject);
    }

    /**
     *
     * @Title: editStateUpById
     * @Description: 编辑任务分类状态为上线
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ProTaskTypeController/editStateUpById")
    @ResponseBody
    public void editStateUpById(InputObject inputObject, OutputObject outputObject) throws Exception {
        proTaskTypeService.editStateUpById(inputObject, outputObject);
    }

    /**
     *
     * @Title: editStateDownById
     * @Description: 编辑任务分类状态为下线
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ProTaskTypeController/editStateDownById")
    @ResponseBody
    public void editStateDownById(InputObject inputObject, OutputObject outputObject) throws Exception {
        proTaskTypeService.editStateDownById(inputObject, outputObject);
    }

    /**
     *
     * @Title: deleteProTaskTypeById
     * @Description: 编辑任务分类状态为删除
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ProTaskTypeController/deleteProTaskTypeById")
    @ResponseBody
    public void deleteProTaskTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
        proTaskTypeService.deleteProTaskTypeById(inputObject, outputObject);
    }

}
