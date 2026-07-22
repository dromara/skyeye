/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.doc.gitcode.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.doc.gitcode.entity.GitCodeIssue;
import com.skyeye.doc.gitcode.service.GitCodeIssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: GitCodeIssueController
 * @Description: GitCode Issue管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2025/1/1 12:00
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@RestController
@Api(value = "GitCode Issue管理", tags = "GitCode Issue管理", modelName = "GitCode Issue管理")
public class GitCodeIssueController {

    @Autowired
    private GitCodeIssueService gitCodeIssueService;

    @ApiOperation(id = "writeIssue", value = "创建/编辑Issue", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = GitCodeIssue.class)
    @RequestMapping("/post/GitCodeIssueController/writeIssue")
    public void writeIssue(InputObject inputObject, OutputObject outputObject) {
        gitCodeIssueService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteIssueById", value = "删除Issue", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "Issue ID", required = "required")})
    @RequestMapping("/post/GitCodeIssueController/deleteIssueById")
    public void deleteIssueById(InputObject inputObject, OutputObject outputObject) {
        gitCodeIssueService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "updateIssueState", value = "更新Issue状态", method = "PUT", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "Issue ID", required = "required"),
        @ApiImplicitParam(id = "state", name = "state", value = "状态", required = "required")})
    @RequestMapping("/post/GitCodeIssueController/updateIssueState")
    public void updateIssueState(InputObject inputObject, OutputObject outputObject) {
        gitCodeIssueService.updateIssueState(inputObject, outputObject);
    }

    @ApiOperation(id = "updateIssueRecordBug", value = "更新Issue是否记录Bug", method = "PUT", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "Issue ID", required = "required")})
    @RequestMapping("/post/GitCodeIssueController/updateIssueRecordBug")
    public void updateIssueRecordBug(InputObject inputObject, OutputObject outputObject) {
        gitCodeIssueService.updateIssueRecordBug(inputObject, outputObject);
    }

    @ApiOperation(id = "updateIssueRecordRequirement", value = "更新Issue是否记录需求", method = "PUT", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "Issue ID", required = "required")})
    @RequestMapping("/post/GitCodeIssueController/updateIssueRecordRequirement")
    public void updateIssueRecordRequirement(InputObject inputObject, OutputObject outputObject) {
        gitCodeIssueService.updateIssueRecordRequirement(inputObject, outputObject);
    }

    @ApiOperation(id = "updateIssueBugCompleted", value = "更新Issue的Bug是否完成", method = "PUT", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "Issue ID", required = "required")})
    @RequestMapping("/post/GitCodeIssueController/updateIssueBugCompleted")
    public void updateIssueBugCompleted(InputObject inputObject, OutputObject outputObject) {
        gitCodeIssueService.updateIssueBugCompleted(inputObject, outputObject);
    }

    @ApiOperation(id = "updateIssueRequirementCompleted", value = "更新Issue的需求是否完成", method = "PUT", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "Issue ID", required = "required")})
    @RequestMapping("/post/GitCodeIssueController/updateIssueRequirementCompleted")
    public void updateIssueRequirementCompleted(InputObject inputObject, OutputObject outputObject) {
        gitCodeIssueService.updateIssueRequirementCompleted(inputObject, outputObject);
    }

    @ApiOperation(id = "queryIssueList", value = "获取Issue列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/GitCodeIssueController/queryIssueList")
    public void queryIssueList(InputObject inputObject, OutputObject outputObject) {
        gitCodeIssueService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryIssueListForAdmin", value = "后台获取Issue列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/GitCodeIssueController/queryIssueListForAdmin")
    public void queryIssueListForAdmin(InputObject inputObject, OutputObject outputObject) {
        gitCodeIssueService.queryIssueListForAdmin(inputObject, outputObject);
    }

    @ApiOperation(id = "queryIssueById", value = "根据ID获取Issue详情", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "Issue ID", required = "required")})
    @RequestMapping("/post/GitCodeIssueController/queryIssueById")
    public void queryIssueById(InputObject inputObject, OutputObject outputObject) {
        gitCodeIssueService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "insertUploadImageToIssue", value = "上传图片到Issue", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "images", name = "images", value = "图片Base64", required = "required"),
        @ApiImplicitParam(id = "fileName", name = "fileName", value = "图片名称", required = "required")})
    @RequestMapping("/post/GitCodeIssueController/insertUploadImageToIssue")
    public void insertUploadImageToIssue(InputObject inputObject, OutputObject outputObject) {
        gitCodeIssueService.insertUploadImageToIssue(inputObject, outputObject);
    }

}
