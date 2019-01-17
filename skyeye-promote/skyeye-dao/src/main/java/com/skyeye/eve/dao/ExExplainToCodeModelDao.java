package com.skyeye.eve.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface ExExplainToCodeModelDao {

	public int insertExExplainToCodeModelMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryExExplainToCodeModelMation(Map<String, Object> map) throws Exception;

	public int editExExplainToCodeModelMationById(Map<String, Object> map) throws Exception;

}
