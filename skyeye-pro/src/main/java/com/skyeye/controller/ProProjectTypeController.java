/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.service.ProProjectTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ProProjectTypeController {

    @Autowired
    private ProProjectTypeService proProjectTypeService;

    /**
     *
     * @Title: insertProProjectType
     * @Description: 添加项目分类信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ProProjectTypeController/insertProProjectType")
    @ResponseBody
    public void insertProProjectType(InputObject inputObject, OutputObject outputObject) throws Exception {
        proProjectTypeService.insertProProjectType(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryProProjectTypeList
     * @Description: 获取所有项目分类状态为未被删除的记录
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ProProjectTypeController/queryProProjectTypeList")
    @ResponseBody
    public void queryProProjectTypeList(InputObject inputObject, OutputObject outputObject) throws Exception{
        proProjectTypeService.queryProProjectTypeList(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryStateUpList
     * @Description: 获取项目分类状态为上线的所有记录
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ProProjectTypeController/queryStateUpList")
    @ResponseBody
    public void queryStateUpList(InputObject inputObject, OutputObject outputObject) throws Exception{
        proProjectTypeService.queryStateUpList(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryProProjectTypeMationById
     * @Description: 通过项目分类id查询id和name
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ProProjectTypeController/queryProProjectTypeMationById")
    @ResponseBody
    public void queryProProjectTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        proProjectTypeService.queryProProjectTypeMationById(inputObject, outputObject);
    }

    /**
     *
     * @Title: ediProProjectTypeById
     * @Description: 编辑项目分类名称
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ProProjectTypeController/editProProjectTypeById")
    @ResponseBody
    public void editProProjectTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
        proProjectTypeService.editProProjectTypeById(inputObject, outputObject);
    }

    /**
     *
     * @Title: editStateUpById
     * @Description: 编辑项目分类状态为上线
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ProProjectTypeController/editStateUpById")
    @ResponseBody
    public void editStateUpById(InputObject inputObject, OutputObject outputObject) throws Exception {
        proProjectTypeService.editStateUpById(inputObject, outputObject);
    }

    /**
     *
     * @Title: editStateDownById
     * @Description: 编辑项目分类状态为下线
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ProProjectTypeController/editStateDownById")
    @ResponseBody
    public void editStateDownById(InputObject inputObject, OutputObject outputObject) throws Exception {
        proProjectTypeService.editStateDownById(inputObject, outputObject);
    }

    /**
     *
     * @Title: deleteProProjectTypeById
     * @Description: 编辑项目分类状态为删除
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ProProjectTypeController/deleteProProjectTypeById")
    @ResponseBody
    public void deleteProProjectTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
        proProjectTypeService.deleteProProjectTypeById(inputObject, outputObject);
    }

}
