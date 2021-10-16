package com.skyeye.eve.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.SysStaffJobResumeService;

@Controller
public class SysStaffJobResumeController {
	
	@Autowired
	private SysStaffJobResumeService sysStaffJobResumeService;
	
	/**
	 * 
	 * @Title: queryAllSysStaffJobResumeList
	 * @Description: 查询所有工作履历列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @return: void
	 * @throws
	 */
    @RequestMapping("/post/SysStaffJobResumeController/queryAllSysStaffJobResumeList")
    @ResponseBody
    public void queryAllSysStaffJobResumeList(InputObject inputObject, OutputObject outputObject) throws Exception {
    	sysStaffJobResumeService.queryAllSysStaffJobResumeList(inputObject, outputObject);
    }
    
    /**
	 * 
	 * @Title: insertSysStaffJobResumeMation
	 * @Description: 员工工作履历信息录入
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @return: void
	 * @throws
	 */
    @RequestMapping("/post/SysStaffJobResumeController/insertSysStaffJobResumeMation")
    @ResponseBody
    public void insertSysStaffJobResumeMation(InputObject inputObject, OutputObject outputObject) throws Exception {
    	sysStaffJobResumeService.insertSysStaffJobResumeMation(inputObject, outputObject);
    }
    
    /**
	 * 
	 * @Title: querySysStaffJobResumeMationToEdit
	 * @Description: 编辑员工工作履历信息时回显
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @return: void
	 * @throws
	 */
    @RequestMapping("/post/SysStaffJobResumeController/querySysStaffJobResumeMationToEdit")
    @ResponseBody
    public void querySysStaffJobResumeMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception {
    	sysStaffJobResumeService.querySysStaffJobResumeMationToEdit(inputObject, outputObject);
    }
    
    /**
	 * 
	 * @Title: editSysStaffJobResumeMationById
	 * @Description: 编辑员工工作履历信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @return: void
	 * @throws
	 */
    @RequestMapping("/post/SysStaffJobResumeController/editSysStaffJobResumeMationById")
    @ResponseBody
    public void editSysStaffJobResumeMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
    	sysStaffJobResumeService.editSysStaffJobResumeMationById(inputObject, outputObject);
    }
    
    /**
	 * 
	 * @Title: deleteSysStaffJobResumeMationById
	 * @Description: 删除员工工作履历信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @return: void
	 * @throws
	 */
    @RequestMapping("/post/SysStaffJobResumeController/deleteSysStaffJobResumeMationById")
    @ResponseBody
    public void deleteSysStaffJobResumeMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
    	sysStaffJobResumeService.deleteSysStaffJobResumeMationById(inputObject, outputObject);
    }
    
    /**
	 * 
	 * @Title: queryPointStaffSysStaffJobResumeList
	 * @Description: 查询指定员工的工作履历列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @return: void
	 * @throws
	 */
    @RequestMapping("/post/SysStaffJobResumeController/queryPointStaffSysStaffJobResumeList")
    @ResponseBody
    public void queryPointStaffSysStaffJobResumeList(InputObject inputObject, OutputObject outputObject) throws Exception {
    	sysStaffJobResumeService.queryPointStaffSysStaffJobResumeList(inputObject, outputObject);
    }
	
}
