/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.service.SealSeServiceModeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SealSeServiceModeController {

    @Autowired
    private SealSeServiceModeService sealSeServiceModeService;

    /**
     *
     * @Title: insertSealSeServiceMode
     * @Description: 添加售后服务方式表信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealSeServiceModeController/insertSealSeServiceMode")
    @ResponseBody
    public void insertSealSeServiceMode(InputObject inputObject, OutputObject outputObject) throws Exception {
        sealSeServiceModeService.insertSealSeServiceMode(inputObject, outputObject);
    }

    /**
     *
     * @Title: querySealSeServiceModeList
     * @Description: 获取所有售后服务方式表状态为未被删除的记录
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealSeServiceModeController/querySealSeServiceModeList")
    @ResponseBody
    public void querySealSeServiceModeList(InputObject inputObject, OutputObject outputObject) throws Exception{
        sealSeServiceModeService.querySealSeServiceModeList(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryStateUpList
     * @Description: 获取售后服务方式表状态为上线的所有记录
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealSeServiceModeController/queryStateUpList")
    @ResponseBody
    public void queryStateUpList(InputObject inputObject, OutputObject outputObject) throws Exception{
        sealSeServiceModeService.queryStateUpList(inputObject, outputObject);
    }

    /**
     *
     * @Title: querySealSeServiceModeMationById
     * @Description: 通过售后服务方式表id查询id和name
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealSeServiceModeController/querySealSeServiceModeMationById")
    @ResponseBody
    public void querySealSeServiceModeMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        sealSeServiceModeService.querySealSeServiceModeMationById(inputObject, outputObject);
    }

    /**
     *
     * @Title: ediSealSeServiceModeById
     * @Description: 编辑售后服务方式表名称
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealSeServiceModeController/editSealSeServiceModeById")
    @ResponseBody
    public void editSealSeServiceModeById(InputObject inputObject, OutputObject outputObject) throws Exception{
        sealSeServiceModeService.editSealSeServiceModeById(inputObject, outputObject);
    }

    /**
     *
     * @Title: editStateUpById
     * @Description: 编辑售后服务方式表状态为上线
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealSeServiceModeController/editStateUpById")
    @ResponseBody
    public void editStateUpById(InputObject inputObject, OutputObject outputObject) throws Exception {
        sealSeServiceModeService.editStateUpById(inputObject, outputObject);
    }

    /**
     *
     * @Title: editStateDownById
     * @Description: 编辑售后服务方式表状态为下线
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealSeServiceModeController/editStateDownById")
    @ResponseBody
    public void editStateDownById(InputObject inputObject, OutputObject outputObject) throws Exception {
        sealSeServiceModeService.editStateDownById(inputObject, outputObject);
    }

    /**
     *
     * @Title: deleteSealSeServiceModeById
     * @Description: 编辑售后服务方式表状态为删除
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealSeServiceModeController/deleteSealSeServiceModeById")
    @ResponseBody
    public void deleteSealSeServiceModeById(InputObject inputObject, OutputObject outputObject) throws Exception{
        sealSeServiceModeService.deleteSealSeServiceModeById(inputObject, outputObject);
    }

}
