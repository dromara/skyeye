package com.skyeye.eve.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.SysStaffArchivesService;

/**
 * 员工档案类
 *
 * @author 卫志强
 */
@Controller
public class SysStaffArchivesController {

	@Autowired
	private SysStaffArchivesService sysStaffArchivesService;
	
	/**
	 * 
	 * @Title: queryAllSysStaffArchivesList
	 * @Description: 查询所有档案列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @return: void
	 * @throws
	 */
    @RequestMapping("/post/SysStaffArchivesController/queryAllSysStaffArchivesList")
    @ResponseBody
    public void queryAllSysStaffArchivesList(InputObject inputObject, OutputObject outputObject) throws Exception {
    	sysStaffArchivesService.queryAllSysStaffArchivesList(inputObject, outputObject);
    }
    
    /**
	 * 
	 * @Title: querySysLeaveStaffArchivesList
	 * @Description: 离职员工在档列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @return: void
	 * @throws
	 */
    @RequestMapping("/post/SysStaffArchivesController/querySysLeaveStaffArchivesList")
    @ResponseBody
    public void querySysLeaveStaffArchivesList(InputObject inputObject, OutputObject outputObject) throws Exception {
    	sysStaffArchivesService.querySysLeaveStaffArchivesList(inputObject, outputObject);
    }
    
    /**
	 * 
	 * @Title: querySysStaffNotInArchivesList
	 * @Description: 员工不在档列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @return: void
	 * @throws
	 */
    @RequestMapping("/post/SysStaffArchivesController/querySysStaffNotInArchivesList")
    @ResponseBody
    public void querySysStaffNotInArchivesList(InputObject inputObject, OutputObject outputObject) throws Exception {
    	sysStaffArchivesService.querySysStaffNotInArchivesList(inputObject, outputObject);
    }
    
    /**
	 * 
	 * @Title: querySysStaffNoArchivesList
	 * @Description: 员工无在档列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @return: void
	 * @throws
	 */
    @RequestMapping("/post/SysStaffArchivesController/querySysStaffNoArchivesList")
    @ResponseBody
    public void querySysStaffNoArchivesList(InputObject inputObject, OutputObject outputObject) throws Exception {
    	sysStaffArchivesService.querySysStaffNoArchivesList(inputObject, outputObject);
    }
    
    /**
	 * 
	 * @Title: insertSysStaffArchivesMation
	 * @Description: 员工档案信息录入
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @return: void
	 * @throws
	 */
    @RequestMapping("/post/SysStaffArchivesController/insertSysStaffArchivesMation")
    @ResponseBody
    public void insertSysStaffArchivesMation(InputObject inputObject, OutputObject outputObject) throws Exception {
    	sysStaffArchivesService.insertSysStaffArchivesMation(inputObject, outputObject);
    }
    
    /**
	 * 
	 * @Title: querySysStaffArchivesMationToEdit
	 * @Description: 编辑员工档案信息时回显
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @return: void
	 * @throws
	 */
    @RequestMapping("/post/SysStaffArchivesController/querySysStaffArchivesMationToEdit")
    @ResponseBody
    public void querySysStaffArchivesMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception {
    	sysStaffArchivesService.querySysStaffArchivesMationToEdit(inputObject, outputObject);
    }
    
    /**
	 * 
	 * @Title: editSysStaffArchivesMationById
	 * @Description: 编辑员工档案信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @return: void
	 * @throws
	 */
    @RequestMapping("/post/SysStaffArchivesController/editSysStaffArchivesMationById")
    @ResponseBody
    public void editSysStaffArchivesMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
    	sysStaffArchivesService.editSysStaffArchivesMationById(inputObject, outputObject);
    }
	
}
