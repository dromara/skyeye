/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface SysWorkPlanService {

	public void querySysWorkPlanList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertSysWorkPlanISPeople(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertSysWorkPlanISDepartMent(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertSysWorkPlanISCompany(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteSysWorkPlanTimingById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteSysWorkPlanById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySysWorkPlanToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSysWorkPlanISPeople(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSysWorkPlanISDepartMentOrCompany(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSysWorkPlanTimingSend(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySysWorkPlanDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryMySysWorkPlanListByUserId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void subEditWorkPlanStateById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryMyCreateSysWorkPlanList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryAllSysWorkPlanList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryMyBranchSysWorkPlanList(InputObject inputObject, OutputObject outputObject) throws Exception;
}
