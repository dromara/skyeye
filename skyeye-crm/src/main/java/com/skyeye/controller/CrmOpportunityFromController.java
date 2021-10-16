/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.service.CrmOpportunityFromService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CrmOpportunityFromController {

    @Autowired
    private CrmOpportunityFromService crmOpportunityFromService;

    /**
     *
     * @Title: insertCrmOpportunityFrom
     * @Description: 添加商机来源信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CrmOpportunityFromController/insertCrmOpportunityFrom")
    @ResponseBody
    public void insertCrmOpportunityFrom(InputObject inputObject, OutputObject outputObject) throws Exception {
        crmOpportunityFromService.insertCrmOpportunityFrom(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryCrmOpportunityFromList
     * @Description: 获取所有商机来源状态为未被删除的记录
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CrmOpportunityFromController/queryCrmOpportunityFromList")
    @ResponseBody
    public void queryCrmOpportunityFromList(InputObject inputObject, OutputObject outputObject) throws Exception{
        crmOpportunityFromService.queryCrmOpportunityFromList(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryStateUpList
     * @Description: 获取商机来源状态为上线的所有记录
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CrmOpportunityFromController/queryStateUpList")
    @ResponseBody
    public void queryStateUpList(InputObject inputObject, OutputObject outputObject) throws Exception{
        crmOpportunityFromService.queryStateUpList(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryCrmOpportunityFromMationById
     * @Description: 通过商机来源id查询id和name
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CrmOpportunityFromController/queryCrmOpportunityFromMationById")
    @ResponseBody
    public void queryCrmOpportunityFromMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        crmOpportunityFromService.queryCrmOpportunityFromMationById(inputObject, outputObject);
    }

    /**
     *
     * @Title: ediCrmOpportunityFromById
     * @Description: 编辑商机来源名称
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CrmOpportunityFromController/editCrmOpportunityFromById")
    @ResponseBody
    public void editCrmOpportunityFromById(InputObject inputObject, OutputObject outputObject) throws Exception{
        crmOpportunityFromService.editCrmOpportunityFromById(inputObject, outputObject);
    }

    /**
     *
     * @Title: editStateUpById
     * @Description: 编辑商机来源状态为上线
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CrmOpportunityFromController/editStateUpById")
    @ResponseBody
    public void editStateUpById(InputObject inputObject, OutputObject outputObject) throws Exception {
        crmOpportunityFromService.editStateUpById(inputObject, outputObject);
    }

    /**
     *
     * @Title: editStateDownById
     * @Description: 编辑商机来源状态为下线
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CrmOpportunityFromController/editStateDownById")
    @ResponseBody
    public void editStateDownById(InputObject inputObject, OutputObject outputObject) throws Exception {
        crmOpportunityFromService.editStateDownById(inputObject, outputObject);
    }

    /**
     *
     * @Title: deleteCrmOpportunityFromById
     * @Description: 编辑商机来源状态为删除
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CrmOpportunityFromController/deleteCrmOpportunityFromById")
    @ResponseBody
    public void deleteCrmOpportunityFromById(InputObject inputObject, OutputObject outputObject) throws Exception{
        crmOpportunityFromService.deleteCrmOpportunityFromById(inputObject, outputObject);
    }

}
