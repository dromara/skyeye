/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SchoolQuestionBankDao {

	public List<Map<String, Object>> querySchoolQuestionBankMationList(Map<String, Object> map) throws Exception;

	public int addQuestionMation(Map<String, Object> question) throws Exception;

	public int editQuestionMationById(Map<String, Object> question) throws Exception;

	public int addQuestionRadioMationList(List<Map<String, Object>> quRadio) throws Exception;

	public int editQuestionRadioMationList(List<Map<String, Object>> editquRadio) throws Exception;

	public int deleteOldBindingByQuId(@Param("questionId") String questionId) throws Exception;

	public int insertNewBinding(List<Map<String, Object>> items) throws Exception;

	public int deleteSchoolQuestionBankMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryQuestionMationById(@Param("questionId") String questionId) throws Exception;

	public List<Map<String, Object>> queryQuestionRadioListByQuestionId(Map<String, Object> question) throws Exception;

	public List<Map<String, Object>> queryQuestionKnowledgeByQuestionId(@Param("questionId") String questionId) throws Exception;

	public int deleteQuestionRadioOptionMationList(@Param("idsList") List<String> idList) throws Exception;

	public int addQuestionCheckBoxMationList(List<Map<String, Object>> quCheckbox) throws Exception;

	public int editQuestionCheckBoxMationList(List<Map<String, Object>> editquCheckbox) throws Exception;

	public int deleteQuestionCheckBoxOptionMationList(@Param("idsList") List<String> idList) throws Exception;

	public List<Map<String, Object>> queryQuestionCheckBoxListByQuestionId(Map<String, Object> question) throws Exception;

	public int addQuestionScoreMationList(List<Map<String, Object>> quScore) throws Exception;

	public int editQuestionScoreMationList(List<Map<String, Object>> editquScore) throws Exception;

	public int deleteQuestionScoreOptionMationList(@Param("idsList") List<String> idList) throws Exception;

	public List<Map<String, Object>> queryQuestionScoreListByQuestionId(Map<String, Object> question) throws Exception;

	public int deleteQuestionOrderOptionMationList(@Param("idsList") List<String> idList) throws Exception;

	public int addQuestionOrderquMationList(List<Map<String, Object>> quOrderqu) throws Exception;

	public int editQuestionOrderquMationList(List<Map<String, Object>> editquOrderqu) throws Exception;

	public List<Map<String, Object>> queryQuestionOrderByListByQuestionId(Map<String, Object> question) throws Exception;

	public int deleteQuestionMultiFillblankOptionMationList(@Param("idsList") List<String> idList) throws Exception;

	public int addQuestionMultiFillblankMationList(List<Map<String, Object>> quMultiFillblank) throws Exception;

	public int editQuestionMultiFillblankMationList(List<Map<String, Object>> editquMultiFillblank) throws Exception;

	public List<Map<String, Object>> queryQuestionMultiFillBlankListByQuestionId(Map<String, Object> question) throws Exception;

	public int deleteQuestionColumnOptionMationList(@Param("idsList") List<String> idList) throws Exception;

	public int deleteQuestionRowOptionMationList(@Param("idsList") List<String> idList) throws Exception;

	public int addQuestionColumnMationList(List<Map<String, Object>> quColumn) throws Exception;

	public int editQuestionColumnMationList(List<Map<String, Object>> editquColumn) throws Exception;

	public int addQuestionRowMationList(List<Map<String, Object>> quRow) throws Exception;

	public int editQuestionRowMationList(List<Map<String, Object>> editquRow) throws Exception;

	public List<Map<String, Object>> queryQuestionChenRowListByQuestionId(Map<String, Object> question) throws Exception;

	public List<Map<String, Object>> queryQuestionChenColumnListByQuestionId(Map<String, Object> question) throws Exception;

	public List<Map<String, Object>> queryQuestionChenOptionListByQuestionId(Map<String, Object> question) throws Exception;

	public List<Map<String, Object>> querySchoolQuestionBankMationListToChoose(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> querySchoolQuestionBankMationListByIds(@Param("idsList") List<String> idsList) throws Exception;

	public List<Map<String, Object>> querySchoolQuestionBankMationAllList(Map<String, Object> map) throws Exception;

}
