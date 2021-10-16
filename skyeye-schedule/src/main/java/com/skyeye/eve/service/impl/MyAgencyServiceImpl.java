/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.constans.QuartzConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.dao.MyAgencyDao;
import com.skyeye.eve.service.MyAgencyService;
import com.skyeye.quartz.config.QuartzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class MyAgencyServiceImpl implements MyAgencyService{
	
	@Autowired
	private MyAgencyDao myAgencyDao;

	@Autowired
	private QuartzService quartzService;
	
	@Override
	public void queryMyAgencyList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("userId", user.get("id"));
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = myAgencyDao.queryMyAgencyList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 
	     * @Title: deleteMyAgencyList
	     * @Description: 取消代办提醒
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteMyAgencyList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		// 删除定时任务
		quartzService.stopAndDeleteTaskQuartz(map.get("id").toString(), QuartzConstants.QuartzMateMationJobType.MY_SCHEDULEDAY_MATION.getTaskType());
		// 修改日程信息为已结束提醒
		myAgencyDao.deleteMyAgencyList(map);
	}
	
}
