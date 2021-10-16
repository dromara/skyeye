/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.CheckWorkService;

@Controller
public class CheckWorkController {

	@Autowired
	private CheckWorkService checkWorkService;
	
	/**
	 * 
	     * @Title: insertCheckWorkStartWork
	     * @Description: 上班打卡
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CheckWorkController/insertCheckWorkStartWork")
	@ResponseBody
	public void insertCheckWorkStartWork(InputObject inputObject, OutputObject outputObject) throws Exception{
		checkWorkService.insertCheckWorkStartWork(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editCheckWorkEndWork
	     * @Description: 下班打卡
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CheckWorkController/editCheckWorkEndWork")
	@ResponseBody
	public void editCheckWorkEndWork(InputObject inputObject, OutputObject outputObject) throws Exception{
		checkWorkService.editCheckWorkEndWork(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryCheckWork
	     * @Description: 我的考勤
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CheckWorkController/queryCheckWork")
	@ResponseBody
	public void queryCheckWork(InputObject inputObject, OutputObject outputObject) throws Exception{
		checkWorkService.queryCheckWork(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryCheckWorkIdByAppealType
	     * @Description: 申诉内容
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CheckWorkController/queryCheckWorkIdByAppealType")
	@ResponseBody
	public void queryCheckWorkIdByAppealType(InputObject inputObject, OutputObject outputObject) throws Exception{
		checkWorkService.queryCheckWorkIdByAppealType(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySysEveUserStaff
	     * @Description: 审批人列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CheckWorkController/querySysEveUserStaff")
	@ResponseBody
	public void querySysEveUserStaff(InputObject inputObject, OutputObject outputObject) throws Exception{
		checkWorkService.querySysEveUserStaff(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertCheckWorkAppeal
	     * @Description: 新增申诉
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CheckWorkController/insertCheckWorkAppeal")
	@ResponseBody
	public void insertCheckWorkAppeal(InputObject inputObject, OutputObject outputObject) throws Exception{
		checkWorkService.insertCheckWorkAppeal(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryCheckWorkAppeallist
	     * @Description: 我的申诉申请列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CheckWorkController/queryCheckWorkAppeallist")
	@ResponseBody
	public void queryCheckWorkAppeallist(InputObject inputObject, OutputObject outputObject) throws Exception{
		checkWorkService.queryCheckWorkAppeallist(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryCheckWorkAppealMyGetlist
	     * @Description: 员工考勤申诉审批
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CheckWorkController/queryCheckWorkAppealMyGetlist")
	@ResponseBody
	public void queryCheckWorkAppealMyGetlist(InputObject inputObject, OutputObject outputObject) throws Exception{
		checkWorkService.queryCheckWorkAppealMyGetlist(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryStaffCheckWorkDetails
	     * @Description: 回显员工的申诉申请详情
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CheckWorkController/queryStaffCheckWorkDetails")
	@ResponseBody
	public void queryStaffCheckWorkDetails(InputObject inputObject, OutputObject outputObject) throws Exception{
		checkWorkService.queryStaffCheckWorkDetails(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editStaffCheckWork
	     * @Description: 审批员工的申诉申请
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CheckWorkController/editStaffCheckWork")
	@ResponseBody
	public void editStaffCheckWork(InputObject inputObject, OutputObject outputObject) throws Exception{
		checkWorkService.editStaffCheckWork(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryCheckWorkDetails
	     * @Description: 申诉申请详情
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CheckWorkController/queryCheckWorkDetails")
	@ResponseBody
	public void queryCheckWorkDetails(InputObject inputObject, OutputObject outputObject) throws Exception{
		checkWorkService.queryCheckWorkDetails(inputObject, outputObject);
	}
	
	/**
     * 
         * @Title: queryCheckWorkTimeToShowButton
         * @Description: 判断显示打上班卡或者下班卡
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @RequestMapping("/post/CheckWorkController/queryCheckWorkTimeToShowButton")
    @ResponseBody
    public void queryCheckWorkTimeToShowButton(InputObject inputObject, OutputObject outputObject) throws Exception{
        checkWorkService.queryCheckWorkTimeToShowButton(inputObject, outputObject);
    }
	
    /**
     * 
         * @Title: queryCheckWorkMationByMonth
         * @Description: 根据月份查询当月的考勤信息
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @RequestMapping("/post/CheckWorkController/queryCheckWorkMationByMonth")
    @ResponseBody
    public void queryCheckWorkMationByMonth(InputObject inputObject, OutputObject outputObject) throws Exception{
        checkWorkService.queryCheckWorkMationByMonth(inputObject, outputObject);
    }
    
    /**
     * 
         * @Title: queryCheckWorkReport
         * @Description: 获取考勤报表数据
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @RequestMapping("/post/CheckWorkController/queryCheckWorkReport")
    @ResponseBody
    public void queryCheckWorkReport(InputObject inputObject, OutputObject outputObject) throws Exception{
        checkWorkService.queryCheckWorkReport(inputObject, outputObject);
    }
	
    /**
     * 
         * @Title: queryCheckWorkEcharts
         * @Description: 获取考勤图标数据
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @RequestMapping("/post/CheckWorkController/queryCheckWorkEcharts")
    @ResponseBody
    public void queryCheckWorkEcharts(InputObject inputObject, OutputObject outputObject) throws Exception{
        checkWorkService.queryCheckWorkEcharts(inputObject, outputObject);
    }
    
    /**
     * 
         * @Title: downloadCheckWorkTemplate
         * @Description: 个人考勤情况导出
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @RequestMapping("/post/CheckWorkController/downloadCheckWorkTemplate")
    @ResponseBody
    public void downloadCheckWorkTemplate(InputObject inputObject, OutputObject outputObject) throws Exception{
        checkWorkService.downloadCheckWorkTemplate(inputObject, outputObject);
    }
    
    /**
     * 
         * @Title: queryReportDetail
         * @Description: 获取表格数据详情信息
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @RequestMapping("/post/CheckWorkController/queryReportDetail")
    @ResponseBody
    public void queryReportDetail(InputObject inputObject, OutputObject outputObject) throws Exception{
        checkWorkService.queryReportDetail(inputObject, outputObject);
    }
	
    /**
     * 
         * @Title: queryEchartsDetail
         * @Description: 获取图表数据详情信息
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @RequestMapping("/post/CheckWorkController/queryEchartsDetail")
    @ResponseBody
    public void queryEchartsDetail(InputObject inputObject, OutputObject outputObject) throws Exception{
        checkWorkService.queryEchartsDetail(inputObject, outputObject);
    }
    
    /**
     * 
         * @Title: queryReportDownload
         * @Description: 考勤报表导出
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @RequestMapping("/post/CheckWorkController/queryReportDownload")
    @ResponseBody
    public void queryReportDownload(InputObject inputObject, OutputObject outputObject) throws Exception{
        checkWorkService.queryReportDownload(inputObject, outputObject);
    }
    
}
