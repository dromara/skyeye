package com.skyeye.eve.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface ExExplainToRmPropertyDao {

	public int insertExExplainToRmPropertyMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryExExplainToRmPropertyMation(Map<String, Object> map) throws Exception;

	public int editExExplainToRmPropertyMationById(Map<String, Object> map) throws Exception;

}
