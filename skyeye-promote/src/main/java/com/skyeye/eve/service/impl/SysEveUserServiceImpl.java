package com.skyeye.eve.service.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.skyeye.common.constans.Constants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.SysEveUserDao;
import com.skyeye.eve.service.SysEveUserService;
import com.skyeye.jedis.JedisClientService;

@Service
public class SysEveUserServiceImpl implements SysEveUserService{
	
	@Autowired
	public SysEveUserDao sysEveUserDao;
	
	@Autowired
	public JedisClientService jedisClient;
	
	/**
	 * 
	     * @Title: querySysUserList
	     * @Description: 获取管理员用户列表
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySysUserList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = sysEveUserDao.querySysUserList(map, 
				new PageBounds(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString())));
		PageList<Map<String, Object>> beansPageList = (PageList<Map<String, Object>>)beans;
		int total = beansPageList.getPaginator().getTotalCount();
		outputObject.setBeans(beans);
		outputObject.settotal(total);
	}

	/**
	 * 
	     * @Title: editSysUserLockStateToLockById
	     * @Description: 锁定账号
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void editSysUserLockStateToLockById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sysEveUserDao.querySysUserLockStateById(map);
		if(Constants.SYS_USER_LOCK_STATE_ISUNLOCK.equals(bean.get("userLock").toString())){//未锁定
			map.put("userLock", Constants.SYS_USER_LOCK_STATE_ISLOCK);//锁定
			sysEveUserDao.editSysUserLockStateToLockById(map);
		}else{
			outputObject.setreturnMessage("该账号已被锁定，请刷新页面.");
		}
	}

	/**
	 * 
	     * @Title: editSysUserLockStateToUnLockById
	     * @Description: 解锁账号
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void editSysUserLockStateToUnLockById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sysEveUserDao.querySysUserLockStateById(map);
		if(Constants.SYS_USER_LOCK_STATE_ISLOCK.equals(bean.get("userLock").toString())){//锁定
			map.put("userLock", Constants.SYS_USER_LOCK_STATE_ISUNLOCK);//解锁
			sysEveUserDao.editSysUserLockStateToUnLockById(map);
		}else{
			outputObject.setreturnMessage("该账号已解锁，请刷新页面.");
		}
	}
	
	/**
	 * 
	     * @Title: querySysUserMationToEditById
	     * @Description: 编辑账号时获取账号信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySysUserMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sysEveUserDao.querySysUserMationToEditById(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}
	
	/**
	 * 
	     * @Title: insertSysUserMationById
	     * @Description: 创建账号
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void insertSysUserMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> userCode = sysEveUserDao.querySysUserCodeByMation(map);
		if(userCode == null){
			Map<String, Object> user = inputObject.getLogParams();
			int pwdNum = (int)(Math.random()*100);
			String password = map.get("password").toString();
			for(int i = 0; i < pwdNum; i++){
				password = ToolUtil.MD5(password);
			}
			String userId = ToolUtil.getSurFaceId();
			map.put("id", userId);
			map.put("password", password);
			map.put("pwdNum", pwdNum);
			map.put("userLock", 0);
			map.put("createId", user.get("id"));
			map.put("createTime", ToolUtil.getTimeAndToString());
			
			Map<String, Object> bean = new HashMap<>();
			bean.put("id", ToolUtil.getSurFaceId());
			bean.put("userId", userId);
			bean.put("winBgPicUrl", "/assets/winbgpic/default.jpg");
			bean.put("winLockBgPicUrl", "/assets/winlockbgpic/default.jpg");
			bean.put("winThemeColor", "31");
			bean.put("winStartMenuSize", "sm");
			bean.put("winTaskPosition", "bottom");
			bean.put("createId", user.get("id"));
			bean.put("createTime", ToolUtil.getTimeAndToString());
			
			Map<String, Object> jobBean = new HashMap<>();
			jobBean.put("id", ToolUtil.getSurFaceId());
			jobBean.put("userId", userId);
			jobBean.put("createId", user.get("id"));
			jobBean.put("createTime", ToolUtil.getTimeAndToString());
			jobBean.put("companyId", map.get("companyId"));
			jobBean.put("departmentId", map.get("departmentId"));
			jobBean.put("jobId", map.get("jobId"));
			
			sysEveUserDao.insertSysUserJobMation(jobBean);
			sysEveUserDao.insertSysUserMation(map);
			sysEveUserDao.insertSysUserInstallMation(bean);
		}else{
			outputObject.setreturnMessage("该账号已存在，请更换！");
		}
	}

	/**
	 * 
	     * @Title: editSysUserMationById
	     * @Description: 编辑账号
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void editSysUserMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		sysEveUserDao.editSysUserMationById(map);
		Map<String, Object> userJob = sysEveUserDao.querySysUserJobMationById(map);
		if(userJob == null){
			Map<String, Object> user = inputObject.getLogParams();
			Map<String, Object> jobBean = new HashMap<>();
			jobBean.put("id", ToolUtil.getSurFaceId());
			jobBean.put("userId", map.get("id"));
			jobBean.put("createId", user.get("id"));
			jobBean.put("createTime", ToolUtil.getTimeAndToString());
			jobBean.put("companyId", map.get("companyId"));
			jobBean.put("departmentId", map.get("departmentId"));
			jobBean.put("jobId", map.get("jobId"));
			sysEveUserDao.insertSysUserJobMation(jobBean);
		}else{
			sysEveUserDao.editSysUserJobMationById(map);
		}
	}

	/**
	 * 
	     * @Title: queryUserToLogin
	     * @Description: 登录
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
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
				if(Constants.SYS_USER_LOCK_STATE_ISLOCK.equals(userMation.get("userLock").toString())){
					outputObject.setreturnMessage("您的账号已被锁定，请联系管理员解除！");
				}else{
					List<Map<String, Object>> deskTops = sysEveUserDao.queryDeskTopsMenuByUserId(userMation);//桌面菜单列表
					List<Map<String, Object>> allMenu = sysEveUserDao.queryAllMenuByUserId(userMation);
					allMenu = ToolUtil.allMenuToTree(allMenu);
					deskTops = ToolUtil.deskTopsTree(deskTops);
					jedisClient.set("userMation:" + userMation.get("id").toString(), JSON.toJSONString(userMation));
					jedisClient.expire("userMation:" + userMation.get("id").toString(), 1800);//时间为30分钟
					jedisClient.set("deskTopsMation:" + userMation.get("id").toString(), JSON.toJSONString(deskTops));
					jedisClient.expire("deskTopsMation:" + userMation.get("id").toString(), 1800);//时间为30分钟
					jedisClient.set("allMenuMation:" + userMation.get("id").toString(), JSON.toJSONString(allMenu));
					jedisClient.expire("allMenuMation:" + userMation.get("id").toString(), 1800);//时间为30分钟
					outputObject.setBean(userMation);
				}
			}else{
				outputObject.setreturnMessage("密码输入错误！");
			}
		}
	}

	/**
	 * 
	     * @Title: queryUserMationBySession
	     * @Description: 从session中获取用户信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
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
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void deleteUserMationBySession(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		jedisClient.del("userMation:" + map.get("userToken").toString());
		jedisClient.del("deskTopsMation:" + map.get("userToken").toString());
		jedisClient.del("allMenuMation:" + map.get("userToken").toString());
		inputObject.removeSession();
	}

	/**
	 * 
	     * @Title: queryRoleAndBindRoleByUserId
	     * @Description: 获取角色和当前已经绑定的角色信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
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
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void editRoleIdsByUserId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		sysEveUserDao.editRoleIdsByUserId(map);
	}

	/**
	 * 
	     * @Title: queryDeskTopMenuBySession
	     * @Description: 获取桌面菜单列表
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
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
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
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
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void editUserInstallThemeColor(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("userId", user.get("id"));
		//修改reids中的用户信息
		user.put("winThemeColor", map.get("themeColor"));
		jedisClient.set("userMation:" + user.get("id").toString(), JSON.toJSONString(user));
		jedisClient.expire("userMation:" + user.get("id").toString(), 1800);//时间为30分钟
		sysEveUserDao.editUserInstallThemeColor(map);
	}

	/**
	 * 
	     * @Title: editUserInstallWinBgPic
	     * @Description: 自定义设置win背景图片
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void editUserInstallWinBgPic(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("userId", user.get("id"));
		//修改reids中的用户信息
		user.put("winBgPicUrl", map.get("winBgPicUrl"));
		jedisClient.set("userMation:" + user.get("id").toString(), JSON.toJSONString(user));
		jedisClient.expire("userMation:" + user.get("id").toString(), 1800);//时间为30分钟
		sysEveUserDao.editUserInstallWinBgPic(map);
	}

	/**
	 * 
	     * @Title: editUserInstallWinLockBgPic
	     * @Description: 自定义设置win锁屏背景图片
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void editUserInstallWinLockBgPic(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("userId", user.get("id"));
		//修改reids中的用户信息
		user.put("winLockBgPicUrl", map.get("winLockBgPicUrl"));
		jedisClient.set("userMation:" + user.get("id").toString(), JSON.toJSONString(user));
		jedisClient.expire("userMation:" + user.get("id").toString(), 1800);//时间为30分钟
		sysEveUserDao.editUserInstallWinLockBgPic(map);
	}

	/**
	 * 
	     * @Title: editUserInstallWinStartMenuSize
	     * @Description: 自定义设置win开始菜单尺寸
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void editUserInstallWinStartMenuSize(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("userId", user.get("id"));
		//修改reids中的用户信息
		user.put("winStartMenuSize", map.get("winStartMenuSize"));
		jedisClient.set("userMation:" + user.get("id").toString(), JSON.toJSONString(user));
		jedisClient.expire("userMation:" + user.get("id").toString(), 1800);//时间为30分钟
		sysEveUserDao.editUserInstallWinStartMenuSize(map);
	}

	/**
	 * 
	     * @Title: editUserInstallWinTaskPosition
	     * @Description: 自定义设置win任务栏在屏幕的位置
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void editUserInstallWinTaskPosition(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("userId", user.get("id"));
		//修改reids中的用户信息
		user.put("winTaskPosition", map.get("winTaskPosition"));
		jedisClient.set("userMation:" + user.get("id").toString(), JSON.toJSONString(user));
		jedisClient.expire("userMation:" + user.get("id").toString(), 1800);//时间为30分钟
		sysEveUserDao.editUserInstallWinTaskPosition(map);
	}

	/**
	 * 
	     * @Title: editUserPassword
	     * @Description: 修改密码
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
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

}
