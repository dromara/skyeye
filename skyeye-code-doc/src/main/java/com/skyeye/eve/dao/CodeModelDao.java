/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: CodeModelDao
 * @Description: 模板信息管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 23:37
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface CodeModelDao {

	public List<Map<String, Object>> queryCodeModelList(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryCodeModelMationByName(Map<String, Object> map) throws Exception;

	public int insertCodeModelMation(Map<String, Object> map) throws Exception;

	public int deleteCodeModelById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryCodeModelMationToEditById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryCodeModelMationByIdAndName(Map<String, Object> map) throws Exception;

	public int editCodeModelMationById(Map<String, Object> map) throws Exception;

}
