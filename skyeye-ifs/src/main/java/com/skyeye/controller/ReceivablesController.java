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
import com.skyeye.service.ReceivablesService;

/**
 *
 * @ClassName: ReceivablesController
 * @Description: 收款单管理控制类
 * @author: skyeye云系列--卫志强
 * @date: 2021/10/6 9:23
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Controller
public class ReceivablesController {

    @Autowired
    private ReceivablesService receivablesService;

    /**
     * 查询收款单列表信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ReceivablesController/queryReceivablesByList")
    @ResponseBody
    public void queryReceivablesByList(InputObject inputObject, OutputObject outputObject) throws Exception{
        receivablesService.queryReceivablesByList(inputObject, outputObject);
    }

    /**
     * 添加收款单
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ReceivablesController/insertReceivables")
    @ResponseBody
    public void insertReceivables(InputObject inputObject, OutputObject outputObject) throws Exception{
        receivablesService.insertReceivables(inputObject, outputObject);
    }

    /**
     * 查询收款单用于数据回显
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ReceivablesController/queryReceivablesToEditById")
    @ResponseBody
    public void queryReceivablesToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
        receivablesService.queryReceivablesToEditById(inputObject, outputObject);
    }

    /**
     * 编辑收款单信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ReceivablesController/editReceivablesById")
    @ResponseBody
    public void editReceivablesById(InputObject inputObject, OutputObject outputObject) throws Exception{
        receivablesService.editReceivablesById(inputObject, outputObject);
    }

    /**
     * 删除收款单信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ReceivablesController/deleteReceivablesById")
    @ResponseBody
    public void deleteReceivablesById(InputObject inputObject, OutputObject outputObject) throws Exception{
        receivablesService.deleteReceivablesById(inputObject, outputObject);
    }

    /**
     * 查看收款单详情
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ReceivablesController/queryReceivablesByDetail")
    @ResponseBody
    public void queryReceivablesByDetail(InputObject inputObject, OutputObject outputObject) throws Exception{
        receivablesService.queryReceivablesByDetail(inputObject, outputObject);
    }
    
    /**
     * 导出Excel
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ReceivablesController/queryMationToExcel")
    @ResponseBody
    public void queryMationToExcel(InputObject inputObject, OutputObject outputObject) throws Exception{
    	receivablesService.queryMationToExcel(inputObject, outputObject);
    }
    
}
