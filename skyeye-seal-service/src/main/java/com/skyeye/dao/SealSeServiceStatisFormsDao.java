/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dao;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: SealSeServiceStatisFormsDao
 * @Description: 售后服务模块统计服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 23:19
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface SealSeServiceStatisFormsDao {

	public List<Map<String, Object>> queryCustomOrderTable(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryUserWorkerOrderTable(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryWarrantyOrderTable(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryProductTypeOrderTable(Map<String, Object> map) throws Exception;

}
