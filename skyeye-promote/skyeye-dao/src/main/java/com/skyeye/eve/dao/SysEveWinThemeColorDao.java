/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved.
 */
package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;

@Repository
@Mapper
public interface SysEveWinThemeColorDao {

	public List<Map<String, Object>> querySysEveWinThemeColorList(Map<String, Object> map, PageBounds pageBounds) throws Exception;

	public int insertSysEveWinThemeColorMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysEveWinThemeColorMationByName(Map<String, Object> map) throws Exception;

	public int deleteSysEveWinThemeColorMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysEveWinThemeColorMationToEditById(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysEveWinThemeColorMationByNameAndId(Map<String, Object> map) throws Exception;

	public int editSysEveWinThemeColorMationById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> querySysEveWinThemeColorListToShow(Map<String, Object> map) throws Exception;

}
