/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: CompanyTalkGroupDao
 * @Description: 群组信息管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 22:52
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface CompanyTalkGroupDao {

	public int insertGroupMation(Map<String, Object> map) throws Exception;

	public int insertGroupInviteMation(List<Map<String, Object>> beans) throws Exception;

	public int insertMakeGroupUserMation(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryGroupInvitationMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryGroupInvitationMationById(Map<String, Object> map) throws Exception;

	public int editAgreeInGroupInvitationMation(Map<String, Object> map) throws Exception;

	public int editRefuseInGroupInvitationMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryGroupMationByGroupId(Map<String, Object> bean) throws Exception;

	public List<Map<String, Object>> queryGroupMationList(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryInGroupByUserAndGroupId(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryInGroupInviteByUserAndGroupId(Map<String, Object> map) throws Exception;

	public int insertInGroupInviteByUserAndGroupId(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryCreateGroupUserByGroupId(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryGroupMemberByGroupId(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryGroupMemberByGroupIdAndNotThisUser(Map<String, Object> map) throws Exception;

	public int insertPersonToPersonMessage(Map<String, Object> map) throws Exception;

	public int insertPersonToGroupMessage(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryChatLogByPerToPer(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryChatLogByPerToGroup(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryGroupCreateIdById(Map<String, Object> map) throws Exception;

	public int deleteUserToExitGroup(Map<String, Object> map) throws Exception;

	public int editCreateToExitGroup(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryGroupStateById(Map<String, Object> map1) throws Exception;

}
