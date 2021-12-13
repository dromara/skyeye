/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.activiti.controller;

import com.skyeye.activiti.service.CounterSignService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ClassName: CounterSignController
 * @Description: 会签相关
 * @author: skyeye云系列--卫志强
 * @date: 2021/12/13 20:45
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Controller
public class CounterSignController {

    @Autowired
    private CounterSignService counterSignService;

    /**
     * 将 普通节点转换成为会签 任务
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/CounterSignController/covertToMultiInstance")
    @ResponseBody
    public void covertToMultiInstance(InputObject inputObject, OutputObject outputObject) throws Exception {
        counterSignService.covertToMultiInstance(inputObject, outputObject);
    }

}
