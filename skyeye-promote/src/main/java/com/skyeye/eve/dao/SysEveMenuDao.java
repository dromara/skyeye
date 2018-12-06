package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;

@Repository
@Mapper
public interface SysEveMenuDao {

	public List<Map<String, Object>> querySysMenuList(Map<String, Object> map, PageBounds pageBounds) throws Exception;

	public int insertSysMenuMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysMenuMationToEditById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> querySysMenuMationBySimpleLevel(Map<String, Object> map) throws Exception;

	public int editSysMenuMationById(Map<String, Object> map) throws Exception;

	public int deleteSysMenuChildMationById(Map<String, Object> map) throws Exception;

	public int deleteSysMenuMationById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryTreeSysMenuMationBySimpleLevel(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryUseThisMenuRoleById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> querySysMenuLevelList(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysMenuAfterOrderBumByParentId(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryOldParentIdById(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysEveMenuISTopByThisId(Map<String, Object> map) throws Exception;

	public int editSysEveMenuSortTopById(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysEveMenuISLowerByThisId(Map<String, Object> map) throws Exception;

	public int editSysEveMenuSortLowerById(Map<String, Object> map) throws Exception;

}
