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
import com.skyeye.eve.service.UserEmailService;

@Controller
public class UserEmailController {
	
	@Autowired
	private UserEmailService userEmailService;
	
	/**
	 * 
	     * @Title: queryEmailListByUserId
	     * @Description: 根据用户获取该用户绑定的邮箱信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/UserEmailController/queryEmailListByUserId")
	@ResponseBody
	public void queryEmailListByUserId(InputObject inputObject, OutputObject outputObject) throws Exception{
		userEmailService.queryEmailListByUserId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertEmailListByUserId
	     * @Description: 用户新增邮箱
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/UserEmailController/insertEmailListByUserId")
	@ResponseBody
	public void insertEmailListByUserId(InputObject inputObject, OutputObject outputObject) throws Exception{
		userEmailService.insertEmailListByUserId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertEmailListFromServiceByUserId
	     * @Description: 从服务器上获取收件箱里的邮件
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/UserEmailController/insertEmailListFromServiceByUserId")
	@ResponseBody
	public void insertEmailListFromServiceByUserId(InputObject inputObject, OutputObject outputObject) throws Exception{
		userEmailService.insertEmailListFromServiceByUserId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryInboxEmailListByEmailId
	     * @Description: 根据绑定邮箱id获取收件箱内容
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/UserEmailController/queryInboxEmailListByEmailId")
	@ResponseBody
	public void queryInboxEmailListByEmailId(InputObject inputObject, OutputObject outputObject) throws Exception{
		userEmailService.queryInboxEmailListByEmailId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryEmailMationByEmailId
	     * @Description: 获取邮件内容
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/UserEmailController/queryEmailMationByEmailId")
	@ResponseBody
	public void queryEmailMationByEmailId(InputObject inputObject, OutputObject outputObject) throws Exception{
		userEmailService.queryEmailMationByEmailId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertSendedEmailListFromServiceByUserId
	     * @Description: 从服务器上获取已发送邮件
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/UserEmailController/insertSendedEmailListFromServiceByUserId")
	@ResponseBody
	public void insertSendedEmailListFromServiceByUserId(InputObject inputObject, OutputObject outputObject) throws Exception{
		userEmailService.insertSendedEmailListFromServiceByUserId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySendedEmailListByEmailId
	     * @Description: 根据绑定邮箱id获取已发送邮件
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/UserEmailController/querySendedEmailListByEmailId")
	@ResponseBody
	public void querySendedEmailListByEmailId(InputObject inputObject, OutputObject outputObject) throws Exception{
		userEmailService.querySendedEmailListByEmailId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertDelsteEmailListFromServiceByUserId
	     * @Description: 从服务器上获取已删除邮件
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/UserEmailController/insertDelsteEmailListFromServiceByUserId")
	@ResponseBody
	public void insertDelsteEmailListFromServiceByUserId(InputObject inputObject, OutputObject outputObject) throws Exception{
		userEmailService.insertDelsteEmailListFromServiceByUserId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryDeleteEmailListByEmailId
	     * @Description: 根据绑定邮箱id获取已删除邮件
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/UserEmailController/queryDeleteEmailListByEmailId")
	@ResponseBody
	public void queryDeleteEmailListByEmailId(InputObject inputObject, OutputObject outputObject) throws Exception{
		userEmailService.queryDeleteEmailListByEmailId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertDraftsEmailListFromServiceByUserId
	     * @Description: 从服务器上获取草稿箱邮件
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/UserEmailController/insertDraftsEmailListFromServiceByUserId")
	@ResponseBody
	public void insertDraftsEmailListFromServiceByUserId(InputObject inputObject, OutputObject outputObject) throws Exception{
		userEmailService.insertDraftsEmailListFromServiceByUserId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryDraftsEmailListByEmailId
	     * @Description: 根据绑定邮箱id获取草稿箱邮件
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/UserEmailController/queryDraftsEmailListByEmailId")
	@ResponseBody
	public void queryDraftsEmailListByEmailId(InputObject inputObject, OutputObject outputObject) throws Exception{
		userEmailService.queryDraftsEmailListByEmailId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertToSendEmailMationByUserId
	     * @Description: 发送邮件
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/UserEmailController/insertToSendEmailMationByUserId")
	@ResponseBody
	public void insertToSendEmailMationByUserId(InputObject inputObject, OutputObject outputObject) throws Exception{
		userEmailService.insertToSendEmailMationByUserId(inputObject, outputObject);
	}
	
	/**
     * 
         * @Title: insertToDraftsEmailMationByUserId
         * @Description: 保存邮件为草稿
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @RequestMapping("/post/UserEmailController/insertToDraftsEmailMationByUserId")
    @ResponseBody
    public void insertToDraftsEmailMationByUserId(InputObject inputObject, OutputObject outputObject) throws Exception{
        userEmailService.insertToDraftsEmailMationByUserId(inputObject, outputObject);
    }
    
    /**
     * 
         * @Title: queryDraftsEmailMationToEditByUserId
         * @Description: 编辑草稿箱内容展示时回显使用
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @RequestMapping("/post/UserEmailController/queryDraftsEmailMationToEditByUserId")
    @ResponseBody
    public void queryDraftsEmailMationToEditByUserId(InputObject inputObject, OutputObject outputObject) throws Exception{
        userEmailService.queryDraftsEmailMationToEditByUserId(inputObject, outputObject);
    }
	
    /**
     * 
         * @Title: editToDraftsEmailMationByUserId
         * @Description: 草稿邮件修改
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @RequestMapping("/post/UserEmailController/editToDraftsEmailMationByUserId")
    @ResponseBody
    public void editToDraftsEmailMationByUserId(InputObject inputObject, OutputObject outputObject) throws Exception{
        userEmailService.editToDraftsEmailMationByUserId(inputObject, outputObject);
    }
	
    /**
     * 
         * @Title: insertToSendEmailMationByEmailId
         * @Description: 草稿箱邮件发送
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @RequestMapping("/post/UserEmailController/insertToSendEmailMationByEmailId")
    @ResponseBody
    public void insertToSendEmailMationByEmailId(InputObject inputObject, OutputObject outputObject) throws Exception{
        userEmailService.insertToSendEmailMationByEmailId(inputObject, outputObject);
    }
	
    /**
     * 
         * @Title: queryForwardEmailMationToEditByUserId
         * @Description: 转发时进行信息回显
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @RequestMapping("/post/UserEmailController/queryForwardEmailMationToEditByUserId")
    @ResponseBody
    public void queryForwardEmailMationToEditByUserId(InputObject inputObject, OutputObject outputObject) throws Exception{
        userEmailService.queryForwardEmailMationToEditByUserId(inputObject, outputObject);
    }
    
    /**
     * 
         * @Title: insertForwardToSendEmailMationByUserId
         * @Description: 转发邮件
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @RequestMapping("/post/UserEmailController/insertForwardToSendEmailMationByUserId")
    @ResponseBody
    public void insertForwardToSendEmailMationByUserId(InputObject inputObject, OutputObject outputObject) throws Exception{
        userEmailService.insertForwardToSendEmailMationByUserId(inputObject, outputObject);
    }
    
}
