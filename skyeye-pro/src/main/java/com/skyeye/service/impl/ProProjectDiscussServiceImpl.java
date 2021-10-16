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
import com.skyeye.dao.ProProjectDiscussDao;
import com.skyeye.service.ProProjectDiscussService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: ProProjectDiscussServiceImpl
 * @Description: 项目讨论板管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 23:28
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class ProProjectDiscussServiceImpl implements ProProjectDiscussService {

    @Autowired
    private ProProjectDiscussDao proProjectDiscussDao;

    /**
    *
    * @Title: queryProProjectDiscussList
    * @Description: 获取项目的讨论版的列表
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@Override
	public void queryProProjectDiscussList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = proProjectDiscussDao.queryProProjectDiscussList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

    /**
    *
    * @Title: insertProProjectDiscuss
    * @Description: 社区发帖
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@Override
	@Transactional(value="transactionManager")
	public void insertProProjectDiscuss(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("id", ToolUtil.getSurFaceId());
		map.put("createId", user.get("id"));
		map.put("createName", user.get("userName"));
		map.put("createTime", DateUtil.getTimeAndToString());
		proProjectDiscussDao.insertProProjectDiscuss(map);
	}

    /**
    *
    * @Title: insertProProjectDiscussReply
    * @Description: 新增帖子的回复贴
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@Override
	@Transactional(value="transactionManager")
	public void insertProProjectDiscussReply(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("id", ToolUtil.getSurFaceId());
		map.put("createId", user.get("id"));
		map.put("createName", user.get("userName"));
		map.put("createTime", DateUtil.getTimeAndToString());
		proProjectDiscussDao.insertProProjectDiscussReply(map);
	}

    /**
    *
    * @Title: deleteProProjectDiscussById
    * @Description: 删除讨论版
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@Override
	@Transactional(value="transactionManager")
	public void deleteProProjectDiscussById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		proProjectDiscussDao.deleteDiscussMationById(map);
	}

    /**
    *
    * @Title: queryProProjectDiscussMationById
    * @Description: 根据讨论版Id获取讨论版详情
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@Override
	public void queryProProjectDiscussMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		//获取帖子信息
		Map<String, Object> bean = proProjectDiscussDao.queryDiscussMationById(map);
		//获取所有回复贴信息
		List<Map<String, Object>> beans = proProjectDiscussDao.queryDiscussReplyByDiscussId(map);
		bean.put("replyString", beans);
		outputObject.setBean(bean);
    	outputObject.settotal(1);
	}

	/**
    *
    * @Title: queryAllDiscussList
    * @Description: 获取所有讨论板
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@Override
	public void queryAllDiscussList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = proProjectDiscussDao.queryAllDiscussList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

}
