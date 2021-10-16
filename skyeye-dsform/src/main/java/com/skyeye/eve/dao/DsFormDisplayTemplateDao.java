/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: DsFormDisplayTemplateDao
 * @Description: 动态表单数据展示模板管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 23:40
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface DsFormDisplayTemplateDao {

	public List<Map<String, Object>> queryDsFormDisplayTemplateList(Map<String, Object> map) throws Exception;

	public int insertDsFormDisplayTemplateMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryDsFormDisplayTemplateMationByName(Map<String, Object> map) throws Exception;

	public int deleteDsFormDisplayTemplateMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryDsFormDisplayTemplateMationToEditById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryDsFormDisplayTemplateMationByNameAndId(Map<String, Object> map) throws Exception;

	public int editDsFormDisplayTemplateMationById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryDisplayTemplateListToShow(Map<String, Object> map) throws Exception;

}
