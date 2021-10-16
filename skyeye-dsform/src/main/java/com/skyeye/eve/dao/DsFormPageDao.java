/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: DsFormPageDao
 * @Description: 动态表单页面管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 23:33
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface DsFormPageDao {

	public List<Map<String, Object>> queryDsFormPageList(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryDsFormPageMationByName(Map<String, Object> map) throws Exception;

	public int insertDsFormPageMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryDsFormPageOrderby(Map<String, Object> map) throws Exception;

	public int insertDsFormPageContent(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> selectFormPageContentByPageId(Map<String, Object> map) throws Exception;

	public int deleteDsFormPageById(Map<String, Object> map) throws Exception;

	public int deleteDsFormContentByPageId(Map<String, Object> map) throws Exception;

	public Map<String, Object> selectDsFormPageById(Map<String, Object> map) throws Exception;

	public int editDsFormPageMationById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryAllDsFormPageContent(Map<String, Object> map) throws Exception;

	public int editDsFormPageContentByPageId(Map<String, Object> m) throws Exception;

	public int editDsFormPageContentToDelete(Map<String, Object> m) throws Exception;

	public List<Map<String, Object>> queryDsFormContentListByPageId(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryFromDsFormPageContent(Map<String, Object> o) throws Exception;

	public int insertDsFormPageSequence(Map<String, Object> m) throws Exception;

	public int insertDsFormPageData(List<Map<String, Object>> beans) throws Exception;

}
