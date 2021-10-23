/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: MailListDao
 * @Description: 通讯录管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 22:54
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface MailListDao {

	public List<Map<String, Object>> queryComMailMationList(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryCommonMailMationList(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryPersonalMailMationList(Map<String, Object> map) throws Exception;

	public int insertMailMation(Map<String, Object> map) throws Exception;

	public int deleteMailMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryMailMationToEditById(Map<String, Object> map) throws Exception;

	public int editMailMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryMailMationDetailsById(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysMailMationDetailsById(Map<String, Object> map) throws Exception;

}
