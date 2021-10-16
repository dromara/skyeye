/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: InspectionDao
 * @Description: 车辆年检管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 23:34
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface InspectionDao {

	public List<Map<String, Object>> selectAllInspectionMation(Map<String, Object> map) throws Exception;

	public int insertInspectionMation(Map<String, Object> map) throws Exception;

	public int deleteInspectionById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryInspectionMationById(Map<String, Object> map) throws Exception;

	public int editInspectionMationById(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> selectInspectionDetailsById(Map<String, Object> map) throws Exception;
	
}
