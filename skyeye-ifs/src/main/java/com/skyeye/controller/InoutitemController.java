/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.service.InoutitemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @ClassName: InoutitemController
 * @Description: 收支项目管理控制类
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/21 17:14
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Controller
public class InoutitemController {

    @Autowired
    private InoutitemService inoutitemService;

    /**
     * 查询收支项目信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/InoutitemController/queryInoutitemByList")
    @ResponseBody
    public void queryInoutitemByList(InputObject inputObject, OutputObject outputObject) throws Exception{
        inoutitemService.queryInoutitemByList(inputObject, outputObject);
    }

    /**
     * 添加收支项目信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/InoutitemController/insertInoutitem")
    @ResponseBody
    public void insertInoutitem(InputObject inputObject, OutputObject outputObject) throws Exception{
        inoutitemService.insertInoutitem(inputObject, outputObject);
    }

    /**
     * 查询单个收支项目信息，用于信息回显
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/InoutitemController/queryInoutitemById")
    @ResponseBody
    public void queryInoutitemById(InputObject inputObject, OutputObject outputObject) throws Exception{
        inoutitemService.queryInoutitemById(inputObject, outputObject);
    }

    /**
     * 删除收支项目信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/InoutitemController/deleteInoutitemById")
    @ResponseBody
    public void deleteInoutitemById(InputObject inputObject, OutputObject outputObject) throws Exception{
        inoutitemService.deleteInoutitemById(inputObject, outputObject);
    }

    /**
     * 编辑收支项目信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/InoutitemController/editInoutitemById")
    @ResponseBody
    public void editInoutitemById(InputObject inputObject, OutputObject outputObject) throws Exception{
        inoutitemService.editInoutitemById(inputObject, outputObject);
    }

    /**
     * 查看收支项目详情
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/InoutitemController/queryInoutitemByIdAndInfo")
    @ResponseBody
    public void queryInoutitemByIdAndInfo(InputObject inputObject, OutputObject outputObject) throws Exception{
        inoutitemService.queryInoutitemByIdAndInfo(inputObject, outputObject);
    }
    
    /**
     * 根据条件查询收支项目展示为下拉框
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/InoutitemController/queryInoutitemListToSelect")
    @ResponseBody
    public void queryInoutitemListToSelect(InputObject inputObject, OutputObject outputObject) throws Exception{
        inoutitemService.queryInoutitemListToSelect(inputObject, outputObject);
    }

}
