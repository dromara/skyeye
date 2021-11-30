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
import com.skyeye.service.ExpenditureService;

/**
 *
 * @ClassName: ExpenditureController
 * @Description: 记账支出管理控制类
 * @author: skyeye云系列--卫志强
 * @date: 2021/10/6 9:21
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Controller
public class ExpenditureController {

    @Autowired
    private ExpenditureService expenditureService;

    /**
     * 查询记账支出列表信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ExpenditureController/queryExpenditureByList")
    @ResponseBody
    public void queryExpenditureByList(InputObject inputObject, OutputObject outputObject) throws Exception{
        expenditureService.queryExpenditureByList(inputObject, outputObject);
    }

    /**
     * 添加记账支出
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ExpenditureController/insertExpenditure")
    @ResponseBody
    public void insertExpenditure(InputObject inputObject, OutputObject outputObject) throws Exception{
        expenditureService.insertExpenditure(inputObject, outputObject);
    }

    /**
     * 查询记账支出用于数据回显
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ExpenditureController/queryExpenditureToEditById")
    @ResponseBody
    public void queryExpenditureToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
        expenditureService.queryExpenditureToEditById(inputObject, outputObject);
    }

    /**
     * 编辑记账支出信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ExpenditureController/editExpenditureById")
    @ResponseBody
    public void editExpenditureById(InputObject inputObject, OutputObject outputObject) throws Exception{
        expenditureService.editExpenditureById(inputObject, outputObject);
    }

    /**
     * 删除记账支出信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ExpenditureController/deleteExpenditureById")
    @ResponseBody
    public void deleteExpenditureById(InputObject inputObject, OutputObject outputObject) throws Exception{
        expenditureService.deleteExpenditureById(inputObject, outputObject);
    }

    /**
     * 查看记账支出详情
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ExpenditureController/queryExpenditureByDetail")
    @ResponseBody
    public void queryExpenditureByDetail(InputObject inputObject, OutputObject outputObject) throws Exception{
        expenditureService.queryExpenditureByDetail(inputObject, outputObject);
    }
    
    /**
     * 导出Excel
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ExpenditureController/queryMationToExcel")
    @ResponseBody
    public void queryMationToExcel(InputObject inputObject, OutputObject outputObject) throws Exception{
    	expenditureService.queryMationToExcel(inputObject, outputObject);
    }
    
}
