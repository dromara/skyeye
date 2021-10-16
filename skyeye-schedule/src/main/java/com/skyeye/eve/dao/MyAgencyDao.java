/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

public interface MyAgencyDao {

	public List<Map<String, Object>> queryMyAgencyList(Map<String, Object> map) throws Exception;

	public int deleteMyAgencyList(Map<String, Object> map) throws Exception;

}
