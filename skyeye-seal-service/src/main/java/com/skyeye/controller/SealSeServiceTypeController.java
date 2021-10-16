/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.service.SealSeServiceTypeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SealSeServiceTypeController {

    @Autowired
    private SealSeServiceTypeService sealSeServiceTypeService;

    /**
     *
     * @Title: insertSealSeServiceType
     * @Description: 添加售后服务类型表信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealSeServiceTypeController/insertSealSeServiceType")
    @ResponseBody
    public void insertSealSeServiceType(InputObject inputObject, OutputObject outputObject) throws Exception {
        sealSeServiceTypeService.insertSealSeServiceType(inputObject, outputObject);
    }

    /**
     *
     * @Title: querySealSeServiceTypeList
     * @Description: 获取所有售后服务类型表状态为未被删除的记录
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealSeServiceTypeController/querySealSeServiceTypeList")
    @ResponseBody
    public void querySealSeServiceTypeList(InputObject inputObject, OutputObject outputObject) throws Exception{
        sealSeServiceTypeService.querySealSeServiceTypeList(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryStateUpList
     * @Description: 获取售后服务类型表状态为上线的所有记录
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealSeServiceTypeController/queryStateUpList")
    @ResponseBody
    public void queryStateUpList(InputObject inputObject, OutputObject outputObject) throws Exception{
        sealSeServiceTypeService.queryStateUpList(inputObject, outputObject);
    }

    /**
     *
     * @Title: querySealSeServiceTypeMationById
     * @Description: 通过售后服务类型表id查询id和name
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealSeServiceTypeController/querySealSeServiceTypeMationById")
    @ResponseBody
    public void querySealSeServiceTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        sealSeServiceTypeService.querySealSeServiceTypeMationById(inputObject, outputObject);
    }

    /**
     *
     * @Title: ediSealSeServiceTypeById
     * @Description: 编辑售后服务类型表名称
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealSeServiceTypeController/editSealSeServiceTypeById")
    @ResponseBody
    public void editSealSeServiceTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
        sealSeServiceTypeService.editSealSeServiceTypeById(inputObject, outputObject);
    }

    /**
     *
     * @Title: editStateUpById
     * @Description: 编辑售后服务类型表状态为上线
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealSeServiceTypeController/editStateUpById")
    @ResponseBody
    public void editStateUpById(InputObject inputObject, OutputObject outputObject) throws Exception {
        sealSeServiceTypeService.editStateUpById(inputObject, outputObject);
    }

    /**
     *
     * @Title: editStateDownById
     * @Description: 编辑售后服务类型表状态为下线
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealSeServiceTypeController/editStateDownById")
    @ResponseBody
    public void editStateDownById(InputObject inputObject, OutputObject outputObject) throws Exception {
        sealSeServiceTypeService.editStateDownById(inputObject, outputObject);
    }

    /**
     *
     * @Title: deleteSealSeServiceTypeById
     * @Description: 编辑售后服务类型表状态为删除
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealSeServiceTypeController/deleteSealSeServiceTypeById")
    @ResponseBody
    public void deleteSealSeServiceTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
        sealSeServiceTypeService.deleteSealSeServiceTypeById(inputObject, outputObject);
    }

}
