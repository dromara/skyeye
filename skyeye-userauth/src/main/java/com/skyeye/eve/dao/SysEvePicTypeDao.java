/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: SysEvePicTypeDao
 * @Description: 系统图片类型管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 23:32
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface SysEvePicTypeDao {

	public List<Map<String, Object>> querySysPicTypeList(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysPicTypeMationByName(Map<String, Object> map) throws Exception;

	public int insertSysPicTypeMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysPicTypeBySimpleLevel(Map<String, Object> map) throws Exception;

	public int deleteSysPicTypeById(Map<String, Object> map) throws Exception;

	public int updateUpSysPicTypeById(Map<String, Object> map) throws Exception;

	public int updateDownSysPicTypeById(Map<String, Object> map) throws Exception;

	public Map<String, Object> selectSysPicTypeById(Map<String, Object> map) throws Exception;

	public int editSysPicTypeMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysPicTypeUpMationById(Map<String, Object> map) throws Exception;

	public int editSysPicTypeMationOrderNumUpById(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysPicTypeDownMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysPicTypeStateById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> querySysPicTypeUpStateList(Map<String, Object> map) throws Exception;

}
