/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.service.CrmCustomerIndustryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CrmCustomerIndustryController {

    @Autowired
    private CrmCustomerIndustryService crmCustomerIndustryService;

    /**
     *
     * @Title: insertCrmCustomerIndustry
     * @Description: 添加客户所属行业信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CrmCustomerIndustryController/insertCrmCustomerIndustry")
    @ResponseBody
    public void insertCrmCustomerIndustry(InputObject inputObject, OutputObject outputObject) throws Exception {
        crmCustomerIndustryService.insertCrmCustomerIndustry(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryCrmCustomerIndustryList
     * @Description: 获取所有客户所属行业状态为未被删除的记录
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CrmCustomerIndustryController/queryCrmCustomerIndustryList")
    @ResponseBody
    public void queryCrmCustomerIndustryList(InputObject inputObject, OutputObject outputObject) throws Exception{
        crmCustomerIndustryService.queryCrmCustomerIndustryList(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryStateUpList
     * @Description: 获取客户所属行业状态为上线的所有记录
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CrmCustomerIndustryController/queryStateUpList")
    @ResponseBody
    public void queryStateUpList(InputObject inputObject, OutputObject outputObject) throws Exception{
        crmCustomerIndustryService.queryStateUpList(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryCrmCustomerIndustryMationById
     * @Description: 通过客户所属行业id查询id和name
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CrmCustomerIndustryController/queryCrmCustomerIndustryMationById")
    @ResponseBody
    public void queryCrmCustomerIndustryMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        crmCustomerIndustryService.queryCrmCustomerIndustryMationById(inputObject, outputObject);
    }

    /**
     *
     * @Title: ediCrmCustomerIndustryById
     * @Description: 编辑客户所属行业名称
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CrmCustomerIndustryController/editCrmCustomerIndustryById")
    @ResponseBody
    public void editCrmCustomerIndustryById(InputObject inputObject, OutputObject outputObject) throws Exception{
        crmCustomerIndustryService.editCrmCustomerIndustryById(inputObject, outputObject);
    }

    /**
     *
     * @Title: editStateUpById
     * @Description: 编辑客户所属行业状态为上线
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CrmCustomerIndustryController/editStateUpById")
    @ResponseBody
    public void editStateUpById(InputObject inputObject, OutputObject outputObject) throws Exception {
        crmCustomerIndustryService.editStateUpById(inputObject, outputObject);
    }

    /**
     *
     * @Title: editStateDownById
     * @Description: 编辑客户所属行业状态为下线
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CrmCustomerIndustryController/editStateDownById")
    @ResponseBody
    public void editStateDownById(InputObject inputObject, OutputObject outputObject) throws Exception {
        crmCustomerIndustryService.editStateDownById(inputObject, outputObject);
    }

    /**
     *
     * @Title: deleteCrmCustomerIndustryById
     * @Description: 编辑客户所属行业状态为删除
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CrmCustomerIndustryController/deleteCrmCustomerIndustryById")
    @ResponseBody
    public void deleteCrmCustomerIndustryById(InputObject inputObject, OutputObject outputObject) throws Exception{
        crmCustomerIndustryService.deleteCrmCustomerIndustryById(inputObject, outputObject);
    }

}
