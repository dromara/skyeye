/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 */

package com.skyeye.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.service.ErpFarmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author 卫志强
 * @title: ErpFarmController
 * @projectName skyeye-promote
 * @description: 加工车间
 * @date 2020/8/30 14:09
 */
@Controller
public class ErpFarmController {

    @Autowired
    private ErpFarmService erpFarmService;

    /**
     * 获取车间列表
     * @param inputObject 入参
     * @param outputObject 出参
     * @throws Exception
     */
    @RequestMapping("/post/ErpFarmController/queryErpFarmList")
    @ResponseBody
    public void queryErpFarmList(InputObject inputObject, OutputObject outputObject) throws Exception{
        erpFarmService.queryErpFarmList(inputObject, outputObject);
    }

    /**
     * 新增车间
     * @param inputObject 入参
     * @param outputObject 出参
     * @throws Exception
     */
    @RequestMapping("/post/ErpFarmController/insertErpFarmMation")
    @ResponseBody
    public void insertErpFarmMation(InputObject inputObject, OutputObject outputObject) throws Exception{
        erpFarmService.insertErpFarmMation(inputObject, outputObject);
    }

    /**
     * 编辑车间信息时回显
     * @param inputObject 入参
     * @param outputObject 出参
     * @throws Exception
     */
    @RequestMapping("/post/ErpFarmController/queryErpFarmToEditById")
    @ResponseBody
    public void queryErpFarmToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
        erpFarmService.queryErpFarmToEditById(inputObject, outputObject);
    }

    /**
     * 编辑车间信息
     * @param inputObject 入参
     * @param outputObject 出参
     * @throws Exception
     */
    @RequestMapping("/post/ErpFarmController/editErpFarmMationById")
    @ResponseBody
    public void editErpFarmMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
        erpFarmService.editErpFarmMationById(inputObject, outputObject);
    }

    /**
     * 删除车间信息
     * @param inputObject 入参
     * @param outputObject 出参
     * @throws Exception
     */
    @RequestMapping("/post/ErpFarmController/deleteErpFarmMationById")
    @ResponseBody
    public void deleteErpFarmMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
        erpFarmService.deleteErpFarmMationById(inputObject, outputObject);
    }

    /**
     * 获取车间详情信息
     * @param inputObject 入参
     * @param outputObject 出参
     * @throws Exception
     */
    @RequestMapping("/post/ErpFarmController/queryErpFarmMationById")
    @ResponseBody
    public void queryErpFarmMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
        erpFarmService.queryErpFarmMationById(inputObject, outputObject);
    }

    /**
     * 根据工序id获取车间列表
     * @param inputObject 入参
     * @param outputObject 出参
     * @throws Exception
     */
    @RequestMapping("/post/ErpFarmController/queryErpFarmListByProcedureId")
    @ResponseBody
    public void queryErpFarmListByProcedureId(InputObject inputObject, OutputObject outputObject) throws Exception{
        erpFarmService.queryErpFarmListByProcedureId(inputObject, outputObject);
    }

    /**
     * 修改车间信息为正常
     * @param inputObject 入参
     * @param outputObject 出参
     * @throws Exception
     */
    @RequestMapping("/post/ErpFarmController/normalErpFarmMationById")
    @ResponseBody
    public void normalErpFarmMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
        erpFarmService.normalErpFarmMationById(inputObject, outputObject);
    }

    /**
     * 修改车间信息为维修整改
     * @param inputObject 入参
     * @param outputObject 出参
     * @throws Exception
     */
    @RequestMapping("/post/ErpFarmController/rectificationErpFarmMationById")
    @ResponseBody
    public void rectificationErpFarmMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
        erpFarmService.rectificationErpFarmMationById(inputObject, outputObject);
    }
    
    /**
     * 查询车间列表展示为表格供其他选择
     * @param inputObject 入参
     * @param outputObject 出参
     * @throws Exception
     */
    @RequestMapping("/post/ErpFarmController/queryErpFarmListToTable")
    @ResponseBody
    public void queryErpFarmListToTable(InputObject inputObject, OutputObject outputObject) throws Exception{
        erpFarmService.queryErpFarmListToTable(inputObject, outputObject);
    }
    
    /**
     * 根据车间id串获取车间列表
     * @param inputObject 入参
     * @param outputObject 出参
     * @throws Exception
     */
    @RequestMapping("/post/ErpFarmController/queryErpFarmProcedureListByIds")
    @ResponseBody
    public void queryErpFarmProcedureListByIds(InputObject inputObject, OutputObject outputObject) throws Exception{
        erpFarmService.queryErpFarmProcedureListByIds(inputObject, outputObject);
    }

}
