/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.service.StoreHouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author 卫志强
 * @Description 仓库管理
 * @Date 2019/9/14 10:32
 */
@Controller
public class StoreHouseController {

    @Autowired
    private StoreHouseService storeHouseService;

    /**
     * 获取仓库信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/StoreHouseController/queryStoreHouseByList")
    @ResponseBody
    public void queryStoreHouseByList(InputObject inputObject, OutputObject outputObject) throws Exception{
        storeHouseService.queryStoreHouseByList(inputObject, outputObject);
    }

    /**
     * 添加仓库信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/StoreHouseController/insertStoreHouse")
    @ResponseBody
    public void insertStoreHouse(InputObject inputObject, OutputObject outputObject) throws Exception{
        storeHouseService.insertStoreHouse(inputObject, outputObject);
    }

    /**
     * 查询单个仓库信息，用于数据回显
     * @param inputObject{"id"}
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/StoreHouseController/queryStoreHouseById")
    @ResponseBody
    public void queryStoreHouseById(InputObject inputObject, OutputObject outputObject) throws Exception{
        storeHouseService.queryStoreHouseById(inputObject, outputObject);
    }

    /**
     * 删除仓库信息
     * @param inputObject{"id"}
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/StoreHouseController/deleteStoreHouseById")
    @ResponseBody
    public void deleteStoreHouseById(InputObject inputObject, OutputObject outputObject) throws Exception{
        storeHouseService.deleteStoreHouseById(inputObject, outputObject);
    }

    /**
     * 编辑仓库信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/StoreHouseController/editStoreHouseById")
    @ResponseBody
    public void editStoreHouseById(InputObject inputObject, OutputObject outputObject) throws Exception{
        storeHouseService.editStoreHouseById(inputObject, outputObject);
    }

    /**
     * 设置仓库为默认状态
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/StoreHouseController/editStoreHouseByDefault")
    @ResponseBody
    public void editStoreHouseByDefault(InputObject inputObject, OutputObject outputObject) throws Exception{
        storeHouseService.editStoreHouseByDefault(inputObject, outputObject);
    }
    
    /**
     * 获取所有仓库展示为下拉框
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/StoreHouseController/queyrStoreHouseListToSelect")
    @ResponseBody
    public void queyrStoreHouseListToSelect(InputObject inputObject, OutputObject outputObject) throws Exception{
        storeHouseService.queyrStoreHouseListToSelect(inputObject, outputObject);
    }
    
    /**
     * 查看仓库详情
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/StoreHouseController/queryStoreHouseByIdAndInfo")
    @ResponseBody
    public void queryStoreHouseByIdAndInfo(InputObject inputObject, OutputObject outputObject) throws Exception{
        storeHouseService.queryStoreHouseByIdAndInfo(inputObject, outputObject);
    }
    
    /**
     * 获取当前登录用户管理的仓库列表展示为下拉框
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/StoreHouseController/queryStoreHouseListByCurrentUserId")
    @ResponseBody
    public void queryStoreHouseListByCurrentUserId(InputObject inputObject, OutputObject outputObject) throws Exception{
        storeHouseService.queryStoreHouseListByCurrentUserId(inputObject, outputObject);
    }
    
}
