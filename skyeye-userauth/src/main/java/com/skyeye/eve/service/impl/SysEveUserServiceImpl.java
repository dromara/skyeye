/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import cn.hutool.json.JSONUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.cache.redis.RedisCache;
import com.skyeye.common.constans.Constants;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.SysEveUserDao;
import com.skyeye.eve.dao.SysEveUserStaffDao;
import com.skyeye.eve.service.SysEveUserService;
import com.skyeye.jedis.JedisClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class SysEveUserServiceImpl implements SysEveUserService{

	private static final Logger LOGGER = LoggerFactory.getLogger(SysEveUserServiceImpl.class);

	@Autowired
	private SysEveUserDao sysEveUserDao;

	@Autowired
	private SysEveUserStaffDao sysEveUserStaffDao;

	@Autowired
	private JedisClientService jedisClient;

	@Autowired
	private RedisCache redisCache;

	/**
	 * 账号状态
	 */
	public static enum STATE{
		SYS_USER_LOCK_STATE_ISUNLOCK(0, "未锁定"),
		SYS_USER_LOCK_STATE_ISLOCK(1, "锁定");

		private int state;
		private String name;
		STATE(int state, String name){
			this.state = state;
			this.name = name;
		}
		public int getState() {
			return state;
		}

		public String getName() {
			return name;
		}
	}

	/**
	 * 
	     * @Title: querySysUserList
	     * @Description: 获取管理员用户列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySysUserList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = sysEveUserDao.querySysUserList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 
	     * @Title: editSysUserLockStateToLockById
	     * @Description: 锁定账号
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editSysUserLockStateToLockById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sysEveUserDao.querySysUserLockStateById(map);
		int userLock = Integer.parseInt(bean.get("userLock").toString());
		if(STATE.SYS_USER_LOCK_STATE_ISUNLOCK.getState() == userLock){
			// 未锁定，设置为锁定
			map.put("userLock", STATE.SYS_USER_LOCK_STATE_ISLOCK.getState());
			sysEveUserDao.editSysUserLockStateToLockById(map);
		}else{
			outputObject.setreturnMessage("该账号已被锁定，请刷新页面.");
		}
	}

	/**
	 * 
	     * @Title: editSysUserLockStateToUnLockById
	     * @Description: 解锁账号
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editSysUserLockStateToUnLockById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sysEveUserDao.querySysUserLockStateById(map);
		int userLock = Integer.parseInt(bean.get("userLock").toString());
		if(STATE.SYS_USER_LOCK_STATE_ISLOCK.getState() == userLock){
			// 锁定，设置为解锁
			map.put("userLock", STATE.SYS_USER_LOCK_STATE_ISUNLOCK.getState());
			sysEveUserDao.editSysUserLockStateToUnLockById(map);
		}else{
			outputObject.setreturnMessage("该账号已解锁，请刷新页面.");
		}
	}
	
	/**
	 * 
	     * @Title: insertSysUserMationById
	     * @Description: 创建账号
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertSysUserMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> userCode = sysEveUserDao.querySysUserCodeByMation(map);
		if(userCode == null){
			Map<String, Object> user = inputObject.getLogParams();
			int pwdNum = (int)(Math.random() * 100);
			String password = map.get("password").toString();
			String userId = ToolUtil.getSurFaceId();
			Map<String, Object> newUser = new HashMap<>();
			newUser.put("id", userId);
			newUser.put("password", getCalcPaswword(password, pwdNum));
			newUser.put("pwdNum", pwdNum);
			newUser.put("userLock", 0);
			newUser.put("isTermOfValidity", map.get("isTermOfValidity"));
			newUser.put("createId", user.get("id"));
			newUser.put("userCode", map.get("userCode"));
			newUser.put("createTime", DateUtil.getTimeAndToString());

			//根据员工id获取员工所属部门
			Map<String, Object> staffMation = sysEveUserStaffDao.querySysUserStaffById(map);
			if(staffMation != null && !staffMation.isEmpty()){
				//删除redis中缓存的单位下的用户
				jedisClient.delKeys(Constants.getSysTalkGroupUserListMationById(staffMation.get("departmentId").toString()) + "*");
			}
			// 1.新增用户信息
			sysEveUserDao.insertSysUserMation(newUser);
			// 2.新增用户设置信息
			setUserBaseInstall(userId, user.get("id").toString());
			// 3.修改员工与账号的关系
			map.put("userId", userId);
			sysEveUserDao.editSysUserStaffBindUserId(map);
		}else{
			outputObject.setreturnMessage("该账号已存在，请更换！");
		}
	}

	private String getCalcPaswword(String password, int pwdNum) throws Exception {
		for(int i = 0; i < pwdNum; i++){
			password = ToolUtil.MD5(password);
		}
		return password;
	}

	/**
	 * 设置用户基础配置信息
	 * @param userId 用户id
	 * @param createId 创建人id
	 * @throws Exception
	 */
	private void setUserBaseInstall(String userId, String createId) throws Exception {
		Map<String, Object> bean = new HashMap<>();
		bean.put("id", ToolUtil.getSurFaceId());
		bean.put("userId", userId);
		bean.put("winBgPicUrl", "/images/upload/winbgpic/default.jpg");
		bean.put("winLockBgPicUrl", "/images/upload/winlockbgpic/default.jpg");
		bean.put("winThemeColor", "31");
		bean.put("winStartMenuSize", "sm");
		bean.put("winTaskPosition", "bottom");
		bean.put("createId", createId);
		bean.put("createTime", DateUtil.getTimeAndToString());
		sysEveUserDao.insertSysUserInstallMation(bean);
	}

	/**
	 * 
	     * @Title: editSysUserPasswordMationById
	     * @Description: 重置密码
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editSysUserPasswordMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		int pwdNum = (int)(Math.random() * 100);
		String password = map.get("password").toString();
		map.put("password", getCalcPaswword(password, pwdNum));
		map.put("pwdNum", pwdNum);
		sysEveUserDao.editSysUserPasswordMationById(map);
	}

	/**
	 * 
	     * @Title: queryUserToLogin
	     * @Description: 登录
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryUserToLogin(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> userMation = sysEveUserDao.queryMationByUserCode(map);
		if(userMation == null){
			outputObject.setreturnMessage("请确保用户名输入无误！");
		}else{
			int pwdNum = Integer.parseInt(userMation.get("pwdNum").toString());
			String password = map.get("password").toString();
			for(int i = 0; i < pwdNum; i++){
				password = ToolUtil.MD5(password);
			}
			if(password.equals(userMation.get("password").toString())){
				int userLock = Integer.parseInt(userMation.get("userLock").toString());
				if(STATE.SYS_USER_LOCK_STATE_ISLOCK.getState() == userLock){
					outputObject.setreturnMessage("您的账号已被锁定，请联系管理员解除！");
				}else{
					String userId = userMation.get("id").toString();
					List<Map<String, Object>> authPoints = getMenuAndAuthToRedis(userMation, userId);
					judgeAndGetSchoolMation(userMation, userId);
					LOGGER.info("set userMation to redis cache start.");
					jedisClient.set("userMation:" + userId, JSONUtil.toJsonStr(userMation));
					LOGGER.info("set userMation to redis cache end.");
					
					outputObject.setBean(userMation);
					outputObject.setBeans(authPoints);
				}
			}else{
				outputObject.setreturnMessage("密码输入错误！");
			}
		}
	}

	/**
	 * 获取用户菜单权限信息并存入redis缓存
	 *
	 * @param userMation
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	private List<Map<String, Object>> getMenuAndAuthToRedis(Map<String, Object> userMation, String userId) throws Exception {
		LOGGER.info("get menu and auth mation.");
		List<Map<String, Object>> deskTops = sysEveUserDao.queryDeskTopsMenuByUserId(userMation);//桌面菜单列表
		List<Map<String, Object>> allMenu = sysEveUserDao.queryAllMenuByUserId(userMation);
		List<Map<String, Object>> authPoints = sysEveUserDao.queryAuthPointsByUserId(userMation);
		allMenu = ToolUtil.allMenuToTree(allMenu);
		deskTops = ToolUtil.deskTopsTree(deskTops);

		LOGGER.info("set menu and auth mation to redis cache start.");
		jedisClient.set("deskTopsMation:" + userId, JSONUtil.toJsonStr(deskTops));
		jedisClient.set("allMenuMation:" + userId, JSONUtil.toJsonStr(allMenu));
		jedisClient.set("authPointsMation:" + userId, JSONUtil.toJsonStr(authPoints));
		LOGGER.info("set menu and auth mation to redis cache end.");
		return authPoints;
	}

	/**
	 * 处理该用户的学校权限信息
	 *
	 * @param userMation
	 * @param userId
	 */
	private void judgeAndGetSchoolMation(Map<String, Object> userMation, String userId) {
		// 处理学校权限信息
		// 当前登录帐号包含某所学校的id
		Map<String, Object> schoolMation = sysEveUserDao.queryUserSchoolMationByUserId(userId);
		if(schoolMation != null && !schoolMation.isEmpty()){
			if(schoolMation.containsKey("schoolId") && !ToolUtil.isBlank(schoolMation.get("schoolId").toString())){
				// 判断该用户的学校的数据权限-----数据权限  1.查看所有  2.查看本校
				int power = schoolMation.containsKey("schoolPower") ? Integer.parseInt(schoolMation.get("schoolPower").toString()) : 2;
				if(power == 2){
					// 将用户有权查看的学校id放入登录信息中
					userMation.put("schoolPowerId", schoolMation.get("schoolId").toString());
				}else{
					userMation.put("schoolPowerId", "all");
				}
			}
		}
	}

	/**
	 * 
	     * @Title: queryUserMationBySession
	     * @Description: 从session中获取用户信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryUserMationBySession(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> userMation = inputObject.getLogParams();
		if(userMation == null){
			outputObject.setreturnMessage("登录超时，请重新登录。");
		}else{
			outputObject.setBean(userMation);
		}
	}

	/**
	 * 
	     * @Title: deleteUserMationBySession
	     * @Description: 退出
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void deleteUserMationBySession(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		jedisClient.del("userMation:" + map.get("userToken").toString());
		jedisClient.del("deskTopsMation:" + map.get("userToken").toString());
		jedisClient.del("allMenuMation:" + map.get("userToken").toString());
		jedisClient.del("authPointsMation:" + map.get("userToken").toString());
		inputObject.removeSession();
	}

	/**
	 * 
	     * @Title: queryRoleAndBindRoleByUserId
	     * @Description: 获取角色和当前已经绑定的角色信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryRoleAndBindRoleByUserId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> roles = sysEveUserDao.queryRoleList(map);//获取角色列表
		Map<String, Object> userRole = sysEveUserDao.queryBindRoleMationByUserId(map);//获取用户绑定的角色ID串
		String[] roleIds = userRole.get("roleIds").toString().split(",");
		for(Map<String, Object> bean : roles){
			if(Arrays.asList(roleIds).contains(bean.get("id").toString())){
				bean.put("isCheck", "checked");
			}
		}
		outputObject.setBeans(roles);
		outputObject.settotal(roles.size());
	}

	/**
	 * 
	     * @Title: editRoleIdsByUserId
	     * @Description: 编辑用户绑定的角色
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editRoleIdsByUserId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		sysEveUserDao.editRoleIdsByUserId(map);
	}

	/**
	 * 
	     * @Title: queryDeskTopMenuBySession
	     * @Description: 获取桌面菜单列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryDeskTopMenuBySession(InputObject inputObject, OutputObject outputObject) throws Exception {
		List<Map<String, Object>> deskTops = inputObject.getLogDeskTopMenuParams();
		outputObject.setBeans(deskTops);
	}

	/**
	 * 
	     * @Title: queryAllMenuBySession
	     * @Description: 获取全部菜单列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryAllMenuBySession(InputObject inputObject, OutputObject outputObject) throws Exception {
		List<Map<String, Object>> deskTops = inputObject.getLogAllMenuParams();
		outputObject.setBeans(deskTops);
	}

	/**
	 * 
	     * @Title: editUserInstallThemeColor
	     * @Description: 自定义设置主题颜色
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editUserInstallThemeColor(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("userId", user.get("id"));
		//修改reids中的用户信息
		user.put("winThemeColor", map.get("themeColor"));
		jedisClient.set("userMation:" + user.get("id").toString(), JSONUtil.toJsonStr(user));
		sysEveUserDao.editUserInstallThemeColor(map);
	}

	/**
	 * 
	     * @Title: editUserInstallWinBgPic
	     * @Description: 自定义设置win背景图片
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editUserInstallWinBgPic(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("userId", user.get("id"));
		//修改reids中的用户信息
		user.put("winBgPicUrl", map.get("winBgPicUrl"));
		jedisClient.set("userMation:" + user.get("id").toString(), JSONUtil.toJsonStr(user));
		sysEveUserDao.editUserInstallWinBgPic(map);
	}

	/**
	 * 
	     * @Title: editUserInstallWinLockBgPic
	     * @Description: 自定义设置win锁屏背景图片
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editUserInstallWinLockBgPic(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("userId", user.get("id"));
		//修改reids中的用户信息
		user.put("winLockBgPicUrl", map.get("winLockBgPicUrl"));
		jedisClient.set("userMation:" + user.get("id").toString(), JSONUtil.toJsonStr(user));
		sysEveUserDao.editUserInstallWinLockBgPic(map);
	}

	/**
	 * 
	     * @Title: editUserInstallWinStartMenuSize
	     * @Description: 自定义设置win开始菜单尺寸
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editUserInstallWinStartMenuSize(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("userId", user.get("id"));
		//修改reids中的用户信息
		user.put("winStartMenuSize", map.get("winStartMenuSize"));
		jedisClient.set("userMation:" + user.get("id").toString(), JSONUtil.toJsonStr(user));
		sysEveUserDao.editUserInstallWinStartMenuSize(map);
	}

	/**
	 * 
	     * @Title: editUserInstallWinTaskPosition
	     * @Description: 自定义设置win任务栏在屏幕的位置
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editUserInstallWinTaskPosition(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("userId", user.get("id"));
		//修改reids中的用户信息
		user.put("winTaskPosition", map.get("winTaskPosition"));
		jedisClient.set("userMation:" + user.get("id").toString(), JSONUtil.toJsonStr(user));
		sysEveUserDao.editUserInstallWinTaskPosition(map);
	}

	/**
	 * 
	     * @Title: editUserPassword
	     * @Description: 修改密码
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editUserPassword(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("userCode", user.get("userCode"));
		Map<String, Object> userMation = sysEveUserDao.queryMationByUserCode(map);//根据redis中的用户信息userCode获取用户信息
		int pwdNum = Integer.parseInt(userMation.get("pwdNum").toString());
		String password = map.get("oldPassword").toString();
		for(int i = 0; i < pwdNum; i++){
			password = ToolUtil.MD5(password);
		}
		if(password.equals(userMation.get("password").toString())){//输入的旧密码数据库中的旧密码一致
			//转化新密码
			String newPassword = map.get("newPassword").toString();
			for(int i = 0; i < pwdNum; i++){
				newPassword = ToolUtil.MD5(newPassword);
			}
			Map<String, Object> bean = new HashMap<>();
			bean.put("id", user.get("id"));
			bean.put("password", newPassword);
			sysEveUserDao.editUserPassword(bean);
		}else{
			outputObject.setreturnMessage("旧密码输入错误.");
		}
	}

	/**
	 * 
	     * @Title: editUserInstallVagueBgSrc
	     * @Description: 自定义设置win雾化
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editUserInstallVagueBgSrc(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("userId", user.get("id"));
		//修改reids中的用户信息
		user.put("winBgPicVague", map.get("winBgPicVague"));
		user.put("winBgPicVagueValue", map.get("winBgPicVagueValue"));
		jedisClient.set("userMation:" + user.get("id").toString(), JSONUtil.toJsonStr(user));
		sysEveUserDao.editUserInstallVagueBgSrc(map);
	}

	/**
	 * 
	     * @Title: editUserInstallLoadMenuIconById
	     * @Description: 自定义设置窗口下面展示的是图标还是图标+文字
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editUserInstallLoadMenuIconById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("userId", user.get("id"));
		//修改reids中的用户信息
		user.put("loadBottomMenuIcon", map.get("loadBottomMenuIcon"));
		jedisClient.set("userMation:" + user.get("id").toString(), JSONUtil.toJsonStr(user));
		sysEveUserDao.editUserInstallLoadMenuIconById(map);
	}

	/**
	 * 
	     * @Title: queryUserLockByLockPwd
	     * @Description: 锁屏密码解锁
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryUserLockByLockPwd(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		int pwdNum = Integer.parseInt(user.get("pwdNum").toString());
		String password = map.get("password").toString();
		for(int i = 0; i < pwdNum; i++){
			password = ToolUtil.MD5(password);
		}
		if(!password.equals(user.get("password").toString())){
			outputObject.setreturnMessage("密码输入错误。");
		}
	}
	
	/**
	 * 
	     * @Title: queryUserDetailsMationByUserId
	     * @Description: 修改个人信息时获取数据回显
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryUserDetailsMationByUserId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> user = inputObject.getLogParams();
		Map<String, Object> bean = sysEveUserDao.queryUserDetailsMationByUserId(user.get("id").toString());
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editUserDetailsMationByUserId
	     * @Description: 修改个人信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editUserDetailsMationByUserId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("userId", user.get("id"));
		sysEveUserDao.editUserDetailsMationByUserId(map);
		jedisClient.del(Constants.getSysTalkUserThisMainMationById(user.get("id").toString()));//删除用户在redis中存储的信息
	}

	/**
	 * 
	     * @Title: querySysUserListByUserName
	     * @Description: 获取还没有分配账号的员工
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySysUserListByUserName(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = sysEveUserDao.querySysUserListByUserName(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
     * 
         * @Title: querySysDeskTopByUserId
         * @Description: 获取该用户拥有的桌面
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @Override
    public void querySysDeskTopByUserId(InputObject inputObject, OutputObject outputObject) throws Exception {
    	Map<String, Object> map = inputObject.getParams();
        Map<String, Object> user = inputObject.getLogParams();
        map.put("id", user.get("id"));
        List<Map<String, Object>> beans = sysEveUserDao.querySysDeskTopByUserId(map);
        outputObject.setBeans(beans);
    }

    @Override
	public Map<String, Object> getUserMationByUserId(String userId) {
    	String cacheKey = String.format(Locale.ROOT, "userMation-%s", userId);
    	return redisCache.getMap(cacheKey, key -> {
			try {
				return sysEveUserDao.queryUserDetailsMationByUserId(userId);
			} catch (Exception ee) {
				LOGGER.warn("get user details mation by userId error.", ee);
			}
			return null;
		}, RedisConstants.THIRTY_DAY_SECONDS);
	}

}
