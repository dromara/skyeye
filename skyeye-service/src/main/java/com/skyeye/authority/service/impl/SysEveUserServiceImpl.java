package com.skyeye.authority.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.skyeye.authority.dao.SysEveUserDao;
import com.skyeye.authority.service.SysEveUserService;
import com.skyeye.common.constans.Constants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.jedis.service.JedisClient;

@Service
public class SysEveUserServiceImpl implements SysEveUserService{
	
	@Autowired
	public SysEveUserDao sysEveUserDao;
	
	@Autowired
	public JedisClient jedisClient;
	
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
					jedisClient.set(userMation.get("id").toString() + ":userMation", JSON.toJSONString(userMation));
					jedisClient.expire(userMation.get("id").toString() + ":userMation", 30);
					outputObject.setLogDeskTopMenuParams(deskTops);
					outputObject.setLogParams(userMation);
					outputObject.setLogAllMenuParams(allMenu);
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
			outputObject.setreturnMessage("请重新登录");
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
	
	
	
}
