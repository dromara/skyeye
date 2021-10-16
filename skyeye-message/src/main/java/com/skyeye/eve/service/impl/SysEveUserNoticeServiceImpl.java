/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.eve.dao.SysEveUserNoticeDao;
import com.skyeye.eve.service.SysEveUserNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: SysEveUserNoticeServiceImpl
 * @Description: 用户消息管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 19:12
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class SysEveUserNoticeServiceImpl implements SysEveUserNoticeService{
	
	@Autowired
	private SysEveUserNoticeDao sysEveUserNoticeDao;
	
	/**
	 * 
	     * @Title: getNoticeListByUserId
	     * @Description: 根据用户id获取用户的消息只查询8条
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void getNoticeListByUserId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("userId", user.get("id"));
		List<Map<String, Object>> beans = sysEveUserNoticeDao.getNoticeListByUserId(map);
		if(!beans.isEmpty()){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}
	
	/**
	 * 
	     * @Title: getAllNoticeListByUserId
	     * @Description: 根据用户id获取用户的消息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void getAllNoticeListByUserId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("userId", user.get("id"));
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = sysEveUserNoticeDao.getAllNoticeListByUserId(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 
	     * @Title: editNoticeMationById
	     * @Description: 用户阅读消息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void editNoticeMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("userId", user.get("id"));
		Map<String, Object> notice = sysEveUserNoticeDao.queryNoticeMationById(map);
		if(notice != null && !notice.isEmpty()){
			if("1".equals(notice.get("state").toString())){//未读状态下的消息
				map.put("readTime", DateUtil.getTimeAndToString());
				sysEveUserNoticeDao.editNoticeMationById(map);
			}
		}else{
			outputObject.setreturnMessage("您不具备该消息的操作权限。");
		}
	}

	/**
	 * 
	     * @Title: deleteNoticeMationById
	     * @Description: 用户删除消息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void deleteNoticeMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("userId", user.get("id"));
		Map<String, Object> notice = sysEveUserNoticeDao.queryNoticeMationById(map);
		if(notice != null && !notice.isEmpty()){
			map.put("delTime", DateUtil.getTimeAndToString());
			sysEveUserNoticeDao.deleteNoticeMationById(map);
		}else{
			outputObject.setreturnMessage("您不具备该消息的操作权限。");
		}
	}

	/**
	 * 
	     * @Title: editNoticeMationByIds
	     * @Description: 用户批量阅读消息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void editNoticeMationByIds(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("userId", user.get("id"));
		List<Map<String, Object>> notices = sysEveUserNoticeDao.queryNoticeNoReadMationByIds(map);//获取未读的消息信息
		String editIds = "";
		for(Map<String, Object> notice: notices){
			editIds = notice.get("id").toString() + ",";
		}
		map.put("editIds", editIds);
		map.put("readTime", DateUtil.getTimeAndToString());
		sysEveUserNoticeDao.editNoticeMationByIds(map);
	}

	/**
	 * 
	     * @Title: deleteNoticeMationByIds
	     * @Description: 用户批量删除消息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void deleteNoticeMationByIds(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("userId", user.get("id"));
		List<Map<String, Object>> notices = sysEveUserNoticeDao.queryNoticeNoDelMationByIds(map);//获取未删除的消息信息
		String editIds = "";
		for(Map<String, Object> notice: notices){
			editIds = notice.get("id").toString() + ",";
		}
		map.put("editIds", editIds);
		map.put("delTime", DateUtil.getTimeAndToString());
		sysEveUserNoticeDao.deleteNoticeMationByIds(map);
	}

	/**
	 * 
	     * @Title: queryNoticeMationById
	     * @Description: 获取消息详情
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryNoticeMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("userId", user.get("id"));
		Map<String, Object> bean = sysEveUserNoticeDao.queryNoticeDetailsMationById(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

}
