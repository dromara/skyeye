/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.listenner.service;

import com.skyeye.activiti.factory.ActivitiRunFactory;
import com.skyeye.common.constans.ActivitiConstants;
import com.skyeye.common.util.SpringUtils;
import com.skyeye.eve.dao.VehicleApplyUseDao;
import com.skyeye.eve.dao.VehicleDao;
import lombok.SneakyThrows;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

import java.util.Map;

/**
 * 用车申请监听
 * @author Lenovo
 *
 */
public class VehicleUseNodeListener implements JavaDelegate {
	
	//值为pass，则通过，为nopass，则不通过
	private Expression state;

	/**
	 * 用车申请关联的工作流的key
	 */
	private static final String ACTIVITI_VEHICLE_USE_PAGE_KEY = ActivitiConstants.ActivitiObjectType.ACTIVITI_VEHICLE_USE_PAGE.getKey();

	@SneakyThrows
	@Override
	public void execute(DelegateExecution execution) {
		VehicleDao vehicleDao = SpringUtils.getBean(VehicleDao.class);
		VehicleApplyUseDao vehicleApplyUseDao = SpringUtils.getBean(VehicleApplyUseDao.class);
		String processInstanceId = execution.getProcessInstanceId();//流程实例id
		// 获取用车申请表信息
		Map<String, Object> map = vehicleApplyUseDao.queryVehicleUseByProcessInstanceId(processInstanceId);//获得的map：id, userCompany
		if(map == null || map.isEmpty()){
			throw new Exception("无法获取用车申请数据");
		}
		String id = map.get("id").toString();
		//服务任务状态值
		String value1 = (String) state.getValue(execution);
		if("pass".equals(value1.toLowerCase())){//通过
			//查询是否指定车辆，乘车人数等信息
			Map<String, Object> bean = vehicleApplyUseDao.queryVehicleUseMationById(map);
			if(bean.get("designatedVehicleId").toString().length() == 0){//没有指定车辆
				bean.put("userCompany", map.get("userCompany").toString()); 
				queryTheSuitableVehicle(bean);
			}else {//已经指定车辆
				//查询该车在预定时间段内有没有被使用
				Map<String, Object> enty = vehicleApplyUseDao.queryVehicleIsHasUseById(bean);
				//乘车人数大于准载人数或者该车在预定时间内已经被使用，系统重新选车
				if("0".equals(bean.get("isOk").toString()) || (enty != null && !enty.isEmpty())){
					bean.put("userCompany", map.get("userCompany").toString()); 
					queryTheSuitableVehicle(bean);
				}else{//该车可以使用
					Map<String, Object> b = vehicleDao.queryVehicleMationByVehicleId(bean);
					bean.put("vehicleState", "1");//车辆充足
					bean.put("vehicleId", b.get("vehicleId").toString());
					bean.put("vehicleInfo", b.get("designatedVehicleInfo").toString());
					bean.put("vehicleImg", b.get("vehicleImg").toString());
				}
			}
			if(bean.get("isSelfDrive").toString().equals("2")){//1.自驾 2.安排司机
				if(bean.get("driverId").toString().length() == 0){//没有指定司机
					Map<String, Object> b = vehicleDao.queryTheSuitableDriver(map);
					bean.put("driverId", b.get("driverId").toString());
				}else{
					bean.put("driverId", "");
				}
			}
			//修改用车申请单状态,审批通过
			bean.put("state", 2);
			vehicleApplyUseDao.editVehicleUseById(bean);
		}else{
			//修改用车申请单状态,审批不通过
			map.put("state", 3);
			vehicleApplyUseDao.editVehicleUseById(map);
		}
		// 编辑流程表参数
		ActivitiRunFactory.run(ACTIVITI_VEHICLE_USE_PAGE_KEY).editApplyMationInActiviti(id);
	}
	
	/**
	 * 查询符合要求的一辆车
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> queryTheSuitableVehicle(Map<String, Object> map) throws Exception{
		VehicleApplyUseDao vehicleApplyUseDao = SpringUtils.getBean(VehicleApplyUseDao.class);
		Map<String, Object> b = vehicleApplyUseDao.queryTheSuitableVehicleToUse(map);//查询符合要求的一辆车
		if(b == null || b.isEmpty()){
			map.put("vehicleState", "2");//车辆不足
		}else{
			map.put("vehicleState", "1");//车辆充足
			map.put("vehicleId", b.get("vehicleId").toString());
			map.put("vehicleInfo", b.get("vehicleInfo").toString());
			map.put("vehicleImg", b.get("vehicleImg").toString());
		}
		return map;
	}

}
