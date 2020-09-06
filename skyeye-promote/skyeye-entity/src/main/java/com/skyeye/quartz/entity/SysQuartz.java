/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.quartz.entity;

public class SysQuartz {
	
	private String id;

	private String name;

	private String groups;

	private Integer status;

	private String cron;

	private String remark;

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
		this.name = name == null ? null : name.trim();
	}

	public String getGroups() {
		return groups;
	}

	public void setGroups(String groups) {
		this.groups = groups == null ? null : groups.trim();
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getCron() {
		return cron;
	}

	public void setCron(String cron) {
		this.cron = cron == null ? null : cron.trim();
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark == null ? null : remark.trim();
	}

	@Override
	public String toString() {
		return "SysQuartz{" + "id=" + id + ", name='" + name + '\''
				+ ", groups='" + groups + '\'' + ", status=" + status
				+ ", cron='" + cron + '\'' + ", remark='" + remark + '\'' + '}';
	}
}
