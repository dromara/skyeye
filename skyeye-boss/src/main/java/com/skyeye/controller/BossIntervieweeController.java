/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.service.BossIntervieweeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ClassName: BossIntervieweeController
 * @Description: 面试者管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2021/11/27 13:28
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Controller
public class BossIntervieweeController {

    @Autowired
    private BossIntervieweeService bossIntervieweeService;

    /**
     *
     * @Title: queryBossIntervieweeList
     * @Description: 分页+title模糊查询动态表单页面分类列表
     * @param inputObject
     * @param outputObject
     * @throws Exception    异常
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/BossIntervieweeController/queryBossIntervieweeList")
    @ResponseBody
    public void queryBossIntervieweeList(InputObject inputObject, OutputObject outputObject) throws Exception{
        bossIntervieweeService.queryBossIntervieweeList(inputObject, outputObject);
    }

    /**
     *
     * @Title: insertBossInterviewee
     * @Description: 新增面试者信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    异常
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/BossIntervieweeController/insertBossInterviewee")
    @ResponseBody
    public void insertBossInterviewee(InputObject inputObject, OutputObject outputObject) throws Exception{
        bossIntervieweeService.insertBossInterviewee(inputObject, outputObject);
    }

    /**
     *
     * @Title: delBossIntervieweeById
     * @Description: 删除面试者信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    异常
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/BossIntervieweeController/delBossIntervieweeById")
    @ResponseBody
    public void delBossIntervieweeById(InputObject inputObject, OutputObject outputObject) throws Exception{
        bossIntervieweeService.delBossIntervieweeById(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryBossIntervieweeById
     * @Description: 根据id查询面试者详情
     * @param inputObject
     * @param outputObject
     * @throws Exception    异常
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/BossIntervieweeController/queryBossIntervieweeById")
    @ResponseBody
    public void queryBossIntervieweeById(InputObject inputObject, OutputObject outputObject) throws Exception{
        bossIntervieweeService.queryBossIntervieweeById(inputObject, outputObject);
    }

    /**
     *
     * @Title: updateBossIntervieweeById
     * @Description: 通过id编辑对应的面试者信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    异常
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/BossIntervieweeController/updateBossIntervieweeById")
    @ResponseBody
    public void updateBossIntervieweeById(InputObject inputObject, OutputObject outputObject) throws Exception{
        bossIntervieweeService.updateBossIntervieweeById(inputObject, outputObject);
    }

}
