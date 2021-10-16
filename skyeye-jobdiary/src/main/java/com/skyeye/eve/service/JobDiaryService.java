/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface JobDiaryService {

	public void insertDayJobDiary(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySysEveUserStaff(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryJobDiaryDayReceived(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryJobDiaryDetails(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryJobDiaryDayMysend(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteJobDiaryDayMysend(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void selectMysendDetails(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editMyReceivedJobDiary(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertWeekJobDiary(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void selectMysendWeekDetails(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryWeekJobDiaryDetails(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertMonthJobDiary(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void selectMysendMonthDetails(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryMonthJobDiaryDetails(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editJobDiaryDayMysend(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryJobDiaryDayMysendToEdit(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editDayJobDiary(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryWeekJobDiaryDayMysendToEdit(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editWeekDayJobDiary(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryMonthJobDiaryDayMysendToEdit(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editMonthDayJobDiary(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryJobDiaryDayNumber(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void queryJobDiaryListToTimeTree(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void editReceivedJobDiaryToAlreadyRead(InputObject inputObject, OutputObject outputObject) throws Exception;

}
