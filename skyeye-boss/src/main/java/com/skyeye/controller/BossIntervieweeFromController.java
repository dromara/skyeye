/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.service.BossIntervieweeFromService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ClassName: BossIntervieweeFromController
 * @Description: 面试者来源管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2021/11/7 13:28
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Controller
public class BossIntervieweeFromController {

    @Autowired
    private BossIntervieweeFromService bossIntervieweeFromService;

    /**
     *
     * @Title: queryBossIntervieweeFromList
     * @Description: 分页+title模糊查询动态表单页面分类列表
     * @param inputObject
     * @param outputObject
     * @throws Exception    异常
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/BossIntervieweeFromController/queryBossIntervieweeFromList")
    @ResponseBody
    public void queryBossIntervieweeFromList(InputObject inputObject, OutputObject outputObject) throws Exception{
        bossIntervieweeFromService.queryBossIntervieweeFromList(inputObject, outputObject);
    }

    /**
     *
     * @Title: insertBossIntervieweeFrom
     * @Description: 新增面试者来源信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    异常
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/BossIntervieweeFromController/insertBossIntervieweeFrom")
    @ResponseBody
    public void insertBossIntervieweeFrom(InputObject inputObject, OutputObject outputObject) throws Exception{
        bossIntervieweeFromService.insertBossIntervieweeFrom(inputObject, outputObject);
    }

    /**
     *
     * @Title: delBossIntervieweeFromById
     * @Description: 删除面试者来源信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    异常
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/BossIntervieweeFromController/delBossIntervieweeFromById")
    @ResponseBody
    public void delBossIntervieweeFromById(InputObject inputObject, OutputObject outputObject) throws Exception{
        bossIntervieweeFromService.delBossIntervieweeFromById(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryBossIntervieweeFromById
     * @Description: 根据id查询面试者来源信息详情
     * @param inputObject
     * @param outputObject
     * @throws Exception    异常
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/BossIntervieweeFromController/queryBossIntervieweeFromById")
    @ResponseBody
    public void queryBossIntervieweeFromById(InputObject inputObject, OutputObject outputObject) throws Exception{
        bossIntervieweeFromService.queryBossIntervieweeFromById(inputObject, outputObject);
    }

    /**
     *
     * @Title: updateBossIntervieweeFromById
     * @Description: 通过id编辑对应的面试者来源信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    异常
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/BossIntervieweeFromController/updateBossIntervieweeFromById")
    @ResponseBody
    public void updateBossIntervieweeFromById(InputObject inputObject, OutputObject outputObject) throws Exception{
        bossIntervieweeFromService.updateBossIntervieweeFromById(inputObject, outputObject);
    }

}
