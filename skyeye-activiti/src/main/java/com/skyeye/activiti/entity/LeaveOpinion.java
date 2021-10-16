/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.activiti.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author 卫志强 请假流程 审批信息
 */
@Getter
@Setter
public class LeaveOpinion implements Serializable {

	private static final long serialVersionUID = -6536860710977259314L;
	// 审批人id
	private String opId;
	// 审批人姓名
	private String opName;
	// 审批意见
	private String opinion;
	// 审批时间
	private Date createTime;
	// 是否通过
	private boolean flag;
	// 流程id
	private String taskId;

}
