/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: OilingDao
 * @Description: 车辆加油数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 23:23
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface OilingDao {

	public List<Map<String, Object>> selectAllOilingMation(Map<String, Object> map) throws Exception;

	public int insertOilingMation(Map<String, Object> map) throws Exception;

	public int deleteOilingById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryOilingMationById(Map<String, Object> map) throws Exception;

	public int editOilingMationById(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> selectOilingDetailsById(Map<String, Object> map) throws Exception;
	
}
