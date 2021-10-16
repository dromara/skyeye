/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: SysEveUserNoticeDao
 * @Description: 用户消息管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 19:12
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface SysEveUserNoticeDao {

	public List<Map<String, Object>> getNoticeListByUserId(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> getAllNoticeListByUserId(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryNoticeMationById(Map<String, Object> map) throws Exception;

	public int editNoticeMationById(Map<String, Object> map) throws Exception;

	public int deleteNoticeMationById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryNoticeNoReadMationByIds(Map<String, Object> map) throws Exception;

	public int editNoticeMationByIds(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryNoticeNoDelMationByIds(Map<String, Object> map) throws Exception;

	public int deleteNoticeMationByIds(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryNoticeDetailsMationById(Map<String, Object> map) throws Exception;

}
