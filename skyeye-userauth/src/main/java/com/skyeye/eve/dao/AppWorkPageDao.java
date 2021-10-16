/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: AppWorkPageDao
 * @Description: 手机端菜单以及目录功能接口类
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/10 23:19
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
public interface AppWorkPageDao {

	public List<Map<String, Object>> queryAppWorkPageList(Map<String, Object> map) throws Exception;
	
	public int insertAppWorkPageMation(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> queryAppWorkPageAfterOrderBum(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> queryAppWorkPageTAfterOrderBum(Map<String, Object> map) throws Exception;
	
	public List<Map<String, Object>> queryAppWorkPageListById(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> queryAppWorkPageMationById(Map<String, Object> map) throws Exception;
	
	public int editAppWorkPageMationById(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> queryAppWorkPageStateById(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> queryAppWorkPageISTopByThisId(Map<String, Object> map) throws Exception;

	public int editAppWorkPageSortById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryAppWorkPageISLowerByThisId(Map<String, Object> map) throws Exception;

	public int editAppWorkPageStateById(Map<String, Object> map) throws Exception;
	
	public int editAppWorkPageTitleById(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> queryAppWorkISTopByThisId(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryAppWorkISLowerByThisId(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> queryAppWorkPageExistById(Map<String, Object> map) throws Exception;
	
}
