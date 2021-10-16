/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.constans.OrganizationConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.CompanyDepartmentDao;
import com.skyeye.eve.service.CompanyDepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: CompanyDepartmentServiceImpl
 * @Description: 公司部门信息管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:56
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class CompanyDepartmentServiceImpl implements CompanyDepartmentService{
	
	@Autowired
	private CompanyDepartmentDao companyDepartmentDao;

	/**
	 * 
	     * @Title: queryCompanyDepartmentList
	     * @Description: 获取公司部门信息列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryCompanyDepartmentList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = companyDepartmentDao.queryCompanyDepartmentList(map);
		beans.forEach(bean -> {
			Integer overtimeSettlementType = Integer.parseInt(bean.get("overtimeSettlementType").toString());
			bean.put("overtimeSettlementTypeName", OrganizationConstants.OvertimeSettlementType.getTitleByType(overtimeSettlementType));
		});
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 
	     * @Title: insertCompanyDepartmentMation
	     * @Description: 添加公司部门信息信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertCompanyDepartmentMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = companyDepartmentDao.queryCompanyDepartmentMationByName(map);
		if(bean == null){
			Map<String, Object> user = inputObject.getLogParams();
			map.put("id", ToolUtil.getSurFaceId());
			map.put("createId", user.get("id"));
			map.put("pId", "0");
			map.put("createTime", DateUtil.getTimeAndToString());
			companyDepartmentDao.insertCompanyDepartmentMation(map);
		}else{
			outputObject.setreturnMessage("该公司部门名称已存在.");
		}
	}

	/**
	 * 
	     * @Title: deleteCompanyDepartmentMationById
	     * @Description: 删除公司部门信息信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteCompanyDepartmentMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = companyDepartmentDao.queryCompanyDepartmentUserMationById(map);
		if(Integer.parseInt(bean.get("childsNum").toString()) == 0){//判断是否有员工
			bean = companyDepartmentDao.queryCompanyJobNumMationById(map);
			if(Integer.parseInt(bean.get("companyJobNum").toString()) == 0){//判断是否有职位
				companyDepartmentDao.deleteCompanyDepartmentMationById(map);
			}else{
				outputObject.setreturnMessage("该部门下存在职位，无法直接删除。");
			}
		}else{
			outputObject.setreturnMessage("该部门下存在员工，无法直接删除。");
		}
	}

	/**
	 * 
	     * @Title: queryCompanyDepartmentMationToEditById
	     * @Description: 编辑公司部门信息信息时进行回显
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryCompanyDepartmentMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String id = map.get("id").toString();
		Map<String, Object> bean = companyDepartmentDao.queryCompanyDepartmentMationById(id);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editCompanyDepartmentMationById
	     * @Description: 编辑公司部门信息信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editCompanyDepartmentMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = companyDepartmentDao.queryCompanyDepartmentMationByNameAndId(map);
		if(bean == null){
			companyDepartmentDao.editCompanyDepartmentMationById(map);
		}else{
			outputObject.setreturnMessage("该公司部门名称已存在.");
		}
	}

	/**
	 * 
	     * @Title: queryCompanyDepartmentListTreeByCompanyId
	     * @Description: 获取公司部门信息列表展示为树根据公司id
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryCompanyDepartmentListTreeByCompanyId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = companyDepartmentDao.queryCompanyDepartmentListTreeByCompanyId(map);
		beans = ToolUtil.listToTree(beans, "id", "parentId", "children");
		if(!beans.isEmpty()){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}

	/**
	 * 
	     * @Title: queryCompanyDepartmentListByCompanyIdToSelect
	     * @Description: 根据公司id获取部门列表展示为下拉选择框
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryCompanyDepartmentListByCompanyIdToSelect(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = companyDepartmentDao.queryCompanyDepartmentListByCompanyIdToSelect(map);
		if(!beans.isEmpty()){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}

	/**
	 * 获取部门列表展示为表格供其他选择
	 *
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	public void queryCompanyDepartmentListToChoose(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = companyDepartmentDao.queryCompanyDepartmentListToChoose(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 根据公部门ids获取部门信息列表
	 *
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	public void queryCompanyDepartmentListByIds(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<String> idsList = Arrays.asList(map.get("ids").toString().split(","));
		List<Map<String, Object>> beans = new ArrayList<>();
		if(!idsList.isEmpty()){
			beans = companyDepartmentDao.queryCompanyDepartmentListByIds(idsList);
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}else{
			outputObject.setBeans(beans);
		}
	}

	/**
	 * 根据部门id获取部门加班结算方式
	 *
	 * @param departmentId 部门id
	 * @return 部门加班结算方式
	 * @throws Exception
	 */
	@Override
	public int getDepartmentOvertimeSettlementType(String departmentId) throws Exception {
		Map<String, Object> bean = companyDepartmentDao.queryCompanyDepartmentMationById(departmentId);
		return Integer.parseInt(bean.get("overtimeSettlementType").toString());
	}

}
