/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface ReportTypeService {

	public void queryReportTypeList(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void insertReportTypeMation(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void queryReportTypeMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editReportTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void editReportTypeSortTopById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editReportTypeSortLowerById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void deleteReportTypeById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void editReportTypeUpTypeById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void editReportTypeDownTypeById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void queryReportTypeUpList(InputObject inputObject, OutputObject outputObject) throws Exception;

}
