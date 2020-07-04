/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved.
 */
package com.skyeye.eve.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface ExExplainToDsFormDisplayTemplateDao {

	public int insertExExplainToDsFormDisplayTemplateMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryExExplainToDsFormDisplayTemplateMation(Map<String, Object> map) throws Exception;

	public int editExExplainToDsFormDisplayTemplateMationById(Map<String, Object> map) throws Exception;

}
