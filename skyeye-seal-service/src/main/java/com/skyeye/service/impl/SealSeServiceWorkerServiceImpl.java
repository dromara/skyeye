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
import com.skyeye.dao.SealSeServiceWorkerDao;
import com.skyeye.service.SealSeServiceWorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: SealSeServiceWorkerServiceImpl
 * @Description: 工人信息管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 20:51
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class SealSeServiceWorkerServiceImpl implements SealSeServiceWorkerService{
	
	@Autowired
	private SealSeServiceWorkerDao sealSeServiceWorkerDao;

	/**
    *
    * @Title: queryServiceWorkerList
    * @Description: 获取所有工人信息
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@Override
	public void queryServiceWorkerList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
        List<Map<String, Object>> beans = sealSeServiceWorkerDao.queryServiceWorkerList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
    *
    * @Title: insertServiceWorkerMation
    * @Description: 新增工人资料信息
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@Override
	public void insertServiceWorkerMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sealSeServiceWorkerDao.queryServiceWorkerMationByUserId(map);
		//该账号是否加入工人管理中
		if(bean == null || bean.isEmpty()){
			map.put("id", ToolUtil.getSurFaceId());
			map.put("createId", inputObject.getLogParams().get("id"));
			map.put("createTime", DateUtil.getTimeAndToString());
			sealSeServiceWorkerDao.insertServiceWorkerMation(map);
		}else{
			outputObject.setreturnMessage("该账号已为工人帐号.");
		}
	}

	/**
    *
    * @Title: deleteServiceWorkerMationById
    * @Description: 删除工人资料信息
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@Override
	public void deleteServiceWorkerMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		sealSeServiceWorkerDao.deleteServiceWorkerMationById(map);
	}

	/**
    *
    * @Title: queryServiceWorkerMationToEditById
    * @Description: 编辑工人资料信息时进行回显
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@Override
	public void queryServiceWorkerMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sealSeServiceWorkerDao.queryServiceWorkerMationToEditById(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
    *
    * @Title: editServiceWorkerMationById
    * @Description: 编辑工人资料信息
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@Override
	public void editServiceWorkerMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		sealSeServiceWorkerDao.editServiceWorkerMationById(map);
	}

	/**
    *
    * @Title: queryServiceWorkerShowList
    * @Description: 获取所有工人信息供展示选择
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@Override
	public void queryServiceWorkerShowList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
        List<Map<String, Object>> beans = sealSeServiceWorkerDao.queryServiceWorkerShowList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
    *
    * @Title: queryServiceWorkerToMapList
    * @Description: 获取所有工人信息信息分布图
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@Override
	public void queryServiceWorkerToMapList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
        List<Map<String, Object>> beans = sealSeServiceWorkerDao.queryServiceWorkerToMapList(map);
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
	}
	
}
