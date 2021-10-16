/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import cn.hutool.json.JSONUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.CompanyDepartmentDao;
import com.skyeye.eve.dao.CompanyJobDao;
import com.skyeye.eve.dao.CompanyMationDao;
import com.skyeye.eve.dao.CompanyTaxRateDao;
import com.skyeye.eve.service.CompanyMationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 *
 * @ClassName: CompanyMationServiceImpl
 * @Description: 公司信息管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:57
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class CompanyMationServiceImpl implements CompanyMationService{
	
	@Autowired
	private CompanyMationDao companyMationDao;

	@Autowired
	private CompanyDepartmentDao companyDepartmentDao;

	@Autowired
	private CompanyJobDao companyJobDao;

	@Autowired
	private CompanyTaxRateDao companyTaxRateDao;

	/**
	 * 
	     * @Title: queryCompanyMationList
	     * @Description: 获取公司信息列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryCompanyMationList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = companyMationDao.queryCompanyMationList(map);
		if(!beans.isEmpty()){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}

	/**
	 * 
	     * @Title: insertCompanyMation
	     * @Description: 添加公司信息信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertCompanyMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = companyMationDao.queryCompanyMationByName(map);
		if(bean == null){
			Map<String, Object> user = inputObject.getLogParams();
			String companyId = ToolUtil.getSurFaceId();
			map.put("id", companyId);
			map.put("createId", user.get("id"));
			map.put("createTime", DateUtil.getTimeAndToString());
			companyMationDao.insertCompanyMation(map);
			// 处理个人所得税税率信息
			dealTaxRate(map.get("taxRateStr").toString(), companyId);
		}else{
			outputObject.setreturnMessage("该公司信息已注册，请确认。");
		}
	}

	/**
	 * 处理个人所得税税率信息
	 *
	 * @param str
	 * @param companyId 公司id
	 */
	private void dealTaxRate(String str, String companyId) throws Exception {
		companyTaxRateDao.deleteCompanyTaxRateByCompanyId(companyId);
		if(ToolUtil.isBlank(str)){
			return;
		}
		List<Map<String, Object>> beans = JSONUtil.toList(JSONUtil.parseArray(str), null);
		beans.stream().forEach(bean -> {
			bean.put("id", ToolUtil.getSurFaceId());
			bean.put("companyId", companyId);
		});
		if(beans.isEmpty()){
			return;
		}
		companyTaxRateDao.insertCompanyTaxRate(beans);
	}

	/**
	 * 
	     * @Title: deleteCompanyMationById
	     * @Description: 删除公司信息信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteCompanyMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = companyMationDao.queryCompanyMationById(map);
		if(Integer.parseInt(bean.get("childsNum").toString()) == 0){//判断是否有子公司
			bean = companyMationDao.queryCompanyDepartMentNumMationById(map);
			if(Integer.parseInt(bean.get("departmentNum").toString()) == 0){//判断是否有部门
				bean = companyMationDao.queryCompanyUserNumMationById(map);
				if(Integer.parseInt(bean.get("companyUserNum").toString()) == 0){//判断是否有员工
					String companyId = map.get("id").toString();
					// 1.删除企业信息
					companyMationDao.deleteCompanyMationById(companyId);
					// 2.根据公司id删除该公司拥有的个人所得税税率信息
					companyTaxRateDao.deleteCompanyTaxRateByCompanyId(companyId);
				}else{
					outputObject.setreturnMessage("该公司下存在员工，无法直接删除。");
				}
			}else{
				outputObject.setreturnMessage("该公司下存在部门，无法直接删除。");
			}
		}else{
			outputObject.setreturnMessage("该公司下存在子公司，无法直接删除。");
		}
	}

	/**
	 * 
	     * @Title: queryCompanyMationToEditById
	     * @Description: 编辑公司信息信息时进行回显
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryCompanyMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String companyId = map.get("id").toString();
		Map<String, Object> bean = companyMationDao.queryCompanyMationToEditById(companyId);
		// 个人所得税税率信息
		bean.put("taxRateJson", companyTaxRateDao.queryCompanyTaxRateByCompanyId(companyId));
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editCompanyMationById
	     * @Description: 编辑公司信息信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editCompanyMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = companyMationDao.queryCompanyMationByNameAndId(map);
		if(bean == null){
			companyMationDao.editCompanyMationById(map);
			String companyId = map.get("id").toString();
			// 处理个人所得税税率信息
			dealTaxRate(map.get("taxRateStr").toString(), companyId);
		}else{
			outputObject.setreturnMessage("该公司信息已注册，请确认。");
		}
	}

	/**
	 * 
	     * @Title: queryOverAllCompanyMationList
	     * @Description: 获取总公司信息列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryOverAllCompanyMationList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = companyMationDao.queryOverAllCompanyMationList(map);
		if(!beans.isEmpty()){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}
	
	/**
	 * 
	     * @Title: queryCompanyMationListTree
	     * @Description: 获取公司信息列表展示为树
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void queryCompanyMationListTree(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = companyMationDao.queryCompanyMationListTree(map);
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
	 * 为企业树设置isLast属性
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
	     * @Title: queryCompanyListToSelect
	     * @Description: 获取公司列表展示为下拉选择框
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryCompanyListToSelect(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = companyMationDao.queryCompanyListToSelect(map);
		if(!beans.isEmpty()){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}

	/**
	 * 获取企业组织机构图
	 *
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	public void queryCompanyOrganization(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = new ArrayList<>();
		// 1.获取企业
		List<Map<String, Object>> company = companyMationDao.queryCompanyListToSelect(map);
		beans.addAll(company);
		// 2.获取部门
		List<Map<String, Object>> department = companyDepartmentDao.queryCompanyDepartmentOrganization(map);
		for(Map<String, Object> bean : department){
			String[] s = bean.get("parentId").toString().split(",");
			if(s.length > 0 && !"0".equals(bean.get("parentId").toString())){
				bean.put("parentId", s[s.length - 1]);
			}else{
				bean.put("parentId", bean.get("companyId"));
			}
		}
		beans.addAll(department);
		// 3.获取职位
		List<Map<String, Object>> job = companyJobDao.queryCompanyJobOrganization(map);
		for(Map<String, Object> bean : job){
			String[] s = bean.get("parentId").toString().split(",");
			if(s.length > 0 && !"0".equals(bean.get("parentId").toString())){
				bean.put("parentId", s[s.length - 1]);
			}else{
				bean.put("parentId", bean.get("departmentId"));
			}
		}
		beans.addAll(job);
		beans = ToolUtil.listToTree(beans, "id", "parentId", "children");
		outputObject.setBeans(getParentMap(beans));
	}

	private List<Map<String, Object>> getParentMap(List<Map<String, Object>> children){
		List<Map<String, Object>> beans = new ArrayList<>();
		Map<String, Object> map = new HashMap<>();
		map.put("name", "所有");
		map.put("title", "企业组织结构图");
		map.put("children", children);
		beans.add(map);
		return beans;
	}

	/**
	 * 获取公司信息列表展示为表格供其他需要选择
	 *
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	public void queryCompanyMationListToChoose(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = companyMationDao.queryCompanyMationListToChoose(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 根据公司ids获取公司信息列表
	 *
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	public void queryCompanyMationListByIds(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<String> idsList = Arrays.asList(map.get("ids").toString().split(","));
		List<Map<String, Object>> beans = new ArrayList<>();
		if(!idsList.isEmpty()){
			beans = companyMationDao.queryCompanyMationListByIds(idsList);
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}else{
			outputObject.setBeans(beans);
		}
	}

}
