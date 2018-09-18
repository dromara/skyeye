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
	
	
	
}
