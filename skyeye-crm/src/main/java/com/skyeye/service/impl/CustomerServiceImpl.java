/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service.impl;

import cn.hutool.json.JSONUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.activiti.factory.ActivitiRunFactory;
import com.skyeye.common.constans.ActivitiConstants;
import com.skyeye.common.constans.CrmConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.dao.CustomerDao;
import com.skyeye.eve.dao.SysEnclosureDao;
import com.skyeye.eve.dao.SysEveUserStaffDao;
import com.skyeye.eve.service.SystemFoundationSettingsService;
import com.skyeye.jedis.JedisClientService;
import com.skyeye.service.CustomerService;
import com.skyeye.annotation.transaction.ActivitiAndBaseTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: CustomerServiceImpl
 * @Description: 客户信息管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:21
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerDao customerDao;
    
    @Autowired
	private JedisClientService jedisClient;
    
	@Autowired
	private SystemFoundationSettingsService systemFoundationSettingsService;
	
	@Autowired
	private SysEnclosureDao sysEnclosureDao;
	
	@Autowired
    private SysEveUserStaffDao sysEveUserStaffDao;

	/**
	 * 客户合同提交到工作流中的key
	 */
	private static final String CRM_CONTRACT_PAGE_KEY = ActivitiConstants.ActivitiObjectType.CRM_CONTRACT_PAGE.getKey();

	/**
	 * 客户商机申请到工作流中的key
	 */
	private static final String CRM_OPPORTUNITY_PAGE_KEY = ActivitiConstants.ActivitiObjectType.CRM_OPPORTUNITY_PAGE.getKey();
	
	/**
	 *
	 * @Title: queryCustomerList
	 * @Description: 获取客户管理列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	public void queryCustomerList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = customerDao.queryCustomerList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 *
	 * @Title: insertCustomerMation
	 * @Description: 添加客户信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	@Transactional(value = "transactionManager")
	public void insertCustomerMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = customerDao.queryCustomerMationByName(map);
		if(bean != null && !bean.isEmpty()){
			outputObject.setreturnMessage("customer name already exists.");
		}else{
			map.put("id", ToolUtil.getSurFaceId());
			map.put("createId", inputObject.getLogParams().get("id").toString());
			map.put("createTime", DateUtil.getTimeAndToString());
			customerDao.insertCustomerMation(map);
			jedisClient.del(CrmConstants.sysAllCustomerList());
		}
	}
	
	/**
	 *
	 * @Title: queryCustomerMationById
	 * @Description: 根据id获取客户信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	public void queryCustomerMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = customerDao.queryCustomerMationById(map);
		if(bean != null && !bean.isEmpty()){
			if(!ToolUtil.isBlank(bean.get("enclosureInfo").toString())){
				bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
			}
			
			// 获取负责人
			bean.put("chargeUser", sysEveUserStaffDao.queryUserNameList(bean.get("chargeUser").toString()));
			
			outputObject.setBean(bean);
			outputObject.settotal(1);
		}else{
			outputObject.setreturnMessage("this data is non-exits.");
		}
	}
  
	/**
	*
	* @Title: editCustomerMationById
	* @Description: 编辑客户信息
	* @param inputObject
	* @param outputObject
	* @throws Exception
	*/
	@Override
	@Transactional(value = "transactionManager")
	public void editCustomerMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = customerDao.queryCustomerMationByNameAndId(map);
		if(bean == null){
			map.put("lastUpdateId", inputObject.getLogParams().get("id").toString());
			customerDao.editCustomerMationById(map);
			jedisClient.del(CrmConstants.sysAllCustomerList());
		}else{
			outputObject.setreturnMessage("customer name already exists.");
		}
	}
	
	/**
	*
	* @Title: deleteCustomerMationById
	* @Description: 删除客户信息
	* @param inputObject
	* @param outputObject
	* @throws Exception
	*/
	@Override
	@ActivitiAndBaseTransaction(value = {"activitiTransactionManager", "transactionManager"})
	public void deleteCustomerMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		customerDao.deleteCustomerMationById(map); //删除客户
		jedisClient.del(CrmConstants.sysAllCustomerList());
		// 获取与客户相关的合同列表
		List<Map<String, Object>> beans = customerDao.queryContractListByCustomerId(map);
		for(Map<String, Object> bean : beans){
			if("1".equals(bean.get("state").toString())){
				String createId = bean.get("createId").toString();
				String processInstanceId = bean.get("processInstanceId").toString();
				// 撤销工作流
				ActivitiRunFactory.run(CRM_CONTRACT_PAGE_KEY).revokeActivi(processInstanceId, createId);
			}
		}
		customerDao.deleteContractMationByCustomerId(map); //删除客户相关的合同
		jedisClient.del(CrmConstants.sysContractListById(map.get("id").toString()));
		// 获取与客户相关的商机列表
		List<Map<String, Object>> list = customerDao.queryOpportunityListByCustomerId(map);
		for(Map<String, Object> bean : list){
			if("1".equals(bean.get("state").toString())){
				String createId = bean.get("createId").toString();
				String processInstanceId = bean.get("processInstanceId").toString();
				// 撤销工作流
				ActivitiRunFactory.run(CRM_OPPORTUNITY_PAGE_KEY).revokeActivi(processInstanceId, createId);
			}
		}
		customerDao.deleteOpportunityMationByCustomerId(map); //删除客户相关的商机
		customerDao.deleteServiceMationByCustomerId(map); //删除客户相关的售后服务
	}
	
	/**
	 *
	 * @Title: queryCustomerMationToDetail
	 * @Description: 根据id获取客户信息详情
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	public void queryCustomerMationToDetail(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = customerDao.queryCustomerMationToDetail(map);
		if(bean != null && !bean.isEmpty()){
			if(!ToolUtil.isBlank(bean.get("enclosureInfo").toString())){
				bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
			}
			outputObject.setBean(bean);
			outputObject.settotal(1);
		}else{
			outputObject.setreturnMessage("this data is non-exits.");
		}
	}
	
	/**
	 *
	 * @Title: queryCustomerListToChoose
	 * @Description: 获取客户列表用于下拉框选择
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void queryCustomerListToChoose(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = new ArrayList<>();
		if(ToolUtil.isBlank(jedisClient.get(CrmConstants.sysAllCustomerList()))){
			beans = customerDao.queryCustomerListToChoose(map);
			jedisClient.set(CrmConstants.sysAllCustomerList(), JSONUtil.toJsonStr(beans));
		}else{
			beans = JSONUtil.toList(jedisClient.get(CrmConstants.sysAllCustomerList()), null);
		}
		outputObject.setBeans(beans);
		outputObject.settotal(beans.size());
	}
	
	/**
	 *
	 * @Title: queryCustomerNumsDetail
	 * @Description: 获取客户列表中商机，合同，售后服务，跟单记录，联系人，讨论板列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	public void queryCustomerNumsDetail(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String searchType = map.get("searchType").toString();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = new ArrayList<>();
		if(searchType.equals("opportunity")){
			// 商机
			beans = customerDao.queryOpportunityNumsDetail(map);
		}else if(searchType.equals("contract")){
			// 合同
			beans = customerDao.queryContractNumsDetail(map);
		}else if(searchType.equals("service")){
			// 售后服务
			beans = customerDao.queryServiceNumsDetail(map);
		}else if(searchType.equals("documentary")){
			// 跟单记录
			beans = customerDao.queryDocumentaryNumsDetail(map);
		}else if(searchType.equals("contacts")){
			// 联系人
			beans = customerDao.queryContactsNumsDetail(map);
		}else if(searchType.equals("discuss")){
			// 讨论板
			beans = customerDao.queryDiscussNumsDetail(map);
		}
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}
	
	/**
	 *
	 * @Title: queryCustomerListTableToChoose
	 * @Description: 获取客户列表供其他内容选择
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	public void queryCustomerListTableToChoose(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = customerDao.queryCustomerListTableToChoose(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 *
	 * @Title: queryMyConscientiousList
	 * @Description: 获取我负责的客户管理列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@Override
	public void queryMyConscientiousList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("userId", inputObject.getLogParams().get("id"));
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = customerDao.queryMyConscientiousList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 *
	 * @Title: queryMyCreateList
	 * @Description: 获取我创建的客户管理列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@Override
	public void queryMyCreateList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("userId", inputObject.getLogParams().get("id"));
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = customerDao.queryMyCreateList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 *
	 * @Title: queryInternationalCustomerList
	 * @Description: 获取公海客户群列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@Override
	public void queryInternationalCustomerList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object>	map = inputObject.getParams();
		Map<String, Object> settings = systemFoundationSettingsService.getSystemFoundationSettings();
		map.putAll(settings);
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = customerDao.queryInternationalCustomerList(map);
		outputObject.setBean(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}
	
}
