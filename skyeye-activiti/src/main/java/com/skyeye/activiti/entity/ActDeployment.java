/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.activiti.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import org.activiti.engine.repository.Deployment;

/**
 * 
 * @author 卫志强
 *
 */
@Getter
@Setter
public class ActDeployment implements Serializable {
	private static final long serialVersionUID = 5337694999055428654L;
	private String id;
	private String name;
	private Date deploymentTime;
	private String category;
	private String tenantId;

	public ActDeployment() {
	}

	public ActDeployment(Deployment deployment) {
		this.id = deployment.getId();
		this.name = deployment.getName();
		this.deploymentTime = deployment.getDeploymentTime();
		this.category = deployment.getCategory();
		this.tenantId = deployment.getTenantId();
	}
}
