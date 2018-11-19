package com.skyeye.authority.dao;

import java.util.List;
import java.util.Map;
import com.github.miemiedev.mybatis.paginator.domain.PageBounds;

public interface SysEveWinBgPicDao {

	public List<Map<String, Object>> querySysEveWinBgPicList(Map<String, Object> map, PageBounds pageBounds) throws Exception;

	public int insertSysEveWinBgPicMation(Map<String, Object> map) throws Exception;

	public int deleteSysEveWinBgPicMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysEveMationById(Map<String, Object> map) throws Exception;

}
