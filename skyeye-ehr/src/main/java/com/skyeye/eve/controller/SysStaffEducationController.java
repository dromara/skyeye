package com.skyeye.eve.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.SysStaffEducationService;

@Controller
public class SysStaffEducationController {
	
	@Autowired
	private SysStaffEducationService sysStaffEducationService;
	
	/**
	 * 
	 * @Title: queryAllSysStaffEducationList
	 * @Description: 查询所有教育背景列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @return: void
	 * @throws
	 */
    @RequestMapping("/post/SysStaffEducationController/queryAllSysStaffEducationList")
    @ResponseBody
    public void queryAllSysStaffEducationList(InputObject inputObject, OutputObject outputObject) throws Exception {
    	sysStaffEducationService.queryAllSysStaffEducationList(inputObject, outputObject);
    }
    
    /**
	 * 
	 * @Title: insertSysStaffEducationMation
	 * @Description: 员工教育背景信息录入
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @return: void
	 * @throws
	 */
    @RequestMapping("/post/SysStaffEducationController/insertSysStaffEducationMation")
    @ResponseBody
    public void insertSysStaffEducationMation(InputObject inputObject, OutputObject outputObject) throws Exception {
    	sysStaffEducationService.insertSysStaffEducationMation(inputObject, outputObject);
    }
    
    /**
	 * 
	 * @Title: querySysStaffEducationMationToEdit
	 * @Description: 编辑员工教育背景信息时回显
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @return: void
	 * @throws
	 */
    @RequestMapping("/post/SysStaffEducationController/querySysStaffEducationMationToEdit")
    @ResponseBody
    public void querySysStaffEducationMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception {
    	sysStaffEducationService.querySysStaffEducationMationToEdit(inputObject, outputObject);
    }
    
    /**
	 * 
	 * @Title: editSysStaffEducationMationById
	 * @Description: 编辑员工教育背景信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @return: void
	 * @throws
	 */
    @RequestMapping("/post/SysStaffEducationController/editSysStaffEducationMationById")
    @ResponseBody
    public void editSysStaffEducationMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
    	sysStaffEducationService.editSysStaffEducationMationById(inputObject, outputObject);
    }
    
    /**
	 * 
	 * @Title: deleteSysStaffEducationMationById
	 * @Description: 删除员工教育背景信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @return: void
	 * @throws
	 */
    @RequestMapping("/post/SysStaffEducationController/deleteSysStaffEducationMationById")
    @ResponseBody
    public void deleteSysStaffEducationMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
    	sysStaffEducationService.deleteSysStaffEducationMationById(inputObject, outputObject);
    }
    
    /**
	 * 
	 * @Title: queryPointStaffSysStaffEducationList
	 * @Description: 查询指定员工的教育背景列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @return: void
	 * @throws
	 */
    @RequestMapping("/post/SysStaffEducationController/queryPointStaffSysStaffEducationList")
    @ResponseBody
    public void queryPointStaffSysStaffEducationList(InputObject inputObject, OutputObject outputObject) throws Exception {
    	sysStaffEducationService.queryPointStaffSysStaffEducationList(inputObject, outputObject);
    }
	
}
