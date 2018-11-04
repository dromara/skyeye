package com.skyeye.authority.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.skyeye.authority.dao.SysEveMenuDao;
import com.skyeye.authority.service.SysEveMenuService;
import com.skyeye.common.constans.Constants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ToolUtil;

@Service
public class SysEveMenuServiceImpl implements SysEveMenuService{
	
	@Autowired
	private SysEveMenuDao sysEveMenuDao;

	/**
	 * 
	     * @Title: querySysMenuList
	     * @Description: 获取菜单列表
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySysMenuList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = sysEveMenuDao.querySysMenuList(map, 
				new PageBounds(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString())));
		PageList<Map<String, Object>> beansPageList = (PageList<Map<String, Object>>)beans;
		int total = beansPageList.getPaginator().getTotalCount();
		outputObject.setBeans(beans);
		outputObject.settotal(total);
	}

	/**
	 * 
	     * @Title: insertSysMenuMation
	     * @Description: 添加菜单
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void insertSysMenuMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		if(Constants.SYS_MENU_TYPE_IS_IFRAME.equals(map.get("menuType").toString())){//iframe
			map.put("openType", Constants.SYS_MENU_OPEN_TYPE_IS_IFRAME);//1：打开iframe
		}else if(Constants.SYS_MENU_TYPE_IS_HTML.equals(map.get("menuType").toString())){//html
			map.put("openType", Constants.SYS_MENU_OPEN_TYPE_IS_HTML);//2：打开html
		}else{
			outputObject.setreturnMessage("菜单类型错误。");
			return;
		}
		if("0".equals(map.get("parentId").toString())){
			map.put("menuLevel", 0);
		}else{
			String[] str = map.get("parentId").toString().split(",");
			map.put("menuLevel", str.length);
		}
		map.put("id", ToolUtil.getSurFaceId());
		map.put("createTime", ToolUtil.getTimeAndToString());
		map.put("createId", user.get("id"));
		sysEveMenuDao.insertSysMenuMation(map);
	}
	
	/**
	 * 
	     * @Title: querySysMenuMationBySimpleLevel
	     * @Description: 查看同级菜单
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySysMenuMationBySimpleLevel(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = sysEveMenuDao.querySysMenuMationBySimpleLevel(map);
		if(!beans.isEmpty()){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}

	/**
	 * 
	     * @Title: querySysMenuMationToEditById
	     * @Description: 编辑菜单时进行信息回显
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySysMenuMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sysEveMenuDao.querySysMenuMationToEditById(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editSysMenuMationById
	     * @Description: 编辑菜单信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void editSysMenuMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		if(Constants.SYS_MENU_TYPE_IS_IFRAME.equals(map.get("menuType").toString())){//iframe
			map.put("openType", Constants.SYS_MENU_OPEN_TYPE_IS_IFRAME);//1：打开iframe
		}else if(Constants.SYS_MENU_TYPE_IS_HTML.equals(map.get("menuType").toString())){//html
			map.put("openType", Constants.SYS_MENU_OPEN_TYPE_IS_HTML);//2：打开html
		}else{
			outputObject.setreturnMessage("菜单类型错误。");
			return;
		}
		if("0".equals(map.get("parentId").toString())){
			map.put("menuLevel", 0);
		}else{
			String[] str = map.get("parentId").toString().split(",");
			map.put("menuLevel", str.length);
		}
		sysEveMenuDao.editSysMenuMationById(map);
	}

	/**
	 * 
	     * @Title: deleteSysMenuMationById
	     * @Description: 删除菜单信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void deleteSysMenuMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> menuBean = sysEveMenuDao.queryUseThisMenuRoleById(map);
		if(menuBean == null){
			//删除子菜单
			sysEveMenuDao.deleteSysMenuChildMationById(map);
			//删除自身菜单
			sysEveMenuDao.deleteSysMenuMationById(map);
		}else{
			if(Integer.parseInt(menuBean.get("roleNum").toString()) == 0){//该菜单没有角色使用
				//删除子菜单
				sysEveMenuDao.deleteSysMenuChildMationById(map);
				//删除自身菜单
				sysEveMenuDao.deleteSysMenuMationById(map);
			}else{
				outputObject.setreturnMessage("该菜单正在被一个或多个角色使用，无法删除。");
			}
		}
	}
	
	/**
	 * 
	     * @Title: queryTreeSysMenuMationBySimpleLevel
	     * @Description: 查看同级菜单
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryTreeSysMenuMationBySimpleLevel(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = sysEveMenuDao.queryTreeSysMenuMationBySimpleLevel(map);
		if(!beans.isEmpty()){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}

	/**
	 * 
	     * @Title: querySysMenuLevelList
	     * @Description: 获取菜单级别列表
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySysMenuLevelList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = sysEveMenuDao.querySysMenuLevelList(map);
		if(!beans.isEmpty()){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}

}
