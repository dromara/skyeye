/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dao;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: SealSeServiceWorkerDao
 * @Description: 工人信息管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 20:51
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface SealSeServiceWorkerDao {

	public List<Map<String, Object>> queryServiceWorkerList(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryServiceWorkerMationByUserId(Map<String, Object> map) throws Exception;

	public int insertServiceWorkerMation(Map<String, Object> map) throws Exception;

	public int deleteServiceWorkerMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryServiceWorkerMationToEditById(Map<String, Object> map) throws Exception;

	public int editServiceWorkerMationById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryServiceWorkerShowList(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryServiceWorkerToMapList(Map<String, Object> map) throws Exception;

}
