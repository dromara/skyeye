package com.skyeye.authority.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.skyeye.authority.dao.SysEveRoleDao;
import com.skyeye.authority.service.SysEveRoleService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ToolUtil;

@Service
public class SysEveRoleServiceImpl implements SysEveRoleService{
	
	@Autowired
	private SysEveRoleDao sysEveRoleDao;

	/**
	 * 
	     * @Title: querySysRoleList
	     * @Description: 获取角色列表
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySysRoleList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = sysEveRoleDao.querySysRoleList(map, 
				new PageBounds(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString())));
		PageList<Map<String, Object>> beansPageList = (PageList<Map<String, Object>>)beans;
		int total = beansPageList.getPaginator().getTotalCount();
		outputObject.setBeans(beans);
		outputObject.settotal(total);
	}

	/**
	 * 
	     * @Title: querySysRoleBandMenuList
	     * @Description: 获取角色需要绑定的菜单列表
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySysRoleBandMenuList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = sysEveRoleDao.querySysRoleBandMenuList(map);
		for(Map<String, Object> bean : beans){
			String[] str = bean.get("pId").toString().split(",");
			bean.put("pId", str[str.length-1]);
		}
		outputObject.setBeans(beans);
	}

	/**
	 * 
	     * @Title: insertSysRoleMation
	     * @Description: 新增角色
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void insertSysRoleMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		Map<String, Object> roleName = sysEveRoleDao.querySysRoleNameByName(map);
		if(roleName == null){
			String roleId = ToolUtil.getSurFaceId();
			map.put("createId", user.get("id"));
			map.put("createTime", ToolUtil.getTimeAndToString());
			map.put("id", roleId);
			List<Map<String,Object>> beans = new ArrayList<>();
			String[] menuIds = map.get("menuIds").toString().split(",");
			if(menuIds.length > 0){
				for(String str : menuIds){
					Map<String,Object> item = new HashMap<>();
					item.put("id", ToolUtil.getSurFaceId());
					item.put("roleId", roleId);
					item.put("menuId", str);
					item.put("createId", user.get("id"));
					item.put("createTime", ToolUtil.getTimeAndToString());
					beans.add(item);
				}
				sysEveRoleDao.insertSysRoleMation(map);
				sysEveRoleDao.insertSysRoleMenuMation(beans);
			}else{
				outputObject.setreturnMessage("请选择该角色即将拥有的权限！");
			}
		}else{
			outputObject.setreturnMessage("该角色名称已存在，请更换！");
		}
	}

	/**
	 * 
	     * @Title: querySysRoleMationToEditById
	     * @Description: 编辑角色时进行回显
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySysRoleMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> roleMation = sysEveRoleDao.querySysRoleMationByRoleId(map);
		List<Map<String, Object>> roleMenuId = sysEveRoleDao.querySysRoleMenuIdByRoleId(map);
		outputObject.setBean(roleMation);
		outputObject.setBeans(roleMenuId);
		outputObject.settotal(roleMenuId.size());
	}
	
}
