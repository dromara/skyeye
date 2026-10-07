/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.retail.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.depot.entity.DepotOut;
import com.skyeye.retail.entity.RetailOutLet;
import com.skyeye.retail.service.RetailOutLetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: RetailOutLetController
 * @Description: 零售出库单控制类
 * @author: skyeye云系列--卫志强
 * @date: 2019/10/16 15:32
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "零售出库单", tags = "零售出库单", modelName = "零售模块")
public class RetailOutLetController {

    @Autowired
    private RetailOutLetService retailOutLetService;

    @ApiOperation(id = "retailoutlet001", value = "获取零售出库单列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/RetailOutLetController/queryRetailOutLetList")
    public void queryRetailOutLetList(InputObject inputObject, OutputObject outputObject) {
        retailOutLetService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeRetailOutLet", value = "新增/编辑零售出库单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = RetailOutLet.class)
    @RequestMapping("/post/RetailOutLetController/writeRetailOutLet")
    public void writeRetailOutLet(InputObject inputObject, OutputObject outputObject) {
        retailOutLetService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryRetailOutLetTransById", value = "转仓库出库单时，根据id查询零售出库信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/RetailOutLetController/queryRetailOutLetTransById")
    public void queryRetailOutLetTransById(InputObject inputObject, OutputObject outputObject) {
        retailOutLetService.queryRetailOutLetTransById(inputObject, outputObject);
    }

    @ApiOperation(id = "insertRetailOutLetToTurnDepot", value = "零售出库单信息转仓库出库单", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = DepotOut.class, value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/RetailOutLetController/insertRetailOutLetToTurnDepot")
    public void insertRetailOutLetToTurnDepot(InputObject inputObject, OutputObject outputObject) {
        retailOutLetService.insertRetailOutLetToTurnDepot(inputObject, outputObject);
    }

}
