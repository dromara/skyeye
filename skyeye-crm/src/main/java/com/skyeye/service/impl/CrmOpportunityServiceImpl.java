/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.activiti.factory.ActivitiRunFactory;
import com.skyeye.common.constans.ActivitiConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.dao.CrmOpportunityChangeHistoryDao;
import com.skyeye.dao.CrmOpportunityDao;
import com.skyeye.eve.dao.SysEnclosureDao;
import com.skyeye.jedis.JedisClientService;
import com.skyeye.service.CrmOpportunityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: CrmOpportunityServiceImpl
 * @Description: 客户商机管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/5 13:03
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
public class CrmOpportunityServiceImpl implements CrmOpportunityService {
	
	@Autowired
	private CrmOpportunityDao crmOpportunityDao;
	
    @Autowired
	public JedisClientService jedisClient;
    
	@Autowired
	private CrmOpportunityChangeHistoryDao crmOpportunityChangeHistoryDao;
	
	@Autowired
	private SysEnclosureDao sysEnclosureDao;

	/**
	 * 客户商机申请到工作流中的key
	 */
	private static final String CRM_OPPORTUNITY_PAGE_KEY = ActivitiConstants.ActivitiObjectType.CRM_OPPORTUNITY_PAGE.getKey();

	/**
    *
    * @Title: queryCrmOpportunityList
    * @Description: 获取商机列表
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@Override
	public void queryCrmOpportunityList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = crmOpportunityDao.queryCrmOpportunityList(map);
		Map<String, Object> user = inputObject.getLogParams();
		String userId = user.get("id").toString();
		String taskType = ActivitiRunFactory.run(inputObject, outputObject, CRM_OPPORTUNITY_PAGE_KEY).getActModelTitle();
		for(Map<String, Object> bean: beans){
			bean.put("taskType", taskType);
			int state = Integer.parseInt(bean.get("state").toString());
			ActivitiRunFactory.run(inputObject, outputObject, CRM_OPPORTUNITY_PAGE_KEY).setDataStateEditRowWhenInExamine(bean, state, userId);
		}
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
    *
    * @Title: insertCrmOpportunityMation
    * @Description: 新增商机
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@Override
	@Transactional(value = "transactionManager")
	public void insertCrmOpportunityMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = crmOpportunityDao.queryOpportunityMationByName(map);
		if(bean != null && !bean.isEmpty()){
			outputObject.setreturnMessage("该商机名称已存在，不能重复添加！");
		}else{
			Map<String, Object> user = inputObject.getLogParams();
			judgeParams(map);
			String id = ToolUtil.getSurFaceId();//商机id
			map.put("id", id);
			map.put("createId", user.get("id"));
			map.put("createName", user.get("userName"));
			map.put("createTime", DateUtil.getTimeAndToString());
			crmOpportunityDao.insertCrmOpportunityMation(map);
			// 判断是否提交审批
			if("2".equals(map.get("subType").toString())){
				// 提交审批
				ActivitiRunFactory.run(inputObject, outputObject, CRM_OPPORTUNITY_PAGE_KEY).submitToActivi(id);
			}
		}
	}

	private void judgeParams(Map<String, Object> map) {
		if (ToolUtil.isBlank(map.get("estimateEndTime").toString())) {
			map.remove("estimateEndTime");
		}
		if (ToolUtil.isBlank(map.get("estimatePrice").toString())) {
			map.remove("estimatePrice");
		}
	}

	/**
	 *
	 * @Title: queryOpportunityMationToDetail
	 * @Description: 根据id获取商机信息详情
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	public void queryOpportunityMationToDetail(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String opportunityId = map.get("id").toString();
		Map<String, Object> bean = crmOpportunityDao.queryOpportunityMationToDetail(map);
		if(!ToolUtil.isBlank(bean.get("enclosureInfo").toString())){
			bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
		}
		// 状态变更历史
		bean.put("changeHistory", crmOpportunityChangeHistoryDao.queryOpportunityChangeHistoryByOpportunityId(opportunityId));
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}
	
	/**
	 *
	 * @Title: queryOpportunityMationById
	 * @Description: 获取商机信息进行编辑回显
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	public void queryOpportunityMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = crmOpportunityDao.queryOpportunityMationToEditById(map);
		List<Map<String,Object>> beans = null;
		// 集合中放入附件信息
		if(!ToolUtil.isBlank(bean.get("enclosureInfo").toString())){
			bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
		}
		// 集合中放入商机负责人信息
		beans = crmOpportunityDao.queryResponsIdInOpportunityById(map);
        bean.put("responsId", beans);
        // 集合中放入商机参与人信息
        beans = crmOpportunityDao.queryPartIdInOpportunityById(map);
        bean.put("partId", beans);
        // 集合中放入商机关注人信息
        beans = crmOpportunityDao.queryFollowIdInOpportunityById(map);
        bean.put("followId", beans);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	*
	* @Title: editOpportunityMationById
	* @Description: 编辑商机信息
	* @param inputObject
	* @param outputObject
	* @throws Exception    参数
	* @return void    返回类型
	* @throws
	*/
	@Override
	@Transactional(value = "transactionManager")
	public void editOpportunityMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = crmOpportunityDao.queryOpportunityMationByName(map);
		if(bean == null){
			judgeParams(map);
			String id = map.get("id").toString();
			crmOpportunityDao.editOpportunityMationById(map);
			// 判断是否提交审批
			if("2".equals(map.get("subType").toString())){
				// 提交审批
				ActivitiRunFactory.run(inputObject, outputObject, CRM_OPPORTUNITY_PAGE_KEY).submitToActivi(id);
			}
		}else{
			outputObject.setreturnMessage("该商机名称已存在，不可进行二次保存");
		}
	}
	
	/**
	*
	* @Title: deleteOpportunityMationById
	* @Description: 根据id删除商机信息
	* @param inputObject
	* @param outputObject
	* @throws Exception    参数
	* @return void    返回类型
	* @throws
	*/
	@Override
	@Transactional(value = "transactionManager")
	public void deleteOpportunityMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String opportunityId = map.get("id").toString();
		Map<String, Object> bean = crmOpportunityDao.queryCrmOpportunityStateById(opportunityId);//查询商机的状态
		// 草稿、审核不通过状态下可以删除
		if("0".equals(bean.get("state").toString()) || "12".equals(bean.get("state").toString())){
			// 删除商机表中的商机
			crmOpportunityDao.deleteOpportunityMationById(map);
			// 若商机工作流关联表中已经存在这条数据则删除
			crmOpportunityDao.deleteCrmOpportunityProcessById(opportunityId);
			// 删除与这条商机绑定的跟单
			crmOpportunityDao.deleteCrmDocumentaryByOpportunityId(map);
			// 删除属于这条商机的讨论版
			crmOpportunityDao.deleteDiscussByOpportunityId(map);
			// 删除属于这条商机的讨论版的回复贴
			crmOpportunityDao.deleteDiscussReplyByOpportunityId(map);
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}

	/**
	*
	* @Title: queryDiscussNumsList
	* @Description: 获取商机的讨论板的列表
	* @param inputObject
	* @param outputObject
	* @throws Exception    参数
	* @return void    返回类型
	* @throws
	*/
	@Override
	public void queryDiscussNumsList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = crmOpportunityDao.queryDiscussNumsList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
    *
    * @Title: insertOpportunityDiscussMation
    * @Description: 社区发帖
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@Override
	@Transactional(value = "transactionManager")
	public void insertOpportunityDiscussMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("id", ToolUtil.getSurFaceId());
		map.put("createId", user.get("id"));
		map.put("createName", user.get("userName"));
		map.put("createTime", DateUtil.getTimeAndToString());
		crmOpportunityDao.insertOpportunityDiscussMation(map);
	}

	/**
	*
	* @Title: queryOpportunityListUseToSelect
	* @Description: 获取我的运行中的商机用于下拉框
	* @param inputObject
	* @param outputObject
	* @throws Exception    参数
	* @return void    返回类型
	* @throws
	*/
	@Override
	public void queryOpportunityListUseToSelect(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("userId", inputObject.getLogParams().get("id"));
		List<Map<String, Object>> beans = crmOpportunityDao.queryOpportunityListUseToSelect(map);
		outputObject.setBeans(beans);
		outputObject.settotal(beans.size());
	}

	/**
    *
    * @Title: queryMyCrmOpportunityList
    * @Description: 获取我的商机列表
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@Override
	public void queryMyCrmOpportunityList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("userId", inputObject.getLogParams().get("id"));
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = crmOpportunityDao.queryMyCrmOpportunityList(map);
		Map<String, Object> user = inputObject.getLogParams();
		String userId = user.get("id").toString();
		String taskType = ActivitiRunFactory.run(inputObject, outputObject, CRM_OPPORTUNITY_PAGE_KEY).getActModelTitle();
		for(Map<String, Object> bean: beans){
			bean.put("taskType", taskType);
			int state = Integer.parseInt(bean.get("state").toString());
			ActivitiRunFactory.run(inputObject, outputObject, CRM_OPPORTUNITY_PAGE_KEY).setDataStateEditRowWhenInExamine(bean, state, userId);
		}
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	   *
	   * @Title: insertDiscussReplyMation
	   * @Description: 新增帖子的回复贴
	   * @param inputObject
	   * @param outputObject
	   * @throws Exception    参数
	   * @return void    返回类型
	   * @throws
	   */
	@Override
	@Transactional(value = "transactionManager")
	public void insertDiscussReplyMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("id", ToolUtil.getSurFaceId());
		map.put("createId", user.get("id"));
		map.put("createName", user.get("userName"));
		map.put("createTime", DateUtil.getTimeAndToString());
		crmOpportunityDao.insertDiscussReplyMation(map);
	}

	/**
	*
	* @Title: deleteDiscussReplyMation
	* @Description: 删除帖子的回复贴
	* @param inputObject
	* @param outputObject
	* @throws Exception    参数
	* @return void    返回类型
	* @throws
	*/
	@Override
	@Transactional(value = "transactionManager")
	public void deleteDiscussMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		crmOpportunityDao.deleteDiscussMationById(map);
	}

	/**
	*
	* @Title: queryDiscussMationById
	* @Description: 获取讨论板详情
	* @param inputObject
	* @param outputObject
	* @throws Exception    参数
	* @return void    返回类型
	* @throws
	*/
	@Override
	public void queryDiscussMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		// 获取帖子信息
		Map<String, Object> bean = crmOpportunityDao.queryDiscussMationById(map);
		if(bean != null && !bean.isEmpty()){
			// 获取所有回复贴信息
			List<Map<String, Object>> beans = crmOpportunityDao.queryDiscussReplyByDiscussId(map);
			bean.put("replyString", beans);
			outputObject.setBean(bean);
			outputObject.settotal(1);
		}
	}

	/**
	*
	* @Title: editOpportunityToApprovalById
	* @Description: 根据商机Id提交审批
	* @param inputObject
	* @param outputObject
	* @throws Exception    参数
	* @return void    返回类型
	* @throws
	*/
	@Override
	@Transactional(value = "transactionManager")
	public void editOpportunityToApprovalById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String opportunityId = map.get("id").toString();
		Map<String, Object> bean = crmOpportunityDao.queryCrmOpportunityStateById(opportunityId);//查询商机的状态
		if("0".equals(bean.get("state").toString()) || "12".equals(bean.get("state").toString())){
			// 草稿、审核不通过状态下可以提交审批
			ActivitiRunFactory.run(inputObject, outputObject, CRM_OPPORTUNITY_PAGE_KEY).submitToActivi(opportunityId);
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}

	/**
	*
	* @Title: editOpportunityMationByIdInProcess
	* @Description: 在工作流中全部商机编辑商机信息
	* @param inputObject
	* @param outputObject
	* @throws Exception    参数
	* @return void    返回类型
	* @throws
	*/
	@Override
	@Transactional(value = "transactionManager")
	public void editOpportunityMationByIdInProcess(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = crmOpportunityDao.queryOpportunityMationByName(map);
		if(bean == null){
			judgeParams(map);
			String id = map.get("id").toString();
			crmOpportunityDao.editOpportunityMationById(map);
			// 编辑流程表参数
			ActivitiRunFactory.run(inputObject, outputObject, CRM_OPPORTUNITY_PAGE_KEY).editApplyMationInActiviti(id);
		}else{
			outputObject.setreturnMessage("该商机名称已存在，不可进行二次保存");
		}
	}
	
	/**
	 * @throws Exception 
	 * 
	    * @Title: insertOpportunityChangeHistory
	    * @Description: 记录商机变更历史
	    * @param originalState 变更前的状态
	    * @param nowState 变更后的状态
	    * @param userId 变更人
	    * @param opportunityId 商机id
	    * @return void    返回类型
	    * @throws
	 */
	private void insertOpportunityChangeHistory(int originalState, int nowState, String userId, String opportunityId) throws Exception{
		Map<String, Object> changeHistory = new HashMap<>();
		changeHistory.put("id", ToolUtil.getSurFaceId());
		changeHistory.put("originalState", originalState);
		changeHistory.put("nowState", nowState);
		changeHistory.put("userId", userId);
		changeHistory.put("opportunityId", opportunityId);
		changeHistory.put("createTime", DateUtil.getTimeAndToString());
		crmOpportunityChangeHistoryDao.insertOpportunityChangeHistory(changeHistory);
	}
	
	/**
	*
	* @Title: editOpportunityToConmunicate
	* @Description: 根据商机Id初期沟通
	* @param inputObject
	* @param outputObject
	* @throws Exception    参数
	* @return void    返回类型
	* @throws
	*/
	@Override
	@Transactional(value = "transactionManager")
	public void editOpportunityToConmunicate(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String opportunityId = map.get("id").toString();
		Map<String, Object> bean = crmOpportunityDao.queryCrmOpportunityStateById(opportunityId);//查询商机的状态
		int originalState = Integer.parseInt(bean.get("state").toString());
		//审核通过、方案与报价、竞争与投标、商务谈判、搁置状态下可以初期沟通
		if(11 == originalState || 3 == originalState || 4 == originalState || 5 == originalState || 8 == originalState){
			crmOpportunityDao.editOpportunityState(opportunityId, 2);
			insertOpportunityChangeHistory(originalState, 2, inputObject.getLogParams().get("id").toString(), opportunityId);
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}
	
	/**
	*
	* @Title: editOpportunityToQuotedPrice
	* @Description: 根据商机Id方案与报价
	* @param inputObject
	* @param outputObject
	* @throws Exception    参数
	* @return void    返回类型
	* @throws
	*/
	@Override
	@Transactional(value = "transactionManager")
	public void editOpportunityToQuotedPrice(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String opportunityId = map.get("id").toString();
		Map<String, Object> bean = crmOpportunityDao.queryCrmOpportunityStateById(opportunityId);//查询商机的状态
		int originalState = Integer.parseInt(bean.get("state").toString());
		//审核通过、初期沟通、竞争与投标、商务谈判状态下可以方案与报价
		if(11 == originalState || 2 == originalState || 4 == originalState || 5 == originalState){
			crmOpportunityDao.editOpportunityState(opportunityId, 3);
			insertOpportunityChangeHistory(originalState, 3, inputObject.getLogParams().get("id").toString(), opportunityId);
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}
	
	/**
	*
	* @Title: editOpportunityToTender
	* @Description: 根据商机Id竞争与投标
	* @param inputObject
	* @param outputObject
	* @throws Exception    参数
	* @return void    返回类型
	* @throws
	*/
	@Override
	@Transactional(value = "transactionManager")
	public void editOpportunityToTender(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String opportunityId = map.get("id").toString();
		Map<String, Object> bean = crmOpportunityDao.queryCrmOpportunityStateById(opportunityId);//查询商机的状态
		int originalState = Integer.parseInt(bean.get("state").toString());
		//审核通过、初期沟通、方案与报价、商务谈判状态下可以竞争与投标
		if(11 == originalState || 2 == originalState || 3 == originalState || 5 == originalState){
			crmOpportunityDao.editOpportunityState(opportunityId, 4);
			insertOpportunityChangeHistory(originalState, 4, inputObject.getLogParams().get("id").toString(), opportunityId);
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}
	
	/**
	*
	* @Title: editOpportunityToNegotiate
	* @Description: 根据商机Id商务谈判
	* @param inputObject
	* @param outputObject
	* @throws Exception    参数
	* @return void    返回类型
	* @throws
	*/
	@Override
	@Transactional(value = "transactionManager")
	public void editOpportunityToNegotiate(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String opportunityId = map.get("id").toString();
		Map<String, Object> bean = crmOpportunityDao.queryCrmOpportunityStateById(opportunityId);//查询商机的状态
		int originalState = Integer.parseInt(bean.get("state").toString());
		//审核通过、初期沟通、方案与报价、竞争与投标状态下可以商务谈判
		if(11 == originalState || 2 == originalState || 3 == originalState || 4 == originalState){
			crmOpportunityDao.editOpportunityState(opportunityId, 5);
			insertOpportunityChangeHistory(originalState, 5, inputObject.getLogParams().get("id").toString(), opportunityId);
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}
	
	/**
	*
	* @Title: editOpportunityToTurnover
	* @Description: 根据商机Id成交
	* @param inputObject
	* @param outputObject
	* @throws Exception    参数
	* @return void    返回类型
	* @throws
	*/
	@Override
	@Transactional(value = "transactionManager")
	public void editOpportunityToTurnover(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String opportunityId = map.get("id").toString();
		Map<String, Object> bean = crmOpportunityDao.queryCrmOpportunityStateById(opportunityId);//查询商机的状态
		int originalState = Integer.parseInt(bean.get("state").toString());
		//审核通过、初期沟通、方案与报价、竞争与投标、商务谈判状态下可以成交
		if(11 == originalState || 2 == originalState || 3 == originalState || 4 == originalState || 5 == originalState){
			crmOpportunityDao.editOpportunityState(opportunityId, 6);
			insertOpportunityChangeHistory(originalState, 6, inputObject.getLogParams().get("id").toString(), opportunityId);
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}
	
	/**
	*
	* @Title: editOpportunityToLosingTable
	* @Description: 根据商机Id丢单
	* @param inputObject
	* @param outputObject
	* @throws Exception    参数
	* @return void    返回类型
	* @throws
	*/
	@Override
	@Transactional(value = "transactionManager")
	public void editOpportunityToLosingTable(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String opportunityId = map.get("id").toString();
		Map<String, Object> bean = crmOpportunityDao.queryCrmOpportunityStateById(opportunityId);//查询商机的状态
		int originalState = Integer.parseInt(bean.get("state").toString());
		//审核通过、初期沟通、方案与报价、竞争与投标、商务谈判、搁置状态下可以丢单
		if(11 == originalState || 2 == originalState || 3 == originalState || 4 == originalState || 5 == originalState || 8 == originalState){
			crmOpportunityDao.editOpportunityState(opportunityId, 7);
			insertOpportunityChangeHistory(originalState, 7, inputObject.getLogParams().get("id").toString(), opportunityId);
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}
	
	/**
	*
	* @Title: editOpportunityToLayAside
	* @Description: 根据商机Id搁置
	* @param inputObject
	* @param outputObject
	* @throws Exception    参数
	* @return void    返回类型
	* @throws
	*/
	@Override
	@Transactional(value = "transactionManager")
	public void editOpportunityToLayAside(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String opportunityId = map.get("id").toString();
		Map<String, Object> bean = crmOpportunityDao.queryCrmOpportunityStateById(opportunityId);//查询商机的状态
		int originalState = Integer.parseInt(bean.get("state").toString());
		//审核通过、初期沟通、方案与报价、竞争与投标、商务谈判状态下可以搁置
		if(11 == originalState || 2 == originalState || 3 == originalState || 4 == originalState || 5 == originalState){
			crmOpportunityDao.editOpportunityState(opportunityId, 8);
			insertOpportunityChangeHistory(originalState, 8, inputObject.getLogParams().get("id").toString(), opportunityId);
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
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
		List<Map<String, Object>> beans = crmOpportunityDao.queryAllDiscussList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

    /**
     * 
         * @Title: editOpportunityProcessToRevoke
         * @Description: 撤销商机审批申请
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
	@Override
	@Transactional(value = "transactionManager")
	public void editOpportunityProcessToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception {
		ActivitiRunFactory.run(inputObject, outputObject, CRM_OPPORTUNITY_PAGE_KEY).revokeActivi();
	}

}
