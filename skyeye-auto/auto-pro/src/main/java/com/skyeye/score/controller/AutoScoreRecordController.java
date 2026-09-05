/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.score.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.score.service.AutoScoreRecordService;
import com.skyeye.score.service.AutoScoreSettleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: AutoScoreRecordController
 * @Description: 需求积分控制层
 * @author: skyeye云系列--卫志强
 * @date: 2026/8/18
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "需求积分", tags = "需求积分", modelName = "需求积分")
public class AutoScoreRecordController {

    @Autowired
    private AutoScoreRecordService autoScoreRecordService;

    @Autowired
    private AutoScoreSettleService autoScoreSettleService;

    @ApiOperation(id = "queryMyAutoScore", value = "查询当前用户已获得/预计积分", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "objectId", name = "objectId", value = "项目id", required = "required"),
        @ApiImplicitParam(id = "objectKey", name = "objectKey", value = "项目key"),
        @ApiImplicitParam(id = "versionId", name = "versionId", value = "版本id")})
    @RequestMapping("/post/AutoScoreRecordController/queryMyAutoScore")
    public void queryMyAutoScore(InputObject inputObject, OutputObject outputObject) {
        autoScoreRecordService.queryMyAutoScore(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAutoScoreBoard", value = "查询项目成员总积分", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "objectId", name = "objectId", value = "项目id", required = "required"),
        @ApiImplicitParam(id = "objectKey", name = "objectKey", value = "项目key")})
    @RequestMapping("/post/AutoScoreRecordController/queryAutoScoreBoard")
    public void queryAutoScoreBoard(InputObject inputObject, OutputObject outputObject) {
        autoScoreRecordService.queryAutoScoreBoard(inputObject, outputObject);
    }

    @ApiOperation(id = "settleAutoScore", value = "项目经理结算积分", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "objectId", name = "objectId", value = "项目id", required = "required"),
        @ApiImplicitParam(id = "objectKey", name = "objectKey", value = "项目key"),
        @ApiImplicitParam(id = "remark", name = "remark", value = "备注"),
        @ApiImplicitParam(id = "userList", name = "userList", value = "结算列表，格式：[{'userId':'用户id','score':'本次结算积分'}]", required = "required,json")})
    @RequestMapping("/post/AutoScoreRecordController/settleAutoScore")
    public void settleAutoScore(InputObject inputObject, OutputObject outputObject) {
        autoScoreRecordService.settleAutoScore(inputObject, outputObject);
    }

    @ApiOperation(id = "writeExtraAutoScore", value = "项目经理额外加分/扣分", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "objectId", name = "objectId", value = "项目id", required = "required"),
        @ApiImplicitParam(id = "objectKey", name = "objectKey", value = "项目key"),
        @ApiImplicitParam(id = "userId", name = "userId", value = "成员id", required = "required"),
        @ApiImplicitParam(id = "scoreType", name = "scoreType", value = "类型：extraGrant 加分 / extraDeduct 扣分", required = "required"),
        @ApiImplicitParam(id = "score", name = "score", value = "积分（正数）", required = "required"),
        @ApiImplicitParam(id = "versionId", name = "versionId", value = "版本id"),
        @ApiImplicitParam(id = "demandId", name = "demandId", value = "需求id"),
        @ApiImplicitParam(id = "remark", name = "remark", value = "备注")})
    @RequestMapping("/post/AutoScoreRecordController/writeExtraAutoScore")
    public void writeExtraAutoScore(InputObject inputObject, OutputObject outputObject) {
        autoScoreRecordService.writeExtraAutoScore(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAutoScoreSettleList", value = "查询积分结算记录", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/AutoScoreRecordController/queryAutoScoreSettleList")
    public void queryAutoScoreSettleList(InputObject inputObject, OutputObject outputObject) {
        autoScoreSettleService.queryPageList(inputObject, outputObject);
    }

}
