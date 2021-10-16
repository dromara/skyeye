/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.service.SealSeServiceUrgencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SealSeServiceUrgencyController {

    @Autowired
    private SealSeServiceUrgencyService sealSeServiceUrgencyService;

    /**
     *
     * @Title: insertSealSeServiceUrgency
     * @Description: 添加售后服务工单紧急程度表信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealSeServiceUrgencyController/insertSealSeServiceUrgency")
    @ResponseBody
    public void insertSealSeServiceUrgency(InputObject inputObject, OutputObject outputObject) throws Exception {
        sealSeServiceUrgencyService.insertSealSeServiceUrgency(inputObject, outputObject);
    }

    /**
     *
     * @Title: querySealSeServiceUrgencyList
     * @Description: 获取所有售后服务工单紧急程度表状态为未被删除的记录
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealSeServiceUrgencyController/querySealSeServiceUrgencyList")
    @ResponseBody
    public void querySealSeServiceUrgencyList(InputObject inputObject, OutputObject outputObject) throws Exception{
        sealSeServiceUrgencyService.querySealSeServiceUrgencyList(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryStateUpList
     * @Description: 获取售后服务工单紧急程度表状态为上线的所有记录
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealSeServiceUrgencyController/queryStateUpList")
    @ResponseBody
    public void queryStateUpList(InputObject inputObject, OutputObject outputObject) throws Exception{
        sealSeServiceUrgencyService.queryStateUpList(inputObject, outputObject);
    }

    /**
     *
     * @Title: querySealSeServiceUrgencyMationById
     * @Description: 通过售后服务工单紧急程度表id查询id和name
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealSeServiceUrgencyController/querySealSeServiceUrgencyMationById")
    @ResponseBody
    public void querySealSeServiceUrgencyMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        sealSeServiceUrgencyService.querySealSeServiceUrgencyMationById(inputObject, outputObject);
    }

    /**
     *
     * @Title: ediSealSeServiceUrgencyById
     * @Description: 编辑售后服务工单紧急程度表名称
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealSeServiceUrgencyController/editSealSeServiceUrgencyById")
    @ResponseBody
    public void editSealSeServiceUrgencyById(InputObject inputObject, OutputObject outputObject) throws Exception{
        sealSeServiceUrgencyService.editSealSeServiceUrgencyById(inputObject, outputObject);
    }

    /**
     *
     * @Title: editStateUpById
     * @Description: 编辑售后服务工单紧急程度表状态为上线
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealSeServiceUrgencyController/editStateUpById")
    @ResponseBody
    public void editStateUpById(InputObject inputObject, OutputObject outputObject) throws Exception {
        sealSeServiceUrgencyService.editStateUpById(inputObject, outputObject);
    }

    /**
     *
     * @Title: editStateDownById
     * @Description: 编辑售后服务工单紧急程度表状态为下线
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealSeServiceUrgencyController/editStateDownById")
    @ResponseBody
    public void editStateDownById(InputObject inputObject, OutputObject outputObject) throws Exception {
        sealSeServiceUrgencyService.editStateDownById(inputObject, outputObject);
    }

    /**
     *
     * @Title: deleteSealSeServiceUrgencyById
     * @Description: 编辑售后服务工单紧急程度表状态为删除
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealSeServiceUrgencyController/deleteSealSeServiceUrgencyById")
    @ResponseBody
    public void deleteSealSeServiceUrgencyById(InputObject inputObject, OutputObject outputObject) throws Exception{
        sealSeServiceUrgencyService.deleteSealSeServiceUrgencyById(inputObject, outputObject);
    }

}
