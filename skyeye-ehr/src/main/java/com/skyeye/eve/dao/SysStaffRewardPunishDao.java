/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: SysStaffRewardPunishDao
 * @Description: 员工奖惩管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 23:41
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface SysStaffRewardPunishDao {

	public List<Map<String, Object>> queryAllSysStaffRewardPunishList(Map<String, Object> params) throws Exception;

	public int insertSysStaffRewardPunishMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysStaffRewardPunishMationToEdit(@Param("id") String id) throws Exception;

	public int editSysStaffRewardPunishMationById(Map<String, Object> map) throws Exception;

	public int deleteSysStaffRewardPunishMationById(@Param("id") String id) throws Exception;

	public List<Map<String, Object>> queryPointStaffSysStaffRewardPunishList(Map<String, Object> params) throws Exception;

}
