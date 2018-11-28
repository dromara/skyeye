package com.skyeye.company.service.impl;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.skyeye.company.dao.CompanyDepartmentDao;
import com.skyeye.company.service.CompanyDepartmentService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ToolUtil;

@Service
public class CompanyDepartmentServiceImpl implements CompanyDepartmentService{
	
	@Autowired
	private CompanyDepartmentDao companyDepartmentDao;

	/**
	 * 
	     * @Title: queryCompanyDepartmentList
	     * @Description: 获取公司部门信息列表
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryCompanyDepartmentList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = companyDepartmentDao.queryCompanyDepartmentList(map, 
				new PageBounds(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString())));
		PageList<Map<String, Object>> beansPageList = (PageList<Map<String, Object>>)beans;
		int total = beansPageList.getPaginator().getTotalCount();
		outputObject.setBeans(beans);
		outputObject.settotal(total);
	}

	/**
	 * 
	     * @Title: insertCompanyDepartmentMation
	     * @Description: 添加公司部门信息信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void insertCompanyDepartmentMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = companyDepartmentDao.queryCompanyDepartmentMationByName(map);
		if(bean == null){
			Map<String, Object> user = inputObject.getLogParams();
			map.put("id", ToolUtil.getSurFaceId());
			map.put("createId", user.get("id"));
			map.put("pId", "0");
			map.put("createTime", ToolUtil.getTimeAndToString());
			companyDepartmentDao.insertCompanyDepartmentMation(map);
		}else{
			outputObject.setreturnMessage("该公司部门信息名称已存在，不可进行二次保存");
		}
	}

	/**
	 * 
	     * @Title: deleteCompanyDepartmentMationById
	     * @Description: 删除公司部门信息信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void deleteCompanyDepartmentMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = companyDepartmentDao.queryCompanyDepartmentUserMationById(map);
		if(Integer.parseInt(bean.get("childsNum").toString()) == 0){
			companyDepartmentDao.deleteCompanyDepartmentMationById(map);
		}else{
			outputObject.setreturnMessage("该部门下存在员工，无法直接删除。");
		}
	}

	/**
	 * 
	     * @Title: queryCompanyDepartmentMationToEditById
	     * @Description: 编辑公司部门信息信息时进行回显
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryCompanyDepartmentMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = companyDepartmentDao.queryCompanyDepartmentMationToEditById(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editCompanyDepartmentMationById
	     * @Description: 编辑公司部门信息信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void editCompanyDepartmentMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = companyDepartmentDao.queryCompanyDepartmentMationByNameAndId(map);
		if(bean == null){
			companyDepartmentDao.editCompanyDepartmentMationById(map);
		}else{
			outputObject.setreturnMessage("该公司部门信息名称已存在，不可进行二次保存");
		}
	}
	
}
