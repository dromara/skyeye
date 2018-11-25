package com.skyeye.company.service.impl;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.skyeye.company.dao.CompanyMatonDao;
import com.skyeye.company.service.CompanyMatonService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ToolUtil;

@Service
public class CompanyMatonServiceImpl implements CompanyMatonService{
	
	@Autowired
	private CompanyMatonDao companyMatonDao;

	/**
	 * 
	     * @Title: queryCompanyMatonList
	     * @Description: 获取公司信息列表
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryCompanyMatonList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = companyMatonDao.queryCompanyMatonList(map, 
				new PageBounds(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString())));
		PageList<Map<String, Object>> beansPageList = (PageList<Map<String, Object>>)beans;
		int total = beansPageList.getPaginator().getTotalCount();
		outputObject.setBeans(beans);
		outputObject.settotal(total);
	}

	/**
	 * 
	     * @Title: insertCompanyMatonMation
	     * @Description: 添加公司信息信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void insertCompanyMatonMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = companyMatonDao.queryCompanyMatonMationByName(map);
		if(bean == null){
			Map<String, Object> user = inputObject.getLogParams();
			map.put("id", ToolUtil.getSurFaceId());
			map.put("createId", user.get("id"));
			map.put("createTime", ToolUtil.getTimeAndToString());
			companyMatonDao.insertCompanyMatonMation(map);
		}else{
			outputObject.setreturnMessage("该公司信息名称已存在，不可进行二次保存");
		}
	}

	/**
	 * 
	     * @Title: deleteCompanyMatonMationById
	     * @Description: 删除公司信息信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void deleteCompanyMatonMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		companyMatonDao.deleteCompanyMatonMationById(map);
	}

	/**
	 * 
	     * @Title: queryCompanyMatonMationToEditById
	     * @Description: 编辑公司信息信息时进行回显
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryCompanyMatonMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = companyMatonDao.queryCompanyMatonMationToEditById(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editCompanyMatonMationById
	     * @Description: 编辑公司信息信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void editCompanyMatonMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = companyMatonDao.queryCompanyMatonMationByNameAndId(map);
		if(bean == null){
			companyMatonDao.editCompanyMatonMationById(map);
		}else{
			outputObject.setreturnMessage("该公司信息名称已存在，不可进行二次保存");
		}
	}
	
}
