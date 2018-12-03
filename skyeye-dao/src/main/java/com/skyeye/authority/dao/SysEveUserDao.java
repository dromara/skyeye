package com.skyeye.authority.dao;

import java.util.List;
import java.util.Map;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;

public interface SysEveUserDao {

	public List<Map<String, Object>> querySysUserList(Map<String, Object> map, PageBounds pageBounds) throws Exception;

	public Map<String, Object> querySysUserLockStateById(Map<String, Object> map) throws Exception;

	public int editSysUserLockStateToLockById(Map<String, Object> map) throws Exception;

	public int editSysUserLockStateToUnLockById(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysUserMationToEditById(Map<String, Object> map) throws Exception;

	public int editSysUserMationById(Map<String, Object> map) throws Exception;

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

	public int insertSysUserJobMation(Map<String, Object> jobBean) throws Exception;

	public int editSysUserJobMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysUserJobMationById(Map<String, Object> map) throws Exception;
	
	
	
}
