/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.activiti.service.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.util.io.InputStreamSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.skyeye.activiti.mapper.ActAssigneeMapper;
import com.skyeye.activiti.service.ActAssigneeService;

@Service
public class ActAssigneeServiceImpl implements ActAssigneeService {
	
	@Autowired
	private ActAssigneeMapper actAssigneeMapper;

	@Autowired
	private RepositoryService repositoryService;

	@Override
	public int deleteByNodeId(String nodeId) throws Exception {
		return actAssigneeMapper.deleteByNodeId(nodeId);
	}

	@Override
	public List<ActivityImpl> getActivityList(String deploymentId) throws Exception {
		org.activiti.engine.repository.ProcessDefinition processDefinition = repositoryService
				.createProcessDefinitionQuery().deploymentId(deploymentId).singleResult();
		ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
				.getDeployedProcessDefinition(processDefinition.getId());
		InputStream inputStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(),
				processDefinition.getResourceName());
		new BpmnXMLConverter().convertToBpmnModel(new InputStreamSource(inputStream), false, true);
		return selectAllActivity(processDefinitionEntity.getActivities());

	}

	@Override
	public List<ActivityImpl> selectAllActivity(List<ActivityImpl> activities) throws Exception {
		List<ActivityImpl> list = new ArrayList<>(activities);
		for (ActivityImpl activity : activities) {
			List<ActivityImpl> childActivities = activity.getActivities();
			if (!childActivities.isEmpty()) {
				list.addAll(selectAllActivity(childActivities));
			}
		}
		return list;
	}
}
