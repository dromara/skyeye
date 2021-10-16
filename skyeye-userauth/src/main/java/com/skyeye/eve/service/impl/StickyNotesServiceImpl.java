/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service.impl;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.StickyNotesDao;
import com.skyeye.eve.service.StickyNotesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class StickyNotesServiceImpl implements StickyNotesService {
	
	@Autowired
	private StickyNotesDao stickyNotesDao;

	/**
	 * 
	     * @Title: insertStickyNotesMation
	     * @Description: 新增便签
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertStickyNotesMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("id", ToolUtil.getSurFaceId());
		map.put("createId", inputObject.getLogParams().get("id"));
		map.put("createTime", DateUtil.getTimeAndToString());
		stickyNotesDao.insertStickyNotesMation(map);
		outputObject.setBean(map);
	}

	/**
	 * 
	     * @Title: selectStickyNotesMation
	     * @Description: 查询便签
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void selectStickyNotesMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("createId", inputObject.getLogParams().get("id"));
		List<Map<String, Object>> beans = stickyNotesDao.selectStickyNotesMation(map);
		outputObject.setBeans(beans);
		outputObject.settotal(beans.size());
	}

	/**
	 * 
	     * @Title: editStickyNotesMation
	     * @Description: 编辑便签
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editStickyNotesMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		stickyNotesDao.editStickyNotesMation(map);
	}

	/**
	 * 
	     * @Title: deleteStickyNotesMation
	     * @Description: 删除便签
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteStickyNotesMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		stickyNotesDao.deleteStickyNotesMation(map);
	}

}
