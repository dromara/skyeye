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
import com.skyeye.eve.service.ErpProducePageService;

@Controller
public class ErpProducePageController {
	
	@Autowired
	private ErpProducePageService erpProducePageService;
	
	/**
	 * 
	    * @Title: queryDepartmentPickMaterial
	    * @Description: 统计当前部门月度领料图
	    * @param inputObject
	    * @param outputObject
	    * @throws Exception    参数
	    * @return void    返回类型
	    * @throws
	 */
    @RequestMapping("/post/ErpProducePageController/queryDepartmentPickMaterial")
    @ResponseBody
    public void queryDepartmentPickMaterial(InputObject inputObject, OutputObject outputObject) throws Exception{
    	erpProducePageService.queryDepartmentPickMaterial(inputObject, outputObject);
    }
    
    /**
	 * 
	    * @Title: queryDepartmentPatchMaterial
	    * @Description: 统计当前部门月度补料图
	    * @param inputObject
	    * @param outputObject
	    * @throws Exception    参数
	    * @return void    返回类型
	    * @throws
	 */
    @RequestMapping("/post/ErpProducePageController/queryDepartmentPatchMaterial")
    @ResponseBody
    public void queryDepartmentPatchMaterial(InputObject inputObject, OutputObject outputObject) throws Exception{
    	erpProducePageService.queryDepartmentPatchMaterial(inputObject, outputObject);
    }
    
    /**
	 * 
	    * @Title: queryDepartmentReturnMaterial
	    * @Description: 统计当前部门月度退料图
	    * @param inputObject
	    * @param outputObject
	    * @throws Exception    参数
	    * @return void    返回类型
	    * @throws
	 */
    @RequestMapping("/post/ErpProducePageController/queryDepartmentReturnMaterial")
    @ResponseBody
    public void queryDepartmentReturnMaterial(InputObject inputObject, OutputObject outputObject) throws Exception{
    	erpProducePageService.queryDepartmentReturnMaterial(inputObject, outputObject);
    }
    
    /**
	 * 
	    * @Title: queryDepartmentMachin
	    * @Description: 统计当前部门月度新建加工单图
	    * @param inputObject
	    * @param outputObject
	    * @throws Exception    参数
	    * @return void    返回类型
	    * @throws
	 */
    @RequestMapping("/post/ErpProducePageController/queryDepartmentMachin")
    @ResponseBody
    public void queryDepartmentMachin(InputObject inputObject, OutputObject outputObject) throws Exception{
    	erpProducePageService.queryDepartmentMachin(inputObject, outputObject);
    }
	
}
