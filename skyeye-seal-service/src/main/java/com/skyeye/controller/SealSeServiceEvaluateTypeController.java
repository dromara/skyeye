/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.service.SealSeServiceEvaluateTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SealSeServiceEvaluateTypeController {

    @Autowired
    private SealSeServiceEvaluateTypeService sealSeServiceEvaluateTypeService;

    /**
     *
     * @Title: insertSealSeServiceEvaluateType
     * @Description: 添加售后服务评价类型表信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
	@RequestMapping("/post/SealSeServiceEvaluateTypeController/insertSealSeServiceEvaluateType")
    @ResponseBody
    public void insertSealSeServiceEvaluateType(InputObject inputObject, OutputObject outputObject) throws Exception {
        sealSeServiceEvaluateTypeService.insertSealSeServiceEvaluateType(inputObject, outputObject);
    }

    /**
     *
     * @Title: querySealSeServiceEvaluateTypeList
     * @Description: 获取所有售后服务评价类型表状态为未被删除的记录
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealSeServiceEvaluateTypeController/querySealSeServiceEvaluateTypeList")
    @ResponseBody
    public void querySealSeServiceEvaluateTypeList(InputObject inputObject, OutputObject outputObject) throws Exception{
        sealSeServiceEvaluateTypeService.querySealSeServiceEvaluateTypeList(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryStateUpList
     * @Description: 获取售后服务评价类型表状态为上线的所有记录
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealSeServiceEvaluateTypeController/queryStateUpList")
    @ResponseBody
    public void queryStateUpList(InputObject inputObject, OutputObject outputObject) throws Exception{
        sealSeServiceEvaluateTypeService.queryStateUpList(inputObject, outputObject);
    }

    /**
     *
     * @Title: querySealSeServiceEvaluateTypeMationById
     * @Description: 通过售后服务评价类型表id查询id和name
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealSeServiceEvaluateTypeController/querySealSeServiceEvaluateTypeMationById")
    @ResponseBody
    public void querySealSeServiceEvaluateTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        sealSeServiceEvaluateTypeService.querySealSeServiceEvaluateTypeMationById(inputObject, outputObject);
    }

    /**
     *
     * @Title: ediSealSeServiceEvaluateTypeById
     * @Description: 编辑售后服务评价类型表名称
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealSeServiceEvaluateTypeController/editSealSeServiceEvaluateTypeById")
    @ResponseBody
    public void editSealSeServiceEvaluateTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
        sealSeServiceEvaluateTypeService.editSealSeServiceEvaluateTypeById(inputObject, outputObject);
    }

    /**
     *
     * @Title: editStateUpById
     * @Description: 编辑售后服务评价类型表状态为上线
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealSeServiceEvaluateTypeController/editStateUpById")
    @ResponseBody
    public void editStateUpById(InputObject inputObject, OutputObject outputObject) throws Exception {
        sealSeServiceEvaluateTypeService.editStateUpById(inputObject, outputObject);
    }

    /**
     *
     * @Title: editStateDownById
     * @Description: 编辑售后服务评价类型表状态为下线
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealSeServiceEvaluateTypeController/editStateDownById")
    @ResponseBody
    public void editStateDownById(InputObject inputObject, OutputObject outputObject) throws Exception {
        sealSeServiceEvaluateTypeService.editStateDownById(inputObject, outputObject);
    }

    /**
     *
     * @Title: deleteSealSeServiceEvaluateTypeById
     * @Description: 编辑售后服务评价类型表状态为删除
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SealSeServiceEvaluateTypeController/deleteSealSeServiceEvaluateTypeById")
    @ResponseBody
    public void deleteSealSeServiceEvaluateTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
        sealSeServiceEvaluateTypeService.deleteSealSeServiceEvaluateTypeById(inputObject, outputObject);
    }

}
