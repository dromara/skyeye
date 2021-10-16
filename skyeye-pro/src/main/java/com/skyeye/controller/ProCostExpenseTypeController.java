/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.service.ProCostExpenseTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ProCostExpenseTypeController {

    @Autowired
    private ProCostExpenseTypeService proCostExpenseTypeService;

    /**
     *
     * @Title: insertProCostExpenseType
     * @Description: 添加成本费用支出分类信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ProCostExpenseTypeController/insertProCostExpenseType")
    @ResponseBody
    public void insertProCostExpenseType(InputObject inputObject, OutputObject outputObject) throws Exception {
        proCostExpenseTypeService.insertProCostExpenseType(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryProCostExpenseTypeList
     * @Description: 获取所有成本费用支出分类状态为未被删除的记录
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ProCostExpenseTypeController/queryProCostExpenseTypeList")
    @ResponseBody
    public void queryProCostExpenseTypeList(InputObject inputObject, OutputObject outputObject) throws Exception{
        proCostExpenseTypeService.queryProCostExpenseTypeList(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryStateUpList
     * @Description: 获取成本费用支出分类状态为上线的所有记录
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ProCostExpenseTypeController/queryStateUpList")
    @ResponseBody
    public void queryStateUpList(InputObject inputObject, OutputObject outputObject) throws Exception{
        proCostExpenseTypeService.queryStateUpList(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryProCostExpenseTypeMationById
     * @Description: 通过成本费用支出分类id查询id和name
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ProCostExpenseTypeController/queryProCostExpenseTypeMationById")
    @ResponseBody
    public void queryProCostExpenseTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        proCostExpenseTypeService.queryProCostExpenseTypeMationById(inputObject, outputObject);
    }

    /**
     *
     * @Title: ediProCostExpenseTypeById
     * @Description: 编辑成本费用支出分类名称
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ProCostExpenseTypeController/editProCostExpenseTypeById")
    @ResponseBody
    public void editProCostExpenseTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
        proCostExpenseTypeService.editProCostExpenseTypeById(inputObject, outputObject);
    }

    /**
     *
     * @Title: editStateUpById
     * @Description: 编辑成本费用支出分类状态为上线
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ProCostExpenseTypeController/editStateUpById")
    @ResponseBody
    public void editStateUpById(InputObject inputObject, OutputObject outputObject) throws Exception {
        proCostExpenseTypeService.editStateUpById(inputObject, outputObject);
    }

    /**
     *
     * @Title: editStateDownById
     * @Description: 编辑成本费用支出分类状态为下线
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ProCostExpenseTypeController/editStateDownById")
    @ResponseBody
    public void editStateDownById(InputObject inputObject, OutputObject outputObject) throws Exception {
        proCostExpenseTypeService.editStateDownById(inputObject, outputObject);
    }

    /**
     *
     * @Title: deleteProCostExpenseTypeById
     * @Description: 编辑成本费用支出分类状态为删除
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ProCostExpenseTypeController/deleteProCostExpenseTypeById")
    @ResponseBody
    public void deleteProCostExpenseTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
        proCostExpenseTypeService.deleteProCostExpenseTypeById(inputObject, outputObject);
    }

}
