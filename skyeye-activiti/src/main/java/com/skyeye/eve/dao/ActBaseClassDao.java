/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: ActBaseClassDao
 * @Description: 工作流配置类管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 22:34
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ActBaseClassDao {

	public List<Map<String, Object>> queryActBaseClassList(Map<String, Object> map) throws Exception;

	public int insertActBaseClassMation(Map<String, Object> map) throws Exception;

	public int deleteActBaseClassById(Map<String, Object> map) throws Exception;

	public Map<String, Object> selectActBaseClassById(Map<String, Object> map) throws Exception;

	public int editActBaseClassMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> selectActBaseClassByIdToDedails(Map<String, Object> map) throws Exception;

}
