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
import com.skyeye.eve.dao.InsuranceDao;
import com.skyeye.eve.dao.SysEnclosureDao;
import com.skyeye.eve.dao.VehicleDao;
import com.skyeye.eve.service.InsuranceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: InsuranceServiceImpl
 * @Description: 车辆保险服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/24 15:54
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class InsuranceServiceImpl implements InsuranceService {

	@Autowired
	private InsuranceDao insuranceDao;

	@Autowired
	private VehicleDao vehicleDao;

	@Autowired
	private SysEnclosureDao sysEnclosureDao;

	/**
	 * 
	     * @Title: selectAllInsuranceMation
	     * @Description: 遍历所有的保险
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void selectAllInsuranceMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("userId", inputObject.getLogParams().get("id"));
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = insuranceDao.selectAllInsuranceMation(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 
	     * @Title: insertInsuranceMation
	     * @Description: 新增保险
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertInsuranceMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("id", ToolUtil.getSurFaceId());
		map.put("state", 1);
		map.put("createTime", DateUtil.getTimeAndToString());
		map.put("createId", inputObject.getLogParams().get("id"));
		insuranceDao.insertInsuranceMation(map);
		List<Map<String,Object>> beans = new ArrayList<>();
		String[] menuIds = map.get("coverageIds").toString().split(";");
		if(menuIds.length > 0){
			Map<String,Object> item;
			String[] arr;
			for(String str : menuIds){
				arr = str.split(",");
				item = new HashMap<>();
				item.put("id", ToolUtil.getSurFaceId());
				item.put("insuranceId", map.get("id"));
				item.put("coverageId", arr[0]);
				item.put("premium", arr[1]);
				item.put("insuredAmount", arr[2]);
				if(arr.length > 3){
					item.put("desc", arr[3]);
				}else{
					item.put("desc", "");
				}
				item.put("createId", map.get("createId"));
				item.put("createTime", map.get("createTime"));
				beans.add(item);
			}
		}
		insuranceDao.insertInsuranceMations(beans);
		vehicleDao.updateInsuranceDeadlineTime(map);
	}

	/**
	 * 
	     * @Title: deleteInsuranceById
	     * @Description: 删除保险
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteInsuranceById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		insuranceDao.deleteInsuranceById(map);
		insuranceDao.deleteCoverageById(map);
	}

	/**
	 * 
	     * @Title: editInsuranceMationById
	     * @Description: 查询保险信息用以编辑
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryInsuranceMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = insuranceDao.queryInsuranceMationById(map);
		if(!ToolUtil.isBlank(bean.get("enclosureInfo").toString())){
			bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
		}
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editInsuranceMationById
	     * @Description: 编辑保险
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editInsuranceMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		insuranceDao.editInsuranceMationById(map);
		Map<String, Object> bean = insuranceDao.queryInsuranceMationById(map);
		insuranceDao.deleteCoverageById(map);
		List<Map<String,Object>> beans = new ArrayList<>();
		String[] menuIds = map.get("coverageIds").toString().split(";");
		if(menuIds.length > 0){
			String[] arr;
			Map<String,Object> item;
			for(String str : menuIds){
				arr = str.split(",");
				item = new HashMap<>();
				item.put("id", ToolUtil.getSurFaceId());
				item.put("insuranceId", map.get("id"));
				item.put("coverageId", arr[0]);
				item.put("premium", arr[1]);
				item.put("insuredAmount", arr[2]);
				if(arr.length > 3){
					item.put("desc", arr[3]);
				}else{
					item.put("desc", "");
				}
				item.put("createId", bean.get("createId"));
				item.put("createTime", bean.get("createTime"));
				beans.add(item);
			}
		}
		insuranceDao.insertInsuranceMations(beans);
		map.put("vehicleId", bean.get("vehicleId"));
		vehicleDao.updateInsuranceDeadlineTime(map);
	}
	
	/**
	 * 
	     * @Title: selectInsuranceDetailsById
	     * @Description: 保险详情
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void selectInsuranceDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = insuranceDao.selectInsuranceDetailsById(map);
		if(!ToolUtil.isBlank(bean.get("enclosureInfo").toString())){
			bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
		}
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}
	
}
