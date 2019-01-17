package com.skyeye.eve.service.impl;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.CompanyDepartmentDao;
import com.skyeye.eve.service.CompanyDepartmentService;

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

	/**
	 * 
	     * @Title: queryCompanyDepartmentListTreeByCompanyId
	     * @Description: 获取公司部门信息列表展示为树根据公司id
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void queryCompanyDepartmentListTreeByCompanyId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = companyDepartmentDao.queryCompanyDepartmentListTreeByCompanyId(map);
		JSONArray result = ToolUtil.listToTree(JSONArray.parseArray(JSON.toJSONString(beans)), "id", "parentId", "children");
		beans = (List)result;
		if(!beans.isEmpty()){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}
	
}
