/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.agent.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.ai.agent.boardconfig.AiBizBoardRegistry;
import com.skyeye.ai.agent.listconfig.AiBizListEndpointRegistry;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Agent biz_list / biz_board 内置默认与生效预览，供平台信息设置页使用。
 */
@RestController
@Api(value = "Agent业务接入配置", tags = "Agent业务接入配置", modelName = "AI模块")
public class AiBizListConfigController {

    @Autowired
    private AiBizListEndpointRegistry listRegistry;

    @Autowired
    private AiBizBoardRegistry boardRegistry;

    @ApiOperation(id = "queryAiBizListEndpointsDefault", value = "获取内置默认的biz_list接入配置", method = "GET", allUse = "2")
    @RequestMapping("/post/AiBizListConfigController/queryAiBizListEndpointsDefault")
    public void queryAiBizListEndpointsDefault(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> data = new HashMap<>();
        List<Map<String, Object>> endpoints = listRegistry.getClasspathEndpoints();
        data.put("bizListEndpoints", endpoints);
        data.put("docs", listRegistry.getClasspathDocs());
        data.put("source", "classpath");
        outputObject.setBean(data);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @ApiOperation(id = "queryAiBizListEndpointsEffective", value = "获取当前生效的biz_list接入来源", method = "GET", allUse = "2")
    @RequestMapping("/post/AiBizListConfigController/queryAiBizListEndpointsEffective")
    public void queryAiBizListEndpointsEffective(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> data = new HashMap<>();
        data.put("source", listRegistry.currentSource());
        data.put("supported", listRegistry.supported());
        outputObject.setBean(data);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @ApiOperation(id = "queryAiBizBoardEndpointsDefault", value = "获取内置默认的biz_board接入配置", method = "GET", allUse = "2")
    @RequestMapping("/post/AiBizListConfigController/queryAiBizBoardEndpointsDefault")
    public void queryAiBizBoardEndpointsDefault(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> data = new HashMap<>();
        data.put("bizBoardEndpoints", boardRegistry.getClasspathBoards());
        data.put("docs", boardRegistry.getClasspathDocs());
        data.put("source", "classpath");
        outputObject.setBean(data);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @ApiOperation(id = "queryAiBizBoardEndpointsEffective", value = "获取当前生效的biz_board接入来源", method = "GET", allUse = "2")
    @RequestMapping("/post/AiBizListConfigController/queryAiBizBoardEndpointsEffective")
    public void queryAiBizBoardEndpointsEffective(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> data = new HashMap<>();
        data.put("source", boardRegistry.currentSource());
        data.put("domains", boardRegistry.domains());
        outputObject.setBean(data);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }
}
