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
import com.skyeye.eve.dao.SysStaffLanguageDao;
import com.skyeye.eve.service.SysStaffLanguageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: SysStaffLanguageServiceImpl
 * @Description: 员工语言能力管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:40
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class SysStaffLanguageServiceImpl implements SysStaffLanguageService{
	
	@Autowired
	private SysStaffLanguageDao sysStaffLanguageDao;
	
	@Autowired
	private SysEnclosureDao sysEnclosureDao;

	/**
	 * 
	 * Title: queryAllSysStaffLanguageList
	 * Description: 查询所有语言能力列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @see com.skyeye.eve.service.SysStaffLanguageService#queryAllSysStaffLanguageList(com.skyeye.common.object.InputObject, com.skyeye.common.object.OutputObject)
	 */
	@Override
	public void queryAllSysStaffLanguageList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(params.get("page").toString()), Integer.parseInt(params.get("limit").toString()));
        List<Map<String, Object>> beans = sysStaffLanguageDao.queryAllSysStaffLanguageList(params);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 
	 * Title: insertSysStaffLanguageMation
	 * Description: 员工语言能力信息录入
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @see com.skyeye.eve.service.SysStaffLanguageService#insertSysStaffLanguageMation(com.skyeye.common.object.InputObject, com.skyeye.common.object.OutputObject)
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertSysStaffLanguageMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("id", ToolUtil.getSurFaceId());
		map.put("createTime", DateUtil.getTimeAndToString());
		sysStaffLanguageDao.insertSysStaffLanguageMation(map);
	}

	/**
	 * 
	 * Title: querySysStaffLanguageMationToEdit
	 * Description: 编辑员工语言能力信息时回显
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @see com.skyeye.eve.service.SysStaffLanguageService#querySysStaffLanguageMationToEdit(com.skyeye.common.object.InputObject, com.skyeye.common.object.OutputObject)
	 */
	@Override
	public void querySysStaffLanguageMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String id = map.get("id").toString();
		Map<String, Object> certificate = sysStaffLanguageDao.querySysStaffLanguageMationToEdit(id);
		if(certificate != null && !certificate.isEmpty()){
			// 附件
			if(certificate.containsKey("enclosure") && !ToolUtil.isBlank(certificate.get("enclosure").toString())){
				List<Map<String,Object>> beans = sysEnclosureDao.queryEnclosureInfo(certificate.get("enclosure").toString());
				certificate.put("enclosureInfo", beans);
			}
			outputObject.setBean(certificate);
			outputObject.settotal(1);
		}else{
			outputObject.setreturnMessage("该语言能力信息不存在.");
		}
	}

	/**
	 * 
	 * Title: editSysStaffLanguageMationById
	 * Description: 编辑员工语言能力信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @see com.skyeye.eve.service.SysStaffLanguageService#editSysStaffLanguageMationById(com.skyeye.common.object.InputObject, com.skyeye.common.object.OutputObject)
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editSysStaffLanguageMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String id = map.get("id").toString();
		Map<String, Object> certificate = sysStaffLanguageDao.querySysStaffLanguageMationToEdit(id);
		if(certificate != null && !certificate.isEmpty()){
			sysStaffLanguageDao.editSysStaffLanguageMationById(map);
		}else{
			outputObject.setreturnMessage("该语言能力信息不存在.");
		}
	}

	/**
	 * 
	 * Title: deleteSysStaffLanguageMationById
	 * Description: 删除员工语言能力信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @see com.skyeye.eve.service.SysStaffLanguageService#deleteSysStaffLanguageMationById(com.skyeye.common.object.InputObject, com.skyeye.common.object.OutputObject)
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteSysStaffLanguageMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String id = map.get("id").toString();
		sysStaffLanguageDao.deleteSysStaffLanguageMationById(id);
	}

	/**
	 * 
	 * Title: queryPointStaffSysStaffLanguageList
	 * Description: 查询指定员工的语言能力列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @see com.skyeye.eve.service.SysStaffLanguageService#queryPointStaffSysStaffLanguageList(com.skyeye.common.object.InputObject, com.skyeye.common.object.OutputObject)
	 */
	@Override
	public void queryPointStaffSysStaffLanguageList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(params.get("page").toString()), Integer.parseInt(params.get("limit").toString()));
        List<Map<String, Object>> beans = sysStaffLanguageDao.queryPointStaffSysStaffLanguageList(params);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}
	
}
