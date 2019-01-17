package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface CompanyMationDao {

	public List<Map<String, Object>> queryCompanyMationList(Map<String, Object> map) throws Exception;

	public int insertCompanyMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryCompanyMationByName(Map<String, Object> map) throws Exception;

	public int deleteCompanyMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryCompanyMationToEditById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryCompanyMationByNameAndId(Map<String, Object> map) throws Exception;

	public int editCompanyMationById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryOverAllCompanyMationList(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryCompanyMationById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryCompanyMationListTree(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryCompanyUserNumMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryCompanyDepartMentNumMationById(Map<String, Object> map) throws Exception;

}
