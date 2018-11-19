package com.skyeye.authority.dao;

import java.util.List;
import java.util.Map;
import com.github.miemiedev.mybatis.paginator.domain.PageBounds;

public interface SysEveWinLockBgPicDao {

	public List<Map<String, Object>> querySysEveWinLockBgPicList(Map<String, Object> map, PageBounds pageBounds) throws Exception;

	public int insertSysEveWinLockBgPicMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysEveWinLockBgPicMationByName(Map<String, Object> map) throws Exception;

	public int deleteSysEveWinLockBgPicMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysEveMationById(Map<String, Object> map) throws Exception;

}
