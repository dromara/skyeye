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
import com.skyeye.dao.SealServicePhoneDao;
import com.skyeye.eve.dao.SysEnclosureDao;
import com.skyeye.service.SealServicePhoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SealServicePhoneServiceImpl implements SealServicePhoneService{
	
	@Autowired
	private SealServicePhoneDao sealServicePhoneDao;
	
	@Autowired
	private SysEnclosureDao sysEnclosureDao;

	/**
	 * 
	     * @Title: queryNumberInEveryState
	     * @Description: 手机端查询不同状态下的工单数量
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryNumberInEveryState(InputObject inputObject, OutputObject outputObject) throws Exception {
		String userId = inputObject.getLogParams().get("id").toString();
		List<Map<String, Object>> beans = new ArrayList<>();
		//获取我的待接单、待签到、待完工、待评价四种状态的数量
		List<Map<String, Object>> mineStateNumber = sealServicePhoneDao.queryNumberInEveryStateIsMine(userId);
		if(mineStateNumber != null && !mineStateNumber.isEmpty()){
			beans.addAll(mineStateNumber);
		}
		
		//获取所有待派工、待审核、已完成三种状态的数量
		List<Map<String, Object>> allStateNumber = sealServicePhoneDao.queryNumberInEveryStateIsAll();
		if(allStateNumber != null && !allStateNumber.isEmpty()){
			beans.addAll(allStateNumber);
		}
		outputObject.setBeans(beans);
	}

	/**
	 * 
	     * @Title: insertSealSeServiceWaitToSignonMation
	     * @Description: 手机端签到
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void insertSealSeServiceWaitToSignonMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sealServicePhoneDao.querySealSeServiceState(map.get("serviceId").toString());
		if(bean != null && !bean.isEmpty()){
			if("3".equals(bean.get("state").toString())){//3.待签到可以进行签到
				map.put("id", ToolUtil.getSurFaceId());
				map.put("createTime", DateUtil.getTimeAndToString());
				map.put("registerId", inputObject.getLogParams().get("id"));
				sealServicePhoneDao.insertSealSeServiceWaitToSignonMation(map);
				sealServicePhoneDao.editSealSeServiceWaitToSignonMation(map);
			}else{
				outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
			}
		}else{
			outputObject.setreturnMessage("不存在的工单信息。");
		}
	}

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
        List<Map<String, Object>> beans = sealServicePhoneDao.queryFeedBackList(map);
        for(Map<String, Object> item: beans){
        	item.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(item.get("enclosureInfo").toString()));
        }
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
	}

	/**
	 *
	 * @Title: queryAllPartsList
	 * @Description: 获取所有配件信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@Override
	public void queryAllPartsList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(params.get("page").toString()), Integer.parseInt(params.get("limit").toString()));
        List<Map<String, Object>> beans = sealServicePhoneDao.queryAllPartsList(params);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

}
