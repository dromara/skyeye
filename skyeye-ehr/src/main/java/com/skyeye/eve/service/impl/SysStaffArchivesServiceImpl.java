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
import com.skyeye.eve.dao.SysStaffArchivesDao;
import com.skyeye.eve.service.SysStaffArchivesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: SysStaffArchivesServiceImpl
 * @Description: 员工档案管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:36
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class SysStaffArchivesServiceImpl implements SysStaffArchivesService {
	
	@Autowired
	private SysStaffArchivesDao sysStaffArchivesDao;
	
	@Autowired
	private SysEnclosureDao sysEnclosureDao;

	/**
	 * 
	 * Title: queryAllSysStaffArchivesList
	 * Description: 查询所有档案列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @see com.skyeye.eve.service.SysStaffArchivesService#queryAllSysStaffArchivesList(com.skyeye.common.object.InputObject, com.skyeye.common.object.OutputObject)
	 */
	@Override
	public void queryAllSysStaffArchivesList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(params.get("page").toString()), Integer.parseInt(params.get("limit").toString()));
        List<Map<String, Object>> beans = sysStaffArchivesDao.queryAllSysStaffArchivesList(params);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 
	 * Title: querySysLeaveStaffArchivesList
	 * Description: 离职员工在档列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @see com.skyeye.eve.service.SysStaffArchivesService#querySysLeaveStaffArchivesList(com.skyeye.common.object.InputObject, com.skyeye.common.object.OutputObject)
	 */
	@Override
	public void querySysLeaveStaffArchivesList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(params.get("page").toString()), Integer.parseInt(params.get("limit").toString()));
        List<Map<String, Object>> beans = sysStaffArchivesDao.querySysLeaveStaffArchivesList(params);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 
	 * Title: querySysStaffNotInArchivesList
	 * Description: 员工不在档列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @see com.skyeye.eve.service.SysStaffArchivesService#querySysStaffNotInArchivesList(com.skyeye.common.object.InputObject, com.skyeye.common.object.OutputObject)
	 */
	@Override
	public void querySysStaffNotInArchivesList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(params.get("page").toString()), Integer.parseInt(params.get("limit").toString()));
        List<Map<String, Object>> beans = sysStaffArchivesDao.querySysStaffNotInArchivesList(params);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 
	 * Title: querySysStaffNoArchivesList
	 * Description: 员工无在档列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @see com.skyeye.eve.service.SysStaffArchivesService#querySysStaffNoArchivesList(com.skyeye.common.object.InputObject, com.skyeye.common.object.OutputObject)
	 */
	@Override
	public void querySysStaffNoArchivesList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(params.get("page").toString()), Integer.parseInt(params.get("limit").toString()));
        List<Map<String, Object>> beans = sysStaffArchivesDao.querySysStaffNoArchivesList(params);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 
	 * Title: insertSysStaffArchivesMation
	 * Description: 员工档案信息录入
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @see com.skyeye.eve.service.SysStaffArchivesService#insertSysStaffArchivesMation(com.skyeye.common.object.InputObject, com.skyeye.common.object.OutputObject)
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertSysStaffArchivesMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String staffId = map.get("staffId").toString();
		Map<String, Object> archivesMation = sysStaffArchivesDao.querySysStaffArchivesMationByStaffId(staffId);
		if(archivesMation == null || archivesMation.isEmpty()){
			map.put("id", ToolUtil.getSurFaceId());
			map.put("createTime", DateUtil.getTimeAndToString());
			sysStaffArchivesDao.insertSysStaffArchivesMation(map);
		}else{
			outputObject.setreturnMessage("该员工已存在档案信息，请不要重复录入.");
		}
	}

	/**
	 * 
	 * Title: querySysStaffArchivesMationToEdit
	 * Description: 编辑员工档案信息时回显
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @see com.skyeye.eve.service.SysStaffArchivesService#querySysStaffArchivesMationToEdit(com.skyeye.common.object.InputObject, com.skyeye.common.object.OutputObject)
	 */
	@Override
	public void querySysStaffArchivesMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String id = map.get("id").toString();
		Map<String, Object> archivesMation = sysStaffArchivesDao.querySysStaffArchivesMationToEdit(id);
		if(archivesMation != null && !archivesMation.isEmpty()){
			// 附件
			if(archivesMation.containsKey("enclosure") && !ToolUtil.isBlank(archivesMation.get("enclosure").toString())){
				List<Map<String,Object>> beans = sysEnclosureDao.queryEnclosureInfo(archivesMation.get("enclosure").toString());
				archivesMation.put("enclosureInfo", beans);
			}
			outputObject.setBean(archivesMation);
			outputObject.settotal(1);
		}else{
			outputObject.setreturnMessage("该档案信息不存在.");
		}
	}

	/**
	 * 
	 * Title: editSysStaffArchivesMationById
	 * Description: 编辑员工档案信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @see com.skyeye.eve.service.SysStaffArchivesService#editSysStaffArchivesMationById(com.skyeye.common.object.InputObject, com.skyeye.common.object.OutputObject)
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editSysStaffArchivesMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String id = map.get("id").toString();
		Map<String, Object> archivesMation = sysStaffArchivesDao.querySysStaffArchivesMationToEdit(id);
		if(archivesMation != null && !archivesMation.isEmpty()){
			sysStaffArchivesDao.editSysStaffArchivesMationById(map);
		}else{
			outputObject.setreturnMessage("该档案信息不存在.");
		}
	}
	
}
