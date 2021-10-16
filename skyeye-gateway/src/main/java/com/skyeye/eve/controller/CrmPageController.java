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
import com.skyeye.eve.service.CrmPageService;

@Controller
public class CrmPageController {
	
	@Autowired
	private CrmPageService crmPageService;
	
	/**
	 * 
	    * @Title: queryInsertNumByYear
	    * @Description: 获取指定年度的客户新增量，联系人新增量
	    * @param inputObject
	    * @param outputObject
	    * @throws Exception    参数
	    * @return void    返回类型
	    * @throws
	 */
    @RequestMapping("/post/CrmPageController/queryInsertNumByYear")
    @ResponseBody
    public void queryInsertNumByYear(InputObject inputObject, OutputObject outputObject) throws Exception{
    	crmPageService.queryInsertNumByYear(inputObject, outputObject);
    }
    
    /**
	 * 
	    * @Title: queryCustomNumByOtherType
	    * @Description: 根据客户分类，客户来源，所属行业，客户分组统计客户数量
	    * @param inputObject
	    * @param outputObject
	    * @throws Exception    参数
	    * @return void    返回类型
	    * @throws
	 */
    @RequestMapping("/post/CrmPageController/queryCustomNumByOtherType")
    @ResponseBody
    public void queryCustomNumByOtherType(InputObject inputObject, OutputObject outputObject) throws Exception{
    	crmPageService.queryCustomNumByOtherType(inputObject, outputObject);
    }
    
    /**
	 * 
	    * @Title: queryCustomDocumentaryType
	    * @Description: 客户跟单方式分析
	    * @param inputObject
	    * @param outputObject
	    * @throws Exception    参数
	    * @return void    返回类型
	    * @throws
	 */
    @RequestMapping("/post/CrmPageController/queryCustomDocumentaryType")
    @ResponseBody
    public void queryCustomDocumentaryType(InputObject inputObject, OutputObject outputObject) throws Exception{
    	crmPageService.queryCustomDocumentaryType(inputObject, outputObject);
    }
    
    /**
	 * 
	    * @Title: queryNewContractNum
	    * @Description: 获取合同在指定年度的月新增量
	    * @param inputObject
	    * @param outputObject
	    * @throws Exception    参数
	    * @return void    返回类型
	    * @throws
	 */
    @RequestMapping("/post/CrmPageController/queryNewContractNum")
    @ResponseBody
    public void queryNewContractNum(InputObject inputObject, OutputObject outputObject) throws Exception{
    	crmPageService.queryNewContractNum(inputObject, outputObject);
    }
    
    /**
	 * 
	    * @Title: queryNewDocumentaryNum
	    * @Description: 获取员工跟单在指定年度的月新增量
	    * @param inputObject
	    * @param outputObject
	    * @throws Exception    参数
	    * @return void    返回类型
	    * @throws
	 */
    @RequestMapping("/post/CrmPageController/queryNewDocumentaryNum")
    @ResponseBody
    public void queryNewDocumentaryNum(InputObject inputObject, OutputObject outputObject) throws Exception{
    	crmPageService.queryNewDocumentaryNum(inputObject, outputObject);
    }
	
}
