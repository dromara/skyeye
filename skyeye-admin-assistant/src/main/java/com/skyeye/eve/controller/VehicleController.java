/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.VehicleService;

/**
 *
 * @ClassName: VehicleController
 * @Description: 车辆管理控制类
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/1 17:47
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Controller
public class VehicleController {
	
	@Autowired
	private VehicleService vehicleService;
	
	/**
	 * 
	     * @Title: selectAllVehicleMation
	     * @Description: 遍历所有的车辆
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/VehicleController/selectAllVehicleMation")
	@ResponseBody
	public void selectAllVehicleMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		vehicleService.selectAllVehicleMation(inputObject, outputObject);
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
	@RequestMapping("/post/VehicleController/insertVehicleMation")
	@ResponseBody
	public void insertVehicleMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		vehicleService.insertVehicleMation(inputObject, outputObject);
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
	@RequestMapping("/post/VehicleController/deleteVehicleById")
	@ResponseBody
	public void deleteVehicleById(InputObject inputObject, OutputObject outputObject) throws Exception{
		vehicleService.deleteVehicleById(inputObject, outputObject);
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
	@RequestMapping("/post/VehicleController/updateVehicleNormalById")
	@ResponseBody
	public void updateVehicleNormalById(InputObject inputObject, OutputObject outputObject) throws Exception{
		vehicleService.updateVehicleNormalById(inputObject, outputObject);
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
	@RequestMapping("/post/VehicleController/updateVehicleRepairById")
	@ResponseBody
	public void updateVehicleRepairById(InputObject inputObject, OutputObject outputObject) throws Exception{
		vehicleService.updateVehicleRepairById(inputObject, outputObject);
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
	@RequestMapping("/post/VehicleController/updateVehicleScrapById")
	@ResponseBody
	public void updateVehicleScrapById(InputObject inputObject, OutputObject outputObject) throws Exception{
		vehicleService.updateVehicleScrapById(inputObject, outputObject);
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
	@RequestMapping("/post/VehicleController/selectVehicleDetailsById")
	@ResponseBody
	public void selectVehicleDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception{
		vehicleService.selectVehicleDetailsById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryVehicleMationById
	     * @Description: 查询车辆信息用以编辑
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/VehicleController/queryVehicleMationById")
	@ResponseBody
	public void queryVehicleMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		vehicleService.queryVehicleMationById(inputObject, outputObject);
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
	@RequestMapping("/post/VehicleController/editVehicleMationById")
	@ResponseBody
	public void editVehicleMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		vehicleService.editVehicleMationById(inputObject, outputObject);
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
	@RequestMapping("/post/VehicleController/queryAllVehicleToChoose")
	@ResponseBody
	public void queryAllVehicleToChoose(InputObject inputObject, OutputObject outputObject) throws Exception{
		vehicleService.queryAllVehicleToChoose(inputObject, outputObject);
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
	@RequestMapping("/post/VehicleController/queryTheSuitableVehicleToChoose")
	@ResponseBody
	public void queryTheSuitableVehicleToChoose(InputObject inputObject, OutputObject outputObject) throws Exception{
		vehicleService.queryTheSuitableVehicleToChoose(inputObject, outputObject);
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
	@RequestMapping("/post/VehicleController/queryAvailableDrivers")
	@ResponseBody
	public void queryAvailableDrivers(InputObject inputObject, OutputObject outputObject) throws Exception{
		vehicleService.queryAvailableDrivers(inputObject, outputObject);
	}
	
}
