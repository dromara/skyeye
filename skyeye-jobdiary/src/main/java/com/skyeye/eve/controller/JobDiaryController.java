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
import com.skyeye.eve.service.JobDiaryService;

@Controller
public class JobDiaryController {
	
	@Autowired
	private JobDiaryService jobDiaryService;
	
	/**
	 * 
	     * @Title: queryJobDiaryDayReceived
	     * @Description: 遍历我收到的日志
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/JobDiaryController/queryJobDiaryDayReceived")
	@ResponseBody
	public void queryJobDiaryDayReceived(InputObject inputObject, OutputObject outputObject) throws Exception{
		jobDiaryService.queryJobDiaryDayReceived(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertDayJobDiary
	     * @Description: 发表日志
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/JobDiaryController/insertDayJobDiary")
	@ResponseBody
	public void insertDayJobDiary(InputObject inputObject, OutputObject outputObject) throws Exception{
		jobDiaryService.insertDayJobDiary(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySysEveUserStaff
	     * @Description: 查出所有有账户的员工
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/JobDiaryController/querySysEveUserStaff")
	@ResponseBody
	public void querySysEveUserStaff(InputObject inputObject, OutputObject outputObject) throws Exception{
		jobDiaryService.querySysEveUserStaff(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryJobDiaryDetails
	     * @Description: 日报详情
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/JobDiaryController/queryJobDiaryDetails")
	@ResponseBody
	public void queryJobDiaryDetails(InputObject inputObject, OutputObject outputObject) throws Exception{
		jobDiaryService.queryJobDiaryDetails(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryJobDiaryDayMysend
	     * @Description: 遍历我发出的所有日志
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/JobDiaryController/queryJobDiaryDayMysend")
	@ResponseBody
	public void queryJobDiaryDayMysend(InputObject inputObject, OutputObject outputObject) throws Exception{
		jobDiaryService.queryJobDiaryDayMysend(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteJobDiaryDayMysend
	     * @Description: 撤销我发出的日志
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/JobDiaryController/deleteJobDiaryDayMysend")
	@ResponseBody
	public void deleteJobDiaryDayMysend(InputObject inputObject, OutputObject outputObject) throws Exception{
		jobDiaryService.deleteJobDiaryDayMysend(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: selectMysendDetails
	     * @Description: 阅读我发出的日报详情
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/JobDiaryController/selectMysendDetails")
	@ResponseBody
	public void selectMysendDetails(InputObject inputObject, OutputObject outputObject) throws Exception{
		jobDiaryService.selectMysendDetails(inputObject, outputObject);
	}

	/**
	 * 
	     * @Title: editMyReceivedJobDiary
	     * @Description: 删除我收到的日志
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/JobDiaryController/editMyReceivedJobDiary")
	@ResponseBody
	public void editMyReceivedJobDiary(InputObject inputObject, OutputObject outputObject) throws Exception{
		jobDiaryService.editMyReceivedJobDiary(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertWeekJobDiary
	     * @Description: 发表周报
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/JobDiaryController/insertWeekJobDiary")
	@ResponseBody
	public void insertWeekJobDiary(InputObject inputObject, OutputObject outputObject) throws Exception{
		jobDiaryService.insertWeekJobDiary(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: selectMysendWeekDetails
	     * @Description: 阅读我发出的周报详情
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/JobDiaryController/selectMysendWeekDetails")
	@ResponseBody
	public void selectMysendWeekDetails(InputObject inputObject, OutputObject outputObject) throws Exception{
		jobDiaryService.selectMysendWeekDetails(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryWeekJobDiaryDetails
	     * @Description: 阅读我收到的周报
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/JobDiaryController/queryWeekJobDiaryDetails")
	@ResponseBody
	public void queryWeekJobDiaryDetails(InputObject inputObject, OutputObject outputObject) throws Exception{
		jobDiaryService.queryWeekJobDiaryDetails(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertMonthJobDiary
	     * @Description: 发表月报
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/JobDiaryController/insertMonthJobDiary")
	@ResponseBody
	public void insertMonthJobDiary(InputObject inputObject, OutputObject outputObject) throws Exception{
		jobDiaryService.insertMonthJobDiary(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: selectMysendMonthDetails
	     * @Description: 阅读我发出的月报详情
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/JobDiaryController/selectMysendMonthDetails")
	@ResponseBody
	public void selectMysendMonthDetails(InputObject inputObject, OutputObject outputObject) throws Exception{
		jobDiaryService.selectMysendMonthDetails(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryMonthJobDiaryDetails
	     * @Description: 阅读我收到的月报
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/JobDiaryController/queryMonthJobDiaryDetails")
	@ResponseBody
	public void queryMonthJobDiaryDetails(InputObject inputObject, OutputObject outputObject) throws Exception{
		jobDiaryService.queryMonthJobDiaryDetails(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editJobDiaryDayMysend
	     * @Description: 删除我发出的日志
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/JobDiaryController/editJobDiaryDayMysend")
	@ResponseBody
	public void editJobDiaryDayMysend(InputObject inputObject, OutputObject outputObject) throws Exception{
		jobDiaryService.editJobDiaryDayMysend(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryJobDiaryDayMysendToEdit
	     * @Description: 回显我撤回的日报
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/JobDiaryController/queryJobDiaryDayMysendToEdit")
	@ResponseBody
	public void queryJobDiaryDayMysendToEdit(InputObject inputObject, OutputObject outputObject) throws Exception{
		jobDiaryService.queryJobDiaryDayMysendToEdit(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editDayJobDiary
	     * @Description: 提交撤回的日报
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/JobDiaryController/editDayJobDiary")
	@ResponseBody
	public void editDayJobDiary(InputObject inputObject, OutputObject outputObject) throws Exception{
		jobDiaryService.editDayJobDiary(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryWeekJobDiaryDayMysendToEdit
	     * @Description: 回显我撤回的周报
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/JobDiaryController/queryWeekJobDiaryDayMysendToEdit")
	@ResponseBody
	public void queryWeekJobDiaryDayMysendToEdit(InputObject inputObject, OutputObject outputObject) throws Exception{
		jobDiaryService.queryWeekJobDiaryDayMysendToEdit(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editWeekDayJobDiary
	     * @Description: 提交撤回的周报
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/JobDiaryController/editWeekDayJobDiary")
	@ResponseBody
	public void editWeekDayJobDiary(InputObject inputObject, OutputObject outputObject) throws Exception{
		jobDiaryService.editWeekDayJobDiary(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryMonthJobDiaryDayMysendToEdit
	     * @Description: 回显我撤回的月报
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/JobDiaryController/queryMonthJobDiaryDayMysendToEdit")
	@ResponseBody
	public void queryMonthJobDiaryDayMysendToEdit(InputObject inputObject, OutputObject outputObject) throws Exception{
		jobDiaryService.queryMonthJobDiaryDayMysendToEdit(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editMonthDayJobDiary
	     * @Description: 提交撤回的月报
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/JobDiaryController/editMonthDayJobDiary")
	@ResponseBody
	public void editMonthDayJobDiary(InputObject inputObject, OutputObject outputObject) throws Exception{
		jobDiaryService.editMonthDayJobDiary(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryJobDiaryDayNumber
	     * @Description: 查询日志类型各个类型的条数
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/JobDiaryController/queryJobDiaryDayNumber")
	@ResponseBody
	public void queryJobDiaryDayNumber(InputObject inputObject, OutputObject outputObject) throws Exception{
		jobDiaryService.queryJobDiaryDayNumber(inputObject, outputObject);
	}
	
	/**
     * 
         * @Title: queryJobDiaryListToTimeTree
         * @Description: 获取日志列表展示位时间树
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @RequestMapping("/post/JobDiaryController/queryJobDiaryListToTimeTree")
    @ResponseBody
    public void queryJobDiaryListToTimeTree(InputObject inputObject, OutputObject outputObject) throws Exception{
        jobDiaryService.queryJobDiaryListToTimeTree(inputObject, outputObject);
    }
    
    /**
     * 
         * @Title: editReceivedJobDiaryToAlreadyRead
         * @Description: 我收到的日志全部设置为已读
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @RequestMapping("/post/JobDiaryController/editReceivedJobDiaryToAlreadyRead")
    @ResponseBody
    public void editReceivedJobDiaryToAlreadyRead(InputObject inputObject, OutputObject outputObject) throws Exception{
        jobDiaryService.editReceivedJobDiaryToAlreadyRead(inputObject, outputObject);
    }
	
}
