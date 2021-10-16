/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: DsFormLimitRequirementDao
 * @Description: 动态表单条件限制类型管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 23:39
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface DsFormLimitRequirementDao {

	public List<Map<String, Object>> queryDsFormLimitRequirementList(Map<String, Object> map) throws Exception;

	public int insertDsFormLimitRequirementMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryDsFormLimitRequirementMationByName(Map<String, Object> map) throws Exception;

	public int deleteDsFormLimitRequirementMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryDsFormLimitRequirementMationToEditById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryDsFormLimitRequirementMationByNameAndId(Map<String, Object> map) throws Exception;

	public int editDsFormLimitRequirementMationById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryDsFormLimitRequirementMationToShow(Map<String, Object> map) throws Exception;

}
