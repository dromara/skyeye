/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.store.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.entity.intercourse.StoreIntercourseQueryDo;
import com.skyeye.store.service.StoreIntercourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: StoreIntercourseController
 * @Description: 门店往来管理
 * @author: skyeye云系列--卫志强
 * @date: 2022/3/10 21:53
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "门店往来管理", tags = "门店往来管理", modelName = "商城模块")
public class StoreIntercourseController {

    @Autowired
    private StoreIntercourseService storeIntercourseService;

    @ApiOperation(id = "queryStoreIntercourseList", value = "获取指定门店的支出/收入往来的数据", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = StoreIntercourseQueryDo.class)
    @RequestMapping("/post/StoreIntercourseController/queryStoreIntercourseList")
    public void queryStoreIntercourseList(InputObject inputObject, OutputObject outputObject) {
        storeIntercourseService.queryStoreIntercourseList(inputObject, outputObject);
    }

    @ApiOperation(id = "editStoreIntercourseState", value = "编辑指定门店的支出/收入往来的状态", method = "PUT", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "往来数据id", required = "required"),
        @ApiImplicitParam(id = "state", name = "state", value = "状态  1.待套餐购买门店确认  2.待保养门店确认  3.已确认", required = "required,num")})
    @RequestMapping("/post/StoreIntercourseController/editStoreIntercourseState")
    public void editStoreIntercourseState(InputObject inputObject, OutputObject outputObject) {
        storeIntercourseService.editStoreIntercourseState(inputObject, outputObject);
    }

}
