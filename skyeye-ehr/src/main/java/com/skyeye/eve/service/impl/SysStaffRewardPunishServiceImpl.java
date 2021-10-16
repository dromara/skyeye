/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.SysEnclosureDao;
import com.skyeye.eve.dao.SysStaffRewardPunishDao;
import com.skyeye.eve.service.SysStaffRewardPunishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: SysStaffRewardPunishServiceImpl
 * @Description: 员工奖惩管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:41
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class SysStaffRewardPunishServiceImpl implements SysStaffRewardPunishService{
	
	@Autowired
	private SysStaffRewardPunishDao sysStaffRewardPunishDao;
	
	@Autowired
	private SysEnclosureDao sysEnclosureDao;

	/**
	 * 
	 * Title: queryAllSysStaffRewardPunishList
	 * Description: 查询所有奖惩列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @see com.skyeye.eve.service.SysStaffRewardPunishService#queryAllSysStaffRewardPunishList(com.skyeye.common.object.InputObject, com.skyeye.common.object.OutputObject)
	 */
	@Override
	public void queryAllSysStaffRewardPunishList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(params.get("page").toString()), Integer.parseInt(params.get("limit").toString()));
        List<Map<String, Object>> beans = sysStaffRewardPunishDao.queryAllSysStaffRewardPunishList(params);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 
	 * Title: insertSysStaffRewardPunishMation
	 * Description: 员工奖惩信息录入
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @see com.skyeye.eve.service.SysStaffRewardPunishService#insertSysStaffRewardPunishMation(com.skyeye.common.object.InputObject, com.skyeye.common.object.OutputObject)
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertSysStaffRewardPunishMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("id", ToolUtil.getSurFaceId());
		map.put("createTime", DateUtil.getTimeAndToString());
		if(ToolUtil.isBlank(map.get("price").toString())){
			map.put("price", null);
		}
		sysStaffRewardPunishDao.insertSysStaffRewardPunishMation(map);
	}

	/**
	 * 
	 * Title: querySysStaffRewardPunishMationToEdit
	 * Description: 编辑员工奖惩信息时回显
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @see com.skyeye.eve.service.SysStaffRewardPunishService#querySysStaffRewardPunishMationToEdit(com.skyeye.common.object.InputObject, com.skyeye.common.object.OutputObject)
	 */
	@Override
	public void querySysStaffRewardPunishMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String id = map.get("id").toString();
		Map<String, Object> certificate = sysStaffRewardPunishDao.querySysStaffRewardPunishMationToEdit(id);
		if(certificate != null && !certificate.isEmpty()){
			// 附件
			if(certificate.containsKey("enclosure") && !ToolUtil.isBlank(certificate.get("enclosure").toString())){
				List<Map<String,Object>> beans = sysEnclosureDao.queryEnclosureInfo(certificate.get("enclosure").toString());
				certificate.put("enclosureInfo", beans);
			}
			outputObject.setBean(certificate);
			outputObject.settotal(1);
		}else{
			outputObject.setreturnMessage("该教育背景信息不存在.");
		}
	}

	/**
	 * 
	 * Title: editSysStaffRewardPunishMationById
	 * Description: 编辑员工奖惩信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @see com.skyeye.eve.service.SysStaffRewardPunishService#editSysStaffRewardPunishMationById(com.skyeye.common.object.InputObject, com.skyeye.common.object.OutputObject)
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editSysStaffRewardPunishMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String id = map.get("id").toString();
		Map<String, Object> certificate = sysStaffRewardPunishDao.querySysStaffRewardPunishMationToEdit(id);
		if(certificate != null && !certificate.isEmpty()){
			if(ToolUtil.isBlank(map.get("price").toString())){
				map.put("price", null);
			}
			sysStaffRewardPunishDao.editSysStaffRewardPunishMationById(map);
		}else{
			outputObject.setreturnMessage("该教育背景信息不存在.");
		}
	}

	/**
	 * 
	 * Title: deleteSysStaffRewardPunishMationById
	 * Description: 删除员工奖惩信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @see com.skyeye.eve.service.SysStaffRewardPunishService#deleteSysStaffRewardPunishMationById(com.skyeye.common.object.InputObject, com.skyeye.common.object.OutputObject)
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteSysStaffRewardPunishMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String id = map.get("id").toString();
		sysStaffRewardPunishDao.deleteSysStaffRewardPunishMationById(id);
	}

	/**
	 * 
	 * Title: queryPointStaffSysStaffRewardPunishList
	 * Description: 查询指定员工的奖惩列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @see com.skyeye.eve.service.SysStaffRewardPunishService#queryPointStaffSysStaffRewardPunishList(com.skyeye.common.object.InputObject, com.skyeye.common.object.OutputObject)
	 */
	@Override
	public void queryPointStaffSysStaffRewardPunishList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(params.get("page").toString()), Integer.parseInt(params.get("limit").toString()));
        List<Map<String, Object>> beans = sysStaffRewardPunishDao.queryPointStaffSysStaffRewardPunishList(params);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}
	
}
