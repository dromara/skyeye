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
import com.skyeye.eve.dao.InspectionDao;
import com.skyeye.eve.dao.SysEnclosureDao;
import com.skyeye.eve.dao.VehicleDao;
import com.skyeye.eve.service.InspectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: InspectionServiceImpl
 * @Description: 车辆年检管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 23:33
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class InspectionServiceImpl implements InspectionService {

	@Autowired
	private InspectionDao inspectionDao;
	
	@Autowired
	private VehicleDao vehicleDao;

	@Autowired
	private SysEnclosureDao sysEnclosureDao;

	/**
	 * 
	     * @Title: selectAllInspectionMation
	     * @Description: 遍历所有的年检
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void selectAllInspectionMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("userId", inputObject.getLogParams().get("id"));
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = inspectionDao.selectAllInspectionMation(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 
	     * @Title: insertInspectionMation
	     * @Description: 新增年检
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertInspectionMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		if(ToolUtil.isBlank(map.get("inspectionPrice").toString())){
			map.put("inspectionPrice", null);
		}
		map.put("id", ToolUtil.getSurFaceId());
		map.put("state", 1);
		map.put("createTime", DateUtil.getTimeAndToString());
		map.put("createId", inputObject.getLogParams().get("id"));
		inspectionDao.insertInspectionMation(map);
		vehicleDao.updateNextInspectionTime(map);
	}

	/**
	 * 
	     * @Title: deleteInspectionById
	     * @Description: 删除年检
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteInspectionById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		inspectionDao.deleteInspectionById(map);
	}

	/**
	 * 
	     * @Title: editInspectionMationById
	     * @Description: 查询年检信息用以编辑
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryInspectionMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = inspectionDao.queryInspectionMationById(map);
		if(!ToolUtil.isBlank(bean.get("enclosureInfo").toString())){
			bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
		}
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editInspectionMationById
	     * @Description: 编辑年检
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editInspectionMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		if(ToolUtil.isBlank(map.get("inspectionPrice").toString())){
			map.put("inspectionPrice", null);
		}
		inspectionDao.editInspectionMationById(map);
		Map<String, Object> bean = inspectionDao.queryInspectionMationById(map);
		map.put("vehicleId", bean.get("vehicleId"));
		vehicleDao.updateNextInspectionTime(map);
	}
	
	/**
	 * 
	     * @Title: selectInspectionDetailsById
	     * @Description: 年检详情
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void selectInspectionDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = inspectionDao.selectInspectionDetailsById(map);
		if(!ToolUtil.isBlank(bean.get("enclosureInfo").toString())){
			bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
		}
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}
	
}
