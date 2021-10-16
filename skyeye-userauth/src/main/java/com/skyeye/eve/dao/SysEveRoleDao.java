/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: SysEveRoleDao
 * @Description: 角色管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 11:38
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface SysEveRoleDao {

	public List<Map<String, Object>> querySysRoleList(Map<String, Object> map) throws Exception;

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
	
	public List<Map<String, Object>> querySysRoleBandAppMenuList(Map<String, Object> map) throws Exception;
	
    public List<Map<String, Object>> querySysRoleAppMenuIdByRoleId(Map<String, Object> map) throws Exception;
	
    public int deleteRoleAppMenuByRoleId(Map<String, Object> map) throws Exception;
	
    public int insertSysRoleAppMenuMation(List<Map<String, Object>> beans) throws Exception;
	
    public int deleteRoleAppPointByRoleId(Map<String, Object> map) throws Exception;
	
    public int insertSysRoleAppPointMation(List<Map<String, Object>> beans) throws Exception;


}
