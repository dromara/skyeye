/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service.impl;

import cn.hutool.json.JSONUtil;
import com.skyeye.common.constans.Constants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.CompanyChatDao;
import com.skyeye.eve.service.CompanyChatService;
import com.skyeye.jedis.JedisClientService;
import com.skyeye.websocket.TalkWebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class CompanyChatServiceImpl implements CompanyChatService{
	
	@Autowired
	private CompanyChatDao companyChatDao;
	
	@Autowired
	private JedisClientService jedisService;
	
	/**
	 * 
	     * @Title: getList
	     * @Description: 获取好友列表，群聊信息，个人信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void getList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		String userId = user.get("id").toString();
		map.put("userId", userId);
		//获取个人信息
		Map<String, Object> mine = null;
		if(ToolUtil.isBlank(jedisService.get(Constants.getSysTalkUserThisMainMationById(userId)))){
			mine = companyChatDao.queryUserMineByUserId(map);
			jedisService.set(Constants.getSysTalkUserThisMainMationById(userId), JSONUtil.toJsonStr(mine));
		}else{
			mine = JSONUtil.toBean(jedisService.get(Constants.getSysTalkUserThisMainMationById(userId)), null);
		}
		
		//获取聊天组
		List<Map<String, Object>> group = null;
		if(ToolUtil.isBlank(jedisService.get(Constants.getSysTalkUserHasGroupListMationById(userId)))){
			group = companyChatDao.queryUserGroupByUserId(map);
			jedisService.set(Constants.getSysTalkUserHasGroupListMationById(userId), JSONUtil.toJsonStr(group));
		}else{
			group = JSONUtil.toList(jedisService.get(Constants.getSysTalkUserHasGroupListMationById(userId)), null);
		}
		
		//获取公司单位
		List<Map<String, Object>> companyDepartment = companyChatDao.queryCompanyDepartmentByUserId(map);
		
		//循环获取分组的人列表
		for(Map<String, Object> depart : companyDepartment){
			List<Map<String, Object>> userList = null;
			if(ToolUtil.isBlank(jedisService.get(Constants.getSysTalkGroupUserListMationById(depart.get("id").toString() + "_" + userId)))){
				userList = companyChatDao.queryDepartmentUserByDepartId(depart);
				jedisService.set(Constants.getSysTalkGroupUserListMationById(depart.get("id").toString() + "_" + userId), JSONUtil.toJsonStr(userList));
			}else{
				userList = JSONUtil.toList(jedisService.get(Constants.getSysTalkGroupUserListMationById(depart.get("id").toString() + "_" + userId)), null);
			}
			if(userList != null && !userList.isEmpty()){
				Set<String> uId = TalkWebSocket.getOnlineUserId();
				for(Map<String, Object> u : userList){
					if(uId.contains(u.get("id").toString())){
						u.put("status", "online");
					}else{
						u.put("status", "offline");
					}
				}
			}
			depart.put("list", userList);
		}
		map.clear();
		map.put("friend", companyDepartment);
		map.put("group", group);
		map.put("mine", mine);
		outputObject.setBean(map);
	}
	
	/**
	 * 
	     * @Title: editUserSignByUserId
	     * @Description: 编辑签名
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editUserSignByUserId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("userId", user.get("id"));
		companyChatDao.editUserSignByUserId(map);
	}
	
}
