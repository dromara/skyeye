/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.SysEnclosureDao;
import com.skyeye.eve.dao.SysStaffFamilyDao;
import com.skyeye.eve.service.SysStaffFamilyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: SysStaffFamilyServiceImpl
 * @Description: 员工家庭情况管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:39
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class SysStaffFamilyServiceImpl implements SysStaffFamilyService{
	
	@Autowired
	private SysStaffFamilyDao sysStaffFamilyDao;
	
	@Autowired
	private SysEnclosureDao sysEnclosureDao;

	/**
	 * 
	 * Title: queryAllSysStaffFamilyList
	 * Description: 查询所有家庭情况列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @see com.skyeye.eve.service.SysStaffFamilyService#queryAllSysStaffFamilyList(com.skyeye.common.object.InputObject, com.skyeye.common.object.OutputObject)
	 */
	@Override
	public void queryAllSysStaffFamilyList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(params.get("page").toString()), Integer.parseInt(params.get("limit").toString()));
        List<Map<String, Object>> beans = sysStaffFamilyDao.queryAllSysStaffFamilyList(params);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 
	 * Title: insertSysStaffFamilyMation
	 * Description: 员工家庭情况信息录入
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @see com.skyeye.eve.service.SysStaffFamilyService#insertSysStaffFamilyMation(com.skyeye.common.object.InputObject, com.skyeye.common.object.OutputObject)
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertSysStaffFamilyMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("id", ToolUtil.getSurFaceId());
		map.put("createTime", DateUtil.getTimeAndToString());
		sysStaffFamilyDao.insertSysStaffFamilyMation(map);
	}

	/**
	 * 
	 * Title: querySysStaffFamilyMationToEdit
	 * Description: 编辑员工家庭情况信息时回显
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @see com.skyeye.eve.service.SysStaffFamilyService#querySysStaffFamilyMationToEdit(com.skyeye.common.object.InputObject, com.skyeye.common.object.OutputObject)
	 */
	@Override
	public void querySysStaffFamilyMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String id = map.get("id").toString();
		Map<String, Object> certificate = sysStaffFamilyDao.querySysStaffFamilyMationToEdit(id);
		if(certificate != null && !certificate.isEmpty()){
			// 附件
			if(certificate.containsKey("enclosure") && !ToolUtil.isBlank(certificate.get("enclosure").toString())){
				List<Map<String,Object>> beans = sysEnclosureDao.queryEnclosureInfo(certificate.get("enclosure").toString());
				certificate.put("enclosureInfo", beans);
			}
			outputObject.setBean(certificate);
			outputObject.settotal(1);
		}else{
			outputObject.setreturnMessage("该家庭情况信息不存在.");
		}
	}

	/**
	 * 
	 * Title: editSysStaffFamilyMationById
	 * Description: 编辑员工家庭情况信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @see com.skyeye.eve.service.SysStaffFamilyService#editSysStaffFamilyMationById(com.skyeye.common.object.InputObject, com.skyeye.common.object.OutputObject)
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editSysStaffFamilyMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String id = map.get("id").toString();
		Map<String, Object> certificate = sysStaffFamilyDao.querySysStaffFamilyMationToEdit(id);
		if(certificate != null && !certificate.isEmpty()){
			sysStaffFamilyDao.editSysStaffFamilyMationById(map);
		}else{
			outputObject.setreturnMessage("该家庭情况信息不存在.");
		}
	}

	/**
	 * 
	 * Title: deleteSysStaffFamilyMationById
	 * Description: 删除员工家庭情况信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @see com.skyeye.eve.service.SysStaffFamilyService#deleteSysStaffFamilyMationById(com.skyeye.common.object.InputObject, com.skyeye.common.object.OutputObject)
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteSysStaffFamilyMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String id = map.get("id").toString();
		sysStaffFamilyDao.deleteSysStaffFamilyMationById(id);
	}

	/**
	 * 
	 * Title: queryPointStaffSysStaffFamilyList
	 * Description: 查询指定员工的家庭情况列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @see com.skyeye.eve.service.SysStaffFamilyService#queryPointStaffSysStaffFamilyList(com.skyeye.common.object.InputObject, com.skyeye.common.object.OutputObject)
	 */
	@Override
	public void queryPointStaffSysStaffFamilyList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(params.get("page").toString()), Integer.parseInt(params.get("limit").toString()));
        List<Map<String, Object>> beans = sysStaffFamilyDao.queryPointStaffSysStaffFamilyList(params);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}
	
}
