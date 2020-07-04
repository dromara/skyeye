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
public interface RmGroupMemberDao {

	public List<Map<String, Object>> queryRmGroupMemberList(Map<String, Object> map, PageBounds pageBounds) throws Exception;

	public Map<String, Object> queryRmGroupMemberISTop(Map<String, Object> map) throws Exception;

	public int insertRmGroupMemberMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryRmGroupMemberISLowerByThisId(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryRmGroupMemberISTopByThisId(Map<String, Object> map) throws Exception;

	public int editRmGroupMemberSortTopById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryUseRmGroupMemberNumById(Map<String, Object> map) throws Exception;

	public int deleteRmGroupMemberById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryRmGroupMemberMationToEditById(Map<String, Object> map) throws Exception;

	public int editRmGroupMemberMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryRmGroupMemberMationById(Map<String, Object> map) throws Exception;

	public int deleteRmGroupMemberAndPropertyMationById(Map<String, Object> map) throws Exception;

	public int insertRmGroupMemberAndPropertyMationById(List<Map<String, Object>> beans) throws Exception;

	public List<Map<String, Object>> queryRmGroupMemberAndPropertyMationById(Map<String, Object> map) throws Exception;

}
