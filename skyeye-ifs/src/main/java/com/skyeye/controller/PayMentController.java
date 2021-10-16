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
import com.skyeye.service.PayMentService;

/**
 *
 * @ClassName: PayMentController
 * @Description: 付款单管理控制类
 * @author: skyeye云系列--卫志强
 * @date: 2021/10/6 9:23
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Controller
public class PayMentController {

    @Autowired
    private PayMentService payMentService;

    /**
     * 查询付款单列表信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/PayMentController/queryPayMentByList")
    @ResponseBody
    public void queryPayMentByList(InputObject inputObject, OutputObject outputObject) throws Exception{
        payMentService.queryPayMentByList(inputObject, outputObject);
    }

    /**
     * 添加付款单
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/PayMentController/insertPayMent")
    @ResponseBody
    public void insertPayMent(InputObject inputObject, OutputObject outputObject) throws Exception{
        payMentService.insertPayMent(inputObject, outputObject);
    }

    /**
     * 查询付款单用于数据回显
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/PayMentController/queryPayMentToEditById")
    @ResponseBody
    public void queryPayMentToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
        payMentService.queryPayMentToEditById(inputObject, outputObject);
    }

    /**
     * 编辑付款单信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/PayMentController/editPayMentById")
    @ResponseBody
    public void editPayMentById(InputObject inputObject, OutputObject outputObject) throws Exception{
        payMentService.editPayMentById(inputObject, outputObject);
    }

    /**
     * 删除付款单信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/PayMentController/deletePayMentById")
    @ResponseBody
    public void deletePayMentById(InputObject inputObject, OutputObject outputObject) throws Exception{
        payMentService.deletePayMentById(inputObject, outputObject);
    }

    /**
     * 查看付款单详情
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/PayMentController/queryPayMentByDetail")
    @ResponseBody
    public void queryPayMentByDetail(InputObject inputObject, OutputObject outputObject) throws Exception{
        payMentService.queryPayMentByDetail(inputObject, outputObject);
    }
    
    /**
     * 导出Excel
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/PayMentController/queryMationToExcel")
    @ResponseBody
    public void queryMationToExcel(InputObject inputObject, OutputObject outputObject) throws Exception{
    	payMentService.queryMationToExcel(inputObject, outputObject);
    }
    
}
