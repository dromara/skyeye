/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.service.ProProjectDiscussTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ProProjectDiscussTypeController {

    @Autowired
    private ProProjectDiscussTypeService proProjectDiscussTypeService;

    /**
     *
     * @Title: insertProProjectDiscussType
     * @Description: 添加项目管理讨论板分类信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ProProjectDiscussTypeController/insertProProjectDiscussType")
    @ResponseBody
    public void insertProProjectDiscussType(InputObject inputObject, OutputObject outputObject) throws Exception {
        proProjectDiscussTypeService.insertProProjectDiscussType(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryProProjectDiscussTypeList
     * @Description: 获取所有项目管理讨论板分类状态为未被删除的记录
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ProProjectDiscussTypeController/queryProProjectDiscussTypeList")
    @ResponseBody
    public void queryProProjectDiscussTypeList(InputObject inputObject, OutputObject outputObject) throws Exception{
        proProjectDiscussTypeService.queryProProjectDiscussTypeList(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryStateUpList
     * @Description: 获取项目管理讨论板分类状态为上线的所有记录
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ProProjectDiscussTypeController/queryStateUpList")
    @ResponseBody
    public void queryStateUpList(InputObject inputObject, OutputObject outputObject) throws Exception{
        proProjectDiscussTypeService.queryStateUpList(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryProProjectDiscussTypeMationById
     * @Description: 通过项目管理讨论板分类id查询id和name
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ProProjectDiscussTypeController/queryProProjectDiscussTypeMationById")
    @ResponseBody
    public void queryProProjectDiscussTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        proProjectDiscussTypeService.queryProProjectDiscussTypeMationById(inputObject, outputObject);
    }

    /**
     *
     * @Title: ediProProjectDiscussTypeById
     * @Description: 编辑项目管理讨论板分类名称
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ProProjectDiscussTypeController/editProProjectDiscussTypeById")
    @ResponseBody
    public void editProProjectDiscussTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
        proProjectDiscussTypeService.editProProjectDiscussTypeById(inputObject, outputObject);
    }

    /**
     *
     * @Title: editStateUpById
     * @Description: 编辑项目管理讨论板分类状态为上线
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ProProjectDiscussTypeController/editStateUpById")
    @ResponseBody
    public void editStateUpById(InputObject inputObject, OutputObject outputObject) throws Exception {
        proProjectDiscussTypeService.editStateUpById(inputObject, outputObject);
    }

    /**
     *
     * @Title: editStateDownById
     * @Description: 编辑项目管理讨论板分类状态为下线
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ProProjectDiscussTypeController/editStateDownById")
    @ResponseBody
    public void editStateDownById(InputObject inputObject, OutputObject outputObject) throws Exception {
        proProjectDiscussTypeService.editStateDownById(inputObject, outputObject);
    }

    /**
     *
     * @Title: deleteProProjectDiscussTypeById
     * @Description: 编辑项目管理讨论板分类状态为删除
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ProProjectDiscussTypeController/deleteProProjectDiscussTypeById")
    @ResponseBody
    public void deleteProProjectDiscussTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
        proProjectDiscussTypeService.deleteProProjectDiscussTypeById(inputObject, outputObject);
    }

}
