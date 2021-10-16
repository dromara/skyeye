package com.skyeye.eve.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.CheckWorkTimeService;

@Controller
public class CheckWorkTimeController {
	
	@Autowired
	private CheckWorkTimeService checkWorkTimeService;
	
	/**
	 * 
	 * @Title: queryAllCheckWorkTimeList
	 * @Description: 查询所有考勤班次列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @return: void
	 * @throws
	 */
    @RequestMapping("/post/CheckWorkTimeController/queryAllCheckWorkTimeList")
    @ResponseBody
    public void queryAllCheckWorkTimeList(InputObject inputObject, OutputObject outputObject) throws Exception {
    	checkWorkTimeService.queryAllCheckWorkTimeList(inputObject, outputObject);
    }
    
    /**
	 * 
	 * @Title: insertCheckWorkTimeMation
	 * @Description: 员工考勤班次信息录入
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @return: void
	 * @throws
	 */
    @RequestMapping("/post/CheckWorkTimeController/insertCheckWorkTimeMation")
    @ResponseBody
    public void insertCheckWorkTimeMation(InputObject inputObject, OutputObject outputObject) throws Exception {
    	checkWorkTimeService.insertCheckWorkTimeMation(inputObject, outputObject);
    }
    
    /**
	 * 
	 * @Title: queryCheckWorkTimeMationToEdit
	 * @Description: 编辑员工考勤班次信息时回显
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @return: void
	 * @throws
	 */
    @RequestMapping("/post/CheckWorkTimeController/queryCheckWorkTimeMationToEdit")
    @ResponseBody
    public void queryCheckWorkTimeMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception {
    	checkWorkTimeService.queryCheckWorkTimeMationToEdit(inputObject, outputObject);
    }
    
    /**
	 * 
	 * @Title: editCheckWorkTimeMationById
	 * @Description: 编辑员工考勤班次信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @return: void
	 * @throws
	 */
    @RequestMapping("/post/CheckWorkTimeController/editCheckWorkTimeMationById")
    @ResponseBody
    public void editCheckWorkTimeMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
    	checkWorkTimeService.editCheckWorkTimeMationById(inputObject, outputObject);
    }
    
    /**
	 * 
	 * @Title: deleteCheckWorkTimeMationById
	 * @Description: 删除员工考勤班次信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @return: void
	 * @throws
	 */
    @RequestMapping("/post/CheckWorkTimeController/deleteCheckWorkTimeMationById")
    @ResponseBody
    public void deleteCheckWorkTimeMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
    	checkWorkTimeService.deleteCheckWorkTimeMationById(inputObject, outputObject);
    }
    
    /**
	 * 
	 * @Title: queryEnableCheckWorkTimeList
	 * @Description: 查询启用的考勤班次列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @return: void
	 * @throws
	 */
    @RequestMapping("/post/CheckWorkTimeController/queryEnableCheckWorkTimeList")
    @ResponseBody
    public void queryEnableCheckWorkTimeList(InputObject inputObject, OutputObject outputObject) throws Exception {
    	checkWorkTimeService.queryEnableCheckWorkTimeList(inputObject, outputObject);
    }
    
    /**
	 * 
	 * @Title: queryCheckWorkTimeListByLoginUser
	 * @Description: 获取当前登陆人的考勤班次
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @return: void
	 * @throws
	 */
    @RequestMapping("/post/CheckWorkTimeController/queryCheckWorkTimeListByLoginUser")
    @ResponseBody
    public void queryCheckWorkTimeListByLoginUser(InputObject inputObject, OutputObject outputObject) throws Exception {
    	checkWorkTimeService.queryCheckWorkTimeListByLoginUser(inputObject, outputObject);
    }
	
}
