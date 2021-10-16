/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import cn.hutool.json.JSONUtil;
import com.gexin.fastjson.JSON;
import com.skyeye.common.constans.ErpConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.SystemFoundationSettingsDao;
import com.skyeye.eve.service.SystemFoundationSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @ClassName: SystemFoundationSettingsServiceImpl
 * @Description: 系统基础设置服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/6/6 22:39
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class SystemFoundationSettingsServiceImpl implements SystemFoundationSettingsService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SystemFoundationSettingsServiceImpl.class);

	@Autowired
	private SystemFoundationSettingsDao systemFoundationSettingsDao;

	/**
	 * 
	     * @Title: querySystemFoundationSettingsList
	     * @Description: 获取系统基础设置
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySystemFoundationSettingsList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object>	bean = systemFoundationSettingsDao.querySystemFoundationSettingsList();
		if(bean == null){
			map = getBaseSettings();
			systemFoundationSettingsDao.insertSystemFoundationSettings(map);
			outputObject.setBean(map);
			outputObject.settotal(1);
		}else{
			judgeAndInitDefault(bean);
			outputObject.setBean(bean);
			outputObject.settotal(1);
		}
	}

	/**
	 * 
	     * @Title: editSystemFoundationSettings
	     * @Description: 编辑系统基础设置
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editSystemFoundationSettings(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		systemFoundationSettingsDao.editSystemFoundationSettings(map);
	}

	/**
	 * 获取系统配置信息
	 *
	 * @return 系统配置信息
	 * @throws Exception
	 */
	@Override
	public Map<String, Object> getSystemFoundationSettings() throws Exception {
		Map<String, Object> map = systemFoundationSettingsDao.querySystemFoundationSettingsList();
		if(map == null || map.isEmpty()){
			map = getBaseSettings();
			systemFoundationSettingsDao.insertSystemFoundationSettings(map);
		}
		judgeAndInitDefault(map);
		LOGGER.info("email server mation is: {}.", JSONUtil.toJsonStr(map));
		return map;
	}
	
	private Map<String, Object> getBaseSettings(){
		Map<String, Object> bean = new HashMap<>();
		bean.put("id", ToolUtil.getSurFaceId());
		bean.put("emailType", "imap");
		bean.put("emailReceiptServer", "imap.qq.com");
		bean.put("emailReceiptServerPort", "995");
		bean.put("emailSendServer", "smtp.qq.com");
		bean.put("emailSendServerPort", "25");
		bean.put("noDocumentaryDayNum", "30");
		bean.put("noChargeId", "2");
		bean.put("holidaysTypeJson", new ArrayList<>());
		bean.put("yearHolidaysMation", new ArrayList<>());
		bean.put("abnormalMation", new ArrayList<>());
		return bean;
	}

	/**
	 * 为其中的一些字段设置默认值
	 * @param map
	 */
	private void judgeAndInitDefault(Map<String, Object> map){
		// 企业假期类型以及扣薪信息
		if(!map.containsKey("holidaysTypeJson") || ToolUtil.isBlank(map.get("holidaysTypeJson").toString())){
			map.put("holidaysTypeJson", new ArrayList<>());
		}
		// 年假信息
		if(!map.containsKey("yearHolidaysMation") || ToolUtil.isBlank(map.get("yearHolidaysMation").toString())){
			map.put("yearHolidaysMation", new ArrayList<>());
		}
		// 异常考勤制度管理信息
		if(!map.containsKey("abnormalMation") || ToolUtil.isBlank(map.get("abnormalMation").toString())){
			map.put("abnormalMation", new ArrayList<>());
		}
		// ERP单据审核的一些设置
		if(!map.containsKey("erpExamineBasicDesign") || ToolUtil.isBlank(map.get("erpExamineBasicDesign").toString())){
			map.put("erpExamineBasicDesign", JSON.toJSONString(ErpConstants.DepoTheadSubType.getNeedExamineOrderISOne()));
		}
	}
	
}
