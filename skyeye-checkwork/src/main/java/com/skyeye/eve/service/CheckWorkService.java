/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

import java.util.List;
import java.util.Map;

public interface CheckWorkService {

	public void insertCheckWorkStartWork(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editCheckWorkEndWork(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryCheckWork(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryCheckWorkIdByAppealType(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertCheckWorkAppeal(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryCheckWorkAppeallist(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryCheckWorkAppealMyGetlist(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySysEveUserStaff(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editStaffCheckWork(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryStaffCheckWorkDetails(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryCheckWorkDetails(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void queryCheckWorkTimeToShowButton(InputObject inputObject, OutputObject outputObject) throws Exception;
	
    public void queryCheckWorkMationByMonth(InputObject inputObject, OutputObject outputObject) throws Exception;

	/**
	 * 获取用户指定班次在指定月份的其他日期信息[审核通过的](例如：请假，出差，加班等)
	 *
	 * @param userId 用户id
	 * @param timeId 班次id
	 * @param months 指定月份，格式["2020-04", "2020-05"...]
	 * @return
	 * @throws Exception
	 */
    public List<Map<String, Object>> getUserOtherDayMation(String userId, String timeId, List<String> months) throws Exception;
	
    public void queryCheckWorkReport(InputObject inputObject, OutputObject outputObject) throws Exception;
	
    public void queryCheckWorkEcharts(InputObject inputObject, OutputObject outputObject) throws Exception;
    
    public void downloadCheckWorkTemplate(InputObject inputObject, OutputObject outputObject) throws Exception;
    
    public void queryReportDetail(InputObject inputObject, OutputObject outputObject) throws Exception;
	
    public void queryEchartsDetail(InputObject inputObject, OutputObject outputObject) throws Exception;
    
    public void queryReportDownload(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryDayWorkMation(List<Map<String, Object>> beans, List<String> months, String timeId) throws Exception;
    
}
