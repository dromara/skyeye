/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

public interface EditUploadDao {

	public int insertFileImgMation(Map<String, Object> bean) throws Exception;

	public List<Map<String, Object>> queryFileImgMation(Map<String, Object> bean) throws Exception;
	
}
