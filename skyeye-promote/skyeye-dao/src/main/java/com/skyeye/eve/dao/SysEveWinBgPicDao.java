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
public interface SysEveWinBgPicDao {

	public List<Map<String, Object>> querySysEveWinBgPicList(Map<String, Object> map, PageBounds pageBounds) throws Exception;

	public int insertSysEveWinBgPicMation(Map<String, Object> map) throws Exception;

	public int deleteSysEveWinBgPicMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysEveMationById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> querySysEveWinBgPicListToShow(Map<String, Object> map) throws Exception;

	public int insertSysEveWinBgPicMationByCustom(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> querySysEveWinBgPicCustomList(Map<String, Object> map) throws Exception;

}
