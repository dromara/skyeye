/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.dao.SealSeServiceCheckWorkDao;
import com.skyeye.service.SealSeServiceCheckWorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SealSeServiceCheckWorkServiceImpl implements SealSeServiceCheckWorkService{
	
	@Autowired
	private SealSeServiceCheckWorkDao sealSeServiceCheckWorkDao;

	/**
    *
    * @Title: querySealSeServiceCheckWorkList
    * @Description: 获取所有的签到记录
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@Override
	public void querySealSeServiceCheckWorkList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
        List<Map<String, Object>> beans = sealSeServiceCheckWorkDao.querySealSeServiceCheckWorkList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}
	
}
