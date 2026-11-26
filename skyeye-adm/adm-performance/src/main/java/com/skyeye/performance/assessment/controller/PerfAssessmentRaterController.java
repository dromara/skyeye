package com.skyeye.performance.assessment.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.performance.assessment.entity.PerfAssessmentRater;
import com.skyeye.performance.assessment.service.PerfAssessmentRaterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "360评价人", tags = "360评价人", modelName = "员工绩效")
public class PerfAssessmentRaterController {

    @Autowired
    private PerfAssessmentRaterService perfAssessmentRaterService;

    @ApiOperation(id = "queryPerfAssessmentRaters", value = "查询考核单评价人", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "assessmentId", name = "assessmentId", value = "考核单id", required = "required")})
    @RequestMapping("/post/PerfAssessmentRaterController/queryRaters")
    public void queryRaters(InputObject inputObject, OutputObject outputObject) {
        perfAssessmentRaterService.queryRaters(inputObject, outputObject);
    }

    @ApiOperation(id = "writePerfAssessmentRater", value = "新增/编辑评价人", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = PerfAssessmentRater.class)
    @RequestMapping("/post/PerfAssessmentRaterController/writeRater")
    public void writeRater(InputObject inputObject, OutputObject outputObject) {
        perfAssessmentRaterService.saveRater(inputObject, outputObject);
    }

    @ApiOperation(id = "deletePerfAssessmentRater", value = "删除评价人", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "评价人记录id", required = "required")})
    @RequestMapping("/post/PerfAssessmentRaterController/deleteRater")
    public void deleteRater(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        perfAssessmentRaterService.deleteRaterById(id);
        outputObject.setreturnMessage("success");
    }

    @ApiOperation(id = "savePerfAssessmentRaterScores", value = "保存评价人打分", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "assessmentId", name = "assessmentId", value = "考核单id", required = "required"),
        @ApiImplicitParam(id = "raterId", name = "raterId", value = "评价人记录id", required = "required"),
        @ApiImplicitParam(id = "scoreList", name = "scoreList", value = "打分明细JSON", required = "required,json")})
    @RequestMapping("/post/PerfAssessmentRaterController/saveRaterScores")
    public void saveRaterScores(InputObject inputObject, OutputObject outputObject) {
        perfAssessmentRaterService.saveRaterScores(inputObject, outputObject);
    }

    @ApiOperation(id = "aggregatePerfAssessmentRaterScores", value = "汇总360评价到考核明细", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "考核单id", required = "required")})
    @RequestMapping("/post/PerfAssessmentRaterController/aggregateRaterScores")
    public void aggregateRaterScores(InputObject inputObject, OutputObject outputObject) {
        perfAssessmentRaterService.aggregateRaterScores(inputObject, outputObject);
    }
}
