package com.skyeye.authority.dao;

import java.util.List;
import java.util.Map;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;

public interface SysEveUserDao {

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
	public List<Map<String, Object>> querySysUserList(Map<String, Object> map, PageBounds pageBounds) throws Exception;
	
	/**
	 * 
	     * @Title: querySysUserLockStateById
	     * @Description: 根据用户账号ID获取用户的当前锁定状态
	     * @param @param map
	     * @param @return
	     * @param @throws Exception    参数
	     * @return Map<String,Object>    返回类型
	     * @throws
	 */
	public Map<String, Object> querySysUserLockStateById(Map<String, Object> map) throws Exception;

	/**
	 * 
	     * @Title: editSysUserLockStateToLockById
	     * @Description: 锁定账号
	     * @param @param map
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	public void editSysUserLockStateToLockById(Map<String, Object> map) throws Exception;

	/**
	 * 
	     * @Title: editSysUserLockStateToUnLockById
	     * @Description: 解锁账号
	     * @param @param map
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	public void editSysUserLockStateToUnLockById(Map<String, Object> map) throws Exception;

	/**
	 * 
	     * @Title: querySysUserMationToEditById
	     * @Description: 编辑账号时获取账号信息
	     * @param @param map
	     * @param @return
	     * @param @throws Exception    参数
	     * @return Map<String,Object>    返回类型
	     * @throws
	 */
	public Map<String, Object> querySysUserMationToEditById(Map<String, Object> map) throws Exception;

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
	public void editSysUserMationById(Map<String, Object> map) throws Exception;
	
	
	
}
