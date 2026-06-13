/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.activity.controller;

import com.skyeye.activity.entity.ChooseActivity;
import com.skyeye.activity.service.ChooseActivityService;
import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ActivityController
 * @Description: 活动管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/8 10:21
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "选题活动管理", tags = "选题活动管理", modelName = "选题活动管理")
public class ChooseActivityController {

    @Autowired
    private ChooseActivityService chooseActivityService;

    @ApiOperation(id = "writeChooseActivity", value = "新增/编辑选题活动信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = ChooseActivity.class)
    @RequestMapping("/post/ActivityController/writeChooseActivity")
    public void writeChooseActivity(InputObject inputObject, OutputObject outputObject) {
        chooseActivityService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteActivityById", value = "根据id删除选题活动信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "课题id", required = "required")})
    @RequestMapping("/post/ActivityController/deleteActivityById")
    public void deleteActivityById(InputObject inputObject, OutputObject outputObject) {
        chooseActivityService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryActivityById", value = "根据id查询选题活动信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "课题id", required = "required")})
    @RequestMapping("/post/ActivityController/queryActivityById")
    public void queryActivityById(InputObject inputObject, OutputObject outputObject) {
        chooseActivityService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryActivityList", value = "获取选题活动信息列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ActivityController/queryActivityList")
    public void queryActivityList(InputObject inputObject, OutputObject outputObject) {
        chooseActivityService.queryActivityList(inputObject, outputObject);
    }

    @ApiOperation(id = "updateActivityFeatureEnable", value = "更新活动选题/导师功能开关", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "活动id", required = "required"),
        @ApiImplicitParam(id = "topicEnable", name = "topicEnable", value = "是否开启选题", enumClass = WhetherEnum.class),
        @ApiImplicitParam(id = "teacherEnable", name = "teacherEnable", value = "是否开启导师选择", enumClass = WhetherEnum.class)
    })
    @RequestMapping("/post/ActivityController/updateActivityFeatureEnable")
    public void updateActivityFeatureEnable(InputObject inputObject, OutputObject outputObject) {
        chooseActivityService.updateActivityFeatureEnable(inputObject, outputObject);
    }
}
