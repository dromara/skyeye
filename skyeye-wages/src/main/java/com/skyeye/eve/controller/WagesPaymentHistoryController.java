package com.skyeye.eve.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.WagesPaymentHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class WagesPaymentHistoryController {

    @Autowired
    private WagesPaymentHistoryService wagesPaymentHistoryService;

    /**
     *
     * @Title: queryAllGrantWagesPaymentHistoryList
     * @Description: 获取所有已发放薪资发放历史列表
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/WagesPaymentHistoryController/queryAllGrantWagesPaymentHistoryList")
    @ResponseBody
    public void queryAllGrantWagesPaymentHistoryList(InputObject inputObject, OutputObject outputObject) throws Exception{
        wagesPaymentHistoryService.queryAllGrantWagesPaymentHistoryList(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryMyWagesPaymentHistoryList
     * @Description: 获取我的薪资发放历史列表
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/WagesPaymentHistoryController/queryMyWagesPaymentHistoryList")
    @ResponseBody
    public void queryMyWagesPaymentHistoryList(InputObject inputObject, OutputObject outputObject) throws Exception{
        wagesPaymentHistoryService.queryMyWagesPaymentHistoryList(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryAllNotGrantWagesPaymentHistoryList
     * @Description: 获取所有待发放薪资列表
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/WagesPaymentHistoryController/queryAllNotGrantWagesPaymentHistoryList")
    @ResponseBody
    public void queryAllNotGrantWagesPaymentHistoryList(InputObject inputObject, OutputObject outputObject) throws Exception{
        wagesPaymentHistoryService.queryAllNotGrantWagesPaymentHistoryList(inputObject, outputObject);
    }

}
