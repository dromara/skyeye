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
import com.skyeye.eve.service.ScheduleDayService;


@Controller
public class ScheduleDayController {
	
	@Autowired
	private ScheduleDayService scheduleDayService;
	
	/**
	 * 
	     * @Title: insertScheduleDayMation
	     * @Description: 添加日程信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ScheduleDayController/insertScheduleDayMation")
	@ResponseBody
	public void insertScheduleDayMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		scheduleDayService.insertScheduleDayMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryScheduleDayMationByUserId
	     * @Description: 根据用户日程信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ScheduleDayController/queryScheduleDayMationByUserId")
	@ResponseBody
	public void queryScheduleDayMationByUserId(InputObject inputObject, OutputObject outputObject) throws Exception{
		scheduleDayService.queryScheduleDayMationByUserId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryScheduleDayMationTodayByUserId
	     * @Description: 根据用户获取今日日程信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ScheduleDayController/queryScheduleDayMationTodayByUserId")
	@ResponseBody
	public void queryScheduleDayMationTodayByUserId(InputObject inputObject, OutputObject outputObject) throws Exception{
		scheduleDayService.queryScheduleDayMationTodayByUserId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editScheduleDayMationById
	     * @Description: 修改日程日期信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ScheduleDayController/editScheduleDayMationById")
	@ResponseBody
	public void editScheduleDayMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		scheduleDayService.editScheduleDayMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryScheduleDayMationById
	     * @Description: 获取日程详细信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ScheduleDayController/queryScheduleDayMationById")
	@ResponseBody
	public void queryScheduleDayMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		scheduleDayService.queryScheduleDayMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteScheduleDayMationById
	     * @Description: 删除日程信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ScheduleDayController/deleteScheduleDayMationById")
	@ResponseBody
	public void deleteScheduleDayMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		scheduleDayService.deleteScheduleDayMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryHolidayScheduleList
	     * @Description: 获取系统发布的请假日程
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ScheduleDayController/queryHolidayScheduleList")
	@ResponseBody
	public void queryHolidayScheduleList(InputObject inputObject, OutputObject outputObject) throws Exception{
		scheduleDayService.queryHolidayScheduleList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: downloadScheduleTemplate
	     * @Description: 下载节假日导入模板
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ScheduleDayController/downloadScheduleTemplate")
	@ResponseBody
	public void downloadScheduleTemplate(InputObject inputObject, OutputObject outputObject) throws Exception{
		scheduleDayService.downloadScheduleTemplate(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: exploreScheduleTemplate
	     * @Description: 导入节假日日程
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ScheduleDayController/exploreScheduleTemplate")
	@ResponseBody
	public void exploreScheduleTemplate(InputObject inputObject, OutputObject outputObject) throws Exception{
		scheduleDayService.exploreScheduleTemplate(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteHolidayScheduleById
	     * @Description: 删除节假日日程
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ScheduleDayController/deleteHolidayScheduleById")
	@ResponseBody
	public void deleteHolidayScheduleById(InputObject inputObject, OutputObject outputObject) throws Exception{
		scheduleDayService.deleteHolidayScheduleById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteHolidayScheduleByThisYear
	     * @Description: 删除本年度节假日日程
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ScheduleDayController/deleteHolidayScheduleByThisYear")
	@ResponseBody
	public void deleteHolidayScheduleByThisYear(InputObject inputObject, OutputObject outputObject) throws Exception{
		scheduleDayService.deleteHolidayScheduleByThisYear(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: addHolidayScheduleRemind
	     * @Description: 添加节假日日程提醒
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ScheduleDayController/addHolidayScheduleRemind")
	@ResponseBody
	public void addHolidayScheduleRemind(InputObject inputObject, OutputObject outputObject) throws Exception{
		scheduleDayService.addHolidayScheduleRemind(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteHolidayScheduleRemind
	     * @Description: 取消节假日日程提醒
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ScheduleDayController/deleteHolidayScheduleRemind")
	@ResponseBody
	public void deleteHolidayScheduleRemind(InputObject inputObject, OutputObject outputObject) throws Exception{
		scheduleDayService.deleteHolidayScheduleRemind(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryScheduleByIdToEdit
	     * @Description: 回显节假日信息以编辑
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ScheduleDayController/queryScheduleByIdToEdit")
	@ResponseBody
	public void queryScheduleByIdToEdit(InputObject inputObject, OutputObject outputObject) throws Exception{
		scheduleDayService.queryScheduleByIdToEdit(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editScheduleById
	     * @Description: 编辑节假日
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ScheduleDayController/editScheduleById")
	@ResponseBody
	public void editScheduleById(InputObject inputObject, OutputObject outputObject) throws Exception{
		scheduleDayService.editScheduleById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: addSchedule
	     * @Description: 新增节假日
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ScheduleDayController/addSchedule")
	@ResponseBody
	public void addSchedule(InputObject inputObject, OutputObject outputObject) throws Exception{
		scheduleDayService.addSchedule(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryHolidayScheduleListBySys
	     * @Description: 获取所有节假日
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ScheduleDayController/queryHolidayScheduleListBySys")
	@ResponseBody
	public void queryHolidayScheduleListBySys(InputObject inputObject, OutputObject outputObject) throws Exception{
		scheduleDayService.queryHolidayScheduleListBySys(inputObject, outputObject);
	}

	/**
	 *
	 * @Title: queryMyScheduleList
	 * @Description: 获取我的日程
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    参数
	 * @return void    返回类型
	 * @throws
	 */
	@RequestMapping("/post/ScheduleDayController/queryMyScheduleList")
	@ResponseBody
	public void queryMyScheduleList(InputObject inputObject, OutputObject outputObject) throws Exception{
		scheduleDayService.queryMyScheduleList(inputObject, outputObject);
	}

}
