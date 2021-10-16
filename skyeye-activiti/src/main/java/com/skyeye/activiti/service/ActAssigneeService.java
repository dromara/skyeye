/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.activiti.service;

import org.activiti.engine.impl.pvm.process.ActivityImpl;

import java.util.List;

public interface ActAssigneeService {
	public int deleteByNodeId(String nodeId) throws Exception;

	public List<ActivityImpl> getActivityList(String deploymentId) throws Exception;

	public List<ActivityImpl> selectAllActivity(List<ActivityImpl> activities) throws Exception;
}
