/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dao;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: MemberDao
 * @Description: 会员管理
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/8 10:49
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface MemberDao {

    public List<Map<String, Object>> queryMemberByList(Map<String, Object> params) throws Exception;

    public Map<String, Object> queryMemberByUserIdAndMember(Map<String, Object> params) throws Exception;

    public void insertMember(Map<String, Object> params) throws Exception;

    public Map<String, Object> queryMemberById(Map<String, Object> params) throws Exception;

    public int editMemberByDeleteFlag(Map<String, Object> params) throws Exception;

    public int editMemberById(Map<String, Object> params) throws Exception;

    public int editMemberByEnabled(Map<String, Object> params) throws Exception;

    public int editMemberByNotEnabled(Map<String, Object> params) throws Exception;

    public Map<String, Object> queryMemberByIdAndName(Map<String, Object> params) throws Exception;

    public Map<String, Object> queryMemberByEnabled(Map<String, Object> params) throws Exception;

    public Map<String, Object> queryMemberByIdAndInfo(Map<String, Object> params) throws Exception;

	public List<Map<String, Object>> queryMemberListToSelect(Map<String, Object> params) throws Exception;
}
