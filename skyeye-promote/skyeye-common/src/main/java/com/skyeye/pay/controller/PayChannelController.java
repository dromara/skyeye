/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.pay.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.pay.entity.PayChannel;
import com.skyeye.pay.service.PayChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: PayChannelController
 * @Description: 支付渠道信息管理
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31.
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "支付渠道管理", tags = "支付渠道管理", modelName = "支付渠道管理")
public class PayChannelController {

    @Autowired
    private PayChannelService payChannelService;

    @ApiOperation(id = "writePayChannel", value = "新增/修改支付渠道信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = PayChannel.class)
    @RequestMapping("/post/PayChannelController/writePayChannel")
    public void writePayChannel(InputObject inputObject, OutputObject outputObject) {
        payChannelService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deletePayChannelById", value = "根据id删除支付渠道信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PayChannelController/deletePayChannelById")
    public void deletePayChannelById(InputObject inputObject, OutputObject outputObject) {
        payChannelService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryPayChannelById", value = "根据id查询支付渠道信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PayChannelController/queryPayChannelById")
    public void queryPayChannelById(InputObject inputObject, OutputObject outputObject) {
        payChannelService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryPayChannelList", value = "分页查询支付渠道信息列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/PayChannelController/queryPayChannelList")
    public void queryPayChannelList(InputObject inputObject, OutputObject outputObject) {
        payChannelService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryEnabledPayChannelList", value = "获取指定支付应用下已启用的支付渠道（收银台用）", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "appKey", name = "appKey", value = "支付应用标识，如 tenant-buy、mall-order", required = "required"),
        @ApiImplicitParam(id = "clientType", name = "clientType", value = "客户端类型(选填)：pc-PC网页、h5-手机浏览器、wechat_mp-微信公众号、wechat_mini-微信小程序、app-原生App、bar-付款码收银；传则只返回当前终端可用渠道")
    })
    @RequestMapping("/post/PayChannelController/queryEnabledPayChannelList")
    public void queryEnabledPayChannelList(InputObject inputObject, OutputObject outputObject) {
        payChannelService.queryEnabledPayChannelList(inputObject, outputObject);
    }
}