/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.service.SealSeServiceFeedbackTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SealSeServiceFeedbackTypeController {

    @Autowired
    private SealSeServiceFeedbackTypeService sealSeServiceFeedbackTypeService;

    /**
     *
     * @Title: insertCrmServiceFeedbackType
     * @Description: 添加售后服务反馈类型表信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealSeServiceFeedbackTypeController/insertCrmServiceFeedbackType")
    @ResponseBody
    public void insertCrmServiceFeedbackType(InputObject inputObject, OutputObject outputObject) throws Exception {
        sealSeServiceFeedbackTypeService.insertCrmServiceFeedbackType(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryCrmServiceFeedbackTypeList
     * @Description: 获取所有售后服务反馈类型表状态为未被删除的记录
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealSeServiceFeedbackTypeController/queryCrmServiceFeedbackTypeList")
    @ResponseBody
    public void queryCrmServiceFeedbackTypeList(InputObject inputObject, OutputObject outputObject) throws Exception{
        sealSeServiceFeedbackTypeService.queryCrmServiceFeedbackTypeList(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryStateUpList
     * @Description: 获取售后服务反馈类型表状态为上线的所有记录
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealSeServiceFeedbackTypeController/queryStateUpList")
    @ResponseBody
    public void queryStateUpList(InputObject inputObject, OutputObject outputObject) throws Exception{
        sealSeServiceFeedbackTypeService.queryStateUpList(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryCrmServiceFeedbackTypeMationById
     * @Description: 通过售后服务反馈类型表id查询id和name
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealSeServiceFeedbackTypeController/queryCrmServiceFeedbackTypeMationById")
    @ResponseBody
    public void queryCrmServiceFeedbackTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        sealSeServiceFeedbackTypeService.queryCrmServiceFeedbackTypeMationById(inputObject, outputObject);
    }

    /**
     *
     * @Title: ediCrmServiceFeedbackTypeById
     * @Description: 编辑售后服务反馈类型表名称
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealSeServiceFeedbackTypeController/editCrmServiceFeedbackTypeById")
    @ResponseBody
    public void editCrmServiceFeedbackTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
        sealSeServiceFeedbackTypeService.editCrmServiceFeedbackTypeById(inputObject, outputObject);
    }

    /**
     *
     * @Title: editStateUpById
     * @Description: 编辑售后服务反馈类型表状态为上线
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealSeServiceFeedbackTypeController/editStateUpById")
    @ResponseBody
    public void editStateUpById(InputObject inputObject, OutputObject outputObject) throws Exception {
        sealSeServiceFeedbackTypeService.editStateUpById(inputObject, outputObject);
    }

    /**
     *
     * @Title: editStateDownById
     * @Description: 编辑售后服务反馈类型表状态为下线
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealSeServiceFeedbackTypeController/editStateDownById")
    @ResponseBody
    public void editStateDownById(InputObject inputObject, OutputObject outputObject) throws Exception {
        sealSeServiceFeedbackTypeService.editStateDownById(inputObject, outputObject);
    }

    /**
     *
     * @Title: deleteCrmServiceFeedbackTypeById
     * @Description: 编辑售后服务反馈类型表状态为删除
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealSeServiceFeedbackTypeController/deleteCrmServiceFeedbackTypeById")
    @ResponseBody
    public void deleteCrmServiceFeedbackTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
        sealSeServiceFeedbackTypeService.deleteCrmServiceFeedbackTypeById(inputObject, outputObject);
    }

}
