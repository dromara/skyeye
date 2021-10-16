/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: InsuranceDao
 * @Description: 车辆保险管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 23:25
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface InsuranceDao {

	public List<Map<String, Object>> selectAllInsuranceMation(Map<String, Object> map) throws Exception;

	public int insertInsuranceMation(Map<String, Object> map) throws Exception;
	
	public int insertInsuranceMations(List<Map<String, Object>> beans) throws Exception;

	public int deleteInsuranceById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryInsuranceMationById(Map<String, Object> map) throws Exception;

	public int editInsuranceMationById(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> selectInsuranceDetailsById(Map<String, Object> map) throws Exception;
	
	public int deleteCoverageById(Map<String, Object> map) throws Exception;

}
