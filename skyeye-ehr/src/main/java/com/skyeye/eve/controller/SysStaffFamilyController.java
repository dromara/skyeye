package com.skyeye.eve.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.SysStaffFamilyService;

@Controller
public class SysStaffFamilyController {
	
	@Autowired
	private SysStaffFamilyService sysStaffFamilyService;
	
	/**
	 * 
	 * @Title: queryAllSysStaffFamilyList
	 * @Description: 查询所有家庭情况列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @return: void
	 * @throws
	 */
    @RequestMapping("/post/SysStaffFamilyController/queryAllSysStaffFamilyList")
    @ResponseBody
    public void queryAllSysStaffFamilyList(InputObject inputObject, OutputObject outputObject) throws Exception {
    	sysStaffFamilyService.queryAllSysStaffFamilyList(inputObject, outputObject);
    }
    
    /**
	 * 
	 * @Title: insertSysStaffFamilyMation
	 * @Description: 员工家庭情况信息录入
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @return: void
	 * @throws
	 */
    @RequestMapping("/post/SysStaffFamilyController/insertSysStaffFamilyMation")
    @ResponseBody
    public void insertSysStaffFamilyMation(InputObject inputObject, OutputObject outputObject) throws Exception {
    	sysStaffFamilyService.insertSysStaffFamilyMation(inputObject, outputObject);
    }
    
    /**
	 * 
	 * @Title: querySysStaffFamilyMationToEdit
	 * @Description: 编辑员工家庭情况信息时回显
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @return: void
	 * @throws
	 */
    @RequestMapping("/post/SysStaffFamilyController/querySysStaffFamilyMationToEdit")
    @ResponseBody
    public void querySysStaffFamilyMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception {
    	sysStaffFamilyService.querySysStaffFamilyMationToEdit(inputObject, outputObject);
    }
    
    /**
	 * 
	 * @Title: editSysStaffFamilyMationById
	 * @Description: 编辑员工家庭情况信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @return: void
	 * @throws
	 */
    @RequestMapping("/post/SysStaffFamilyController/editSysStaffFamilyMationById")
    @ResponseBody
    public void editSysStaffFamilyMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
    	sysStaffFamilyService.editSysStaffFamilyMationById(inputObject, outputObject);
    }
    
    /**
	 * 
	 * @Title: deleteSysStaffFamilyMationById
	 * @Description: 删除员工家庭情况信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @return: void
	 * @throws
	 */
    @RequestMapping("/post/SysStaffFamilyController/deleteSysStaffFamilyMationById")
    @ResponseBody
    public void deleteSysStaffFamilyMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
    	sysStaffFamilyService.deleteSysStaffFamilyMationById(inputObject, outputObject);
    }
    
    /**
	 * 
	 * @Title: queryPointStaffSysStaffFamilyList
	 * @Description: 查询指定员工的家庭情况列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @return: void
	 * @throws
	 */
    @RequestMapping("/post/SysStaffFamilyController/queryPointStaffSysStaffFamilyList")
    @ResponseBody
    public void queryPointStaffSysStaffFamilyList(InputObject inputObject, OutputObject outputObject) throws Exception {
    	sysStaffFamilyService.queryPointStaffSysStaffFamilyList(inputObject, outputObject);
    }
	
}
