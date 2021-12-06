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
import com.skyeye.dao.CrmContractDao;
import com.skyeye.eve.dao.SysEnclosureDao;
import com.skyeye.eve.dao.SysEveUserStaffDao;
import com.skyeye.jedis.JedisClientService;
import com.skyeye.service.CrmContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: CrmContractServiceImpl
 * @Description: 项目合同管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/5 13:05
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
public class CrmContractServiceImpl implements CrmContractService {

    @Autowired
    private CrmContractDao crmContractDao;
    
    @Autowired
	private JedisClientService jedisClient;
    
	@Autowired
	private SysEnclosureDao sysEnclosureDao;
	
	@Autowired
    private SysEveUserStaffDao sysEveUserStaffDao;

	/**
	 * 客户合同提交到工作流中的key
	 */
	private static final String CRM_CONTRACT_PAGE_KEY = ActivitiConstants.ActivitiObjectType.CRM_CONTRACT_PAGE.getKey();

	/**
	 *
	 * @Title: queryCrmContractList
	 * @Description: 获取全部合同管理列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	public void queryCrmContractList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = crmContractDao.queryCrmContractList(map);
		String taskType = ActivitiRunFactory.run(inputObject, outputObject, CRM_CONTRACT_PAGE_KEY).getActModelTitle();
		for (Map<String, Object> bean : beans) {
			bean.put("taskType", taskType);
		}
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}
	
	/**
	 *
	 * @Title: queryMyCrmContractList
	 * @Description: 获取我的合同管理列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	public void queryMyCrmContractList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		String userId = user.get("id").toString();
		map.put("userId", userId);
		String taskType = ActivitiRunFactory.run(inputObject, outputObject, CRM_CONTRACT_PAGE_KEY).getActModelTitle();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = crmContractDao.queryMyCrmContractList(map);
		for (Map<String, Object> bean : beans){
			bean.put("taskType", taskType);
			int state = Integer.parseInt(bean.get("state").toString());
			if(0 == state || 12 == state || 4 == state){
				// 草稿、审核未通过、已撤销
				bean.put("editRow", "1");// 可编辑
			}else if (1 == state) {
				// 审核中
				ActivitiRunFactory.run(inputObject, outputObject, CRM_CONTRACT_PAGE_KEY).setDataStateEditRowWhenInExamine(bean, state, userId);
			} else {
				bean.put("editRow", "-1");
			}
		}
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}
	
	/**
	 *
	 * @Title: queryDepartmentListToChoose
	 * @Description: 获取部门列表用于下拉框选择
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	public void queryDepartmentListToChoose(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("userId", user.get("id"));
		List<Map<String, Object>> list = crmContractDao.queryDepartmentListToChoose(map);
		outputObject.setBeans(list);
		outputObject.settotal(list.size());
	}
	
	/**
	 *
	 * @Title: insertCrmContractMation
	 * @Description: 新增合同信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	@Transactional(value = "transactionManager")
	public void insertCrmContractMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		judgeParams(map);
		List<Map<String, Object>> beans = crmContractDao.queryCrmContractMationByNameAndNum(map);
		if(beans != null && beans.size() > 0){
			outputObject.setreturnMessage("合同名称或编号已存在！");
		}else{
			Map<String, Object> user = inputObject.getLogParams();
			String id = ToolUtil.getSurFaceId();
			map.put("id", id);
			map.put("state", 0);
			map.put("createId", user.get("id"));
			map.put("createName", user.get("userName"));
			map.put("createTime", DateUtil.getTimeAndToString());
			crmContractDao.insertCrmContractMation(map);
			jedisClient.del(CrmConstants.sysContractListById(map.get("customerId").toString()));
			// 判断是否提交审批
			if("2".equals(map.get("subType").toString())){
				// 提交审批
				ActivitiRunFactory.run(inputObject, outputObject, CRM_CONTRACT_PAGE_KEY).submitToActivi(id, ActivitiConstants.APPROVAL_ID);
			}
		}
	}

	private void judgeParams(Map<String, Object> map) {
		if(ToolUtil.isBlank(map.get("effectTime").toString())){
			map.remove("effectTime");
		}
		if(ToolUtil.isBlank(map.get("serviceEndTime").toString())){
			map.remove("serviceEndTime");
		}
		if(ToolUtil.isBlank(map.get("price").toString())){
			map.remove("price");
		}
	}

	/**
	 *
	 * @Title: queryCrmContractMationToDetail
	 * @Description: 根据id获取合同信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	public void queryCrmContractMationToDetail(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String id = map.get("id").toString();
		Map<String, Object> bean = crmContractDao.queryCrmContractMationToDetail(id);
		if(!ToolUtil.isBlank(bean.get("enclosureInfo").toString())){
			List<Map<String,Object>> beans = sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString());
			bean.put("enclosureInfo", beans);
		}
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}
	
	/**
	 *
	 * @Title: queryCrmContractMationById
	 * @Description: 获取合同信息进行回显
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	public void queryCrmContractMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = crmContractDao.queryCrmContractMationById(map);
		if(!ToolUtil.isBlank(bean.get("enclosureInfo").toString())){
			bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
		}
		// 查询关联人员
		bean.put("relationUserId", sysEveUserStaffDao.queryUserNameList(bean.get("relationUserId").toString()));
		Map<String, Object> user = inputObject.getLogParams();
		int state = Integer.parseInt(bean.get("state").toString());
		if (0 == state || 12 == state || 4 == state){
			// 草稿、审核未通过、已撤销
			bean.put("editRow", "1");// 可编辑
		}else if (1 == state) {
			// 审核中
			ActivitiRunFactory.run(inputObject, outputObject, CRM_CONTRACT_PAGE_KEY).setDataStateEditRowWhenInExamine(bean, state, user.get("id").toString());
		} else {
			bean.put("editRow", "-1");
		}
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}
	
	/**
	*
	* @Title: editCrmContractMationById
	* @Description: 编辑合同信息
	* @param inputObject
	* @param outputObject
	* @throws Exception
	*/
	@Override
	@Transactional(value = "transactionManager")
	public void editCrmContractMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		judgeParams(map);
		List<Map<String, Object>> beans = crmContractDao.queryCrmContractMationByNameAndId(map);
		if(beans != null && beans.size() > 0){
			outputObject.setreturnMessage("合同名称或编号已存在！");
		}else{
			String id = map.get("id").toString();
			crmContractDao.editCrmContractMationById(map);
			jedisClient.del(CrmConstants.sysContractListById(map.get("customerId").toString()));
			// 判断是否提交审批
			if("2".equals(map.get("subType").toString())){
				// 提交审批
				ActivitiRunFactory.run(inputObject, outputObject, CRM_CONTRACT_PAGE_KEY).submitToActivi(id, ActivitiConstants.APPROVAL_ID);
			}
		}
	}
	
	/**
	*
	* @Title: editCrmContractMationToSave
	* @Description: 编辑合同信息(已提交审核)
	* @param inputObject
	* @param outputObject
	* @throws Exception
	*/
	@Override
	@Transactional(value = "transactionManager")
	public void editCrmContractMationToSave(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		judgeParams(map);
		List<Map<String, Object>> list = crmContractDao.queryCrmContractMationByNameAndId(map);
		if(list != null && list.size() > 0){
			outputObject.setreturnMessage("合同名称或编号已存在！");
		}else{
			String id = map.get("id").toString();
			crmContractDao.editCrmContractMationById(map);
			jedisClient.del(CrmConstants.sysContractListById(map.get("customerId").toString()));
			// 编辑工作流中的数据
			ActivitiRunFactory.run(inputObject, outputObject, CRM_CONTRACT_PAGE_KEY).editApplyMationInActiviti(id);
		}
	}
	
	/**
	 *
	 * @Title: queryCrmContractListToChoose
	 * @Description: 根据客户id获取合同列表用于下拉框选择
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	public void queryCrmContractListToChoose(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String id = map.get("id").toString();
		List<Map<String, Object>> beans = new ArrayList<>();
		if(ToolUtil.isBlank(jedisClient.get(CrmConstants.sysContractListById(id)))){
			beans = crmContractDao.queryCrmContractListToChoose(map);
			jedisClient.set(CrmConstants.sysContractListById(id), JSONUtil.toJsonStr(beans));
		}else{
			beans = JSONUtil.toList(jedisClient.get(CrmConstants.sysContractListById(id)), null);
		}
		outputObject.setBeans(beans);
		outputObject.settotal(beans.size());
	}
	
	/**
     * 
         * @Title: editCrmContractToSubApproval
         * @Description: 合同提交审批
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
	@Override
	@Transactional(value="transactionManager")
	public void editCrmContractToSubApproval(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String id = map.get("id").toString();
		Map<String, Object> bean = crmContractDao.queryCrmContractStateById(map);
		int state = Integer.parseInt(bean.get("state").toString());
		if(0 == state || 12 == state || 4 == state){
			// 草稿、审核不通过或者撤销状态下可以提交审批
			ActivitiRunFactory.run(inputObject, outputObject, CRM_CONTRACT_PAGE_KEY).submitToActivi(id, ActivitiConstants.APPROVAL_ID);
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}

	/**
     * 
         * @Title: editCrmContractToPerform
         * @Description: 合同执行
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
	@Override
	@Transactional(value="transactionManager")
	public void editCrmContractToPerform(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = crmContractDao.queryCrmContractStateById(map);
		if("11".equals(bean.get("state").toString())){//审核通过可以执行
			map.put("state", 2);
			map.put("contractId", map.get("id"));
			crmContractDao.updateCrmContractState(map);//将合同表的状态更改为执行中
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}
	
	/**
     * 
         * @Title: editCrmContractToClose
         * @Description: 合同关闭
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
	@Override
	@Transactional(value="transactionManager")
	public void editCrmContractToClose(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = crmContractDao.queryCrmContractStateById(map);
		if("2".equals(bean.get("state").toString())){//执行中可以关闭
			map.put("state", 3);
			map.put("contractId", map.get("id"));
			crmContractDao.updateCrmContractState(map);//将合同表的状态更改为已关闭
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}
	
	/**
     * 
         * @Title: editCrmContractToShelve
         * @Description: 合同搁置
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
	@Override
	@Transactional(value="transactionManager")
	public void editCrmContractToShelve(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = crmContractDao.queryCrmContractStateById(map);
		if("2".equals(bean.get("state").toString())){//执行中可以搁置
			map.put("state", 5);
			map.put("contractId", map.get("id"));
			crmContractDao.updateCrmContractState(map);//将合同表的状态更改为已搁置
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}
	
	/**
     * 
         * @Title: editCrmContractToRecovery
         * @Description: 合同恢复
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
	@Override
	@Transactional(value="transactionManager")
	public void editCrmContractToRecovery(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = crmContractDao.queryCrmContractStateById(map);
		if("5".equals(bean.get("state").toString())){//搁置中可以恢复
			map.put("state", 2);
			map.put("contractId", map.get("id"));
			crmContractDao.updateCrmContractState(map);//将合同表的状态更改为执行中
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}
	
	/**
     * 
         * @Title: deleteCrmContractById
         * @Description: 删除合同信息
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
	@Override
	@Transactional(value="transactionManager")
	public void deleteCrmContractById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = crmContractDao.queryCrmContractStateById(map);
		int state = Integer.parseInt(bean.get("state").toString());
		if(0 == state || 12 == state || 4 == state){
			// 草稿、审核不通过、已撤销可以删除
			crmContractDao.deleteCrmContractById(map);//删除合同信息
			crmContractDao.deleteCrmServiceByContractId(map);//删除相关的售后服务信息
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}
	
	/**
	 * 
	     * @Title: editCrmContractToRevokeByProcessInstanceId
	     * @Description: 合同审批撤销
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void editCrmContractToRevokeByProcessInstanceId(InputObject inputObject, OutputObject outputObject) throws Exception {
		ActivitiRunFactory.run(inputObject, outputObject, CRM_CONTRACT_PAGE_KEY).revokeActivi();
	}

}
