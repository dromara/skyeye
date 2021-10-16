package com.skyeye.eve.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.SysStaffContractService;

@Controller
public class SysStaffContractController {
	
	@Autowired
	private SysStaffContractService sysStaffContractService;
	
	/**
	 * 
	 * @Title: queryAllSysStaffContractList
	 * @Description: 查询所有合同列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @return: void
	 * @throws
	 */
    @RequestMapping("/post/SysStaffContractController/queryAllSysStaffContractList")
    @ResponseBody
    public void queryAllSysStaffContractList(InputObject inputObject, OutputObject outputObject) throws Exception {
    	sysStaffContractService.queryAllSysStaffContractList(inputObject, outputObject);
    }
    
    /**
	 * 
	 * @Title: insertSysStaffContractMation
	 * @Description: 员工合同信息录入
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @return: void
	 * @throws
	 */
    @RequestMapping("/post/SysStaffContractController/insertSysStaffContractMation")
    @ResponseBody
    public void insertSysStaffContractMation(InputObject inputObject, OutputObject outputObject) throws Exception {
    	sysStaffContractService.insertSysStaffContractMation(inputObject, outputObject);
    }
    
    /**
	 * 
	 * @Title: querySysStaffContractMationToEdit
	 * @Description: 编辑员工合同信息时回显
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @return: void
	 * @throws
	 */
    @RequestMapping("/post/SysStaffContractController/querySysStaffContractMationToEdit")
    @ResponseBody
    public void querySysStaffContractMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception {
    	sysStaffContractService.querySysStaffContractMationToEdit(inputObject, outputObject);
    }
    
    /**
	 * 
	 * @Title: editSysStaffContractMationById
	 * @Description: 编辑员工合同信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @return: void
	 * @throws
	 */
    @RequestMapping("/post/SysStaffContractController/editSysStaffContractMationById")
    @ResponseBody
    public void editSysStaffContractMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
    	sysStaffContractService.editSysStaffContractMationById(inputObject, outputObject);
    }
    
    /**
	 * 
	 * @Title: deleteSysStaffContractMationById
	 * @Description: 删除员工合同信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @return: void
	 * @throws
	 */
    @RequestMapping("/post/SysStaffContractController/deleteSysStaffContractMationById")
    @ResponseBody
    public void deleteSysStaffContractMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
    	sysStaffContractService.deleteSysStaffContractMationById(inputObject, outputObject);
    }
    
    /**
	 * 
	 * @Title: queryPointStaffSysStaffContractList
	 * @Description: 查询指定员工的合同列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @return: void
	 * @throws
	 */
    @RequestMapping("/post/SysStaffContractController/queryPointStaffSysStaffContractList")
    @ResponseBody
    public void queryPointStaffSysStaffContractList(InputObject inputObject, OutputObject outputObject) throws Exception {
    	sysStaffContractService.queryPointStaffSysStaffContractList(inputObject, outputObject);
    }
    
    /**
	 * 
	 * @Title: signSysStaffContractById
	 * @Description: 员工合同签约
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @return: void
	 * @throws
	 */
    @RequestMapping("/post/SysStaffContractController/signSysStaffContractById")
    @ResponseBody
    public void signSysStaffContractById(InputObject inputObject, OutputObject outputObject) throws Exception {
    	sysStaffContractService.signSysStaffContractById(inputObject, outputObject);
    }
	
}
