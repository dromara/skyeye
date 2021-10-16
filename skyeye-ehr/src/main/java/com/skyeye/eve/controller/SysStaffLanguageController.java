package com.skyeye.eve.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.SysStaffLanguageService;

@Controller
public class SysStaffLanguageController {
	
	@Autowired
	private SysStaffLanguageService sysStaffLanguageService;
	
	/**
	 * 
	 * @Title: queryAllSysStaffLanguageList
	 * @Description: 查询所有语言能力列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @return: void
	 * @throws
	 */
    @RequestMapping("/post/SysStaffLanguageController/queryAllSysStaffLanguageList")
    @ResponseBody
    public void queryAllSysStaffLanguageList(InputObject inputObject, OutputObject outputObject) throws Exception {
    	sysStaffLanguageService.queryAllSysStaffLanguageList(inputObject, outputObject);
    }
    
    /**
	 * 
	 * @Title: insertSysStaffLanguageMation
	 * @Description: 员工语言能力信息录入
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @return: void
	 * @throws
	 */
    @RequestMapping("/post/SysStaffLanguageController/insertSysStaffLanguageMation")
    @ResponseBody
    public void insertSysStaffLanguageMation(InputObject inputObject, OutputObject outputObject) throws Exception {
    	sysStaffLanguageService.insertSysStaffLanguageMation(inputObject, outputObject);
    }
    
    /**
	 * 
	 * @Title: querySysStaffLanguageMationToEdit
	 * @Description: 编辑员工语言能力信息时回显
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @return: void
	 * @throws
	 */
    @RequestMapping("/post/SysStaffLanguageController/querySysStaffLanguageMationToEdit")
    @ResponseBody
    public void querySysStaffLanguageMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception {
    	sysStaffLanguageService.querySysStaffLanguageMationToEdit(inputObject, outputObject);
    }
    
    /**
	 * 
	 * @Title: editSysStaffLanguageMationById
	 * @Description: 编辑员工语言能力信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @return: void
	 * @throws
	 */
    @RequestMapping("/post/SysStaffLanguageController/editSysStaffLanguageMationById")
    @ResponseBody
    public void editSysStaffLanguageMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
    	sysStaffLanguageService.editSysStaffLanguageMationById(inputObject, outputObject);
    }
    
    /**
	 * 
	 * @Title: deleteSysStaffLanguageMationById
	 * @Description: 删除员工语言能力信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @return: void
	 * @throws
	 */
    @RequestMapping("/post/SysStaffLanguageController/deleteSysStaffLanguageMationById")
    @ResponseBody
    public void deleteSysStaffLanguageMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
    	sysStaffLanguageService.deleteSysStaffLanguageMationById(inputObject, outputObject);
    }
    
    /**
	 * 
	 * @Title: queryPointStaffSysStaffLanguageList
	 * @Description: 查询指定员工的语言能力列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @return: void
	 * @throws
	 */
    @RequestMapping("/post/SysStaffLanguageController/queryPointStaffSysStaffLanguageList")
    @ResponseBody
    public void queryPointStaffSysStaffLanguageList(InputObject inputObject, OutputObject outputObject) throws Exception {
    	sysStaffLanguageService.queryPointStaffSysStaffLanguageList(inputObject, outputObject);
    }
	
}
