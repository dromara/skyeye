package com.skyeye.eve.assets.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.assets.service.AssetDepreciationRunService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "固定资产月结折旧", tags = "固定资产月结折旧", modelName = "资产模块")
public class AssetDepreciationRunController {

    @Autowired
    private AssetDepreciationRunService assetDepreciationRunService;

    @ApiOperation(id = "queryAssetDeprecRunList", value = "月结折旧批次列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/AssetDepreciationRunController/queryAssetDeprecRunList")
    public void queryAssetDeprecRunList(InputObject inputObject, OutputObject outputObject) {
        assetDepreciationRunService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAssetDeprecRunById", value = "月结折旧批次详情", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "批次id", required = "required")})
    @RequestMapping("/post/AssetDepreciationRunController/queryAssetDeprecRunById")
    public void queryAssetDeprecRunById(InputObject inputObject, OutputObject outputObject) {
        assetDepreciationRunService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "previewAssetDeprecMonthly", value = "预览本期可计提折旧", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "period", name = "period", value = "折旧期间YYYY-MM，空则当月")})
    @RequestMapping("/post/AssetDepreciationRunController/previewAssetDeprecMonthly")
    public void previewAssetDeprecMonthly(InputObject inputObject, OutputObject outputObject) {
        assetDepreciationRunService.previewMonthlyDeprec(inputObject, outputObject);
    }

    @ApiOperation(id = "runAssetDeprecMonthly", value = "执行月结批量折旧", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "period", name = "period", value = "折旧期间YYYY-MM，空则当月"),
        @ApiImplicitParam(id = "setOfBooksId", name = "setOfBooksId", value = "账套id", required = "required"),
        @ApiImplicitParam(id = "remark", name = "remark", value = "备注")})
    @RequestMapping("/post/AssetDepreciationRunController/runAssetDeprecMonthly")
    public void runAssetDeprecMonthly(InputObject inputObject, OutputObject outputObject) {
        assetDepreciationRunService.runMonthlyDeprec(inputObject, outputObject);
    }
}
