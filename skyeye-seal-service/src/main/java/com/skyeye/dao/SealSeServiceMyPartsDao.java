/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dao;

import java.util.List;
import java.util.Map;

public interface SealSeServiceMyPartsDao {

	public List<Map<String, Object>> queryMyApplyPartsList(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryMyApplyUsePartsList(Map<String, Object> map) throws Exception;

}
