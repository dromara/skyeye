/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.service.AdvanceChargeService;

/**
 * @Author 卫志强
 * @Description 收预付款
 * @Date 2019/10/20 10:22
 */
@Controller
public class AdvanceChargeController {

    @Autowired
    private AdvanceChargeService advanceChargeService;

    /**
     * 查询收预付款列表信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/AdvanceChargeController/queryAdvanceChargeByList")
    @ResponseBody
    public void queryAdvanceChargeByList(InputObject inputObject, OutputObject outputObject) throws Exception{
        advanceChargeService.queryAdvanceChargeByList(inputObject, outputObject);
    }

    /**
     * 添加收预付款
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/AdvanceChargeController/insertAdvanceCharge")
    @ResponseBody
    public void insertAdvanceCharge(InputObject inputObject, OutputObject outputObject) throws Exception{
        advanceChargeService.insertAdvanceCharge(inputObject, outputObject);
    }

    /**
     * 查询收预付款用于数据回显
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/AdvanceChargeController/queryAdvanceChargeToEditById")
    @ResponseBody
    public void queryAdvanceChargeToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
        advanceChargeService.queryAdvanceChargeToEditById(inputObject, outputObject);
    }

    /**
     * 编辑收预付款信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/AdvanceChargeController/editAdvanceChargeById")
    @ResponseBody
    public void editAdvanceChargeById(InputObject inputObject, OutputObject outputObject) throws Exception{
        advanceChargeService.editAdvanceChargeById(inputObject, outputObject);
    }

    /**
     * 删除收预付款信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/AdvanceChargeController/deleteAdvanceChargeById")
    @ResponseBody
    public void deleteAdvanceChargeById(InputObject inputObject, OutputObject outputObject) throws Exception{
        advanceChargeService.deleteAdvanceChargeById(inputObject, outputObject);
    }

    /**
     * 查看收预付款详情
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/AdvanceChargeController/queryAdvanceChargeByDetail")
    @ResponseBody
    public void queryAdvanceChargeByDetail(InputObject inputObject, OutputObject outputObject) throws Exception{
        advanceChargeService.queryAdvanceChargeByDetail(inputObject, outputObject);
    }
    
    /**
     * 导出Excel
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/AdvanceChargeController/queryMationToExcel")
    @ResponseBody
    public void queryMationToExcel(InputObject inputObject, OutputObject outputObject) throws Exception{
    	advanceChargeService.queryMationToExcel(inputObject, outputObject);
    }
    
}
