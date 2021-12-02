/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.activiti.controller;

import com.skyeye.activiti.service.DsFormActivitiService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ClassName: DsFormActivitiController
 * @Description: 动态表单类型的工作流相关内容
 * @author: skyeye云系列--卫志强
 * @date: 2021/12/2 20:24
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Controller
public class DsFormActivitiController {

    @Autowired
    private DsFormActivitiService dsFormActivitiService;

    /**
     *
     * @Title: insertDSFormProcess
     * @Description: 动态表单类型的工作流提交审批
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/DsFormActivitiController/insertDSFormProcess")
    @ResponseBody
    public void insertDSFormProcess(InputObject inputObject, OutputObject outputObject) throws Exception{
        dsFormActivitiService.insertDSFormProcess(inputObject, outputObject);
    }

    /**
     *
     * @Title: editDsFormContentToRevokeByProcessInstanceId
     * @Description: 动态表单类型的工作流进行撤销操作
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/DsFormActivitiController/editDsFormContentToRevokeByProcessInstanceId")
    @ResponseBody
    public void editDsFormContentToRevokeByProcessInstanceId(InputObject inputObject, OutputObject outputObject) throws Exception{
        dsFormActivitiService.editDsFormContentToRevokeByProcessInstanceId(inputObject, outputObject);
    }

}
