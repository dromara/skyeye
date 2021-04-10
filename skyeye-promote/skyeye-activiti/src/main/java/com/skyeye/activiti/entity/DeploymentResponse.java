/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved.
 */
package com.skyeye.activiti.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.activiti.engine.repository.Deployment;
import org.activiti.rest.common.util.DateToStringSerializer;
import java.util.Date;

/**
 * @ClassName: DeploymentResponse
 * @Description: 工作流对象类
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/10 22:03
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
public class DeploymentResponse {

	private String id;
	private String name;
	@JsonSerialize(using = DateToStringSerializer.class, as = Date.class)
	private Date deploymentTime;
	private String category;
	private String tenantId;

	public DeploymentResponse(Deployment deployment) {
		setId(deployment.getId());
		setName(deployment.getName());
		setDeploymentTime(deployment.getDeploymentTime());
		setCategory(deployment.getCategory());
		setTenantId(deployment.getTenantId());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDeploymentTime() {
		return deploymentTime;
	}

	public void setDeploymentTime(Date deploymentTime) {
		this.deploymentTime = deploymentTime;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getTenantId() {
		return tenantId;
	}
}