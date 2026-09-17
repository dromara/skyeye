/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.store.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.store.entity.ShopStore;
import com.skyeye.store.service.ShopStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ShopStoreController
 * @Description: 门店管理控制类
 * @author: skyeye云系列--卫志强
 * @date: 2022/2/4 12:34
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "门店管理", tags = "门店管理", modelName = "门店管理")
public class ShopStoreController {

    @Autowired
    private ShopStoreService shopStoreService;

    @ApiOperation(id = "store001", value = "获取门店信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ShopStoreController/queryStoreList")
    public void queryStoreList(InputObject inputObject, OutputObject outputObject) {
        shopStoreService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryStoreListFoServer", value = "其他微服务调用，获取门店信息", method = "POST", allUse = "0")
    @ApiImplicitParams(classBean = CommonPageInfo.class, value = {
        @ApiImplicitParam(id = "enabled", name = "enabled", value = "状态", required = "required,num", defaultValue = "1")})
    @RequestMapping("/post/ShopStoreController/queryStoreListFoServer")
    public void queryStoreListFoServer(InputObject inputObject, OutputObject outputObject) {
        shopStoreService.queryStoreListFoServer(inputObject, outputObject);
    }

    @ApiOperation(id = "writeStore", value = "添加/编辑门店", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = ShopStore.class)
    @RequestMapping("/post/ShopStoreController/writeStore")
    public void writeStore(InputObject inputObject, OutputObject outputObject) {
        shopStoreService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryStoreById", value = "据ID查询门店信息", method = "GET", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ShopStoreController/queryStoreById")
    public void queryStoreById(InputObject inputObject, OutputObject outputObject) {
        shopStoreService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryStoreByIds", value = "根据ID批量查询门店信息", method = "POST", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "ids", name = "ids", value = "主键id", required = "required")})
    @RequestMapping("/post/ShopStoreController/queryStoreByIds")
    public void queryStoreByIds(InputObject inputObject, OutputObject outputObject) {
        shopStoreService.selectByIds(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteStoreById", value = "根据id删除门店信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ShopStoreController/deleteStoreById")
    public void deleteStoreById(InputObject inputObject, OutputObject outputObject) {
        shopStoreService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryStoreListByParams", value = "获取门店列表信息", method = "GET", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "shopAreaId", name = "shopAreaId", value = "区域ID"),
        @ApiImplicitParam(id = "enabled", name = "enabled", value = "状态", required = "num")})
    @RequestMapping("/post/ShopStoreController/queryStoreListByParams")
    public void queryStoreListByParams(InputObject inputObject, OutputObject outputObject) {
        shopStoreService.queryStoreListByParams(inputObject, outputObject);
    }

    @ApiOperation(id = "queryStoreOnlineById", value = "根据门店ID获取门店设置的线上预约信息(已结合当前登陆用户)", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ShopStoreController/queryStoreOnlineById")
    public void queryStoreOnlineById(InputObject inputObject, OutputObject outputObject) {
        shopStoreService.queryStoreOnlineById(inputObject, outputObject);
    }

    @ApiOperation(id = "saveStoreOnlineMation", value = "保存门店线上预约信息", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required"),
        @ApiImplicitParam(id = "startTime", name = "startTime", value = "营业开始时间", required = "required"),
        @ApiImplicitParam(id = "endTime", name = "endTime", value = "营业结束时间", required = "required"),
        @ApiImplicitParam(id = "onlineBookAppoint", name = "onlineBookAppoint", value = "是否开启线上预约", required = "required,num"),
        @ApiImplicitParam(id = "onlineBookRadix", name = "onlineBookRadix", value = "线上预约基数，以分钟为单位，如果设置为30，则会自动计算在营业时间段内的可预约时间段", required = "num"),
        @ApiImplicitParam(id = "onlineBookType", name = "onlineBookType", value = "线上预约类型的设定", required = "num"),
        @ApiImplicitParam(id = "onlineBookJson", name = "onlineBookJson", value = "设置线上预约时需要存储各个时间段内的信息", required = "json")})
    @RequestMapping("/post/ShopStoreController/saveStoreOnlineMation")
    public void saveStoreOnlineMation(InputObject inputObject, OutputObject outputObject) {
        shopStoreService.saveStoreOnlineMation(inputObject, outputObject);
    }

    @ApiOperation(id = "store010", value = "获取门店指定日期的预约信息", method = "GET", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "门店id", required = "required"),
        @ApiImplicitParam(id = "onlineDay", name = "onlineDay", value = "预约日期，格式为YYYY-MM-dd", required = "required")})
    @RequestMapping("/post/ShopStoreController/queryStoreOnlineMationPointDay")
    public void queryStoreOnlineMationPointDay(InputObject inputObject, OutputObject outputObject) {
        shopStoreService.queryStoreOnlineMationPointDay(inputObject, outputObject);
    }

    @ApiOperation(id = "queryMyPersonalStoreList", value = "查询当前会员的个人门店列表", method = "GET", allUse = "2")
    @RequestMapping("/post/ShopStoreController/queryMyPersonalStoreList")
    public void queryMyPersonalStoreList(InputObject inputObject, OutputObject outputObject) {
        shopStoreService.queryMyPersonalStoreList(inputObject, outputObject);
    }

}
