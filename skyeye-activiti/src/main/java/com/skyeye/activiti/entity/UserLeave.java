/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.activiti.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.*;

/**
 * 
 * @author 卫志强
 *
 */
@Table(name = "user_leave")
public class UserLeave extends BaseTask {

	private static final long serialVersionUID = 9219826375746625710L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "JDBC")
	protected String id;

	/**
	 * @return id
	 */
	@Override
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 */
	@Override
	public void setId(String id) {
		this.id = id == null ? null : id.trim();
	}

	private Integer days;

	@Column(name = "begin_time")
	private Date beginTime;

	@Column(name = "end_time")
	private Date endTime;

	@Column(name = "process_instance_Id")
	private String processInstanceId;

	private String status;

	@Column(name = "create_date")
	private Date createDate;

	@Column(name = "create_by")
	private String createBy;

	@Column(name = "update_date")
	private Date updateDate;

	@Column(name = "update_by")
	private String updateBy;

	// ***实时节点信息
	@Transient
	private String taskName;

	// 请假单审核信息
	private List<LeaveOpinion> opinionList = new ArrayList<>();

	public void leaveOpAdd(LeaveOpinion leaveOpinion) {
		this.opinionList.add(leaveOpinion);
	}

	public void leaveOpAddAll(List<LeaveOpinion> leaveOpinionList) {
		this.opinionList.addAll(leaveOpinionList);
	}

	public List<LeaveOpinion> getOpinionList() {
		return opinionList;
	}

	public void setOpinionList(List<LeaveOpinion> opinionList) {
		this.opinionList = opinionList;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return days
	 */
	public Integer getDays() {
		return days;
	}

	/**
	 * @param days
	 */
	public void setDays(Integer days) {
		this.days = days;
	}

	public Date getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
}
