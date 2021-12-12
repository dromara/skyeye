/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.CheckWorkOvertimeService;

/**
 *
 * @ClassName: CheckWorkOvertimeController
 * @Description: 加班申请控制类
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/8 22:19
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Controller
public class CheckWorkOvertimeController {

    @Autowired
    private CheckWorkOvertimeService checkWorkOvertimeService;

    /**
     *
     * @Title: queryMyCheckWorkOvertimeList
     * @Description: 获取我的加班申请列表
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CheckWorkOvertimeController/queryMyCheckWorkOvertimeList")
    @ResponseBody
    public void queryMyCheckWorkOvertimeList(InputObject inputObject, OutputObject outputObject) throws Exception{
        checkWorkOvertimeService.queryMyCheckWorkOvertimeList(inputObject, outputObject);
    }
    
    /**
     *
     * @Title: insertCheckWorkOvertimeMation
     * @Description: 新增加班申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CheckWorkOvertimeController/insertCheckWorkOvertimeMation")
    @ResponseBody
    public void insertCheckWorkOvertimeMation(InputObject inputObject, OutputObject outputObject) throws Exception{
        checkWorkOvertimeService.insertCheckWorkOvertimeMation(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryCheckWorkOvertimeToEditById
     * @Description: 加班申请编辑时进行回显
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CheckWorkOvertimeController/queryCheckWorkOvertimeToEditById")
    @ResponseBody
    public void queryCheckWorkOvertimeToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
        checkWorkOvertimeService.queryCheckWorkOvertimeToEditById(inputObject, outputObject);
    }

    /**
     *
     * @Title: updateCheckWorkOvertimeById
     * @Description: 编辑加班申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CheckWorkOvertimeController/updateCheckWorkOvertimeById")
    @ResponseBody
    public void updateCheckWorkOvertimeById(InputObject inputObject, OutputObject outputObject) throws Exception{
        checkWorkOvertimeService.updateCheckWorkOvertimeById(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryCheckWorkOvertimeDetailsById
     * @Description: 加班申请详情
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CheckWorkOvertimeController/queryCheckWorkOvertimeDetailsById")
    @ResponseBody
    public void queryCheckWorkOvertimeDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception{
        checkWorkOvertimeService.queryCheckWorkOvertimeDetailsById(inputObject, outputObject);
    }

    /**
     *
     * @Title: editCheckWorkOvertimeToSubApproval
     * @Description: 提交审批加班申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CheckWorkOvertimeController/editCheckWorkOvertimeToSubApproval")
    @ResponseBody
    public void editCheckWorkOvertimeToSubApproval(InputObject inputObject, OutputObject outputObject) throws Exception{
        checkWorkOvertimeService.editCheckWorkOvertimeToSubApproval(inputObject, outputObject);
    }

    /**
     *
     * @Title: updateCheckWorkOvertimeToCancellation
     * @Description: 作废加班申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CheckWorkOvertimeController/updateCheckWorkOvertimeToCancellation")
    @ResponseBody
    public void updateCheckWorkOvertimeToCancellation(InputObject inputObject, OutputObject outputObject) throws Exception{
        checkWorkOvertimeService.updateCheckWorkOvertimeToCancellation(inputObject, outputObject);
    }

    /**
     *
     * @Title: editCheckWorkOvertimeToRevoke
     * @Description: 撤销加班申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CheckWorkOvertimeController/editCheckWorkOvertimeToRevoke")
    @ResponseBody
    public void editCheckWorkOvertimeToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception{
        checkWorkOvertimeService.editCheckWorkOvertimeToRevoke(inputObject, outputObject);
    }

}
