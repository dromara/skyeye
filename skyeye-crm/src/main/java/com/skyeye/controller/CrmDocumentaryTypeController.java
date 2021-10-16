/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.service.CrmDocumentaryTypeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CrmDocumentaryTypeController {

    @Autowired
    private CrmDocumentaryTypeService crmDocumentaryTypeService;

    /**
     *
     * @Title: insertCrmDocumentaryType
     * @Description: 添加跟单分类表信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CrmDocumentaryTypeController/insertCrmDocumentaryType")
    @ResponseBody
    public void insertCrmDocumentaryType(InputObject inputObject, OutputObject outputObject) throws Exception {
        crmDocumentaryTypeService.insertCrmDocumentaryType(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryCrmDocumentaryTypeList
     * @Description: 获取所有跟单分类表状态为未被删除的记录
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CrmDocumentaryTypeController/queryCrmDocumentaryTypeList")
    @ResponseBody
    public void queryCrmDocumentaryTypeList(InputObject inputObject, OutputObject outputObject) throws Exception{
        crmDocumentaryTypeService.queryCrmDocumentaryTypeList(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryStateUpList
     * @Description: 获取跟单分类表状态为上线的所有记录
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CrmDocumentaryTypeController/queryStateUpList")
    @ResponseBody
    public void queryStateUpList(InputObject inputObject, OutputObject outputObject) throws Exception{
        crmDocumentaryTypeService.queryStateUpList(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryCrmDocumentaryTypeMationById
     * @Description: 通过跟单分类表id查询id和name
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CrmDocumentaryTypeController/queryCrmDocumentaryTypeMationById")
    @ResponseBody
    public void queryCrmDocumentaryTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        crmDocumentaryTypeService.queryCrmDocumentaryTypeMationById(inputObject, outputObject);
    }

    /**
     *
     * @Title: ediCrmDocumentaryTypeById
     * @Description: 编辑跟单分类表名称
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CrmDocumentaryTypeController/editCrmDocumentaryTypeById")
    @ResponseBody
    public void editCrmDocumentaryTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
        crmDocumentaryTypeService.editCrmDocumentaryTypeById(inputObject, outputObject);
    }

    /**
     *
     * @Title: editStateUpById
     * @Description: 编辑跟单分类表状态为上线
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CrmDocumentaryTypeController/editStateUpById")
    @ResponseBody
    public void editStateUpById(InputObject inputObject, OutputObject outputObject) throws Exception {
        crmDocumentaryTypeService.editStateUpById(inputObject, outputObject);
    }

    /**
     *
     * @Title: editStateDownById
     * @Description: 编辑跟单分类表状态为下线
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CrmDocumentaryTypeController/editStateDownById")
    @ResponseBody
    public void editStateDownById(InputObject inputObject, OutputObject outputObject) throws Exception {
        crmDocumentaryTypeService.editStateDownById(inputObject, outputObject);
    }

    /**
     *
     * @Title: deleteCrmDocumentaryTypeById
     * @Description: 编辑跟单分类表状态为删除
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CrmDocumentaryTypeController/deleteCrmDocumentaryTypeById")
    @ResponseBody
    public void deleteCrmDocumentaryTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
        crmDocumentaryTypeService.deleteCrmDocumentaryTypeById(inputObject, outputObject);
    }

}
