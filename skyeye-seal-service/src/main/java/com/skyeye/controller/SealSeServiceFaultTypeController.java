/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.service.SealSeServiceFaultTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SealSeServiceFaultTypeController {

    @Autowired
    private SealSeServiceFaultTypeService sealSeServiceFaultTypeService;

    /**
     *
     * @Title: insertSealSeServiceFaultType
     * @Description: 添加售后服务故障类型表信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealSeServiceFaultTypeController/insertSealSeServiceFaultType")
    @ResponseBody
    public void insertSealSeServiceFaultType(InputObject inputObject, OutputObject outputObject) throws Exception {
        sealSeServiceFaultTypeService.insertSealSeServiceFaultType(inputObject, outputObject);
    }

    /**
     *
     * @Title: querySealSeServiceFaultTypeList
     * @Description: 获取所有售后服务故障类型表状态为未被删除的记录
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealSeServiceFaultTypeController/querySealSeServiceFaultTypeList")
    @ResponseBody
    public void querySealSeServiceFaultTypeList(InputObject inputObject, OutputObject outputObject) throws Exception{
        sealSeServiceFaultTypeService.querySealSeServiceFaultTypeList(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryStateUpList
     * @Description: 获取售后服务故障类型表状态为上线的所有记录
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealSeServiceFaultTypeController/queryStateUpList")
    @ResponseBody
    public void queryStateUpList(InputObject inputObject, OutputObject outputObject) throws Exception{
        sealSeServiceFaultTypeService.queryStateUpList(inputObject, outputObject);
    }

    /**
     *
     * @Title: querySealSeServiceFaultTypeMationById
     * @Description: 通过售后服务故障类型表id查询id和name
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealSeServiceFaultTypeController/querySealSeServiceFaultTypeMationById")
    @ResponseBody
    public void querySealSeServiceFaultTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        sealSeServiceFaultTypeService.querySealSeServiceFaultTypeMationById(inputObject, outputObject);
    }

    /**
     *
     * @Title: ediSealSeServiceFaultTypeById
     * @Description: 编辑售后服务故障类型表名称
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealSeServiceFaultTypeController/editSealSeServiceFaultTypeById")
    @ResponseBody
    public void editSealSeServiceFaultTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
        sealSeServiceFaultTypeService.editSealSeServiceFaultTypeById(inputObject, outputObject);
    }

    /**
     *
     * @Title: editStateUpById
     * @Description: 编辑售后服务故障类型表状态为上线
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealSeServiceFaultTypeController/editStateUpById")
    @ResponseBody
    public void editStateUpById(InputObject inputObject, OutputObject outputObject) throws Exception {
        sealSeServiceFaultTypeService.editStateUpById(inputObject, outputObject);
    }

    /**
     *
     * @Title: editStateDownById
     * @Description: 编辑售后服务故障类型表状态为下线
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealSeServiceFaultTypeController/editStateDownById")
    @ResponseBody
    public void editStateDownById(InputObject inputObject, OutputObject outputObject) throws Exception {
        sealSeServiceFaultTypeService.editStateDownById(inputObject, outputObject);
    }

    /**
     *
     * @Title: deleteSealSeServiceFaultTypeById
     * @Description: 编辑售后服务故障类型表状态为删除
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealSeServiceFaultTypeController/deleteSealSeServiceFaultTypeById")
    @ResponseBody
    public void deleteSealSeServiceFaultTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
        sealSeServiceFaultTypeService.deleteSealSeServiceFaultTypeById(inputObject, outputObject);
    }

}
