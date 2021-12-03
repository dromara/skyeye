/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SysEveUserDao {

	public List<Map<String, Object>> querySysUserList(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysUserLockStateById(Map<String, Object> map) throws Exception;

	public int editSysUserLockStateToLockById(Map<String, Object> map) throws Exception;

	public int editSysUserLockStateToUnLockById(Map<String, Object> map) throws Exception;

	/**
	 * 重置密码
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public int editSysUserPasswordMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryMationByUserCode(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryRoleList(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryBindRoleMationByUserId(Map<String, Object> map) throws Exception;

	public int editRoleIdsByUserId(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryDeskTopsMenuByUserId(Map<String, Object> userMation) throws Exception;

	public List<Map<String, Object>> queryAllMenuByUserId(Map<String, Object> userMation) throws Exception;

	public int editUserInstallThemeColor(Map<String, Object> map) throws Exception;

	public int editUserInstallWinBgPic(Map<String, Object> map) throws Exception;

	public int editUserInstallWinLockBgPic(Map<String, Object> map) throws Exception;

	public int editUserInstallWinStartMenuSize(Map<String, Object> map) throws Exception;

	public int editUserInstallWinTaskPosition(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysUserCodeByMation(Map<String, Object> map) throws Exception;

	public int insertSysUserMation(Map<String, Object> map) throws Exception;

	public int insertSysUserInstallMation(Map<String, Object> map) throws Exception;

	public int editUserPassword(Map<String, Object> bean) throws Exception;

	public int editUserInstallVagueBgSrc(Map<String, Object> map) throws Exception;

	public int editUserInstallLoadMenuIconById(Map<String, Object> map) throws Exception;
	
	public List<Map<String, Object>> queryAuthPointsByUserId(Map<String, Object> userMation) throws Exception;
	
	Map<String, Object> queryUserDetailsMationByUserId(@Param("userId") String userId) throws Exception;

	public int editUserDetailsMationByUserId(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> querySysUserListByUserName(Map<String, Object> map) throws Exception;

	public int editSysUserStaffBindUserId(Map<String, Object> map) throws Exception;
	
	public List<Map<String, Object>> querySysDeskTopByUserId(Map<String, Object> user) throws Exception;

	public Map<String, Object> queryUserMationByUserId(Map<String, Object> userMation) throws Exception;

	public Map<String, Object> queryUserNameByProcessInstanceId(Map<String, Object> applicant) throws Exception;

	public Map<String, Object> queryUserSchoolMationByUserId(@Param("userId") String userId);
	
}
