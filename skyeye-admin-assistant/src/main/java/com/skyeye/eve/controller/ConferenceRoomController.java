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
import com.skyeye.eve.service.ConferenceRoomService;

/**
 *
 * @ClassName: ConferenceRoomController
 * @Description: 会议室管理控制类
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/1 15:23
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Controller
public class ConferenceRoomController {
	
	@Autowired
	private ConferenceRoomService conferenceRoomService;
	
	/**
	 * 
	     * @Title: selectAllConferenceRoomMation
	     * @Description: 遍历所有的会议室
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ConferenceRoomController/selectAllConferenceRoomMation")
	@ResponseBody
	public void selectAllConferenceRoomMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		conferenceRoomService.selectAllConferenceRoomMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertConferenceRoomMation
	     * @Description: 新增会议室
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ConferenceRoomController/insertConferenceRoomMation")
	@ResponseBody
	public void insertConferenceRoomMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		conferenceRoomService.insertConferenceRoomMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteConferenceRoomById
	     * @Description: 删除会议室
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ConferenceRoomController/deleteConferenceRoomById")
	@ResponseBody
	public void deleteConferenceRoomById(InputObject inputObject, OutputObject outputObject) throws Exception{
		conferenceRoomService.deleteConferenceRoomById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: updateConferenceRoomNormalById
	     * @Description: 会议室恢复正常
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ConferenceRoomController/updateConferenceRoomNormalById")
	@ResponseBody
	public void updateConferenceRoomNormalById(InputObject inputObject, OutputObject outputObject) throws Exception{
		conferenceRoomService.updateConferenceRoomNormalById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: updateConferenceRoomRepairById
	     * @Description: 会议室维修
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ConferenceRoomController/updateConferenceRoomRepairById")
	@ResponseBody
	public void updateConferenceRoomRepairById(InputObject inputObject, OutputObject outputObject) throws Exception{
		conferenceRoomService.updateConferenceRoomRepairById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: updateConferenceRoomScrapById
	     * @Description: 会议室报废
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ConferenceRoomController/updateConferenceRoomScrapById")
	@ResponseBody
	public void updateConferenceRoomScrapById(InputObject inputObject, OutputObject outputObject) throws Exception{
		conferenceRoomService.updateConferenceRoomScrapById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: selectConferenceRoomDetailsById
	     * @Description: 会议室详情
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ConferenceRoomController/selectConferenceRoomDetailsById")
	@ResponseBody
	public void selectConferenceRoomDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception{
		conferenceRoomService.selectConferenceRoomDetailsById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryConferenceRoomMationById
	     * @Description: 查询会议室信息用以编辑
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ConferenceRoomController/queryConferenceRoomMationById")
	@ResponseBody
	public void queryConferenceRoomMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		conferenceRoomService.queryConferenceRoomMationById(inputObject, outputObject);
	}

	/**
	 * 
	     * @Title: editConferenceRoomMationById
	     * @Description: 编辑会议室
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ConferenceRoomController/editConferenceRoomMationById")
	@ResponseBody
	public void editConferenceRoomMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		conferenceRoomService.editConferenceRoomMationById(inputObject, outputObject);
	}
	
    /**
	 * 
	     * @Title: selectConferenceRoomListToChoose
	     * @Description: 获取会议室列表用于预定选择
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ConferenceRoomController/selectConferenceRoomListToChoose")
	@ResponseBody
	public void selectConferenceRoomListToChoose(InputObject inputObject, OutputObject outputObject) throws Exception{
		conferenceRoomService.selectConferenceRoomListToChoose(inputObject, outputObject);
	}
	
}
