/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface SchoolQuestionBankService {

	public void querySchoolQuestionBankMationList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void addQuRadioMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteSchoolQuestionBankMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryQuRadioMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void addQuCheckBoxMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryQuCheckBoxMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void addQuFillblankMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryQuFillblankMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void addQuScoreMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryQuScoreMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void addQuOrderbyMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryQuOrderbyMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void addQuMultiFillblankAddMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryQuMultiFillblankMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void addQuChenMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryQuChenMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySchoolQuestionBankMationListToChoose(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySchoolQuestionBankMationListByIds(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySchoolQuestionBankMationAllList(InputObject inputObject, OutputObject outputObject) throws Exception;

}
