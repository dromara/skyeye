/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service.impl;

import cn.hutool.json.JSONUtil;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.SchoolUserDao;
import com.skyeye.eve.service.SchoolUserService;
import com.skyeye.jedis.JedisClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SchoolUserServiceImpl implements SchoolUserService{
	
	@Autowired
	private SchoolUserDao schoolUserDao;
	
	@Autowired
	private JedisClientService jedisClient;

	/**
	 * 
	     * @Title: queryStuMationToLogin
	     * @Description: 手机端学生登录
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryStuMationToLogin(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> userMation = schoolUserDao.queryMationByIdCardOrNo(map);
		if(userMation == null){
			outputObject.setreturnMessage("请确保用户名输入无误！");
		}else{
			String password = ToolUtil.MD5(map.get("password").toString());
			if(password.equals(userMation.get("password").toString())){
				//学生这里以学生id作为userToken,确保不会和后台登录用户的id重复
				String userToken = userMation.get("id").toString();
				jedisClient.set("userMation:" + userToken + "-APP", JSONUtil.toJsonStr(userMation));
				outputObject.setBean(userMation);
			}else{
				outputObject.setreturnMessage("密码输入错误！");
			}
		}
	}

	/**
	 * 
	     * @Title: queryStuUserMation
	     * @Description: 手机端从session中获取学生信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryStuUserMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> user = inputObject.getLogParams();
		outputObject.setBean(user);
	}

	/**
	 * 
	     * @Title: queryStuExit
	     * @Description: 手机端注销登录
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryStuExit(InputObject inputObject, OutputObject outputObject) throws Exception {
		String userToken = inputObject.getLogParams().get("id").toString();
		jedisClient.del("userMation:" + userToken+ "-APP");
	}

	/**
	 * 
	     * @Title: queryStuUserMationDetailById
	     * @Description: 获取学生信息详细信息-我的名片
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryStuUserMationDetailById(InputObject inputObject, OutputObject outputObject) throws Exception {
		String stuId = inputObject.getLogParams().get("id").toString();
		Map<String, Object> stuMation = schoolUserDao.queryStuUserMationDetailById(stuId);
		if(stuMation != null && !stuMation.isEmpty()){
			outputObject.setBean(stuMation);
		}else{
			outputObject.setreturnMessage("信息不存在");
		}
	}

	/**
	 * 
	     * @Title: editUserPassword
	     * @Description: 修改密码-修改密码
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void editUserPassword(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		//获取当前学生信息
		String stuId = inputObject.getLogParams().get("id").toString();
		Map<String, Object> stuMation = schoolUserDao.queryStuUserMationDetailById(stuId);
		//旧密码匹配
		if(stuMation.get("password").toString().equals(ToolUtil.MD5(map.get("oldPassword").toString()))){
			String newPassword = ToolUtil.MD5(map.get("newPassword").toString());
			schoolUserDao.editUserPassword(stuId, newPassword);
		}else{
			outputObject.setreturnMessage("旧密码输入错误.");
		}
	}

}
