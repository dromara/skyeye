package com.skyeye.authority.dao;

import java.util.List;
import java.util.Map;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;

public interface SysEveUserDao {

	public List<Map<String, Object>> querySysUserList(Map<String, Object> map, PageBounds pageBounds) throws Exception;

	public Map<String, Object> querySysUserLockStateById(Map<String, Object> map) throws Exception;

	public void editSysUserLockStateToLockById(Map<String, Object> map) throws Exception;

	public void editSysUserLockStateToUnLockById(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysUserMationToEditById(Map<String, Object> map) throws Exception;

	public void editSysUserMationById(Map<String, Object> map) throws Exception;
	
	
	
}
