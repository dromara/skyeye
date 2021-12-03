/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.entity.quartz;

import com.skyeye.annotation.api.ApiEntityColumn;
import lombok.Data;

import java.io.Serializable;

@Data
public class SysQuartz implements Serializable {
	
	private static final long serialVersionUID = 412493760737681267L;

	private String id;

	// 任务的唯一性
	private String name;

	private String groups;

	private Integer status;

	@ApiEntityColumn("时间点")
	private String cron;

	private String remark;
	
	private String quartzIp;
	
	private String quartzPort;

	/**
	 * 定时任务类型  1.用户设定  2.系统任务
	 */
	private String quartzType;

	/**
	 * 定时任务的key，可有可无，但是不能重复
	 */
	private String quartzKey;
	
	private String createId;
	
	private String createTime;

}
