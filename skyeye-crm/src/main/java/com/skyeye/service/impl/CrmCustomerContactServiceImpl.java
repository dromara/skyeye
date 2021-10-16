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
import com.skyeye.dao.CrmCustomerContactDao;
import com.skyeye.service.CrmCustomerContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: CrmCustomerContactServiceImpl
 * @Description: 客户联系人管理
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:18
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class CrmCustomerContactServiceImpl implements CrmCustomerContactService{
	
	@Autowired
	private CrmCustomerContactDao crmCustomerContactDao;

	/**
	 *
	 * @Title: queryCustomerContactList
	 * @Description: 获取联系人列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@Override
	public void queryCustomerContactList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = crmCustomerContactDao.queryCustomerContactList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}
	
	/**
	 *
	 * @Title: insertCustomerContactMation
	 * @Description: 新增联系人信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertCustomerContactMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String userId = inputObject.getLogParams().get("id").toString();
		if("1".equals(map.get("isDefault").toString())){
			// 如果新增的联系人设置了为默认联系人，则修改之前的联系人为非默认
			crmCustomerContactDao.editCustomerContactIsNotDefaultMation(map.get("customerId").toString());
		}
		map.put("id", ToolUtil.getSurFaceId());
		map.put("createId", userId);
		map.put("createTime", DateUtil.getTimeAndToString());
		map.put("lastUpdateId", userId);
		map.put("lastUpdateTime", DateUtil.getTimeAndToString());
		crmCustomerContactDao.insertCustomerContactMation(map);
	}

	/**
	 *
	 * @Title: queryCustomerContactMationToEditById
	 * @Description: 编辑联系人信息时进行回显
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@Override
	public void queryCustomerContactMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String contactId = map.get("contactId").toString();
		Map<String, Object> contactMation = crmCustomerContactDao.queryCustomerContactMationToEditById(contactId);
		if(contactMation == null || contactMation.isEmpty()){
			outputObject.setreturnMessage("该联系人信息不存在.");
		}else{
			outputObject.setBean(contactMation);
			outputObject.settotal(1);
		}
	}

	/**
	 *
	 * @Title: editCustomerContactMation
	 * @Description: 编辑联系人信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editCustomerContactMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		if("1".equals(map.get("isDefault").toString())){
			// 如果新增的联系人设置了为默认联系人，则修改之前的联系人为非默认
			crmCustomerContactDao.editCustomerContactIsNotDefaultMation(map.get("customerId").toString());
		}
		map.put("lastUpdateId", inputObject.getLogParams().get("id"));
		map.put("lastUpdateTime", DateUtil.getTimeAndToString());
		crmCustomerContactDao.editCustomerContactMation(map);
	}

	/**
	 *
	 * @Title: deleteCustomerContactMationById
	 * @Description: 删除联系人信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteCustomerContactMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		crmCustomerContactDao.deleteCustomerContactMationById(map.get("contactId").toString());
	}
	
}
