/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.activiti.entity;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author 卫志强 流程任务
 */
@Getter
@Setter
@ToString
public class Task {
	private String id;
	private String name;
	private Date createTime;
	private String assignee;
	private String processInstanceId;// 流程实例id
	private String processDefinitionId;// 流程定义id
	private String description;
	private String category;

	private String userName;
	private String reason;
	private String urlpath;

	public Task() {
	}

	public Task(org.activiti.engine.task.Task t) {
		this.id = t.getId();
		this.name = t.getName();
		this.createTime = t.getCreateTime();
		this.assignee = t.getAssignee();
		this.processInstanceId = t.getProcessInstanceId();
		this.processDefinitionId = t.getProcessDefinitionId();
		this.description = t.getDescription();
		this.category = t.getCategory();
	}
}
