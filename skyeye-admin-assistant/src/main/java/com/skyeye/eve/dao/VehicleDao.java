/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: VehicleDao
 * @Description: 车辆管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/1 18:07
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface VehicleDao {

	public List<Map<String, Object>> selectAllVehicleMation(Map<String, Object> map) throws Exception;

	public int insertVehicleMation(Map<String, Object> map) throws Exception;

	public int deleteVehicleById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryVehicleState(Map<String, Object> map) throws Exception;

	public int updateVehicleRepairById(Map<String, Object> map) throws Exception;

	public int updateVehicleScrapById(Map<String, Object> map) throws Exception;

	public Map<String, Object> selectVehicleDetailsById(Map<String, Object> map) throws Exception;

	public int updateVehicleNormalById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryVehicleMationById(Map<String, Object> map) throws Exception;

	public int editVehicleMationById(Map<String, Object> map) throws Exception;
	
	public List<Map<String, Object>> queryAllVehicleToChoose(Map<String, Object> map) throws Exception;
	
	public int updateNextInspectionTime(Map<String, Object> map) throws Exception;
	
	public int updateInsuranceDeadlineTime(Map<String, Object> map) throws Exception;
	
	public int updatePrevMaintainTime(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> queryVehicleMationBylicensePlate(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> queryVehicleMationBylicensePlateAndId(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryTheSuitableVehicleToChoose(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryAvailableDrivers(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryTheSuitableDriver(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryMyVehicleHistoryList(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryVehicleMationByVehicleId(Map<String, Object> map) throws Exception;
}
