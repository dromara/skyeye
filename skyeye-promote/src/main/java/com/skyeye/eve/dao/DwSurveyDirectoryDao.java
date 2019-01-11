package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;

@Repository
@Mapper
public interface DwSurveyDirectoryDao {

	public List<Map<String, Object>> queryDwSurveyDirectoryList(Map<String, Object> map, PageBounds pageBounds) throws Exception;

	public int insertDwSurveyDirectoryMation(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryQuestionListByBelongId(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryQuestionLogicListByQuestionId(Map<String, Object> question) throws Exception;

	public List<Map<String, Object>> queryQuestionChenRowListByQuestionId(Map<String, Object> question) throws Exception;

	public List<Map<String, Object>> queryQuestionChenColumnListByQuestionId(Map<String, Object> question) throws Exception;

	public List<Map<String, Object>> queryQuestionMultiFillBlankListByQuestionId(Map<String, Object> question) throws Exception;

	public List<Map<String, Object>> queryQuestionRadioListByQuestionId(Map<String, Object> question) throws Exception;

	public List<Map<String, Object>> queryQuestionCheckBoxListByQuestionId(Map<String, Object> question) throws Exception;

	public List<Map<String, Object>> queryQuestionChenOptionListByQuestionId(Map<String, Object> question) throws Exception;

	public List<Map<String, Object>> queryQuestionScoreListByQuestionId(Map<String, Object> question) throws Exception;

	public List<Map<String, Object>> queryQuestionOrderByListByQuestionId(Map<String, Object> question) throws Exception;

	public List<Map<String, Object>> queryChildQuestionListByBelongId(Map<String, Object> question) throws Exception;

	public Map<String, Object> querySurveyMationById(Map<String, Object> map) throws Exception;

	public int editDwSurveyMationById(Map<String, Object> map) throws Exception;

	public int addQuestionMation(Map<String, Object> map) throws Exception;

	public int addQuestionLogicsMationList(List<Map<String, Object>> quLogics) throws Exception;

	public int addQuestionScoreMationList(List<Map<String, Object>> quScore) throws Exception;

	public int addQuestionOrderquMationList(List<Map<String, Object>> quOrderqu) throws Exception;
	
	public int addQuestionRadioMationList(List<Map<String, Object>> quRadio) throws Exception;

	public int addQuestionCheckBoxMationList(List<Map<String, Object>> quCheckBox) throws Exception;

	public int addQuestionMultiFillblankMationList(List<Map<String, Object>> quMultiFillblank) throws Exception;

	public int addQuestionColumnMationList(List<Map<String, Object>> quColumn) throws Exception;

	public int addQuestionRowMationList(List<Map<String, Object>> quRow) throws Exception;

	public Map<String, Object> queryQuestionMationById(Map<String, Object> map) throws Exception;

	public int deleteLogicQuestionMationById(Map<String, Object> map) throws Exception;

	public int deleteQuestionMationById(Map<String, Object> map) throws Exception;

	public int deleteQuestionOptionMationByQuId(Map<String, Object> map) throws Exception;

	public int updateQuestionOrderByIdByQuId(Map<String, Object> question) throws Exception;

	public Map<String, Object> queryQuestionChenColumnById(Map<String, Object> map) throws Exception;

	public int deleteLogicQuestionChenColumnMationById(Map<String, Object> map) throws Exception;

	public int deleteQuestionChenColumnMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryQuestionChenRowById(Map<String, Object> map) throws Exception;

	public int deleteQuestionChenRowMationById(Map<String, Object> map) throws Exception;

	public int deleteLogicQuestionChenRowMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryQuestionRadioOptionById(Map<String, Object> map) throws Exception;

	public int deleteQuestionRadioOptionMationById(Map<String, Object> map) throws Exception;

	public int deleteLogicQuestionRadioOptionMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryQuestionChedkBoxOptionById(Map<String, Object> map) throws Exception;

	public int deleteQuestionChedkBoxOptionMationById(Map<String, Object> map) throws Exception;

	public int deleteLogicQuestionChedkBoxOptionMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryQuestionScoreOptionById(Map<String, Object> map) throws Exception;

	public int deleteQuestionScoreOptionMationById(Map<String, Object> map) throws Exception;

	public int deleteLogicQuestionScoreOptionMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryQuestionOrderOptionById(Map<String, Object> map) throws Exception;

	public int deleteQuestionOrderOptionMationById(Map<String, Object> map) throws Exception;

	public int deleteLogicQuestionOrderOptionMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryQuestionMultiFillblankOptionById(Map<String, Object> map) throws Exception;

	public int deleteQuestionMultiFillblankOptionMationById(Map<String, Object> map) throws Exception;

	public int deleteLogicQuestionMultiFillblankOptionMationById(Map<String, Object> map) throws Exception;

	public int editQuestionMationById(Map<String, Object> map) throws Exception;

	public int editQuestionLogicsMationList(List<Map<String, Object>> editquLogics) throws Exception;

	public int editQuestionScoreMationList(List<Map<String, Object>> editquScore) throws Exception;

	public int editQuestionOrderquMationList(List<Map<String, Object>> editquOrderqu) throws Exception;

	public int editQuestionRadioMationList(List<Map<String, Object>> editquRadio) throws Exception;

	public int editQuestionCheckBoxMationList(List<Map<String, Object>> editquCheckbox) throws Exception;

	public int editQuestionMultiFillblankMationList(List<Map<String, Object>> editquMultiFillblank) throws Exception;

	public int editQuestionColumnMationList(List<Map<String, Object>> editquColumn) throws Exception;

	public int editQuestionRowMationList(List<Map<String, Object>> editquRow) throws Exception;

	public int deleteSurveyMationById(Map<String, Object> map) throws Exception;

	public int editSurveyStateToReleaseById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryRadioGroupStat(Map<String, Object> question) throws Exception;

	public List<Map<String, Object>> queryCheckBoxGroupStat(Map<String, Object> question) throws Exception;

	public Map<String, Object> queryFillBlankGroupStat(Map<String, Object> question) throws Exception;

	public Map<String, Object> queryAnswerGroupStat(Map<String, Object> question) throws Exception;

	public List<Map<String, Object>> queryMultiFillBlankGroupStat(Map<String, Object> question) throws Exception;

	public List<Map<String, Object>> queryEnumQuGroupStat(Map<String, Object> question) throws Exception;

	public List<Map<String, Object>> queryChenRadioGroupStat(Map<String, Object> question) throws Exception;

	public List<Map<String, Object>> queryChenFbkGroupStat(Map<String, Object> question) throws Exception;

	public List<Map<String, Object>> queryChenCheckBoxGroupStat(Map<String, Object> question) throws Exception;

	public List<Map<String, Object>> queryChenScoreGroupStat(Map<String, Object> question) throws Exception;

	public List<Map<String, Object>> queryScoreGroupStat(Map<String, Object> question) throws Exception;

	public List<Map<String, Object>> queryOrderQuGroupStat(Map<String, Object> question) throws Exception;

	public int insertSurveyMationCopyById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryQuestionMationCopyById(Map<String, Object> map) throws Exception;

	public int addQuestionMationCopyBySurveyId(List<Map<String, Object>> questions) throws Exception;

	public List<Map<String, Object>> queryQuestionRadioListByCopyId(Map<String, Object> question) throws Exception;

	public int addQuestionRadioMationCopyList(List<Map<String, Object>> questionRadio) throws Exception;

	public List<Map<String, Object>> queryQuestionCheckBoxListByCopyId(Map<String, Object> question) throws Exception;

	public int addQuestionCheckBoxMationCopyList(List<Map<String, Object>> questionCheckBoxs) throws Exception;

	public List<Map<String, Object>> queryQuestionMultiFillBlankListByCopyId(Map<String, Object> question) throws Exception;

	public int addQuestionMultiFillBlankMationCopyList(List<Map<String, Object>> questionMultiFillBlanks) throws Exception;

	public List<Map<String, Object>> queryQuestionChenRowListByCopyId(Map<String, Object> question) throws Exception;

	public int addQuestionChenRowMationCopyList(List<Map<String, Object>> questionChenRows) throws Exception;

	public List<Map<String, Object>> queryQuestionChenColumnListByCopyId(Map<String, Object> question) throws Exception;

	public int addQuestionChenColumnMationCopyList(List<Map<String, Object>> questionChenColumns) throws Exception;

	public List<Map<String, Object>> queryQuestionChenOptionListByCopyId(Map<String, Object> question) throws Exception;

	public List<Map<String, Object>> queryQuestionScoreListByCopyId(Map<String, Object> question) throws Exception;

	public int addQuestionScoreMationCopyList(List<Map<String, Object>> questionScores) throws Exception;

	public List<Map<String, Object>> queryQuestionOrderByListByCopyId(Map<String, Object> question) throws Exception;

	public int addQuestionOrderByMationCopyList(List<Map<String, Object>> questionOrderBys) throws Exception;

}
