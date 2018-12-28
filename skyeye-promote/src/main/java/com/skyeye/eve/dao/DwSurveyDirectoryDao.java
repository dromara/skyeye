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

}
