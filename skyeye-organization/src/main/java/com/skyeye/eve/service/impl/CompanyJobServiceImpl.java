/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.CompanyJobDao;
import com.skyeye.eve.dao.CompanyJobScoreDao;
import com.skyeye.eve.service.CompanyJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: CompanyJobServiceImpl
 * @Description: 公司部门职位信息管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:57
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class CompanyJobServiceImpl implements CompanyJobService{
	
	@Autowired
	private CompanyJobDao companyJobDao;

	@Autowired
	private CompanyJobScoreDao companyJobScoreDao;

	/**
	 * 
	     * @Title: queryCompanyJobList
	     * @Description: 获取公司部门职位信息列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryCompanyJobList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = companyJobDao.queryCompanyJobList(map);
		String[] s;
		for(Map<String, Object> bean : beans){
			s = bean.get("pId").toString().split(",");
			if(s.length > 0){
				bean.put("pId", s[s.length - 1]);
			}else{
				bean.put("pId", "0");
			}
		}
		outputObject.setBeans(beans);
		outputObject.settotal(beans.size());
	}

	/**
	 * 
	     * @Title: insertCompanyJobMation
	     * @Description: 添加公司部门职位信息信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertCompanyJobMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = companyJobDao.queryCompanyJobMationByName(map);
		if(bean == null){
			Map<String, Object> user = inputObject.getLogParams();
			map.put("id", ToolUtil.getSurFaceId());
			map.put("createId", user.get("id"));
			map.put("createTime", DateUtil.getTimeAndToString());
			companyJobDao.insertCompanyJobMation(map);
		}else{
			outputObject.setreturnMessage("该公司部门职位名称已存在.");
		}
	}

	/**
	 * 
	     * @Title: deleteCompanyJobMationById
	     * @Description: 删除公司部门职位信息信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteCompanyJobMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		// 1.删除职位信息
		companyJobDao.deleteCompanyJobMationById(map);
		// 2.删除职位等级信息
		companyJobScoreDao.editCompanyJobScoreStateMationByJobId(map.get("id").toString(), CompanyJobScoreServiceImpl.STATE.START_DELETE.getState());
	}

	/**
	 * 
	     * @Title: queryCompanyJobMationToEditById
	     * @Description: 编辑公司部门职位信息信息时进行回显
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryCompanyJobMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = companyJobDao.queryCompanyJobMationToEditById(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editCompanyJobMationById
	     * @Description: 编辑公司部门职位信息信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editCompanyJobMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = companyJobDao.queryCompanyJobMationByNameAndId(map);
		if(bean == null){
			companyJobDao.editCompanyJobMationById(map);
		}else{
			outputObject.setreturnMessage("该公司部门职位名称已存在.");
		}
	}

	/**
	 * 
	     * @Title: queryCompanyJobListTreeByDepartmentId
	     * @Description: 获取公司部门职位信息列表展示为树根据公司id
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void queryCompanyJobListTreeByDepartmentId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = companyJobDao.queryCompanyJobListTreeByDepartmentId(map);
		String[] s;
		for(Map<String, Object> bean : beans){
			s = bean.get("parentId").toString().split(",");
			bean.put("level", s.length);
			if(s.length > 0){
				bean.put("parentId", s[s.length - 1]);
			}else{
				bean.put("parentId", "0");
			}
		}
		beans = ToolUtil.listToTree(beans, "id", "parentId", "children");
		if(!beans.isEmpty()){
			setLastIdentification(beans);
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}

	/**
	 * 为职位树设置isLast属性
	 *
	 * @param beans
	 */
	private void setLastIdentification(List<Map<String, Object>> beans){
		beans.stream().forEach(bean -> {
			if(bean.containsKey("children") && bean.get("children") != null){
				bean.put("isLast", 0);
				setLastIdentification((List<Map<String, Object>>) bean.get("children"));
			}else{
				bean.put("isLast", 1);
			}
		});
	}

	/**
	 * 
	     * @Title: queryCompanyJobListByToSelect
	     * @Description: 根据公司id和部门id获取职位列表展示为下拉选择框
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryCompanyJobListByToSelect(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = companyJobDao.queryCompanyJobListByToSelect(map);
		if(!beans.isEmpty()){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}

	/**
	 * 
	     * @Title: queryCompanyJobSimpleListByToSelect
	     * @Description: 根据部门id获取职位同级列表且不包含当前id的值展示为下拉选择框
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryCompanyJobSimpleListByToSelect(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = companyJobDao.queryCompanyJobSimpleListByToSelect(map);
		if(!beans.isEmpty()){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}
	
}
