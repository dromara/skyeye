/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.SysQuartzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SysQuartzController {

    @Autowired
    private SysQuartzService sysQuartzService;

    /**
     *
     * @Title: querySystemQuartzList
     * @Description: 获取系统定时任务列表
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SysQuartzController/querySystemQuartzList")
    @ResponseBody
    public void querySystemQuartzList(InputObject inputObject, OutputObject outputObject) throws Exception{
        sysQuartzService.querySystemQuartzList(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryMyTaskQuartzList
     * @Description: 获取我的定时任务列表
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SysQuartzController/queryMyTaskQuartzList")
    @ResponseBody
    public void queryMyTaskQuartzList(InputObject inputObject, OutputObject outputObject) throws Exception{
        sysQuartzService.queryMyTaskQuartzList(inputObject, outputObject);
    }

    /**
     *
     * @Title: runSystemQuartz
     * @Description: 启动系统定时任务
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/SysQuartzController/runSystemQuartz")
    @ResponseBody
    public void runSystemQuartz(InputObject inputObject, OutputObject outputObject) throws Exception{
        sysQuartzService.runSystemQuartz(inputObject, outputObject);
    }

}
