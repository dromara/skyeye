package com.skyeye.eve.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface ExExplainToDsFormContentDao {

	public int insertExExplainToDsFormContentMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryExExplainToDsFormContentMation(Map<String, Object> map) throws Exception;

	public int editExExplainToDsFormContentMationById(Map<String, Object> map) throws Exception;

}
