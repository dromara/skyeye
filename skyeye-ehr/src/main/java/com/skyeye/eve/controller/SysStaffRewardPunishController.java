package com.skyeye.eve.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.SysStaffRewardPunishService;

@Controller
public class SysStaffRewardPunishController {
	
	@Autowired
	private SysStaffRewardPunishService sysStaffRewardPunishService;
	
	/**
	 * 
	 * @Title: queryAllSysStaffRewardPunishList
	 * @Description: 查询所有奖惩列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @return: void
	 * @throws
	 */
    @RequestMapping("/post/SysStaffRewardPunishController/queryAllSysStaffRewardPunishList")
    @ResponseBody
    public void queryAllSysStaffRewardPunishList(InputObject inputObject, OutputObject outputObject) throws Exception {
    	sysStaffRewardPunishService.queryAllSysStaffRewardPunishList(inputObject, outputObject);
    }
    
    /**
	 * 
	 * @Title: insertSysStaffRewardPunishMation
	 * @Description: 员工奖惩信息录入
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @return: void
	 * @throws
	 */
    @RequestMapping("/post/SysStaffRewardPunishController/insertSysStaffRewardPunishMation")
    @ResponseBody
    public void insertSysStaffRewardPunishMation(InputObject inputObject, OutputObject outputObject) throws Exception {
    	sysStaffRewardPunishService.insertSysStaffRewardPunishMation(inputObject, outputObject);
    }
    
    /**
	 * 
	 * @Title: querySysStaffRewardPunishMationToEdit
	 * @Description: 编辑员工奖惩信息时回显
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @return: void
	 * @throws
	 */
    @RequestMapping("/post/SysStaffRewardPunishController/querySysStaffRewardPunishMationToEdit")
    @ResponseBody
    public void querySysStaffRewardPunishMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception {
    	sysStaffRewardPunishService.querySysStaffRewardPunishMationToEdit(inputObject, outputObject);
    }
    
    /**
	 * 
	 * @Title: editSysStaffRewardPunishMationById
	 * @Description: 编辑员工奖惩信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @return: void
	 * @throws
	 */
    @RequestMapping("/post/SysStaffRewardPunishController/editSysStaffRewardPunishMationById")
    @ResponseBody
    public void editSysStaffRewardPunishMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
    	sysStaffRewardPunishService.editSysStaffRewardPunishMationById(inputObject, outputObject);
    }
    
    /**
	 * 
	 * @Title: deleteSysStaffRewardPunishMationById
	 * @Description: 删除员工奖惩信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @return: void
	 * @throws
	 */
    @RequestMapping("/post/SysStaffRewardPunishController/deleteSysStaffRewardPunishMationById")
    @ResponseBody
    public void deleteSysStaffRewardPunishMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
    	sysStaffRewardPunishService.deleteSysStaffRewardPunishMationById(inputObject, outputObject);
    }
    
    /**
	 * 
	 * @Title: queryPointStaffSysStaffRewardPunishList
	 * @Description: 查询指定员工的奖惩列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @return: void
	 * @throws
	 */
    @RequestMapping("/post/SysStaffRewardPunishController/queryPointStaffSysStaffRewardPunishList")
    @ResponseBody
    public void queryPointStaffSysStaffRewardPunishList(InputObject inputObject, OutputObject outputObject) throws Exception {
    	sysStaffRewardPunishService.queryPointStaffSysStaffRewardPunishList(inputObject, outputObject);
    }
	
}
