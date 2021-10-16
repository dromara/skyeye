package com.skyeye.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ApiController {

    @Autowired
    private ApiService apiService;

    /**
     *
     * @Title: queryAllSysEveReqMapping
     * @Description: 获取接口列表
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ApiController/queryAllSysEveReqMapping")
    @ResponseBody
    public void queryAllSysEveReqMapping(InputObject inputObject, OutputObject outputObject) throws Exception{
        apiService.queryAllSysEveReqMapping(inputObject, outputObject);
    }

    /**
     *
     * @Title: querySysEveReqMappingMation
     * @Description: 获取接口详情
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ApiController/querySysEveReqMappingMation")
    @ResponseBody
    public void querySysEveReqMappingMation(InputObject inputObject, OutputObject outputObject) throws Exception{
        apiService.querySysEveReqMappingMation(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryLimitRestrictions
     * @Description: 获取限制条件
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ApiController/queryLimitRestrictions")
    @ResponseBody
    public void queryLimitRestrictions(InputObject inputObject, OutputObject outputObject) throws Exception{
        apiService.queryLimitRestrictions(inputObject, outputObject);
    }

}
