/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface ExamService {

	public void queryExamList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertExamMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryExamMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void addQuFillblankMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void addQuScoreMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void addQuOrderquMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void addQuPagetagMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void addQuRadioMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void addQuCheckBoxMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void addQuMultiFillblankMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void addQuParagraphMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void addQuChenMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteQuestionMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteQuestionChenColumnMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteQuestionChenRowMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteQuestionRadioOptionMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteQuestionChedkBoxOptionMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteQuestionScoreOptionMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteQuestionOrderOptionMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteQuestionMultiFillblankOptionMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editExamStateToReleaseById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryExamMationByIdToHTML(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteExamMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryExamFxMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertExamMationCopyById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryAnswerExamMationByIp(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertAnswerExamMationByIp(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void updateExamMationEndById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryMyExamList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryExamAnswerMationByAnswerId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryExamAnswerMationToMarkingByAnswerId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertExamAnswerResultMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryExamMationDetailById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryExamAnswerMationDetailById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryExamAndMarkPeopleMationDetailById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editMarkPeopleMationDetailById(InputObject inputObject, OutputObject outputObject) throws Exception;

}
