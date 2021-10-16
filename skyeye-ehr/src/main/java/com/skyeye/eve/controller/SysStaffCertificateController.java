package com.skyeye.eve.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.SysStaffCertificateService;

@Controller
public class SysStaffCertificateController {
	
	@Autowired
	private SysStaffCertificateService sysStaffCertificateService;
	
	/**
	 * 
	 * @Title: queryAllSysStaffCertificateList
	 * @Description: 查询所有证书列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @return: void
	 * @throws
	 */
    @RequestMapping("/post/SysStaffCertificateController/queryAllSysStaffCertificateList")
    @ResponseBody
    public void queryAllSysStaffCertificateList(InputObject inputObject, OutputObject outputObject) throws Exception {
    	sysStaffCertificateService.queryAllSysStaffCertificateList(inputObject, outputObject);
    }
    
    /**
	 * 
	 * @Title: insertSysStaffCertificateMation
	 * @Description: 员工证书信息录入
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @return: void
	 * @throws
	 */
    @RequestMapping("/post/SysStaffCertificateController/insertSysStaffCertificateMation")
    @ResponseBody
    public void insertSysStaffCertificateMation(InputObject inputObject, OutputObject outputObject) throws Exception {
    	sysStaffCertificateService.insertSysStaffCertificateMation(inputObject, outputObject);
    }
    
    /**
	 * 
	 * @Title: querySysStaffCertificateMationToEdit
	 * @Description: 编辑员工证书信息时回显
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @return: void
	 * @throws
	 */
    @RequestMapping("/post/SysStaffCertificateController/querySysStaffCertificateMationToEdit")
    @ResponseBody
    public void querySysStaffCertificateMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception {
    	sysStaffCertificateService.querySysStaffCertificateMationToEdit(inputObject, outputObject);
    }
    
    /**
	 * 
	 * @Title: editSysStaffCertificateMationById
	 * @Description: 编辑员工证书信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @return: void
	 * @throws
	 */
    @RequestMapping("/post/SysStaffCertificateController/editSysStaffCertificateMationById")
    @ResponseBody
    public void editSysStaffCertificateMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
    	sysStaffCertificateService.editSysStaffCertificateMationById(inputObject, outputObject);
    }
    
    /**
	 * 
	 * @Title: deleteSysStaffCertificateMationById
	 * @Description: 删除员工证书信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @return: void
	 * @throws
	 */
    @RequestMapping("/post/SysStaffCertificateController/deleteSysStaffCertificateMationById")
    @ResponseBody
    public void deleteSysStaffCertificateMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
    	sysStaffCertificateService.deleteSysStaffCertificateMationById(inputObject, outputObject);
    }
    
    /**
	 * 
	 * @Title: queryPointStaffSysStaffCertificateList
	 * @Description: 查询指定员工的证书列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @return: void
	 * @throws
	 */
    @RequestMapping("/post/SysStaffCertificateController/queryPointStaffSysStaffCertificateList")
    @ResponseBody
    public void queryPointStaffSysStaffCertificateList(InputObject inputObject, OutputObject outputObject) throws Exception {
    	sysStaffCertificateService.queryPointStaffSysStaffCertificateList(inputObject, outputObject);
    }
	
}
