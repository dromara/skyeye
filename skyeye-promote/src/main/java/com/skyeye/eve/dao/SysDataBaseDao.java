package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;

@Repository
@Mapper
public interface SysDataBaseDao {

	public List<Map<String, Object>> querySysDataBaseList(Map<String, Object> map, PageBounds pageBounds) throws Exception;

	public List<Map<String, Object>> querySysDataBaseSelectList(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> querySysDataBaseDescSelectList(Map<String, Object> map) throws Exception;

}
