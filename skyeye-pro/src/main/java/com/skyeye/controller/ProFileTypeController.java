/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.service.ProFileTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ProFileTypeController {

    @Autowired
    private ProFileTypeService proFileTypeService;

    /**
     *
     * @Title: insertProFileType
     * @Description: 添加文档分类信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ProFileTypeController/insertProFileType")
    @ResponseBody
    public void insertProFileType(InputObject inputObject, OutputObject outputObject) throws Exception {
        proFileTypeService.insertProFileType(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryProFileTypeList
     * @Description: 获取所有文档分类状态为未被删除的记录
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ProFileTypeController/queryProFileTypeList")
    @ResponseBody
    public void queryProFileTypeList(InputObject inputObject, OutputObject outputObject) throws Exception{
        proFileTypeService.queryProFileTypeList(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryStateUpList
     * @Description: 获取文档分类状态为上线的所有记录
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ProFileTypeController/queryStateUpList")
    @ResponseBody
    public void queryStateUpList(InputObject inputObject, OutputObject outputObject) throws Exception{
        proFileTypeService.queryStateUpList(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryProFileTypeMationById
     * @Description: 通过文档分类id查询id和name
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ProFileTypeController/queryProFileTypeMationById")
    @ResponseBody
    public void queryProFileTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        proFileTypeService.queryProFileTypeMationById(inputObject, outputObject);
    }

    /**
     *
     * @Title: ediProFileTypeById
     * @Description: 编辑文档分类名称
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ProFileTypeController/editProFileTypeById")
    @ResponseBody
    public void editProFileTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
        proFileTypeService.editProFileTypeById(inputObject, outputObject);
    }

    /**
     *
     * @Title: editStateUpById
     * @Description: 编辑文档分类状态为上线
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ProFileTypeController/editStateUpById")
    @ResponseBody
    public void editStateUpById(InputObject inputObject, OutputObject outputObject) throws Exception {
        proFileTypeService.editStateUpById(inputObject, outputObject);
    }

    /**
     *
     * @Title: editStateDownById
     * @Description: 编辑文档分类状态为下线
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ProFileTypeController/editStateDownById")
    @ResponseBody
    public void editStateDownById(InputObject inputObject, OutputObject outputObject) throws Exception {
        proFileTypeService.editStateDownById(inputObject, outputObject);
    }

    /**
     *
     * @Title: deleteProFileTypeById
     * @Description: 编辑文档分类状态为删除
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ProFileTypeController/deleteProFileTypeById")
    @ResponseBody
    public void deleteProFileTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
        proFileTypeService.deleteProFileTypeById(inputObject, outputObject);
    }

}
