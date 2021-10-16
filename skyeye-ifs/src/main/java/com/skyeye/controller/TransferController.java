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
import com.skyeye.service.TransferService;

/**
 *
 * @ClassName: TransferController
 * @Description: 转账单管理控制类
 * @author: skyeye云系列--卫志强
 * @date: 2021/10/6 9:24
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Controller
public class TransferController {

    @Autowired
    private TransferService transferService;

    /**
     * 查询转账单列表信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/TransferController/queryTransferByList")
    @ResponseBody
    public void queryTransferByList(InputObject inputObject, OutputObject outputObject) throws Exception{
        transferService.queryTransferByList(inputObject, outputObject);
    }

    /**
     * 添加转账单
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/TransferController/insertTransfer")
    @ResponseBody
    public void insertTransfer(InputObject inputObject, OutputObject outputObject) throws Exception{
        transferService.insertTransfer(inputObject, outputObject);
    }

    /**
     * 查询转账单用于数据回显
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/TransferController/queryTransferToEditById")
    @ResponseBody
    public void queryTransferToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
        transferService.queryTransferToEditById(inputObject, outputObject);
    }

    /**
     * 编辑转账单信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/TransferController/editTransferById")
    @ResponseBody
    public void editTransferById(InputObject inputObject, OutputObject outputObject) throws Exception{
        transferService.editTransferById(inputObject, outputObject);
    }

    /**
     * 删除转账单信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/TransferController/deleteTransferById")
    @ResponseBody
    public void deleteTransferById(InputObject inputObject, OutputObject outputObject) throws Exception{
        transferService.deleteTransferById(inputObject, outputObject);
    }

    /**
     * 查看转账单详情
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/TransferController/queryTransferByDetail")
    @ResponseBody
    public void queryTransferByDetail(InputObject inputObject, OutputObject outputObject) throws Exception{
        transferService.queryTransferByDetail(inputObject, outputObject);
    }
    
    /**
     * 导出Excel
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/TransferController/queryMationToExcel")
    @ResponseBody
    public void queryMationToExcel(InputObject inputObject, OutputObject outputObject) throws Exception{
    	transferService.queryMationToExcel(inputObject, outputObject);
    }
    
}
