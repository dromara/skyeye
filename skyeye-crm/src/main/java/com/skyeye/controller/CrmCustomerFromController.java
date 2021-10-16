/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.service.CrmCustomerFromService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CrmCustomerFromController {

    @Autowired
    private CrmCustomerFromService crmCustomerFromService;

    /**
     *
     * @Title: insertCrmCustomerFrom
     * @Description: 添加客户来源表信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CrmCustomerFromController/insertCrmCustomerFrom")
    @ResponseBody
    public void insertCrmCustomerFrom(InputObject inputObject, OutputObject outputObject) throws Exception {
        crmCustomerFromService.insertCrmCustomerFrom(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryCrmCustomerFromList
     * @Description: 获取所有客户来源表状态为未被删除的记录
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CrmCustomerFromController/queryCrmCustomerFromList")
    @ResponseBody
    public void queryCrmCustomerFromList(InputObject inputObject, OutputObject outputObject) throws Exception{
        crmCustomerFromService.queryCrmCustomerFromList(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryStateUpList
     * @Description: 获取客户来源表状态为上线的所有记录
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CrmCustomerFromController/queryStateUpList")
    @ResponseBody
    public void queryStateUpList(InputObject inputObject, OutputObject outputObject) throws Exception{
        crmCustomerFromService.queryStateUpList(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryCrmCustomerFromMationById
     * @Description: 通过客户来源表id查询id和name
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CrmCustomerFromController/queryCrmCustomerFromMationById")
    @ResponseBody
    public void queryCrmCustomerFromMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        crmCustomerFromService.queryCrmCustomerFromMationById(inputObject, outputObject);
    }

    /**
     *
     * @Title: ediCrmCustomerFromById
     * @Description: 编辑客户来源表名称
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CrmCustomerFromController/editCrmCustomerFromById")
    @ResponseBody
    public void editCrmCustomerFromById(InputObject inputObject, OutputObject outputObject) throws Exception{
        crmCustomerFromService.editCrmCustomerFromById(inputObject, outputObject);
    }

    /**
     *
     * @Title: editStateUpById
     * @Description: 编辑客户来源表状态为上线
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CrmCustomerFromController/editStateUpById")
    @ResponseBody
    public void editStateUpById(InputObject inputObject, OutputObject outputObject) throws Exception {
        crmCustomerFromService.editStateUpById(inputObject, outputObject);
    }

    /**
     *
     * @Title: editStateDownById
     * @Description: 编辑客户来源表状态为下线
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CrmCustomerFromController/editStateDownById")
    @ResponseBody
    public void editStateDownById(InputObject inputObject, OutputObject outputObject) throws Exception {
        crmCustomerFromService.editStateDownById(inputObject, outputObject);
    }

    /**
     *
     * @Title: deleteCrmCustomerFromById
     * @Description: 编辑客户来源表状态为删除
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CrmCustomerFromController/deleteCrmCustomerFromById")
    @ResponseBody
    public void deleteCrmCustomerFromById(InputObject inputObject, OutputObject outputObject) throws Exception{
        crmCustomerFromService.deleteCrmCustomerFromById(inputObject, outputObject);
    }

}
