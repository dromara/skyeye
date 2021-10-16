/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

import java.util.List;
import java.util.Map;

public interface CheckWorkTimeService {

	public void queryAllCheckWorkTimeList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertCheckWorkTimeMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryCheckWorkTimeMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editCheckWorkTimeMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteCheckWorkTimeMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryEnableCheckWorkTimeList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryCheckWorkTimeListByLoginUser(InputObject inputObject, OutputObject outputObject) throws Exception;

	/**
	 * 根据指定年月获取所有的考勤班次的信息以及工作日信息等
	 *
	 * @param pointMonthDate 指定年月，格式为yyyy-MM
	 * @return
	 * @throws Exception
	 */
	List<Map<String, Object>> getAllCheckWorkTime(String pointMonthDate) throws Exception;

}
