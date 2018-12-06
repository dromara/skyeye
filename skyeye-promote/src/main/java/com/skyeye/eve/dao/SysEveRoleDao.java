package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;

@Repository
@Mapper
public interface SysEveRoleDao {

	public List<Map<String, Object>> querySysRoleList(Map<String, Object> map, PageBounds pageBounds) throws Exception;

	public List<Map<String, Object>> querySysRoleBandMenuList(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysRoleNameByName(Map<String, Object> map) throws Exception;

	public int insertSysRoleMation(Map<String, Object> map) throws Exception;

	public int insertSysRoleMenuMation(List<Map<String, Object>> beans) throws Exception;

	public Map<String, Object> querySysRoleMationByRoleId(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> querySysRoleMenuIdByRoleId(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryRoleNameByIdAndName(Map<String, Object> map) throws Exception;

	public int editSysRoleMationById(Map<String, Object> map) throws Exception;

	public int deleteRoleMenuByRoleId(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryUserRoleByRoleId(Map<String, Object> map) throws Exception;

	public int deleteRoleByRoleId(Map<String, Object> map) throws Exception;


}
