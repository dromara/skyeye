/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.dao.SealSeServiceFeedBackDao;
import com.skyeye.eve.dao.SysEnclosureDao;
import com.skyeye.service.SealSeServiceFeedBackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public class SealSeServiceFeedBackServiceImpl implements SealSeServiceFeedBackService{
	
	@Autowired
	private SealSeServiceFeedBackDao sealSeServiceFeedBackDao;
	
	@Autowired
	private SysEnclosureDao sysEnclosureDao;

	/**
	 *
	 * @Title: queryFeedBackList
	 * @Description: 根据工单id获取情况反馈列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@Override
	public void queryFeedBackList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
        List<Map<String, Object>> beans = sealSeServiceFeedBackDao.queryFeedBackList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}
	
	/**
	 *
	 * @Title: querySealServiceMationToFeedBack
	 * @Description: 根据工单id获取反馈信息填写时的信息展示
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@Override
	public void querySealServiceMationToFeedBack(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sealSeServiceFeedBackDao.querySealServiceMationToFeedBack(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 *
	 * @Title: insertFeedBackMation
	 * @Description: 新增情况反馈
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@Override
	public void insertFeedBackMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("id", ToolUtil.getSurFaceId());
		map.put("createId", inputObject.getLogParams().get("id"));
		map.put("createTime", DateUtil.getTimeAndToString());
		sealSeServiceFeedBackDao.insertFeedBackMation(map);
	}

	/**
	 *
	 * @Title: queryFeedBackMationToEditById
	 * @Description: 编辑情况反馈时信息回显
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@Override
	public void queryFeedBackMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sealSeServiceFeedBackDao.queryFeedBackMationToEditById(map);
		if(bean != null && !ToolUtil.isBlank(bean.get("enclosureInfo").toString())){
			List<Map<String,Object>> beans = sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString());
			bean.put("enclosureInfo", beans);
		}
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 *
	 * @Title: editFeedBackMationById
	 * @Description: 编辑情况反馈
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@Override
	public void editFeedBackMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		sealSeServiceFeedBackDao.editFeedBackMationById(map);
	}

	/**
	 *
	 * @Title: deleteFeedBackMationById
	 * @Description: 删除情况反馈
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@Override
	public void deleteFeedBackMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		sealSeServiceFeedBackDao.deleteFeedBackMationById(map);
	}

	/**
	 *
	 * @Title: queryFeedBackDetailsMationById
	 * @Description: 情况反馈详情
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@Override
	public void queryFeedBackDetailsMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sealSeServiceFeedBackDao.queryFeedBackDetailsMationById(map);
		if(bean != null && !ToolUtil.isBlank(bean.get("enclosureInfo").toString())){
			List<Map<String,Object>> beans = sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString());
			bean.put("enclosureInfo", beans);
		}
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

}
