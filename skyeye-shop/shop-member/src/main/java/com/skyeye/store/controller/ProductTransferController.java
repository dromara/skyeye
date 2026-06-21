/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.store.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.features.SubmitSkyeyeFlowable;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.store.entity.ProductTransfer;
import com.skyeye.store.service.ProductTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ProductTransferController
 * @Description: 门店产品调拨申请控制层（选品/规格/查库存复用 ERP 已有接口，与备件申领一致）
 * @author: skyeye云系列--卫志强
 * @date: 2025/01/XX XX:XX
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "门店产品调拨", tags = "门店产品调拨", modelName = "门店产品调拨")
public class ProductTransferController {

    @Autowired
    private ProductTransferService productTransferService;

    @ApiOperation(id = "queryProductTransferList", value = "获取我的门店产品调拨申请列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ProductTransferController/queryProductTransferList")
    public void queryProductTransferList(InputObject inputObject, OutputObject outputObject) {
        productTransferService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeProductTransfer", value = "新增/编辑门店产品调拨申请", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = ProductTransfer.class)
    @RequestMapping("/post/ProductTransferController/writeProductTransfer")
    public void writeProductTransfer(InputObject inputObject, OutputObject outputObject) {
        productTransferService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryProductTransferById", value = "根据id查询门店产品调拨申请信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ProductTransferController/queryProductTransferById")
    public void queryProductTransferById(InputObject inputObject, OutputObject outputObject) {
        productTransferService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteProductTransferById", value = "删除门店产品调拨申请", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ProductTransferController/deleteProductTransferById")
    public void deleteProductTransferById(InputObject inputObject, OutputObject outputObject) {
        productTransferService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "submitProductTransferToApproval", value = "门店产品调拨申请提交审批", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/ProductTransferController/submitToApproval")
    public void submitToApproval(InputObject inputObject, OutputObject outputObject) {
        productTransferService.submitToApproval(inputObject, outputObject);
    }

    @ApiOperation(id = "revokeProductTransfer", value = "撤销门店产品调拨申请", method = "PUT", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/ProductTransferController/revoke")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        productTransferService.revoke(inputObject, outputObject);
    }

}
