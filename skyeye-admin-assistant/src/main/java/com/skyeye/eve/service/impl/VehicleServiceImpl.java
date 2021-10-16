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
import com.skyeye.eve.dao.SysEveUserStaffDao;
import com.skyeye.eve.dao.VehicleDao;
import com.skyeye.eve.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @ClassName: VehicleServiceImpl
 * @Description: 用车申请服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/5 13:01
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
public class VehicleServiceImpl implements VehicleService {

	@Autowired
	private VehicleDao vehicleDao;
	
	@Autowired
	private SysEveUserStaffDao sysEveUserStaffDao;
	
	@Autowired
	private SysEnclosureDao sysEnclosureDao;

	/**
	 * 
	     * @Title: selectAllVehicleMation
	     * @Description: 遍历我的车辆申请
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void selectAllVehicleMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("userId", inputObject.getLogParams().get("id"));
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = vehicleDao.selectAllVehicleMation(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 
	     * @Title: insertVehicleMation
	     * @Description: 新增车辆
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertVehicleMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("licensePlate", map.get("licensePlate").toString().replaceAll("\\s*", ""));
		Map<String, Object> bean = vehicleDao.queryVehicleMationBylicensePlate(map);
		if(bean != null && !bean.isEmpty()){
			outputObject.setreturnMessage("该车牌号已经存在，不能重复添加！");
		}else{
			map.put("id", ToolUtil.getSurFaceId());
			map.put("state", 1);
			map.put("createTime", DateUtil.getTimeAndToString());
			map.put("createId", inputObject.getLogParams().get("id"));
			Set<Entry<String, Object>> entrys = map.entrySet();
			for(Map.Entry<String, Object> entry : entrys){
				if(ToolUtil.isBlank(entry.getValue().toString())){
					map.put(entry.getKey(), null);
				}
			}
			Map<String, Object> cmap = new HashMap<>();
			cmap.put("id", inputObject.getLogParams().get("id"));
			Map<String, Object> b = sysEveUserStaffDao.queryCompanyIdByUserId(cmap);
			map.put("vehicleCompany", b.get("companyId"));
			vehicleDao.insertVehicleMation(map);
		}
	}

	/**
	 * 
	     * @Title: deleteVehicleById
	     * @Description: 删除车辆
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteVehicleById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		vehicleDao.deleteVehicleById(map);
	}

	/**
	 * 
	     * @Title: updateVehicleNormalById
	     * @Description: 车辆恢复正常
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void updateVehicleNormalById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> m = vehicleDao.queryVehicleState(map);
		if("2".equals(m.get("state").toString()) || "3".equals(m.get("state").toString())){//维修或者报废可以恢复正常
			vehicleDao.updateVehicleNormalById(map);
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}

	/**
	 * 
	     * @Title: updateVehicleRepairById
	     * @Description: 车辆维修
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void updateVehicleRepairById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> m = vehicleDao.queryVehicleState(map);
		if("1".equals(m.get("state").toString()) || "3".equals(m.get("state").toString())){//正常或者报废可以维修
			vehicleDao.updateVehicleRepairById(map);
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}

	/**
	 * 
	     * @Title: updateVehicleScrapById
	     * @Description: 车辆报废
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void updateVehicleScrapById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> m = vehicleDao.queryVehicleState(map);
		if("1".equals(m.get("state").toString()) || "2".equals(m.get("state").toString())){//正常或者维修可以报废
			vehicleDao.updateVehicleScrapById(map);
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}

	/**
	 * 
	     * @Title: selectVehicleDetailsById
	     * @Description: 车辆详情
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void selectVehicleDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = vehicleDao.selectVehicleDetailsById(map);
		if(!ToolUtil.isBlank(bean.get("enclosureInfo").toString())){
			bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
		}
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editVehicleMationById
	     * @Description: 查询车辆信息用以编辑
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryVehicleMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = vehicleDao.queryVehicleMationById(map);
		// 查询附件
		bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
		// 查询车辆管理人
		bean.put("vehicleAdmin", sysEveUserStaffDao.queryUserNameList(bean.get("vehicleAdmin").toString()));
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editVehicleMationById
	     * @Description: 编辑车辆
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editVehicleMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("licensePlate", map.get("licensePlate").toString().replaceAll("\\s*", ""));
		Map<String, Object> bean = vehicleDao.queryVehicleMationBylicensePlateAndId(map);
		if(bean == null){
			Set<Entry<String, Object>> entrys = map.entrySet();
			for(Map.Entry<String, Object> entry : entrys){
				if(ToolUtil.isBlank(entry.getValue().toString())){
					map.put(entry.getKey(), null);
				}
			}
			vehicleDao.editVehicleMationById(map);
		}else{
			outputObject.setreturnMessage("该车牌号已存在，不可进行二次保存");
		}
	}
	
	/**
	 * 
	     * @Title: queryAllVehicleToChoose
	     * @Description: 查询所有的车牌号用于下拉选择框
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryAllVehicleToChoose(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("userId", inputObject.getLogParams().get("id"));
		List<Map<String, Object>> beans = vehicleDao.queryAllVehicleToChoose(map);
		outputObject.setBeans(beans);
		outputObject.settotal(beans.size());
	}

	/**
	 * 
	     * @Title: queryTheSuitableVehicleToChoose
	     * @Description: 查询可用的合适的车辆用于用户用车申请
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryTheSuitableVehicleToChoose(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("id", inputObject.getLogParams().get("id"));
		Map<String, Object> b =sysEveUserStaffDao.queryCompanyIdByUserId(map);
		map.put("vehicleCompany", b.get("companyId"));
		List<Map<String, Object>> beans = vehicleDao.queryTheSuitableVehicleToChoose(map);
		outputObject.setBeans(beans);
		outputObject.settotal(beans.size());
	}

	/**
	 * 
	     * @Title: queryAvailableDrivers
	     * @Description: 查询空闲的司机
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryAvailableDrivers(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("id", inputObject.getLogParams().get("id"));
		Map<String, Object> b =sysEveUserStaffDao.queryCompanyIdByUserId(map);
		map.put("companyId", b.get("companyId"));
		List<Map<String, Object>> beans = vehicleDao.queryAvailableDrivers(map);
		outputObject.setBeans(beans);
		outputObject.settotal(beans.size());
	}
	
}
