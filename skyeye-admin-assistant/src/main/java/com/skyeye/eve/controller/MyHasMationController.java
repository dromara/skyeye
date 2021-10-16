/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.MyHasMationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 行政模块我名下的...
 */
@Controller
public class MyHasMationController {

    @Autowired
    private MyHasMationService myHasMationService;

    /**
     *
     * @Title: queryMyAssetManagementList
     * @Description: 获取我名下的资产
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/MyHasMationController/queryMyAssetManagementList")
    @ResponseBody
    public void queryMyAssetManagementList(InputObject inputObject, OutputObject outputObject) throws Exception{
        myHasMationService.queryMyAssetManagementList(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryMySealManageList
     * @Description: 获取我借用中的印章
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/MyHasMationController/queryMySealManageList")
    @ResponseBody
    public void queryMySealManageList(InputObject inputObject, OutputObject outputObject) throws Exception{
        myHasMationService.queryMySealManageList(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryMyLicenceManageList
     * @Description: 获取我借用中的证照
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/MyHasMationController/queryMyLicenceManageList")
    @ResponseBody
    public void queryMyLicenceManageList(InputObject inputObject, OutputObject outputObject) throws Exception{
        myHasMationService.queryMyLicenceManageList(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryMyAssetArticlesList
     * @Description: 我的用品领用历史
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/MyHasMationController/queryMyAssetArticlesList")
    @ResponseBody
    public void queryMyAssetArticlesList(InputObject inputObject, OutputObject outputObject) throws Exception{
        myHasMationService.queryMyAssetArticlesList(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryMyVehicleHistoryList
     * @Description: 我的用车历史
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/MyHasMationController/queryMyVehicleHistoryList")
    @ResponseBody
    public void queryMyVehicleHistoryList(InputObject inputObject, OutputObject outputObject) throws Exception{
        myHasMationService.queryMyVehicleHistoryList(inputObject, outputObject);
    }

}
