package com.skyeye.authority.service.impl;

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
	
}
