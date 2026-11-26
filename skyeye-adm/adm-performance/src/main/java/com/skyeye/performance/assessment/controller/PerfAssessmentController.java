package com.skyeye.performance.assessment.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.features.SubmitSkyeyeFlowable;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.performance.assessment.entity.PerfAssessment;
import com.skyeye.performance.assessment.service.PerfAssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "员工绩效考核单", tags = "员工绩效考核单", modelName = "员工绩效")
public class PerfAssessmentController {

    @Autowired
    private PerfAssessmentService perfAssessmentService;

    @ApiOperation(id = "queryPerfAssessmentList", value = "查询绩效考核单列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/PerfAssessmentController/queryPerfAssessmentList")
    public void queryPerfAssessmentList(InputObject inputObject, OutputObject outputObject) {
        perfAssessmentService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writePerfAssessment", value = "新增/编辑绩效考核单", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = PerfAssessment.class)
    @RequestMapping("/post/PerfAssessmentController/writePerfAssessment")
    public void writePerfAssessment(InputObject inputObject, OutputObject outputObject) {
        perfAssessmentService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryPerfAssessmentById", value = "根据id查询绩效考核单", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PerfAssessmentController/queryPerfAssessmentById")
    public void queryPerfAssessmentById(InputObject inputObject, OutputObject outputObject) {
        perfAssessmentService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "deletePerfAssessmentById", value = "删除绩效考核单", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PerfAssessmentController/deletePerfAssessmentById")
    public void deletePerfAssessmentById(InputObject inputObject, OutputObject outputObject) {
        perfAssessmentService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "submitToApprovalPerfAssessment", value = "绩效考核单提交审批", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/PerfAssessmentController/submitToApproval")
    public void submitToApproval(InputObject inputObject, OutputObject outputObject) {
        perfAssessmentService.submitToApproval(inputObject, outputObject);
    }

    @ApiOperation(id = "revokePerfAssessment", value = "撤销绩效考核单审批", method = "PUT", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/PerfAssessmentController/revoke")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        perfAssessmentService.revoke(inputObject, outputObject);
    }

    @ApiOperation(id = "invalidPerfAssessment", value = "作废绩效考核单", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PerfAssessmentController/invalid")
    public void invalid(InputObject inputObject, OutputObject outputObject) {
        perfAssessmentService.invalid(inputObject, outputObject);
    }

    @ApiOperation(id = "queryPerformanceDashboard", value = "绩效看板数据", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "cycleId", name = "cycleId", value = "周期id")})
    @RequestMapping("/post/PerfAssessmentController/queryPerformanceDashboard")
    public void queryPerformanceDashboard(InputObject inputObject, OutputObject outputObject) {
        perfAssessmentService.queryPerformanceDashboard(inputObject, outputObject);
    }

    @ApiOperation(id = "fillPerfAssessmentAutoScore", value = "按指标来源自动取分", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "考核单id", required = "required")})
    @RequestMapping("/post/PerfAssessmentController/fillAutoScores")
    public void fillAutoScores(InputObject inputObject, OutputObject outputObject) {
        perfAssessmentService.fillAutoScores(inputObject, outputObject);
    }
}
