/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.dao.CrmDocumentaryDao;
import com.skyeye.eve.dao.SysEnclosureDao;
import com.skyeye.service.CrmDocumentaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: CrmDocumentaryServiceImpl
 * @Description: 服务跟单管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:20
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class CrmDocumentaryServiceImpl implements CrmDocumentaryService {

    @Autowired
    private CrmDocumentaryDao documentaryDao;
    
    @Autowired
	private SysEnclosureDao sysEnclosureDao;

	/**
	 *
	 * @Title: queryMyDocumentaryList
	 * @Description: 获取我的跟单管理列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	public void queryMyDocumentaryList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("userId", user.get("id"));
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = documentaryDao.queryMyDocumentaryList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}
	
	/**
	 *
	 * @Title: queryAllDocumentaryList
	 * @Description: 获取所有跟单管理列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	public void queryAllDocumentaryList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = documentaryDao.queryAllDocumentaryList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 *
	 * @Title: insertDocumentary
	 * @Description: 添加跟单信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	@Transactional(value = "transactionManager")
	public void insertDocumentary(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("id", ToolUtil.getSurFaceId());
		map.put("createId", user.get("id"));
		map.put("createName", user.get("userName"));
		map.put("createTime", DateUtil.getTimeAndToString());
		documentaryDao.insertDocumentary(map);
	}

	/**
	 *
	 * @Title: queryDocumentaryMationById
	 * @Description: 根据id获取跟单信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	public void queryDocumentaryMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = documentaryDao.queryDocumentaryMationById(map);
		if(!ToolUtil.isBlank(bean.get("enclosureInfo").toString())){
			List<Map<String,Object>> beans = sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString());
			bean.put("enclosureInfo", beans);
		}
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}
  
	/**
	*
	* @Title: editDocumentaryMationById
	* @Description: 编辑跟单信息
	* @param inputObject
	* @param outputObject
	* @throws Exception
	*/
	@Override
	@Transactional(value = "transactionManager")
	public void editDocumentaryMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		documentaryDao.editDocumentaryMationById(map);
	}
	
	/**
	*
	* @Title: deleteDocumentaryMationById
	* @Description: 删除跟单信息
	* @param inputObject
	* @param outputObject
	* @throws Exception
	*/
	@Override
	@Transactional(value = "transactionManager")
	public void deleteDocumentaryMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		documentaryDao.deleteDocumentaryMationById(map);
	}
	
	/**
	 *
	 * @Title: queryDocumentaryMationToDetail
	 * @Description: 根据id获取跟单信息详情
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	public void queryDocumentaryMationToDetail(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = documentaryDao.queryDocumentaryMationToDetail(map);
		if(!ToolUtil.isBlank(bean.get("enclosureInfo").toString())){
			List<Map<String,Object>> beans = sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString());
			bean.put("enclosureInfo", beans);
		}
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}
}
