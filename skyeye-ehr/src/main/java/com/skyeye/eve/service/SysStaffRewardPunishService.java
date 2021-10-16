/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 *
 * @ClassName: SysStaffRewardPunishService
 * @Description: 员工奖惩管理服务接口类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:41
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface SysStaffRewardPunishService {

	public void queryAllSysStaffRewardPunishList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertSysStaffRewardPunishMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySysStaffRewardPunishMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSysStaffRewardPunishMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteSysStaffRewardPunishMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryPointStaffSysStaffRewardPunishList(InputObject inputObject, OutputObject outputObject) throws Exception;

}
