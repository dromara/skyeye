/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: SysNoticeDao
 * @Description: 公告模块数据交互层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 21:36
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface SysNoticeDao {
	
	public List<Map<String, Object>> querySysNoticeList(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysNoticeMationByName(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> querySysNoticeMationByNameAndId(Map<String, Object> map) throws Exception;

	/**
	 * 新增公告
	 *
	 * @param map 公告信息
	 * @return 成功的条数
	 * @throws Exception
	 */
	public int insertSysNoticeMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysNoticeBySimpleLevel(Map<String, Object> map) throws Exception;

	public int deleteSysNoticeById(Map<String, Object> map) throws Exception;

	public int updateUpSysNoticeById(Map<String, Object> map) throws Exception;

	public int updateDownSysNoticeById(Map<String, Object> map) throws Exception;

	public Map<String, Object> selectSysNoticeById(Map<String, Object> map) throws Exception;

	public int editSysNoticeMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysNoticeUpMationById(Map<String, Object> map) throws Exception;

	public int editSysNoticeMationOrderNumUpById(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysNoticeDownMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysNoticeStateById(Map<String, Object> map) throws Exception;

	public int insertSysNoticeUser(List<Map<String, Object>> beans) throws Exception;

	public int deleteSysNoticeUserById(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysNoticeStateAndTimeSend(Map<String, Object> map) throws Exception;

	public int editSysNoticeTimeUpMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysNoticeDetailsById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> selectAllSendUser(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysNoticeMationById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryUserReceivedSysNotice(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryReceivedSysNoticeDetailsById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryReceivedSysNoticeUserInfoById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryAllUserList() throws Exception;

}
