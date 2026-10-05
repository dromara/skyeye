package com.skyeye.finance.page.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.finance.page.service.IfsPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 财务流程板统计。
 */
@RestController
@Api(value = "财务统计模块", tags = "财务统计模块", modelName = "财务中枢")
public class IfsPageController {

    @Autowired
    private IfsPageService ifsPageService;

    /**
     * 一次性返回财务业务流程各节点数量，key 为节点 code（F00/F01/G01/H01/J01 等），value 为笔数。
     */
    @ApiOperation(id = "queryIfsProcessFlowCount", value = "获取财务业务流程各节点单据数量", method = "POST", allUse = "2")
    @RequestMapping("/post/IfsPageController/queryIfsProcessFlowCount")
    public void queryIfsProcessFlowCount(InputObject inputObject, OutputObject outputObject) {
        ifsPageService.queryProcessFlowCount(inputObject, outputObject);
    }
}
