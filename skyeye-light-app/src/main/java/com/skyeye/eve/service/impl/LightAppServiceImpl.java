/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import cn.hutool.json.JSONUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.constans.Constants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.LightAppDao;
import com.skyeye.eve.dao.SysEveUserDao;
import com.skyeye.eve.dao.SysEveWinDragDropDao;
import com.skyeye.eve.service.LightAppService;
import com.skyeye.jedis.JedisClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: LightAppServiceImpl
 * @Description: 轻应用管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:54
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class LightAppServiceImpl implements LightAppService {

	@Autowired
	private LightAppDao lightAppDao;
	
	@Autowired
	public JedisClientService jedisClient;
	
	@Autowired
	private SysEveWinDragDropDao sysEveWinDragDropDao;
	
	@Autowired
	private SysEveUserDao sysEveUserDao;
	
	/**
	 * 
	     * @Title: queryLightAppList
	     * @Description: 获取轻应用列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryLightAppList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = lightAppDao.queryLightAppList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}
	
	/**
	 * 
	     * @Title: insertLightAppMation
	     * @Description: 新增轻应用
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertLightAppMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = lightAppDao.queryLightAppMationByName(map);
		if(bean != null && !bean.isEmpty()){
			outputObject.setreturnMessage("该应用名称已存在，不能重复添加！");
		}else{
			Map<String, Object> user = inputObject.getLogParams();
			map.put("createId", user.get("id"));
			map.put("createTime", DateUtil.getTimeAndToString());
			map.put("id", ToolUtil.getSurFaceId());
			map.put("state", "1");
			lightAppDao.insertLightAppMation(map);
		}
	}

	/**
	 * 
	     * @Title: queryLightAppMationToEditById
	     * @Description: 编辑轻应用时进行信息回显
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryLightAppMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = lightAppDao.queryLightAppMationToEditById(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editLightAppMationById
	     * @Description: 编辑轻应用信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editLightAppMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> beans = lightAppDao.queryLightAppStateById(map);
		if("1".equals(beans.get("state").toString()) || "3".equals(beans.get("state").toString())){//新建或者下线的状态可以编辑
			Map<String, Object> bean = lightAppDao.queryLightAppMationByNameAndId(map);
			if(bean == null){
				lightAppDao.editLightAppMationById(map);
			}else{
				outputObject.setreturnMessage("该应用名称已存在，不可进行二次保存");
			}
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}
	
	/**
	 * 
	     * @Title: deleteLightAppById
	     * @Description: 删除轻应用
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteLightAppById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = lightAppDao.queryLightAppStateById(map);
		if("1".equals(bean.get("state").toString()) || "3".equals(bean.get("state").toString())){//新建或者下线的状态可以删除
			lightAppDao.deleteLightAppById(map);
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}
	
	/**
	 * 
	     * @Title: editLightAppUpTypeById
	     * @Description: 轻应用上线
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editLightAppUpById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = lightAppDao.queryLightAppStateById(map);
		if("1".equals(bean.get("state").toString()) || "3".equals(bean.get("state").toString())){//新建或者下线的状态可以上线
			lightAppDao.editLightAppUpById(map);
			jedisClient.del(Constants.checkAppLightAppUpListById(bean.get("typeId").toString()));//根据类型Id删除上线轻应用的redis
			jedisClient.del(Constants.checkAppLightAppUpListById(""));//删除上线轻应用的redis
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}
	
	/**
	 * 
	     * @Title: editLightAppDownTypeById
	     * @Description: 轻应用下线
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editLightAppDownById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = lightAppDao.queryLightAppStateById(map);
		if("2".equals(bean.get("state").toString())){//上线状态可以下线
			lightAppDao.editLightAppDownById(map);
			jedisClient.del(Constants.checkAppLightAppUpListById(bean.get("typeId").toString()));//根据类型Id删除上线轻应用的redis
			jedisClient.del(Constants.checkAppLightAppUpListById(""));//删除上线轻应用的redis
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}
	
	/**
	 * 
	     * @Title: queryLightAppUpList
	     * @Description: 获取轻应用上线列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void queryLightAppUpList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans;
		if(ToolUtil.isBlank(jedisClient.get(Constants.checkAppLightAppUpListById(map.get("typeId").toString())))){
			beans = lightAppDao.queryLightAppUpList(map);
			jedisClient.set(Constants.checkAppLightAppUpListById(map.get("typeId").toString()), JSONUtil.toJsonStr(beans));
		}else{
			beans = JSONUtil.toList(jedisClient.get(Constants.checkAppLightAppUpListById(map.get("typeId").toString())), null);
		}
		if(!beans.isEmpty()){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}
	
	/**
	 * 
	     * @Title: insertLightAppToWin
	     * @Description: 添加轻应用到桌面
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertLightAppToWin(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = lightAppDao.queryLightAppMationToAddWinById(map);
		if(bean != null && !bean.isEmpty()){
			if("2".equals(bean.get("state").toString())){//上线状态可以添加
				Map<String, Object> user = inputObject.getLogParams();
				map.put("createId", user.get("id"));
				map.put("createTime", DateUtil.getTimeAndToString());
				map.put("id", ToolUtil.getSurFaceId());
				map.put("menuName", bean.get("appName"));
				map.put("titleName", bean.get("appName"));
				map.put("menuUrl", bean.get("appUrl"));
				map.put("menuIconType", 2);
				map.put("menuIconPic", bean.get("appLogo"));
				map.put("menuType", "html");
				map.put("menuParentId", 0);
				map.put("openType", 2);
				map.put("lightAppId", bean.get("id"));
				sysEveWinDragDropDao.insertWinCustomMenu(map);
				List<Map<String, Object>> deskTops = sysEveUserDao.queryDeskTopsMenuByUserId(user);//桌面菜单列表
				deskTops = ToolUtil.deskTopsTree(deskTops);
				jedisClient.set("deskTopsMation:" + user.get("id").toString(), JSONUtil.toJsonStr(deskTops));
				outputObject.setBean(map);
			}else{
				outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
			}
		}else{
			outputObject.setreturnMessage("该应用不存在，无法进行添加！");
		}
	}

}
