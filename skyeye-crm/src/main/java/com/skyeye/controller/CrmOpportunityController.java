/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.service.CrmOpportunityService;

@Controller
public class CrmOpportunityController {

    @Autowired
    private CrmOpportunityService crmOpportunityService;

    /**
     *
     * @Title: queryCrmOpportunityList
     * @Description: 获取商机列表
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/CrmOpportunityController/queryCrmOpportunityList")
    @ResponseBody
    public void queryCrmOpportunityList(InputObject inputObject, OutputObject outputObject) throws Exception {
        crmOpportunityService.queryCrmOpportunityList(inputObject, outputObject);
    }
    
	/**
    *
    * @Title: insertCrmOpportunityMation
    * @Description: 新增商机
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@RequestMapping("/post/CrmOpportunityController/insertCrmOpportunityMation")
	@ResponseBody
	public void insertCrmOpportunityMation(InputObject inputObject, OutputObject outputObject) throws Exception {
	   crmOpportunityService.insertCrmOpportunityMation(inputObject, outputObject);
	}
   
	/**
	 *
	 * @Title: queryOpportunityMationToDetail
	 * @Description: 根据id获取商机信息详情
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@RequestMapping("/post/CrmOpportunityController/queryOpportunityMationToDetail")
	@ResponseBody
	public void queryOpportunityMationToDetail(InputObject inputObject, OutputObject outputObject) throws Exception {
		crmOpportunityService.queryOpportunityMationToDetail(inputObject, outputObject);
	}
    
    /**
    *
    * @Title: queryOpportunityMationById
    * @Description: 获取商机信息进行编辑回显
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@RequestMapping("/post/CrmOpportunityController/queryOpportunityMationById")
	@ResponseBody
	public void queryOpportunityMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		crmOpportunityService.queryOpportunityMationById(inputObject, outputObject);
	}

	/**
	*
	* @Title: editOpportunityMationById
	* @Description: 编辑商机信息
	* @param inputObject
	* @param outputObject
	* @throws Exception    参数
	* @return void    返回类型
	* @throws
	*/
	@RequestMapping("/post/CrmOpportunityController/editOpportunityMationById")
	@ResponseBody
	public void editOpportunityMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		crmOpportunityService.editOpportunityMationById(inputObject, outputObject);
	}
  
	/**
	*
	* @Title: deleteOpportunityMationById
	* @Description: 根据id删除商机信息
	* @param inputObject
	* @param outputObject
	* @throws Exception    参数
	* @return void    返回类型
	* @throws
	*/
	@RequestMapping("/post/CrmOpportunityController/deleteOpportunityMationById")
	@ResponseBody
	public void deleteOpportunityMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		crmOpportunityService.deleteOpportunityMationById(inputObject, outputObject);
	}
	
	/**
	*
	* @Title: queryDiscussNumsList
	* @Description: 获取商机的讨论板的列表
	* @param inputObject
	* @param outputObject
	* @throws Exception    参数
	* @return void    返回类型
	* @throws
	*/
	@RequestMapping("/post/CrmOpportunityController/queryDiscussNumsList")
	@ResponseBody
	public void queryDiscussNumsList(InputObject inputObject, OutputObject outputObject) throws Exception {
		crmOpportunityService.queryDiscussNumsList(inputObject, outputObject);
	}
	
	/**
	*
	* @Title: queryOpportunityListUseToSelect
	* @Description: 获取我的运行中的商机用于下拉框
	* @param inputObject
	* @param outputObject
	* @throws Exception    参数
	* @return void    返回类型
	* @throws
	*/
	@RequestMapping("/post/CrmOpportunityController/queryOpportunityListUseToSelect")
	@ResponseBody
	public void queryOpportunityListUseToSelect(InputObject inputObject, OutputObject outputObject) throws Exception {
		crmOpportunityService.queryOpportunityListUseToSelect(inputObject, outputObject);
	}
	
	/**
    *
    * @Title: insertOpportunityDiscussMation
    * @Description: 社区发帖
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@RequestMapping("/post/CrmOpportunityController/insertOpportunityDiscussMation")
	@ResponseBody
	public void insertOpportunityDiscussMation(InputObject inputObject, OutputObject outputObject) throws Exception {
	   crmOpportunityService.insertOpportunityDiscussMation(inputObject, outputObject);
	}
	
    /**
    *
    * @Title: queryMyCrmOpportunityList
    * @Description: 获取我的商机列表
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
   @RequestMapping("/post/CrmOpportunityController/queryMyCrmOpportunityList")
   @ResponseBody
   public void queryMyCrmOpportunityList(InputObject inputObject, OutputObject outputObject) throws Exception {
       crmOpportunityService.queryMyCrmOpportunityList(inputObject, outputObject);
   }
   
	/**
	*
	* @Title: insertDiscussReplyMation
	* @Description: 新增帖子的回复贴
	* @param inputObject
	* @param outputObject
	* @throws Exception    参数
	* @return void    返回类型
	* @throws
	*/
	@RequestMapping("/post/CrmOpportunityController/insertDiscussReplyMation")
	@ResponseBody
	public void insertDiscussReplyMation(InputObject inputObject, OutputObject outputObject) throws Exception {
	   crmOpportunityService.insertDiscussReplyMation(inputObject, outputObject);
	}
	
	/**
	*
	* @Title: deleteDiscussMationById
	* @Description: 删除讨论板
	* @param inputObject
	* @param outputObject
	* @throws Exception    参数
	* @return void    返回类型
	* @throws
	*/
	@RequestMapping("/post/CrmOpportunityController/deleteDiscussMationById")
	@ResponseBody
	public void deleteDiscussMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
	   crmOpportunityService.deleteDiscussMationById(inputObject, outputObject);
	}
	
	/**
	*
	* @Title: queryDiscussMationById
	* @Description: 获取讨论板详情
	* @param inputObject
	* @param outputObject
	* @throws Exception    参数
	* @return void    返回类型
	* @throws
	*/
	@RequestMapping("/post/CrmOpportunityController/queryDiscussMationById")
	@ResponseBody
	public void queryDiscussMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
	   crmOpportunityService.queryDiscussMationById(inputObject, outputObject);
	}
	
	/**
	*
	* @Title: editOpportunityToApprovalById
	* @Description: 根据商机Id提交审批
	* @param inputObject
	* @param outputObject
	* @throws Exception    参数
	* @return void    返回类型
	* @throws
	*/
	@RequestMapping("/post/CrmOpportunityController/editOpportunityToApprovalById")
	@ResponseBody
	public void editOpportunityToApprovalById(InputObject inputObject, OutputObject outputObject) throws Exception {
	   crmOpportunityService.editOpportunityToApprovalById(inputObject, outputObject);
	}
	
	/**
	*
	* @Title: editOpportunityToConmunicate
	* @Description: 根据商机Id初期沟通
	* @param inputObject
	* @param outputObject
	* @throws Exception    参数
	* @return void    返回类型
	* @throws
	*/
	@RequestMapping("/post/CrmOpportunityController/editOpportunityToConmunicate")
	@ResponseBody
	public void editOpportunityToConmunicate(InputObject inputObject, OutputObject outputObject) throws Exception {
	   crmOpportunityService.editOpportunityToConmunicate(inputObject, outputObject);
	}
	
	/**
	*
	* @Title: editOpportunityToQuotedPrice
	* @Description: 根据商机Id方案与报价
	* @param inputObject
	* @param outputObject
	* @throws Exception    参数
	* @return void    返回类型
	* @throws
	*/
	@RequestMapping("/post/CrmOpportunityController/editOpportunityToQuotedPrice")
	@ResponseBody
	public void editOpportunityToQuotedPrice(InputObject inputObject, OutputObject outputObject) throws Exception {
	   crmOpportunityService.editOpportunityToQuotedPrice(inputObject, outputObject);
	}
	
	/**
	*
	* @Title: editOpportunityToTender
	* @Description: 根据商机Id竞争与投标
	* @param inputObject
	* @param outputObject
	* @throws Exception    参数
	* @return void    返回类型
	* @throws
	*/
	@RequestMapping("/post/CrmOpportunityController/editOpportunityToTender")
	@ResponseBody
	public void editOpportunityToTender(InputObject inputObject, OutputObject outputObject) throws Exception {
	   crmOpportunityService.editOpportunityToTender(inputObject, outputObject);
	}
	
	/**
	*
	* @Title: editOpportunityToNegotiate
	* @Description: 根据商机Id商务谈判
	* @param inputObject
	* @param outputObject
	* @throws Exception    参数
	* @return void    返回类型
	* @throws
	*/
	@RequestMapping("/post/CrmOpportunityController/editOpportunityToNegotiate")
	@ResponseBody
	public void editOpportunityToNegotiate(InputObject inputObject, OutputObject outputObject) throws Exception {
	   crmOpportunityService.editOpportunityToNegotiate(inputObject, outputObject);
	}
	
	/**
	*
	* @Title: editOpportunityToTurnover
	* @Description: 根据商机Id成交
	* @param inputObject
	* @param outputObject
	* @throws Exception    参数
	* @return void    返回类型
	* @throws
	*/
	@RequestMapping("/post/CrmOpportunityController/editOpportunityToTurnover")
	@ResponseBody
	public void editOpportunityToTurnover(InputObject inputObject, OutputObject outputObject) throws Exception {
	   crmOpportunityService.editOpportunityToTurnover(inputObject, outputObject);
	}
	
	/**
	*
	* @Title: editOpportunityToLosingTable
	* @Description: 根据商机Id丢单
	* @param inputObject
	* @param outputObject
	* @throws Exception    参数
	* @return void    返回类型
	* @throws
	*/
	@RequestMapping("/post/CrmOpportunityController/editOpportunityToLosingTable")
	@ResponseBody
	public void editOpportunityToLosingTable(InputObject inputObject, OutputObject outputObject) throws Exception {
	   crmOpportunityService.editOpportunityToLosingTable(inputObject, outputObject);
	}
	
	/**
	*
	* @Title: editOpportunityToLayAside
	* @Description: 根据商机Id搁置
	* @param inputObject
	* @param outputObject
	* @throws Exception    参数
	* @return void    返回类型
	* @throws
	*/
	@RequestMapping("/post/CrmOpportunityController/editOpportunityToLayAside")
	@ResponseBody
	public void editOpportunityToLayAside(InputObject inputObject, OutputObject outputObject) throws Exception {
	   crmOpportunityService.editOpportunityToLayAside(inputObject, outputObject);
	}
	
	/**
	*
	* @Title: queryAllDiscussList
	* @Description: 获取所有讨论板
	* @param inputObject
	* @param outputObject
	* @throws Exception    参数
	* @return void    返回类型
	* @throws
	*/
	@RequestMapping("/post/CrmOpportunityController/queryAllDiscussList")
	@ResponseBody
	public void queryAllDiscussList(InputObject inputObject, OutputObject outputObject) throws Exception {
		crmOpportunityService.queryAllDiscussList(inputObject, outputObject);
	}
	
    /**
     * 
         * @Title: editOpportunityProcessToRevoke
         * @Description: 撤销商机审批申请
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @RequestMapping("/post/CrmOpportunityController/editOpportunityProcessToRevoke")
    @ResponseBody
    public void editOpportunityProcessToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception{
    	crmOpportunityService.editOpportunityProcessToRevoke(inputObject, outputObject);
    }

}
