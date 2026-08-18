/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.score.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.score.service.AutoScoreRecordService;
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

    @ApiOperation(id = "queryMyAutoScore", value = "按版本查询当前用户已获得/预计积分", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "objectId", name = "objectId", value = "项目id", required = "required"),
        @ApiImplicitParam(id = "objectKey", name = "objectKey", value = "项目key"),
        @ApiImplicitParam(id = "versionId", name = "versionId", value = "版本id", required = "required")})
    @RequestMapping("/post/AutoScoreRecordController/queryMyAutoScore")
    public void queryMyAutoScore(InputObject inputObject, OutputObject outputObject) {
        autoScoreRecordService.queryMyAutoScore(inputObject, outputObject);
    }

    @ApiOperation(id = "settleAutoScoreByVersion", value = "项目经理按版本结算积分", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "objectId", name = "objectId", value = "项目id", required = "required"),
        @ApiImplicitParam(id = "objectKey", name = "objectKey", value = "项目key"),
        @ApiImplicitParam(id = "versionId", name = "versionId", value = "版本id", required = "required")})
    @RequestMapping("/post/AutoScoreRecordController/settleAutoScoreByVersion")
    public void settleAutoScoreByVersion(InputObject inputObject, OutputObject outputObject) {
        autoScoreRecordService.settleAutoScoreByVersion(inputObject, outputObject);
    }

}
