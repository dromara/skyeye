/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.activiti.controller;

import com.skyeye.activiti.service.ActivitiUserService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ClassName: ActivitiUserController
 * @Description: 工作流用户相关内容
 * @author: skyeye云系列--卫志强
 * @date: 2021/12/2 20:45
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Controller
public class ActivitiUserController {

    @Autowired
    private ActivitiUserService activitiUserService;

    /**
     *
     * @Title: queryUserListToActiviti
     * @Description: 获取人员选择
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ActivitiUserController/queryUserListToActiviti")
    @ResponseBody
    public void queryUserListToActiviti(InputObject inputObject, OutputObject outputObject) throws Exception{
        activitiUserService.queryUserListToActiviti(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryUserGroupListToActiviti
     * @Description: 获取组人员选择
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ActivitiUserController/queryUserGroupListToActiviti")
    @ResponseBody
    public void queryUserGroupListToActiviti(InputObject inputObject, OutputObject outputObject) throws Exception{
        activitiUserService.queryUserGroupListToActiviti(inputObject, outputObject);
    }

    /**
     *
     * @Title: insertSyncUserListMationToAct
     * @Description: 用户以及用户组信息同步到act表中
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ActivitiUserController/insertSyncUserListMationToAct")
    @ResponseBody
    public void insertSyncUserListMationToAct(InputObject inputObject, OutputObject outputObject) throws Exception{
        activitiUserService.insertSyncUserListMationToAct(inputObject, outputObject);
    }

}
